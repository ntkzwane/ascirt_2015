package org.jbox2d.dynamics.joints;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.SolverData;
import org.jbox2d.dynamics.contacts.Velocity;
import org.jbox2d.pooling.IWorldPool;

/**
 * Friction joint for top-down scenes. Simulates friction between the ground and an object.
 * Original C++ source: https://github.com/mikelikespie/box2d-demo/blob/car/Source/Dynamics/Joints/b2FrictionJoint.cpp
 *
 * Created by jamie on 2014/09/15.
 */
public class TopDownFrictionJoint extends Joint {

  private final float linearVelocityThreshold;
  private final float angularVelocityThreshold;

  private final float staticFrictionForce;
  private final float kineticFrictionForce;

  private final float staticFrictionTorque;
  private final float kineticFrictionTorque;

  private final boolean hasLinearStaticFriction;
  private final boolean hasAngularStaticFriction;

  private final boolean hasLinearKineticFriction;
  private final boolean hasAngularKineticFriction;

  private enum FrictionType {
    STATIC, KINETIC, NONE
  }

  private FrictionType frictionType;

  private final Vec2 lambdaP = new Vec2();
  private float lambdaAP = 0f;

  private int index;

  private float mass;
  private float inverseMass;

  private float inertia;
  private float inverseInertia;

  private float dt;
  private float inverseDt;

  private boolean isActive;

  protected TopDownFrictionJoint(IWorldPool worldPool, TopDownFrictionJointDef def) {
    super(worldPool, def);

    linearVelocityThreshold = def.linearVelocityThreshold * def.linearVelocityThreshold;
    angularVelocityThreshold = def.angularVelocityThreshold;

    staticFrictionForce = def.staticFrictionForce;
    kineticFrictionForce = def.kineticFrictionForce;

    staticFrictionTorque = def.staticFrictionTorque;
    kineticFrictionTorque = def.kineticFrictionTorque;

    hasLinearStaticFriction = staticFrictionForce > 0f;
    hasAngularStaticFriction = staticFrictionTorque > 0f;
    hasLinearKineticFriction = kineticFrictionForce > 0f;
    hasAngularKineticFriction = kineticFrictionTorque > 0f;
  }

  @Override
  public void getAnchorA(Vec2 vec2) {
    vec2.set(m_bodyA.getWorldCenter());
  }

  @Override
  public void getAnchorB(Vec2 vec2) {
    vec2.setZero();
  }

  @Override
  public void getReactionForce(float inv_dt, Vec2 argOut) {
    argOut.set(lambdaP).mulLocal(inv_dt);
  }

  @Override
  public float getReactionTorque(float inv_dt) {
    return inv_dt * lambdaAP;
  }

  @Override
  public void initVelocityConstraints(SolverData data) {
    isActive = isActive();
    if (!isActive) {
      return;
    }

    index = m_bodyA.m_islandIndex;

    // Mass
    mass = m_bodyA.m_mass;
    inverseMass = m_bodyA.m_invMass;

    // Moment of inertia
    inertia = m_bodyA.m_I;
    inverseInertia = m_bodyA.m_invI;

    // Determine the starting friction type
    if ((hasLinearStaticFriction || hasAngularStaticFriction) && isStatic(data.velocities[index])) {
      frictionType = FrictionType.STATIC;
    } else if (hasLinearKineticFriction || hasAngularKineticFriction) {
      frictionType = FrictionType.KINETIC;
    } else {
      frictionType = FrictionType.NONE;
    }

    // Calculate time step per iteration
    dt = data.step.dt / data.step.velocityIterations;
    inverseDt = data.step.inv_dt * data.step.velocityIterations;

    lambdaP.setZero();
  }

  @Override
  public void solveVelocityConstraints(SolverData data) {
    if (!isActive || frictionType == FrictionType.NONE) {
      return;
    }

    final Velocity velocity = data.velocities[index];
    applyLinearFriction(velocity);
    applyAngularFriction(velocity);
  }

  private void applyLinearFriction(Velocity velocity) {
    if (frictionType == FrictionType.STATIC && !hasLinearStaticFriction
        || frictionType == FrictionType.KINETIC && !hasLinearKineticFriction) {
      return;
    }

    final Vec2 linearVelocity = velocity.v;

    // Calculate current momentum and force
    final Vec2 momentum = pool.popVec2();
    final Vec2 force = pool.popVec2();
    momentum.set(linearVelocity).mulLocal(mass);
    force.set(momentum).mulLocal(inverseDt);

    float forceMagnitude = force.length();
    if (frictionType == FrictionType.STATIC) {
      // Force greater than the force of static friction, switch to kinetic friction
      if (forceMagnitude > staticFrictionForce) {
        frictionType = FrictionType.KINETIC;

        // If there is no kinetic friction then there is nothing to do
        if (!hasLinearKineticFriction) {
          pool.pushVec2(2);
          return;
        }
      }
    }

    if (frictionType == FrictionType.KINETIC || !hasLinearStaticFriction) {
      // Clamp the applied force to the kinetic friction amount
      if (forceMagnitude > kineticFrictionForce) {
        force.mulLocal(kineticFrictionForce / forceMagnitude);
      }
    }

    // Calculate the new momentum from the force
    momentum.set(force).mulLocal(dt);

    // Update the total momentum
    lambdaP.addLocal(momentum);

    // Calculate the velocity change due to friction
    final Vec2 frictionVelocity = pool.popVec2();
    frictionVelocity.set(momentum).mulLocal(inverseMass);

    // Counteract the velocity
    velocity.v.subLocal(frictionVelocity);

    // Check if the object has become static
    if (frictionType != FrictionType.STATIC && isStatic(velocity)) {
      frictionType = FrictionType.STATIC;
    }

    // Give back the vectors to the pool
    pool.pushVec2(3);
  }

  private void applyAngularFriction(Velocity velocity) {
    if (frictionType == FrictionType.STATIC && !hasAngularStaticFriction
        || frictionType == FrictionType.KINETIC && !hasAngularKineticFriction) {
      return;
    }

    final float angularVelocity = velocity.w;

    // Calculate angular momentum and torque
    float angularMomentum = inertia * angularVelocity;
    float torque = inverseDt * angularMomentum;

    float torqueMagnitude = MathUtils.abs(torque);
    if (frictionType == FrictionType.STATIC) {
      // If applied torque greater than static friction torque, change to kinetic friction
      if (torqueMagnitude > staticFrictionTorque) {
        frictionType = FrictionType.KINETIC;

        // If there is no kinetic friction then there is nothing to do
        if (!hasAngularKineticFriction) {
          return;
        }
      }
    }

    if (frictionType == FrictionType.KINETIC || !hasAngularStaticFriction) {
      // Clamp the applied force to the kinetic friction amount
      if (torqueMagnitude > kineticFrictionTorque) {
        torque = MathUtils.clamp(torque, -kineticFrictionTorque, kineticFrictionTorque);
      }
    }

    // Calculate the new angular momentum from the torque
    angularMomentum = dt * torque;

    // Update the total momentum
    lambdaAP += angularMomentum;

    // Calculate the angular velocity change due to friction
    float frictionAngularVelocity = angularMomentum * inverseInertia;

    // Counteract the angular velocity
    velocity.w = angularVelocity - frictionAngularVelocity;

    // Check if the object has become static
    if (frictionType != FrictionType.STATIC && isStatic(velocity)) {
      frictionType = FrictionType.STATIC;
    }
  }

  @Override
  public boolean solvePositionConstraints(SolverData solverData) {
    return true;
  }

  private boolean isStatic(Velocity velocity) {
    Vec2 linearVelocity = velocity.v;
    float angularVelocity = velocity.w;
    return linearVelocity.lengthSquared() < linearVelocityThreshold
        && MathUtils.abs(angularVelocity) < angularVelocityThreshold;
  }
}

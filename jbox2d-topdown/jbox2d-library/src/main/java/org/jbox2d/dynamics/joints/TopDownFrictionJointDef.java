package org.jbox2d.dynamics.joints;

import org.jbox2d.dynamics.Body;

/**
 * Top down friction joint definition. This requires a single body.
 * Created by jamie on 2014/09/15.
 */
public class TopDownFrictionJointDef extends JointDef {

  float staticFrictionForce;
  float kineticFrictionForce;

  float staticFrictionTorque;
  float kineticFrictionTorque;

  /**
   * The linear velocity at which a body changes from being considered "static" to being considered "dynamic".
   */
  public float linearVelocityThreshold = 0.1f;

  /**
   * The angular velocity at which a body changes from being considered "static" to being considered "dynamic".
   */
  public float angularVelocityThreshold = 0.1f;

  public TopDownFrictionJointDef() {
    super(JointType.TOPDOWNFRICTION);
  }

  public void initialize(Body bodyA, float staticFrictionForce, float kineticFrictionForce,
      float staticFrictionTorque, float kineticFrictionTorque) {
    this.bodyA = bodyA;

    this.staticFrictionForce = staticFrictionForce;
    this.kineticFrictionForce = kineticFrictionForce;

    this.staticFrictionTorque = staticFrictionTorque;
    this.kineticFrictionTorque = kineticFrictionTorque;

    this.bodyB = null;
    this.collideConnected = false;
  }
}

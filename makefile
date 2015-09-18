SRC_DIR = hons-simulator-master/src/%.java
EXP_DIR = hons-experiment-master/src/%.java
DEMO_PATH = ""

all:
	@echo building simulator...
	@cd hons-simulator-master && \
	./gradlew fatJar && \
	cd .. && \
	rm hons-experiment-master/libs/hons-simulator-all.jar && \
	cp hons-simulator-master/build/libs/hons-simulator-master-all.jar hons-experiment-master/libs && \
	mv hons-experiment-master/libs/hons-simulator-master-all.jar hons-experiment-master/libs/hons-simulator-all.jar && \
	rm hons-morphev-master/libs/hons-simulator-all.jar && \
	cp hons-simulator-master/build/libs/hons-simulator-master-all.jar hons-morphev-master/libs && \
	mv hons-morphev-master/libs/hons-simulator-master-all.jar hons-morphev-master/libs/hons-simulator-all.jar && \
	rm hons-controller-master/libs/hons-simulator-all.jar && \
	cp hons-simulator-master/build/libs/hons-simulator-master-all.jar hons-controller-master/libs && \
	mv hons-controller-master/libs/hons-simulator-master-all.jar hons-controller-master/libs/hons-simulator-all.jar && \
	echo building experiment... && \
	cd hons-experiment-master && \
	./gradlew fatJar && \
	cd .. && \
	echo building morphev... && \
	cd hons-morphev-master && \
	./gradlew fatJar && \
	cd .. && \
	echo building controller... && \
	cd hons-controller-master && \
	./gradlew fatJar && \
	echo done

simulator:
	@echo building simulator...
	@cd hons-simulator-master && \
	./gradlew fatJar && \
	cd .. && \
	rm hons-experiment-master/libs/hons-simulator-all.jar && \
	cp hons-simulator-master/build/libs/hons-simulator-master-all.jar hons-experiment-master/libs && \
	mv hons-experiment-master/libs/hons-simulator-master-all.jar hons-experiment-master/libs/hons-simulator-all.jar && \
	rm hons-morphev-master/libs/hons-simulator-all.jar && \
	cp hons-simulator-master/build/libs/hons-simulator-master-all.jar hons-morphev-master/libs && \
	mv hons-morphev-master/libs/hons-simulator-master-all.jar hons-morphev-master/libs/hons-simulator-all.jar && \
	echo done

morphev_and_sim:
	@echo building simulator...
	@cd hons-simulator-master && \
	./gradlew fatJar && \
	cd .. && \
	rm hons-morphev-master/libs/hons-simulator-all.jar && \
	cp hons-simulator-master/build/libs/hons-simulator-master-all.jar hons-morphev-master/libs && \
	mv hons-morphev-master/libs/hons-simulator-master-all.jar hons-morphev-master/libs/hons-simulator-all.jar && \
	echo building morphev... && \
	cd hons-morphev-master && \
	./gradlew fatJar && \
	echo done

morphev_only:
	@echo building morphev... && \
	cd hons-morphev-master && \
	./gradlew fatJar && \
	echo done

controller_only:
	@echo building controller... && \
	cd hons-controller-master && \
	./gradlew fatJar && \
	echo done


experiment_and_sim:
	@echo building simulator...
	@cd hons-simulator-master && \
	./gradlew fatJar && \
	cd .. && \
	rm hons-experiment-master/libs/hons-simulator-all.jar && \
	cp hons-simulator-master/build/libs/hons-simulator-master-all.jar hons-experiment-master/libs && \
	mv hons-experiment-master/libs/hons-simulator-master-all.jar hons-experiment-master/libs/hons-simulator-all.jar && \
	echo building experiment... && \
	cd hons-experiment-master && \
	./gradlew fatJar && \
	echo done

run_experiment:
	@echo running experiment
	@java -jar hons-experiment-master/build/libs/hons-experiment-master-all.jar -c hons-experiment-master/config/mediumSimConfig.yml

run_morphev:
	@echo running morphology evolution
	@cd hons-morphev-master && \
	java -jar build/libs/hons-morphev-master-all.jar

run_morphev_demo:
	@echo running morphology evolution demo
	@cd hons-morphev-master && \
	java -jar build/libs/hons-morphev-master-all.jar --demo DEMO_PATH

SRC_DIR = hons-simulator-master/src/%.java
EXP_DIR = hons-experiment-master/src/%.java

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
	echo building experiment... && \
	cd hons-experiment-master && \
	./gradlew fatJar && \
	cd .. && \
	echo building morphev... && \
	cd hons-morphev-master && \
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


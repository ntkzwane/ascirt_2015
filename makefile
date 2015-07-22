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
	echo building experiment... && \
	cd hons-experiment-master && \
	./gradlew fatJar && \
	echo done

buildsim: hons-simulator-master/src/%.java
	@echo building simulator...
	@cd hons-simulator-master && \
	./gradlew fatJar && \
	cd .. && \
	rm hons-experiment-master/libs/hons-simulator-all.jar && \
	cp hons-simulator-master/build/libs/hons-simulator-master-all.jar hons-experiment-master/libs && \
	mv hons-experiment-master/libs/hons-simulator-master-all.jar hons-experiment-master/libs/hons-simulator-all.jar &&\
	echo done building simulator

buildexp: $(EXP_DIR)
	@echo building experiment... && \
	cd hons-experiment-master && \
	./gradlew fatJar && \
	echo done building experiment

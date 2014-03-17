#! /usr/bin/env bash

export COHERENCE_HOME=/Users/OCTO-YDE/Oracle/Middleware/Oracle_Home/coherence
export CONFIG_FOLDER=/Users/OCTO-YDE/coherence-quartz/coherence-quartz-core/src/main/resources
export CLIENT_CLASSES=/Users/OCTO-YDE/coherence-quartz/coherence-quartz-core/target/classes:/Users/OCTO-YDE/coherence-quartz/coherence-quartz-model/target/classes:/Users/OCTO-YDE/GWT-Gen/gwtgen-api/target/classes

java -Djava.util.logging.config.file=${CONFIG_FOLDER}/logging.properties -Dtangosol.coherence.cacheconfig=${CONFIG_FOLDER}/coherence-quartz-cache-config.xml -cp ${COHERENCE_HOME}:${CLIENT_CLASSES}:${COHERENCE_HOME}/lib/coherence.jar com.tangosol.net.DefaultCacheServer
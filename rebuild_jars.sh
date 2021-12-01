#!/bin/bash

#run script if some changes to jar's source are made
rm -rf Manager/artefacts/*

cd Executor
mvn clean
mvn package
cp target/Executor-1.0-SNAPSHOT.jar ../Manager/artefacts/executor.jar

cd ../Writer
mvn clean
mvn package
cp target/Writer-1.0-SNAPSHOT.jar  ../Manager/artefacts/writer.jar

cd ../Reader
mvn clean
mvn package
cp target/Reader-1.0-SNAPSHOT.jar ../Manager/artefacts/reader.jar

cd ../Manager/libs
rm executor.jar reader.jar writer.jar
cp ../artefacts/* .

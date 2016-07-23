#!/bin/bash
RELEASE_NUM="0.3"
BUILD_DIR="wrapper-$RELEASE_NUM"
mvn clean package && \
mkdir $BUILD_DIR 
cp target/*-jar-with-dependencies.jar $BUILD_DIR/wrapper.jar &&\
cp README* $BUILD_DIR/
cp src/main/resources/application.properties $BUILD_DIR/ 
cp start.* $BUILD_DIR/
zip -9 -r $BUILD_DIR.zip $BUILD_DIR/*

#!/bin/sh

mvn -Ddocker.username=${DOCKER_REGISTRY_USERNAME} -Ddocker.password=${DOCKER_REGISTRY_PASSWORD} $*


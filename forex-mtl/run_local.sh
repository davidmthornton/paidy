#!/bin/bash

docker pull paidyinc/one-frame
docker run -p 8080:8080 paidyinc/one-frame
sbt 'run'
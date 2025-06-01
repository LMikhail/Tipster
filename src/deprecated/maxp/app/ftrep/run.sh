#!/bin/bash

set -ae
source ../conf/prod.env
java -jar ftrep.jar > /app/log/ftrep.log


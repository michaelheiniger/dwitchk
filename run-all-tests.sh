#!/bin/bash

./gradlew clean check

./gradlew test

./gradlew connectedDebugAndroidTest
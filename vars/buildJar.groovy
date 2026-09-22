#!/usr/bin/env groovy

def call() {
    echo "Building the application for branch ${env.GIT_BRANCH}"
    sh 'mvn package'
}
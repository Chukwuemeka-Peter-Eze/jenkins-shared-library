#!/usr/bin/env groovy
package com.example

class Docker implements Serializable {

    def script

    Docker(script) {
        this.script = script
    }

    def buildDockerImage(String imageName) {
        script.echo "Building the Docker image: ${imageName}"
        script.sh "docker build -t ${imageName} ."
    }

    def dockerLogin() {
        script.withCredentials([
            script.usernamePassword(
                credentialsId: 'Docker-Hub-Credentials',
                usernameVariable: 'USER',
                passwordVariable: 'PASS'
            )
        ]) {
            // Single-quoted so $USER and $PASS are expanded by the shell
            // at execution time, inside the withCredentials block, rather
            // than Groovy-interpolated into the script string ahead of
            // time. This keeps the secret off the process command line
            // and lets Jenkins' log masking work correctly.
            script.sh 'echo $PASS | docker login -u $USER --password-stdin'
        }
    }

    def dockerPush(String imageName) {
        script.echo "Pushing image: ${imageName}"
        script.sh "docker push ${imageName}"
    }
}
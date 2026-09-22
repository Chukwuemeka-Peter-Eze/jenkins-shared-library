#!/usr/bin/env groovy
package com.example

class Deploy implements Serializable {

    def script

    Deploy(script) {
        this.script = script
    }

    def run(Map config) {
        def host = config.host
        def user = config.user
        def port = config.port
        def image = config.image
        def sshCredentialId = config.sshCredentialId

        def containerName = image.tokenize('/')[-1].tokenize(':')[0]

        def remoteCommand = "docker pull ${image} && " +
                             "docker stop ${containerName} || true && " +
                             "docker rm ${containerName} || true && " +
                             "docker run -d --name ${containerName} -p ${port}:${port} ${image}"

        script.sshagent([sshCredentialId]) {
            script.sh "ssh -o StrictHostKeyChecking=no ${user}@${host} '${remoteCommand}'"
        }
    }
}
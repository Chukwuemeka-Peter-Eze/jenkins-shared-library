#!/usr/bin/env groovy
package com.example

class Deploy implements Serializable {

def script

Deploy(script) {
    this.script = script
}

def run(Map config) {
    def image = config.image
    def hostPort = config.hostPort ?: '3080'
    def containerPort = config.containerPort ?: '3080'

    def containerName = image.tokenize('/')[-1].tokenize(':')[0]

    def remoteCommand = "docker pull ${image} && " +
                        "docker stop ${containerName} || true && " +
                        "docker rm ${containerName} || true && " +
                        "docker run -d --name ${containerName} -p ${hostPort}:${containerPort} ${image}"

    script.sh remoteCommand
}

}

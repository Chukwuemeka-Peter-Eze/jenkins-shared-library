#!/usr/bin/env groovy

library identifier: 'Jenkins-shared-library@main', retriever: modernSCM(
    [$class: 'GitSCMSource',
     remote: 'https://github.com/Chukwuemeka-Peter-Eze/Jenkins-shared-library.git'
    ]
)

pipeline {
    agent any

    tools {
        maven 'maven-3.9'
    }

    environment {
        // Docker image configuration
        DOCKER_NAMESPACE = 'pierrechukason'
        APP_NAME         = 'demo-app'
        IMAGE_TAG        = 'java-maven-1.0'
        IMAGE_NAME       = "${DOCKER_NAMESPACE}/${APP_NAME}:${IMAGE_TAG}"

        // Application port configuration
        HOST_PORT        = '3080'
        CONTAINER_PORT   = '8080'
    }

    stages {

        stage('Test') {
            steps {
                runTests()
            }
        }

        stage('Build App') {
            steps {
                buildJar()
            }
        }

        stage('Build Image') {
            steps {
                script {
                    buildImage(env.IMAGE_NAME)
                    dockerLogin()
                    dockerPush(env.IMAGE_NAME)
                }
            }
        }

        stage('Deploy') {
            steps {
                deployApp(
                    image: env.IMAGE_NAME,
                    hostPort: env.HOST_PORT,
                    containerPort: env.CONTAINER_PORT
                )
            }
        }
    }

    post {

        success {
            notifyBuildStatus('SUCCESS')
        }

        failure {
            notifyBuildStatus('FAILURE')
        }

        always {
            sh 'docker logout || true'
        }
    }
}
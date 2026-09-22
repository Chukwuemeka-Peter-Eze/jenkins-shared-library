# Setup

## Prerequisites

* Jenkins
* **Pipeline** plugin
* **Pipeline: Shared Groovy Libraries** plugin
* Maven configured in Jenkins under the name `Maven`
* Docker available to the Jenkins execution environment
* Docker Hub credentials
* SSH credentials for the deployment target
* Git

## Clone the Repository

```bash
git clone https://github.com/Chukwuemeka-Peter-Eze/Jenkins-shared-library.git
cd Jenkins-shared-library
```

## Demo Application

The repository includes a small Java/Maven application used to exercise the library end to end:

```text
src/
├── main/
│   ├── java/com/example/Application.java
│   └── resources/static/index.html
└── test/
    └── java/com/example/AppTest.java
```

`runTests()` runs `mvn test` against `AppTest.java`. `buildJar()` runs `mvn package`, which compiles `Application.java` and, through the `spring-boot-maven-plugin` repackage goal, produces the runnable jar that `buildImage()` packages into the Docker image.

## Jenkins Credentials

| Credential ID | Kind | Used by |
|---|---|---|
| `docker-hub-repo` | Username with password | `dockerLogin()` |
| `ec2-server-key` | SSH Username with private key | `deployApp()` |

Never commit passwords, tokens, private keys, API secrets, or cloud credentials to the repository, they belong in Jenkins Credentials only.

## Registering the Shared Library

`Jenkinsfile-SharedLibrary` loads the library dynamically from the `main` branch:

```groovy
library identifier: 'Jenkins-shared-library@main',
        retriever: modernSCM(
            [$class: 'GitSCMSource',
             remote: 'https://github.com/Chukwuemeka-Peter-Eze/Jenkins-shared-library.git'
            ]
        )
```

Registering it globally instead, under Manage Jenkins → System → Global Trusted Pipeline Libraries, lets a consuming Jenkinsfile use the shorter:

```groovy
@Library('Jenkins-shared-library') _
```

## Deployment Target

The `deploy` stage connects to `<DEPLOYMENT_HOST>` over SSH via `deployApp()`, pulls the newly pushed image, stops the previous container, and starts the new one. Provisioning steps for the target server (Docker install, SSH key generation and authorization) are the same process documented in the `jenkins-multibranch-pipeline` repository's setup guide.
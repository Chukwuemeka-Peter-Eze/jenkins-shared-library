# Jenkins Shared Library

> A reusable Jenkins Shared Library that centralizes CI/CD pipeline logic, reduces duplication across Jenkinsfiles, and provides a maintainable foundation for scalable automation.

<p align="center">

  <img src="https://img.shields.io/badge/Jenkins-CI%2FCD-red?logo=jenkins&logoColor=white" alt="Jenkins">
  <img src="https://img.shields.io/badge/Groovy-Pipeline-blue?logo=apachegroovy&logoColor=white" alt="Groovy">
  <img src="https://img.shields.io/badge/Maven-Build-orange?logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/Docker-Containers-blue?logo=docker&logoColor=white" alt="Docker">
  <img src="https://img.shields.io/badge/CI%2FCD-Automation-green" alt="CI/CD">

</p>

---

## Project Overview

This repository demonstrates how to design and consume a **Jenkins Shared Library** to move reusable CI/CD logic out of individual Jenkinsfiles and into a centralized automation layer.

The project combines:

- Jenkins Declarative Pipeline
- Groovy
- Jenkins Shared Libraries
- Maven
- Docker
- Docker Hub
- SSH-based deployment
- Jenkins Credentials
- Reusable pipeline steps
- Shared Library helper classes
- Externalized notification templates

The repository also contains a small **Java/Maven demo application** used to exercise the Shared Library end to end.

The goal is not simply to make a pipeline work.

The goal is to demonstrate an engineering pattern:

> **Keep application-specific configuration in the Jenkinsfile while moving reusable CI/CD implementation into a centralized Shared Library.**

---

## Table of Contents

- [Project Overview](#-project-overview)
- [Engineering Problem](#-engineering-problem)
- [Solution](#-solution)
- [What This Repository Demonstrates](#-what-this-repository-demonstrates)
- [Project Status](#-project-status)
- [Architecture](#-architecture)
- [Technology Stack](#-technology-stack)
- [Repository Structure](#-repository-structure)
- [Shared Library Components](#-shared-library-components)
- [Demo Application](#-demo-application)
- [How Jenkins Shared Libraries Work](#-how-jenkins-shared-libraries-work)
- [Setup](#-setup)
- [Using the Shared Library](#-using-the-shared-library)
- [Pipeline Flow](#-pipeline-flow)
- [Two Pipelines in This Repository](#-two-pipelines-in-this-repository)
- [Security Considerations](#-security-considerations)
- [Engineering Decisions](#-engineering-decisions)
- [Validation](#-validation)
- [Troubleshooting](#-troubleshooting)
- [Documentation](#-documentation)
- [Lessons Learned](#-lessons-learned)
- [Key Skills Demonstrated](#-key-skills-demonstrated)
- [Future Improvements](#-future-improvements)
- [Related Projects](#-related-projects)
- [Connect With Me](#-connect-with-me)
- [License](#-license)

---

# Engineering Problem

As the number of Jenkins pipelines increases, CI/CD logic can become duplicated across multiple repositories.

For example, several application repositories may independently implement:

- application checkout
- testing
- Maven builds
- Docker image creation
- registry authentication
- Docker image pushes
- deployment
- notifications
- credential handling
- validation

This creates a maintenance problem.

If the deployment logic changes, every Jenkinsfile containing that logic may need to be updated.

The engineering question becomes:

> **How can common CI/CD behavior be centralized while still allowing individual applications to control their own configuration?**

---

# Solution

This repository uses a **Jenkins Shared Library** as the reusable automation layer.

Instead of putting implementation details directly inside every Jenkinsfile:

```text
Application Jenkinsfile
        │
        │ calls
        ▼
Jenkins Shared Library
        │
        ├── vars/
        ├── src/
        └── resources/
```

The consuming Jenkinsfile becomes responsible primarily for:

- pipeline orchestration
- environment-specific configuration
- application-specific values

The Shared Library becomes responsible for reusable implementation such as:

- testing
- application builds
- Docker image creation
- Docker authentication
- image publishing
- deployment
- notifications

This creates a clearer separation between:

```text
APPLICATION CONFIGURATION
        │
        ▼
Jenkinsfile
        │
        ▼
REUSABLE CI/CD IMPLEMENTATION
        │
        ▼
Shared Library
```

---

# What This Repository Demonstrates

This project demonstrates the three major structural areas of a Jenkins Shared Library:

```text
vars/
│
└── Reusable pipeline steps

src/
│
└── Reusable Groovy helper classes

resources/
│
└── Externalized templates/resources
```

It also demonstrates how those components work together with a consuming Jenkinsfile.

### Reusable Pipeline Steps

The library exposes reusable steps for:

- testing
- building
- Docker image creation
- Docker authentication
- Docker image publishing
- deployment
- build-status notification

### Helper Classes

The `src/` directory contains reusable Groovy classes for:

- Docker operations
- deployment
- notifications

### Resources

The `resources/` directory contains a reusable notification template loaded with:

```groovy
libraryResource()
```

---

# Project Status

| Component | Status |
|---|---|
| Jenkins Shared Library structure | ✅ Implemented |
| `vars/` reusable pipeline steps | ✅ Implemented |
| `src/com/example/` helper classes | ✅ Implemented |
| `resources/com/example/` template | ✅ Implemented |
| Demo Java/Maven application | ✅ Present |
| Baseline `Jenkinsfile` | ✅ Implemented |
| `Jenkinsfile-SharedLibrary` | ✅ Implemented |
| Docker image build | ✅ Implemented |
| Docker registry push | ✅ Implemented |
| SSH-based deployment | ✅ Implemented |
| Build-status notification | ✅ Implemented |
| Shared Library troubleshooting documentation | ✅ Documented |
| Automated Shared Library tests | 🔄 Future improvement |
| Versioned Shared Library releases | 🔄 Future improvement |
| Real external notification integration | 🔄 Future improvement |

---

# 🏗 Architecture

```text
                         ┌───────────────────────────┐
                         │     Jenkins Controller     │
                         │                           │
                         │   Jenkinsfile             │
                         │   Jenkinsfile-            │
                         │   SharedLibrary           │
                         └─────────────┬─────────────┘
                                       │
                                       │ library(...)
                                       ▼
                    ┌──────────────────────────────────┐
                    │       Jenkins Shared Library      │
                    │                                  │
                    │  vars/                           │
                    │   ├── reusable pipeline steps    │
                    │                                  │
                    │  src/                            │
                    │   ├── Docker.groovy              │
                    │   ├── Deploy.groovy              │
                    │   └── Notifier.groovy            │
                    │                                  │
                    │  resources/                      │
                    │   └── notification template      │
                    └──────────────┬───────────────────┘
                                   │
              ┌────────────────────┼────────────────────┐
              │                    │                    │
              ▼                    ▼                    ▼
        Application A        Application B        Application C
        Jenkinsfile          Jenkinsfile          Jenkinsfile
```

The important architectural boundary is:

```text
┌─────────────────────────────────────────────────┐
│ Application-specific                            │
│                                                 │
│ Image name                                      │
│ Deployment host                                 │
│ Application port                                │
│ Environment configuration                        │
│ Credentials IDs                                 │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│ Shared implementation                           │
│                                                 │
│ Test                                            │
│ Build                                           │
│ Docker                                          │
│ Push                                            │
│ Deploy                                          │
│ Notify                                          │
└─────────────────────────────────────────────────┘
```

---

# 🛠 Technology Stack

| Technology | Purpose |
|---|---|
| **Jenkins** | CI/CD automation |
| **Groovy** | Jenkins Pipeline and Shared Library implementation |
| **Maven** | Java application build tool |
| **Docker** | Container image creation and runtime |
| **Docker Hub** | Container image registry |
| **Git / GitHub** | Source control and Shared Library hosting |
| **SSH** | Remote deployment |
| **AWS EC2** | Deployment target used by the demonstration |

---

# Repository Structure

The repository contains both the **Jenkins Shared Library implementation** and the **Java/Maven demo application** used to exercise it.

```text
Jenkins-shared-library/
│
├── docs/
│   ├── commands.md
│   ├── lessons-learned.md
│   ├── setup.md
│   └── troubleshooting.md
│
├── resources/
│   └── com/
│       └── example/
│           └── notify-template.txt
│
├── src/
│   ├── com/
│   │   └── example/
│   │       ├── Deploy.groovy
│   │       ├── Docker.groovy
│   │       └── Notifier.groovy
│   │
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── Application.java
│   │   │
│   │   └── resources/
│   │       └── static/
│   │           └── index.html
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── AppTest.java
│
├── vars/
│
├── .gitignore
├── Jenkinsfile
├── Jenkinsfile-SharedLibrary
├── pom.xml
└── README.md
```

> **Note:** The `vars/` directory contains the reusable pipeline steps exposed by the Shared Library. Its contents are consumed as pipeline steps rather than application source code.

---

# Shared Library Components

## `vars/`

The `vars/` directory exposes reusable pipeline steps that can be called directly from a Jenkinsfile.

The current library provides seven reusable operations:

| Shared Step | Purpose |
|---|---|
| `runTests()` | Runs the Maven test suite |
| `buildJar()` | Builds the application JAR |
| `buildImage(imageName)` | Builds a Docker image |
| `dockerLogin()` | Authenticates with Docker Hub |
| `dockerPush(imageName)` | Pushes an image to the registry |
| `deployApp(config)` | Deploys the container through SSH |
| `notifyBuildStatus(status)` | Generates a build-status notification |

This allows the consuming Jenkinsfile to express pipeline intent without containing all of the implementation details.

---

## `src/com/example/`

The `src/com/example/` directory contains reusable Groovy helper classes.

### `Docker.groovy`

Responsible for Docker-related operations including:

- Docker image creation
- Docker registry authentication
- Docker image publishing

### `Deploy.groovy`

Responsible for remote deployment.

The class receives deployment configuration such as:

- host
- user
- port
- image
- SSH credential ID

It then performs the remote Docker deployment through SSH.

### `Notifier.groovy`

Responsible for rendering the build-status notification.

It loads the notification template using:

```groovy
libraryResource('com/example/notify-template.txt')
```

and injects Jenkins build information such as:

- job name
- build number
- build status
- build URL

---

# `resources/`

The repository contains:

```text
resources/
└── com/
    └── example/
        └── notify-template.txt
```

The template contains placeholders such as:

```text
Build status for {{JOB_NAME}} #{{BUILD_NUMBER}}: {{STATUS}}

Details: {{BUILD_URL}}
```

Loaded through:

```groovy
libraryResource('com/example/notify-template.txt')
```

This separates notification formatting from the Groovy implementation.

The template can therefore be changed without modifying the notification class itself.

---

# Demo Application

The repository also contains a small Java/Maven application used to exercise the Shared Library.

Its application structure includes:

```text
src/
├── main/
│   ├── java/
│   │   └── com/example/
│   │       └── Application.java
│   │
│   └── resources/
│       └── static/
│           └── index.html
│
└── test/
    └── java/
        └── com/example/
            └── AppTest.java
```

The application provides a concrete workload for the CI/CD pipeline rather than having the Shared Library exist only as isolated Groovy code.

---

# How Jenkins Shared Libraries Work

The execution flow is:

```text
Application Jenkinsfile
        │
        ▼
Load Shared Library
        │
        ▼
Call reusable pipeline step
        │
        ▼
vars/*.groovy
        │
        ▼
src/com/example/*.groovy
        │
        ▼
Jenkins pipeline execution
        │
        ▼
Next pipeline stage
```

For example:

```groovy
stage('Deploy') {
    steps {
        deployApp(
            host: env.DEPLOY_HOST,
            user: env.DEPLOY_USER,
            port: env.APP_PORT,
            image: env.IMAGE_NAME,
            sshCredentialId: env.SSH_CREDENTIAL_ID
        )
    }
}
```

The Jenkinsfile expresses **what should happen**.

The Shared Library contains **how it happens**.

---

# Setup

## Prerequisites

Before using this repository, you need:

- Jenkins
- Pipeline plugin
- Pipeline: Shared Groovy Libraries plugin
- Maven configured in Jenkins
- Docker available to the Jenkins execution environment
- Docker Hub credentials
- SSH credentials for the deployment target
- Git

---

## Clone the Repository

```bash
git clone https://github.com/Chukwuemeka-Peter-Eze/Jenkins-shared-library.git
cd Jenkins-shared-library
```

---

# Jenkins Credentials

The demonstration pipeline uses Jenkins-managed credentials rather than hard-coded secrets.

Example credential IDs:

```text
docker-hub-repo
ec2-server-key
```

The actual secret values should remain inside Jenkins Credentials.

Never commit:

```text
passwords
tokens
private keys
API secrets
cloud credentials
```

to the repository.

---

# Registering the Shared Library

The demonstration pipeline dynamically loads the library from GitHub:

```groovy
library identifier: 'Jenkins-shared-library@main',
        retriever: modernSCM(
            [$class: 'GitSCMSource',
             remote: 'https://github.com/Chukwuemeka-Peter-Eze/Jenkins-shared-library.git'
            ]
        )
```

Alternatively, the library can be registered globally in:

```text
Manage Jenkins
    ↓
System
    ↓
Global Trusted Pipeline Libraries
```

A globally configured library can then be consumed with:

```groovy
@Library('Jenkins-shared-library') _
```

---

# Using the Shared Library

The consuming pipeline defines environment-specific configuration:

```groovy
environment {
    DOCKER_NAMESPACE  = 'pierrechukason'
    APP_NAME          = 'demo-app'
    IMAGE_TAG         = 'java-maven-1.0'
    IMAGE_NAME        = "${DOCKER_NAMESPACE}/${APP_NAME}:${IMAGE_TAG}"

    DEPLOY_HOST       = '<DEPLOYMENT_HOST>'
    DEPLOY_USER       = 'ec2-user'
    APP_PORT          = '3080'

    SSH_CREDENTIAL_ID = 'ec2-server-key'
}
```

The actual reusable operations are then delegated to the library:

```groovy
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
            host: env.DEPLOY_HOST,
            user: env.DEPLOY_USER,
            port: env.APP_PORT,
            image: env.IMAGE_NAME,
            sshCredentialId: env.SSH_CREDENTIAL_ID
        )
    }
}
```

This keeps the consuming Jenkinsfile focused on pipeline orchestration.

---

# Pipeline Flow

The Shared Library pipeline follows this lifecycle:

```text
┌────────────┐
│   Test     │
└─────┬──────┘
      │
      ▼
┌────────────┐
│ Build App  │
└─────┬──────┘
      │
      ▼
┌────────────┐
│Build Image │
└─────┬──────┘
      │
      ▼
┌────────────┐
│Docker Login│
└─────┬──────┘
      │
      ▼
┌────────────┐
│Docker Push │
└─────┬──────┘
      │
      ▼
┌────────────┐
│  Deploy    │
└─────┬──────┘
      │
      ▼
┌────────────┐
│  Notify    │
└────────────┘
```

---

# Two Pipelines in This Repository

The repository intentionally contains two pipeline examples.

## `Jenkinsfile`

The baseline pipeline.

It represents the pipeline approach without the Shared Library abstraction and provides a comparison point.

## `Jenkinsfile-SharedLibrary`

The Shared Library implementation.

This pipeline consumes the reusable library steps for:

```text
Test
Build
Build Image
Docker Login
Docker Push
Deploy
Notify
```

This comparison demonstrates the difference between putting CI/CD implementation directly in a Jenkinsfile and extracting reusable logic into a Shared Library.

---

# Security Considerations

The project uses several security-conscious practices.

### Jenkins Credentials

Secrets are stored in Jenkins Credentials rather than committed to source control.

### SSH Agent

SSH deployment uses Jenkins' `sshagent` mechanism.

### Credential Expansion

Credentials bound into the pipeline are expanded by the shell rather than being unnecessarily interpolated by Groovy.

### Repository Access

Access to modify the Shared Library should be restricted because changes to shared pipeline code can affect every consuming application.

### Production Considerations

The demonstration environment should not be treated as a production security baseline.

For production usage, additional controls should be considered around:

- SSH host verification
- credential scope
- registry permissions
- branch protection
- library versioning
- approval of trusted library changes
- deployment authorization

---

# Engineering Decisions

## Centralize Reusable Pipeline Logic

**Decision**

Move common CI/CD functionality into the Shared Library.

**Reason**

Multiple application repositories can consume the same implementation instead of maintaining duplicated pipeline logic.

---

## Separate Configuration from Implementation

**Decision**

Keep values such as image names, deployment hosts, ports, and credential IDs in the consuming Jenkinsfile.

**Reason**

Different applications can reuse the same implementation while supplying different configuration.

---

## Use `vars/` for Pipeline-Facing Steps

**Decision**

Expose reusable functionality through simple pipeline steps.

**Reason**

The consuming Jenkinsfile remains readable and declarative.

---

## Use `src/` for Reusable Implementation

**Decision**

Move more substantial reusable logic into Groovy classes.

**Reason**

This provides a cleaner separation between pipeline-facing functions and their underlying implementation.

---

## Use `resources/` for Templates

**Decision**

Keep notification formatting in an external template.

**Reason**

The message format can change without modifying the Groovy notification implementation.

---

# Validation

The demonstration pipeline validates the Shared Library through an end-to-end workflow.

The validation includes:

- Shared Library retrieval from Git
- Library loading from the Jenkinsfile
- Execution of reusable pipeline steps
- Maven build/test operations
- Docker image creation
- Registry authentication
- Docker image push
- SSH deployment
- Notification rendering

The repository also retains the baseline `Jenkinsfile` so that the library-powered pipeline can be compared with the non-library approach.

---

# Troubleshooting

Detailed troubleshooting information is maintained separately.

See [`docs/troubleshooting.md`](./docs/troubleshooting.md).

The documentation includes issues related to:

- Shared Library execution
- `NotSerializableException`
- `libraryResource()` paths
- Jenkins configuration
- pipeline execution problems

---

# Documentation

| Document | Purpose |
|---|---|
| [`docs/setup.md`](./docs/setup.md) | Environment and Jenkins setup |
| [`docs/commands.md`](./docs/commands.md) | Useful command reference |
| [`docs/troubleshooting.md`](./docs/troubleshooting.md) | Troubleshooting and failure diagnosis |
| [`docs/lessons-learned.md`](./docs/lessons-learned.md) | Engineering lessons and implementation decisions |

---

# Lessons Learned

Building this project highlighted several practical CI/CD engineering concepts:

- Shared Libraries are an abstraction mechanism, not simply a collection of Groovy scripts.
- `vars/`, `src/`, and `resources/` serve different purposes.
- Pipeline readability improves when implementation details are extracted from Jenkinsfiles.
- Credentials should be managed by Jenkins rather than stored in source code.
- Shared infrastructure code requires stronger change discipline because multiple consumers may depend on it.
- Library versioning becomes increasingly important as the number of consumers grows.
- Reusable deployment logic needs clear configuration boundaries.
- Security considerations become more important when automation gains deployment access.

More detailed notes are available in [`docs/lessons-learned.md`](./docs/lessons-learned.md).

---

# Key Skills Demonstrated

This project demonstrates practical experience with:

### Jenkins

- Declarative Pipelines
- Jenkins Credentials
- Pipeline automation
- Shared Libraries
- `library()`
- `@Library`
- `libraryResource()`
- `sshagent`

### Groovy

- Pipeline scripting
- reusable classes
- maps and configuration objects
- serialization considerations
- separation of orchestration and implementation

### Docker

- Docker image creation
- registry authentication
- image publishing
- remote container deployment

### CI/CD Architecture

- reusable automation
- separation of concerns
- pipeline abstraction
- centralized CI/CD logic
- application-specific configuration

### DevOps Engineering

- automated builds
- automated testing
- containerization
- registry publishing
- remote deployment
- credentials management

---

# Future Improvements

The current implementation provides a working foundation, but several improvements can take it further.

## 1. Add Automated Shared Library Tests

Introduce automated tests for the reusable pipeline steps and helper classes.

## 2. Version the Shared Library

Instead of relying exclusively on:

```text
main
```

introduce versioned releases such as:

```text
v1.0.0
v1.1.0
v2.0.0
```

This allows consuming pipelines to adopt library changes deliberately.

## 3. Add Real Notification Integrations

Extend:

```text
notifyBuildStatus()
```

to support integrations such as:

- Slack
- Microsoft Teams
- Email

## 4. Add Jenkinsfile Validation

Introduce automated validation/linting of pipeline definitions.

## 5. Improve Deployment Safety

Future iterations could include:

- SSH host verification
- deployment health checks
- rollback handling
- container health checks
- deployment timeouts
- failure recovery

## 6. Support Multiple Library Versions

Allow different applications to consume different stable versions of the Shared Library.

---

# Related Projects

### Jenkins CI Pipeline

[jenkins-ci-pipeline](https://github.com/Chukwuemeka-Peter-Eze/jenkins-ci-pipeline)

A Jenkins CI/CD pipeline project demonstrating pipeline automation without the Shared Library abstraction.

### Jenkins Multibranch Pipeline

[jenkins-multibranch-pipeline](https://github.com/Chukwuemeka-Peter-Eze/jenkins-multibranch-pipeline)

A Jenkins Multibranch Pipeline project demonstrating branch-aware pipeline automation.

---

# Connect With Me

**GitHub**
https://github.com/Chukwuemeka-Peter-Eze

**LinkedIn**
https://www.linkedin.com/in/chukwuemekapetereze/

---

# License

This project is licensed under the MIT License. See [`LICENSE`](./LICENSE) for details.
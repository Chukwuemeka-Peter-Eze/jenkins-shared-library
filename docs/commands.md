# Commands Reference

## Maven

```bash
mvn test                # runTests(), runs AppTest.java
mvn package              # buildJar(), compiles Application.java and repackages it
mvn spring-boot:run      # run the application locally, outside the pipeline
```

## Docker

```bash
docker build -t pierrechukason/demo-app:java-maven-1.0 .
echo $PASS | docker login -u $USER --password-stdin
docker push pierrechukason/demo-app:java-maven-1.0
docker pull pierrechukason/demo-app:java-maven-1.0
docker stop demo-app
docker rm demo-app
docker run -d --name demo-app -p 3080:3080 pierrechukason/demo-app:java-maven-1.0
docker logout
```

These are exactly what `dockerLogin()`, `dockerPush()`, and `deployApp()` run internally through `Docker.groovy` and `Deploy.groovy`.

## SSH

```bash
ssh -o StrictHostKeyChecking=no ec2-user@<DEPLOYMENT_HOST> 'docker pull ... && docker stop ... && docker rm ... && docker run ...'
```

The full remote command `deployApp()` constructs and runs through `sshagent`.

## Git

```bash
git clone https://github.com/Chukwuemeka-Peter-Eze/Jenkins-shared-library.git
git rm -r --cached lib/
git commit -m "Remove lib/ from version control"
```

## Shared Library steps

```groovy
runTests()
buildJar()
buildImage(env.IMAGE_NAME)
dockerLogin()
dockerPush(env.IMAGE_NAME)
deployApp(host: ..., user: ..., port: ..., image: ..., sshCredentialId: ...)
notifyBuildStatus('SUCCESS')
```

These are pipeline steps exposed by this library, called directly from a consuming Jenkinsfile like any built-in Jenkins step.
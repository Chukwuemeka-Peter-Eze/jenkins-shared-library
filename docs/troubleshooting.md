# Troubleshooting

## Library not found / checkout fails

**Likely cause:** the `remote` URL in the `library(...)` step is wrong, or the repository's default branch has changed from `main`.

**Check:** the remote URL is reachable and the branch in `identifier: 'Jenkins-shared-library@main'` matches GitHub.

## `mvn package` builds a jar that won't start

**Likely cause:** `Application.java` is missing the `@SpringBootApplication` annotation, or isn't on the classpath `spring-boot-maven-plugin`'s repackage goal expects. Without a valid main class, the jar builds but exits immediately when run.

**Check:** run `mvn package` locally, then `java -jar target/*.jar` directly, before assuming the problem is in Jenkins or Docker. If it fails locally, it'll fail identically in the pipeline.

## `mvn test` fails or reports no tests found

**Likely cause:** `AppTest.java` isn't under `src/test/java/com/example/`, matching Maven's standard test source layout, or the test class doesn't follow JUnit's naming/annotation conventions.

**Check:** confirm the file is at exactly `src/test/java/com/example/AppTest.java`, not `src/main/` or a different package path.

## `buildImage()` or `dockerPush()` fails

**Likely cause:** Docker isn't available on the Jenkins agent, or the `docker-hub-repo` credential is missing or has invalid login details.

**Check:** run `docker --version` on the agent directly, and compare the credential ID in Jenkins against `docker-hub-repo` in `Docker.groovy` character for character.

## `deployApp()` fails over SSH

**Likely cause:** the `ec2-server-key` credential isn't a valid private key, or the target host's security group doesn't allow inbound SSH from the Jenkins agent.

**Check:** SSH manually from the Jenkins host using the same key before assuming the problem is in the pipeline.

## App deploys but isn't reachable on the expected port

**Likely cause:** the port `Application.java`'s Spring Boot config actually listens on (`server.port` in `application.properties`) doesn't match `APP_PORT` in the Jenkinsfile's `environment {}` block, so the `docker run -p` mapping points at the wrong internal port.

**Check:** confirm both values agree before assuming the deploy itself failed, this fails silently, the container runs, the pipeline reports success, but nothing responds at the expected address.

## `groovy.lang.MissingPropertyException` or a step isn't recognized

**Likely cause:** the `library()` step is missing, misplaced after the step that uses it, or failed to load silently.

**Check:** `library identifier: ...` must appear at the top of the Jenkinsfile, before `runTests()`, `buildJar()`, `buildImage()`, `dockerLogin()`, `dockerPush()`, `deployApp()`, or `notifyBuildStatus()` are called.

## `NotSerializableException` inside a `vars/` step or `src/` class

**Likely cause:** Jenkins Pipeline runs in CPS (Continuation Passing Style) and serializes pipeline state between steps. Custom classes referenced across steps need to implement `Serializable`, which is why `Docker`, `Deploy`, and `Notifier` all do.

**Check:** any new helper class added to `src/com/example/` needs `Serializable`, and shouldn't hold references to non-serializable field types.

## `notifyBuildStatus()` fails with a resource not found error

**Likely cause:** `libraryResource('com/example/notify-template.txt')` looks for the file at that exact path under `resources/`, mirroring the same package-path convention as `src/`.

**Check:** the file exists at `resources/com/example/notify-template.txt`, not directly under `resources/`.

## Credential appears in Jenkins console output despite `withCredentials`

**Likely cause:** a bound credential variable was interpolated directly into a Groovy string (`"${script.PASS}"`) before being passed to `sh`, which can print it before Jenkins' masking applies.

**Check:** any `sh` call using a bound credential should use single quotes so the shell expands the variable at execution time: `sh 'echo $PASS | docker login -u $USER --password-stdin'`, not double-quoted Groovy interpolation.
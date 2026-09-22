# Lessons Learned

## Finding and fixing a credential-handling bug

The first version of `dockerLogin()` interpolated the bound credential directly into the shell command string using Groovy string interpolation. It worked, but the secret value got resolved into the command string before Jenkins' credential masking had a chance to intercept it. The fix: single-quote the shell script so `$PASS` and `$USER` are expanded by the shell itself, at execution time, inside the `withCredentials` block. A pipeline can succeed and still be handling secrets unsafely, and that gap doesn't show up until something specifically checks for it.

## Deploy logic belongs in the library, not the Jenkinsfile

The first version of this pipeline had the SSH deploy command built and run directly inline in the Jenkinsfile's `deploy` stage, while everything else (build, image, push) went through the library. That was inconsistent with the whole point of building a Shared Library: centralized, reusable logic. Extracting it into `Deploy.groovy` and a `deployApp()` step means any future consuming Jenkinsfile gets the same pull-stop-remove-run deployment pattern without reimplementing it.

## A Shared Library needs a real workload to prove itself against

Early versions of this repository had `pom.xml` and library code but no actual application source. A Shared Library that only ever runs against a stub proves the pipeline mechanics work, but not that the library handles a real build. Adding `Application.java`, `AppTest.java`, and a static resource gave the pipeline something genuine to test, build, containerize, and deploy, and surfaced the port-mismatch class of failure (the app's actual `server.port` needs to agree with the pipeline's `APP_PORT`) that a stub app would never expose.

## Serialization isn't optional in Shared Libraries

Any custom class used across pipeline steps needs to implement `Serializable`, or the pipeline fails with errors that don't obviously point back to the missing interface. Building `Docker`, `Deploy`, and `Notifier` with this in mind from the start avoided a debugging detour a lot of first Shared Library attempts run into.

## `resources/` and `libraryResource()` are underused

Most Shared Library examples only ever demonstrate `vars/` and `src/`. Building `notifyBuildStatus()` to load a template from `resources/com/example/notify-template.txt` via `libraryResource()` uses a third, less commonly demonstrated part of the Shared Library feature set, and sets up a real extension point: the same template-based message can be sent to Slack, Teams, or email later without changing the pipeline steps that call it.

## Keeping infrastructure details out of a public README

The actual deployment host is a real, working server, and it doesn't need to be published verbatim in documentation anyone can read. Using a placeholder like `<DEPLOYMENT_HOST>` in the README and docs, while the real value lives only in the Jenkinsfile's `environment {}` block, keeps the documentation useful without needlessly publishing infrastructure details.

## Keeping binaries out of version control

The `lib/` directory held the Groovy SDK jars, committed by accident early on. Removing them and adding `lib/` to `.gitignore` is a small fix, but it's a reminder that a repository accumulates whatever gets committed without review.
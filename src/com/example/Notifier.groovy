#!/usr/bin/env groovy
package com.example

class Notifier implements Serializable {

    def script

    Notifier(script) {
        this.script = script
    }

    def notify(String status) {
        def template = script.libraryResource('com/example/notify-template.txt')
        def message = template
            .replace('{{JOB_NAME}}', script.env.JOB_NAME ?: 'unknown-job')
            .replace('{{BUILD_NUMBER}}', script.env.BUILD_NUMBER ?: '0')
            .replace('{{STATUS}}', status)
            .replace('{{BUILD_URL}}', script.env.BUILD_URL ?: '')

        script.echo message

        // Extension point: replace this echo with an HTTP POST to a
        // Slack or Teams webhook, or an email step, once a notification
        // channel credential is configured.
    }
}
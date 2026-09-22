#!/usr/bin/env groovy

import com.example.Notifier

def call(String status) {
    return new Notifier(this).notify(status)
}
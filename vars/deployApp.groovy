#!/usr/bin/env groovy

import com.example.Deploy

def call(Map config) {
    return new Deploy(this).run(config)
}
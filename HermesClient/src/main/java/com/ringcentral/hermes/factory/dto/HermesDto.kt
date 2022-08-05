package com.ringcentral.hermes.factory.dto

import com.ringcentral.hermes.factory.ShellFactory

class HermesDto {
    lateinit var platformName: String
    lateinit var udid: String
    var isIOSSimulator: Boolean = false
    lateinit var hostName: String
    lateinit var hermesPort: Number
    lateinit var shellExec: ShellFactory.ShellExec
}
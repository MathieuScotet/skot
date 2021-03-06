package tech.skot.viewmodel

import tech.skot.contract.modelcontract.Poker

interface NetworkApplicativeModel {
    sealed class Status {
        object Wifi : Status()
        object Mobile : Status()
        object NotConnected : Status()
    }

    val status: Status
    val isConnected: Boolean

    val statusChangedPoker: Poker
}
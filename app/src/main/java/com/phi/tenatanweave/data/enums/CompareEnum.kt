package com.phi.tenatanweave.data.enums

enum class CompareEnum {
    EQUAL,
    LESS_THAN,
    GREATER_THAN;

    override fun toString(): String {
        return when (this) {
            EQUAL -> "="
            LESS_THAN -> "<"
            GREATER_THAN -> ">"
        }
    }
}
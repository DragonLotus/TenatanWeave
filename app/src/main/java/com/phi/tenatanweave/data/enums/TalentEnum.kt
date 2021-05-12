package com.phi.tenatanweave.data.enums

enum class TalentEnum {
    ALL,
    SHADOW,
    LIGHT;

    override fun toString(): String {
        return when (this) {
            ALL -> "All"
            SHADOW -> "Shadow"
            LIGHT -> "Light"
        }
    }
}
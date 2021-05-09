package com.phi.tenatanweave.data

import android.util.Log
import com.phi.tenatanweave.data.enums.ClassEnum
import com.phi.tenatanweave.data.enums.SubTypeEnum
import com.phi.tenatanweave.data.enums.TalentEnum
import com.phi.tenatanweave.data.enums.TypeEnum

class BaseCard(
    val id: String = "",
    val name: String = "",
    val text: String = "",
    val talent: String = "",
    val heroClass: String = "",
    val type: String = "",
    val subTypes: List<String> = listOf(),
    val weaponSlots: Int = -1,
    val intellect: String = "",
    val health: String = "",
    val variableHealth: List<Int> = listOf(),
    val variablePower: List<Int> = listOf(),
    val variableDefense: List<Int> = listOf(),
    val variableValue: List<Int> = listOf(),
    val power: List<String> = listOf(),
    val defense: List<String> = listOf(),
    val pitch: List<String> = listOf(),
    val cost: String = "",
    val deckLimit: Int = -1,
    val specialization: List<String> = listOf(),
    val legalFormats: List<String> = listOf(),
    val printings: List<String> = listOf()
) {

    fun getFullTypeAsString(): String {
        return "${if (talent.isNotEmpty()) getTalentAsEnum().toString() + " " else ""}${if (heroClass.isNotEmpty()) getHeroClassAsEnum().toString() + " " else ""}${getTypeAsEnum().toFullString()} ${
            if (subTypes.isNotEmpty()) "- " + getSubTypesAsEnum().joinToString(" ")
                .replace("\\b" + SubTypeEnum.ALL.toString() +"\\b", "UNKNOWN") else ""
        }${if (weaponSlots == 2) " (2H)" else if (weaponSlots == 1) " (1H)" else ""}"
    }

    fun getTalentAsEnum(): TalentEnum {
        return try {
            TalentEnum.valueOf(talent)
        } catch (e: IllegalArgumentException) {
            TalentEnum.ALL
        }
    }

    fun getHeroClassAsEnum(): ClassEnum {
        return try {
            ClassEnum.valueOf(heroClass)
        } catch (e: IllegalArgumentException) {
            ClassEnum.ALL
        }
    }

    fun getTypeAsEnum(): TypeEnum {
        return try {
            TypeEnum.valueOf(type)
        } catch (e: IllegalArgumentException) {
            TypeEnum.ALL
        }
    }

    fun getSubTypesAsEnum(): List<SubTypeEnum> {
        val subTypeSet = mutableSetOf<SubTypeEnum>()
        for (subType in subTypes) {
            try {
                subTypeSet.add(SubTypeEnum.valueOf(subType))
            } catch (e: IllegalArgumentException) {
                subTypeSet.add(SubTypeEnum.ALL)
            }
        }
        return subTypeSet.toList()
    }

    fun getPowerSafe(version: Int): Int {
        return if (power.isNullOrEmpty())
            0
        else
            try {
                Integer.parseInt(power[version])
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.d("BaseCard", "Version $version is out of bounds. Cannot get power value of $name.")
                Log.d("BaseCard", e.toString())
                0
            } catch (e: NumberFormatException) {
                Log.d("BaseCard", "Unable to parse $version of power as an integer. Cannot get power value of $name.")
                Log.d("BaseCard", e.toString())
                0
            }
    }

    fun getPowerStringSafe(version: Int): String {
        return if (power.isNullOrEmpty())
            ""
        else
            try {
                power[version]
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.d("BaseCard", "Version $version is out of bounds. Cannot get power value of $name.")
                Log.d("BaseCard", e.toString())
                ""
            }
    }

    fun getDefenseSafe(version: Int): Int {
        return if (defense.isNullOrEmpty())
            0
        else
            try {
                Integer.parseInt(defense[version])
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.d("BaseCard", "Version $version is out of bounds. Cannot get defense value of $name.")
                Log.d("BaseCard", e.toString())
                0
            } catch (e: NumberFormatException) {
                Log.d(
                    "BaseCard",
                    "Unable to parse $version of defense as an integer. Cannot get defense value of $name."
                )
                Log.d("BaseCard", e.toString())
                0
            }
    }

    fun getDefenseStringSafe(version: Int): String {
        return if (defense.isNullOrEmpty())
            ""
        else
            try {
                defense[version]
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.d("BaseCard", "Version $version is out of bounds. Cannot get defense value of $name.")
                Log.d("BaseCard", e.toString())
                ""
            }
    }

    fun getPitchSafe(version: Int): Int {
        return if (pitch.isNullOrEmpty())
            0
        else
            try {
                Integer.parseInt(pitch[version])
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.d("BaseCard", "Version $version is out of bounds. Cannot get pitch value of $name.")
                Log.d("BaseCard", e.toString())
                0
            } catch (e: NumberFormatException) {
                Log.d("BaseCard", "Unable to parse $version of pitch as an integer. Cannot get pitch value of $name.")
                Log.d("BaseCard", e.toString())
                -1
            }
    }

    fun getPitchStringSafe(version: Int): String {
        return if (pitch.isNullOrEmpty())
            ""
        else
            try {
                pitch[version]
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.d("BaseCard", "Version $version is out of bounds. Cannot get pitch value of $name.")
                Log.d("BaseCard", e.toString())
                ""
            }
    }

    fun getIntellectSafe(): Int {
        return if (intellect.isEmpty())
            -1
        else
            try {
                Integer.parseInt(intellect)
            } catch (e: NumberFormatException) {
                Log.d("BaseCard", "Unable to parse $intellect as an integer. Cannot get intellect value of $name.")
                Log.d("BaseCard", e.toString())
                -1
            }
    }

    fun getHealthSafe(): Int {
        return if (health.isEmpty())
            -1
        else
            try {
                Integer.parseInt(health)
            } catch (e: NumberFormatException) {
                Log.d("BaseCard", "Unable to parse $health as an integer. Cannot get health value of $name.")
                Log.d("BaseCard", e.toString())
                -1
            }
    }

    fun getCostSafe(): Int {
        return if (cost.isEmpty())
            -1
        else
            try {
                Integer.parseInt(cost)
            } catch (e: NumberFormatException) {
                Log.d("BaseCard", "Unable to parse $cost as an integer. Cannot get cost value of $name.")
                Log.d("BaseCard", e.toString())
            }
    }

//    companion object Factory {
//        fun createFromSnapshot(dataSnapshot: DataSnapshot): BaseCard {
//            return BaseCard(
//                dataSnapshot.key.toString(),
//
//            )
//        }
//
//        fun getValueFromSnapshot(dataSnapshot: DataSnapshot, field: String) : String {
//            return ""
//        }
//    }
}
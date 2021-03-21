package com.phi.tenatanweave.data

import android.util.Log
import com.phi.tenatanweave.data.enums.ClassEnum
import com.phi.tenatanweave.data.enums.SubTypeEnum
import com.phi.tenatanweave.data.enums.TypeEnum

class BaseCard(
    val id: String = "",
    val name: String = "",
    val text: String = "",
    val heroClass: String = "",
    val type: String = "",
    val subTypes: List<String> = listOf(),
    val weaponSlots: Int = -1,
    val intellect: Int = -1,
    val health: Int = -1,
    val variableHealth: List<Int> = listOf(),
    val variablePower: List<Int> = listOf(),
    val variableDefense: List<Int> = listOf(),
    val variableValue: List<Int> = listOf(),
    val power: List<Int> = listOf(),
    val defense: List<Int> = listOf(),
    val pitch: List<Int> = listOf(),
    val cost: Int = -1,
    val deckLimit: Int = -1,
    val specialization: List<String> = listOf(),
    val legalFormats: MutableList<String> = mutableListOf(),
    val printings: List<String> = listOf()
) {

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
                power[version]
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.d("BaseCard", "Version $version is out of bounds. Cannot get power value of $name.")
                Log.d("BaseCard", e.toString())
                0
            }
    }

    fun getDefenseSafe(version: Int): Int {
        return if (defense.isNullOrEmpty())
            0
        else
            try {
                defense[version]
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.d("BaseCard", "Version $version is out of bounds. Cannot get defense value of $name.")
                Log.d("BaseCard", e.toString())
                0
            }
    }

    fun getPitchSafe(version: Int): Int {
        return if (pitch.isNullOrEmpty())
            0
        else
            try {
                pitch[version]
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.d("BaseCard", "Version $version is out of bounds. Cannot get pitch value of $name.")
                Log.d("BaseCard", e.toString())
                0
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
package com.phi.tenatanweave.data

import com.google.firebase.database.DataSnapshot
import com.phi.tenatanweave.data.enums.*
import java.lang.IllegalArgumentException

class BaseCard(
    val id: String = "",
    val name: String = "",
    val text: String = "",
//    val heroClass: ClassEnum = ClassEnum.GENERIC,
    val heroClass: String = "",
//    val type: TypeEnum = TypeEnum.HERO,
    val type: String = "",
//    val subTypes: List<SubTypeEnum> = listOf(),
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
    val legalFormats: MutableList<FormatEnum> = mutableListOf(),
    val printings: List<String> = listOf()
) {

    fun getHeroClassAsEnum() : ClassEnum{
        return try {
            ClassEnum.valueOf(heroClass)
        } catch (e: IllegalArgumentException){
            ClassEnum.ALL
        }
    }

    fun getTypeAsEnum() : TypeEnum{
        return try {
            TypeEnum.valueOf(type)
        } catch (e: IllegalArgumentException){
            TypeEnum.ALL
        }
    }

    fun getSubTypesAsEnum() : List<SubTypeEnum>{
        val subTypeSet = mutableSetOf<SubTypeEnum>()
        for (subType in subTypes) {
            try {
                subTypeSet.add(SubTypeEnum.valueOf(subType))
            } catch (e: IllegalArgumentException){
                subTypeSet.add(SubTypeEnum.ALL)
            }
        }
        return subTypeSet.toList()
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
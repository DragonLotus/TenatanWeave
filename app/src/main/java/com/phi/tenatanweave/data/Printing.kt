package com.phi.tenatanweave.data

import android.os.Parcelable
import android.util.Log
import com.phi.tenatanweave.data.enums.FinishEnum
import com.phi.tenatanweave.data.enums.RarityEnum
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
class Printing(
    val id: String = "",
    val name: String = "",
    val version: Int = 0,
    val flavorText: String = "",
    val rarity: RarityEnum = RarityEnum.P,
    val artist: String = "",
    val setCode: String = "",
    var set: @RawValue ExpansionSet? = null,
    val collectorNumber: Int = 0,
    val finishList: List<FinishEnum> = listOf(),
    var finishVersion: Int = 0,
    var parentSide: String = "",
    var isMarvel: Boolean = false,
    var baseCard: BaseCard = BaseCard()
) : Parcelable {
    fun getFinishSafe(finishVersion: Int): FinishEnum {
        return if (finishList.isNullOrEmpty())
            FinishEnum.REGULAR
        else
            try {
                finishList[finishVersion]
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.d("BaseCard", "Finish version $finishVersion is out of bounds. Cannot get finish value of $name.")
                Log.d("BaseCard", e.toString())
                finishList[0]
            }
    }

    fun getPowerSafe(): Int {
        return baseCard.getPowerSafe(version)
    }

    fun getPowerStringSafe(): String {
        return baseCard.getPowerStringSafe(version)
    }

    fun getDefenseSafe(): Int {
        return baseCard.getDefenseSafe(version)
    }

    fun getDefenseStringSafe(): String {
        return baseCard.getDefenseStringSafe(version)
    }

    fun getPitchSafe(): Int {
        return baseCard.getPitchSafe(version)
    }

    fun getPitchStringSafe(): String {
        return baseCard.getPitchStringSafe(version)
    }

    fun getIntellectSafe(): Int {
        return baseCard.getIntellectSafe()
    }

    fun getHealthSafe(): Int {
        return baseCard.getHealthSafe()
    }

    fun getCostSafe(): Int {
        return baseCard.getCostSafe()
    }
}
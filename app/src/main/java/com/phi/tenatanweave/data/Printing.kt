package com.phi.tenatanweave.data

import android.util.Log
import com.google.gson.Gson
import com.phi.tenatanweave.data.enums.FinishEnum
import com.phi.tenatanweave.data.enums.RarityEnum

class Printing(
    val id: String = "",
    val name: String = "",
    val version: Int = 0,
    val flavorText: String = "",
    val rarity: RarityEnum = RarityEnum.P,
    val artist: String = "",
    val setCode: String = "",
    var set: ExpansionSet? = null,
    val collectorNumber: Int = 0,
    val finishList: List<FinishEnum> = listOf(),
    var finishVersion: Int = 0,
    var baseCard: BaseCard = BaseCard()
) {
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

    fun deepCopy() : Printing{
        return Printing()
    }
}
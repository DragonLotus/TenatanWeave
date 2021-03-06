package com.phi.tenatanweave.data

import android.util.Log
import com.phi.tenatanweave.data.enums.FinishEnum
import com.phi.tenatanweave.data.enums.RarityEnum
import com.phi.tenatanweave.data.enums.SetEnum

class Printing(
    val id: String = "",
    val name: String = "",
    val version: Int = 0,
    val flavorText: String = "",
    val rarity: RarityEnum = RarityEnum.P,
    val artist: String = "",
    val setCode: String = "",
    var set : ExpansionSet? = null,
    val collectorNumber: Int = 0,
    val finish: MutableList<FinishEnum> = mutableListOf()
) {
    fun getFinishSafe(finishVersion: Int):  FinishEnum{
        return if(finish.isNullOrEmpty())
            FinishEnum.REGULAR
        else
            try {
                finish[finishVersion]
            } catch (e: ArrayIndexOutOfBoundsException){
                Log.d("BaseCard", "Finish version $finishVersion is out of bounds. Cannot get finish value of $name.")
                Log.d("BaseCard", e.toString())
                finish[0]
            }
    }
}
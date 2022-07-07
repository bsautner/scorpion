package scorpion.device

import com.google.gson.Gson

data class SonarData(val f : Double, val l: Double, val r: Double, val d: Double) {




    fun isClear() : Boolean {
        if (f > distance ) {
              return true
        }
        return false
    }

    companion object {
        const val  distance = 30
        fun fromJson(json: String) : SonarData {
            val f1 = json.trimStart('\"').trimEnd('\"').replace("\\", "")
            return Gson().fromJson(f1, SonarData::class.java)
        }
    }

}

package scorpion.device

object Sonar {
    val distance = 50
    const val FRONT_SONAR = "front_sonar"
    const val RIGHT_FRONT_SONAR = "right_front_sonar"
    const val LEFT_FRONT_SONAR = "left_front_sonar"
    const val DOWN_FRONT_SONAR = "down_front_sonar"
    private val data : MutableMap<String, Double> = mutableMapOf(
        Pair(FRONT_SONAR, 0.0),
        Pair(RIGHT_FRONT_SONAR, 0.0),
        Pair(LEFT_FRONT_SONAR, 0.0),
        Pair(DOWN_FRONT_SONAR, 0.0),
    )

    fun getDistance(it: String) : Double {
        return data[it]!!
    }

    fun feed(it: String) {
        println(it)
        val split = it.replace("\"", "").split(",")
        data[split[0]] = split[1].toDouble()
    }

    fun clear() : Boolean {
        return data[RIGHT_FRONT_SONAR]!! > distance && data[LEFT_FRONT_SONAR]!! > distance && data[FRONT_SONAR]!! > distance
    }



}
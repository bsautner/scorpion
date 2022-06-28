package scorpion.device

data class Sonar(val f : Double, val l: Double, val r: Double, val d: Double) {

    fun isClear() : Boolean {
        if (f > 20) {
            return true
        }
        return false
    }
}

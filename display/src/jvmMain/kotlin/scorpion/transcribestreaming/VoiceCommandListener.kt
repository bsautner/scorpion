package scorpion.transcribestreaming

interface VoiceCommandListener {

    fun onVoiceCommand(cmd: String)
}
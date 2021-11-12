package ar.edu.itba.hci.android.ui.routine

data class Routine(
    val name:String,
    val durationMinutes:Int,
    val exercises:List<Exercise>
) {
    val shareLink = "https://google.com/"
}
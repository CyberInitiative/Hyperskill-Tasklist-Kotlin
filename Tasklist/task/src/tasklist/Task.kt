package tasklist

import kotlinx.datetime.*

data class Task(
    var date: String = "",
    var time: String = "",
    var priority: String = "",
    val subList: MutableList<String> = mutableListOf(),
    var dueTag : String = ""
) {
    fun defineDueTag(){
        val parsedDate = LocalDate.parse(date)
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
        val numberOfDays = currentDate.daysUntil(parsedDate)
        when {
            numberOfDays == 0 -> dueTag = "T"
            numberOfDays > 0 -> dueTag = "I"
            numberOfDays < 0 -> dueTag = "O"
        }
    }

}
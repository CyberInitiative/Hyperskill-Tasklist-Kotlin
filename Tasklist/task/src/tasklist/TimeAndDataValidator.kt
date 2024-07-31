package tasklist

import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object TimeAndDateValidator {
    fun isValidTime(timeStr: String): String? {
        val timePattern = """^([01]?[0-9]|2[0-3]):[0-5]?[0-9]$""".toRegex()
        return if (timePattern.matches(timeStr)) {
            try {
                val parsedTime = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("H:m"))
                parsedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            } catch (e: DateTimeParseException) {
                null
            }
        } else {
            null
        }
    }

    fun isValidDate(dateStr: String): String? {
        val datePattern = """^\d{4}-\d{1,2}-\d{1,2}$""".toRegex()
        if (!datePattern.matches(dateStr)) {
            return null
        }

        val (yearStr, monthStr, dayStr) = dateStr.split('-')
        val year = yearStr.toIntOrNull() ?: return null
        val month = monthStr.toIntOrNull() ?: return null
        val day = dayStr.toIntOrNull() ?: return null

        if (year < 1 || month !in 1..12 || day < 1) {
            return null
        }

        return try {
            val date = LocalDate.of(year, month, day)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            date.format(formatter)
        } catch (e: DateTimeException) {
            null
        }
    }
}
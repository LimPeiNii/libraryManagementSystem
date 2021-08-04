package library.util
import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeParseException}

object DateUtil {
    val pattern = "dd/MM/yyyy"
    val formatter = DateTimeFormatter.ofPattern(pattern)

    implicit class DateFormatter (val date: LocalDate){
        //convert local date to string
        def inString: String = {
            if (date == null)
                return ""
            formatter.format(date);
        }
    }

    implicit class StringFormatter (val dateString: String) {
        //convert string to local date
        def parseLocalDate: LocalDate = {
            try
                LocalDate.parse(dateString, formatter)
            catch{
                case e: DateTimeParseException => null
            }
        }
        
        //check if the string format is correct
        def isValid: Boolean = dateString.parseLocalDate != null
    }
}
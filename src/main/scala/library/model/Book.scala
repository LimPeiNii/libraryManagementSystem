package library.model
import scalafx.beans.property.{StringProperty, ObjectProperty} 
import java.time.LocalDate
import library.util.Database
import library.util.DateUtil._
import scala.util.Try
import library.MainApp

abstract class Book() extends Database{
    val _id: String
    val _title: String
    val _author: String
    val _language: String
    val _location: String
    val _publish: String
    val _entry: String
    val _status: String
    val idFrontPart: String
    val idLastPart: String
    val idNum = new StringProperty(_id)
    val bookTitle = new StringProperty(_title)
    val author = new StringProperty(_author)
    val language = new StringProperty(_language)
    var location = new StringProperty(_location)
    val publishedDate = ObjectProperty[LocalDate](_publish.parseLocalDate)
    val entryDate = ObjectProperty[LocalDate](_entry.parseLocalDate)
    var dueDate = ObjectProperty[LocalDate]("".parseLocalDate)
    var status = new StringProperty(_status)

    def saveBook(): Try[Int]

    def deleteBook(): Try[Int]

    def isExist: Boolean
}

object Book{
    //store genre list, genre name and genre symbol of each genre
    val genreDataList = List(("Horror", "H", MainApp.horrorList), ("Romance", "R", MainApp.romanceList), ("History", "I", MainApp.historyList))  
}
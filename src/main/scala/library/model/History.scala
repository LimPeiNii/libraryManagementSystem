package library.model
import java.time.LocalDate
import library.util.Database
import library.util.DateUtil._
import scalikejdbc._
import scala.util.{Try, Success, Failure}

class History (val _id: String, val _title: String, val _author: String, val _language: String, val _location: String, val _publish: String, val _entry: String, val _status: String) extends NonFiction{
    def this() = this(null, null, null, null, null, null, null, null)
    
    val idLastPart = "I"

    def saveBook(): Try[Int] = {
        //insert new record
        if (!(isExist)) {
            Try(DB autoCommit {implicit session =>
                    sql"""insert into History(idNum, bookTitle, author, language, location, publishedDate, 
                        entryDate, dueDate, status) values (${idNum.value}, ${bookTitle.value}, 
                        ${author.value}, ${language.value}, ${location.value}, ${publishedDate.value.inString}, 
                        ${entryDate.value.inString}, ${dueDate.value.inString}, ${status.value})
                    """.update.apply()
            })
        } 
        //update a record
        else {
            Try(DB autoCommit {implicit session =>
                    sql"""
                    update History
                    set
                    bookTitle = ${bookTitle.value},
                    author = ${author.value},
                    language = ${language.value},
                    location = ${location.value},
                    publishedDate = ${publishedDate.value.inString},
                    entryDate = ${entryDate.value.inString},
                    dueDate = ${dueDate.value.inString},
                    status = ${status.value}
                    where idNum = ${idNum.value}
                    """.update.apply()
            })
        }
    }

    def deleteBook(): Try[Int] = {
        if (isExist){
            Try (DB autoCommit {implicit session =>
                sql"""
                    delete from History
                    where idNum = ${idNum.value}
                """.update.apply()
            })
        } else 
            throw new Exception ("Book is not exist in Database")
    }

    //check if a record is exist
    def isExist: Boolean = {
        DB readOnly {implicit session =>
            sql"""
                select * from History
                where idNum = ${idNum.value}
            """.map(rs => rs.string("idNum")).single.apply()
        } match {
            case Some(x) => true
            case None => false
        }
    }
}

object History extends Database{
    def apply(
        _idS: String, 
        _titleS: String,
        _authorS: String,
        _languageS: String,
        _locationS: String,
        _publishS: String,
        _entryS: String,
        _returnDateS: String,
        _statusS: String
    ): History = new History(_idS, _titleS, _authorS, _languageS, _locationS, _publishS, _entryS, _statusS){
                    dueDate.value = _returnDateS.parseLocalDate                
                }

    def createTable() = {
        DB autoCommit {implicit session =>
            sql"""
            create table History(
                idNum varchar(10) not null PRIMARY KEY,
                bookTitle varchar(100),
                author varchar(100),
                language varchar(15),
                location varchar(10),
                publishedDate char(10),
                entryDate char(10),
                dueDate char(10),
                status varchar(20))
            """.execute.apply()
        }
    }

    def getAllBook: List[History] = {
        DB readOnly {implicit session =>
            sql"select * from History".map(rs => History(rs.string("idNum"), rs.string("bookTitle"), 
            rs.string("author"), rs.string("language"), rs.string("location"), rs.string("publishedDate"), 
            rs.string("entryDate"), rs.string("dueDate"), rs.string("status") )).list.apply()
        }
    }
}
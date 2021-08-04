package library.model
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import scalikejdbc._
import library.util.Database
import library.util.DateUtil._
import scala.util.{Try, Success, Failure}
import library.MainApp


class Record[+T] (val person: T, val book: Book) extends Database{  
    var recordID = Record.id
    var borrowDate = LocalDate.now
    
    //to check the person object is student or staff
    def isStudent: Boolean = {
        if (person.isInstanceOf[Student])
            return true
        else
            return false
    }
    
    def dueDate: LocalDate = {
        if (isStudent)
            borrowDate.plusDays(7)
        else
            borrowDate.plusDays(30)
    }

    def calculateOverdueFees (): Double = {
        var amount: Double = 0.0
        if (LocalDate.now.isAfter(dueDate)){
            var dueDays: Long = ChronoUnit.DAYS.between(LocalDate.now, dueDate).toInt.abs
            val total: Double = dueDays*0.10
            amount = (f"$total%.2f").toDouble
        }
        amount
    }

    def saveRecord() : Unit = {
        //insert a record
		if (isExist == false) {
            //to get the id of the person for database storing
            var personID = ""
            if (isStudent)
                personID = person.asInstanceOf[Student].idNum.value
            else 
                personID = person.asInstanceOf[Staff].idNum.value
			
            //insert into database
            Try(DB autoCommit { implicit session => 
				sql"""
					insert into Record (recordID, personID, bookID, borrowDate) 
                    values (${recordID}, ${personID}, ${book.idNum.value},
                    ${borrowDate.inString})
				""".update.apply()
			})
		} 			
	}

	def deleteRecord() : Try[Int] = {
		if (isExist) {
			Try(DB autoCommit { implicit session => 
			sql"""
				delete from Record where  
				recordID = ${recordID}
				""".update.apply()
			})
		} else 
			throw new Exception("Record not Exists in Database")		
	}

    //check if a record is exist
	def isExist : Boolean =  {
		DB readOnly { implicit session =>
			sql"""
				select * from Record where 
				recordID = ${recordID} 
			""".map(rs => rs.string("recordID")).single.apply()
		} match {
			case Some(x) => true
			case None => false
		}

	}
}

object Record extends Database{
    var id = 0

    //convert id to person
    def idToPerson(id: String): Any = {
        val tempStuList = MainApp.studentList.toList
        var index = tempStuList.indexWhere(stu => stu.idNum.value == id)
        //if the id is belongs to a staff
        if (index == -1){
            val tempStaffList = MainApp.staffList.toList
            index = tempStaffList.indexWhere(staff => staff.idNum.value == id)
            return tempStaffList.apply(index)
        }
        //if the if is belongs to a student
        else
            return tempStuList.apply(index)
    }    

    //convert id to book
    def idToBook(id: String): Book = {
        val tempBookList = MainApp.bookList.toList
        val index = tempBookList.indexWhere(book => book.idNum.value == id)
        tempBookList.apply(index)
    }

    def apply (
      _recordID: Int,
      _personID : String, 
      _bookID : String,
      _borrowDate : String
	) : Record[Any] = {
            val thisRecord = new Record[Any](idToPerson(_personID), idToBook(_bookID)) {
                recordID = _recordID
                borrowDate = _borrowDate.parseLocalDate
            }
            if (_personID.charAt(0) == 's')
                idToPerson(_personID).asInstanceOf[Student].borrowList += thisRecord.asInstanceOf[Record[Student]]
            else
                idToPerson(_personID).asInstanceOf[Staff].borrowList += thisRecord.asInstanceOf[Record[Staff]]
            thisRecord
	}

	def createTable() = {
		DB autoCommit { implicit session => 
			sql"""
			  create table Record (
			  recordID int not null PRIMARY KEY, 
              personID varchar(15), 
              bookID varchar(15),
			  borrowDate varchar(10))
			""".execute.apply()
		}
	}

    def getAllRecord : List[Record[Any]] = {
		DB readOnly { implicit session =>
			sql"select * from Record".map(rs => Record(rs.int("recordID"), rs.string("personID"),
				rs.string("bookID"),rs.string("borrowDate") )).list.apply()
		}
	}
}
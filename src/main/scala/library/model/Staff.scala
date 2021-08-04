package library.model
import scalafx.beans.property.{StringProperty, ObjectProperty} 
import java.time.LocalDate
import library.util.Database
import library.util.DateUtil._
import scalikejdbc._
import scala.util.{Try, Success, Failure}
import scalafx.collections.ObservableBuffer

class Staff (val id: String, var name: String, val birthDate: String, var mobileNum: String, var emailAdd: String, val natio: String, val entry: String) extends Database{
    def this() = this(null,null,null,null,null,null,null)

    val idNum = new StringProperty(id)
    var staffName = new StringProperty(name)
    val dob = ObjectProperty[LocalDate](birthDate.parseLocalDate)
    var mobile = new StringProperty(mobileNum)
    var email = new StringProperty(emailAdd)
    val nationality = new StringProperty(natio)
    val entryDate = ObjectProperty[LocalDate](entry.parseLocalDate)
    val borrowList = new ObservableBuffer[Record[Staff]]()
}

object Staff extends Database{
    def apply(
        _id: String,
        _name: String, 
        _birthDate: String,
        _mobileNum: String,
        _emailAdd: String,
        _natio: String,
        _entry: String
    ): Staff =  new Staff(_id, _name, _birthDate, _mobileNum, _emailAdd, _natio, _entry)

    def createTable() = {
        DB autoCommit {implicit session =>
            sql"""
                create table staff(
                idNum varchar(20) not null PRIMARY KEY,
                staffName varchar(30), 
                dob varchar(64), 
                mobile varchar(11), 
                email varchar(50),
                nationality varchar(30), 
                entryDate varchar(64))
            """.execute.apply()
            }
    }

    def getAllStaff: List[Staff] = {
        DB readOnly {implicit session =>
            sql"select * from staff".map(rs => Staff(rs.string("idNum"), 
            rs.string("staffName"), rs.string("dob"), rs.string("mobile"), 
            rs.string("email"), rs.string("nationality"), 
            rs.string("entryDate"))).list.apply()
        }
    }  
}
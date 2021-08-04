package library.model
import scalafx.beans.property.{StringProperty, ObjectProperty} 
import java.time.LocalDate
import library.util.Database
import library.util.DateUtil._
import library.MainApp
import scalikejdbc._
import scala.util.{Try, Success, Failure}
import scalafx.collections.ObservableBuffer

class Student (val id: String, var name: String, val birthDate: String, var mobileNum: String, var emailAdd: String, val natio: String, var courseName: String, val enrol: String, var graduate: String) extends Database{
    def this() = this(null,null,null,null,null,null,null,null,null)
    
    val idNum = new StringProperty(id)
    var stuName = new StringProperty(name)
    val dob = ObjectProperty[LocalDate](birthDate.parseLocalDate)
    var mobile = new StringProperty(mobileNum)
    var email = new StringProperty(emailAdd)
    val nationality = new StringProperty(natio)
    var course = new StringProperty(courseName)
    val enrolDate = ObjectProperty[LocalDate](enrol.parseLocalDate)
    var graduateDate = ObjectProperty[LocalDate](graduate.parseLocalDate)
    val borrowList = new ObservableBuffer[Record[Student]]()
}

object Student extends Database{
    def apply(
        _id: String,
        _name: String, 
        _birthDate: String,
        _mobileNum: String,
        _emailAdd: String,
        _natio: String,
        _courseName: String,
        _enrol: String,
        _graduate: String,
    ): Student = new Student(_id, _name, _birthDate, _mobileNum, _emailAdd, _natio, _courseName, _enrol, _graduate)

    def createTable() = {
        DB autoCommit {implicit session =>
            sql"""
                create table student(
                idNum varchar(20) not null PRIMARY KEY,
                stuName varchar(30), 
                dob varchar(64), 
                mobile varchar(11), 
                email varchar(50),
                nationality varchar(30), 
                course varchar(100), 
                enrolDate varchar(64), 
                graduateDate varchar(64))
            """.execute.apply()
        }
    }

    def getAllStudent: List[Student] = {
        DB readOnly {implicit session =>
            sql"select * from student".map(rs => Student(rs.string("idNum"), 
            rs.string("stuName"), rs.string("dob"), rs.string("mobile"), 
            rs.string("email"), rs.string("nationality"), rs.string("course"), 
            rs.string("enrolDate"), rs.string("graduateDate"))).list.apply()
        }
    }
}
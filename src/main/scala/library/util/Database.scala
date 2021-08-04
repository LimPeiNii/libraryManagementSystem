package library.util
import scalikejdbc._
import library.model.{Horror, Romance, History, Record, Student, Staff, DailyRecord, Account} 

trait Database{
    val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
    val dbURL = "jdbc:derby:myDB;create=true;";
    Class.forName(derbyDriverClassname) 
    ConnectionPool.singleton(dbURL, "me", "mine")

    //ad-hoc session provider on the REPL
    implicit val session = AutoSession
}

object Database extends Database{
    //check if the database has these table
    def hasHorrorBookTable: Boolean = {
        DB getTable "Horror" match{
            case Some(x) => true
            case None => false
        }
    }

    def hasRomanceBookTable: Boolean = {
        DB getTable "Romance" match{
            case Some(x) => true
            case None => false
        }
    }

    def hasHistoryBookTable: Boolean = {
        DB getTable "History" match{
            case Some(x) => true
            case None => false
        }
    }

    def hasStudentTable: Boolean = {
        DB getTable "student" match{
            case Some(x) => true
            case None => false
        }
    }

    def hasStaffTable: Boolean = {
        DB getTable "staff" match{
            case Some(x) => true
            case None => false
        }
    }

    def hasDailyRecordTable: Boolean = {
        DB getTable "DailyRecord" match{
            case Some(x) => true
            case None => false
        }
    }

    def hasRecordTable: Boolean = {
        DB getTable "Record" match{
            case Some(x) => true
            case None => false
        }
    }

    def hasAccountTable: Boolean = {
        DB getTable "account" match{
            case Some(x) => true
            case None => false
        }
    }    

    def setupDB() = {
        if (!hasHorrorBookTable)
            Horror.createTable()
        if (!hasRomanceBookTable)
            Romance.createTable()
        if (!hasHistoryBookTable)
            History.createTable()
        if (!hasRecordTable)
            Record.createTable()
        if (!hasStudentTable)
            Student.createTable()
        if (!hasStaffTable)
            Staff.createTable()
        if (!hasDailyRecordTable)
            DailyRecord.createTable()
        if (!hasAccountTable)
            Account.createTable()   
    }
}

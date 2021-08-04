package library.model
import scalikejdbc._
import library.util.Database
import library.util.DateUtil._
import scala.util.{Try, Success, Failure}
import java.time.LocalDate
import scalafx.beans.property.{ObjectProperty, StringProperty} 

class DailyRecord (val date: LocalDate) extends Database{
    def this() = this(null)
    
    //wrap the date into stringproperty
    val dateWrapped = StringProperty(date.inString)
    var expectedOverdueFee = ObjectProperty[Double](0.0)
    //received overdue fee
    var overdueFee = ObjectProperty[Double](0.0)
    //received printing fee
    var printingFee = ObjectProperty[Double](0.0)
    var expectedPrintingFee = ObjectProperty[Double](0.0)
    
    def overdueFeeLoss: ObjectProperty[Double] = {
        val total = overdueFee.value - expectedOverdueFee.value
        ObjectProperty[Double](f"${total}%.2f".toDouble)
    }

    def printingFeeLoss: ObjectProperty[Double] = {
        val total = printingFee.value - expectedPrintingFee.value
        ObjectProperty[Double](f"${total}%.2f".toDouble)
    }

    def totalReceived: ObjectProperty[Double] = {
        val totalAmount = overdueFee.value + printingFee.value
        ObjectProperty[Double](f"${totalAmount}%.2f".toDouble)
    }

    def saveDailyRecord() : Try[Int] = {
        //insert new record
		if (!(isExist)) {
			Try(DB autoCommit { implicit session => 
				sql"""
					insert into DailyRecord (date, overdueFee, overdueFeeReceived, printingFee, expectedPrintingFee) values 
						(${date.inString}, ${expectedOverdueFee.value.toString}, ${overdueFee.value.toString}, ${printingFee.value.toString}, 
                        ${expectedPrintingFee.value.toString})
				""".update.apply()
			})
		} 
        //update record
        else {
			Try(DB autoCommit { implicit session => 
				sql"""
				update DailyRecord 
				set 
				overdueFee = ${expectedOverdueFee.value.toString} ,
                overdueFeeReceived = ${overdueFee.value.toString},
				printingFee = ${printingFee.value.toString},
				expectedPrintingFee = ${expectedPrintingFee.value.toString}
				 where date = ${date.inString}
				""".update.apply()
			})
		}	
	}

    //check if a record is exist
    def isExist : Boolean =  {
		DB readOnly { implicit session =>
			sql"""
				select * from DailyRecord where 
				date = ${date.inString}
			""".map(rs => rs.string("date")).single.apply()
		} match {
			case Some(x) => true
			case None => false
		}
	}
}


object DailyRecord extends Database{
    var todayRecord: DailyRecord = null

    def apply(
        _date: String, 
        _expectedOverdueFee: String, 
        _overdueFee: String,
        _printingFee: String, 
        _expectedPrintingFee: String
	) : DailyRecord = {
        val aDailyRecord = new DailyRecord(_date.parseLocalDate) {
            expectedOverdueFee.value = _expectedOverdueFee.toDouble
            overdueFee.value = _overdueFee.toDouble
            printingFee.value = _printingFee.toDouble
            expectedPrintingFee.value = _expectedPrintingFee.toDouble
        }
        if (_date.parseLocalDate == LocalDate.now)
            todayRecord = aDailyRecord
        aDailyRecord
    }

    def createTable() = {
		DB autoCommit { implicit session => 
			sql"""
			create table DailyRecord (
			  date varchar(10) not null PRIMARY KEY, 
              overdueFee varchar(10),
              overdueFeeReceived varchar(10),
              printingFee varchar(10), 
              expectedPrintingFee varchar(10)
			)
			""".execute.apply()
		}
	}

    def getAllDailyRecord : List[DailyRecord] = {
		DB readOnly { implicit session =>
			sql"select * from DailyRecord".map(rs => DailyRecord(rs.string("date"), rs.string("overdueFee"),
				rs.string("overdueFeeReceived"), rs.string("printingFee"),rs.string("expectedPrintingFee") )).list.apply()
		}
	}
}
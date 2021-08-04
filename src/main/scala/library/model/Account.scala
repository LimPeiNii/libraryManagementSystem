package library.model
import library.util.Database
import scalikejdbc._
import scala.util.{Try, Success, Failure}

class Account (val username: String, var password: String) extends Database{
    def this() = this(null, null)

	def saveAccount() : Try[Int] = {
		//create new record
		if (!(isExist)) {
            Try(DB autoCommit { implicit session => 
                sql"""
                    insert into account (id, username, password) values 
                        (${0}, ${username}, ${password})
                """.update.apply()
            })
		}
		//update record 
		else {
			Try(DB autoCommit { implicit session => 
				sql"""
				update account 
				set 
				username  = ${username} ,
				password   = ${password}
				 where id = ${0}
				""".update.apply()
			})
		}	
	}

	//check if a record is exist
	def isExist : Boolean =  {
		DB readOnly { implicit session =>
			sql"""
				select * from account where 
				id = ${0} 
			""".map(rs => rs.int("id")).single.apply()
		} match {
			case Some(x) => true
			case None => false
		}

	}
}

object Account extends Database{
    def apply (
      _username : String, 
      _password : String
	) : Account = new Account(_username, _password)

	def createTable() = {
		DB autoCommit { implicit session => 
			sql"""
			create table account (
			  id int not null PRIMARY KEY, 
			  username varchar(20), 
			  password varchar(20)
			)
			""".execute.apply()
		}
	}

  	def getAllAccount : List[Account] = {
		DB readOnly { implicit session =>
			sql"select * from account".map(rs => Account(
                rs.string("username"), rs.string("password") )).list.apply()
		}
	}
}
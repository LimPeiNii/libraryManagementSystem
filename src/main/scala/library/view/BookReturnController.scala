package library.view
import library.MainApp
import library.util.AlertWindowPop
import library.util.DateUtil._
import scalafx.scene.control.{RadioButton, TextField, Label}
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import library.model.{Staff, Student, Record, DailyRecord, Book}
import javafx.scene.input.{KeyEvent,KeyCode}
import scalafxml.core.macros.sfxml
import scalafx.Includes._

@sfxml
class BookReturnController(
    private val bookIdButton: RadioButton,
    private val idTextField: TextField,
    private val bookIdLabel: Label,
    private val bookTitleLabel: Label,
    private val dueDateLabel: Label,
    private val overdueDaysLabel: Label,
    private val totalFeeLabel: Label

) extends AlertWindowPop{

    //show a book details when the book is returned
    private def showDetails(book: Option[Book]) = {
        book match {
            case Some(book) =>
                var dueDays: Int = 0
                bookIdLabel.text <== book.idNum
                bookTitleLabel.text <== book.bookTitle
                //show the due days if the current date is later than due date
                if (LocalDate.now.isAfter(book.dueDate.value)){
                    dueDays = ChronoUnit.DAYS.between(book.dueDate.value, LocalDate.now).toInt
                    overdueDaysLabel.text = dueDays.toString
                }
                else
                    overdueDaysLabel.text = 0.toString
                val total: Double = dueDays*0.10
                totalFeeLabel.text = f"$total%.2f"
                dueDateLabel.text = book.dueDate.value.inString 

            case None =>
                bookIdLabel.text.unbind()
                bookTitleLabel.text.unbind()
                bookIdLabel.text      = ""
                bookTitleLabel.text   = ""
                dueDateLabel.text     = ""
                overdueDaysLabel.text = ""
                totalFeeLabel.text    = ""
        }
    }

    showDetails(None)

    def returnBook (keyEvent: KeyEvent): Unit = {
        if (keyEvent.getCode() == KeyCode.ENTER){
            var alert: Boolean = false   
            var unborrowed = false
            var idInput = idTextField.text.value   
            var index = -1

            //check if the entered ID has correct format          
            var indexLastCharCheck = Book.genreDataList.indexWhere(tuple => tuple._2 == idInput.takeRight(1))
            if ((idInput.take(2)=="BF" || idInput.take(2)=="BN") && idInput.length == 9 && indexLastCharCheck != -1){
                val tempBookList = MainApp.bookList.toList
                index = tempBookList.indexWhere(book => book.idNum.value == idInput)
                //book not found            
                if (index == -1)
                    alert = true 
                else{
                    var book = MainApp.bookList.apply(index)
                    //check if the book is borrowed by someone
                    if (book.status.value == "checked out"){
                        showDetails(Option(book))
                        //update book status and dueDate value
                        book.status.value = "available"
                        book.dueDate.value = null
                        //update database
                        book.saveBook()
                        idTextField.text.value = ""
                        for (r <- 0 until MainApp.recordList.length){
                            var record = MainApp.recordList.apply(r)
                            if (record.book == book){
                                //when borrower is student
                                if (record.isStudent == true)
                                    //remove the record
                                    record.person.asInstanceOf[Student].borrowList -= record.asInstanceOf[Record[Student]]
                                
                                //when borrower is staff
                                else
                                    //remove the record
                                    record.person.asInstanceOf[Staff].borrowList -= record.asInstanceOf[Record[Staff]]
                                
                                /*add overdue fee to total overduefee of daily record (= how many money receive today)
                                if due days = 0, then the fee calculated is 0*/
                                DailyRecord.todayRecord.expectedOverdueFee.value += record.calculateOverdueFees()
                                //update today record in database
                                DailyRecord.todayRecord.saveDailyRecord()
                                MainApp.recordList -= record
                                record.deleteRecord()
                                return 
                            }
                        }
                    }
                    else 
                        //when the book is not checked out
                        unborrowed = true
                }
            }
            
            else // invalid ID 
                popAlertError("Invalid Book ID", "Please double check the entered ID.", "Book ID (" + idInput + ") format invalid")       
            if (alert == true)
                // this ID doesnt exist
                popAlertWarning("Non-exist Book ID", "Please double check the entered ID.", "Book ID (" + idInput + ") doesnt exist")
            if (unborrowed == true)
                //book is not borrowed by anyone etc.
                popAlertWarning("Book is not checked out", "Please double check.", "This book is not borrowed by anyone.")
        }
    }
}
package library.view
import library.MainApp
import scalafx.scene.control.{RadioButton, TextField, TableView, TableColumn, Label}
import javafx.scene.input.{KeyEvent,KeyCode}
import java.time.LocalDate
import scalafxml.core.macros.sfxml
import library.model.{Student, Record}
import library.util.Behavior
import library.util.DateUtil._
import scalafx.beans.property.ObjectProperty
import scalafx.Includes._

@sfxml
class StudentBookBorrowController(
    private val personIdButton: RadioButton,
    private val bookIdButton: RadioButton,
    private val idTextField: TextField,
    private val bookBorrowedTable: TableView[Record[Student]],
    private val bookIdColumn: TableColumn[Record[Student], String],
    private val bookTitleColumn: TableColumn[Record[Student], String],
    private val authorColumn: TableColumn[Record[Student], String],
    private val borrowDateColumn: TableColumn[Record[Student], LocalDate],
    private val dueDateColumn: TableColumn[Record[Student], LocalDate],
    private val stuIdLabel: Label,
    private val stuNameLabel: Label,
    private val dobLabel: Label,
    private val mobileLabel: Label,
    private val emailLabel: Label,
    private val nationalityLabel: Label,
    private val courseLabel: Label,
    private val enrolDateLabel: Label,
    private val graduateDateLabel: Label
    
) extends Behavior{
    var currentStudent: Student = null

    private def showStudentDetails (student: Student) = {
        stuIdLabel.text <== student.idNum
        stuNameLabel.text <== student.stuName
        mobileLabel.text <== student.mobile
        emailLabel.text <== student.email
        nationalityLabel.text <== student.nationality
        courseLabel.text <== student.course
        enrolDateLabel.text = student.enrolDate.value.inString
        dobLabel.text = student.dob.value.inString
        graduateDateLabel.text = student.graduateDate.value.inString

        //if there is at least one book in the borrowlist, show the list of book borrowed by this student
        if (currentStudent.borrowList.length > 0){
            bookBorrowedTable.items = currentStudent.borrowList 
            bookIdColumn.cellValueFactory = (x) => x.value.book.idNum 
            bookTitleColumn.cellValueFactory  = (x) => x.value.book.bookTitle
            authorColumn.cellValueFactory  = (x) => x.value.book.author
            borrowDateColumn.cellValueFactory  = (x) => ObjectProperty[LocalDate](x.value.borrowDate)
            dueDateColumn.cellValueFactory  = (x) => x.value.book.dueDate
        }
    }

    currentStudent = MainApp.currentSelected.asInstanceOf[Student]
    showStudentDetails(currentStudent) 

    def selectPersonBook (keyEvent: KeyEvent): Unit = {
        if (keyEvent.getCode() == KeyCode.ENTER){
            //load another person (to view)
            if (personIdButton.selected.apply())
               loadPerson(idTextField.text.value)
            
            //to borrow book (this student)
            else {
                val success = borrowBook(idTextField.text.value, currentStudent, true)
                if (success){
                    idTextField.text_=("")
                    showStudentDetails(currentStudent)
                }
            }
        }
    }
}
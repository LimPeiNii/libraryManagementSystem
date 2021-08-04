package library.view
import library.MainApp
import scalafx.scene.control.{RadioButton, TextField, TableView, TableColumn, Label}
import javafx.scene.input.{KeyEvent,KeyCode}
import java.time.LocalDate
import scalafxml.core.macros.sfxml
import library.model.{Staff, Record}
import library.util.Behavior
import library.util.DateUtil._
import scalafx.beans.property.ObjectProperty
import scalafx.Includes._

@sfxml
class StaffBookBorrowController(
    private val personIdButton: RadioButton,
    private val bookIdButton: RadioButton,
    private val idTextField: TextField,
    private val bookBorrowedTable: TableView[Record[Staff]],
    private val bookIdColumn: TableColumn[Record[Staff], String],
    private val bookTitleColumn: TableColumn[Record[Staff], String],
    private val authorColumn: TableColumn[Record[Staff], String],
    private val borrowDateColumn: TableColumn[Record[Staff], LocalDate],
    private val dueDateColumn: TableColumn[Record[Staff], LocalDate],
    private val staffIdLabel: Label,
    private val staffNameLabel: Label,
    private val dobLabel: Label,
    private val mobileLabel: Label,
    private val emailLabel: Label,
    private val nationalityLabel: Label,
    private val entryDateLabel: Label

) extends Behavior{
    var currentStaff: Staff = null
        
    private def showStaffDetails (staff: Staff) = {
        staffIdLabel.text <== staff.idNum
        staffNameLabel.text <== staff.staffName
        mobileLabel.text <== staff.mobile
        emailLabel.text <== staff.email
        nationalityLabel.text <== staff.nationality
        entryDateLabel.text = staff.entryDate.value.inString
        dobLabel.text = staff.dob.value.inString

        //if there is at least one book in the borrowlist, show the list of book borrowed by this staff
        if (currentStaff.borrowList.length > 0){
            bookBorrowedTable.items = currentStaff.borrowList 
            bookIdColumn.cellValueFactory = (x) => x.value.book.idNum 
            bookTitleColumn.cellValueFactory  = (x) => x.value.book.bookTitle
            authorColumn.cellValueFactory  = (x) => x.value.book.author
            borrowDateColumn.cellValueFactory  = (x) => ObjectProperty[LocalDate](x.value.borrowDate)
            dueDateColumn.cellValueFactory  = (x) => x.value.book.dueDate
        }        
    }

    currentStaff = MainApp.currentSelected.asInstanceOf[Staff]
    showStaffDetails(currentStaff) 

    def selectPersonBook (keyEvent: KeyEvent): Unit = {
        if (keyEvent.getCode() == KeyCode.ENTER){
            //load person (to view)
            if (personIdButton.selected.apply())
                loadPerson(idTextField.text.value)

            //to borrow book (this staff)
            else{
                val success = borrowBook (idTextField.text.value, currentStaff, false)
                if (success){
                    idTextField.text_=("")
                    showStaffDetails(currentStaff)
                }     
            }
        }
    }
}
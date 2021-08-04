package library.view
import scalafx.scene.control.{TableView, TableColumn}
import java.time.LocalDate
import scalafxml.core.macros.sfxml
import library.model.{Record, Student, Staff} 
import scalafx.beans.property.{ObjectProperty, StringProperty}
import library.MainApp

@sfxml
class BorrowListController(
    private val borrowRecordsTable: TableView[Record[Any]],
    private val personIDcolumn: TableColumn[Record[Any], String],
    private val bookIDcolumn: TableColumn[Record[Any], String],
    private val bookTitleColumn: TableColumn[Record[Any], String],
    private val borrowDateColumn: TableColumn[Record[Any], LocalDate],
    private val dueDateColumn: TableColumn[Record[Any], LocalDate],
    private val commentColumn: TableColumn[Record[Any], String]

){
    //show book borrow record list if the length of the list >= 1
    if (MainApp.recordList.length > 0){
        borrowRecordsTable.items = MainApp.recordList
        personIDcolumn.cellValueFactory  = (x) => (if (x.value.isStudent == true) (x.value.person.asInstanceOf[Student].idNum) else (x.value.person.asInstanceOf[Staff].idNum))
        bookIDcolumn.cellValueFactory = (x) => x.value.book.idNum
        bookTitleColumn.cellValueFactory  = (x) => x.value.book.bookTitle
        borrowDateColumn.cellValueFactory  = (x) => ObjectProperty[LocalDate](x.value.borrowDate)
        dueDateColumn.cellValueFactory  = (x) => ObjectProperty[LocalDate](x.value.dueDate)
        commentColumn.cellValueFactory  = (x) => (if (x.value.dueDate.isAfter(LocalDate.now)) (new StringProperty("")) else (new StringProperty("Overdue")))
    }
}
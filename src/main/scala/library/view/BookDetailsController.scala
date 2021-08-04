package library.view
import library.MainApp
import scalafx.scene.control.{RadioButton, TextField, Label}
import javafx.scene.input.{KeyEvent,KeyCode}
import scalafxml.core.macros.sfxml
import library.model.Book
import library.util.Behavior
import library.util.DateUtil._
import scalafx.Includes._

@sfxml
class BookDetailsController(
    private val personIdButton: RadioButton,
    private val bookIdButton: RadioButton,
    private val idTextField: TextField,
    private val bookIdLabel: Label,
    private val bookTitleLabel: Label,
    private val authorLabel: Label,
    private val languageLabel: Label,
    private val locationLabel: Label,
    private val publishedDateLabel: Label,
    private val entryDateLabel: Label,
    private val dueDateLabel: Label,
    private val statusLabel: Label

) extends Behavior{
    var currentBook: Book = null
    
    private def showBookDetails (book: Book) = {
        bookIdLabel.text <== book.idNum
        bookTitleLabel.text <== book.bookTitle
        authorLabel.text <== book.author
        languageLabel.text <== book.language
        locationLabel.text <== book.location
        statusLabel.text <== book.status
        entryDateLabel.text = book.entryDate.value.inString
        publishedDateLabel.text = book.publishedDate.value.inString
        dueDateLabel.text = book.dueDate.value.inString
        
    }

    currentBook = MainApp.currentSelected.asInstanceOf[Book]
    showBookDetails(currentBook) 

    //to decide load person details or load book details
    def selectPersonBook (keyEvent: KeyEvent): Unit = {
        if (keyEvent.getCode() == KeyCode.ENTER){
            if (personIdButton.selected.apply())
                loadPerson(idTextField.text.value)
            else
                loadBook(idTextField.text.value)                       
        }
    }
}
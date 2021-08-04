package library.view
import library.model.Book
import scalafx.scene.control.{TextField, ChoiceBox, Label, TextArea}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.Includes._
import java.time.LocalDate
import library.util.AlertWindowPop
import library.util.DateUtil._
import scalafx.collections.ObservableBuffer

@sfxml
class BookAddOrEditWindowController (
    private val bookIdTextField: TextField,
    private val bookTitleTextField: TextField,
    private val authorTextField: TextField,
    private val languageTextField: TextField,
    private val locationTextField: TextField,
    private val publishedDateTextField: TextField,
    private val entryDateTextField: TextField,
    private val statusChoiceBox: ChoiceBox[String],
    private val bookTitleLabel: Label,
    private val authorLabel: Label,
    private val languageLabel: Label,
    private val publishedDateLabel: Label,
    private val statusLabel: Label,
    private val checkedOutLabel: Label

) extends AlertWindowPop {
    var dialogStage: Stage  = null 
    private var _book: Book = null 
    var okClicked: Boolean = false 
    var originalStatus: String = ""
    var _caller: String = ""
    var _subBookList: ObservableBuffer[Book] = null
    var status = ObservableBuffer[String]("available", "unavailable","lost")
    statusChoiceBox.items_=(status)

    def book: Book = _book 
    def book_=(x : Book) {
            _book = x
            
            //update values in the text fields and do some settings on them
            bookIdTextField.text = _book.idNum.value
            bookTitleTextField.text = _book.bookTitle.value
            authorTextField.text = _book.author.value
            languageTextField.text = _book.language.value
            locationTextField.text = _book.location.value
            entryDateTextField.text = _book.entryDate.value.inString
            statusChoiceBox.value_=(_book.status.value)
            originalStatus = _book.status.value
            checkedOutLabel.visible_=(false)

            //the window act as book editing window
            if (_caller == "Edit Book"){
                publishedDateTextField.text = _book.publishedDate.value.inString
                val label = Array(bookTitleLabel, authorLabel, languageLabel, publishedDateLabel)
                val textfield = Array(bookTitleTextField, authorTextField, languageTextField, publishedDateTextField)
                for (i <- 0 until label.length){
                    label(i).disable_=(true)
                    textfield(i).disable_=(true)
                }
                if (_book.status.value == "checked out"){
                    statusLabel.disable_=(true)
                    statusChoiceBox.disable_=(true)
                    checkedOutLabel.visible_=(true)
                    checkedOutLabel.disable_=(true)
                }
            }

            //the window act as new book adding window            
            else if (_caller == "Add New Book")
                publishedDateTextField.text = "" 
            

    }

    def handleOk(){
        if (validateInput()) {
            //update this 2 properties (add book/edit book)
            _book.location.value = locationTextField.text()
            _book.status.value = statusChoiceBox.value.apply()

            //update properties (only for add book)
            if (_caller == "Add New Book"){
                _book.bookTitle.value = bookTitleTextField.text() 
                _book.author.value = authorTextField.text()
                _book.language.value = languageTextField.text() 
                _book.publishedDate.value = publishedDateTextField.text().parseLocalDate
                _book.entryDate.value = entryDateTextField.text().parseLocalDate
            }

            okClicked = true;
            dialogStage.close()
        }
    }

    def handleCancel() = dialogStage.close()

    def nullChecking (x : String) = x == null || x.length == 0

    //error checking for value from each text field (if is empty)
    def errorChecking (value: String, msg: String): String = {
        var errorMessage = ""        
        if (nullChecking(value))
            errorMessage += msg
        errorMessage
    }

    def validateInput() : Boolean = {
        var errorMessage = ""

        errorMessage += errorChecking(bookTitleTextField.text(), "No valid book title!\n")
        errorMessage += errorChecking(authorTextField.text(), "No valid author!\n")
        errorMessage += errorChecking(languageTextField.text(), "No valid language!\n")
        errorMessage += errorChecking(locationTextField.text(), "No valid location!\n")
        
        if (nullChecking(publishedDateTextField.text()))
            errorMessage += "No valid published date!\n"
        else if (!publishedDateTextField.text.value.isValid) //isValid is a function in DateUtil
            errorMessage += "No valid published date.\nUse the format DD/MM/YYYY!\n";

        //check for status only when the window act as new book adding window
        if (_caller == "Add New Book")
            errorMessage += errorChecking(statusChoiceBox.value.apply(), "No valid status!\n")

        if (errorMessage.length() == 0)
            return true; 
        else {
            popAlertErrorExpand("Invalid Fields", "Please correct invalid fields", new TextArea(errorMessage))
            return false;
        }
    }
} 

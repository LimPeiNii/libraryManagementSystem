package library.view
import library.model.Book
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.scene.control.Label
import scalafx.event.ActionEvent
import library.util.DateUtil._

@sfxml
class BookDeleteConfirmWindowController (
    private val bookIdLabel: Label,
    private val bookTitleLabel: Label,
    private val authorLabel: Label,
    private val languageLabel: Label,
    private val locationLabel: Label,
    private val publishedDateLabel: Label,
    private val entryDateLabel: Label,
    private val statusLabel: Label

){
  var dialogStage : Stage  = null 
  private var _book : Book = null
  var okClicked: Boolean = false

  def book: Book = _book
  def book_=(x : Book) = {
      _book = x

        //show details of book
        bookIdLabel.text <== _book.idNum
        bookTitleLabel.text <== _book.bookTitle
        authorLabel.text <== _book.author
        languageLabel.text <== _book.language
        locationLabel.text <== _book.location
        statusLabel.text <== _book.status
        entryDateLabel.text = _book.entryDate.value.inString
        publishedDateLabel.text = _book.publishedDate.value.inString

  }

  def handleOk(action :ActionEvent) = {
      okClicked = true;
      dialogStage.close()
  }

  def handleCancel(action :ActionEvent) = dialogStage.close()
} 

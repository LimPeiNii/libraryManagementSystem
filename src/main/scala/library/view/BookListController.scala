package library.view
import scalafx.scene.control.{TableView, TableColumn, ChoiceBox, TextField}
import scalafxml.core.macros.sfxml
import library.model.{Book, Horror, Romance, History}
import scalafx.collections.ObservableBuffer
import library.util.AlertWindowPop
import library.util.DateUtil._
import library.MainApp
import scalafx.Includes._ 
import java.time.LocalDate

@sfxml
class BookListController(
    private val bookTable: TableView[Book],
    private val bookIDcolumn: TableColumn[Book, String],
    private val bookTitleColumn: TableColumn[Book, String],
    private val authorColumn: TableColumn[Book, String],
    private val languageColumn: TableColumn[Book, String],
    private val locationColumn: TableColumn[Book, String],
    private val statusColumn: TableColumn[Book, String],
    private val genreChoiceBox: ChoiceBox[String],
    private val filterBookTextField: TextField

) extends AlertWindowPop{
    //add genres to choice box and set the default selected choice
    val genres = ObservableBuffer[String]("Book")
    for (b <- Book.genreDataList){
        genres += b._1
    }
    genreChoiceBox.items_=(genres)
    genreChoiceBox.value_=("Book")

    //show book list
    bookTable.items = MainApp.bookList
    private def showBookList() = {
        bookIDcolumn.cellValueFactory = (x) => x.value.idNum
        bookTitleColumn.cellValueFactory  = (x) => x.value.bookTitle
        authorColumn.cellValueFactory  = (x) => x.value.author
        languageColumn.cellValueFactory  = (x) => x.value.language
        locationColumn.cellValueFactory  = (x) => x.value.location
        statusColumn.cellValueFactory  = (x) => x.value.status
    }

    showBookList()

    //filter book list
    def filter(): Unit = {
        //filter according to genres
        if (genreChoiceBox.value.apply() != "Book"){
            val index = Book.genreDataList.indexWhere(tuple => tuple._1 == genreChoiceBox.value.apply())
            bookTable.items = Book.genreDataList.apply(index)._3
        }
        
        //when "Book" is selected, show entire book list
        else{
            bookTable.items = MainApp.bookList
        }

        //filter according to booktitle
        if (filterBookTextField.text.value != ""){
            val filteredBookList = new ObservableBuffer[Book]()
            for (b <- MainApp.bookList){
                if (b.bookTitle.value.toLowerCase() == filterBookTextField.text.value.toLowerCase()){
                    filteredBookList += b
                }
            }
            bookTable.items = filteredBookList
            //when book title is entered, genres will change back to Book 
            //because the filter is based on booktitle, not genre
            genreChoiceBox.value_=("Book")
        }
        showBookList()
        filterBookTextField.text.value = ""
    }

    def addNewBook() = {
        //assume only have 3 genre of books
        var book: Book = null
        var subBookList: ObservableBuffer[Book] = null
        val choice = genreChoiceBox.value.apply()
        var newBookID = ""
        
        //users need to select a genre before creating
        if (choice == "Book")
            popAlertWarning("Genre unselected", "Please select the genre at from the top left corner.", "The genre of the new book is not specified")
        else {
            if (choice == "Horror"){
                book = new Horror("", "", "", "", "", "", LocalDate.now.inString, "")
                subBookList = MainApp.horrorList 
            }
            else if (choice == "Romance"){
                book = new Romance("", "", "", "", "", "", LocalDate.now.inString, "")
                subBookList = MainApp.romanceList
            }
            else if (choice == "History"){
                book = new History("", "", "", "", "", "", LocalDate.now.inString, "")
                subBookList = MainApp.historyList
            }

            //to auto generate book id (assume amount will not exceed 10 thousand books for each genre)
            //if there is at least one book in the list
            if (subBookList.length > 0){
                //generate number part
                val currentIDNum = subBookList.apply(subBookList.length-1).idNum.value.substring(2,8).toInt
                var newIDNumInString = (currentIDNum + 1).toString
                while (newIDNumInString.length < 6)
                    newIDNumInString = "0" + newIDNumInString
                //add alphabets for genres
                newBookID = book.idFrontPart + newIDNumInString + book.idLastPart
                book.idNum.value = newBookID
            }
            else{
                newBookID = book.idFrontPart + "000001" + book.idLastPart
                book.idNum.value = newBookID
            }
            
            //update list and database
            if (MainApp.loadBookAddOrEditWindow(book, "Add New Book", subBookList)){
                if (subBookList.length > 0){
                    //since book list mix all type of book, it find the last index of a certain genre and add after it
                    val lastBook = subBookList.apply(subBookList.length - 1)
                    val indexForNewBook = (MainApp.bookList.indexOf(lastBook)) + 1
                    MainApp.bookList.insert(indexForNewBook, book)
                }
                //when book list is empty, directly add into the list
                else
                    MainApp.bookList += book
                subBookList += book
                book.saveBook()
                showBookList()
            }
        }
    }

    def editBook() = {
        val selectedBook = bookTable.selectionModel().getSelectedItem()
        if (selectedBook != null){ 
            if (MainApp.loadBookAddOrEditWindow(selectedBook, "Edit Book", null)){
                selectedBook.saveBook()
                showBookList()
            }
        }
        else 
            popAlertWarning("No Selection", "Please select a book from the table.", "No book is selected")
    }

    def deleteBook() = {
        val book = bookTable.selectionModel().getSelectedItem()
        if (book != null){
            //only unchecked-out book can be deleted
            if (book.status.value != "checked out"){
                if (MainApp.confirmDeleteBook(book)){
                    val book = bookTable.selectionModel().getSelectedItem()
                    val index = Book.genreDataList.indexWhere(tuple => tuple._2 == book.idLastPart)
                    book.deleteBook()    
                    Book.genreDataList.apply(index)._3 -= book           
                    MainApp.bookList -= book
                    showBookList()
                }
            }                
            else 
                popAlertWarning("Checked Out book", "Please double check.", "This book is borrowed by someone.")
        }
        else 
            popAlertWarning("No Selection", "Please select a book from the table.", "No book is selected")
    }
}
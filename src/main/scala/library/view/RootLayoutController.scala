package library.view
import library.MainApp
import scalafxml.core.macros.sfxml
import library.util.AlertWindowPop
import scalafx.scene.control.TextArea

@sfxml
class RootLayoutController () extends AlertWindowPop{
    def borrowBook(): Unit = MainApp.loadPages("view/PersonBookSelection.fxml")

    def returnBook(): Unit = MainApp.loadPages("view/BookReturn.fxml")

    def updateDailyRecord(): Unit = MainApp.showUpdateDailyRecordPage()

    //add new book into database (jump to book list page first)
    def addBook(): Unit = MainApp.loadPages("view/BookList.fxml")

    //edit a book information (jump to book list page first)
    def editBook(): Unit = MainApp.loadPages("view/BookList.fxml")

    //delete a book from database (jump to book list page first)
    def deleteBook(): Unit = MainApp.loadPages("view/BookList.fxml")

    def viewBookList(): Unit = MainApp.loadPages("view/BookList.fxml")

    def viewBorrowList(): Unit = MainApp.loadPages("view/BorrowList.fxml")

    def viewDailyRecord(): Unit = MainApp.loadPages("view/DailyRecord.fxml")
    
    def logOut(): Unit = MainApp.loadLoginPage()   

    def changePassword(): Unit = MainApp.loadCreateOrEditAccountPage("Edit Account")   

    //pop an information type alert message that can guide users who are not familiar to the system
    def viewGuidance(): Unit = {
        var guidanceContent: String = "1. View student or staff details:\nFile > Borrow Book > Select Person ID > Enter a staff or student ID > Press Enter\nFor testing purpose, you can try student ID (s18081111 or s19062222) or staff ID (S09011111 or L11012222)\n\n"
        guidanceContent += "2. View book details:\nFile > Borrow Book > Select Book ID > Enter a book ID > Press Enter\nFor testing purpose, you can refer book ID in the book list under 'View' of the menu bar\n\n"
        guidanceContent += "3. Borrow a book:\nView the person details > Select Book ID > Enter a book ID > Press Enter\n\n"
        guidanceContent += "4. Return a book:\nFile > Return Book > Enter a book ID > Press Enter\n\n"
        guidanceContent += "5. View book list:\nView > Book List\n\n"
        guidanceContent += "6. Filter book list\nWay 1: Enter a book title > Filter\nWay 2: Select a genre at the top left corner > Filter\n*To show the entire list: Select 'Book' in the genre list > Filter\n\n"
        guidanceContent += "7. View borrow list:\nView > Borrow List\n\n"
        guidanceContent += "8. Add new book:\nEdit > Book List > Add New Book > Select a genre a the top left corner > New\n*You can also load the book list to add a new book\n\n"
        guidanceContent += "9. Edit a book:\nEdit > Book List > Edit Book Details > Select a book > Edit\n*You can also load the book list to edit a book\n\n"
        guidanceContent += "10. Delete a book:\nEdit > Book List > Delete Book > Select a book > Delete\n*You can also load the book list to edit a book\n\n"
        guidanceContent += "11. View daily record list:\nView > Daily Record List\n\n"
        guidanceContent += "12. Filter daily record list: Enter a date > Filter\n*To show the entire list: Leave the text field empty > Filter\n\n"
        guidanceContent += "13. Update daily record:\nFile > Update Daily Record\n\n"
        guidanceContent += "14. Change account password:\nAccount > Change Password\n\n"
        guidanceContent += "15. Logout:\nAccount > Log Out"
        popAlertInfomationExpand("Guidance", "If you are unfamiliar to this system, you will need me!", new TextArea(guidanceContent))
    }

    def viewAbout(): Unit = popAlertInfomation("About", "Sunday University Library Management System", "Copyrighted by Lim Pei Ni (19066273).")
}
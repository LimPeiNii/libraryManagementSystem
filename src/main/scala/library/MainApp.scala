package library
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.Includes._
import scalafxml.core.{NoDependencyResolver, FXMLLoader}
import javafx.{scene => jfxs}
import library.util.Database
import java.time.LocalDate
import scalafx.collections.ObservableBuffer
import library.model.{DailyRecord, Book, History, Romance, Horror, Record} 
import library.model.{Student, Staff, Account}
import scala.util.control.Breaks._
import scalafx.stage.{Stage, Modality}
import library.view.{BookDeleteConfirmWindowController, BookAddOrEditWindowController}
import library.view.{DailyRecordController, UpdatingDailyRecordWindowController}
import library.view.{LoginPageController, AccountCreateOrEditWindowController}

object MainApp extends JFXApp {
    //setup database
    Database.setupDB()

    //to store account objects (from database)
    val account = new ObservableBuffer[Account]()
    account ++= Account.getAllAccount

    //assign all book into book list
    val horrorList = new ObservableBuffer[Book]()
    horrorList ++= Horror.getAllBook
    val romanceList =  new ObservableBuffer[Book]()
    romanceList ++= Romance.getAllBook
    val historyList =  new ObservableBuffer[Book]()
    historyList ++= History.getAllBook
    val bookList = new ObservableBuffer[Book]()  
    bookList ++= horrorList
    bookList ++= romanceList
    bookList ++= historyList

    //to store student objects (from database)
    val studentList = new ObservableBuffer[Student]()
    studentList ++= Student.getAllStudent

    //to store staff objects (from database)
    val staffList = new ObservableBuffer[Staff]()
    staffList ++= Staff.getAllStaff

    //to store record objects (from database)
    val recordList = new ObservableBuffer[Record[Any]]()
    recordList ++= Record.getAllRecord

    //to store daily record objects (from database)
    val dailyRecords = new ObservableBuffer[DailyRecord]()
    dailyRecords ++= DailyRecord.getAllDailyRecord

    //generate a today record once the system start to store fees for that day
    //if today record is null, means that the record table in database doesn't have today record
    if (DailyRecord.todayRecord == null){
        DailyRecord.todayRecord = new DailyRecord(LocalDate.now)
        dailyRecords += DailyRecord.todayRecord
        DailyRecord.todayRecord.saveDailyRecord()
    }


    //after storing all data (extracted from database) into list:
    //primary stage
    val rootResource = getClass.getResourceAsStream("view/RootLayout.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(rootResource);
    val root1: jfxs.layout.BorderPane = loader.getRoot[jfxs.layout.BorderPane]

    stage = new PrimaryStage {
        title = "University Library Management System"
        scene = new Scene {
          root = root1
          stylesheets += getClass.getResource("view/cssTheme.css").toString()
        }
        icons += new Image(getClass.getResourceAsStream("view/books.png")) 
    }
    
    //for loading pages
    def loadPages(fileName: String) = {
        val resource = getClass.getResourceAsStream(fileName)
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots= loader.getRoot[jfxs.layout.AnchorPane]()
        this.root1.setCenter(roots)
    }

    //for loading pages that can view student/staff/book details
    var currentSelected: Any = null
    def loadWithSelectedObject(fileName: String, enteredObj: Any) = {
        currentSelected = enteredObj
        loadPages(fileName)
    }

    //pop up new window for delete confirmation
    def confirmDeleteBook(book: Book): Boolean = { 
        val resource = getClass.getResourceAsStream("view/BookDeleteConfirmWindow.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots2  = loader.getRoot[jfxs.Parent] 
        val control = loader.getController[BookDeleteConfirmWindowController#Controller]

        val window = new Stage() { 
            initModality(Modality.ApplicationModal) 
            initOwner(stage) 
            scene = new Scene {
              root = roots2 
              stylesheets += getClass.getResource("view/cssTheme.css").toString()          
            }
            title = "Delete Confirmation"
            resizable_=(false)
            icons += new Image(getClass.getResourceAsStream("view/books.png")) 
        }
        control.dialogStage = window
        control.book = book
        window.showAndWait() 
        control.okClicked
    }

    //pop up new window for adding new book or editing book
    def loadBookAddOrEditWindow(book: Book, caller: String, subBookList: ObservableBuffer[Book]): Boolean = {
        val resource = getClass.getResourceAsStream("view/BookAddOrEditWindow.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots2  = loader.getRoot[jfxs.Parent] 
        val control = loader.getController[BookAddOrEditWindowController#Controller]

        val window = new Stage() { 
            initModality(Modality.ApplicationModal) 
            initOwner(stage) 
            scene = new Scene {
              root = roots2 
              stylesheets += getClass.getResource("view/cssTheme.css").toString()          
            }
            title = caller
            resizable_=(false)
            icons += new Image(getClass.getResourceAsStream("view/books.png")) 
        }
        control.dialogStage = window
        control._caller = caller   
        control._subBookList = subBookList
        control.book = book  
        window.showAndWait() 
        control.okClicked
    } 

    //pop up new window for updating daily record
    def showUpdateDailyRecordPage() = {
        val resource = getClass.getResourceAsStream("view/UpdatingDailyRecordWindow.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots2  = loader.getRoot[jfxs.Parent] 
        val control = loader.getController[UpdatingDailyRecordWindowController#Controller]

        val window = new Stage() { 
            initModality(Modality.ApplicationModal) 
            initOwner(stage) 
            scene = new Scene {
              root = roots2 
              stylesheets += getClass.getResource("view/cssTheme.css").toString()          
            }
            title = "Update Daily Record"
            resizable_=(false)
            icons += new Image(getClass.getResourceAsStream("view/books.png")) 
        }
        control.dialogStage = window
        window.showAndWait()
        if (control.okClicked)
          loadPages("view/DailyRecord.fxml")
    }
    
    def loadLoginPage() = {
        val resource = getClass.getResourceAsStream("view/LoginPage.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots = loader.getRoot[jfxs.layout.BorderPane]()
        val control = loader.getController[LoginPageController#Controller]
        stage.resizable_=(false)
        this.root1.top.value.visible_=(false)
        this.root1.setCenter(roots)
        control.stage = stage
    }

    //pop up new window for creating or editing account 
    def loadCreateOrEditAccountPage(_title: String): Boolean = { 
        val resource = getClass.getResourceAsStream("view/AccountCreateOrEditWindow.fxml")
        val loader = new FXMLLoader(null, NoDependencyResolver)
        loader.load(resource);
        val roots2  = loader.getRoot[jfxs.Parent] 
        val control = loader.getController[AccountCreateOrEditWindowController#Controller]

        val window = new Stage() { 
            initModality(Modality.ApplicationModal) 
            initOwner(stage) 
            scene = new Scene {
              root = roots2 
              stylesheets += getClass.getResource("view/cssTheme.css").toString()          
            }
            title = _title
            resizable_=(false)
            icons += new Image(getClass.getResourceAsStream("view/books.png")) 
        }
        control.dialogStage = window
        window.showAndWait()
        control.accCreated
    }  

    // call to display login page when app start
    loadLoginPage()
}
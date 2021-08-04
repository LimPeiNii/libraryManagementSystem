package library.view
import scalafxml.core.macros.sfxml
import scalafx.scene.control.{TextField, Label}
import scalafx.scene.text.Text
import library.MainApp
import library.util.AlertWindowPop
import scalafx.application.JFXApp.PrimaryStage
import scalafx.Includes._
import scalafx.scene.image.{ImageView, Image}

@sfxml
class LoginPageController(
    private val usernameTextField: TextField,
    private val passwordTextField: TextField,
    private val newAccountLabel: Label,
    private val uniName: Text,
    private val imgView: ImageView

) extends AlertWindowPop {
    var stage: PrimaryStage = null
    
    //put university name and image
    uniName.text_=("Sunday University")
    val img = new Image(getClass().getResourceAsStream("University.png"),500,200,false,false)
    imgView.image_=(img)

    /*if the database already have an account, then set the label unvisible
    so that users cannot create account because only can have one account for 
    the system*/
    if (MainApp.account.length > 0)
        newAccountLabel.visible_=(false)
            
    def login() = {
        //check if there is any error/empty text field
        var errorMsg = ""
        if (usernameTextField.text().length == 0 || usernameTextField.text() == null)
                errorMsg += "No valid username\n"
        if (passwordTextField.text().length == 0 || passwordTextField.text() == null)
                errorMsg += "No valid password"
        if (errorMsg.length() != 0)
            popAlertError("Invalid Fields", "Please correct invalid fields", errorMsg)  
        else{
            if (MainApp.account.length == 1){
                val account = MainApp.account.apply(0)
                //check if username and password correct
                if (account.username == usernameTextField.text.value && account.password == passwordTextField.text.value){
                        //set menubar visible
                        MainApp.root1.top.value.setVisible(true)
                        MainApp.stage.resizable_=(true)
                        MainApp.loadPages("view/PersonBookSelection.fxml")
                }
                else
                    popAlertWarning("Login Fail", "Fail to login", "Incorrect username or password.")
            }        
            else
                popAlertWarning("Login Fail", "Fail to login", "This account doesn't exist.")
        }
    }

    def cancel() = stage.close()

    def createAccount() = {
        //inform user that only one account can be created
        if (MainApp.account.length == 0)
            popAlertInfomation("Alert", "Account creation alert", "This system only allows one account to be created.\nPlease remember your account username and password.")
        
        val accCreated = MainApp.loadCreateOrEditAccountPage("Create Account")

        //after creating an account, set the label unvisible (system only can have 1 account)
        if (accCreated)
            newAccountLabel.visible_=(false)
    }
}
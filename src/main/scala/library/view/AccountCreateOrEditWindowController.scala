package library.view
import scalafxml.core.macros.sfxml
import scalafx.scene.control.TextField
import library.MainApp
import library.model.Account
import scalafx.stage.Stage
import library.util.AlertWindowPop

@sfxml
class AccountCreateOrEditWindowController(
    private val usernameTextField: TextField,
    private val passwordTextField: TextField

)extends AlertWindowPop{
    var dialogStage: Stage = null
    var accCreated: Boolean = false

    if (MainApp.account.length > 0){
        usernameTextField.text = MainApp.account.apply(0).username
        usernameTextField.disable_=(true)
        passwordTextField.text = MainApp.account.apply(0).password
    }

    def done() = {
        var errorMsg = ""
        val acc = new Account(usernameTextField.text.value, passwordTextField.text.value)
        if (usernameTextField.text().length == 0 || usernameTextField.text() == null)
                errorMsg += "No valid username\n"
        if (passwordTextField.text().length == 0 || passwordTextField.text() == null)
                errorMsg += "No valid password"
        if (errorMsg.length() != 0)
            popAlertError("Invalid Fields", "Please correct invalid fields", errorMsg) 
        
        //this window will act as create account window
        else if (MainApp.account.length == 0){
            MainApp.account += acc
            acc.saveAccount()
            accCreated = true
            dialogStage.close()
        }
        
        //this window will act as edit account (change password) window        
        else{
            MainApp.account.apply(0).password = passwordTextField.text()
            MainApp.account.apply(0).saveAccount()
            dialogStage.close()
        }
    }

    def cancel() = dialogStage.close()
}
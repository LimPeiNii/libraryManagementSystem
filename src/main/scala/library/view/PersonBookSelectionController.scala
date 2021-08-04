package library.view
import scalafx.scene.control.{RadioButton, TextField}
import javafx.scene.input.{KeyEvent,KeyCode}
import scalafxml.core.macros.sfxml
import library.util.Behavior
import scalafx.Includes._

@sfxml
class PersonBookSelectionController(
    private val personIdButton: RadioButton,
    private val bookIdButton: RadioButton,
    private val idTextField: TextField

) extends Behavior{
    //to decide load person details or load book details
    def selectPersonBook (keyEvent: KeyEvent): Unit = {
        if (keyEvent.getCode() == KeyCode.ENTER){
            //load a person (to view)
            if (personIdButton.selected.apply())
                loadPerson(idTextField.text.value)

            //load a book (to view)
            else
                loadBook(idTextField.text.value)                       
        }
    }
}
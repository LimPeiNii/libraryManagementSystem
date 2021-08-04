package library.view
import library.model.DailyRecord
import library.MainApp
import scalafx.scene.control.{TextField, TextArea}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.Includes._
import java.time.LocalDate
import library.util.AlertWindowPop
import library.util.DateUtil._

@sfxml
class UpdatingDailyRecordWindowController (
    private val dateTextField: TextField,
    private val blacknWhiteTextField: TextField,
    private val colourTextField: TextField,
    private val icTextField: TextField,
    private val bNwFalseTextField: TextField,
    private val colourFalseTextField: TextField,
    private val icFalseTextField: TextField,    
    private val feeReceivedTextField: TextField,
    private val overdueFeeReceivedTextField: TextField

) extends AlertWindowPop {
    var dialogStage: Stage  = null 
    var okClicked: Boolean = false
    
    dateTextField.text = LocalDate.now.inString

    def calculateExpectedPrintingFee(): Double = {
        val bNwTotal = (blacknWhiteTextField.text.value.toInt)*0.10
        val colorTotal = (colourTextField.text.value.toInt)*0.20
        val icTotal = (icTextField.text.value.toInt)*0.50
        val bNwFalse = (bNwFalseTextField.text.value.toInt)*0.10
        val colorFalse = (colourFalseTextField.text.value.toInt)*0.20
        val icFalse = (icFalseTextField.text.value.toInt)*0.50
        val expectedFee = f"${bNwTotal + colorTotal + icTotal - bNwFalse - colorFalse - icFalse}%.2f".toDouble
        expectedFee
    }

    def handleOk(){
        if (validateInput()) {
            val expectedPrintingFee = calculateExpectedPrintingFee()
            DailyRecord.todayRecord.printingFee.value = f"${feeReceivedTextField.text.value.toDouble}%.2f".toDouble
            DailyRecord.todayRecord.expectedPrintingFee.value = expectedPrintingFee
            DailyRecord.todayRecord.overdueFee.value = f"${overdueFeeReceivedTextField.text.value.toDouble}%.2f".toDouble
            DailyRecord.todayRecord.saveDailyRecord()
            okClicked = true
            dialogStage.close()
        }
    }

    def handleCancel() = dialogStage.close()

    def nullChecking (x : String) = x == null || x.length == 0

    //error checking for value from each text field (integer)
    def errorChecking(fieldValue: String, msg1: String, msg2: String): String = {
        var errorMessage = ""        
        if (nullChecking(fieldValue))
            errorMessage += msg1
        else{
            try 
                fieldValue.toInt 
            catch{
                case e : NumberFormatException => errorMessage += msg2
            }
        }
        errorMessage
    }

    //check error for each input
    def validateInput() : Boolean = {
        var errorMessage = ""

        errorMessage += errorChecking(blacknWhiteTextField.text(), "No valid B&W number!\n", "No valid B&W number (must be an integer)!\n")
        errorMessage += errorChecking(colourTextField.text(), "No valid colour number!\n", "No valid colour number (must be an integer)!\n")
        errorMessage += errorChecking(icTextField.text(), "No valid I/C number!\n", "No valid ic number (must be an integer)!\n")
        errorMessage += errorChecking(bNwFalseTextField.text(), "No valid false B&W number!\n", "No valid false B&W number (must be an integer)!\n")
        errorMessage += errorChecking(colourFalseTextField.text(), "No valid false colour number!\n", "No valid false colour number (must be an integer)!\n")
        errorMessage += errorChecking(icFalseTextField.text(), "No valid false I/C number!\n", "No valid false ic number (must be an integer)!\n")
        
        if (nullChecking(feeReceivedTextField.text()))
            errorMessage += "No valid printing fee received number!\n"
        else{
            try 
                feeReceivedTextField.text().toDouble
            catch{
                case e : NumberFormatException => 
                errorMessage += "No valid printing fee received number (must be a number)!\n"
            }
        }
        
        if (nullChecking(overdueFeeReceivedTextField.text()))
            errorMessage += "No valid overdue fee received number!\n"
        else{
            try 
                overdueFeeReceivedTextField.text().toDouble
            catch{
                case e : NumberFormatException => 
                errorMessage += "No valid overdue fee received number (must be a number)!\n"
            }
        }

        if (errorMessage.length() == 0)
            return true
        else {
            popAlertErrorExpand("Invalid Fields", "Please correct the invalid fields", new TextArea(errorMessage))
            return false
        }
    }
} 

package library.view
import scalafx.scene.control.{TableView, TableColumn, TextField}
import scalafxml.core.macros.sfxml
import library.model.DailyRecord
import scalafx.collections.ObservableBuffer
import library.MainApp
import library.util.AlertWindowPop
import library.util.DateUtil._

@sfxml
class DailyRecordController(
    private val recordTable: TableView[DailyRecord],
    private val datecolumn: TableColumn[DailyRecord, String],
    private val overdueFeeColumn: TableColumn[DailyRecord, Double],
    private val overdueFeeReceivedColumn: TableColumn[DailyRecord, Double],
    private val overdueFeeLossColumn: TableColumn[DailyRecord, Double],
    private val printFeeColumn: TableColumn[DailyRecord, Double],
    private val printFeeExpectedColumn: TableColumn[DailyRecord, Double],
    private val lossColumn: TableColumn[DailyRecord, Double],
    private val totalColumn: TableColumn[DailyRecord, Double],
    private val filterDateTextField: TextField

)extends AlertWindowPop{
    recordTable.items = MainApp.dailyRecords
    
    def showDailyRecordList() = {
        datecolumn.cellValueFactory = (x) => x.value.dateWrapped
        overdueFeeColumn.cellValueFactory  = (x) => x.value.expectedOverdueFee
        overdueFeeReceivedColumn.cellValueFactory  = (x) => x.value.overdueFee
        overdueFeeLossColumn.cellValueFactory  = (x) => x.value.overdueFeeLoss
        printFeeColumn.cellValueFactory  = (x) => x.value.printingFee
        printFeeExpectedColumn.cellValueFactory  = (x) => x.value.expectedPrintingFee
        lossColumn.cellValueFactory  = (x) => x.value.printingFeeLoss
        totalColumn.cellValueFactory  = (x) => x.value.totalReceived
    }

    showDailyRecordList()

    def filter(): Unit = {
        //filter out a record using a date 
        if (filterDateTextField.text.value != ""){
            //if date format is valid
            if (filterDateTextField.text.value.isValid){
                val filteredDate = new ObservableBuffer[DailyRecord]()
                for (r <- MainApp.dailyRecords){
                    if (r.dateWrapped.value == filterDateTextField.text.value)
                        filteredDate += r
                }
                recordTable.items = filteredDate
                filterDateTextField.text.value = ""        
            }
            else
                popAlertError("Invalid Date Format", "Please double check the date format.", "The valid date format is DD/MM/YYYY\ne.g. 01/01/2001")
        }
        else
            //if the text field is empty, load all records
            recordTable.items = MainApp.dailyRecords
        showDailyRecordList()
    }
}
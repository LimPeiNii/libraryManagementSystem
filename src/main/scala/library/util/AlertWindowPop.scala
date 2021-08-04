package library.util
import library.MainApp
import scalafx.scene.control.{Alert, TextArea}
import scalafx.Includes._

trait AlertWindowPop{
    def popAlertWarning (_title: String, _header: String, _context: String): Unit = {
        new Alert(Alert.AlertType.Warning){ 
            initOwner(MainApp.stage)
            title       = _title
            headerText  = _header
            contentText = _context
        }.showAndWait()
    }

    def popAlertError (_title: String, _header: String, _context: String): Unit = {
        new Alert(Alert.AlertType.Error){ 
            initOwner(MainApp.stage)
            title       = _title
            headerText  = _header
            contentText = _context
        }.showAndWait()
    }

    def popAlertErrorExpand (_title: String, _header: String, _content: TextArea): Unit = {
        _content.editable_=(false)
        new Alert(Alert.AlertType.Error){ 
            initOwner(MainApp.stage)
            title       = _title
            headerText  = _header
            dialogPane.value.setExpandableContent(_content);
            dialogPane.value.setExpanded(true);
        }.showAndWait()
    }

    def popAlertInfomation (_title: String, _header: String, _context: String): Unit = {
        new Alert(Alert.AlertType.Information){
            initOwner(MainApp.stage)
            title       = _title
            headerText  = _header
            contentText = _context
        }.showAndWait()
    }

    def popAlertInfomationExpand (_title: String, _header: String, _content: TextArea): Unit = {
        _content.editable_=(false)
        new Alert(Alert.AlertType.Information){
            initOwner(MainApp.stage)
            title       = _title
            headerText  = _header
            dialogPane.value.setExpandableContent(_content);
            dialogPane.value.setExpanded(true);
        }.showAndWait()
    }
}
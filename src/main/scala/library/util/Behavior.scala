package library.util
import library.MainApp
import library.model.{Book, Record, Student, Staff}

trait Behavior extends AlertWindowPop{
    def loadPerson (idInput: String): Unit = {
        var alert: Boolean = false        
        var index = -1
        //check the entered ID format: for student
        if (idInput.charAt(0 )=='s'){
            val tempStuList = MainApp.studentList.toList
            index = tempStuList.indexWhere(student => student.idNum.value == idInput)
            //student not found            
            if (index == -1)
                alert = true 
            else
                MainApp.loadWithSelectedObject("view/StudentBookBorrow.fxml",MainApp.studentList.apply(index))
        }

        //check the entered ID format: for staff
        else if (idInput.charAt(0) =='S'|| idInput.charAt(0) == 'L'){
            val tempStaffList = MainApp.staffList.toList
            index = tempStaffList.indexWhere(staff => staff.idNum.value == idInput)
            //staff not found
            if (index == -1)
                alert = true 
            else
                MainApp.loadWithSelectedObject("view/StaffBookBorrow.fxml",MainApp.staffList.apply(index))
        }
        
        else
            // invalid ID 
            popAlertError("Invalid Person ID", "Please double check the entered ID.", "Person ID (" + idInput + ") format invalid")
        
        if (alert == true)
            // this ID doesnt exist
            popAlertWarning("Non-exist Person ID", "Please double check the entered ID.", "Person ID (" + idInput + ") doesnt exist")
    }

    def loadBook(idInput: String): Unit = {
        var alert: Boolean = false
        var index = -1
        //check the entered ID format: for book
        var indexLastCharCheck = Book.genreDataList.indexWhere(tuple => tuple._2 == idInput.takeRight(1))
        if ((idInput.take(2)=="BF" || idInput.take(2)=="BN") && idInput.length == 9 && indexLastCharCheck != -1){
            val tempBookList = MainApp.bookList.toList
            index = tempBookList.indexWhere(book => book.idNum.value == idInput)
            //book not found            
            if (index == -1)
                alert = true 
            else
                //jump to the page that shows book details
                MainApp.loadWithSelectedObject("view/BookDetails.fxml",MainApp.bookList.apply(index))                        
        }

        else
            // invalid ID 
            popAlertError("Invalid Book ID", "Please double check the entered ID.", "Book ID (" + idInput + ") format invalid")
        
        if (alert == true)
            // this ID doesnt exist
            popAlertWarning("Non-exist Book ID", "Please double check the entered ID.", "Book ID (" + idInput + ") doesnt exist")
    }

    def borrowBook (idInput: String, currentPerson: Any, isStudent: Boolean): Boolean = {
        var alert: Boolean = false 
        var unavailable = false
        var index = -1
        //check the entered ID format of book
        var indexLastCharCheck = Book.genreDataList.indexWhere(tuple => tuple._2 == idInput.takeRight(1))
        if ((idInput.take(2)=="BF" || idInput.take(2)=="BN") && idInput.length == 9 && indexLastCharCheck != -1){
            val tempBookList = MainApp.bookList.toList
            index = tempBookList.indexWhere(book => book.idNum.value == idInput)
            //book not found
            if (index == -1)
                alert = true 
            else{
                val book = tempBookList.apply(index)
                //book is available to be borrowed
                if (book.status.value == "available"){
                    //update id counter to get new record id
                    if (MainApp.recordList.length > 0)
                        Record.id = MainApp.recordList.apply(MainApp.recordList.length-1).recordID + 1
                    else
                        Record.id = 1

                    //borrower is student
                    if (isStudent){
                        var record = new Record[Student](currentPerson.asInstanceOf[Student], book)
                        currentPerson.asInstanceOf[Student].borrowList += record
                        MainApp.recordList += record
                        //update record in database
                        record.saveRecord()
                        book.dueDate.value = record.dueDate  
                    }

                    //borrower is staff
                    else{
                        var record = new Record[Staff](currentPerson.asInstanceOf[Staff], book)
                        currentPerson.asInstanceOf[Staff].borrowList += record
                        MainApp.recordList += record   
                        //update record in database
                        record.saveRecord()                                                               
                        book.dueDate.value = record.dueDate
                    }                      
                    book.status.value = "checked out"
                    //update book in database
                    book.saveBook()
                    return true
                }
                //book is not available at that moment
                else
                    unavailable = true
            }
        }
        else
            // invalid ID 
            popAlertError("Invalid Book ID", "Please double check the entered ID.", "Book ID (" + idInput + ") format invalid")
        if (alert == true)
            // this ID doesnt exist
            popAlertWarning("Non-exist Book ID", "Please double check the entered ID.", "Book ID (" + idInput + ") doesnt exist")
        if (unavailable == true)
            //book is borrowed by someone else etc.
            popAlertWarning("Book is unavailable", "Please double check.", "This book has already been borrowed\nor\nThis book is unavailable right now")
        false
    }
}
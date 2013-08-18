package models
import org.apache.poi.hssf.usermodel._
import scala._
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Cell
import scala.collection.mutable.HashMap
import java.io.FileOutputStream
import java.io.File
import java.io.FileInputStream
import org.apache.poi.hssf.usermodel._
import scala._
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Cell
import scala.collection.mutable.HashMap
import java.io.FileOutputStream
import java.io.File
import scala.io.Source._
import play.api.libs.json._
import sun.security.pkcs11.KeyCache

class Json2Excel {
  var user: String = null
  var templateUploaded: Boolean = false
  var files: List[String] = null

  def updateUserinfo(userName: String) = {
    user = userName

    val dirPath = "files/" + user
    val dir = new File(dirPath)

    if (!dir.exists) { dir.mkdir }
    else {
      val file = new File(dirPath + "/template.xls")
      if (file exists) {
        templateUploaded = true
      }
      files = dir.list.toList
    }
  }

  def resetUserInfo = {
    user = null
    templateUploaded = false
    files = null
  }

  def populate(jsonPath: String): String = {
    val excelFileName = jsonPath + ".xls"
    var result: String = ""
    "files/" + user + "/template.xls"
    var workbook = new HSSFWorkbook(new FileInputStream(new File("files/" + user + "/template.xls")))
    var worksheet = workbook.getSheetAt(0)

    val jsonString = fromFile(jsonPath).getLines.mkString
    val json: JsValue = Json.parse(jsonString)
    val recordList = (json.validate[JsArray]).get.value
    var lastRowNum = worksheet.getLastRowNum
    result += lastRowNum

    for (record <- recordList) {

      var newRow = worksheet.createRow(lastRowNum);
      lastRowNum += 1

      var keyCells = worksheet.getRow(0).cellIterator()
      while (keyCells.hasNext()) {
        var keyCell = keyCells.next
        var column = keyCell.getColumnIndex

        var key: String = keyCell.getRichStringCellValue.toString

        //if key available in record
        var currentValue = record \ key
        if (currentValue != null) {
          var cell = newRow.createCell(column)
          currentValue match {
            case _: JsBoolean => cell.setCellValue(currentValue.validate[Boolean].get)
            case _: JsNumber => cell.setCellValue(currentValue.validate[Double].get)
            case _: JsString => cell.setCellValue(currentValue.validate[String].get)
            case _ =>
          }
        }
      }
    }

    workbook.write(new FileOutputStream(new File(excelFileName)))
    "Generated Successfully\n\n" + Json.prettyPrint(json) + "\n\n\n" + result
  }

  //  def generatedExcel = new ExcelGenerator(excelTemplate, jsonInput)

}

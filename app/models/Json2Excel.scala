package models

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

import scala.io.Source.fromFile

import org.apache.poi.hssf.usermodel.HSSFWorkbook

import play.api.libs.json.JsArray
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNumber
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Json

class Json2Excel {
  var user: String = null
  var templateUploaded: Boolean = false
  var files: List[String] = null

  def populate(tempId: String, jsonId: String, xlsId: String): Boolean = {
    val excelFileName = "files/" + xlsId + ".xls"
    val templateFileName = "files/" + tempId + ".xls"
    val jsonFileName = "files/" + jsonId + ".json"

    var workbook = new HSSFWorkbook(new FileInputStream(new File(templateFileName)))
    var worksheet = workbook.getSheetAt(0)

    val jsonString = fromFile(jsonFileName).getLines.mkString
    val json: JsValue = Json.parse(jsonString)
    val recordList = (json.validate[JsArray]).get.value
    var lastRowNum = 1 //worksheet.getLastRowNum

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
    true;
  }

}

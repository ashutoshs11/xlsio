package controllers

import java.io.File

import scala.util.Random

import models.Json2Excel
import play.api.libs.iteratee.Enumerator
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.ResponseHeader
import play.api.mvc.SimpleResult

object Application extends Controller {
  var json2Excel = new Json2Excel;
  def genRandomId = Random.alphanumeric.take(10).mkString

  /*
  def index = Action {
    val xlsId = genRandomId
    json2Excel.populate("123", "123", xlsId)
    val file = new java.io.File("files/" + xlsId + ".xls")
    val fileContent: Enumerator[Array[Byte]] = Enumerator.fromFile(file)
    SimpleResult(
      header = ResponseHeader(200),
      body = fileContent).withHeaders(CONTENT_DISPOSITION -> ("attachment; filename=" + xlsId + ".xls"))
  }
*/

  def uploadTemplate = Action(parse.multipartFormData) { request =>
    request.body.file("template").map { template =>
      val id = genRandomId
      //TODO: check if it already exists
      val filePath = "files/" + id + ".xls"
      template.ref.moveTo(new File(filePath))
      Ok(id)
    }.getOrElse {
      BadRequest("template upload failed")
    }
  }

  def uploadJson(id: String) = Action(parse.multipartFormData) { request =>
    request.body.file("json").map { json =>

      val jsonId = genRandomId
      //TODO: check if it already exists
      val xlsId = genRandomId
      //TODO: check if it already exists

      val filePath = "files/" + jsonId + ".json"
      json.ref.moveTo(new File(filePath))
      //      Ok("ssdsd")
      json2Excel.populate(id, jsonId, xlsId)
      Ok(xlsId)
    }.getOrElse {
      BadRequest("json upload failed")
    }
  }

  def downloadFile(id: String) = Action {
    val filePath = "files/" + id + ".xls"

    val file = new java.io.File(filePath)
    if (!file.exists()) {
      Ok("File not found : " + id)
    } else {
      val fileContent: Enumerator[Array[Byte]] = Enumerator.fromFile(file)
      //  Ok(fileContent) as
      SimpleResult(
        header = ResponseHeader(200),
        body = fileContent).withHeaders(CONTENT_DISPOSITION -> ("attachment; filename=" + id + ".xls"))
    }
  }
}

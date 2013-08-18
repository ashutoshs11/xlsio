package controllers

import play.api.data.Form
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.text
import play.api.data.Forms.tuple
import play.api.mvc.Action
import play.api.mvc.Controller
import models.Json2Excel
import play.api.libs.iteratee.Enumerator
import play.api.mvc.SimpleResult
import play.api.mvc.ResponseHeader
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

object Application extends Controller {
  def timestamp = new SimpleDateFormat("yyyy_MM_dd__hh_mm_ss").format(new Date())
  var json2Excel = new Json2Excel;

  val loginForm = Form(
    tuple("user" -> nonEmptyText, "password" -> text) verifying (
      "Invalid username or password", result => result match { case (user, password) => check(user, password) }));

  def check(username: String, password: String) = {
    (username == "ashu" && password == "1234")
  }
  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => Redirect(routes.Application.index).withSession("user" -> user._1))
  }
  def logout = Action {
    json2Excel.resetUserInfo
    Redirect(routes.Application.index).withNewSession.flashing(
      "success" -> "You are now logged out.")
  }

  def index = Action { implicit request =>
    session.get("user").map { user =>
      json2Excel.updateUserinfo(user)
      Redirect(routes.Application.home)
    }.getOrElse {
      Ok(views.html.login(loginForm))
    }
  }

  def home = Action { implicit request =>
    session.get("user").map { user =>
      json2Excel.updateUserinfo(user)
      Ok(views.html.home(user, json2Excel.templateUploaded, json2Excel.files))
    }.getOrElse {
      Ok(views.html.login(loginForm))
    }
  }

  def uploadTemplate = Action(parse.multipartFormData) { request =>
    request.body.file("uploadedFile").map { uploadedFile =>
      if (json2Excel.user == null) {
        Ok("Please login \nuser is not set : " + json2Excel.user)
        //        Ok(views.html.login(loginForm))
      } else {
        val filePath = "files/" + json2Excel.user + "/template.xls"
        uploadedFile.ref.moveTo(new File(filePath))
        Redirect(routes.Application.home)
        //        Ok("file upload successfull : " + filePath)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  def uploadJson = Action(parse.multipartFormData) { request =>
    request.body.file("uploadedFile").map { uploadedFile =>
      if (json2Excel.user == null) {
        Ok("Please login \nuser is not set : " + json2Excel.user)
      } else {
        val filePath = "files/" + json2Excel.user + "/" + timestamp + "_upload.json"
        uploadedFile.ref.moveTo(new File(filePath))
        Ok(json2Excel.populate(filePath))     
//        Redirect(routes.Application.home)

      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  //  def uploadJson = Action(parse.json) { request =>
  //    (request.body \ "name").asOpt[String].map { name =>
  //      Ok("Hello " + name)
  //    }.getOrElse {
  //      BadRequest("Missing parameter [name]")
  //    }
  //  }

  def downloadFile(name: String) = Action {
    val filePath = "files/" + json2Excel.user + "/" + name

    val file = new java.io.File(filePath)
    if (!file.exists()) {
      Ok("File not found : " + filePath)
    } else {
      val fileContent: Enumerator[Array[Byte]] = Enumerator.fromFile(file)
      //  Ok(fileContent) as
      SimpleResult(
        header = ResponseHeader(200),
        body = fileContent).withHeaders(CONTENT_DISPOSITION -> ("attachment; filename=" + name))
    }
  }

}

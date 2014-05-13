package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.*;


public class Application extends Controller {

  public static Result index() {
    return ok(main.render());
  }
  
  public static Result signin() {
    return ok(signin.render());
  }
}

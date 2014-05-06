package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;


public class Application extends Controller {

  public static Result index() {
    return redirect(controllers.routes.Wizard.step(1));
  }
  
  public static Result demo() {
    return ok(demo.render());
  }
}

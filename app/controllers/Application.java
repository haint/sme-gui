package controllers;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.demo.*;


public class Application extends Controller {

  public static Result index() {
    return redirect(controllers.routes.Wizard.step(1));
  }
  
  public static Result demo() {
    return ok(demo.render());
  }
  
  public static Result dashboard() {
    return ok(dashboard.render());
  }
  
  public static Result  javascript() {
    return ok(javascript.render());
  }
  
  public static Result javascriptRoutes() {
    response().setContentType("text/javascript");
    return ok(Routes.javascriptRouter("jsRoutes", 
        routes.javascript.Application.dashboard(),
        routes.javascript.Application.javascript()));
  }
}

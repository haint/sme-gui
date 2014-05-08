package controllers;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;


public class Application extends Controller {

  public static Result index() {
    return ok(main.render());
  }
  
  public static Result signin() {
    return ok(signin.render());
  }
  
  public static Result modal(int type) {
    switch (type) {
    //create new vm
    case 1:
      return ok(createMachineModal.render());
    default:
      return status(404).as("Unsupported modal type " + type);
    }
  }
  
  public static Result javascriptRoutes() {
    response().setContentType("text/javascript");
    return ok(Routes.javascriptRouter("router", 
        routes.javascript.Application.modal()));
  }
}

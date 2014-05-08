/**
 * 
 */
package controllers;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.demo.dashboard;
import views.html.demo.demo;
import views.html.demo.javascript;
import views.html.demo.widget;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * May 8, 2014
 */
public class Demo extends Controller {
  
  public static Result demo() {
    return ok(demo.render());
  }
  
  public static Result dashboard() {
    return ok(dashboard.render());
  }
  
  public static Result  javascript() {
    return ok(javascript.render());
  }
  
  public static Result widget() {
    return ok(widget.render());
  }
  
  public static Result javascriptRoutes() {
    response().setContentType("text/javascript");
    return ok(Routes.javascriptRouter("jsRoutes", 
        routes.javascript.Demo.dashboard(),
        routes.javascript.Demo.javascript(),
        routes.javascript.Demo.widget()));
  }
}

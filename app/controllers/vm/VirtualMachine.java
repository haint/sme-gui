/**
 * 
 */
package controllers.vm;

import java.io.UnsupportedEncodingException;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.vm.*;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * May 9, 2014
 */
public class VirtualMachine extends Controller {

  public static Result create() throws UnsupportedEncodingException {
    return ok(modal.render());
  }
  
  public static Result list() {
    return ok(vmlist.render(true, true));
  }
  
  public static Result detail(String id) {
    return ok(vmdetail.render());
  }
  
  public static Result ajax() {
    response().setContentType("text/javascript");
    return ok(Routes.javascriptRouter("vmRouter", 
        routes.javascript.VirtualMachine.create(),
        routes.javascript.VirtualMachine.detail()));
  }
}

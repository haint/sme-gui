/**
 * 
 */
package controllers.job;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.job.*;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * May 13, 2014
 */
public class JobController extends Controller {

  public static Result list(String jobType) {
    return ok(job.render(jobType));
  }
  
  public static Result create(String jobType) {
    return ok(modal.render(jobType));
  }
  
  public static Result ajax() {
    response().setContentType("text/javascript");
    return ok(Routes.javascriptRouter("jobRouter", 
        routes.javascript.JobController.list(),
        routes.javascript.JobController.create())); 
  }
}

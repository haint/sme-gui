/**
 * 
 */
package controllers;

import java.util.HashMap;
import java.util.Map;

import models.Job;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 24, 2014
 */
public class Wizard extends Controller {
  
  private static Job toJob(Map<String, String> session) {
    Job job = new Job();
    job.git = session.get("git");
    job.numberOfNode = (session.get("numberOfNode") != null && !session.get("numberOfNode").isEmpty()) ? Integer.parseInt(session.get("numberOfNode")) : 1;
    return job;
  }
  
  private static Map<String, String> toMap(Form<Job> job) {
    Map<String, String> map = new HashMap<String, String>();
    map.put("git", job.field("git").valueOr(""));
    map.put("numberOfNode", job.field("numberOfNode").valueOr(""));
    return map;
  }
 
  /**
   * Navigate between states. We saved the current user into the session.
   * This is of course only possible because we have little data.
   * 
   */
  public static Result step(int step) {
    Job user = toJob(session());
    Form<Job> jobForm = Form.form(Job.class).fill(user);
    
    if (step == 1)
      return ok(form1.render(jobForm));
    else
      return ok(form2.render(jobForm));
  }
  
  public static Result submit(int step) {
    Form<Job> filledForm = Form.form(Job.class).bindFromRequest();
    
    if (step == 1) {
      if (filledForm.hasErrors()) 
        return badRequest(form1.render(filledForm));
      else {
        session().putAll(toMap(filledForm));
        return redirect(routes.Wizard.step(2));
      }
    } else {
      
      if ("Previous".equals(filledForm.field("action").value())) {
        // We saved our info in the session
        session().putAll(toMap(filledForm));
        return redirect(routes.Wizard.step(1));
      }

      if (filledForm.hasErrors()) 
        return badRequest(form2.render(filledForm));
      else {
        Job created = filledForm.get();
        session().clear();
        return redirect(routes.Console.monitor(created.git, created.numberOfNode, filledForm.field("service.offering").value()));
      }
    }
  }
}

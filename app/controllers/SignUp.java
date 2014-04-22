/**
 * 
 */
package controllers;

import static play.data.Form.form;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.signup.form;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 22, 2014
 */
public class SignUp extends Controller {
  
  /**
   * Defines form wrapping the User class.
   */
  final static Form<User> signupForm = form(User.class);

  public static Result blank() {
    return ok(form.render(signupForm));
  }
  
  public static Result edit() {
    User existingUser = new User("fakeuser", "fake@gmail.com", "secret", 
        new User.Profile("France", null, 30));
    return ok(form.render(signupForm.fill(existingUser)));
  }
  
  public static Result submit() {
    Form<User> filledForm = signupForm.bindFromRequest();
    
    //Check accept conditions
    if (!"true".equals(filledForm.field("accept").value())) {
      filledForm.reject("accept", "You must accept the terms and conditions");
    }
    
    //Check repeat password
    if (!filledForm.field("password").valueOr("").isEmpty()) {
      if (!filledForm.field("password").valueOr("").equals(filledForm.field("repeatPassword").value())) {
        filledForm.reject("repeatPassword", "Password don't match");
      }
    }
    
    //Check if username is valid
    if (!filledForm.hasErrors()) {
      if (filledForm.get().username.equals("admin") || filledForm.get().username.equals("guest")) {
        filledForm.reject("username", "This username is already taken");
      }
    }
    
    if (filledForm.hasErrors()) {
      return badRequest(form.render(filledForm));
    } else {
      return ok();
    }
  }
}

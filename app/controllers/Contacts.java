/**
 * 
 */
package controllers;

import models.Contact;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.contact.form;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 23, 2014
 */
public class Contacts extends Controller {

  public static Form<Contact> contactForm = Form.form(Contact.class);
  
  public static Result blank() {
    return ok(form.render(contactForm));
  }
  
  public static Result edit() {
    return ok("");
  }
  
  public static Result submit() {
    return ok(""); 
  }
}

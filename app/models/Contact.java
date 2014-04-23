/**
 * 
 */
package models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 23, 2014
 */
public class Contact {
  
  @Required
  public String firstName;
  
  @Required
  public String lastName;
  
  public String company;
  
  @Valid
  public List<Information> informations;
  
  public Contact() {}
  
  public Contact(String firstName, String lastName, String company, Information ... informations) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.company = company;
    this.informations = new ArrayList<Information>();
    for (Information info : informations) {
      this.informations.add(info);
    }
  }
  
  public static class Information {
    
    @Required
    public String label;
    
    @Email
    public String email;
    
    @Valid
    public List<Phone> phones;
    
    public Information() {}
    
    public Information(String label, String email, String ... phones) {
      this.label = label;
      this.email = email;
      this.phones = new ArrayList<Phone>();
      for (String phone : phones) {
        this.phones.add(new Phone(phone));
      }
    }
    
    public static class Phone {
      
      @Required
      @Pattern(value = "[0-9.+]+", message = "A valid phone number is required")
      public String number;
      
      public Phone() {}
      
      public Phone(String number) {
        this.number = number;
      }
    }
  }
}

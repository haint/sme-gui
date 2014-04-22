/**
 * 
 */
package models;

import javax.validation.Valid;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 22, 2014
 */
public class User {
  
  @Required
  @MinLength(4)
  public String username;
  
  @Required
  @Email
  public String email;
  
  @Required
  @MinLength(6)
  public String password;
  
  @Valid
  public Profile profile;
  
  public User() {}
  
  public User(String username, String email, String password, Profile profile) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }
  
  public static class Profile {
    
    @Required
    public String country;
    
    public String address;
    
    @Min(18) @Max(100)
    public Integer age;
    
    public Profile() {}
    
    public Profile(String country, String address, Integer age) {
      this.country = country;
      this.address = address;
      this.age = age;
    }
  }
}

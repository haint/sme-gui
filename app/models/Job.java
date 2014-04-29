/**
 * 
 */
package models;

import javax.validation.Valid;

import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.Required;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 28, 2014
 */
public class Job {

  @Required
  public String git;
  
  @Valid
  @Min(1) @Max(3)
  public Integer numberOfNode;
}

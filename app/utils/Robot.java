/**
 * 
 */
package utils;

import akka.actor.UntypedActor;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * May 12, 2014
 */
public class Robot extends UntypedActor  {
  
  public void onReceive(Object msg) throws Exception {
    System.out.println(msg);
  }
}

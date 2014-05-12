/**
 * 
 */
package controllers.vm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.Routes;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.vm.*;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * May 9, 2014
 */
public class VirtualMachine extends Controller {
  
  private static ConcurrentLinkedQueue<String> queue1 = new ConcurrentLinkedQueue<String>();
  private static ConcurrentLinkedQueue<String> queue2 = new ConcurrentLinkedQueue<String>();
  
  static {
    try {
      BufferedReader reader = new BufferedReader(new FileReader("system.log.txt"));
      String line = null;
      while((line = reader.readLine()) != null) {
        queue1.add(line);
      }
      reader.close();
      
      reader = new BufferedReader(new FileReader("system.log.txt"));
      while((line = reader.readLine()) != null) {
        queue2.add(line);
      }
      reader.close();
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static Result doCreate() {
    DynamicForm form = Form.form().bindFromRequest();
    int number = Integer.parseInt(form.get("number"));
    
    ObjectNode json = Json.newObject();
    
    ArrayNode html = json.putArray("html");
    ArrayNode console = json.putArray("console");
    for (int i = 0; i < number; i++) {
     html.add(vm.render("slave-1248588-" + i, "linux").toString());
     console.add("<pre class='"+  "slave-1248588-" + i +" pre-scrollable' style='display: none;'><code></code><i class='icon-spinner icon-spin'></i></pre>");
    }
    return ok(json);
  }
  
  public static Result create() {
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
        routes.javascript.VirtualMachine.detail(),
        routes.javascript.VirtualMachine.doCreate()));
  }
  
  public static WebSocket<String> console(final String name) {
    return new WebSocket<String>() {

      @Override
      public void onReady(play.mvc.WebSocket.In<String> in, play.mvc.WebSocket.Out<String> out) {
        try {
          if (name.endsWith("0")) {
            System.out.println(name + " ready.....");
            while(queue1.peek() != null) {
              Thread.sleep(1000);
              out.write(queue1.poll());
            }
          } else if (name.endsWith("1")) {
            System.out.println(name + " ready.....");
            while(queue2.peek() != null) {
              Thread.sleep(1000);
              out.write(queue2.poll());
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
  }
}

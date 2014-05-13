/**
 * 
 */
package controllers.vm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bouncycastle.asn1.cmp.OOBCertHash;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

import play.Routes;
import play.api.templates.Html;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.libs.F.Callback;
import scala.collection.mutable.StringBuilder;
import utils.DBFactory;
import views.html.vm.*;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * May 9, 2014
 */
public class VirtualMachine extends Controller {
  
  private static ConcurrentLinkedQueue<String> queue1 = new ConcurrentLinkedQueue<String>();
  private static ConcurrentLinkedQueue<String> queue2 = new ConcurrentLinkedQueue<String>();
  
  public static Result doCreate() throws Exception {
    DynamicForm form = Form.form().bindFromRequest();
    int number = Integer.parseInt(form.get("number"));
    String ostype = form.get("ostype");
    String offering = form.get("vmoffering");
    
    BasicDBObject obj = new BasicDBObject();
    obj.put("os", "Ubuntu 12.04 " + ostype);
    
    if ("small".equals(offering)) {
      obj.put("cpu", "500 Mhz");
      obj.put("memory", "512 MB");
    } else if ("medium".equals(offering)) {
      obj.put("cpu", "1000 Mhz");
      obj.put("memory", "1G");
    } else if ("large".equals(offering)) {
      obj.put("cpu", "1000 Mhz");
      obj.put("memory", "2G");
    }
    
    DBCollection collection = DBFactory.getDatabase().getCollection("vm");
    
    ObjectNode json = Json.newObject();
    
    ArrayNode html = json.putArray("html");
    ArrayNode console = json.putArray("console");
    
    for (int i = 0; i < number; i++) {
     String name = "slave-" +System.currentTimeMillis()+ "-" + i;
     Date date = new Date(System.currentTimeMillis());
     obj.put("name", name);
     obj.put("date", date);
     obj.put("status", "Starting");
     obj.put("_id", UUID.randomUUID().toString());
     scala.collection.mutable.StringBuilder sb = new scala.collection.mutable.StringBuilder("<span>+ ");
     sb.append(obj.get("os")).append("</span><br>");
     sb.append("<span>+ ").append(date).append("</span>");
     
     play.api.templates.Html summary = new play.api.templates.Html(sb);
     obj.put("summary", sb.toString());
     collection.insert(obj);
     
     RunJob runJob = new RunJob(name);
     runJob.start();
     
     html.add(vm.render(true, name, "linux", "Starting", summary).toString());
     console.add("<pre class='"+  name +" pre-scrollable' style='display: none;'><code></code><i class='icon-spinner icon-spin'></i></pre>");
    }
    return ok(json);
  }
  
  public static Result create() {
    return ok(modal.render());
  }
  
  public static Result list() {
    DBCollection collection = DBFactory.getDatabase().getCollection("vm");
    DBCursor cursor = collection.find();
    scala.collection.mutable.StringBuilder sb = new scala.collection.mutable.StringBuilder();
    while(cursor.hasNext()) {
      DBObject obj = cursor.next();
      String name = (String)obj.get("name");
      String status = (String)obj.get("status");
      String summary = (String)obj.get("summary");
      sb.append(vm.render(false, name, "linux", status, new Html(new StringBuilder(summary))));
    }
    return ok(vmlist.render(true, true, new Html(sb)));
  }
  
  public static Result detail(String id) {
    DBCollection collection = DBFactory.getDatabase().getCollection("vm");
    DBCursor cursor = collection.find(new BasicDBObject("name", id));
    if (cursor.hasNext()) {
      DBObject obj = cursor.next();
      String status = (String)obj.get("status");
      String ip = (String)obj.get("ip");
      return ok(vmdetail.render(status, ip));
    }
    return ok(vmdetail.render("Starting", null));
  }
  
  public static Result log(String name) {
    DBCollection collection = DBFactory.getDatabase().getCollection("vm");
    DBCursor cursor = collection.find(new BasicDBObject("name", name));
    if (cursor.hasNext()) {
      String log = (String) cursor.next().get("log");
      return ok(vmlog.render(name, log));
    } else {
      return ok(vmlog.render(name, null));
    }
  }
  
  public static Result destroy(String name) {
    DBCollection collection = DBFactory.getDatabase().getCollection("vm");
    DBCursor cursor = collection.find(new BasicDBObject("name", name));
    if (cursor.hasNext()) {
      DBObject obj = cursor.next();
      collection.remove(obj, WriteConcern.JOURNAL_SAFE);
    } 
    return ok("OK");
  }
  
  public static Result ajax() {
    response().setContentType("text/javascript");
    return ok(Routes.javascriptRouter("vmRouter", 
        routes.javascript.VirtualMachine.create(),
        routes.javascript.VirtualMachine.detail(),
        routes.javascript.VirtualMachine.doCreate(),
        routes.javascript.VirtualMachine.list(),
        routes.javascript.VirtualMachine.log(),
        routes.javascript.VirtualMachine.destroy(),
        routes.javascript.VirtualMachine.status()));
  } 
  
  public static class RunJob extends Thread {
    
    private final String name;
    
    public RunJob(String name) {
      this.name = name;
    }

    @Override
    public void run() {
      try {
        BufferedReader reader = new BufferedReader(new FileReader("system.log.txt"));
        String line = null;
        if (name.endsWith("0"))
          queue1.add(name);
        else
          queue2.add(name);
        
        while((line = reader.readLine()) != null) {
          if (name.endsWith("0"))
            queue1.add(line);
          else
            queue2.add(line);
        }
        reader.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public static Result status(final String  name) {
    DBCollection collection = DBFactory.getDatabase().getCollection("vm");
    DBCursor cursor = collection.find(new BasicDBObject("name", name));
    DBObject obj = cursor.next();
    return ok((String)obj.get("status"));
  }
  
  public static WebSocket<String> console(final String name) {
    return new WebSocket<String>() {

      @Override
      public void onReady(play.mvc.WebSocket.In<String> in, play.mvc.WebSocket.Out<String> out) {
        
        DBCollection collection = DBFactory.getDatabase().getCollection("vm");
        DBCursor cursor = collection.find(new BasicDBObject("name", name));
        DBObject obj = cursor.next();
        
        StringBuilder sb = new StringBuilder();
        
        try {
          if (name.endsWith("0")) {
            System.out.println(name + " ready.....");
            while(queue1.peek() != null) {
              Thread.sleep(300);
              String s = queue1.poll();
              out.write(s);
              sb.setLength(0);
              String log = (String)obj.get("log");
              if (log != null) sb.append(log);
              sb.append(s).append("\n");
              obj.put("log", sb.toString());
              collection.save(obj);
            }
            obj.put("status", "Running");
            obj.put("ip", "172.27.4.86");
            collection.save(obj);
          } else if (name.endsWith("1")) {
            System.out.println(name + " ready.....");
            while(queue2.peek() != null) {
              Thread.sleep(300);
              String s = queue2.poll();
              out.write(s);
              sb.setLength(0);
              String log = (String)obj.get("log");
              if (log != null) sb.append(log);
              sb.append(s).append("\n");
              obj.put("log", sb.toString());
              collection.save(obj);
            }
            obj.put("status", "Running");
            obj.put("ip", "172.27.4.81");
            collection.save(obj);
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          out.close();
        }
      }
    };
  }
}

package controllers;

import java.util.Date;

import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.main;
import views.html.signin;
import views.html.socket;


public class Application extends Controller {

  public static Result index() {
    return ok(main.render());
  }
  
  public static Result signin() {
    return ok(signin.render());
  }
  
  public static Result testSoc() {
    return ok(socket.render());
  }
  
  public static WebSocket<String> socket() {
    return new WebSocket<String>() {

      @Override
      public void onReady(play.mvc.WebSocket.In<String> in, play.mvc.WebSocket.Out<String> out) {
        in.onMessage(new Callback<String>() {
          @Override
          public void invoke(String event) throws Throwable {
            System.out.println(event);
          }
        });
        
        in.onClose(new Callback0() {
          @Override
          public void invoke() throws Throwable {
            System.out.println("Disconnected");
          }
        });
      }
    };
  }
}

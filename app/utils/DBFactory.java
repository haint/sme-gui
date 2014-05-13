/**
 * 
 */
package utils;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * May 13, 2014
 */
public class DBFactory {

  public static DB getDatabase() {
    try {
      ServerAddress address = new ServerAddress("172.27.4.48");
      MongoClientOptions options = new MongoClientOptions.Builder().build();
      MongoClient client = new MongoClient(address, options);
      return client.getDB("sme");
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}

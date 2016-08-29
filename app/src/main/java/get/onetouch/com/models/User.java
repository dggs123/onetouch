package get.onetouch.com.models;

/**
 * Created by gaurav on 30/8/16.
 */

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;



public class User {

    public String name;
    public String email;
    public String lat;
    public String lang;
    public String time;
    public Boolean help;
    public String helpUserId;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email,String lat,String lang,String time) {
        this.name=name;
        this.email = email;
        this.lat = lat;
        this.lang=lang;
        this.lang=lang;
        this.time=time;
        this.help=false;
        this.helpUserId="";
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("lat", lat);
        result.put("lang",lang);
        result.put("time",time);
        result.put("help",help);
        result.put("helpUserId",helpUserId);
        return result;
    }

}
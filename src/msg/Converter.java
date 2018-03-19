/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msg;

import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import synchronization.Token;

/**
 *
 * @author Prashu
 */
public class Converter {
    public static String jsonToString(JSONObject obj)
    {
        return obj.toJSONString();
    }
    
    public static JSONObject toJson(JSONObject obj, HashMap<String,String> args)
    {
        JSONObject json = obj == null ? new JSONObject() : obj;
        json.putAll(args);
        return json;
    }
    
    public static JSONObject addArrayTOJson(JSONObject obj, String key,String[] val)
    {
        JSONObject json = obj == null ? new JSONObject() : obj;
        JSONArray arr = new JSONArray();
        for(String v: val)
            arr.add(v);
        json.put(key, arr);
        return json;
    }
    
    public static JSONObject stringToJson(String str)
    {
        JSONParser parser = new JSONParser();
        JSONObject obj = null;
        try {
                obj = (JSONObject) parser.parse(str);
        } catch (ParseException e) {
                e.printStackTrace();
        }
        return obj;
    }
    
    /*public static JSONObject createJsonForToken(Token t)
    {
        JSONObject obj = new JSONObject();
        return toJson(obj, map);
    }*/
}

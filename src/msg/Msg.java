/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msg;

import java.util.StringTokenizer;

/**
 *
 * @author Prashu
 */
public class Msg 
{
    int srcId;
    int destId;
    String tag;
    String message;
    
    public Msg(int srcId, int destId, String tag, String message)    
    {
        this.srcId = srcId;
        this.destId = destId;
        this.tag = tag;
        this.message = message;
    }

    public int getSrcId() 
    {
        return srcId;
    }

    public void setSrcId(int srcId) 
    {
        this.srcId = srcId;
    }

    public int getDesId() 
    {
        return destId;
    }

    public void setDesId(int destId) 
    {
        this.destId = destId;
    }

    public String getTag() 
    {
        return tag;
    }

    public void setTag(String tag) 
    {
        this.tag = tag;
    }

    public String getMessage() 
    {
        return message;
    }

    public void setMessage(String message) 
    {
        this.message = message;
    }
    
    public int getMessageInt()
    {
        StringTokenizer st = new StringTokenizer(message);
        return Integer.parseInt(st.nextToken());
    }
    
    public String toString()
    {
        return String.valueOf(srcId) + " " + String.valueOf(destId) + " " + tag + " " +message + "#";
    }
    
    public static Msg parseMsg(StringTokenizer st)
    {
        int srcId = Integer.parseInt(st.nextToken());
        int destId = Integer.parseInt(st.nextToken());
        String tag = st.nextToken();
        String message = st.nextToken("#");
        return new Msg(srcId, destId, tag, message);
    }
}

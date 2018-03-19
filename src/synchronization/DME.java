/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;

import java.util.LinkedList;
import java.util.Queue;
import msg.Msg;
import util.Util;
import connectivity.Linker;
import java.util.HashMap;
import msg.Converter;
import org.json.simple.JSONObject;

/**
 *
 * @author Prashu
 */
public class DME extends Process implements Lock{
    
    boolean CsPermission = false;
    int col;
    int row;
    boolean chCol;
    Queue<Integer> queue = new LinkedList<>();
    public DME(Linker initComm, int coordinator) {
        super(initComm);
        CsPermission = (myId == coordinator);
    }
    public synchronized void initiate() {
        //if (CsPermission) sendToken();
    }

    @Override
    public void requestCS() {
       queue.add(myId);
       if(queue.size() == 1)
       {
           int next = (int)this.comm.neighbors.get(0);
           sendMsg(next,"request",myId);
           while(!CsPermission)
           {
               //System.out.println("waiting");
               myWait();
           }
           
       }
    }

    @Override
    public void releaseCS() {
        CsPermission = false;
        while(!queue.isEmpty())
            queue.poll();
        Token t = Token.getToken();
        if(col == Math.sqrt(N))
        {
            col = 0;
            sendToken("down");
        }
        else
        {
            sendToken("right");
        }
    }
    
    void sendToken(String direction) {
        int next;
        if(direction.equals("down"))
            next = (int)this.comm.neighbors.get(1);
        else
            next = (int)this.comm.neighbors.get(0);
        Util.println("Process " + myId + " has sent the token to "+next);
        
        HashMap<String,String> map = new HashMap<>();
        map.put("rowCounter", String.valueOf(row));
        map.put("colCounter", String.valueOf(col));
        map.put("chCol",String.valueOf(chCol));
        String token = Converter.jsonToString(Converter.toJson(null,map));
        sendMsg(next, "token",token);
    }
    public synchronized void handleMsg(Msg m, int src, String tag) {
        Util.println(m.getMessage() + " :: "+tag);
        if(tag.equals("request"))
        {
            int j = Integer.parseInt(m.getMessage().trim());
            if(myId != j)
            {
                queue.add(j);
                int next = (int)this.comm.neighbors.get(0);
                sendMsg(next,"request",j);
            }
        }
        else if(tag.equals("token"))
        {
            JSONObject obj = Converter.stringToJson(m.getMessage());
            col = Integer.parseInt(obj.get("colCounter").toString());
            row = Integer.parseInt(obj.get("rowCounter").toString());
            chCol = Boolean.valueOf(obj.get("chCol").toString());
            //Token t = Token.getToken();
            Util.println(col +" :: "+row);
            if(col == 0)
            {
                row++;
                if(row == Math.sqrt(N))
                {
                    chCol = true;
                    col = -1;
                    sendToken("right");// send to right neighbor
                }
                else if(queue.isEmpty())
                {
                    sendToken("down");// send to right neighbor
                }
                else
                {
                    col = 1;
                    if(queue.contains(myId))
                    {
                        CsPermission = true;
                        notify();
                    }
                    else
                    {
                      while(!queue.isEmpty())
                        queue.poll();
                       sendToken("right");// send to right neighbor
                    }
                }
            }
            else if(col > 0)
            {
                col++;
                if(queue.contains(myId))
                {
                    CsPermission = true;
                    notify();
                }
                else
                {
                   while(!queue.isEmpty())
                        queue.poll();
                   if(col == Math.sqrt(N))
                   {
                       col = 0;
                       sendToken("down");// send to right neighbor
                   }
                   else
                   {
                      sendToken("right");// send to right neighbor
                   }
                }
            }
            else if(col == -1)
            {
                chCol = false;
                row = 1;
                if(queue.isEmpty())
                {
                    col = 0;
                    System.out.println("queue is empty first if sending token to down");
                    sendToken("down");// send to down neighbor
                }
                else
                {
                    col = 1;
                    if(queue.contains(myId))
                    {
                        System.out.println("queue contains me and going to execute cs");
                        CsPermission = true;
                        notify();
                    }
                    else
                    {
                        System.out.println("going to empty queue and sending token to right");
                        while(!queue.isEmpty())
                        queue.poll();
                        sendToken("right");// send to right neighbor
                    }
                }
            }
        }
    }
    
}

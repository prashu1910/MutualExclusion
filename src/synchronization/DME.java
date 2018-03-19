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

/**
 *
 * @author Prashu
 */
public class DME extends Process implements Lock{
    boolean CsPermission = false;
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
        if(t.getColCounter() == Math.sqrt(N))
        {
            t.setColCounter(0);
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
        sendMsg(next, "token");
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
            Token t = Token.getToken();
            Util.println(t.getColCounter() +" :: "+t.getRowCounter());
            if(t.getColCounter() == 0)
            {
                t.setRowCounter(t.getRowCounter()+1);
                if(t.getRowCounter() == Math.sqrt(N))
                {
                    t.setChCol(true);
                    t.setColCounter(-1);
                    sendToken("right");// send to right neighbor
                }
                else if(queue.isEmpty())
                {
                    sendToken("down");// send to right neighbor
                }
                else
                {
                    t.setColCounter(1);
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
            else if(t.getColCounter() > 0)
            {
                t.setColCounter(t.getColCounter()+1);
                if(queue.contains(myId))
                {
                    CsPermission = true;
                    notify();
                }
                else
                {
                   while(!queue.isEmpty())
                        queue.poll();
                   if(t.getColCounter() == Math.sqrt(N))
                   {
                       t.setColCounter(0);
                       sendToken("down");// send to right neighbor
                   }
                   else
                   {
                      sendToken("right");// send to right neighbor
                   }
                }
            }
            else if(t.getColCounter() == -1)
            {
                t.setChCol(false);
                t.setRowCounter(1);
                if(queue.isEmpty())
                {
                    t.setColCounter(0);
                    System.out.println("queue is empty first if sending token to down");
                    sendToken("down");// send to down neighbor
                }
                else
                {
                    t.setColCounter(1);
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

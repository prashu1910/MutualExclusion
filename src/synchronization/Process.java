/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;

import connectivity.Linker;
import msg.Msg;
import msg.MsgHandler;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Prashu
 */
public class Process implements MsgHandler 
{
    int N, myId;
    Linker comm;

    public Process(Linker initComm) 
    {
        comm = initComm;
        myId = comm.getMyId();
        N = comm.getNumProc();
    }

    
    @Override
    public synchronized void handleMsg(Msg m, int src, String tag) 
    {
        //System.out.println(" Message:: "+m.getMessage() + " :: "+m.getTag());
    }
    public void sendMsg(int destId, String tag, String msg) 
    {
        //Util.println("Sending msg to " + destId + ":" +tag + " " + msg);
        comm.sendMsg(destId, tag, msg);
    }
    public void sendMsg(int destId, String tag, String msg, byte[] signature)  
    {
        //Util.println("Sending msg to " + destId + ":" +tag + " " + msg);
        
        comm.sendMsg(destId, tag, msg,signature);
    }
    public void sendMsg(int destId, String tag, int msg) 
    {
        sendMsg(destId, tag, String.valueOf(msg)+" ");
    }
    public void sendMsg(int destId, String tag, int msg1, int msg2) 
    {
        sendMsg(destId,tag,String.valueOf(msg1) + " "+ String.valueOf(msg2) + " ");
    }
    public void sendMsg(int destId, String tag) 
    {
        sendMsg(destId, tag, " 0 ");
    }
    public void broadcastMsg(String tag, int msg) 
    {
        for (int i = 0; i < N; i++)
            if (i != myId) 
                sendMsg(i, tag, msg);
    }
    public void sendToNeighbors(String tag, int msg) 
    {
        for (int i = 0; i < N; i++)
            if (isNeighbor(i)) sendMsg(i, tag, msg);     
    }
    public boolean isNeighbor(int i) 
    {
        if (comm.neighbors.contains(i)) 
            return true;
        return false;
    }
    public Msg receiveMsg(int fromId) 
    {
        try {
            return comm.receiveMsg(fromId);
        } catch (IOException e){
            System.out.println(e);
            comm.close();
            return null;
        }
    }
    public synchronized void myWait() 
    {
        try 
        {
            wait();
        } 
        catch (InterruptedException e) 
        {
            System.err.println(e);
        }
    }
    
}


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;

import msg.Msg;
import msg.MsgHandler;
import util.Util;

/**
 *
 * @author Prashu
 */
public class ListenerThread extends Thread{
    int channel;
    MsgHandler process;
    private volatile boolean running = true;
    public ListenerThread(int channel, MsgHandler process)
    {
        this.channel = channel;
        this.process = process;
    }
    
    public void run()
    {
        while(running)
        {
            try
            {
                Msg message = process.receiveMsg(channel);
                if(message == null)
                    throw new NullPointerException();
                process.handleMsg(message, message.getSrcId(), message.getTag());
            }
            catch(Exception ex)
            {
                //System.err.println(ex);
                Util.println("Exception in listenerthread " +channel );
                ex.printStackTrace();
                running = false;
                
            }
        }
    }
}

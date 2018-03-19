/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;

import msg.Msg;
import msg.MsgHandler;
import java.io.IOException;

/**
 *
 * @author Prashu
 */
public class ListenerThread extends Thread{
    int channel;
    MsgHandler process;
    
    public ListenerThread(int channel, MsgHandler process)
    {
        this.channel = channel;
        this.process = process;
    }
    
    public void run()
    {
        while(true)
        {
            try
            {
                Msg message = process.receiveMsg(channel);
                process.handleMsg(message, message.getSrcId(), message.getTag());
            }
            catch(IOException ex)
            {
                System.err.println(ex);
            }
        }
    }
}

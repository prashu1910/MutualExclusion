/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;

import java.io.IOException;
import java.net.Socket;
import msg.MsgHandler;
import connectivity.Linker;
import util.Util;

/**
 *
 * @author Prashu
 */
public class LockTest {
    static int myId;
    public static void main(String[] args) {
        Linker comm = null;
        
        try {
            String baseName = args[0];
            int port = Integer.parseInt(args[1]);
            myId = Integer.parseInt(args[2]);
            int numProc = Integer.parseInt(args[3]);
            boolean run = Boolean.parseBoolean(args[4]);
            System.out.println(run);
            comm = new Linker(baseName, myId, port, numProc);
            Lock lock = null;
            lock = new DME(comm,0);
            for (int i = 0; i < numProc; i++)
               if (i != myId)
                  (new ListenerThread(i, (MsgHandler)lock)).start();
            while (true && run) {
                System.out.println(myId + " is not in CS");
                Util.mySleep(4000);
                lock.requestCS();
                executeCS();
                System.out.println(myId + " is in CS *****");
                Util.mySleep(4000);
                lock.releaseCS();
            }
        }
        catch (InterruptedException e) {
            if (comm != null) comm.close();
        }
        catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void executeCS() throws IOException
    {
        Socket s = FileClient.getConnection("127.0.0.1", 13267);
        String message = "D:\\sync\\resource.txt"+"#"+"Message send from process "+myId;
        FileClient.sendMsg(s, message);
        FileClient.closeSocket(s);
    }
}

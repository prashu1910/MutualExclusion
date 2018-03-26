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
import filehandler.Client;
import java.security.PrivateKey;
import java.util.Random;
import security.KeyManagement;
import util.Util;

/**
 *
 * @author Prashu
 */
public class LockTest {
    static int myId;
    static Lock lock = null;
    static Linker comm = null;
    static String baseName;
    static int port;
    public static String path;
    public static int k;
    public static void main(String[] args) {
        
        
        try {
            baseName = args[0];
            port = Integer.parseInt(args[1]);
            myId = Integer.parseInt(args[2]);
            int numProc = Integer.parseInt(args[3]);
            //boolean run = Boolean.parseBoolean(args[4]);
            path = args[4];
            int detectionLimit = Integer.parseInt(args[5]);
            KeyManagement.generateKeys(path+"/key/"+myId,path+"/"+myId);
            //System.out.println(run);
            comm = new Linker(baseName, myId, port, numProc);
            PrivateKey privateakey = KeyManagement.getOwnPrivateKey(path+"/"+myId, KeyManagement.ALGORITHM);
            lock = new DME(comm,0,privateakey,detectionLimit);
            for (int i = 0; i < numProc; i++)
               if (i != myId)
                  (new ListenerThread(i, (MsgHandler)lock)).start();
            
            Random r = new Random();
            while(true)
            {
                boolean run = r.nextBoolean();
                //boolean run = true;
                if(true)
                {
                    System.out.println(myId + " is not in CS");
                    Util.mySleep(4000);
                    lock.requestCS();
                    executeCS();
                    System.out.println(myId + " is in CS *****");
                    Util.mySleep(4000);
                    lock.releaseCS();
                }
                else
                {
                    System.out.println("sleeping");
                    Thread.sleep(1000);
                }
            }
        }
        catch (InterruptedException e) {
            System.out.println("exception in locktest 1");
            if (comm != null) comm.close();
        }
        catch (Exception e) {
            System.out.println("exception in locktest :: "+e);
            e.printStackTrace();
        }
    }
    
    public static void executeCS() throws IOException
    {
        Socket s = Client.getConnection("127.0.0.1", 13267);
        String message = "D:\\sync1\\resource.txt"+"#"+"Message send from process "+myId;
        Client.sendMsg(s, message);
        Client.closeSocket(s);
    }
    
    public static Lock getLock()
    {
        return lock;
    }
}


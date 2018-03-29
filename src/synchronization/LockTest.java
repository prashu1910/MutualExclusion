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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.logging.Logger;
import logger.Log;
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
    public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);   
    
    
    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
     
    
    public static void main(String[] args) 
    {
    
        try 
        {
            
            baseName = args[0];
            port = Integer.parseInt(args[1]);
            myId = Integer.parseInt(args[2]);
            int numProc = Integer.parseInt(args[3]);
            //boolean run = Boolean.parseBoolean(args[4]);
            path = args[4];
            int detectionLimit = Integer.parseInt(args[5]);
            Log.setup("log_"+myId+".txt");
            //KeyManagement.generateKeys(path+"/key/"+myId,path+"/"+myId);
            //Util.println(run);
            boolean isRestarted = args.length >= 7 ? Boolean.valueOf(args[6]) : false;
            if(isRestarted)
                Util.println(".................Restarting the process..................");
            comm = new Linker(baseName, myId, port, numProc,isRestarted);
            PrivateKey privateakey = KeyManagement.getOwnPrivateKey(path+"/"+myId, KeyManagement.ALGORITHM);
            lock = new DME(comm,0,privateakey,detectionLimit,isRestarted);
            comm.setProcess(lock);
            
            if(isRestarted)
            {
                ((DME)lock).updateNeighbour();
            }
            for (int i = 0; i < numProc; i++)
               if (i != myId)
                  (new ListenerThread(i, (MsgHandler)lock)).start();
            
            Random r = new Random();
            int testCase = 5;
            while(true)
            {
                boolean run = r.nextBoolean();
                //boolean run = true;
                    
                if(run)
                {
                    Util.println(myId + " is not in CS");
                    Util.mySleep(4000);
                    lock.requestCS();
                    executeCS();
                    Util.mySleep(4000);
                    lock.releaseCS();
                    
                }
                else
                {
                    Util.println("sleeping");
                    Thread.sleep(1000);
                }
            }
        }
        catch (InterruptedException e) {
            Util.println("exception in locktest 1");
            if (comm != null) comm.close();
        }
        catch (Exception e) {
            Util.println("exception in locktest :: "+e);
            e.printStackTrace();
        }
    }
    
    public static void executeCS() throws IOException
    {
        Util.println("............executing critical Section.............");
        Socket s = Client.getConnection("127.0.0.1", 13267);
        LocalDateTime now = LocalDateTime.now();
        String message = "D:\\sync1\\resource.txt"+"#"+"Message send from process "+myId + " at "+dtf.format(now) + "  "+now;
        Client.sendMsg(s, message);
        Client.closeSocket(s);
        Util.println("............executing critical Section completed.............");
    }
    
    public static Lock getLock()
    {
        return lock;
    }
}


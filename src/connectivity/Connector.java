/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.StringTokenizer;
import msg.MsgHandler;
import synchronization.ListenerThread;
import synchronization.LockTest;
import util.PortAddr;
import util.Symbols;
/**
 *
 * @author Prashu
 */
public class Connector {
    
    ServerSocket listener;  
    Socket [] link;
    HashSet<Integer> smallerDeadLinks = new HashSet<>();
    Name myNameclient;
    BufferedReader[] dataIn;
    PrintWriter[] dataOut;
    static LinkedList<Integer> disconnected = new LinkedList<>();
    String baseName;
    int myId;
    public void Connect(String basename, int myId, int port, int numProc, BufferedReader[] dataIn, PrintWriter[] dataOut) throws Exception 
    {
        myNameclient = new Name();
        link = new Socket[numProc];
        int localport = port > 0?  port : getLocalPort(myId);
        listener = new ServerSocket(localport);
        this.dataIn = dataIn;
        this.dataOut = dataOut;
        baseName = basename;
        /* register in the name server */
        myNameclient.insertName(basename + myId, InetAddress.getLocalHost().getHostName(), localport,myId);
        
        /* accept connections from all the smaller processes */
        for (int i = 0; i < myId; i++) 
        {
            Socket s = listener.accept();
            BufferedReader dIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String getline = dIn.readLine();
            StringTokenizer st = new StringTokenizer(getline);
            int hisId = Integer.parseInt(st.nextToken());
            int destId = Integer.parseInt(st.nextToken());
            String tag = st.nextToken();
            if (tag.equals("hello")) 
            {
                link[hisId] = s;
                dataIn[hisId] = dIn;
                dataOut[hisId] = new PrintWriter(s.getOutputStream());
            }
        }
        /* contact all the bigger processes */
        for (int i = myId + 1; i < numProc; i++) 
        {
            PortAddr addr;
            do
            {
                addr = myNameclient.searchName(basename + i);
                Thread.sleep(100);
            } while (addr.getPort() == -1);
            addr = myNameclient.searchName(basename + i);
            if(addr.getPort() == -1)
            {
                disconnected.add(i);
                continue;
            }
            link[i] = new Socket(addr.getHostName(), addr.getPort());
            dataOut[i] = new PrintWriter(link[i].getOutputStream());
            dataIn[i] = new BufferedReader(new
            InputStreamReader(link[i].getInputStream()));
            /* send a hello message to P_i */
            dataOut[i].println(myId +" "+ i +" "+ "hello" + " " + "null");
            dataOut[i].flush();
        }
        //(new AcceptConnection()).start();
        //(new CreateConnection()).start();
    }
    int getLocalPort(int id) 
    { 
        return Symbols.ServerPort + 10 + id; 
    }
    public void closeSockets()
    {
        try {
            listener.close();
            for (int i=0;i<link.length; i++) 
                link[i].close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    public void closeSocket(int processID)
    {
        try {
            link[processID].close();
            myNameclient.removeName(processID);
            disconnected.add(processID);
        } catch (IOException ex) {
           System.out.println("socket closed for "+processID);
        }
    }
    
    public static LinkedList<Integer> getDisconnectedNode()
    {
        return disconnected;
    }
    
    
    
    private class AcceptConnection extends Thread
    {
         public void run()
         {
            while(true)
            {
                try
                {
                    Socket s = listener.accept();
                    System.out.println("got new connection :: "+(s == null ? "no connection" : s));
                    BufferedReader dIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    String getline = dIn.readLine();
                    StringTokenizer st = new StringTokenizer(getline);
                    int hisId = Integer.parseInt(st.nextToken());
                    int destId = Integer.parseInt(st.nextToken());
                    String tag = st.nextToken();
                    if (tag.equals("hello")) 
                    {
                        link[hisId] = s;
                        dataIn[hisId] = dIn;
                        dataOut[hisId] = new PrintWriter(s.getOutputStream());
                        (new ListenerThread(hisId, (MsgHandler)LockTest.getLock())).start();
                    }
                }
                catch(Exception ex)
                {
                    System.out.println("Exception in accepting connection from socket");
                }
            }
         }
    }
    
    
     private class CreateConnection extends Thread
    {
         public void run()
         {
            while(true)
            {
                try
                {
                    while(!disconnected.isEmpty())
                    {
                        int id = disconnected.poll();
                        PortAddr addr;
                        addr = myNameclient.searchName(baseName + id);
                        if(addr.getPort() == -1)
                        {
                            disconnected.add(id);
                            continue;
                        }
                        link[id] = new Socket(addr.getHostName(), addr.getPort());
                        dataOut[id] = new PrintWriter(link[id].getOutputStream());
                        dataIn[id] = new BufferedReader(new
                        InputStreamReader(link[id].getInputStream()));
                        /* send a hello message to P_i */
                        dataOut[id].println(myId +" "+ id +" "+ "hello" + " " + "null");
                        dataOut[id].flush();
                    }
                }
                catch(Exception ex)
                {
                    System.out.println("Exception in accepting connection from socket");
                }
            }
         }
    }
}



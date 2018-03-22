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
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.PortAddr;
import util.Symbols;
/**
 *
 * @author Prashu
 */
public class Connector {
    
    ServerSocket listener;  
    Socket [] link;
    
    public void Connect(String basename, int myId, int port, int numProc, BufferedReader[] dataIn, PrintWriter[] dataOut) throws Exception 
    {
        Name myNameclient = new Name();
        link = new Socket[numProc];
        int localport = port > 0?  port : getLocalPort(myId);
        listener = new ServerSocket(localport);
        
        /* register in the name server */
        myNameclient.insertName(basename + myId, InetAddress.getLocalHost().getHostName(), localport);
        
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
            link[i] = new Socket(addr.getHostName(), addr.getPort());
            dataOut[i] = new PrintWriter(link[i].getOutputStream());
            dataIn[i] = new BufferedReader(new
            InputStreamReader(link[i].getInputStream()));
            /* send a hello message to P_i */
            dataOut[i].println(myId +" "+ i +" "+ "hello" + " " + "null");
            dataOut[i].flush();
        }
        
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
        } catch (IOException ex) {
           System.out.println("socket closed for "+processID);
        }
    }
}

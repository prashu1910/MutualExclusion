/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectivity;

import msg.Msg;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 *
 * @author Prashu
 */
public class Linker 
{
    PrintWriter[] dataOut;
    BufferedReader[] dataIn;
    BufferedReader dIn;
    int myId, N;
    Connector connector;
    public LinkedList<Integer> neighbors = new LinkedList<>();
    
    public Linker(String basename, int id, int port, int numProc) throws Exception 
    {
        myId = id;
        N = numProc;
        dataIn = new BufferedReader[numProc];
        dataOut = new PrintWriter[numProc];
        Topology.readNeighbors(myId, N, neighbors);
        //System.out.println("creating connector");
        connector = new Connector();
        connector.Connect(basename, myId, port, numProc, dataIn, dataOut);
        //System.out.println("connector created and connect");
    }
    public void sendMsg(int destId, String tag, String msg) 
    {     
        dataOut[destId].println(myId + " " + destId + " " + tag + " " + msg + "#");
        dataOut[destId].flush();
       
    }
    
    public void sendMsg(int destId, String tag, String msg, byte[] sign) 
    {
       int length = sign.length;
      // String signatsure = Arrays.toString(sign);
       // System.out.println("Array signatsure = " + signatsure);
       String signature = Base64.getEncoder().encodeToString(sign);
       //String signature = new String(encode, "UTF-8");
       // System.out.println("signature send= " + signature);
       dataOut[destId].println(myId + " " + destId + " " + tag + " " + msg + "#"+ length + " " + signature +"#");
       dataOut[destId].flush();
    }
    
    public void sendMsg(int destId, String tag) 
    {
        sendMsg(destId, tag, " 0 ");
    }
    public void multicast(LinkedList<Integer> destIds, String tag, String msg)
    {
        for (int i=0; i<destIds.size(); i++) 
        {
            sendMsg(destIds.get(i), tag, msg);
        }
    }
    public Msg receiveMsg(int fromId) throws IOException  
    {        
        String getline = dataIn[fromId].readLine();
       // Util.println(" received message " + getline);
        StringTokenizer st = new StringTokenizer(getline);
        int srcId = Integer.parseInt(st.nextToken().trim());
        int destId = Integer.parseInt(st.nextToken().trim());
        String tag = st.nextToken().trim();
        String msg = st.nextToken("#");
        int signLength = 0;
        String signature = null;
        if(st.hasMoreTokens())
        {
            signLength = Integer.parseInt(st.nextToken(" ").trim().substring(1));
        }
            
        if(st.hasMoreTokens())
        {
            signature = st.nextToken("#").trim();
           // System.out.println("signature received = " + signature);
        }
            
        return new Msg(srcId, destId, tag, msg, signLength, signature);        
    }
    public int getMyId() 
    { 
        return myId; 
    }
    public int getNumProc() 
    { 
        return N; 
    }
    public void close() 
    {
        connector.closeSockets();
    }
}

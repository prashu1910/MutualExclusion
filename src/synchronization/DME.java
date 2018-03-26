/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;

import connectivity.Connector;
import java.util.LinkedList;
import java.util.Queue;
import msg.Msg;
import util.Util;
import connectivity.Linker;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import msg.Converter;
import org.json.simple.JSONObject;
import security.CipherManagement;
import security.KeyManagement;
import security.Sign;

/**
 *
 * @author Prashu
 */


enum TokenType
{
    REAL, BACKUP, NONE
}
public class DME extends Process implements Lock{
    //public enum TokenType = {"REAL","BACKUP","NONE"};
    boolean CsPermission = false;
    static ArrayList<Integer> columnNeighbour ;
    static ArrayList<Integer> rowNeighbour ;
    static ArrayList<Integer> detectionSet ;
    static ArrayList<Integer> faultySet ;
    TokenType tokenType = TokenType.NONE;
    int col;
    int row;
    boolean chCol;
    String rowProcess = "@";
    String colProcess = "@";
    PrivateKey privateKey;
    Queue<Integer> queue = new LinkedList<>();
    int count = 0;
    
    public DME(Linker initComm, int coordinator, PrivateKey privateKey, int detectionLimit) {
        super(initComm);
        int sqrtN = (int)Math.sqrt(N);
        columnNeighbour = new ArrayList<>(sqrtN);
        rowNeighbour = new ArrayList<>(sqrtN);
        detectionSet = new ArrayList<>(2*sqrtN);
        faultySet = new ArrayList<>(2*sqrtN);
        CsPermission = (myId == coordinator);
        this.privateKey = privateKey;
        
        //calculating columns 
        
        initiate(coordinator,detectionLimit);
       
    }
    
    public static ArrayList<Integer> getcolumnNeighbour() {
        return columnNeighbour;
    }

    public static ArrayList<Integer> getrowNeighbour() {
        return rowNeighbour;
    }

    public static ArrayList<Integer> getDetectionSet() {
        return detectionSet;
    }

    public static ArrayList<Integer> getFaultySet() {
        return faultySet;
    }
    
    private void initiate(int coordinator, int detectionLimit) 
    {
        //if (CsPermission) sendToken();
        int sqrtN = (int)Math.sqrt(N);
         boolean isCoordinatorColumn = (coordinator % sqrtN) == (myId % sqrtN);
        
        
        int idx = (myId / sqrtN);
        int firstNode = (myId - idx * sqrtN) % N;
        for(int k = 0; k < sqrtN; k++)
        {
            rowNeighbour.add((firstNode + k * sqrtN) % N);
        }
        
        //calculating rows
        
       
        int rows = myId / sqrtN;
        for(int k = 0; k < sqrtN; k++)
        {
            columnNeighbour.add(rows * sqrtN + k);
        }
        
        System.out.println("col = " + columnNeighbour);
        System.out.println("row = " + rowNeighbour);
        if(isCoordinatorColumn)
            updateDetection(coordinator,detectionLimit,null);
            
        System.out.println("detect = " + detectionSet);
    }
    
    public void updateDetection(int coordinator, int detectionLimit,Token token)
    {
       /* if(token == null)
        {
            this.token = createToken(coordinator);
            this.token.setTokenMovement("down");
            this.token.getProcessSet().add(coordinator);
        }
        else
            this.token = token;
        if(myId == coordinator)
            tokenType = TokenType.REAL;
        else
            tokenType = TokenType.BACKUP;
        
        detectionSet.clear();
        int sqrtN = (int)Math.sqrt(N);
        if(this.token.getTokenMovement().equals("right"))
        {
            int idx = (coordinator % sqrtN);
            for(int k = idx; k <= (idx + detectionLimit); k++)
            {
                int next = columnNeighbour.get(k % sqrtN);
                detectionSet.add(next);
                if(next == myId)
                    break;
            }
        }
        else
        {
            int idx = (coordinator / sqrtN);
            for(int k = idx; k <= (idx + detectionLimit); k++)
            {
                int next = rowNeighbour.get(k % sqrtN);
                detectionSet.add(next);
                if(next == myId)
                    break;
            }
        }
        */
    }
    
    

    
    @Override
    public void requestCS() {
       
       queue.add(myId);
       if(queue.size() == 1)
       {
          //int next = (int)this.comm.neighbors.get(0);
           sendMessage("right","request",myId,null);
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
       
        if(colProcess.contains("@"+String.valueOf(myId)+"@"))
        //if(col == Math.sqrt(N))
        {
            col = 0;
            colProcess = "@";
            //sendToken("down");
            sendMessage("down","token",0,null);
        }
        else
        {
            colProcess += myId + "@";
            //sendToken("right");
            sendMessage("right","token",0,null);
        }
    }
    void sendMessage(String direction, String tag, int message1, String message2)
    {
        
        int next = -1;
        int right = comm.neighbors.get(0);
        int down = comm.neighbors.get(1);
        String dir = "right";
        if(right == -1 && down == -1)
            return;
        if(right == -1 && direction.equals("right"))
        {
            next = down;
            dir = "down";
        }
        else if(direction.equals("right"))
            next = right;
        
        if(down == -1 && direction.equals("down"))
        {    
            next = right;
            dir = "right";
        }
        else if(direction.equals("down"))
        {
            next = down;
            dir = "down";
        }
        
        if(tag.equals("request"))
        {
            if(dir.equals("right"))
            {    
                System.out.println("sending request message from "+myId + " to "+next);
                sendMsg(next, "request", message1);
            }
            else
            {
                System.out.println("can not send request from "+myId + " to "+next);
            }
        }
        else if(tag.equals("token"))
        {
            if(direction.equals("right") && dir.equals("down"))
            {
                col = 0;
                colProcess = "@";
            }
            else if(direction.equals("down") && dir.equals("right"))
            {
                rowProcess = "@";
                chCol = true;
                col = -1;
            }
            HashMap<String,String> map = new HashMap<>();
            map.put("rowCounter", String.valueOf(row));
            map.put("colCounter", String.valueOf(col));
            map.put("chCol",String.valueOf(chCol));
            map.put("colProcess",colProcess);
            map.put("rowProcess",rowProcess);
            String token = Converter.jsonToString(Converter.toJson(null,map));
            token = CipherManagement.encrypt(token, KeyManagement.getNodePublicKey(LockTest.path+"/key/"+next, KeyManagement.ALGORITHM));
            //System.out.println("token = " + token + " :: "+myId);
            byte signature[] = Sign.sign(token, privateKey, Sign.ALGORITHM);
            sendMsg(next, "token",token,signature);
        }
        
    }
    void sendToken(String direction) 
    {
        int next;
    
        if(direction.equals("down"))
            next = this.comm.neighbors.get(1);
        else
            next = this.comm.neighbors.get(0);
        Util.println("Process " + myId + " has sent the token to "+next);
        
        HashMap<String,String> map = new HashMap<>();
        map.put("rowCounter", String.valueOf(row));
        map.put("colCounter", String.valueOf(col));
        map.put("chCol",String.valueOf(chCol));
        map.put("colProcess",colProcess);
        map.put("rowProcess",rowProcess);
        String token = Converter.jsonToString(Converter.toJson(null,map));
        //token = CipherManagement.encrypt(token, KeyManagement.getNodePublicKey(LockTest.path+"/key/"+next, KeyManagement.ALGORITHM));
        //System.out.println("token = " + token + " :: "+myId);
       // byte signature[] = Sign.sign(token, privateKey, Sign.ALGORITHM);
        //System.out.println("signature = " + signature);
        //sendMsg(next, "token",token,signature);
        sendMsg(next, "token",token);
    }
    
   
    public synchronized void handleMsg(Msg m, int src, String tag) {
      //  Util.println(m.getMessage() + " :: "+tag);
        if(tag.equals("request"))
        {
            int j = Integer.parseInt(m.getMessage().trim());
            if(myId != j)
            {
                System.out.println("adding request of "+j+" to "+myId);
                queue.add(j);
                sendMessage("right", "request", j, null);
            }
        }
        else if(tag.equals("token"))
        {
            System.out.println("token received from " + src +" to "+myId + " having value "+m.getMessage());
            byte[] signature = Base64.getDecoder().decode(m.getSignature());
           
            boolean isTrue = Sign.verify(m.getMessage().trim(), KeyManagement.getNodePublicKey(LockTest.path+"/key/"+src, KeyManagement.ALGORITHM), Sign.ALGORITHM, signature);
       
            if(isTrue)
            {
                String message = CipherManagement.decrypt(m.getMessage().trim(), KeyManagement.getOwnPrivateKey(LockTest.path+"/"+myId, KeyManagement.ALGORITHM));
                JSONObject obj = Converter.stringToJson(message);
                col = Integer.parseInt(obj.get("colCounter").toString());
                row = Integer.parseInt(obj.get("rowCounter").toString());
                chCol = Boolean.valueOf(obj.get("chCol").toString());
                colProcess = obj.get("colProcess").toString();
                rowProcess = obj.get("rowProcess").toString();
                System.out.println("colProcess "+colProcess + " rowProcess " + rowProcess);
              
                if(col == 0)
                {
                    row++;
                    int next = comm.neighbors.get(1);
                    if(rowProcess.indexOf("@"+String.valueOf(next)+"@") > 0)
                    //if(row == Math.sqrt(N))
                    {
                        chCol = true;
                        col = -1;
                        rowProcess = "@";
                        //sendToken("right");// send to right neighbor
                        sendMessage("right","token",0,null);
                    }
                    else if(queue.isEmpty())
                    {
                        rowProcess += myId + "@";
                        //sendToken("down");// send to right neighbor
                        sendMessage("down","token",0,null);
                    }
                    else
                    {
                        col = 1;
                        if(queue.contains(myId))
                        {
                            CsPermission = true;
                            notify();
                        }
                        else
                        {
                          while(!queue.isEmpty())
                            queue.poll();
                          colProcess = colProcess + myId + "@";
                           //sendToken("right");// send to right neighbor
                           sendMessage("right","token",0,null);
                        }
                    }
                }
                else if(col > 0)
                {
                    col++;
                    if(queue.contains(myId))
                    {
                        CsPermission = true;
                        notify();
                    }
                    else
                    {
                       while(!queue.isEmpty())
                            queue.poll();
                       if(colProcess.indexOf("@"+String.valueOf(myId)+"@")> -1)
                        //if(col == Math.sqrt(N))
                       {
                           col = 0;
                           colProcess = "@";
                           //sendToken("down");// send to right neighbor
                           sendMessage("down","token",0,null);
                       }
                       else
                       {
                           colProcess = colProcess + myId + "@";
                          //sendToken("right");// send to right neighbor
                          sendMessage("right","token",0,null);
                       }
                    }
                }
                else if(col == -1)
                {
                    chCol = false;
                    row = 1;
                    if(queue.isEmpty())
                    {
                        col = 0;
                        System.out.println("queue is empty first if sending token to down");
                        rowProcess = rowProcess + myId+"@";
                        colProcess = "@";
                        //sendToken("down");// send to down neighbor
                        sendMessage("down","token",0,null);
                    }
                    else
                    {
                        col = 1;
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
                            colProcess = colProcess + myId +"@";
                            //sendToken("right");// send to right neighbor
                            sendMessage("right","token",0,null);
                        }
                    }
                }
            }
            else
                System.out.println("intrusion detected");
        }
    }
    
    
    public static void changeNeighbour(int processId)
    {
        updateFaultySet(processId);
        int right = comm.neighbors.get(0);
        int down = comm.neighbors.get(1);
        LinkedList<Integer> disconnected = Connector.getDisconnectedNode();
        if(processId == right)
        {
            int next = -1;
            ArrayList<Integer> row = DME.getcolumnNeighbour();
            int sqrtN = (int)Math.sqrt(N);
            int idx = (myId) % sqrtN; 
            do
            {
                idx = (idx + 1 )% sqrtN;
                next = row.get(idx);
                if(!disconnected.contains(next))
                    break;
            }while(next != myId);
            if(next == myId)
                next = -1;
            System.err.println("connecting to new process as a right neighbour :: " + next);
            comm.neighbors.set(0, next);
        }
        if(processId == down)
        {
            
            int next = -1;
            ArrayList<Integer> row = DME.getrowNeighbour();
            int sqrtN = (int)Math.sqrt(N);
            int idx = (myId) / sqrtN; 
            do
            {
                idx = (idx + 1 )% sqrtN;
                next = row.get(idx);
                if(!disconnected.contains(next))
                    break;
            }while(next != myId);
            if(next == myId)
                next = -1;
            System.err.println("connecting to new process as a down neighbour :: " + next);
            comm.neighbors.set(1,next);
        }
        
    }
    
    public void updateDetectionSet(String type)
    {
        
    }
    
    public static void updateFaultySet(int processId)
    {
        if(detectionSet.contains(processId))
            faultySet.add(processId);
        int sqrtN = (int)Math.sqrt(N);
        /*if(faultySet.size() == (sqrtN-1))
            use backup token as a real token
        
        else if all my predecssor got crashed then i will use token as a real
        */
         System.out.println("faultySet = " + faultySet);   
    }
}

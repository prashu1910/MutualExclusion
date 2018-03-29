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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import msg.Converter;
import org.json.simple.JSONObject;
import security.CipherManagement;
import security.KeyManagement;

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
    
    static int      count = 0;
    int             detectionLimit;
    static int      col;
    int             row;
    boolean         chCol;
    boolean         CsPermission = false;
    String          rowProcess = "@";
    String          colProcess = "@";
    String          backupTokenPresentAt = "";
    PrivateKey      privateKey;
    
    static TokenType             tokenType = TokenType.NONE;
    static ArrayList<Integer>    columnNeighbour ;
    static ArrayList<Integer>    rowNeighbour ;
    static ArrayList<Integer>    detectionSet ;
    static ArrayList<Integer>    faultySet ;
    private Object               lockObj = new Object();
    Queue<Integer> queue = new LinkedList<>();
    
    public DME(Linker initComm, int coordinator, PrivateKey privateKey, int detectionLimit, boolean isRestarted) 
    {
    
        super(initComm);
        int sqrtN = (int)Math.sqrt(N);
        this.detectionLimit = detectionLimit;
        columnNeighbour = new ArrayList<>(sqrtN);
        rowNeighbour = new ArrayList<>(sqrtN);
        detectionSet = new ArrayList<>(2*sqrtN);
        faultySet = new ArrayList<>(2*sqrtN);
        CsPermission = (myId == coordinator);
        this.privateKey = privateKey;
        
        //calculating columns 
        
        initiate(coordinator,isRestarted);
       
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
    
    private void initiate(int coordinator,boolean isRestarted) 
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
        
        Util.println("col = " + columnNeighbour);
        Util.println("row = " + rowNeighbour);
        if(isCoordinatorColumn && !isRestarted)
            createDetectionSet(coordinator,detectionLimit);
            
        //Util.println("detect = " + detectionSet + " backUp present At "+backupTokenPresentAt);
    }
    
    public void createDetectionSet(int coordinator, int detectionLimit)
    {
        int sqrtN = (int)Math.sqrt(N);
        int idx = (coordinator / sqrtN);
        if((idx+detectionLimit) < myId/sqrtN)
            return;
        
        if(myId == coordinator)
            tokenType = TokenType.REAL;
        else
            tokenType = TokenType.BACKUP;
        
        detectionSet.clear();
        
        for(int k = idx; k <= (idx + detectionLimit); k++)
        {
            int next = rowNeighbour.get(k % sqrtN);
            detectionSet.add(next);
            if(next == myId)
                break;
        }
        if(myId == coordinator)
        {
            ArrayList<Integer> list = new ArrayList<>();
            for(int k = idx + 1; k <= (idx + detectionLimit); k++)
            {
                int next = rowNeighbour.get(k % sqrtN);
                if(!list.contains(next) && next != myId)
                    list.add(next);
            }
            backupTokenPresentAt = list.stream().map(Object::toString)
                        .collect(Collectors.joining(", "));
        }
       /* if(this.token.getTokenMovement().equals("right"))
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
            
        }
        */
    }
    
    

    
    @Override
    public void requestCS() {
       
        queue.add(myId);
        Util.println("..........requesting for cs...........");
        if(queue.size() == 1)
        {
          //int next = (int)this.comm.neighbors.get(Linker.RIGHT);
           sendMessage("right","request",myId,null);
           Util.println("...........cspermission in requestcs..."+CsPermission);
        }
        synchronized(lockObj)
        {
           while(!CsPermission)
            {
                //Util.println("waiting");
               try
               {
                    lockObj.wait();
               }
               catch(InterruptedException ex)
               {
                   System.err.println("exception.. "+ex);
               }
            } 
        }
        
        Util.println("..........requesting for cs end...........");
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
            Util.println(".....Previous ColProcess.... "+colProcess);
            String previousColumnProcess = colProcess;
            colProcess = "@";
            //sendToken("down");
            sendMessage("down","token",0,previousColumnProcess);
            sendClearDetectionSetMessage();
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
        int right = comm.neighbors.get(Linker.RIGHT);
        int down = comm.neighbors.get(Linker.DOWN);
        Util.println("........right........."+right+".......down"+down);
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
                Util.println("sending request message from "+myId + " to "+next);
                sendMsg(next, "request", message1);
            }
            else
            {
                Util.println("can not send request from "+myId + " to "+next);
            }
        }
        else if(tag.equals("token"))
        {
            ArrayList<Integer> backupNodes = new ArrayList<>();
            if(direction.equals("right") && dir.equals("down"))
            {
                getBackupNodes(next,"down",backupNodes);
                col = 0;
                colProcess = "@";
            }
            else if(direction.equals("down") && dir.equals("right"))
            {
                getBackupNodes(next,"right",backupNodes);
                rowProcess = "@";
                colProcess = message2;
                chCol = true;
                col = -1;
            }
            count++;
            getBackupNodes(next,dir,backupNodes);
           //. Util.println("backupNodes are " + backupNodes);
            HashMap<String,String> map = new HashMap<>();
            map.put("rowCounter", String.valueOf(row));
            map.put("colCounter", String.valueOf(col));
            map.put("chCol",String.valueOf(chCol));
            map.put("count",String.valueOf(count));
            map.put("colProcess",colProcess);
            map.put("rowProcess",rowProcess);
            map.put("execProcess",String.valueOf(next));
            String backup = backupNodes.stream().map(Object::toString)
                        .collect(Collectors.joining(", "));
            map.put("backupNodes", backup);
            map.put("count",String.valueOf(count));
            
            String token = Converter.jsonToString(Converter.toJson(null,map));
           // token = CipherManagement.encrypt(token, KeyManagement.getNodePublicKey(LockTest.path+"/key/"+next, KeyManagement.ALGORITHM));
           Util.println("sending token to "+next + " token "+token);
           // byte signature[] = Sign.sign(token, privateKey, Sign.ALGORITHM);
            if(backupNodes.contains(myId))
            {
                tokenType = TokenType.BACKUP;
                updateDetectionSet(backup,next);
            }
            else
            {
                tokenType = TokenType.NONE;
                detectionSet.clear();
            }
           
            sendMsg(next,"token",token);
            for(int node : backupNodes)
            {
                if(node != myId)
                {
                    Util.println("...........Sending backup token to "+node+"...........");
                    sendMsg(node,"token",token);
                }
            }
        }
        
    }
    public void sendClearDetectionSetMessage()
    {
        List<String> result = Arrays.asList(backupTokenPresentAt.split("\\s*,\\s*"));
        Util.println("...........backupTokenPresentAt was..."+result);
        for(String node : result)
        {
            int id = Integer.parseInt(node);
            sendMsg(id, "clearDetectionSet", count);
        }
    }
    public void getBackupNodes(int nextNode,String direction, ArrayList<Integer> list)
    {
        int sqrtN = (int)Math.sqrt(N);
        int idx = direction.equals("down") ? (nextNode / sqrtN) : (nextNode % sqrtN);
        LinkedList<Integer> disconnected = Connector.getDisconnectedNode();
        ArrayList<Integer> nodes = direction.equals("down") ? rowNeighbour : columnNeighbour;
       // Util.println("in getbackup node " +nextNode+ " "+direction + " "+list + " "+detectionLimit + " "+nodes);    
        idx++;
        int i = detectionLimit;
        for(int k = 0; k < nodes.size(); k++)
        {
            
            int next = nodes.get(idx % sqrtN);
            //Util.println("next = " + next + " "+i + " "+k);
            if((i == 0) || (next == nextNode))
                break;
            
            if(!disconnected.contains(next))
            {
                //Util.println("decrememnting i");
                i--;
                list.add(next);
            }
            idx++;
        }
       // Util.println("final list in getBackupNodes is "+list);
    }
  
    public synchronized void handleMsg(Msg m, int src, String tag) {
        Util.println("..............message received....."+tag + " " +m.getMessage());
        if(tag.equals("request"))
        {
            int j = Integer.parseInt(m.getMessage().trim());
            if(myId != j)
            {
                Util.println("adding request of "+j+" to "+myId);
                queue.add(j);
                sendMessage("right", "request", j, null);
            }
        }
        else if(tag.equals("token"))
        {
            Util.println("token received from " + src +" to "+myId + " having value "+m.getMessage());
            //byte[] signature = Base64.getDecoder().decode(m.getSignature());
           
            //boolean isTrue = Sign.verify(m.getMessage().trim(), KeyManagement.getNodePublicKey(LockTest.path+"/key/"+src, KeyManagement.ALGORITHM), Sign.ALGORITHM, signature);
       
            if(true)
            {
                //String message = CipherManagement.decrypt(m.getMessage().trim(), KeyManagement.getOwnPrivateKey(LockTest.path+"/"+myId, KeyManagement.ALGORITHM));
                JSONObject obj = Converter.stringToJson(m.getMessage());
                if(count >= Integer.parseInt(obj.get("count").toString()))
                {
                    Util.println("............received old token..........new count.. " + obj.get("count").toString()+" .... old count "+count);
                    return;
                }
                    
                count = Integer.parseInt(obj.get("count").toString());
                col = Integer.parseInt(obj.get("colCounter").toString());
                //row = Integer.parseInt(obj.get("rowCounter").toString());
                chCol = Boolean.valueOf(obj.get("chCol").toString());
                colProcess = obj.get("colProcess").toString();
                rowProcess = obj.get("rowProcess").toString();
                int execProcess = Integer.parseInt(obj.get("execProcess").toString());
                String backupNodes = obj.get("backupNodes").toString();
                //Util.println("saved all info and now updating detectionset " +execProcess );
                updateDetectionSet(backupNodes,execProcess);
                //Util.println("detectionset updated");
                backupTokenPresentAt = backupNodes;
                
                if(execProcess == myId)
                {
                    tokenType = TokenType.REAL;
                }
                else if(amIOnlyAlive())
                {
                    tokenType = TokenType.REAL;
                    count = count + detectionSet.size() - 1;
                    detectionSet.clear();
                    detectionSet.add(myId);
                }
                else
                    tokenType = TokenType.BACKUP;
                
                //Util.println("colProcess "+colProcess + " rowProcess " + rowProcess);
                if(tokenType == TokenType.REAL)
                {
                    Util.println("..........inside of real token.............");
                    if(col == 0)
                    {
                       // row++;
                        int next = comm.neighbors.get(Linker.DOWN);
                        if(rowProcess.indexOf("@"+String.valueOf(next)+"@") > 0)
                        //if(row == Math.sqrt(N))
                        {
                            chCol = true;
                            col = -1;
                            rowProcess = "@";
                            //sendToken("right");// send to right neighbor
                            sendClearDetectionSetMessage();
                            sendMessage("right","token",0,null);
                        }
                        else if(queue.isEmpty())
                        {
                            rowProcess += myId + "@";
                            String oldColProcess = colProcess;
                            //sendToken("down");// send to right neighbor
                            sendMessage("down","token",0,oldColProcess);
                        }
                        else
                        {
                            col = 1;
                            if(queue.contains(myId))
                            {
                                Util.println("..........permission granted to execute CS.........1");
                                synchronized(lockObj)
                                {
                                    CsPermission = true;
                                    lockObj.notify();
                                }
                            }
                            else
                            {
                              while(!queue.isEmpty())
                                queue.poll();
                              
                              colProcess = colProcess + myId + "@";
                               //sendToken("right");// send to right neighbor
                               sendClearDetectionSetMessage();
                               sendMessage("right","token",0,null);
                            }
                        }
                    }
                    else if(col > 0)
                    {
                        col++;
                        if(queue.contains(myId))
                        {
                            Util.println("..........permission granted to execute CS.........2");
                            synchronized(lockObj)
                            {
                                CsPermission = true;
                                lockObj.notify();
                            }
                        }
                        else
                        {
                           while(!queue.isEmpty())
                                queue.poll();
                           if(colProcess.contains("@"+String.valueOf(myId)+"@"))
                            //if(col == Math.sqrt(N))
                           {
                               col = 0;
                               String oldColProcess = colProcess;
                               colProcess = "@";
                               //sendToken("down");// send to right neighbor
                               sendClearDetectionSetMessage();
                               sendMessage("down","token",0,oldColProcess);
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
                        //row = 1;
                        if(queue.isEmpty())
                        {
                            col = 0;
                            Util.println("queue is empty first if sending token to down");
                            rowProcess = rowProcess + myId+"@";
                            String oldColProcess = colProcess;
                            colProcess = "@";
                            //sendToken("down");// send to down neighbor
                            sendClearDetectionSetMessage();
                            sendMessage("down","token",0,oldColProcess);
                        }
                        else
                        {
                            col = 1;
                            if(queue.contains(myId))
                            {
                                colProcess = colProcess + myId +"@";
                                Util.println("queue contains me and going to execute cs");
                                Util.println("..........permission granted to execute CS.........3");
                                synchronized(lockObj)
                                {
                                    CsPermission = true;
                                    lockObj.notify();
                                }
                            }
                            else
                            {
                                Util.println("going to empty queue and sending token to right");
                                while(!queue.isEmpty())
                                queue.poll();
                                colProcess = colProcess + myId +"@";
                                //sendToken("right");// send to right neighbor
                                sendMessage("right","token",0,null);
                            }
                        }
                    }
                }
                
            }
            else
                Util.println("intrusion detected");
        }
        else if(tag.equals("clearDetectionSet"))
        {
            Util.println("...........cleardetectionset received from "+src);
            int c = Integer.parseInt(m.getMessage().trim());
            if(count <= c)
            {
                Util.println("Clearing detection set from clearDetectionSet message");
                detectionSet.clear();
                tokenType = TokenType.NONE;
            }
        }
        else if(tag.equals("change_neighbour"))
        {
            String msg = m.getMessage().trim();
            if(msg.equals("right"))
            {
                Util.println("............changing "+msg+" neighbour from "+comm.neighbors.get(Linker.RIGHT)+" to "+src+"....");
                comm.neighbors.set(Linker.RIGHT, src);
            }
            else if(msg.equals("down"))
            {
                Util.println("............changing "+msg+" neighbour from "+comm.neighbors.get(Linker.DOWN)+" to "+src+"....");
                comm.neighbors.set(Linker.DOWN, src);
            }
        }
    }
    
    public static boolean amIOnlyAlive()
    {
        for(int node : detectionSet)
        {
            if(!faultySet.contains(node) && node != myId)
                return false;
        }
        return true;
    }
    public void updateNeighbour()
    {
        int right = comm.neighbors.get(Linker.RIGHT);
        int down = comm.neighbors.get(Linker.DOWN);
        LinkedList<Integer> disconnected = Connector.getDisconnectedNode();
        
        //......................find out right neighbour...........................
        
        int next = -1;
        ArrayList<Integer> col = DME.getcolumnNeighbour();
        int sqrtN = (int)Math.sqrt(N);
        int idx = (myId) % sqrtN; 
        do
        {
            idx = (idx + 1 )% sqrtN;
            next = col.get(idx);
            if(!disconnected.contains(next))
                break;
        }while(next != myId);
        if(next == myId)
            next = -1;
        System.err.println("connecting to new process in recovery as a right neighbour :: " + next);
            comm.neighbors.set(Linker.RIGHT, next);
            
        //................................find out down neighbour........................    
        next = -1;
        ArrayList<Integer> row = DME.getrowNeighbour();
        idx = (myId) / sqrtN; 
        do
        {
            idx = (idx + 1 )% sqrtN;
            next = row.get(idx);
            if(!disconnected.contains(next))
                break;
        }while(next != myId);
        if(next == myId)
            next = -1;
        System.err.println("connecting to new process in recovery as a down neighbour :: " + next);
        comm.neighbors.set(Linker.DOWN,next);
        
        
        //-----------------------------find out upper neighbour.............................
        
        next = -1;
        idx = (myId) / sqrtN; 
        do
        {
            
           idx = ( sqrtN + idx - 1 )% sqrtN;
            next = row.get(idx);
            if(!disconnected.contains(next))
                break;
        }while(next != myId);
        if(next == myId)
            next = -1;
        if(next != -1)
        {
            Util.println("...............send message to " + next+" for updating its down neighbour......");
            sendMsg(next, "change_neighbour", "down");
        }
       
        //-----------------------------find out left neightbour-------------------------
        next = -1;
        idx = (myId) % sqrtN; 
        do
        {
            
            idx = (idx + sqrtN - 1 )% sqrtN;
            next = col.get(idx);
            if(!disconnected.contains(next))
                break;
        }while(next != myId);
        if(next == myId)
            next = -1;
        if(next != -1)
        {
            Util.println("...............send message to " + next+" for updating its right neighbour......");
            sendMsg(next, "change_neighbour", "right");
        }
    }
    
    public  void changeNeighbour(int processId)
    {
        int right = comm.neighbors.get(Linker.RIGHT);
        int down = comm.neighbors.get(Linker.DOWN);
        LinkedList<Integer> disconnected = Connector.getDisconnectedNode();
        if(processId == right)
        {
            int next = -1;
            ArrayList<Integer> col = DME.getcolumnNeighbour();
            int sqrtN = (int)Math.sqrt(N);
            int idx = (myId) % sqrtN; 
            do
            {
                idx = (idx + 1 )% sqrtN;
                next = col.get(idx);
                if(!disconnected.contains(next))
                    break;
            }while(next != myId);
            if(next == myId)
                next = -1;
            System.err.println("connecting to new process as a right neighbour :: " + next);
            comm.neighbors.set(Linker.RIGHT, next);
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
        updateFaultySet(processId);
    }
    
    public void updateDetectionSet(String backupNodes,int execProcess)
    {
        List<String> result = Arrays.asList(backupNodes.split("\\s*,\\s*"));
        //Util.println("result of backup node= " + result.size());
        detectionSet.clear();
        detectionSet.add(execProcess);
        if(execProcess == myId)
            return;
        for(String node : result)
        {
            int n = Integer.parseInt(node);
            //Util.println("adding detection set "+n);
            detectionSet.add(n);
            if(n == myId)
                break;
        }
        Util.println(".........updated Detectionset is "+detectionSet);
    }
    
    public  synchronized  void updateFaultySet(int processId)
    {
        if(!detectionSet.contains(processId))
            return;
        else
            faultySet.add(processId);
        int sqrtN = (int)Math.sqrt(N);
        System.err.println("detectionSet in updateFaulty............."+detectionSet);
        System.err.println("FaultySet in updateFaulty................"+faultySet);
        if(amIOnlyAlive() && tokenType == TokenType.BACKUP)
        {
            System.err.println("............going to use backup token...........");
            tokenType = TokenType.REAL;
            count = count + detectionSet.size() - 1;
            detectionSet.clear();
            detectionSet.add(myId);
            rowProcess = "@";
            colProcess = "@";
            if(queue.isEmpty())
            {
                col = 0;
                Util.println("queue is empty first if sending token to down from UpdateFaulty");
                rowProcess = rowProcess + myId+"@";
                String oldColProcess = colProcess;
                colProcess = "@";
                //sendToken("down");// send to down neighbor
                sendMessage("down","token",0,oldColProcess);
            }
            else
            {
                col = 1;
                if(queue.contains(myId))
                {
                    Util.println("queue contains me and going to execute cs");
                    synchronized(lockObj)
                    {
                        CsPermission = true;
                        lockObj.notify();
                    }
                }
                else
                {
                    Util.println("going to empty queue and sending token to right");
                    while(!queue.isEmpty())
                        queue.poll();
                    colProcess = colProcess + myId +"@";
                    //sendToken("right");// send to right neighbor
                    sendMessage("right","token",0,null);
                }
            }
        }
         Util.println("faultySet = " + faultySet);   
    }
}

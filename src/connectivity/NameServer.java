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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import util.Symbols;
import util.Util;
/**
 *
 * @author Prashu
 */
public class NameServer {
   /* NameTable table;
    public NameServer() {
        table = new NameTable();
        
    }
    void handleclient(Socket theClient) {
        try {
            BufferedReader din = new BufferedReader(new InputStreamReader(theClient.getInputStream()));
            PrintWriter pout = new PrintWriter(theClient.getOutputStream());
            String getline = din.readLine();          
            StringTokenizer st = new StringTokenizer(getline);
            String tag = st.nextToken();
            if (tag.equals("search")) {
                int index = table.search(st.nextToken());
                if (index == -1) // not found
                    pout.println(-1 + " " + "nullhost");
                else
                    pout.println(table.getPort(index) + " "
                    + table.getHostName(index));
            } else if (tag.equals("insert")) {
                String name = st.nextToken();
                String hostName = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
                int id = Integer.parseInt(st.nextToken());
                int retValue = table.insert(name, hostName, port, id);
                Util.println("node added:: "+hostName + "@"+name+":"+port);
                pout.println(retValue);
            }
            else if(tag.equals("remove"))
            {
                int id = Integer.parseInt(st.nextToken());
                table.remove(id);
            }
            pout.flush();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    public static void main(String[] args) {
        NameServer ns = new NameServer();
        Util.println("NameServer started:");
        try {
            ServerSocket listener = new ServerSocket(Symbols.SERVERPORT);
            while (true) {
                Socket aClient = listener.accept();
                ns.handleclient(aClient);
                aClient.close();
            }
        } catch (IOException e) {
            System.err.println("Server aborted:" + e);
        }
    }*/
}

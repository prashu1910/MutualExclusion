/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filehandler;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import util.Util;

/**
 *
 * @author Prashu
 */
public class Server {
 /*   
public final static int SOCKET_PORT = 13267;
    
    public static void main(String[] args) throws IOException {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        FileWriter fw = null;
        ServerSocket servsock = null;
        Socket sock = null;
        BufferedWriter bw = null;
        PrintWriter out = null;
        try 
        {
            servsock = new ServerSocket(SOCKET_PORT);
            while (true) 
            {
                Util.println("Waiting...");
                try 
                {
                    sock = servsock.accept();
                    Util.println("Accepted connection : " + sock);
                    DataInputStream dis = new DataInputStream(sock.getInputStream());
                    byte[] buffer = new byte[4096];
                    int read = dis.read(buffer, 0,4096);
                    Util.println("read = " + read);
                    String data = new String(buffer,0,read);
                    Util.println("data = " + data);
                    StringTokenizer st = new StringTokenizer(data, "#");
                    String fileName = st.nextToken();
                    Util.println("fileName = " + fileName);
                    fw = new FileWriter(fileName, true);
                    String d = st.nextToken();
                    Util.println("d = " + d);
                    bw = new BufferedWriter(fw);
                    out = new PrintWriter(bw);
                    out.print(d);
                    out.close();
                    Util.println("Done.");
                }
                finally 
                {
                    if (bis != null) bis.close();
                    if (os != null) os.close();
                    if (sock!=null) sock.close();
                    if (fw != null) fw.close();
                }
            }
        }
        finally 
        {
            if (servsock != null) try {
                servsock.close();
            } catch (IOException ex) {
                
            }
        }
    }*/
    
    
}


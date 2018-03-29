
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
import util.Util;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Prashu
 */
public class TestServer {
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
            servsock = new ServerSocket(1202);
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
    }
    
}

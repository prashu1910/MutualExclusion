
import static filehandler.Server.SOCKET_PORT;
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
                System.out.println("Waiting...");
                try 
                {
                    sock = servsock.accept();
                    System.out.println("Accepted connection : " + sock);
                    DataInputStream dis = new DataInputStream(sock.getInputStream());
                    byte[] buffer = new byte[4096];
                    
                    int read = dis.read(buffer, 0,4096);
                    System.out.println("read = " + read);
                    String data = new String(buffer,0,read);
                    System.out.println("data = " + data);
                    System.out.println("Done.");
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

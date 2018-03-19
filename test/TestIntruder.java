
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Prashu
 */
public class TestIntruder {
    public static void main(String[] args) throws IOException {
        Socket s = new Socket("127.0.0.1",1202);
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        
        
        PrintWriter out = new PrintWriter(dos, true);
        //byte[] buffer = msg.getBytes();
        System.err.println("sending message");
        out.println(0 + " " + 1 + " " + "token" + " " + "hello" + "#"+ 128 + " " + "dsfsdfsdfsdsgdgsdg" +"#");
        s.close();
        //dos
                
    }
}

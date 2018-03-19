
import java.security.PrivateKey;
import java.security.PublicKey;
import security.KeyManagement;
import security.Sign;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Prashu
 */
public class TestSignature {
    public static void main(String[] args) {
        String message = "Hello";
        PrivateKey privateKey = KeyManagement.getOwnPrivateKey("D://sync1/0", "RSA");
        byte signature[] = Sign.sign(message, privateKey, "SHA256withRSA");
        
        PublicKey publicKey = KeyManagement.getNodePublicKey("D://sync1/key/0", "RSA");
        boolean isTrue = Sign.verify(message, publicKey, "SHA256withRSA", signature);
        if(isTrue)
            System.out.println("chal raha hai = ");
        else
            System.out.println("nahi chala");
    }
}

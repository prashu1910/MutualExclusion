/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

/**
 *
 * @author Prashu
 */
public class Sign {
    
    public static byte[] sign(String msg, PrivateKey privateKey, String signAlgo)
    {
        Signature sign = null;
        try {
            sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(privateKey);
            sign.update(msg.getBytes());
            return sign.sign();
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static boolean verify(String message, PublicKey pubKey,String sigAlg, byte[] sigbytes)
    {
        try
        {
            //System.out.println("sigbytes = " + sigbytes);
            Signature sig = Signature.getInstance(sigAlg);
            sig.initVerify(pubKey);
            sig.update(message.getBytes());
            return sig.verify(sigbytes);
        }
        catch( NoSuchAlgorithmException | InvalidKeyException | SignatureException ex)
        {
           //System.err.println(ex.getMessage()); 
            ex.printStackTrace();
        }
        return false;
  }
}

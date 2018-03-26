/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 *
 * @author Prashu
 */
public class CipherManagement {
    
    public static final String ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    public static String encrypt(String message, PublicKey publicKey)
    {
        try
        {
            Cipher encryptCipher = Cipher.getInstance(ALGORITHM);
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] cipherText = encryptCipher.doFinal(message.getBytes(UTF_8));
           // System.out.println("Encryption "+cipherText.length + " :: " +Arrays.toString(cipherText));
            String msg = Base64.getEncoder().encodeToString(cipherText);
            //System.out.println("cipherText = encryption" + msg.length() + " :: "+msg );   
            return Base64.getEncoder().encodeToString(cipherText);
        }
        catch(Exception ex)
        {
           ex.printStackTrace();
        }
        
        return null;
    }
    
    public static String decrypt(String cipherText, PrivateKey privateKey) 
    {
        try
        {
           // System.out.println("decryption msg " + cipherText.length() + " :: "+cipherText);
            byte[] bytes = Base64.getDecoder().decode(cipherText.trim());
         //   System.out.println("Decryption  "+ bytes.length + " :: " + Arrays.toString(bytes));
            Cipher decryptCipher = Cipher.getInstance(ALGORITHM);
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(decryptCipher.doFinal(bytes), UTF_8);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
    /*public static void main(String[] args) {
        byte[] arr = {66, -115, 5, -45, 18, 41, -127, -19, 61, 120, 30, 53, 82, 66, -120, 98, -87, 89, 70, 57, 68, -18, 68, -112, -121, 67, -45, -18, 105, -13, -24, 58, -69, 63, 66, 1, -24, -52, -101, 42, 105, -17, 27, -106, 80, 77, 99, -126, 52, -111, 4, -77, -126, -58, -54, -30, 88, 13, 91, 87, 101, 49, -113, -29, -113, -27, -116, 113, -62, -80, -37, -26, 57, -126, 50, 1, 111, 60, -36, 121, -21, -27, 94, -35, -21, 116, -101, -119, -19, 78, -97, -57, -56, -7, 44, 69, 97, -61, -116, 27, -1, -123, -101, 0, -36, -127, -75, -87, -53, 27, -86, -95, -30, -121, -33, 47, 82, -80, 112, -91, -114, -60, -24, 103, 43, -2, -74, -10, -85, -48, 97, 32, 36, -73, 44, -70, -11, -86, 58, 68, 6, -94, 35, 78, 48, -89, -75, -79, -9, -127, 78, -22, 5, -10, -91, -101, -26, 112, -28, 113, 110, -76, 98, -42, 89, 73, -113, 56, -100, 26, 26, 15, -88, 45, -128, -23, -104, -41, -21, -64, -59, 11, -128, 61, -25, 23, -87, -77, 50, -90, 69, 115, 113, -47, 114, -71, 11, -63, 25, 78, -33, -69, -13, 66, -42, -32, -89, 30, -20, -79, 53, 97, -111, 32, -55, -58, -47, -82, 10, 92, 61, -48, 69, 73, -43, 66, 65, -91, -115, 90, -103, 72, 46, 107, -65, -41, 75, -56, -108, -98, 41, 99, -127, -110, 108, 112, 110, -60, 10, -120, 63, -54, 47, 22, -64, 78};
        String s = decrypt(ALGORITHM, KeyManagement.getOwnPrivateKey(path+"/0", KeyManagement.ALGORITHM), arr);
        System.out.println("s = " + s);
    }*/
}

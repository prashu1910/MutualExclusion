/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Prashu
 */
public class KeyManagement {
    
    public static final String ALGORITHM = "RSA";
    private KeyPairGenerator keyGen;
	private KeyPair pair;
	private PrivateKey privateKey;
	private PublicKey publicKey;

	public KeyManagement(int keylength) throws NoSuchAlgorithmException, NoSuchProviderException {
		this.keyGen = KeyPairGenerator.getInstance(ALGORITHM);
		this.keyGen.initialize(keylength,new SecureRandom());
	}

	public void createKeys() {
		this.pair = this.keyGen.generateKeyPair();
		this.privateKey = pair.getPrivate();
		this.publicKey = pair.getPublic();
	}

	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	public void writeToFile(String path, byte[] key) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();
                //f.

		FileOutputStream fos = new FileOutputStream(f);
		fos.write(key);
		fos.flush();
		fos.close();
	}
        
        public static void generateKeys(String publicKeyFolder, String privateKeyFolder)
        {
            KeyManagement gk;
		try 
                {
                    gk = new KeyManagement(2048);
                    gk.createKeys();
                    //Util.println("gk = " + gk.getPublicKey());
                   // Util.println("gk 2  = " + gk.getPrivateKey() );
                    X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(gk.getPublicKey().getEncoded());
                    File dir = new File(publicKeyFolder);
                    dir.mkdirs();
                    FileOutputStream fos = new FileOutputStream(publicKeyFolder + "/public.key");
                    fos.write(x509EncodedKeySpec.getEncoded());
                    fos.close();
                    
                    dir = new File(privateKeyFolder);
                    dir.mkdirs();
                    PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(gk.getPrivateKey().getEncoded());
                    fos = new FileOutputStream(privateKeyFolder + "/private.key");
                    fos.write(pkcs8EncodedKeySpec.getEncoded());
                    fos.close();
                
                    //gk.writeToFile(publicKeyFolder+"/publicKey", gk.getPublicKey().getEncoded());
                    //gk.writeToFile(privateKeyFolder+"/privateKey", gk.getPrivateKey().getEncoded());
		} catch (NoSuchAlgorithmException | NoSuchProviderException | IOException e) {
			System.err.println(e.getMessage());
		}
        }
        
        public static PrivateKey getOwnPrivateKey(String path, String algorithm)
        {
            PrivateKey privateKey = null;
            try 
            {
                File filePrivateKey = new File(path + "/private.key");
                filePrivateKey.getParentFile().mkdirs();
                FileInputStream fis = new FileInputStream(path + "/private.key");
                byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
                fis.read(encodedPrivateKey);
                fis.close();

                KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
                privateKey = keyFactory.generatePrivate(privateKeySpec);
                return privateKey;
            } catch (NoSuchAlgorithmException  | IOException |InvalidKeySpecException ex) {
                System.err.println(ex.getMessage());
            }
                
            return privateKey;
        }
        
        public static PublicKey getNodePublicKey(String path, String algorithm)
        {
            PublicKey publicKey = null;
            try
            {
                
                File filePublicKey = new File(path + "/public.key");
                filePublicKey.getParentFile().mkdirs();
		FileInputStream fis = new FileInputStream(path + "/public.key");
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();
                
                KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		publicKey = keyFactory.generatePublic(publicKeySpec);
            
            } catch (NoSuchAlgorithmException  | IOException |InvalidKeySpecException ex) {
                System.err.println(ex.getMessage());
            }
            return publicKey;       
        }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
/**
 *
 * @author rohith
 */
public class Aes {
    
    private static final String Algo="AES";
    
    private static final byte[] keyValue = new byte[] {'E','n','c','r','y','p','t','u',
        's','i','n','g','A','E','S','.'};
            
    public static String encrypt(String data) throws Exception{
        Key key = generateKey();
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedVal = c.doFinal(data.getBytes());
        encryptedVal = Base64.getEncoder().encode(encryptedVal);
        String encryptedValue = new String(encryptedVal);
        return encryptedValue;
    }
    
    public static String decrypt(String data) throws Exception{
        Key key = generateKey();
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.getDecoder().decode(data.getBytes());
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }
    
    private static Key generateKey() throws Exception{
        Key key = new SecretKeySpec(keyValue,Algo);
        return key;
    }
}

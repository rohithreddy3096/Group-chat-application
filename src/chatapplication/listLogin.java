/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author rohith
 */
public class listLogin {
    private static final ArrayList<String> loginList = new ArrayList<String>();
    private static Hashtable hash = new Hashtable();
    public static void addLogin(String mobile){
        loginList.add(mobile);
    }
    
    public static ArrayList returnList(){
        return loginList;
    }
    
    public static void setHash(String mobile){
        hash.put(mobile,1);
    }
    
    public static boolean retHash(String mobile){
        return hash.containsKey(mobile);
    }
}

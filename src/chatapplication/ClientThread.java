/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication;

import java.awt.List;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author rohith
 */
public class ClientThread extends Thread{
    
    private String clientNo=null;
    private DataInputStream is=null;
    private PrintStream os=null;
    private Socket clientSocket=null;
    private final ClientThread[] threads;
    private final int maxCount;
    
    
    public ClientThread(Socket clientSocket,ClientThread[] threads){
        this.clientSocket=clientSocket;
        this.threads=threads;
        maxCount=threads.length;
    }
    
    public void run(){
        try{
        is=new DataInputStream(clientSocket.getInputStream());
        os=new PrintStream(clientSocket.getOutputStream());
        
        String mobile = is.readLine();
        clientNo = mobile;
        String check = mobile.substring(0,3);
       
        
        if((check.equals("sig"))){
        String flag = checkMobile(mobile.substring(3));
        
        os.println(flag);
        
        String signup=is.readLine();
        String username = is.readLine();
        
        if(signup.equals("success")){
            updateLogin(mobile,username);
        }
        }
        else if(check.equals("log")){
            int flag=0;
            mobile = mobile.substring(3);
            if(listLogin.retHash(mobile)==false){
            flag=1;
            listLogin.setHash(mobile);
            listLogin.addLogin(mobile);
            }
            synchronized(this){
                for(int i=0;i<maxCount;i++){
                    if(threads[i]!=null && threads[i]!=this && flag==1
                            && threads[i].clientNo.substring(0, 3).equals("log"))
                    threads[i].os.println(Aes.encrypt(mobile));
                    
                    else if(threads[i]==this){
                        threads[i].os.println(Aes.encrypt(listLogin.returnList().toString()));
                    }
                }
            }
        } 
        else{
            while(true){
                String msg=is.readLine();
                
                msg=Aes.decrypt(msg);
                
                System.out.println("1 "+msg);
                
                if(msg.equals("Sending File\r\n")){
                String name=is.readLine();
                name = Aes.decrypt(name);
                
                System.out.println("2 "+name);
                
                FileOutputStream fos = new FileOutputStream("test.pdf");
		byte[] buffer = new byte[40096];
		
                int read=0;
		while((read = is.read(buffer, 0, buffer.length))>0){
                        buffer=Aes.decrypt(buffer.toString()).getBytes();
			fos.write(buffer, 0, read);
		}
		
		fos.close();
                }
                else{
                String name = msg.substring(0,10);
                synchronized(this){
                int sent=0,ind=-1;
                for(int i=0;i<maxCount;i++){
                   
                    if(threads[i]!=null && threads[i]!=this
                            && (threads[i].clientNo).equals(name)){
                        sent=1;
                        System.out.println(name+"->"+msg.substring(10));
                        threads[i].os.println(Aes.encrypt(msg.substring(10)));
                    }
                    
                    if(threads[i]!=null && threads[i]!=this && 
                            (threads[i].clientNo).equals("log"+name)){
                        ind=i;
                    }
                }
                if(sent==0 && ind!=-1){
                    msg = this.clientNo+msg.substring(10);
                    threads[ind].os.println(Aes.encrypt(msg));
                }
            }
            }
        }
        }
        } catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public static String checkMobile(String mobile){
        String str = null;
        
        try{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        String fileName = "/home/rohith/Desktop/login.xml";
        File file = new File(fileName);
        Document doc = dBuilder.parse(file);
        
        NodeList nodelist = doc.getElementsByTagName("Person");
        
        for(int i=0;i<nodelist.getLength();i++){
            if(getEmployee(nodelist.item(i)).equals(mobile)){
                str="YES";
            }
            else{
                str="NO";
            }
        }
        
        } catch(Exception e){
            e.printStackTrace();
        }
        return str;
    }
    
    public static void updateLogin(String mobile,String username){
        try{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            
            Document doc = dBuilder.parse("/home/rohith/Desktop/login.xml");
            
            Element root = doc.getDocumentElement();
            
            Element Person = doc.createElement("Person");
            root.appendChild(Person);
            
            Element userName = doc.createElement("mobileNo");
            userName.appendChild(doc.createTextNode(mobile));
            Person.appendChild(userName);
            
            Element passWord = doc.createElement("userName");
            passWord.appendChild(doc.createTextNode(username));
            Person.appendChild(passWord);
            
            TransformerFactory tFact = TransformerFactory.newInstance();
            Transformer tForm = tFact.newTransformer();
            
            DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult("/home/rohith/Desktop/login.xml");

            tForm.transform(source, result);
            
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private static String getEmployee(Node node) throws Exception {
        //XMLReaderDOM domReader = new XMLReaderDOM();
        String str = null;
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            str = getTagValue("mobileNo", element);
        }
        //System.out.println(str);
        return str;
    }
 
 
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }
}

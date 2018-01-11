/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author rohith
 */
public class loadXml {
    public static ArrayList loadMsg(String recvmobile,String sentmobile){
        String str = recvmobile+" from "+sentmobile+".xml";
        ArrayList<String> list = new ArrayList<String>();
        try{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        File file = new File(str);
        if(!file.exists()){
            return list;
        }
        Document doc = dBuilder.parse(file);
        
        NodeList nodelist = doc.getElementsByTagName("Message");
        
        for(int i=0;i<nodelist.getLength();i++){
            list.add(getMessage(nodelist.item(i)));
        }
        
        } catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
    
    private static String getMessage(Node node) throws Exception {
        //XMLReaderDOM domReader = new XMLReaderDOM();
        String str = null;
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            str = getTagValue("Sender", element);
            str = str + getTagValue("Msg", element);
        }
        return str;
    }
 
 
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }
}

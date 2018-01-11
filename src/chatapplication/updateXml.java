/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author rohith
 */
public class updateXml {
    public static void writeToXml(String msg,String sentmobile,String recvmobile,String msgsender){
        String str=recvmobile+" from "+sentmobile+".xml";
        File file = new File(str);
        
        try{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc;
        Element root;
        if(!file.exists()) {
            doc = dBuilder.newDocument();
            root = doc.createElement("Messages");
            doc.appendChild(root);
            
        } else {
            doc = dBuilder.parse(str);
            root = doc.getDocumentElement();
        }
        
            Element Person = doc.createElement("Message");
            root.appendChild(Person);
            
            Element Sender = doc.createElement("Sender");
            Sender.appendChild(doc.createTextNode(msgsender));
            Person.appendChild(Sender);
            
            Element Msg = doc.createElement("Msg");
            Msg.appendChild(doc.createTextNode(Aes.encrypt(msg)));
            Person.appendChild(Msg);
            
            TransformerFactory tFact = TransformerFactory.newInstance();
            Transformer tForm = tFact.newTransformer();
            
            DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(str);

            tForm.transform(source, result);
            
    } catch(Exception e){
    e.printStackTrace();
}
}

}

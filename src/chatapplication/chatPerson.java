/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
/**
 *
 * @author rohith
 */
public class chatPerson {

    private static String sendingmobile;
    private static String sentmobile;
    //Accessing chat client
    private static OutputStream outputstream;
    private static Socket socket;
    
    static class ChatAccess extends Observable{
        @Override
        public void notifyObservers(Object e){
            super.setChanged();
            super.notifyObservers(e);
        }
        
        //Creating socket and receiving thread
        public void initSocket() throws IOException{
            socket = new Socket("127.0.0.1",3000);
            Thread receiving=new Thread(){
                @Override
                public void run(){
                    try{
                    outputstream = socket.getOutputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                    outputstream.write((sendingmobile+"\r\n").getBytes());
                    String next;
                    while((next=reader.readLine())!=null){
                        System.out.println("chatPerson-"+next);
                        notifyObservers(next);
                    }
                    }catch(IOException e){
                       notifyObservers(e); 
                    } catch (Exception ex) {
                        Logger.getLogger(chatPerson.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            receiving.start();
        }
        private String newline="\r\n";
        public void send(String str){
            try{
            
            str=Aes.encrypt(str);
            outputstream.write((str+newline).getBytes());
            outputstream.flush();
            }catch(Exception e){
                notifyObservers(e);
            }
        }
        
        public void close(){
            try{
               socket.close(); 
            }catch(IOException e){
                notifyObservers(e);
            }
        }
    }
    
    static class ChatFrame extends JFrame implements Observer{
        
        private  JTextArea textArea;
        private JTextField textField;
        private JButton sendButton,fileButton,backButton;
        private ChatAccess chatAccess;
        private SimpleAttributeSet keyWord,sp,left;
        private StyledDocument doc;

        public ChatFrame(ChatAccess chatAccess) {
            this.chatAccess = chatAccess;
            chatAccess.addObserver(this);
            buildGUI();
        }

        /** Builds the user interface */
        private void buildGUI() {
            JTextPane tp = new JTextPane();
            
            tp.setBounds(0, 0, 500, 300);
            tp.setPreferredSize(new Dimension(500,300));
            tp.setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
            tp.setEditable(false);
            //tp.setContentType("text/html");
            tp.setMargin(new Insets(2, 2, 2, 2));
            //tp.setBackground(Color.BLACK);
 
            doc = tp.getStyledDocument();

            keyWord = new SimpleAttributeSet();
            StyleConstants.setForeground(keyWord, Color.BLACK);
            StyleConstants.setBackground(keyWord, Color.PINK);
            StyleConstants.setSpaceAbove(keyWord, 7);
            StyleConstants.setSpaceBelow(keyWord, 7);
            StyleConstants.setFontSize(keyWord, 20);
            StyleConstants.setAlignment(keyWord, (int) RIGHT_ALIGNMENT);

            sp = new SimpleAttributeSet();
            StyleConstants.setFontSize(sp, 8);
            
            left=new SimpleAttributeSet();
            StyleConstants.setForeground(left, Color.BLACK);
            StyleConstants.setBackground(left,Color.CYAN);
            StyleConstants.setSpaceAbove(left, 7);
            StyleConstants.setSpaceBelow(left, 7);
            StyleConstants.setFontSize(left, 20);
            
            ArrayList <String> list = loadXml.loadMsg(sendingmobile,sentmobile);
            for(String s : list){
                if(s.substring(0,10).equals(sendingmobile)){
                    try{
                    doc.insertString(doc.getLength(),Aes.decrypt(s.substring(10)), left);
                    doc.insertString(doc.getLength(), "\n\n" , sp);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    try{
                    doc.insertString(doc.getLength(),Aes.decrypt(s.substring(10)), keyWord);
                    doc.insertString(doc.getLength(), "\n" , sp);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
            add(new JScrollPane(tp), BorderLayout.CENTER);
            

            Box box = Box.createHorizontalBox();
            add(box, BorderLayout.SOUTH);
            textField = new JTextField();
            sendButton = new JButton("Send");
            fileButton = new JButton("File");
            backButton = new JButton("Back");
            box.add(textField);
            box.add(sendButton);
            box.add(fileButton);
            box.add(backButton);
            ActionListener sendListener = new ActionListener(){
                
                public void actionPerformed(ActionEvent e){
                if(e.getSource() == sendButton || e.getSource()==textField){
                String str=textField.getText();
                
                if(str!=null && str.trim().length()>0){
                    try {
                        doc.insertString(doc.getLength(),str, left);
                        doc.insertString(doc.getLength(), "\n\n" , sp);
                        updateXml.writeToXml(str,sentmobile,sendingmobile,sendingmobile);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    str=sentmobile+str;
                    chatAccess.send(str);
                }
                textField.setText("");
                }
                
                else if(e.getSource() == backButton){
                    setVisible(false);
                    String[] str = new String[2];
                    str[0]=sendingmobile;
                    new chatHome().main(str);
                }
                
                else if(e.getSource() == fileButton){
                    try{
                    chatAccess.send("Sending File\r\n");
                    //outputstream.write(Aes.encrypt("Sending File\r\n").getBytes());
                    chatAccess.send((sentmobile+"\r\n"));
                    //outputstream.write((sentmobile+"\r\n").getBytes());
                    
                    FileInputStream fis = new FileInputStream("/home/rohith/Downloads/pdfs/Advanced_java.pdf");
                    byte[] buffer = new byte[4096];

                    while (fis.read(buffer) > 0) {
                            chatAccess.send(buffer.toString());
                    }
		
		fis.close();
                    } catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        };
            textField.addActionListener(sendListener);
            sendButton.addActionListener(sendListener);
            fileButton.addActionListener(sendListener);
            backButton.addActionListener(sendListener);
            
            this.addWindowListener(new WindowAdapter(){
               @Override
               public void windowClosing(WindowEvent e){
                   chatAccess.close();
               }
            });
            
        }
        
        public void update(Observable obs,Object obj){
            final Object finObj=obj;
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    try {
                        
                        doc.insertString(doc.getLength(),Aes.decrypt(finObj.toString()),keyWord);
                        doc.insertString(doc.getLength(), "\n" , sp);
                        updateXml.writeToXml(Aes.decrypt(finObj.toString()),
                                sentmobile,sendingmobile,sentmobile);
                        
                        InputStream fileinput= new FileInputStream(new File(
                        "/home/rohith/Downloads/mySoft.wav"));
                        AudioStream audioStream = new AudioStream(fileinput);
                        AudioPlayer.player.start(audioStream);
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        sendingmobile=args[0];
        sentmobile = args[1];
        ChatAccess caccess=new ChatAccess();
        JFrame cframe=new ChatFrame(caccess);
        cframe.setTitle("Chatting with "+sentmobile);
        cframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cframe.pack();
        cframe.setResizable(true);
        cframe.setVisible(true);
        
        try{
            caccess.initSocket();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }
    
}
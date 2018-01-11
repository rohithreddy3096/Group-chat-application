/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import static java.awt.Font.SERIF;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
/**
 *
 * @author rohith
 */
public class chatHome {

    private static Socket socket;
    private static String mobile;
    private static String username;
    //Accessing chat client
  
    static class ChatAccess extends Observable{
        
        private OutputStream outputstream;
        @Override
        public void notifyObservers(Object e){
            super.setChanged();
            super.notifyObservers(e);
        }
        
        //Creating socket and receiving thread
        public void initSocket(String server,int port,String mobile,String userName) throws IOException{
            socket=new Socket(server,port);
            outputstream=socket.getOutputStream();
            outputstream.write(("log"+mobile+"\r\n").getBytes());
            
            Thread receiving=new Thread(){
                @Override
                public void run(){
                    try{
                   String next;
                   BufferedReader reader=new BufferedReader(new InputStreamReader(
                            socket.getInputStream()));

                    next = reader.readLine();
                    next = Aes.decrypt(next);
                    next = next.substring(1);
                    
                    String[] str= next.split(", ");
                    for(String s : str){
                        s=s.substring(0,10);
                        notifyObservers(s);
                    }
                    
                    while((next=reader.readLine())!=null){
                  
                        next=Aes.decrypt(next);
                        if(next.length()==10){
                        notifyObservers(next);
                        }
                        else{
                            updateXml.writeToXml(next.substring(10),next.substring(0,10),
                                    mobile,next.substring(0,10));
                            
                            InputStream fileinput= new FileInputStream(new File(
                            "/home/rohith/Downloads/mySoft.wav"));
                            AudioStream audioStream = new AudioStream(fileinput);
                            AudioPlayer.player.start(audioStream);
                        }
                    }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            receiving.start();
        }
        private String newline="\r\n";
        public void send(String str){
            try{
            outputstream.write((str+newline).getBytes());
            outputstream.flush();
            }catch(IOException e){
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
        
        //private JTextArea textArea;
        private DefaultListModel onlineUsers; 
        private JList fruitList;
        private JTextField textField;
        private JButton sendButton,fileButton;
        private ChatAccess chatAccess;

        public ChatFrame(ChatAccess chatAccess) {
            this.chatAccess = chatAccess;
            chatAccess.addObserver(this);
            buildGUI();
        }

        /** Builds the user interface */
        private void buildGUI() {
            
            Font font = new Font(SERIF, Font.BOLD, 30);
            JLabel label =new JLabel("Online Users");
            label.setFont(font);
            font = new Font(SERIF, Font.ITALIC, 18);
            onlineUsers = new DefaultListModel();
            fruitList = new JList(onlineUsers);
            
            DefaultListCellRenderer renderer = (DefaultListCellRenderer) fruitList.getCellRenderer();
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            
            fruitList.setFont(font);
            fruitList.setPreferredSize(new Dimension(400, 300));
            fruitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
           
            fruitList.setVisibleRowCount(10);
            add(label);
            add(fruitList);
            
            MouseListener sendListener = new MouseAdapter(){
                
                public void mouseClicked(MouseEvent e){
                if(e.getSource() == fruitList){
                String str=fruitList.getSelectedValue().toString();
                String[] string = new String[2];
                string[1]=str;
                string[0]=mobile;
                setVisible(false);
                new chatPerson().main(string);
                }
            }
        };
            fruitList.addMouseListener(sendListener);
            
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
                    onlineUsers.addElement(finObj.toString());
                    fruitList.setSelectedIndex(0);
                }
            });
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        mobile = args[0];
        username = args[1];
        String server = "127.0.0.1";
        int port=3000;
        
        ChatAccess caccess=new ChatAccess();
        JFrame cframe=new ChatFrame(caccess);
        cframe.setTitle("User Chatting is "+mobile);
        cframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cframe.setLayout(new GridBagLayout());
        cframe.pack();
        cframe.setResizable(true);
        cframe.setVisible(true);
        
        try{
            caccess.initSocket(server,port,mobile,username);
        }catch(Exception e){
            System.out.println("Cannot connect to"+server+":"+port);
            e.printStackTrace();
            System.exit(0);
        }
    }
    
}

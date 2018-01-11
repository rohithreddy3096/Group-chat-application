/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author rohith
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    private static Socket client_socket = null;
    private static ServerSocket server_socket = null;
    private static final int maxConnections=15;
    private static final ClientThread[] clientThreads= new ClientThread[maxConnections];
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        server_socket = new ServerSocket(3000);
        
        while(true){
            try{
            client_socket=server_socket.accept();
            int i;
            for(i=0;i<maxConnections;i++){
                if(clientThreads[i]==null){
                    (clientThreads[i]=new ClientThread(client_socket,clientThreads)).start();
                    break;
                }
            }
            if(i==maxConnections){
                PrintStream ps=new PrintStream(client_socket.getOutputStream());
                ps.println("Server is busy.Please try again later");
                ps.close();
                client_socket.close();
            }
           }
            catch(IOException e){
                System.out.println(e);
            }
        }
    }
    
}

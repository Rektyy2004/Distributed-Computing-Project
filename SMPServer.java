import java.net.*;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.*;

public class SMPServer {
    public static void main(String[] args) {
        int serverPort = 12345; 
        // Allows user to pass a dynamic port in the terminal if they want
        if (args.length == 1) serverPort = Integer.parseInt(args[0]); 

        // Advanced: Thread-safe map to store individual message lists for each user
        ConcurrentHashMap<String, Vector<String>> messageStore = new ConcurrentHashMap<>();
        
        // Advanced: Thread pool to manage concurrent clients efficiently
        ExecutorService pool = Executors.newCachedThreadPool();

        try {
            // SSL Integration
            System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "password");
            SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            ServerSocket myConnectionSocket = ssf.createServerSocket(serverPort); 

            System.out.println("Secure SMP Server ready and listening on port " + serverPort);  

            while (true) {  
                System.out.println("Waiting for a secure connection...");
                MyStreamSocket myDataSocket = new MyStreamSocket(myConnectionSocket.accept());
                System.out.println("A client has connected securely!");

                // Hand the connection off to the thread pool manager instead of a raw Thread
                pool.execute(new SMPServerThread(myDataSocket, messageStore));
            } 
        } catch (Exception ex) {
            System.out.println("Server error: " + ex.getMessage());
        } 
    } 
}
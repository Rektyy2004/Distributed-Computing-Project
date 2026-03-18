import java.net.*;
import javax.net.ssl.*;

public class SMPClientHelper {
    private MyStreamSocket mySocket;
    
    public SMPClientHelper(String host, int port) throws Exception {
        
        // For testing, we use the same keystore file as the truststore
        System.setProperty("javax.net.ssl.trustStore", "keystore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
        
        InetAddress serverHost = InetAddress.getByName(host);
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket sslSocket = factory.createSocket(serverHost, port);

        mySocket = new MyStreamSocket(sslSocket);
        System.out.println("Secure network connection established.");
    }
    
    public String sendAndReceive(String request) throws Exception {
        mySocket.sendMessage(request);
        return mySocket.receiveMessage();
    }
    
    public void close() throws Exception {
        mySocket.close();
    }
}
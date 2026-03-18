import java.net.*;
import java.io.*;

public class MyStreamSocket extends Socket {
   private Socket  socket;
   private BufferedReader input;
   private PrintWriter output;

   MyStreamSocket(InetAddress acceptorHost, int acceptorPort ) throws SocketException, IOException{
      socket = new Socket(acceptorHost, acceptorPort );
      setStreams( );
   }

   MyStreamSocket(Socket socket)  throws IOException {
      this.socket = socket;
      setStreams( );
   }

   private void setStreams( ) throws IOException{
      InputStream inStream = socket.getInputStream();
      input = new BufferedReader(new InputStreamReader(inStream));
      OutputStream outStream = socket.getOutputStream();
      output = new PrintWriter(new OutputStreamWriter(outStream));
   }

   public void sendMessage(String message) throws IOException {	
      output.print(message + "\n");   
      output.flush();               
   } 

   public String receiveMessage( ) throws IOException {	
      String message = input.readLine( );  
      return message;
   } 

   public void close( ) throws IOException {	
      socket.close( );
   }
}
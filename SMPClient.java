import java.io.*;
import java.util.Base64;

public class SMPClient {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter server address (press enter for localhost):");
            String host = br.readLine();
            if(host.isEmpty()) host = "localhost";
            
            System.out.println("Enter port (press enter for 12345):");
            String portStr = br.readLine();
            int port = portStr.isEmpty() ? 12345 : Integer.parseInt(portStr);
            
            SMPClientHelper helper = new SMPClientHelper(host, port);
            boolean done = false;
            
            while(!done) {
                System.out.println("\n--- SMP Main Menu ---");
                System.out.println("1. Login");
                System.out.println("2. Upload Message");
                System.out.println("3. Download Specific Message");
                System.out.println("4. Download All Messages");
                System.out.println("5. Logoff");
                System.out.print("Choose an option: ");
                
                String choice = br.readLine();
                String response = "";
                
                switch(choice) {
                    case "1":
                        System.out.print("Enter username: ");
                        String user = br.readLine();
                        System.out.print("Enter password: ");
                        String pass = br.readLine();
                        response = helper.sendAndReceive("100 " + user + " " + pass);
                        break;
                    case "2":
                        System.out.print("Enter message to upload: ");
                        String msg = br.readLine();
                        // Scramble message to Base64 before sending
                        String b64msg = Base64.getEncoder().encodeToString(msg.getBytes());
                        response = helper.sendAndReceive("200 " + b64msg);
                        break;
                    case "3":
                        System.out.print("Enter message ID (e.g., 0, 1): ");
                        String id = br.readLine();
                        response = helper.sendAndReceive("300 " + id);
                        break;
                    case "4":
                        response = helper.sendAndReceive("400 ");
                        break;
                    case "5":
                        response = helper.sendAndReceive("500 ");
                        helper.close();
                        done = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                        continue;
                }
                
                // Decode Base64 responses if it's a downloaded message
                if (response.startsWith("301") || response.startsWith("401")) {
                    String b64Payload = response.substring(4).trim();
                    String decodedPlaintext = new String(Base64.getDecoder().decode(b64Payload));
                    response = response.substring(0, 4) + "\n" + decodedPlaintext;
                }
                
                System.out.println("\n>> Server Response: " + response);
            }
        } catch(Exception e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }
}
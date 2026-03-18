import javax.swing.JOptionPane;
import java.util.Base64;

public class SMPClientGUI {
    public static void main(String[] args) {
        try {
            String host = JOptionPane.showInputDialog("Enter server address (or leave blank for localhost):", "localhost");
            if (host == null) return; 
            
            String portStr = JOptionPane.showInputDialog("Enter port (or leave blank for 12345):", "12345");
            if (portStr == null) return;
            int port = portStr.isEmpty() ? 12345 : Integer.parseInt(portStr);
            
            SMPClientHelper helper = new SMPClientHelper(host, port);
            boolean done = false;
            
            while (!done) {
                String[] options = {"1. Login", "2. Upload", "3. Download Specific", "4. Download All", "5. Logoff"};
                String choice = (String) JOptionPane.showInputDialog(null, "Select an action:", "SMP Main Menu", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                
                if (choice == null) break;
                String response = "";
                
                if (choice.startsWith("1")) {
                    String user = JOptionPane.showInputDialog("Enter username:");
                    String pass = JOptionPane.showInputDialog("Enter password:");
                    response = helper.sendAndReceive("100 " + user + " " + pass);
                } else if (choice.startsWith("2")) {
                    String msg = JOptionPane.showInputDialog("Enter message to upload:");
                    // Scramble message to Base64 before sending
                    String b64msg = Base64.getEncoder().encodeToString(msg.getBytes());
                    response = helper.sendAndReceive("200 " + b64msg);
                } else if (choice.startsWith("3")) {
                    String id = JOptionPane.showInputDialog("Enter message ID (e.g., 0):");
                    response = helper.sendAndReceive("300 " + id);
                } else if (choice.startsWith("4")) {
                    response = helper.sendAndReceive("400 ");
                } else if (choice.startsWith("5")) {
                    response = helper.sendAndReceive("500 ");
                    helper.close();
                    done = true;
                }
                
                // Decode Base64 responses if it's a downloaded message
                if (response.startsWith("301") || response.startsWith("401")) {
                    String b64Payload = response.substring(4).trim();
                    String decodedPlaintext = new String(Base64.getDecoder().decode(b64Payload));
                    response = response.substring(0, 4) + "\n" + decodedPlaintext;
                }
                
                JOptionPane.showMessageDialog(null, "Server Response:\n" + response, "Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Client Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
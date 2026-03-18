import java.util.Vector;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

class SMPServerThread implements Runnable {
    MyStreamSocket myDataSocket;
    ConcurrentHashMap<String, Vector<String>> messageStore;
    boolean isAuthenticated = false; 
    String loggedInUser = ""; // Track who is logged in

    SMPServerThread(MyStreamSocket myDataSocket, ConcurrentHashMap<String, Vector<String>> messageStore) {
        this.myDataSocket = myDataSocket;
        this.messageStore = messageStore;
    }

    public void run() {
        boolean done = false;
        String incomingMessage;

        try {
            while (!done) {
                incomingMessage = myDataSocket.receiveMessage();
                if (incomingMessage == null) break; 

                System.out.println("Received from client: " + incomingMessage);

                String command = incomingMessage.length() >= 3 ? incomingMessage.substring(0, 3) : "";
                String payload = incomingMessage.length() > 4 ? incomingMessage.substring(4).trim() : "";

                switch (command) {
                    case "100": 
                        if (!payload.isEmpty() && payload.contains(" ")) {
                            isAuthenticated = true;
                            loggedInUser = payload.split(" ")[0]; // Extract the username
                            // Create a blank inbox for them if they don't have one yet
                            messageStore.putIfAbsent(loggedInUser, new Vector<>()); 
                            myDataSocket.sendMessage("101 Login successful");
                        } else {
                            myDataSocket.sendMessage("102 Login failed (invalid format)");
                        }
                        break;
                    case "200": 
                        if (isAuthenticated) {
                            try {
                                // Decode the Base64 message to plain text before saving
                                String decodedMsg = new String(Base64.getDecoder().decode(payload));
                                messageStore.get(loggedInUser).add(decodedMsg);
                                myDataSocket.sendMessage("201 Upload successful");
                            } catch (Exception e) {
                                myDataSocket.sendMessage("202 Upload failed (Invalid Base64 format)");
                            }
                        } else {
                            myDataSocket.sendMessage("202 Upload failed (Please login first)");
                        }
                        break;
                    case "300": 
                        if (isAuthenticated) {
                            try {
                                int id = Integer.parseInt(payload);
                                Vector<String> userMsgs = messageStore.get(loggedInUser);
                                if (id >= 0 && id < userMsgs.size()) {
                                    // Encode back to Base64 for safe network transport
                                    String encoded = Base64.getEncoder().encodeToString(userMsgs.get(id).getBytes());
                                    myDataSocket.sendMessage("301 " + encoded);
                                } else {
                                    myDataSocket.sendMessage("302 Message not found");
                                }
                            } catch (Exception e) {
                                myDataSocket.sendMessage("302 Invalid ID format");
                            }
                        } else {
                            myDataSocket.sendMessage("302 Download failed (Please login first)");
                        }
                        break;
                    case "400": 
                        if (isAuthenticated) {
                            Vector<String> userMsgs = messageStore.get(loggedInUser);
                            if (userMsgs.isEmpty()) {
                                myDataSocket.sendMessage("402 No messages available");
                            } else {
                                StringBuilder allMessages = new StringBuilder();
                                for (int i = 0; i < userMsgs.size(); i++) {
                                    allMessages.append("[").append(i).append("] ").append(userMsgs.get(i)).append("\n");
                                }
                                // Encode the whole compiled list in Base64
                                String encodedAll = Base64.getEncoder().encodeToString(allMessages.toString().getBytes());
                                myDataSocket.sendMessage("401 " + encodedAll);
                            }
                        } else {
                            myDataSocket.sendMessage("402 Download failed (Please login first)");
                        }
                        break;
                    case "500": 
                        myDataSocket.sendMessage("501 Goodbye");
                        System.out.println("Client logged off.");
                        done = true;
                        break;
                    default:
                        myDataSocket.sendMessage("999 Unknown command code");
                }
            }
            myDataSocket.close();
        } catch (Exception ex) {
            System.out.println("Thread error: " + ex.getMessage());
        }
    }
}
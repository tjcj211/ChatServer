/******
 * ChatServer
 * Author: Timothy Carta, Ryan Hayes
 *
 * This code provides the chatserver functionality. Clients can send
 * and recieve messages by connecting to the server. The clients are able to send
 * ENTER [Username]
 * EXIT
 * JOIN [Room Name/Number]
 * TRANSMIT [Message]
 *
 ******/

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class ChatServer {
    public static final int DEFAULT_PORT = 1518;
    public int port; // Port for this server
    public boolean done; //Is the server running?
    public HashSet<Connection> connection;   // The set of client connections
    public File log = new File("log.txt"); // Used to log all interactions between clients and server
    public PrintWriter logWriter;

    private class Connection extends Thread {
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        boolean done; //Is the client still connected
        String name; //What is the name/username of the client
        String room; //What room is the client in

        // Constructor
        public Connection(Socket _socket, String _name, String _room) {
            this.socket = _socket;
            done = false;
            name = _name;
            room = _room;
        }

        // Try to run the thread for this connection
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (!done) {
                    String line = in.readLine();
                    processLine(line);
                }
            } catch (IOException err) {
                System.out.println("    ERROR:" + err.getMessage());
            }

            //Close the socket and I/O streams associated with it
            try {
                System.out.println("The Client is closing down.");
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch(IOException err) {
                System.out.println("Error trying to close the socket. " + err.getMessage());
            }
        }

        // Process line that has been sent through the connection
        private void processLine(String line) {
            if (line != null) {
            	if (line.startsWith("ENTER ")) { // The client enters with the given name and all other clients are notified
            		changeName(this, line.substring(6));
            		processLine("ACK ENTER " + this.name);
            		processLine("ENTERING " + this.name);
            		
            	} else if (line.contentEquals("EXIT")) { // The client is disconnected from the server and all other clients are notified
                    logWriter.println(this.name + " is exiting");
                    logWriter.flush();
            		processLine("EXITING " + this.name);
                    done = true;
            		
            	} else if (line.startsWith("JOIN ")) { // The client joins the given room
                    processLine("EXITING " + this.name);
            		this.setRoom(this, line.substring(5));
            		processLine("ACK JOIN " + this.room);
                    processLine("ENTERING " + this.room);
            	
            	} else if (line.startsWith("TRANSMIT ")) { // The client is transmitting a message
            		processLine("NEWMESSAGE " + this.name + " " + line.substring(9));
            		
            	} else if (line.startsWith("ACK JOIN ")) { // Send an acknowledgement to the client what room they have joined
            		out.println("You have joined room " + line.substring(9));
            		
            	} else if (line.startsWith("ACK ENTER ")) { // Send an acknowledgement to the user that they have entered the chat room with their provided name
            		out.println("You have been registered with the name " + line.substring(10));
            		
            	} else if (line.startsWith("NEWMESSAGE ")) { //Sends message to all clients in the same room as the client
            		String[] array = line.split(" ");
        			String message = "[" + this.room + "] <" + array[1] + "> " + line.substring(line.indexOf(array[2]));
                    logWriter.println(message);
                    logWriter.flush();
            		for (Connection client : connection) {
            			if (client.room.equals(this.room)) {
            				client.out.println(message);
            			}
            		}
            		
            	} else if (line.startsWith("ENTERING ")) { // Notifies all clients in the same room as the client that they have entered
                    logWriter.println(this.name + " has entered room " + this.room);
                    logWriter.flush();
            		for (Connection client : connection) {
            			if (client.room.equals(this.room) && this.name != client.name) {
            				client.out.println(this.name + " has entered the room");
            			}
            		}
            	} else if (line.startsWith("EXITING ")) { // Notifies all clients in the same room as the client that they have left
                    logWriter.println(this.name + " has left room " + this.room);
                    logWriter.flush();
            		//Send to all clients that the client has left
            		for (Connection client : connection) {
            			if (client.room.equals(this.room)) {
                            if (client.name == this.name) {
                                client.out.println("You have left room " + this.room);
                            } else {
                                client.out.println(this.name + " has left the room"); 
                            }
            			}
            		}
            	} else {
            		out.println("Invalid input");
            		
            	}
            	//Sends this for debugging
                //System.out.println("Line from client: " + line);
               
                /*
                for (Connection client : connection) { //Iterate through clients and send the message.
                    client.out.println(line);
                }
                out.println("Message Recieved.");
                */
            }
        }
        
        // Change the username of the client
        public void changeName(Connection _connection, String _name) {
        	_connection.name = _name;
        }
        
        // Change the room of the client
        public void setRoom(Connection _connection, String _room) {
        	_connection.room = _room;
        }

    } /* End Connection Class */

    // Constructor
    public ChatServer(int _port) {
        connection = new HashSet<Connection>();
        this.port = _port;
        try {
            logWriter = new PrintWriter(log); // Used to log interactions between client and server 
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // Add a connection to a new client
    public void addConnection(Socket clientSocket, String name, String room) {
        Connection c = new Connection(clientSocket, name, room);
        connection.add(c);
        c.start();    // Start the thread.
    }

    //Run the server and listen for connections
    public void run() {
        System.out.println("Starting Server on port... " + port);
        logWriter.println("Starting Server on port... " + port);
        logWriter.flush();
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (!done) {
                Socket clientSocket = serverSocket.accept();
                addConnection(clientSocket, "", "0");
            }
            // Close the socket and the logger if the server stops
            if (serverSocket != null) serverSocket.close();
            if (logWriter != null) logWriter.close();
        } catch (Exception e) {
            System.err.println("ABORTING: An error occurred while creating server socket. " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        // Create and start the server
        ChatServer server = new ChatServer(port);
        server.run();
    }
} /* End ChatServer Class */
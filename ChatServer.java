import java.io.BufferedReader;
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

    private class Connection extends Thread {
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        boolean done; //Is the client still connected
        String name;

        // Constructor
        public Connection(Socket _socket, String _name) {
            this.socket = _socket;
            done = false;
            name = _name;
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
            	if (line.startsWith("ENTER ")) {
            		System.out.println("Entered");
            	} else if (line.contentEquals("EXIT")) {
            		
            	} else if (line.startsWith("JOIN ")) {
            		
            	} else if (line.startsWith("TRANSMIT ")) {
            		
            	} else if (line.startsWith("ACK JOIN ")) {
            		
            	} else if (line.startsWith("ACK ENTER ")) {
            		
            	} else if (line.startsWith("NEWMESSAGE ")) {
            		
            	} else if (line.startsWith("ENTERING ")) {
            		
            	} else if (line.startsWith("EXITING ")) {
            		
            	} else {
            		System.out.println("Invalid input");
            	}
            	//Sends this for debugging
                System.out.println("Line from client: " + line);
                for (Connection client : connection) { //Iterate through clients and send the message.
                    client.out.println(line);
                }
                out.println("Message Recieved.");
            }
        }
    } /* End Connection Class */

    // Constructor
    public ChatServer(int _port) {
        connection = new HashSet<Connection>();
        this.port = _port;
    }

    // Add a connection to a new client
    public void addConnection(Socket clientSocket, String name) {
        Connection c = new Connection(clientSocket, name);
        connection.add(c);
        c.start();    // Start the thread.
    }

    //Run the server and listen for connections
    public void run() {
        System.out.println("Starting Server on port... " + port);
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (!done) {

                Socket clientSocket = serverSocket.accept();
                addConnection(clientSocket, "");
            }
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
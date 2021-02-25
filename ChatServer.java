import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    public static final int DEFAULT_PORT = 1518;
    public int port; // Port for this server
    public boolean done; //Is the server running?

    private class Connection extends Thread {
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        boolean done; //Is the client still connected

        // Constructor
        public Connection(Socket _socket) {
            this.socket = _socket;
            done = false;
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
                System.out.println("Line from client: " + line);
                out.println("Message Recieved.");
            }
           
        }
    } /* End Connection Class */

    // Constructor
    public ChatServer(int _port) {
        this.port = _port;
    }

    // Add a connection to a new client
    public void addConnection(Socket clientSocket) {
        Connection c = new Connection(clientSocket);
        c.start();    // Start the thread.
    }

    //Run the server and listen for connections
    public void run() {
        System.out.println("Starting Server on port... " + port);
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (!done) {

                Socket clientSocket = serverSocket.accept();
                addConnection(clientSocket);
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
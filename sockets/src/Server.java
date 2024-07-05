import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {
    private ServerSocket serverSocket;

    public Server(){
        try {
            this.serverSocket = new ServerSocket(8000);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
            while( !this.serverSocket.isClosed() ){
                Socket socket = this.serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                out.println("Welcome to java socket chat room.");
                out.println("Enter your name for chat room that will be visible to everyone:");
                String clientName = in.readLine().trim();
                ClientHandler clientHandler = new ClientHandler(socket, clientName);
                Thread thread = new Thread(clientHandler);
                thread.start();
                System.out.println( clientName + "Connected to chat");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    class ClientHandler implements Runnable{
        private static ArrayList<ClientHandler> clients = new ArrayList<>();
        private Socket socket;
        private String clientName;

        public ClientHandler(Socket socket, String clientName){
            this.socket = socket;
            this.clientName = clientName;
            clients.add(this);
            String msg = clientName + " Has joined the chat";
            broadcastToAllClients(msg);
        }

        public void broadcastToAllClients(String msg){
            try {
                PrintWriter out;
                for(ClientHandler client: clients){
                    out = new PrintWriter(new OutputStreamWriter(client.socket.getOutputStream()), true);
                    out.println(msg);
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }

        }

        public void broadcastFromClient(String msg){
            try {
                PrintWriter out;
                for(ClientHandler client: clients){
                    if(client != this) {
                        out = new PrintWriter(new OutputStreamWriter(client.socket.getOutputStream()), true);
                        out.println(msg);
                    }
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            try{
                if(!this.socket.isClosed()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                    String msgFromClient = null;
                    while ( (msgFromClient = in.readLine()) != null ) {
                        if(msgFromClient.equalsIgnoreCase("/quit")){
                            this.socket.close();
                            clients.remove(this);
                            broadcastToAllClients(this.clientName + " has left the chat");
                            return;
                        }
                        else {
                            broadcastFromClient((this.clientName + ": " + msgFromClient));
                        }
                    }
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        Thread thread = new Thread(server);
        thread.start();
        System.out.println("Server started");
    }
}

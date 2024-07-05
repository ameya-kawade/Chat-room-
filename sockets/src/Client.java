import java.io.*;
import java.net.Socket;

public class Client implements Runnable{
    private Socket socket;

    public Client(){
        try {
            this.socket = new Socket("localhost", 8000);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg){
        try {
            if( !this.socket.isClosed() ){
                PrintWriter out = new PrintWriter( new OutputStreamWriter( this.socket.getOutputStream() ), true);
                out.println(msg);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            if(!this.socket.isClosed()){
                BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                String msgFromServer = null;
                while( (msgFromServer = in.readLine()) != null ){
                    System.out.println(msgFromServer.trim());
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        Thread thread = new Thread(client);
        thread.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String clientInput = null;
        try {
            while( (clientInput = in.readLine()) != null ){
                client.sendMessage(clientInput.trim());
                if( clientInput.trim().equals("/quit") ){
                    break;
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}

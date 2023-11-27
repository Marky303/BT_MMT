import java.net.ServerSocket;

public class App {
    public static void main(String[] args) throws Exception 
    {
        // Starting CLI
        System.out.println("Client application Started");
        ServerSocket bashSocket = new ServerSocket(1232);
        CommandController cli = new CommandController(bashSocket);
        Thread cliThread = new Thread(cli);
        cliThread.start();
    }
}

import java.net.ServerSocket;
import java.util.ArrayList;

public class App 
{
    public static void main(String[] args) throws Exception 
    {
        // Informing user
        System.out.println("___________________________________________________________");
        System.out.println("Server application started");
        
        // Setting up database
        // List<String> clientNameList = new ArrayList<>(); 

        // Client handler list
        ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
        ArrayList<Thread> clientHandlerThreads = new ArrayList<>();

        // Setting up server (create a new thread)
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket,clientHandlers,clientHandlerThreads);
        Thread serverThread = new Thread(server);
        serverThread.start();
        
        // Setting up CLI (create a new thread)
        ServerSocket bashSocket = new ServerSocket(1233);
        CommandController cli = new CommandController(clientHandlers,bashSocket,serverThread,server,clientHandlerThreads);
        Thread cliThread = new Thread(cli);
        cliThread.start();
    }
}

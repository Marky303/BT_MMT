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

        // Setting up server (create a new thread)
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket,clientHandlers);
        Thread serverThread = new Thread(server);
        serverThread.start();
        
        // Setting up CLI (create a new thread)
        CommandController cli = new CommandController(clientHandlers);
        Thread cliThread = new Thread(cli);
        cliThread.start();
        

        
        
        
        // System.out.println("Server application started");
        // CommandController cmd = new CommandController();
        // cmd.inputController();
    }
}

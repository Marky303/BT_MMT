import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception 
    {
        // Local file database
        ArrayList<String> fileList = new ArrayList<>();
        List<String> fileLocationList = new ArrayList<>();

        // Client handler list
        ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
        ArrayList<Thread> clientHandlerThreads = new ArrayList<>();

        // Setting up server (create a new thread)
        ServerSocket serverSocket = new ServerSocket(1235);
        Server server = new Server(serverSocket,clientHandlers,clientHandlerThreads,fileList,fileLocationList);
        Thread serverThread = new Thread(server);
        serverThread.start();

        // Starting CLI
        System.out.println("Client application Started");
        ServerSocket bashSocket = new ServerSocket(1232);
        CommandController cli = new CommandController(bashSocket,fileList,fileLocationList);
        Thread cliThread = new Thread(cli);
        cliThread.start();
    }
}

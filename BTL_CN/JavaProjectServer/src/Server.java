import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.Socket;

public class Server implements Runnable 
{
    // Setting up necessary objects 
    private ServerSocket serverSocket;
    public ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    // Constructor
    public Server(ServerSocket serverSocket, ArrayList<ClientHandler> clientHandlers)
    {
        this.serverSocket = serverSocket;
        this.clientHandlers = clientHandlers;
    }

    // Start server method (create a new thread thingy)
    @Override
    public void run()
    {
        try 
        {
            while (!serverSocket.isClosed())
            {
                // Accepting new client
                Socket socket = serverSocket.accept(); // Blocking method
                //System.out.println("New client");

                // Starting a new clientHandler thread to listen from newly accepted client
                ClientHandler clientHandler = new ClientHandler(socket,clientHandlers);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }
        catch (IOException e)
        {
            
        }
    }

    // Close server method
    public void closeServerSocket()
    {
        try
        {
            if (serverSocket!=null)
                serverSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }



}
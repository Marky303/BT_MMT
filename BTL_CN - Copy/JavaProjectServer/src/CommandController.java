import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;   

public class CommandController implements Runnable
{
    // Bash reader and writer thingy
    private String input;
    ServerSocket bashsocket;
    private Socket bash;
    private BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter; 

    // Database
    public ArrayList<ClientHandler> clientHandlers;
    public ArrayList<Thread> clientHandlerThreads;

    // Holding server thread as a hostage because i said so
    Thread serverThread;
    Server server;
    
    // Constructor
    public CommandController(ArrayList<ClientHandler> clientHandlers, ServerSocket bashSocket, Thread serverThread, Server server, ArrayList<Thread> clientHandlerThreads)
    {

        // Referencing client database
        this.clientHandlers = clientHandlers;
        this.clientHandlerThreads = clientHandlerThreads;

        // Referencing client serversocket for bash command
        this.bashsocket = bashSocket;

        // Referencing the hostage
        this.serverThread = serverThread;
        this.server = server;
        return;
    }

    // CLI method
    public void run()
    {
        // Getting user input
        input = "start";
        while (!(input).equals("stop"))
        {
            if (!input.equals("start"))
            {
                System.out.println("Command is: "+input); //Test
                // Splitting into arguments
                String args[] = input.split("\\s+",2);
                // Cases switch
                switch(args[0])
                {
                    // Discover command
                    case "discover":
                        // Syntax check
                        if (args.length==1)
                        {
                            try 
                            {
                                bufferedWriter.write("No discover target entered");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            break;
                        }
                        // Execution depends on arg passed
                        if (args[1].equals("allClient"))
                        {
                            discoverClientList();
                        }
                        else if (args[1].equals("allFile"))
                        {
                            discoverClientFiles();
                        }
                        else
                            discoverClientFile(args[1]);
                        break;

                    // Ping command
                    case "ping":
                        // Syntax check
                        if (args.length==1)
                        {
                            try 
                            {
                                bufferedWriter.write("No ping target entered");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            break;
                        }
                        // Execution depends on arg passed
                        pingClient(args[1]);
                        break;

                    // Other commands
                    default:
                    try 
                    {
                        bufferedWriter.write("Unidentified command, please re-enter");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            // Checking for new connection, init reader and writer, get input
            try 
            {
                do
                {
                    // Close everything
                    if (bash!=null&&!bash.isClosed())
                        bash.close();
                    if (bufferedReader!=null)
                        bufferedReader.close();
                    if (bufferedWriter!=null)
                        bufferedWriter.close();
                    // Reader
                    this.bash = bashsocket.accept();
                    this.bufferedReader = new BufferedReader(new InputStreamReader(this.bash.getInputStream()));
                    this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.bash.getOutputStream()));
                    input = bufferedReader.readLine();
                }
                while (input==null); 
            }
            catch (IOException e)
            { 
                e.printStackTrace();
            }
            // Separators
            System.out.println("___________________________________________________________");
        
        }
        // Disconnecting procedures
        // Stopping clientHandlers and threads
        if (clientHandlerThreads.size()!=0)
        {
            for (Thread clientHandlerThread : clientHandlerThreads)
            {
                clientHandlerThread.interrupt();
            }
        }
        while (clientHandlers.size()!=0)
        {
            clientHandlers.get(0).closeEverything(clientHandlers.get(0).socket,clientHandlers.get(0).bufferedReader,clientHandlers.get(0).bufferedWriter);;
        }
        // Stopping server
        serverThread.interrupt();
        server.closeServerSocket();
        // Stopping CLI
        try 
        {
            bufferedWriter.write("Server stopped successfully");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            // Close everything
            if (bash!=null&&!bash.isClosed())
                bash.close();
            if (bufferedReader!=null)
                bufferedReader.close();
            if (bufferedWriter!=null)
                bufferedWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }   

    // Other methods
    private void discoverClientList()
    {
        // Printing list of connected clients
        try
        {
            bufferedWriter.write("There are "+clientHandlers.size()+" connected clients.");
            bufferedWriter.newLine();
            if (clientHandlers.size()!=0)
            {
                bufferedWriter.write("List of connected client:");
                bufferedWriter.newLine();
                for (ClientHandler clientHandler : clientHandlers)
                {
                    bufferedWriter.write("| "+clientHandler.clientName);
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return;
    }

    private void discoverClientFiles()
    {
        // Printing list of connected clients
        try
        {
            if (clientHandlers.size()!=0)
            {
                bufferedWriter.write("Displaying all client files");
                bufferedWriter.newLine();
                for (ClientHandler clientHandler : clientHandlers)
                {
                    bufferedWriter.write("=> "+clientHandler.clientName+" ("+clientHandler.clientFile.size()+" files)");
                    bufferedWriter.newLine();
                    for (String fname : clientHandler.clientFile)
                    {
                        bufferedWriter.write("| "+fname);
                        bufferedWriter.newLine();
                    }
                }
            }
            else 
            {
                bufferedWriter.write("No connected client.");
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return;
    }

    private void discoverClientFile(String clientName)
    {
        // Printing list of connected clients and files respectively
        try
        {
            if (clientHandlers.size()!=0)
            {
                for (ClientHandler clientHandler : clientHandlers)
                {
                    if (clientName.equals(clientHandler.clientName))
                    {
                        bufferedWriter.write("=> "+clientHandler.clientName+" ("+clientHandler.clientFile.size()+" files)");
                        bufferedWriter.newLine();
                        for (String fname : clientHandler.clientFile)
                        {
                            bufferedWriter.write("| "+fname);
                            bufferedWriter.newLine();
                        }
                        bufferedWriter.flush();
                        return;
                    }
                }
                bufferedWriter.write("Cannot find client.");
                bufferedWriter.newLine();
            }
            else 
            {
                bufferedWriter.write("No connected client.");
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return;
    }

    private void pingClient(String clientName)
    {
        try
        {
            // Printing client's list of files
            bufferedWriter.write("Pinging client: "+clientName);
            bufferedWriter.newLine();
            for (ClientHandler clientHandler : clientHandlers)
            {
                if (clientHandler.clientName.equals(clientName))
                {
                    clientHandler.ping(bufferedWriter);
                    bufferedWriter.flush();
                    return;
                }
            }
            bufferedWriter.write("Cannot find client.");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return;
    }
}

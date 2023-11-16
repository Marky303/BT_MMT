import java.util.*;   

public class CommandController implements Runnable
{
    // Scanner
    Scanner scan;
    String input;

    // Setting up database
    public ArrayList<ClientHandler> clientHandlers;
    
    // Constructor
    public CommandController(ArrayList<ClientHandler> clientHandlers)
    {
        // Setting up scanner
        this.scan = new Scanner(System.in);

        // Referencing client database
        this.clientHandlers = clientHandlers;
        return;
    }

    // CLI method
    public void run()
    {
        // Getting user input
        while (!((input=scan.nextLine()).equals("stop")))
        {
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
                        System.out.println("No discover target entered");
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
                        System.out.println("No ping target entered");
                        break;
                    }
                    // Execution depends on arg passed
                    pingClient(args[1]);
                    break;

                // Other commands
                default:
                    System.out.println("Unidentified command, please re-enter");
            }

            // Separators
            System.out.println("___________________________________________________________");
        
        }

        // Disconnecting procedures
        System.out.println("Stopping program");

    }   

    // Other methods
    private void discoverClientList()
    {
        // Printing list of connected clients
        System.out.println("There are "+clientHandlers.size()+" connected clients.");
        if (clientHandlers.size()!=0)
        {
            System.out.println("List of connected clients:");
            for (ClientHandler clientHandler : clientHandlers)
            {
                System.out.println("| "+clientHandler.clientName+" ("+clientHandler.clientAddr.toString().replace("/","")+")");
            }
        }
        return;
    }

    private void discoverClientFiles()
    {
        // Printing list of connected clients
        if (clientHandlers.size()!=0)
        {
            System.out.println("Displaying all client files.");
            for (ClientHandler clientHandler : clientHandlers)
            {
                System.out.println("=> "+clientHandler.clientName+" ("+clientHandler.clientFile.size()+" files)");
                for (String fname : clientHandler.clientFile)
                {
                    System.out.println("| "+fname);
                }
            }
        }
        else 
        {
            System.out.println("No connected client.");
        }
        return;
    }

    private void discoverClientFile(String clientName)
    {
        // Printing list of connected clients and files respectively
        if (clientHandlers.size()!=0)
        {
            for (ClientHandler clientHandler : clientHandlers)
            {
                if (clientName.equals(clientHandler.clientName))
                {
                    System.out.println("=> "+clientHandler.clientName+" ("+clientHandler.clientFile.size()+" files)");
                    for (String fname : clientHandler.clientFile)
                    {
                        System.out.println("| "+fname);
                    }
                    return;
                }
            }
            System.out.println("Cannot find client");
        }
        else 
        {
            System.out.println("No connected client.");
        }
        return;
    }

    private void pingClient(String clientName)
    {
        // Printing client's list of files
        System.out.println("Pinging client: "+clientName);
        for (ClientHandler clientHandler : clientHandlers)
        {
            if (clientHandler.clientName.equals(clientName))
            {
                clientHandler.ping();
                return;
            }
        }
        System.out.println("Cannot find client");
        return;
    }
}

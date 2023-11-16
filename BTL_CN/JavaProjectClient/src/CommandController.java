import java.util.*;   
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommandController implements Runnable
{
    // Necessary assets
    Scanner scan = new Scanner(System.in);
    String input;
    Client client;
    
    // Constructor
    public CommandController()
    {
        try
        {
            // Setting up client object
            Socket socket = new Socket("localhost",1234);
            System.out.println("Please insert client name: ");
            String clientName = scan.nextLine();
            this.client = new Client(socket,clientName);
            System.out.println("___________________________________________________________");
            
            // Start new clientThread
            Thread clientThread = new Thread(this.client);
            clientThread.start();
        }
        catch (UnknownHostException e)
        {

        }
        catch (IOException e)
        {

        }
    }

    // Create new thread
    public void run()
    {
        // Getting user input
        while (!((input=scan.nextLine()).equals("stop")))
        {
            // Splitting into arguments
            String args[] = input.split("\\s+",3);

            // Cases switch
            switch(args[0])
            {
                case "publish":
                    if (args.length==1)
                    {
                        System.out.println("No local address and file name entered (Invalid Syntax)");
                        break;
                    }
                    else if (args.length==2)
                    {
                        System.out.println("Missing variables, please re-enter");
                        break;
                    }
                    publish(args[1], args[2]);
                    break;

                case "fetch":
                    fetch(args[1]);
                    break;

                default:
                    System.out.println("Unidentified command, please re-enter");
            }
            System.out.println("___________________________________________________________");
        }
        // Disconnecting procedures
        System.out.println("Stopping program");
    }   

    // Other methods
    private void publish(String lname, String fname)
    {
        // Checking args validity
        if (!fname.matches("[a-zA-Z0-9_.-]+"))
        {
            System.out.println("File name is not acceptable");
            return;
        }

        if (!lname.matches("([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?"))
        {
            System.out.println("File address is not acceptable");
            return;
        }

        // Check if file exists
        File f = new File(lname+"\\"+fname);
        if(f.exists()) 
        { 
            //Upload file info to server
            client.upload(lname,fname);
        }
        else 
        {
            System.out.println("File doesnt exists");
        }
        return;
    }

    private void fetch(String fname)
    {
        // Checking args validity
        if (!fname.matches("[a-zA-Z0-9_.-]+"))
        {
            System.out.println("File name is not acceptable");
            return;
        }


        

    }
}

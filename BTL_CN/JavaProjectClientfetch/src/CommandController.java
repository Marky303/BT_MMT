 import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommandController implements Runnable
{
    // Bash reader and writer thingy
    private String input;
    ServerSocket bashsocket;
    private Socket bash;
    private BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter; 
    
    // CLient object
    Client client;
    
    // Constructor
    public CommandController(ServerSocket bashSocket)
    {
        // Referencing bashSocket
        this.bashsocket = bashSocket;

        // Setting up client object
        try
        {
            // Setting up client object
            Socket socket = new Socket("localhost",1234);
            this.client = new Client(socket);
            
            // Start new clientThread
            Thread clientThread = new Thread(this.client);
            clientThread.start();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // Create new thread
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
                String args[] = input.split("\\s+",3);

                // Cases switch
                switch(args[0])
                {
                    case "publish":
                        if (args.length==1)
                        {
                            try
                            {
                                bufferedWriter.write("No local address and file name entered (Invalid Syntax)");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            break;
                        }
                        else if (args.length==2)
                        {
                            try
                            {
                                bufferedWriter.write("Missing variables, please re-enter");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            break;
                        }
                        publish(args[1], args[2]);
                        break;

                    case "fetch":
                        fetch(args[1]);
                        break;

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
        System.out.println("Stopping program");
    }   

    // Other methods
    private void publish(String lname, String fname)
    {
        // Checking args validity
        if (!fname.matches("[a-zA-Z0-9_.-]+"))
        {
            try
            {
                bufferedWriter.write("File name is not acceptable");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return;
        }

        if (!lname.matches("([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?"))
        {
            try
            {
                bufferedWriter.write("File address is not acceptable");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return;
        }

        // Check if file exists
        File f = new File(lname+"\\"+fname);
        if(f.exists()) 
        { 
            //Upload file info to server
            client.upload(lname,fname,bufferedWriter);
            try
            {
                bufferedWriter.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else 
        {
            try
            {
                bufferedWriter.write("File does not exist on your device");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return;
    }

    private void fetch(String fname)
    {
        // Checking args validity
        if (!fname.matches("[a-zA-Z0-9_.-]+"))
        {
            try
            {
                bufferedWriter.write("File name is not acceptable");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return;
        }

        client.fetch(fname,bufferedWriter);
        try
        {
            bufferedWriter.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

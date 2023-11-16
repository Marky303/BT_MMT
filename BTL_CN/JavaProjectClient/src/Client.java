import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Client implements Runnable
{
    // Necessary assets
    private Socket socket;
    private String clientName;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private List<String> fileList;  
    private List<String> fileLocationList;
    private BlockingQueue<String> queue;

    // Constructor
    public Client(Socket socket, String clientName)
    {
        try
        {
            // Socket and clientname
            this.socket = socket;
            this.clientName = clientName;

            // Client database
            this.fileList = new ArrayList<>(); 
            this.fileLocationList = new ArrayList<>(); 

            // Writer and reader
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            // Concurrent queue
            this.queue = new ArrayBlockingQueue<>(1);

            // Sending client name
            if (socket.isConnected())
            {
                bufferedWriter.write(this.clientName);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }
        catch (IOException e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }   
    }

    // Create a new thread thingy
    public void run()
    {
        String command;
        
        // Listening for server requests
        while (socket.isConnected())
        {
            try
            {
                command = bufferedReader.readLine();
                if (command.equals("ping"))
                {
                    // Handle ping from server
                    bufferedWriter.write("pong");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
                else if (command.equals("exist"))
                {
                    // File already exist
                    try 
                    {
                        queue.put("exist");
                    }
                    catch (InterruptedException e)
                    {

                    }
                }
                else if (command.equals("accepted"))
                {
                    // Adding file to client database
                    try 
                    {
                        queue.put("accepted");
                    }
                    catch (InterruptedException e)
                    {

                    }
                }
            }
            catch (IOException e)
            {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    // Other methods
    public void upload(String lname, String fname)
    {
        // Uploading to server
        try 
        {
            // Telling the server that its going to publish a file
            bufferedWriter.write("publish");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // Publishing file name to server
            bufferedWriter.write(fname);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // Checking if file is acceptable
            try
            {
                String verdict = queue.take();
                if (verdict.equals("exist"))
                {
                    // File already exist
                    System.out.println("File already existed");
                    return;
                }
            } 
            catch (InterruptedException e)
            {

            }

            // Adding file to client database
            fileList.add(fname);
            fileLocationList.add(lname);
            System.out.println("File successfully published");
        }
        catch (IOException e)
        {
            System.out.println("Error occured, closing client");
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
    {
        try 
        {
            if (!socket.isClosed())
                socket.close();
            if (bufferedReader!=null)
                bufferedReader.close();
            if (bufferedWriter!=null)
                bufferedWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return;
    }
}

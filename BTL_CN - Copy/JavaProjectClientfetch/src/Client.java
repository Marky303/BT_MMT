import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileOutputStream;
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
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    List<String> fileList;  
    List<String> fileLocationList;
    private BlockingQueue<String> queue;

    // Constructor
    public Client(Socket socket)
    {
        try
        {
            // Socket and clientname
            this.socket = socket;

            // Client database
            this.fileList = new ArrayList<>(); 
            this.fileLocationList = new ArrayList<>(); 

            // Writer and reader
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            // Concurrent queue
            this.queue = new ArrayBlockingQueue<>(1);

        }
        catch (IOException e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }   
    }

    // Listening from server
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
                else if (command.equals("nonexist"))
                {
                    // File does not exist on server
                    try 
                    {
                        queue.put("nonexist");
                    }
                    catch (InterruptedException e)
                    {

                    }
                }
                else 
                {
                    // Found file on server
                    try 
                    {
                        queue.put(command);
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
    public void upload(String lname, String fname, BufferedWriter bfwrt)
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
                    bfwrt.write("File name already exists on server");
                    bfwrt.newLine();
                    return;
                }
            } 
            catch (InterruptedException e)
            {

            }

            // Adding file to client database
            fileList.add(fname);
            fileLocationList.add(lname);
            bfwrt.write("File successfully added to server");
            bfwrt.newLine();
        }
        catch (IOException e)
        {
            try 
            {
                bfwrt.write("File successfully added to server");
                bfwrt.newLine();
            }
            catch (IOException a)
            {
                a.printStackTrace();
            }
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void fetch(String fname, BufferedWriter bfwrt)
    {
        try
        {
            // Letting the server know
            bufferedWriter.write("fetch");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // Sending file name
            bufferedWriter.write(fname);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // Checking if file exist on server
            try
            {
                String verdict = queue.take();
                if (verdict.equals("nonexist"))
                {
                    // File does not exist
                    bfwrt.write("File does not exist on server");
                    bfwrt.newLine();
                    return;
                }
                else 
                {
                    // Found file on server
                    verdict = verdict.substring(1);
                    System.out.println("File found at address: "+verdict);
                    Socket download = new Socket(verdict,1235);
                    BufferedWriter downloadWriter = new BufferedWriter(new OutputStreamWriter(download.getOutputStream()));

                    downloadWriter.write(fname);
                    downloadWriter.newLine();
                    downloadWriter.flush();

                    // Getting file
                    DataInputStream dataInputStream = new DataInputStream(download.getInputStream());
                    int bytes = 0;
                    FileOutputStream fileOutputStream= new FileOutputStream(fname);
                    long size = dataInputStream.readLong(); // read file size
                    byte[] buffer = new byte[4 * 1024];
                    while (size > 0 && (bytes = dataInputStream.read(buffer, 0,(int)Math.min(buffer.length, size)))!= -1) 
                    {
                        // Here we write the file using write method
                        fileOutputStream.write(buffer, 0, bytes);
                        size -= bytes; // read upto file size
                    }
                    fileOutputStream.close();
                    dataInputStream.close();

                    // Done getting file
                    download.close();
                    bfwrt.write("File downloaded successfully");
                    bfwrt.newLine();
                }
            } 
            catch (InterruptedException e)
            {

            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
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

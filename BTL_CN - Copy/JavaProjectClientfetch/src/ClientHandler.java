import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class ClientHandler implements Runnable
{
    // Setting up necessary objects
    public ArrayList<ClientHandler> clientHandlers;
    public Socket socket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    ArrayList<String> fileList;
    List<String> fileLocationList;

    // Constructor
    public ClientHandler(Socket socket, ArrayList<ClientHandler> clientHandlers, ArrayList<String> fileList, List<String> fileLocationList)
    {
        try
        {
            this.clientHandlers = clientHandlers;
            this.socket = socket;
            this.fileList = fileList;
            this.fileLocationList = fileLocationList;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            clientHandlers.add(this);
        }
        catch (IOException e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Client listening method
    @Override
    public void run()
    {
        String fname;
        
        // Listen for client command
        try
        {
            // Getting file name
            fname = bufferedReader.readLine();
            
            // Finding file location
            int index = 0;

            // Fetching index
            while (index<fileList.size())
            {
                if (fileList.get(index).equals(fname))
                {
                    break;
                }
                index++;
            }
            String lname = fileLocationList.get(index);

            // Transfering file
            String path = lname+ "\\" +fname;
            int bytes = 0;
            File file = new File(path);
            FileInputStream fileInputStream= new FileInputStream(file);
            DataOutputStream dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
            dataOutputStream.writeLong(file.length());
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fileInputStream.read(buffer))!= -1) 
            {
                dataOutputStream.write(buffer, 0, bytes);
                dataOutputStream.flush();
            }

            // Closing
            fileInputStream.close();
            dataOutputStream.close();
        }
        catch (IOException e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
        closeEverything(socket, bufferedReader, bufferedWriter);
        return;
    }   

    // Other methods
    

    public void removeClient()
    {
        clientHandlers.remove(this);
        return;
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
    {
        removeClient();
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
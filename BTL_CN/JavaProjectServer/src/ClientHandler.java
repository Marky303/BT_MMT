import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class ClientHandler implements Runnable
{
    // Setting up necessary objects
    public ArrayList<ClientHandler> clientHandlers;
    public Socket socket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter; 
    public String clientName;
    public List<String> clientFile;
    InetAddress clientAddr;
    long start; 
    long end;

    // Constructor
    public ClientHandler(Socket socket, ArrayList<ClientHandler> clientHandlers)
    {
        try
        {
            this.clientHandlers = clientHandlers;
            this.socket = socket;
            this.clientAddr = socket.getInetAddress();
            this.clientFile = new ArrayList<>();
            start = -1;
            end = -1;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.clientName = this.clientAddr.toString();
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
        String command;
        
        // Listen for client command
        while (socket.isConnected())
        {
            try
            {
                command = bufferedReader.readLine();
                if (command.equals("publish"))
                {
                    // Client publishing a file
                    String fname = bufferedReader.readLine();

                    // Checking file integrity
                    if (!fileCheck(fname))
                    {
                        // Informing client file already exists
                        bufferedWriter.write("exist");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        continue;
                    }
                    // Informing client file is acceptable
                    bufferedWriter.write("accepted");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    // Adding file to server database
                    clientFile.add(fname);
                }
                else if (command.equals("request"))
                {
                    // Client requesting a file



                }
                else if (command.equals("pong"))
                {
                    // Receiving a pong from client
                    end = System.currentTimeMillis();
                }
            }
            catch (IOException e)
            {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
        
        return;
    }   

    // Other methods
    private Boolean fileCheck(String fname)
    {
        for (ClientHandler clientHandler : clientHandlers)
        {
            for (String fileName : clientHandler.clientFile)
            {
                if (fileName.equals(fname))
                    return false;
            }
        }
        return true;
    }

    public void ping(BufferedWriter bfrWrt)
    {
        try 
        {
            // Send client a message and start counting time elapsed
            long start = System.currentTimeMillis();
            bufferedWriter.write("ping");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            
            // Wait until timeout (3000ms)
            Thread.sleep(3000);

            if (end!=-1)
            {
                long elapsed = end - start;
                bfrWrt.write("Client is alive and well ("+elapsed+" ms)");
                bfrWrt.newLine();
            }
            else 
            {
                bfrWrt.write("Ping timeout, client is irresponsive");
                bfrWrt.newLine();
                closeEverything(socket, bufferedReader, bufferedWriter);
            }

            // Resetting start and end 
            start = -1;
            end = -1;
        }
        catch (IOException e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
        catch (InterruptedException e)
        {

        }
    }

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
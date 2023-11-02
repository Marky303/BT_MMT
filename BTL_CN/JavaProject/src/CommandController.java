import java.util.*;   
import java.io.*;

public class CommandController 
{
    Scanner scan = new Scanner(System.in);
    String input;
    
    private void checkSplit(String args[], int length)
    {
        for (int i=0;i<length;i++)
        {
            System.out.print(args[i]);
            System.out.println("|");
        }
        return;
    }
    
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
            System.out.println("File exists");
        }
        else 
        {
            System.out.println("File doesnt exists");
        }
        
        
        // Uploading file info to server


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
    
    public void inputController()
    {
        // Getting user input
        while (!((input=scan.nextLine()).equals("stop")))
        {
            // Splitting into arguments
            String args[] = input.split("\\s+",3);

            // Split space check
            checkSplit(args,args.length);

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
                        System.out.println("No file name entered (Invalid Syntax)");
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
            System.out.println("__________________________________________");
        }
        // Disconnecting procedures
        System.out.println("Stopping program");
    }   
}

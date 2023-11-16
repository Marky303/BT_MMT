public class App {
    public static void main(String[] args) throws Exception 
    {
        // Starting CLI
        System.out.println("___________________________________________________________");
        System.out.println("Client application Started");
        CommandController cli = new CommandController();
        Thread cliThread = new Thread(cli);
        cliThread.start();
    }
}

import java.io.*;
import java.net.*;

//Chat Client Class
public class Client implements Runnable{
	//initialize variables
	DataInputStream console = null;
	DataOutputStream streamOut = null;
	ClientProcess client = null;
	Socket socket = null;
	Thread thread = null;

	public Client(String serverName, int serverPort){
		System.out.println("Connecting...");
		try{
			//try to connect to the server
			socket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + socket);
			start();
			System.out.println("COMMANDS:\n'REG name' to register\n'MESG message' to send message\n'PMSG user message' to send private message\n'EXIT' to exit");
		}
		catch(UnknownHostException e){
			System.out.println("Error: " + e.getMessage());
		}
		catch(IOException e){
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void run(){
		while (thread != null){
			try{
				streamOut.writeUTF(console.readLine());
				streamOut.flush();
			}
			catch(IOException e){
				System.out.println("Error: " + e.getMessage());
				stop();
			}
		}
	}

	public void control(String msg){
		//determine how to handle incoming messages
		if(msg.equals("EXIT")){
			stop();
		}
		else{
			System.out.println(msg);
		}
	}

	public void start() throws IOException{
		console = new DataInputStream(System.in);
		streamOut = new DataOutputStream(socket.getOutputStream());

		if(thread == null){
			client = new ClientProcess(this, socket);
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop(){
		if (thread != null){
			thread.stop();
			thread = null;
		}
		try{
			if(console != null){
				console.close();
			}
			if(streamOut != null){
				streamOut.close();
			}
			if(socket != null){
				socket.close();
			}
		}
		catch(IOException ioe){
			System.out.println("Error closing ...");
		}
		client.end();
		client.stop();
	}

	public static void main(String args[]){
		Client client = null;
		if(args.length != 2){
			System.out.println("Usage: java Client host port");
		}
		else{
			client = new Client(args[0], Integer.parseInt(args[1]));
		}
	}
}
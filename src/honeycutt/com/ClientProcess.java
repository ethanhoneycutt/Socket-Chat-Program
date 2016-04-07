package honeycutt.com;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

//Thread-based class allowing multiple clients
public class ClientProcess extends Thread{
	//initialize variables
	DataInputStream streamIn = null;
	Server server = null;
	Socket socket = null;
	Client client = null;
	
	public ClientProcess(Client c, Socket s){
		client = c;
		socket = s;
		//start a client thread
		begin();  
		start();
	}

	public void begin(){
		try{
			streamIn = new DataInputStream(socket.getInputStream());
		}
		catch(IOException e){
			System.out.println("Error: " + e);
			client.stop();
		}
	}

	public void end(){
		try{
			if(streamIn != null){
				streamIn.close();
			}
		}
		catch(IOException e){
			System.out.println("Error: " + e);
		}
	}

	public void run(){
		while (true){
			try{
				client.control(streamIn.readUTF());
			}
			catch(IOException e){
				System.out.println("Error: " + e.getMessage());
				client.stop();
			}
		}
	}
}
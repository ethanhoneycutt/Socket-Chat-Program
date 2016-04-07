package honeycutt.com;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//server.Server class
public class Server implements Runnable{
	//initialize variables
	Thread thread = null;
	int clientCount = 0;
	ServerProcess clients[] = new ServerProcess[50];
	ServerSocket server = null;

	public Server(int port){
		//create a server object
		try{
			server = new ServerSocket(port);
			System.out.println("Started: " + server);
			start();
		}
		catch(IOException e){
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void run(){
		while(thread != null){
			try{
				System.out.println("Waiting for clients to join"); 
				addThread(server.accept());
			}
			catch(IOException e){
				System.out.println("Error: " + e);
				stop();
			}
		}
	}

	private void addThread(Socket socket){
		//handle a client joining
		if(clientCount < clients.length){
			System.out.println("client.Client added: " + socket);
			clients[clientCount] = new ServerProcess(this, socket);
			try{
				clients[clientCount].begin();
				clients[clientCount].start();
				clientCount++;
			}
			catch(IOException e){
				System.out.println("Error: " + e);
			}
		}
		else{
			System.out.println("No more allowed");
		}
	}

	private int findClient(int ID){
		//find a client object position in the array given its ID
		for(int i = 0; i < clientCount; i++){
			if(clients[i].getID() == ID){
				return i;
			}
		}
		return -1;
	}

	public synchronized void remove(int ID){
		//remove a client
		int clientPos = findClient(ID);
		if (clientPos >= 0){
			ServerProcess clientEnd = clients[clientPos];
			System.out.println("Removing client thread " + ID);
			if(clientPos < clientCount-1) {
				for(int i = clientPos+1; i < clientCount; i++){
					clients[i-1] = clients[i];
				}
			}
			clientCount--;
			try{
				clientEnd.end();
			}
			catch(IOException e){
				System.out.println("Error: " + e);
			}
			clientEnd.stop();
		}
	}

	public synchronized void control(int ID, String input){
		//determine how to handle incoming messages

		//username registration
		if(input.startsWith("REG ")){
			String username = input.substring(input.indexOf(" ") + 1);
			boolean existing = false;
			for(int i = 0; i < clientCount; i++){
				if(clients[i].username != null){
					if((clients[i].username).equals(username)){
						clients[findClient(ID)].send("Username already taken, try again.");
						existing = true;
					}
				}
			}
			if(!existing){
				clients[findClient(ID)].setUsername(username);
				System.out.println("client.Client registered with username " + username);
				for(int i = 0; i < clientCount; i++){
					clients[i].send(clients[findClient(ID)].username + " has joined the chat.");
				}
			}
		}
		//regular message
		else if(input.startsWith("MESG ")){
			String message = input.substring(input.indexOf(" ") + 1);
			for(int i = 0; i < clientCount; i++){
				clients[i].send(clients[findClient(ID)].username + ": " + message);
			}
			System.out.println("Message sent");
		}
		//private message
		else if(input.startsWith("PMSG ")){
			int startPos = input.indexOf(" ") + 1;
			int endPos = input.indexOf(" ", startPos);
			for(int i = 0; i < clientCount; i++){
				System.out.println(":" + clients[i].username + ":" + input.substring(startPos, endPos));
				if(clients[i].username.equals(input.substring(startPos, endPos))){
					clients[i].send(clients[findClient(ID)].username + "(private): " + input.substring(endPos + 1));
				}
			}
			System.out.println("Private message sent: " + input.substring(endPos + 1) + " to " + input.substring(startPos, endPos));
		}
		//exit request
		else if(input.startsWith("EXIT ")) {
			clients[findClient(ID)].send("EXIT");
			for(int i = 0; i < clientCount; i++){
				clients[i].send(clients[findClient(ID)].username + " has left the chat.");
			}
			remove(ID);
		}
	}

	public void start(){
		if(thread == null){
			thread = new Thread(this); 
			thread.start();
		}
	}

	public void stop(){
		if(thread != null){
			thread.stop(); 
			thread = null;
		}
	}

	public static void main(String args[]){
		Server server = null;
		if(args.length != 1){
			System.out.println("Command: java server.Server port");
		}
		else{
			server = new Server(Integer.parseInt(args[0]));
		}
	}
}
import java.io.*;
import java.net.*;

//Thread-based class for server
public class ServerProcess extends Thread{
	//initialize variables
	DataInputStream streamIn = null;
	DataOutputStream streamOut = null;
	Server server = null;
	Socket socket = null;
	int ID = -1;
	String username = null;

	public ServerProcess(Server se, Socket so){
		//start up a server thread
		super();
		server = se;
		socket = so;
		ID = socket.getPort();
	}

	public void send(String msg){
		try{
			streamOut.writeUTF(msg);
			streamOut.flush();
		}
		catch(IOException e){
			System.out.println(ID + " ERROR: " + e.getMessage());
			server.remove(ID);
			stop();
		}
	}

	public int getID(){
		return ID;
	}

	public void setUsername(String u){
		username = u;
	}

	public void run(){
		System.out.println("Server Process " + ID + " running.");
		while (true){
			try{
				server.control(ID, streamIn.readUTF());
			}
			catch(IOException ioe){
				System.out.println(ID + " ERROR reading: " + ioe.getMessage());
				server.remove(ID);
				stop();
			}
		}
	}

	public void begin() throws IOException{
		streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}

	public void end() throws IOException{
		if(socket != null){
			socket.close();
		}
		if(streamIn != null){
			streamIn.close();
		}
		if(streamOut != null){
			streamOut.close();
		}
	}
}
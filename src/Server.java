import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread
{
	final ServerSocket serverS;
	private ConnectionLists list;
	private ConnectionLists connections;
	
	public Server(ServerSocket serverS, ConnectionLists list, ConnectionLists connections)
	{
		this.serverS = serverS;
		this.list = list;
		this.connections = connections;
		System.out.println("Starting a new server!");
	}
	
	@Override
	public void run()
	{
		//Socket stays open to retrieve requests
		while(true)
		{
			Socket sock = null;
			
			try
			{
				//Socket object to receive incoming client requests
				sock = serverS.accept();
				
				System.out.println("A new client is connected.");
				
				//Obtaining input and output streams
				DataInputStream dis = new DataInputStream(sock.getInputStream());
				DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
				
				//Create a new Handler
				Handler handle = new Handler(sock, dis, dos, list);
				handle.start();
				connections.addList(handle);
			}
			catch(Exception e)
			{
				System.out.println("Closing server socket...");
			}
		}
	}
}

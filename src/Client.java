import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Thread
{
	final String ip;
	final int port;
	final int myPortNumber;
	final String ipPort;
	private ConnectionLists list;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket s;
	private boolean run = true;
	
	public Client(String ip, String port, int myPortNumber, ConnectionLists list)
	{
		this.ip = ip;
		this.port = Integer.parseInt(port);
		this.myPortNumber = myPortNumber;
		this.list = list;
		ipPort = ip + " " + port;
	}
	
	@Override
	public void run()
	{
		try
		{
			String received = "";
			String[] tokenAns = null;
			
			//Establish the connection with server port
			s = new Socket(ip, port);
			
			list.addList(ipPort + " Host");
			
			//Obtaining input and output streams
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());
			
			//Send the server your IP and port in case they want to connect to you
			boolean sendIP = false;
			
			//Exchanges information between client and client handler
			while(run)
			{
				try
				{
					received = dis.readUTF();
					tokenAns = received.split(" ");
				}
				catch(IOException ex)
				{
					close(true);
				}
				
				if(!sendIP)
				{
					dos.writeUTF(InetAddress.getLocalHost().getHostAddress() + " " + myPortNumber);
					System.out.print(received + "\n>");
					sendIP = true;
				}
				else
				{
					try
					{
						switch(tokenAns[0])
						{
							//Displays their IP, port, and message
							case "send":
								if(tokenAns != null)
								{
									System.out.println();
									System.out.println("Message received from: " + ip);
									System.out.println("Sender's Port: " + port);
									System.out.print("Message: ");
									for(int i = 1; i < tokenAns.length; i++)
									{
										System.out.print(tokenAns[i] + " ");
									}
									System.out.println();
									System.out.print(">");
									tokenAns = null;
								}
								break;
							
							case "terminate":
								close(false);
								break;
						}
					}
					catch(NullPointerException e)
					{
					}
				}
			}
		}
		//Couldn't connect to the socket, check if the two lists are synced, if it is
		// them remove last from each list, if not remove last from connections only
		catch(Exception e)
		{
			if(list.synced())
			{
				list.removeList(list.size() - 1);
			}
			else
			{
				list.removeLastCon();
			}
			System.out.print("Could not connect to that specific ip and port number.\n>");
		}
	}
	
	//Sends a message to a peer and prints to the user the ID # of the peer
	public void sendMessage(String message, int id)
	{
		try
		{
			dos.writeUTF("send " + message);
			System.out.println("Message sent to: ID #" + ++id);
		}
		catch(IOException ex)
		{
			System.out.println("Error sending message");
		}
	}
	
	//Sends a message to the specific peer to tell them to close their connections with us
	public void endConnection()
	{
		try
		{
			dos.writeUTF("terminate");
			run = false;
		}
		catch(IOException ex)
		{
			Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	//Terminates a connection with a peer, closes InputStream and OutputStream, as well as the socket
	//Removes them from your list of connections and displays a message to the user
	public void close(boolean iClosedIt)
	{
		try
		{
			run = false;
			this.dis.close();
			this.dos.close();
			s.close();
			
			//If its -1 then it's not in the list and has been removed already
			int id = list.getID(ipPort + " Host");
			if(id > -1)
			{
				list.removeList(id);
			}
			
			if(iClosedIt)
			{
				System.out.print("A connection has been closed with ip and port number: [" + ip + " ] [" + port + "]\n>");
			}
			else
			{
				System.out.print("The other peer with ip and port number: [" + ip + " ] [" + port + "] has terminated your connection.\n>");
			}
		}
		catch(Exception e)
		{
			System.out.println("Error terminating connection");
		}
	}
}

import java.util.ArrayList;

public class ConnectionLists
{
	private static ArrayList<String> iPAddress = new ArrayList<>();
	private static ArrayList<Object> connections = new ArrayList<>();

	//Stores both clients and hosts(handlers) in an easy to identify format
	//<ip> <port> <host/client>
	public void addList(String newClient)
	{
		iPAddress.add(newClient);
	}
	
	//Stores Clients into list of connections
	public void addList(Client newConnect)
	{
		connections.add(newConnect);
	}
	
	//Stores Handlers into list of connections
	public void addList(Handler newConnect)
	{
		connections.add(newConnect);
	}
	
	public int size()
	{
		return connections.size();
	}
	
	//Both lists should be the same size in order to be synced
	public boolean synced() { return iPAddress.size() == connections.size(); }
	
	//Retrieves the actual reference to the Handler or Client objects
	public Object get(int index) { return connections.get(index); }
	
	public boolean isClient(int index)
	{
		String[] client = iPAddress.get(index).split(" ");
		if(client[2].equalsIgnoreCase("Client"))
		{
			return true;
		}
		return false;
	}
	
	//Removes references in both lists
	public void removeList(int id)
	{
		iPAddress.remove(id);
		connections.remove(id);
	}
	//list is not synced so remove the last connection, only used if there are more connections
	public void removeLastCon() { connections.remove(connections.size() - 1); }

	public boolean isEmpty() { return iPAddress.isEmpty() && connections.isEmpty(); }
	
	//Status refers to either host or client
	//The item must be in the ArrayList to get the id
	public int getID(String ipPortStatus) { return iPAddress.indexOf(ipPortStatus); }
	
	public boolean contains(String entry) { return iPAddress.contains(entry); }
	
	public void printList()
	{
		System.out.printf("%-14s%-22s%-22s%-22s\n", "ID:", "IP", "AddressPort No.", "They are the");
		for(int i = 0; i < iPAddress.size(); i++)
		{
			int count = i + 1;
			String[] ipPort = iPAddress.get(i).split(" ");
			System.out.printf("%-14d%-22s%-22s%-22s\n", count, ipPort[0], ipPort[1], ipPort[2]);
		}
		System.out.println();
		/*System.out.println(connections);
		System.out.println("Number of threads: "+Thread.activeCount());*/
	}
}

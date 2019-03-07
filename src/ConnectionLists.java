import java.util.ArrayList;

public class ConnectionLists {
    private static ArrayList<String> iPAddress = new ArrayList<>();
    private static ArrayList<Object> connections = new ArrayList<>();


    public void addList(String newClient) {
        iPAddress.add(newClient);
    }

    //storing both handlers and clients into a list of connections
    public void addList(Client newConnect) {
        connections.add(newConnect);
    }

    public void addList(Handler newConnect) {
        connections.add(newConnect);
    }

    public int size() {
        return connections.size();
    }

    //both lists should be same size in order to be synced
    public boolean synced() {
        return iPAddress.size() == connections.size();
    }

    //retrieves the actual reference to the handler or client object
    public Object get(int index) {
        return connections.get(index);
    }

    public boolean isClient(int index) {
        String[] client = iPAddress.get(index).split(" ");
        if(client[2].equalsIgnoreCase("Client")) {
            return true;
        }
        return false;
    }

    //remove references in both lists
    public void removeList(int id) {
        iPAddress.remove(id);
        connections.remove(id);
    }

    public void removeLastCon() {
        connections.remove(connections.size() - 1);
    }

    public boolean isEmpty() {
        if(iPAddress.isEmpty() && connections.isEmpty()) {
            return true;
        }
        return false;
    }

    //status refers to either host or client
    //to get the id first make sure the item is in the arraylist
    public int getID(String ipPortStatus) {
        return iPAddress.indexOf(ipPortStatus);
    }

    public boolean contains(String entry) {
        return iPAddress.contains(entry);
    }

    public void printList() {
        System.out.printf("%-14s%-22s%-22s%-22s\n","ID:","IP","AddressPort No.","They are the");
        for(int i=0; i<iPAddress.size() ; i++) {
            int count = i+1;
            String[] ipPort = iPAddress.get(i).split(" ");
            System.out.printf("%-14d%-22s%-22s%-22s\n",count,ipPort[0],ipPort[1],ipPort[2]);
        }
        System.out.println();
        //printConnections();
    }
    //testing to make sure the references to the objects are there too
    /*public void printConnections() {
        System.out.println("Connections list:");
        for(int i=0; i<connections.size() ;i++){
            System.out.println(connections.get(i));
        }
    }*/
}

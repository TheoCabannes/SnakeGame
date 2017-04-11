package utilities;


public class Client {
	public int id;
	public String address;
	public int listeningPort;
	
	public Client(String address, int listeningPort, byte id){
		/**
		 * Defined by a physical address and a listeningPort
		 */
		this.id=id&255;
		this.address=address;
		this.listeningPort=listeningPort;
	}
	
	
	@Override
	public String toString(){
		return "Client "+this.id+" listening on "+this.address+":"+this.listeningPort;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

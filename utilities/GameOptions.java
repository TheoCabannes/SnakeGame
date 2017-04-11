package utilities;

public class GameOptions {
	
	public final static int gridSize=1<<7-1;
	public final static int appleLifeTime=100;
	public static final byte WEST = 0, NORTH = 1, EAST = 2, SOUTH = 3;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		print();
	}
	
	public static void print(){
		System.out.println("List of final game options:");
		System.out.println("\t- Game:");
		System.out.println("\t\t> Grid size: "+gridSize);
		
		System.out.println("\t- Server:");
		System.out.println("\t\t> nothing yet...");
	}

}

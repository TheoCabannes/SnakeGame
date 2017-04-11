package games_handler;

import game.Game;

import java.io.IOException;
//import java.util.HashSet;
//import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

//import utilities.Client;
import utilities.Job;
import utilities.Runnable_Input;

public class GH_Manager implements Runnable{
	/**
	 * Handles the new player asking to play
	 * 
	 * 
	 */
	// Output Thread > sends a message to the player to tell them that they can ask to play on a specific port
	private Thread output;
		
	// Input thread > players are sending messages to it
	// messages are transformed into Jobs and sent to this class through in_communicator
	private Thread input;
	private ArrayBlockingQueue<Job> in_communicator;
	//private HashSet<Byte> jobAlreadyDone;
	private int nextPortToUseForGame, nbPlayers;
	
	
	public GH_Manager(int inputPort, int outputPort, String serverName, long broadcastTimeInterval, int nbP) throws IOException{
		/**
		 * A GameHandler_Manager:
		 * - listening on port inputPort (using 1 Thread)
		 * - broadcasting this inputPort and the serverName every broadcastTimeInterval (ms) on outputPort (using another Thread)
		 */
		nbPlayers = Math.max(1, Math.min(nbP, 4));
		System.out.println("GH_Manager has been initialized:");
		
		in_communicator=new ArrayBlockingQueue<Job>(100);
		input=new Thread(new Runnable_Input(inputPort, in_communicator, "GH"));
		System.out.println("\t> input Thread initialized on port "+inputPort);
		
		output=new Thread(new GH_Output(outputPort, serverName, broadcastTimeInterval, inputPort));
		System.out.println("\t> output Thread initialized on port "+outputPort+" (for broadcast)");
		
		System.out.println("\t> END");
		//jobAlreadyDone=new HashSet<Byte>();
		nextPortToUseForGame=30000;
		
		
	}

	@Override
	public void run() {
		System.out.println("GH_Manager has been started");
		input.start();
		output.start();
		
		while(true){
			//we indefinitely wait for new players wanting to play
			try {
				Job j=in_communicator.take();
				System.out.println(">>>>>>>>>>>>>>>>>>>>> Received a message");
				switch(j.type()){
				case WANT_TO_PLAY:
					//if(jobAlreadyDone.contains(j.jobId())) break;//if we already did the job, let's do nothing 
					//jobAlreadyDone.add(j.jobId());
					System.out.println("A player want to play");
					Game g= Game.getGameForANewPlayer();
					if(g==null && nextPortToUseForGame<32000){
						//no existing Game for now
						g=new Game(nbPlayers, nextPortToUseForGame++);
						//we start a new Game with maxPlayer, inputPort
						g.start();
					}
					g.addClient(j.address(), j.port());
					break;
					
				default:
					System.out.println("received another type: "+j.type());
					break;
				
				}
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}

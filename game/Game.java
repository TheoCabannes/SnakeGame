package game;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

import utilities.Apple;
import utilities.Client;
import utilities.Job;
import utilities.Point;
import utilities.Runnable_Output;
import utilities.Snake;

public class Game extends Thread {
	// ==========> OBJECT <===============
	public Snake s1;
	public Snake s2;
	public Snake s3;
	public Snake s4;
	
	public volatile boolean waitForClients;
	private String gameName;//unused for now
	private int maxPlayers;
	public LinkedList<Snake> remainingSnakes;//Snakes available for new players
	public HashMap<Integer,Snake> snakes;
	public HashSet<Snake> snakesAtStart;
	public Apple apple;
	
	public G_Manager manager;
	private long multicastTimeInterval = 50;// 50 ms
	
	
	public Game(int maxPlayers, int inputPort) throws IOException {
		this.maxPlayers = maxPlayers;
		this.gameName = "Test";//TODO
		
		s1=new Snake(new Point(0,0),15,(byte)1);
		s2=new Snake(new Point(40,40),15,(byte)2);
		s3=new Snake(new Point(80,80),15,(byte)3);
		s4=new Snake(new Point(120,120),15,(byte)4);
		
		
		snakes=new HashMap<Integer,Snake>();
		remainingSnakes = new LinkedList<Snake>();
		remainingSnakes.add(s1);
		remainingSnakes.add(s2);
		remainingSnakes.add(s3);
		remainingSnakes.add(s4);
		
		snakesAtStart=new HashSet<Snake>();
		
		this.manager = new G_Manager(this, inputPort, multicastTimeInterval);
		System.out.println("Game was initialized, listening on "+inputPort);
		
		waitForClients=true;
		
		games.add(this); //Once a Game is initiated, we had it to this list, so that it can be filled with new players
	}

	public int maxPlayers() {
		return maxPlayers;
	}

	public void addClient(String address, int port) throws IOException, InterruptedException {
		if (this.hasRoom() && waitForClients){
			Snake s=remainingSnakes.removeFirst();
			snakesAtStart.add(s);
			Client c=new Client(address, port, s.id);
			this.snakes.put(c.id, s);
			
			ArrayBlockingQueue<Job> out_communicator = new ArrayBlockingQueue<Job>(100);
			this.manager.out_communicators.put(c, out_communicator);
			Thread t=new Thread(new Runnable_Output(c.address, c.listeningPort, out_communicator, "G", manager));
			t.start();
			
			System.out.println("A client has been added and communicator was initiated");
			
			Job j = new Job(Job.Type.SEND_GAME_INFO);
			j.id(s.id);
			j.port(manager.inputPort);
			out_communicator.put(j);
			
			System.out.println("Game sent a job \""+ j.type() +"\" to Runnable_Output for Client "+c.id);
			resetApple();
			if(!this.hasRoom()) waitForClients=false;
		}
			
	}
	
	public void resetApple(){
		this.apple=new Apple();
	}

	public void removeClient(Client c) {
		snakes.remove(c);
		this.manager.out_communicators.remove(c);
	}

	public boolean hasRoom() {
		return snakes.size() < maxPlayers;
	}

	@Override
	public String toString() {
		String ret = gameName + ":\n";
		for (Snake c : snakes.values()) {
			ret += "\t> " + c + "\n";
		}
		return ret;
	}

	@Override
	public void run() {
		manager.run();
	}

	// ==========> STATIC <===============
	// actually existing Games
	public static LinkedList<Game> games = new LinkedList<Game>();
	//possible starting positions
	
	

	public static Game getGameForANewPlayer() {
		/**
		 * returns an existing Game that is not full
		 */
		for (Game g : games) {
			if (g.hasRoom() && g.waitForClients)
				return g;
		}
		return null;
	}

}

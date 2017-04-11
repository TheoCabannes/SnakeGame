package launcher;

import java.io.IOException;

import games_handler.GH_Manager;

public class LaunchServer {

	public static void main(String[] args) throws IOException {
		int nbJoueur = (args.length > 0 ? Integer.parseInt(args[0]) : 1);
		System.out.println("Server initializing...");
		Thread GH=new Thread(new GH_Manager(5757, 5656, "Snakes Server", 2000, nbJoueur));
		GH.start();
	}

}

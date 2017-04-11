package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

// TEST OK
public class Client_listener implements Runnable {
	// le serveur vide la file avant de mettre un nouvel element
	private ArrayBlockingQueue<Pair<HashMap<Byte, Snake>, Point>> grilleJobs;
	private DatagramChannel listenerChannel; // sur le portEcoute
	private Client client;
	private boolean pasLancerDir = true;

	// le serveur vide la file avant de mettre un nouvel element, ne marche
	// parce qu'un seul thread remplit la file
	protected Client_listener(ArrayBlockingQueue<Pair<HashMap<Byte, Snake>, Point>> jobs, short listeningPort,
			Client c) {
		grilleJobs = jobs;
		client = c;
		try {
			listenerChannel = DatagramChannel.open();
			listenerChannel.socket().bind(new InetSocketAddress(listeningPort));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			boolean gameOver = false;
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (true) {
				listenerChannel.receive(buffer);
				buffer.flip();
				byte type = buffer.get();
				switch (type) {
				case 0:
					if (client.pasRecuPortJeu) {
						client.pasRecuPortJeu = false;
						short gamePort = buffer.getShort();
						byte numero = buffer.get();
						// On a port de jeu = gamePort et numero de client =
						// numero
						client.lancerAffichage(numero);
						client.lancerSpeaker(numero, gamePort);
					}
					break;
				case 1:
					client.print("<HTML><h2>On commence dans " + buffer.get() + " secondes</h2></HTML>");
					break;
				case 2:
					if (pasLancerDir) {
						client.lancerGestionnaireDirection();
						pasLancerDir = false;
						client.print("");
					}
					lireSerpents(buffer);
					break;
				case 3:
					if (!gameOver) {
						gameOver = true;
						client.print(lireBufferFinal(buffer));
					}
					break;
				default:
					throw new Exception("Le message du server de Jeu est corrompu");
				}
				buffer.clear();
			}
		} catch (Exception e) {
		}
	}

	private String lireBufferFinal(ByteBuffer buffer) {
		String s = "<HTML><h2>La partie est finie, voici les scores :</h2>";
		byte nbSnakes = buffer.get();
		for (int i = 0; i < nbSnakes; i++) {
			byte num = buffer.get();
			short score = buffer.getShort();
			s += "<h3>Le serpent " + num + " a " + score + " point</h3>";
		}
		return s+"</HTML>";
	}

	private void lireSerpents(ByteBuffer buffer) throws Exception {
		Pair<HashMap<Byte, Snake>, Point> req = decodeBufferToGame(buffer);
		// ici on enleve tous les elements de la file pour ne prendre en compte
		// que le dernier, marche car un seul thread remplit la file
		while (grilleJobs.size() > 0)
			grilleJobs.poll();
		grilleJobs.put(req);
	}

	// fonction decode
	private static Pair<HashMap<Byte, Snake>, Point> decodeBufferToGame(ByteBuffer buf) throws Exception {
		HashMap<Byte, Snake> snakes = new HashMap<Byte, Snake>();
		try {
			byte nbSnakes = buf.get();
			for (int i = 0; i < nbSnakes; i++) {
				byte numSnake = buf.get();
				LinkedList<Point> curSnake = new LinkedList<Point>();
				Point cur = new Point(buf.get(), buf.get());
				curSnake.add(cur);
				byte nbDir = buf.get();
				byte dir = -1;
				for (int j = 0; j < nbDir; j++) {
					dir = buf.get();
					byte length = buf.get();
					int k = j == 0 ? 1 : 0;
					for (; k < length; k++) {
						Point tmp = new Point(cur.x + (dir % 2 == 0 ? dir - 1 : 0),
								cur.y + (dir % 2 == 1 ? dir - 2 : 0));
						cur = tmp;
						curSnake.add(cur);
					}
				}
				Snake c = new Snake(dir, numSnake, curSnake);
				snakes.put(numSnake, c);
			}
			Point pomme = new Point(buf.get(), buf.get());
			return new Pair<HashMap<Byte, Snake>, Point>(snakes, pomme);
		} catch (Exception e) {
		}
		throw new Exception("Le message du serveur est mal décodé");
	}
}
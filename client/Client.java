package client;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


public class Client{
	// server est initialisé dans le lireBufferAttenteJoueurServeur et utile dans lancerSpeaker
	private InetSocketAddress server;
	// créée dans lancementListener et utile pour lancerAffichage, grilleJobs contient les serpents envoye par le serveur, la liste est partagée entre le client listener aui la remplit et le gestionnaire de la grille qui la vide
	private ArrayBlockingQueue<Pair<HashMap<Byte, Snake>, Point>> grilleJobs; 
	// créée dans lancerAffichage et utile pour lancerSpeaker, directionIdJobs est remplie par le gestionnaire de direction et vidée par le sender
	private BlockingDeque<Pair<Byte,Byte>> directionIdJobs;
	// pour envoyer le message je veux joueur tant qu'on ne recoit pas de message du serveur
	protected volatile boolean pasRecuPortJeu = true;
	private String nomServer;
	private GestionDemandeDirection gest;
	private GestionAffichage fenetre = null;
	
	// on recupere sur le port 5656, le serveur et le port avec lequel on
	// communique avec le serveur, on dit au serveur de nous parler sur 5959
	public Client() throws Exception{
			lancementListener((short) 5959, lireBufferAttenteJoueurServeur(5656));
	}
	
	// On lance un client listener sur le port listeningPort et on envoie au serveur le port sur lequel on va ecouter
	private void lancementListener(short listeningPort, short envoiePort) throws Exception {
		grilleJobs = new ArrayBlockingQueue<Pair<HashMap<Byte, Snake>, Point>>(1);
		new Thread(new Client_listener(grilleJobs, listeningPort, this)).start();
		envoieServer(listeningPort, envoiePort, server);
	}
	
	// appelé par le Client_listener
	void lancerAffichage(byte numero) {
		directionIdJobs = new LinkedBlockingDeque<Pair<Byte,Byte>>(5);
		ArrayBlockingQueue<Byte> directionJobs = new ArrayBlockingQueue<Byte>(5);
		gest = new GestionDemandeDirection(directionIdJobs, directionJobs);
		fenetre = new GestionAffichage(nomServer, numero, directionJobs);
		new Thread(new GestionBackGrille(grilleJobs, fenetre, numero, gest)).start();
	}

	// appelé par le Client_listener, on lance un speaker sur le port gamePort
	void lancerSpeaker(byte numero, short gamePort) {
		new Thread(new Client_sender(server, directionIdJobs, gamePort, numero)).start();
	}
	
	void lancerGestionnaireDirection(){
		new Thread(gest).start();
	}
	
	public void print(String string) {
		//text.setText(string);
		if(fenetre!=null)
			fenetre.print(string);
	}

	private void envoieServer(short listeningPort, short portConnexion,
			InetSocketAddress server) throws Exception {
		// on ouvre une nouvelle connexion avec le serveur sur le port de connexion
		DatagramChannel speakerChannel = DatagramChannel.open();
		speakerChannel.socket().bind(new InetSocketAddress(0));
		InetSocketAddress remote = new InetSocketAddress(server.getAddress(), portConnexion);
		
		// on envoie le port de jeu du client
		ByteBuffer jeVeuxJouer = clientConnection(listeningPort);
		while(pasRecuPortJeu){
			// On envoie un message je veux jouer sur le port portConnexion
			speakerChannel.send(jeVeuxJouer, remote);
			// permet de reenvoyer le buffer
			jeVeuxJouer.position(0); 
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
		// on ferme la communication
		speakerChannel.close();
	}

	private ByteBuffer clientConnection(short listeningPort) {
		ByteBuffer res = ByteBuffer.allocate(3);
		res.put((byte) 0);
		res.putShort(listeningPort);
		res.flip();
		return res;
	}

	private short lireBufferAttenteJoueurServeur(int portServeur)
			throws Exception {
		// on ouvre une communication avec le serveur sur le port indiqué dans la rfc (5656)
		DatagramChannel clientSocket = DatagramChannel.open();
		InetSocketAddress local = new InetSocketAddress(portServeur);
		clientSocket.socket().bind(local);
		// on cree un buffer pour recevoir le message, on attend une réponse du serveur
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		// on recupere l'adresse du serveur
		server = (InetSocketAddress) clientSocket
				.receive(buffer);
		buffer.flip();
		try {
			// on a déjà le nom du serveur codé comme pour DNS
			byte nbChar = buffer.get();
			nomServer = "";
			for (int i = 0; i < nbChar; i++)
				nomServer += (char) buffer.get();
			// on récupère le port de connexion
			short portConnexion = buffer.getShort();
			// On joue sur nomServer nom qui contient nbChar caracteres. On se connecte sur portConnexion
			// on ferme la connexion avec le serveur sur le port 5656
			clientSocket.close();
			return portConnexion;
		} catch (BufferUnderflowException e) {
			clientSocket.close();
			throw new Exception("Le message du serveur est corrompu");
		}
	}

}

class Pair<E,V>{
	E a;
	V b;
	Pair(E a1, V b1){
		a = a1;
		b = b1;
	}
}

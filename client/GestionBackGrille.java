package client;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

// OK : calcule la nouvelle grille a afficher a partir des hashmaps de serpent
class GestionBackGrille implements Runnable {
	private ArrayBlockingQueue<Pair<HashMap<Byte, Snake>, Point>> grilleJobs;
	private GestionAffichage gameDisplay;
	private byte monNumero;
	private GestionDemandeDirection gestionnaire;
	private final int taille = utilities.GameOptions.gridSize;
	
	protected GestionBackGrille(ArrayBlockingQueue<Pair<HashMap<Byte, Snake>, Point>> jobs, GestionAffichage display, byte numero, GestionDemandeDirection gest){
		this.grilleJobs = jobs;
		this.gameDisplay = display;
		this.monNumero = numero;
		gestionnaire = gest;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Pair<HashMap<Byte, Snake>, Point> req = (Pair<HashMap<Byte, Snake>, Point>) grilleJobs.take();
				// System.out.print("On a recu un packet de serpents... ");
				byte[][] backGrille = calculBackGrille(req.a);
				backGrille[req.b.x][req.b.y] = GestionAffichage.POMME;
				gameDisplay.swap(backGrille);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private byte[][] calculBackGrille(HashMap<Byte, Snake> req) {
		// System.out.print("On appele calculBackGrille... ");
		byte[][] grille = new byte[taille][taille];
		for (Snake s : req.values())
			afficher(s, grille);
		return grille;
	}
	
	private void afficher(Snake s, byte[][] grille) {
		// System.out.print("On regarde le serpent numero " + s.numero + "... ");
		byte couleur = GestionAffichage.PLEIN;
		if(s.numero==monNumero){
			// System.out.println("je vais dans la direction " + s.direction);
			couleur = GestionAffichage.PERSO;
			gestionnaire.setDirection(s.direction);
		}
		for (Point p : s.points)
			grille[p.x][p.y] = couleur;
	}
}

package client;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

// NORMALEMENT OK : interface graphique + gestion des demandes du clavier
@SuppressWarnings("serial")
public class GestionAffichage extends JComponent implements KeyListener {
	protected static final byte VIDE = 0, PLEIN = 1, POMME = 2, PERSO = 3,
			cellSize = 10;
	private byte[][] grille; // la grille est calculee par un autre thread
	private JFrame graphe;
	private ArrayBlockingQueue<Byte> demandeDir;
	private JLabel a = new JLabel();
	private final int taille = utilities.GameOptions.gridSize;

	protected void swap(byte[][] backGrille) {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					grille = backGrille;
					paintImmediately(0, 0, getWidth(), getHeight());
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected GestionAffichage(String nomServer, int num, ArrayBlockingQueue<Byte> dir) {
		grille = new byte[taille][taille];
		setGraphe(nomServer, num);
		demandeDir = dir;
	}

	private void setGraphe(String nomServer, int num) {
		graphe = new JFrame("JEU SNAKE MULTIJOUEUR SUR "
				+ nomServer.toUpperCase() + " VOUS ETES LE JOUEUR " + num);
		addKeyListener(this);
		graphe.setBounds(400, 100, (taille+1) * cellSize, (taille + 3) * cellSize);
		a.setBounds(taille * cellSize/4,taille * cellSize/2, taille * cellSize, taille * cellSize/4);
		graphe.add(a);
		graphe.add(this);
		setFocusable(true);
		requestFocusInWindow();
		graphe.setVisible(true);
		setFocusable(true);
	}

	@Override
	public void paintComponent(Graphics g) {
		for (int i = 0; i < taille; i++)
			for (int j = 0; j < taille; j++)
				fill(g, i, j, grille[i][j]);
		// this.remove(a);
	}

	private void fill(Graphics g, int i, int j, byte color) {
		g.setColor((color == VIDE) ? Color.WHITE
				: (color == PLEIN) ? Color.BLACK : (color == POMME) ? Color.RED
						: (color == PERSO) ? Color.blue : Color.gray);
		g.fillRect(i * cellSize + cellSize / 16, j * cellSize + cellSize / 16,
				cellSize - cellSize / 8, cellSize - cellSize / 8);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		byte a = (byte) (e.getKeyCode()-37);
		if(a>=0 && a<4)
			try{
				demandeDir.add(a);
			}catch(IllegalStateException t){
			}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	public void print(String string) {
		paintImmediately(0, 0, getWidth(), getHeight());
		a.setText(string);
	}
}

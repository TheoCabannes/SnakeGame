package client;
import java.awt.Color;
import java.util.LinkedList;

// La classe Snake code les serpents sur une liste de points
public class Snake {
	public byte direction;
	byte numero;
	LinkedList<Point> points;

	public Snake(byte direction, byte numero, LinkedList<Point> points){
		this.direction=direction;
		this.numero=numero;
		this.points=points;
	}
	
	@Override
	public String toString(){
		String s = "Snake " + numero + " [";
		for (Point p : points) {
			s += p.toString();
			s += ",";
		}
		s += "] goes " + direction;
		return s;
	}
}

// Les points sont deux coordonnees et une couleur
class Point {
	int x, y;
	Color color;
	private final int taille = utilities.GameOptions.gridSize;

	Point(int x, int y) {
		while (x < 0)
			x += taille;
		while (y < 0)
			y += taille;
		this.x = x % taille;
		this.y = y % taille;
		color = Color.white;
	}
	
	@Override
	public String toString(){
		return "("+x+","+y+")";
	}
}
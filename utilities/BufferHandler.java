package utilities;

import java.nio.ByteBuffer;
import java.util.HashSet;

public class BufferHandler {
	public static ByteBuffer sendTimer(byte timer){
		ByteBuffer buffer=ByteBuffer.allocate(2);
		buffer.put((byte)1);
		buffer.put(timer);
		return buffer;
	}
	
	public static ByteBuffer sendScores(HashSet<Snake> snakes){
		ByteBuffer buffer=ByteBuffer.allocate(1+1+3*snakes.size());
		buffer.put((byte)3);
		buffer.put((byte)snakes.size());
		for(Snake s:snakes){
			buffer.put(s.id);
			buffer.putShort((short)s.score);
		}
		return buffer;
	}
	
	public static ByteBuffer goPlayThere(int gamePort, byte clientId){
		ByteBuffer buffer=ByteBuffer.allocate(4);
		buffer.put((byte)0);
		buffer.putShort((short)gamePort);
		buffer.put(clientId);
		return buffer;
	}

	public static ByteBuffer helloClient(String serverName, int connectionPort) {
		/**
		 * Creates a buffer ServerName and connectionPort
		 */
		ByteBuffer attenteJoueur = ByteBuffer.allocate(1024);
		byte taille = (byte) serverName.length();
		attenteJoueur.put(taille);
		for (int i = 0; i < taille; i++)
			attenteJoueur.put((byte) serverName.charAt(i));
		attenteJoueur.putShort((short) connectionPort);

		attenteJoueur.flip();
		return attenteJoueur;
	}

}

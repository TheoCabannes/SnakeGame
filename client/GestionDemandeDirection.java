package client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;

public class GestionDemandeDirection implements Runnable {
	private BlockingDeque<Pair<Byte, Byte>> directionJobs;
	private ArrayBlockingQueue<Byte> demandeDir;
	private byte direction, id;

	public GestionDemandeDirection(BlockingDeque<Pair<Byte, Byte>> jobs, ArrayBlockingQueue<Byte> demandeDir) {
		id = 0;
		directionJobs = jobs;
		this.demandeDir = demandeDir;
	}

	@Override
	public void run() {
		while (!demandeDir.isEmpty()) {
			try {
				demandeDir.take();
			} catch (InterruptedException e1) {
			}
		}
		while (true) {
			try {
				byte a = demandeDir.take();
				ajouterDemandeDirection(a);
			} catch (InterruptedException e) {
			}
		}
	}

	public void setDirection(byte directionBis) {
		// System.out.println("appele a setDirection avec direction = " +
		// directionBis); pas cacul par gestion affichage new thread
		direction = directionBis;
		if (!directionJobs.isEmpty()) {
			byte maDirection = directionJobs.element().a;
			if (direction == maDirection)
				directionJobs.remove();
		}
	}

	void ajouterDemandeDirection(byte a) throws InterruptedException {
		if (directionJobs.isEmpty()) {
			if (direction % 2 != a % 2) {
				directionJobs.put(new Pair<Byte, Byte>(a, id));
				id++;
				synchronized (directionJobs) {
					directionJobs.notify();
				}
			}
			return;
		}
		byte lastDir = directionJobs.peekLast().a;
		if (lastDir % 2 != a % 2) {
			directionJobs.put(new Pair<Byte, Byte>(a, id));
			id++;
		}
	}
}

package utilities;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class Runnable_Input extends UDP_Listener implements Runnable {
	private ByteBuffer buffer;
	private ArrayBlockingQueue<Job> communicator;
	String parentPrefix;

	public Runnable_Input(int port, ArrayBlockingQueue<Job> communicator,
			String prefix) throws IOException {
		super(port);
		System.out.println("Runnable input initiated on port "+port);
		buffer = ByteBuffer.allocate(1000);
		this.communicator = communicator;
		this.parentPrefix = prefix;
	}

	@Override
	public void run() {
		System.out.println(parentPrefix + "_Input has been started");
		while (true) {
			try {
				InetSocketAddress remote = listen(buffer);// fills the buffer
				buffer.flip();
				Job j = new Job(buffer, remote.getHostName());// interprets this
																// buffer as a
																// Job to do
				communicator.put(j);// send this job in communicator
				buffer.clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}

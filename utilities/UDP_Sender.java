package utilities;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;


public class UDP_Sender {
	public InetSocketAddress local;
	public InetSocketAddress remote;
	public DatagramChannel channel;

	public UDP_Sender(String target, int remotePort) throws IOException{
		this.channel=DatagramChannel.open();
		local=new InetSocketAddress(0);
		channel.bind(local);
		this.remote = new InetSocketAddress(target, remotePort);
		channel.connect(remote);
		System.out.println("UDP Sender initiated to reach "+target+" on port "+remotePort);
	}
	
	public void send(ByteBuffer b) throws IOException{
		channel.write(b);
	}
}

package utilities;

import java.nio.ByteBuffer;
import java.util.HashSet;

public class Job {
	
	private Type type;
	private String address;
	private int port;
	private byte id;
	private byte direction;
	private byte jobId;
	private byte timer;
	public HashSet<Snake> snakes;

	public static enum Type {
		WANT_TO_PLAY, // type 0 recu
		READY_TO_PLAY,// type 1 recu
		MOVE, // type 2 recu
		SEND_GAME_INFO,// sends game port and player id
		SEND_WAITING_FOR_PLAYERS,//sends just tis type
		SEND_TIMER,//sends timer before this game begins
		SEND_POSITIONS,//sends all the snakes positions
		SEND_SCORES,//sends this type
		UNKNOWN // default
	}

	public Job(Type type) {
		this.type = type;
		snakes=new HashSet<Snake>();
	}

	public Job(ByteBuffer buf, String address) {
		this.address=address;
		byte type = buf.get();
		switch (type) {
		case 0:
			this.type=Type.WANT_TO_PLAY;
			//this.jobId=buf.get();
			this.port= buf.getShort();
			break;
		case 1:
			this.type=Type.READY_TO_PLAY;
			this.id= buf.get();
			break;
		case 2:
			this.type=Type.MOVE;
			this.jobId=buf.get();
			this.id= buf.get();
			this.direction=buf.get();
			break;
		default:
			this.type=Type.UNKNOWN;
			break;
		}
	}
	
	public Type type() {
		return type;
	}
	public void type(Type t){
		type=t;
	}
	
	public String address(){
		return address;
	}
	public void address(String a){
		this.address=a;
	}
	
	public int port(){
		return this.port;
	}
	public void port(int p){
		this.port=p;
	}
	
	public byte direction(){
		return this.direction;
	}
	public void direction(byte d){
		 this.direction=d;
	}
	
	public byte id(){
		return this.id;
	}
	public void id(int id){
		this.id=(byte) id;
	}
	
	public byte jobId(){
		return this.jobId;
	}
	public void jobId(int jobId){
		this.jobId=(byte) jobId;
	}
	public byte timer(){
		return this.timer;
	}
	public void timer(byte t){
		this.timer=t;
	}
}

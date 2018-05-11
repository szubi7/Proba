package agent;
import java.io.*;
import java.net.*;
import java.util.*;

public class Agent implements Runnable{
	
	public Agent(String[] names, int agency, String secret, int num ) {
		this.names = names;
      	this.agency = agency;
      	this.knownSecret.add(secret);
      	this.num = num;
	}

	Random rand = new Random();
	String[]  names;
  	int agency;
	int port;
  	Map<String,boolean> knownSecret = new HashMap();
  	int num;
  	
  
  	

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		System.out.println("Start "+ names[0] + " agent");
		Thread cli = new Thread(new Client(port,this));
		Thread ser = new Thread(new Server(createServer(),this));

		ser.start();
		
		cli.start();
		
	}
	
	public ServerSocket createServer() {		
		while(true) {
			try {
				port = rand.nextInt(10010-10000)+1+10000;
				return new ServerSocket(port, 10);
			}catch(IOException e) {System.out.println("Foglalt");continue;}
		}
	}
  
  	public String sendSecret() {
		Random rand = new Random();
		return knownSecret.get(rand.nextInt(knownSecret.size()));
	}
  
  	public void close(Socket s, Scanner sc, PrintWriter pw){
      sc.close();
      pw.close();
      s.close();
    }

}






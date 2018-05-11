package agent;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.Scanner;

class Server implements Runnable{
	
	ServerSocket server;
	Agent a;
	private Boolean running = true;
	Socket s = null;
	
	
	public Server(ServerSocket server, Agent a) {
		this.server = server;
		this.a = a;
	}
	
	@Override
	public void run() {
		if(running) {
			server = a.createServer();
			try {
				server.setSoTimeout(5000);
				System.out.println("Wait for connection "+ a.names[0]+" with " + server.getLocalPort());
				s = server.accept();
				System.out.println("Accept "+ a.names[0] + " with "+ server.getLocalPort());
				
				running=false;
			} catch (SocketException e) {
				System.out.println("TimeOut "+ a.names[0]);
			} catch (IOException e) {System.out.println("random IOEx"); }
			finally {
				try {
					server.close();
				} catch (IOException e) {}
			}
			run();
		}
		
	}
	
	public String sendName(PrintWriter serPw) {
		Random rand = new Random();
		return a.names[rand.nextInt(a.names.length)];
	}
	
}
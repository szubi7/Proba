package agent;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

class Server implements Runnable{
	
	ServerSocket server;
	Agent a;
	private Boolean running = true;
	Socket s = null;
	
	
	public Server( Agent a) {
		this.a = a;
	}
	
	@Override
	public void run() {
		Random rand = new Random();
		if(running) {
			server = createServer();
			try {
				server.setSoTimeout(5000);
				System.out.println("SERVER: Wait for connection "+ a.names[0]+" with " + server.getLocalPort());
				s = server.accept();
				System.out.println("Create ser cs pw");
				Scanner sc      = new Scanner(s.getInputStream(), "utf-8");
				PrintWriter pw  = new PrintWriter(s.getOutputStream());
				System.out.println("Created ser cs pw");
				System.out.println("SERVER: Accept "+ a.names[0] + " with "+ server.getLocalPort());
				try {
					System.out.println("proba ser");
					pw.println("OK");
					sc.hasNextLine();
				}catch (NoSuchElementException e) {System.out.println("NoSuch ex ser");run();}

				
				pw.println(sendName());// 1. A szerver elküldi az álnevei közül az egyiket véletlenszerûen.
              	if(sc.nextInt() == a.agency){//2.Szerver fogadja
              		System.out.println("The agency mached");
                  	pw.println("OK");//4.Különben a szerver elküldi az OK szöveget.
              		if(sc.nextLine() == "OK"){
	                  pw.println(a.secrets.get(rand.nextInt(a.secrets.size())));//5.majd mindketten elküldenek egy-egy titkos szöveget a másiknak, amit ismernek
	                  a.secrets.add(sc.nextLine());
	                  close(s,sc,pw);//5. és ezután bontják a kapcsolatot
                    }else {
                    	if(sc.nextInt() == a.number) {//Ha helyes a sorszám, elküldi az általa ismert titkok egyikét.
                    		pw.println(sendNewSecret());//6.2 Ha helyes a sorszám, elküldi az általa ismert titkok egyikét
                    	}else {
                    		close(s,sc,pw);//6.1 A szerver azonnal bontja a kapcsolatot, ha téves a sorszám.
                    	}
                    }
                }else{
                	System.out.println("The agency no mached");
                  	close(s,sc,pw);//3.Ha a kliens tévedett, akkor a szerver bontja a kapcsolatot.
                }
				
              	if(a.secretBetray.contains(false)) {
              		running=true;
              	}else {
              		a.catched = true;
              		running=false;}
              	
				
			} catch (SocketTimeoutException e) {
				System.out.println("SERVER: TimeOut "+ a.names[0]);
				try {
						server.close();
						System.out.println("server Close "+ a.names[0]);
						server=null;
				} catch (IOException eIO) {}
				catch (NullPointerException eNULL) {}
			} catch (IOException e) {System.out.println("random IOEx"); }
			finally {
				try {
						s.close();
						s=null;
						
						server.close();
						server=null;
				} catch (IOException e) {}
				catch (NullPointerException e) {}
			}
			checkAllCatch();
			if(AgentMain.sRunning) {
				run();
			}
		}
		
	}
	
	private void checkAllCatch() {
		boolean find = false;
		int i=0;
		while(!find && i<AgentMain.n) {
			find = !AgentMain.agents1.get(i).catched;
		}
		while(!find && i<AgentMain.m) {
			find = !AgentMain.agents2.get(i).catched;
		}
		if(!find) {
			AgentMain.cRunning = false;
			AgentMain.sRunning = false;
		}
		
	}

	public String sendName() {
		Random rand = new Random();
		return a.names[rand.nextInt(a.names.length)];
	}
	
	public ServerSocket createServer() {	
		Random rand = new Random();
		while(true) {
			try {
				synchronized (a) { 
				a.port = rand.nextInt(10010-10000)+1+10000;}
				return new ServerSocket(a.port);
			}catch(IOException e) {continue;}
		}
	}
	
	
	private void close(Socket s, Scanner sc, PrintWriter pw) throws IOException {
		sc.close();
		pw.close();
		s.close();
		s=null;
		
	}
	
	private String sendNewSecret() {
		Random rand = new Random();
		int r=rand.nextInt(a.secrets.size());
		while(!a.secretBetray.get(r)) {
			r=rand.nextInt(a.secrets.size());
		}
		a.secretBetray.set(r, true);
		return a.secrets.get(r);
	}
}
package agent;

import java.io.*;
import java.net.*;
import java.util.*;

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
      Random rand = new Random();
		if(running) {
			server = a.createServer();
			try {
				server.setSoTimeout(5000);
              
				System.out.println("Wait for connection "+ a.names[0]+" with " + server.getLocalPort());
			
              	s = server.accept();
              
              	Scanner sc      = new Scanner(s.getInputStream(), "utf-8");
				PrintWriter pw  = new PrintWriter(s.getOutputStream());
				System.out.println("Accept "+ a.names[0] + " with "+ server.getLocalPort());
              
              	pw.println(sendName());// 1. A szerver elküldi az álnevei közül az egyiket véletlenszerűen.
              	if(sc.nextInt() == a.agency){//2.Szerver fogadja
                  	pw.println("OK");//4.Különben a szerver elküldi az OK szöveget.
                  		if(sc.nextInt() == "OK"){
                          pw.println(a.sendSecret());//5.majd mindketten elküldenek egy-egy titkos szöveget a másiknak, amit ismernek
                          sec = sc.nextLine();
                          
                          a.close(s,sc,pw);//5. és ezután bontják a kapcsolatot
                        }
                }else{
                  	a.close(s,sc,pw);//3.Ha a kliens tévedett, akkor a szerver bontja a kapcsolatot.
                }
				
				running=false;
			} catch (SocketException e) {
				System.out.println("TimeOut "+ a.names[0]);
              
			} catch (IOException e) {
              	System.out.println("random IOEx"); 
            }
			finally {
				try {
					server.close();
				} catch (IOException e) {}
			}
			run(); //restart
		}
		
	}
	
	public String sendName() {
		Random rand = new Random();
		return a.names[rand.nextInt(a.names.length)];
	}
	
}
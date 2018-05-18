package agent;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

class Client implements Runnable{

	private Agent a;
	int p ;
	private Map<String,Integer> knowAgencys= new HashMap<>();
	private Map<String,Integer> knowNumbers= new HashMap<>();

	public Client(Agent a) {
		this.a = a;
	}
	
	
	
	@Override
	public void run() {
		if(AgentMain.cRunning) {
			boolean passAge = false;
			Random rand = new Random();
			Socket client = createClient();
			while(client == null) {
				client = createClient();	
			}
			System.out.println("CLIENT: "+a.names[0]+" connected with" + client.getPort()+" conected:"+client.isConnected());
			try {
				Scanner sc      = new Scanner(client.getInputStream(), "utf-8");
				PrintWriter pw  = new PrintWriter(client.getOutputStream());
				try {
					pw.println("OK");
					pw.flush();
					if(sc.hasNextLine()) {
						String line = sc.nextLine();
					}else {run();}
					System.out.println("proba OK");
				}catch (NoSuchElementException e) {System.out.println("NoSuch ex");run();}
				catch (Exception e) {System.out.println("exxxxxxx:"+e.getClass());}
				
				String gettedName = sc.nextLine();//1. Client fogadja
				System.out.println("Client getted name: "+ gettedName);
				int age=0;
				if(knowAgencys.containsKey(gettedName)) {//2.Erre a kliens elküldi azt, hogy szerinte a szerver melyik ügynökséghez tartozik.
					System.out.println("CLIENT:Know the name");
					pw.println(knowAgencys.get(gettedName));//2.Ha a kliens már találkozott ezzel az álnévvel, akkor tudja a helyes választ erre a kérdésre, és azt küldi el
					pw.flush();
					System.out.println("CLIENT:send the good agency");
					sc.nextLine();//getting OK
					passAge=true;
				}else {
					System.out.println("CLIENT:Don't know the name");
					age = rand.nextInt(2)+1;
					pw.println(age);//2.különben tippel
					pw.flush();
					System.out.println("CLIENT:send the tipp agency");
					try {
						sc.nextLine();//getting OK
						knowAgencys.put(gettedName, age);
						passAge=true;
						
					}catch(NoSuchElementException e) {//3.Ha a kliens tévedett, akkor a szerver bontja a kapcsolatot.
						System.out.println("CLIENT:Agency is not ok");
						if(age==1) {
							knowAgencys.put(gettedName, 2);
						}else
							knowAgencys.put(gettedName, 1);
						close(client,sc,pw);
					}
				}
				int num = 0;
				if(passAge) {
					System.out.println("CLIENT:Agency is ok");
					if(a.agency == age) {//5.A kliens, ha azonos ügynökséghez tartozik, elküldi az OK szöveget,
						System.out.println("CLIENT:Agency is same");
						pw.println("OK");
						pw.flush();
						pw.println(a.secrets.get(rand.nextInt(a.secrets.size())));
						pw.flush();
						System.out.println("CLIENT:Send random secret no betray");
						a.secrets.add(sc.nextLine());
						close(client,sc,pw);//5. és ezután bontják a kapcsolatot
					}else {
						System.out.println("CLIENT:Agency is not same");
						pw.println("???");//6.A kliens, ha a másik ügynökséghez tartozik, elküldi a ??? szövege
						pw.flush();
						if(knowNumbers.containsKey(gettedName)) {//6. majd egy számot, ami szerinte a másik ügynök sorszáma lehet.
							pw.println(knowAgencys.get(gettedName));
							pw.flush();
							System.out.println("CLIENT:Know the number");
						}else {
							System.out.println("CLIENT:tipping a number");
							if(age == 1) {
								num = rand.nextInt(AgentMain.n)+1;
								pw.println(num);
								pw.flush();
							}else {
								num = rand.nextInt(AgentMain.m)+1;
								pw.println(num);
								pw.flush();
							}
						}
						try {
							System.out.println("CLIENT:Number is ok");
							String newSecret = sc.nextLine();
							a.newSecrets.add(newSecret);
							a.secrets.add(newSecret);
							a.secretBetray.add(false);
						}catch(NoSuchElementException e) {System.out.println("CLIENT:Number is not ok");}
					}
				}
				
			} catch (IOException e) {
				System.out.println("io ex");
			}catch (Exception ex) {
				System.out.println("sc or pw Exception"+ex.getClass());
			}
			
			
			//System.out.println("Address:"+ client.getInetAddress().getHostAddress()+"Name: "+client.getInetAddress().getHostName());
			
			try {
				client.close();
				System.out.println("client Close "+ a.names[0]);
				client=null;
			} catch (IOException e) {
				
			}
			checkAllSecret();
			run();
		}	 
	}
	
	private Socket createClient() {

		Random rand = new Random();
		int p = rand.nextInt(20010-20000)+1+20000;
		try {
			Thread.sleep(rand.nextInt(200));
			//Thread.sleep(rand.nextInt(AgentMain.t2-AgentMain.t1)+1+AgentMain.t1);
		} catch (InterruptedException e) {System.out.println("Interrupt");}
		//System.out.println("Server port: "+ a.port +" with "+p+ " NAME: "+a.names[0]);
		synchronized(a) {
			if(p!=a.port) {
				try {
					return new Socket("localhost",p);
				}catch(IOException e) {
					//System.out.println("random IO");
					return null;
				}
				catch (Exception e){System.out.println(e.getClass()); return null;}
				
			}else {
				return null;
			}
		}
	}
	
	private void close(Socket s, Scanner sc, PrintWriter pw) throws IOException {
		sc.close();
		pw.close();
		s.close();
		s=null;
		
	}
	
	public void checkAllSecret() {
		if(a.agency == 1) {
			if(a.newSecrets.size() == AgentMain.m) {
				AgentMain.cRunning = false;
				AgentMain.sRunning = false;
			}
		}else {
			if(a.newSecrets.size() == AgentMain.n) {
				AgentMain.cRunning = false;
				AgentMain.sRunning = false;
			}
		}
		
	}
	
}


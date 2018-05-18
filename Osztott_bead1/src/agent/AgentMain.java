package agent;

import java.io.*;
import java.util.*;

public class AgentMain {
	public static int n = 0;
	public static int m = 0;
	public static int t1 = 0;
	public static int t2 = 0;
	public static List<Agent> agents1 = new ArrayList<>();
	public static List<Agent> agents2 = new ArrayList<>();
	static boolean cRunning = true;
	static boolean sRunning=true;
	
	
	public static void main (String[] args) {
		if(args.length >= 4) {
			try {
				n = Integer.parseInt(args[0]);
				m = Integer.parseInt(args[1]);
				t1 = Integer.parseInt(args[2]);
				t2 = Integer.parseInt(args[3]);
			}catch (NumberFormatException e){
				System.out.println("Not valid parameters");
			}
		}
		
		for(int i=0; i < n; i++) {
			String filename = "agent1-"+(i+1)+".txt";
			try(BufferedReader br = new BufferedReader(new FileReader(filename))){
				String nameLine = br.readLine();
				String[] names = nameLine.split(" ");
				String secret = br.readLine();
				br.close();
				Agent a = new Agent(names,1,(i+1),secret);
				agents1.add(a);
				Thread t = new Thread(a);
				t.start();
				
			}catch(IOException e) {System.out.println("1es hiba");}
		}
		
		for(int i=0; i < m; i++) {
			String filename = "agent2-"+(i+1)+".txt";
			try(BufferedReader br = new BufferedReader(new FileReader(filename))){
				String nameLine = br.readLine();
				String[] names = nameLine.split(" ");
				String secret = br.readLine();
				br.close();
				Agent a = new Agent(names,2,(i+1),secret);
				agents2.add(a);
				Thread t = new Thread(a);
				t.start();
				
			}catch(IOException e) {System.out.println("2es hiba");}
		}
	}
	
}

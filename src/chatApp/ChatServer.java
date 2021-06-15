package chatApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ChatServer {
	
	private ArrayList<PrintWriter> clientOutputStreams;
	
	public class ClientHandler implements Runnable {
		private BufferedReader reader;
		private Socket clientSocket;
		
		public ClientHandler(Socket socket) {
			clientSocket = socket;
			try {
				InputStreamReader streamReader = new InputStreamReader(clientSocket.getInputStream());
				reader = new BufferedReader(streamReader);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			String message;
	
			try {
				while((message = reader.readLine()) != null) {
					System.out.println("read " + message);
					tellEveryone(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void go() {
		clientOutputStreams = new ArrayList<PrintWriter>();
		
		try {
			ServerSocket serverSocket = new ServerSocket(5000);
			
			while(true) {
				Socket clientSocket = serverSocket.accept();
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				clientOutputStreams.add(writer);
				
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
				System.out.println("got a connection");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void tellEveryone(String message) {
		Iterator<PrintWriter> it = clientOutputStreams.iterator();
		while(it.hasNext()) {
			PrintWriter writer = it.next();
			writer.write(message);
			writer.flush();
		}
	}
	
}

package chatApp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class ChatClient {
	
	private JTextArea incoming;
	private JTextField outgoing;
	private BufferedReader reader;
	private PrintWriter writer;
	private Socket sock;
	
	public void go() {
		JFrame frame = new JFrame("Simple Chat Client");
		JPanel mainPanel = new JPanel();
		incoming = new JTextArea(15, 50);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		outgoing = new JTextField(20);
		JButton button = new JButton("Send");
		button.addActionListener(new SendButtonListener());
		
		mainPanel.add(qScroller);
		mainPanel.add(outgoing);
		mainPanel.add(button);
		
		setUpNetworking();
		
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
		
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.setSize(350, 100);
		frame.setVisible(true);
	}
	
	private void setUpNetworking() {
		try {
			sock = new Socket("127.0.0.1", 5000);
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(streamReader);
			writer = new PrintWriter(sock.getOutputStream());
			System.out.println("networking established");
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public class SendButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			writer.println(outgoing.getText());
			writer.flush();
			
			outgoing.setText("");
			outgoing.requestFocus();
		}
		
	}
	
	public class IncomingReader implements Runnable {

		@Override
		public void run() {
			String message;
			
			try {
				while((message = reader.readLine()) != null) {
					System.out.println("read " + message);
					incoming.append(message + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		new ChatClient().go();
	}

}

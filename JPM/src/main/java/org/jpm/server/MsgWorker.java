package org.jpm.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.jpm.receiver.MessageReceiver;

/**
 * @author Ashok Das
 * @since 21-10-2017
 * @see <a href="mailto:jackwenttohill@yahoo.co.uk?Subject=Hello%20World">Hello World</a>
 */
public class MsgWorker extends Thread {

	private Socket socket;
	private MessageReceiver receiver = null;

	public MsgWorker(Socket socket, MessageReceiver receiver) {
		this.socket = socket;
		this.receiver = receiver;
	}

	public void run() {
		try {

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println("Enter a line with only a period to quit\n");

			while (true) {
				String input = in.readLine();
				if (input == null) {
					break;
				}
				receiver.receive(input);
			}
		} catch (IOException e) {

		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}
}
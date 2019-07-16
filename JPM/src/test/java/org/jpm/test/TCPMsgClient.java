package org.jpm.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import org.jpm.util.PropertyReader;

/** test
 * @author Ashok Das
 * @since 21-10-2017
 * @see <a href="mailto:jackwenttohill@yahoo.co.uk?Subject=Hello%20World">Hello World</a>
 */
public class TCPMsgClient {

	private PrintWriter out;
	private Socket socket = null;
	private PropertyReader pReader = new PropertyReader();
	
	public void connectToServer() throws IOException {
		
		socket = new Socket(pReader.getValue("tcpHost"), Integer.parseInt(pReader.getValue("tcpPort")));
		out = new PrintWriter(socket.getOutputStream(), true);
	}
	
	public void close() throws IOException {
		out.close();
		socket.close();
	}

	public void readFileAndPush(String fileAbsolutePath) {

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(fileAbsolutePath));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				out.println(line);
				out.flush();
			}
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		scanner.close();
	}

	public static void main(String[] args) throws Exception {
		TCPMsgClient client = new TCPMsgClient();
		client.connectToServer();
		client.readFileAndPush("src/test/resources/input50.txt");
	}
}

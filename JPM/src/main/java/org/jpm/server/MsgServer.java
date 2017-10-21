package org.jpm.server;

import java.io.IOException;
import java.net.ServerSocket;

import org.jpm.receiver.MessageReceiver;
import org.jpm.util.PropertyReader;

/**
 * @author Ashok Das
 * @since 21-10-2017
 * @see <a href="mailto:jackwenttohill@yahoo.co.uk?Subject=Hello%20World">Hello World</a>
 */
public class MsgServer {

	private final MessageReceiver receiver = new MessageReceiver();
	private PropertyReader pReader = new PropertyReader();
	private ServerSocket server = null;
			
	public void startServer() throws IOException {
		
		try {
			server = new ServerSocket(Integer.parseInt(pReader.getValue("tcpPort")));
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
        try {
            while (true) {
                try {
					new MsgWorker(server.accept(), receiver).start();
				} catch (IOException e) {
				}
            }
        } finally {
            server.close();
        }
	}
	
	public void stopServer() {
		try {
			if (server!=null)
              server.close();
        } catch(IOException e) {
        	e.printStackTrace();
        }
	}

	public MessageReceiver getMessageReceiver() {
		return receiver;
	}
	
	public boolean isSeerverClosed() {
		if (server != null)
			return server.isClosed();
		else
			return true;
	}
	
	/**
	 * Messaging Server listen to any client connected.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		MessageReceiver rec = new MessageReceiver();
		ServerSocket listener = new ServerSocket(9898);
        try {
            while (true) {
                new MsgWorker(listener.accept(), rec).start();
            }
        } finally {
            listener.close();
        }
	}
}
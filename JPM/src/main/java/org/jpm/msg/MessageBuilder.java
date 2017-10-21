package org.jpm.msg;

/**
 * @author Ashok Das
 * @since 21-10-2017
 * @see <a href="mailto:jackwenttohill@yahoo.co.uk?Subject=Hello%20World">Hello World</a>
 */
public class MessageBuilder {

	public Message getMessageByType(MessageFormat msgFormat, String msg) {
		
		Message message = null;
		
		switch(msgFormat) {
			
		 case TEXT:
			 message = new TextMessage(msg);
			 break;
		 case XML:
			 message = new XMLMessage(msg);
			 break;
		}
		return message;
	}
}
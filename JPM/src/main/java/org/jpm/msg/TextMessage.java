package org.jpm.msg;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jpm.product.OperationType;
import org.jpm.product.Product;
import org.jpm.util.PropertyReader;

/**
 * @author Ashok Das
 * @since 21-10-2017
 * @see <a href="mailto:jackwenttohill@yahoo.co.uk?Subject=Hello%20World">Hello World</a>
 */
public class TextMessage implements Message {

	private String msg = null;
	private PropertyReader prop = new PropertyReader();
	private Logger logger = Logger.getLogger(TextMessage.class.getName());
	private List<String> listOfMsgType = null;
	
	public TextMessage(String msg) {
	
		this.msg = msg;
		String validMsg[] = prop.getValue("msgValidType").split(",");
		listOfMsgType = Arrays.asList(validMsg);
	}

	@Override
	public boolean validate() {
		
		if (msg == null ) {
			logger.log(Level.WARNING, "Message is null");
			return false;
		}
		
		if (msg.length() < Integer.parseInt(prop.getValue("minMsgLength")) ) {
			logger.log(Level.WARNING, "Invalid message length");
			return false;
		}
		
		if (!isValid(false)) {
			logger.log(Level.WARNING, "Message is not valid.");
			return false;
		}
		return true;
	}
	
	@Override
	public Product construct() {
		
		Product p = new Product();
		addType(p);
		return p;
	}

	private void addType(Product p) {
		
		if (isStartWith(p)) {
			//Adjustment received.
		} else if (Character.isDigit(msg.charAt(0))) {
			p.setType(OperationType.RECORD);
			recordSales(p);
		} else if (isValid(false)) {
			p.setType(OperationType.LOG);
		} else {
			p.setType(OperationType.PRPOCESS);
		}
	}
	
	private void recordSales(Product p) {

		StringTokenizer token = new StringTokenizer(msg, " ");
		while(token.hasMoreTokens()) {
			String s= token.nextToken();
			if (isQty(s)) {
				p.setQuantity(Integer.parseInt(s));
			} else if(checkProductType(s)) {
				p.setName(s.substring(0, s.length()-1));
			} else if(isPrice(s)) {
				float price = 0;
				try {
						price = Float.parseFloat(s.substring(0,s.length()-1));
				} catch(Exception e) {
					e.printStackTrace();
				}
				p.setPrice(price);
			}
		}
	}
	
	private boolean isQty(String s)	{
		
		 try { 
			   Integer.parseInt(s); 
			   return true; 
		} catch(NumberFormatException er) { 
			 return false; 
	    }
	}

	private boolean checkProductType(String token) {
	
		for (String s : listOfMsgType) {
			if (token.equals(s)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isPrice(String str) {
		return Character.isDigit(str.charAt(0));
	}
	
	private boolean isValid(boolean isStartWith) {
		
		for (String s : listOfMsgType) {
			if (isStartWith) {
				if (msg.startsWith(s)) {
					return true;
				}
		    } else {
		    	if (isContainKeyWord(msg, s)) {
					return true;
				}
		    }
		}
		return false;
	}
	
	private boolean isContainKeyWord(String msg, String match){
	    String pattern = "\\b"+match+"\\b";
	    Pattern p=Pattern.compile(pattern);
	    Matcher m=p.matcher(msg);
	    return m.find();
	}
	
	private boolean isStartWith(Product p) {
		
		boolean flag = false;
		
		if (msg.startsWith(OperationType.ADD.getOperationType())) {
			p.setType(OperationType.ADD);
			adjustProduct(p);
			flag = true;
		} else if (msg.startsWith(OperationType.MULTIPLY.getOperationType())) {
			p.setType(OperationType.MULTIPLY);
			adjustProduct(p);
			flag = true;
		} else if (msg.startsWith(OperationType.SUBSTRACT.getOperationType())) {
			p.setType(OperationType.SUBSTRACT);
			adjustProduct(p);
			flag = true;
		}
		return flag;
	}
	
	private void adjustProduct(Product p) {
		
		StringTokenizer token = new StringTokenizer(msg, " ");
		token.nextToken();
		while(token.hasMoreTokens()) {
			String s = token.nextToken();
			if(Character.isDigit(s.charAt(0))) {
				try {
					  p.setAdjustPrice(Float.parseFloat(s));
					  break;
				} catch (NumberFormatException e) {
					try {
							p.setAdjustPrice(Float.parseFloat(s.substring(0,s.length()-1)));
					} catch(NumberFormatException ne) {
						if (p.getType().getOperationType().equals(OperationType.MULTIPLY.getOperationType()))
							p.setAdjustPrice(1);
						else
							p.setAdjustPrice(0);
					}
				}
			} else {
				p.setName(s.substring(0,s.length()-1));
			}
		}
	}
}
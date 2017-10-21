package org.jpm.receiver;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jpm.msg.Message;
import org.jpm.msg.MessageBuilder;
import org.jpm.msg.MessageFormat;
import org.jpm.product.Product;
import org.jpm.product.Report;
import org.jpm.util.PropertyReader;

/**
 * @author Ashok Das
 * @since 21-10-2017
 * @see <a href="mailto:jackwenttohill@yahoo.co.uk?Subject=Hello%20World">Hello World</a>
 */
public class MessageReceiver {
	
	private Logger logger = Logger.getLogger(MessageReceiver.class.getName());
	private MessageBuilder msgBuilder = null;
	private List<Product> pList = new ArrayList<Product>();
	private List<Product> adjustList = new ArrayList<Product>();
	private PropertyReader pReader = new PropertyReader();
	private String padding = new String(new char[64]);
	private boolean isPause = false;
	
	public MessageReceiver() {
	
		//Disabling the log.
		if(pReader.getValue("disableLogging").equalsIgnoreCase("TRUE"))
			LogManager.getLogManager().reset();
	}
	
	public void receive(String line) throws FileNotFoundException {

		msgBuilder = new MessageBuilder();
		//Process all messages
        Product p = process(line);

        if (p!= null) {
        	//Record all sales
    		record(p);
    		//report 10
            logPeriodicalReport();
            //report 50
     		logAdjustmentReport(p);
    	}
	}

	private Product process(String line) {

		logger.log(Level.INFO, "Message received:" + line);
		Product p = null;
		Message message = msgBuilder.getMessageByType(MessageFormat.TEXT, line);
		
        if (message.validate()) {
        	logger.log(Level.INFO, "Only valid message will be processed");
        	p = message.construct();
        }
        return p;
	}

	private void record(Product p) {
		pList.add(p);		
	}

	private void logPeriodicalReport() {

		if   ((pList.size() % Integer.parseInt(pReader.getValue("logReportPerMessageReceived")) == 0) && !isPause ) {
			printPeriodicalReport();
		}
	}
	
	private void logAdjustmentReport(Product p) {

		switch(p.getType()) {
		
			case ADD:
			case MULTIPLY:
			case SUBSTRACT:
				adjustList.add(p);
				break;
			case LOG:
			case PRPOCESS:
			case RECORD:
				break;
		}
		
		if  (pList.size() == Integer.parseInt(pReader.getValue("maxMessagesToPauseProcessing"))  && !isPause) {
			pause();
			logAdjustmentReport();
		}
	}
	
	private void pause() {
		
		isPause = true;
		logger.log(Level.INFO, "Reached today quota of " + pReader.getValue("maxMessagesToPauseProcessing") + " messages. Pausing.................");
	}

	private void printPeriodicalReport() {
		
		Map<String, Report> reportMap = populateReportMap();
		System.out.println("After " + pReader.getValue("logReportPerMessageReceived") + " messages received, the log report:");		
		System.out.println("+-------------------------------------+----------------+----------+");
		System.out.println("|        NAME                         |	 QUAMTITY      |   TOTAL  |");
		System.out.println("+-------------------------------------+----------------+----------+");
		for (Map.Entry<String, Report> entry : reportMap.entrySet()) {
		    
			String name = entry.getKey();
			Report r = (Report)entry.getValue();
			if (r.getName() != null ) {
				System.out.println("|" + (name + padding).substring(0, 37)  + "|"	+ (r.getQuantity() + padding).substring(0, 16) 	+ "|" + (r.getTotalPrice() + padding).substring(0, 10) + "|");
			}
		}
		System.out.println("+-------------------------------------+----------------+----------+");
		System.out.println("");
		System.out.println("");
	}
	
	private void logAdjustmentReport() {

		logger.log(Level.INFO, "Adjustment received: " + adjustList.size());
		System.out.println("After " + pReader.getValue("maxMessagesToPauseProcessing") + " messages received, the adjustment log report:");
		Map<String, Report> reportMap = populateReportMap();
		for ( Product p : adjustList) {
			//First adjustment received
			if(p != null && p.getName() != null && p.getName().length() > 0)
				adjust(p, reportMap);
		}
		logFinalAdjustmentReport(reportMap);
	}
	
	private void logFinalAdjustmentReport(Map<String, Report> reportMap) {
		System.out.println("+------------------------+");
		System.out.println("|Final Adjuistment Report|");
		System.out.println("+------------------------+");
		System.out.println("+-------------------------------------+----------------+----------+");
		System.out.println("|        NAME                         |	 QUAMTITY      |   TOTAL  |");
		System.out.println("+-------------------------------------+----------------+----------+");
		for (Map.Entry<String, Report> entry : reportMap.entrySet()) {
		    
			String name = entry.getKey();
			Report r = (Report)entry.getValue();
			if (r.getName() != null ) {
				System.out.println("|" + (name + padding).substring(0, 37)  + "|"	+ (r.getQuantity() + padding).substring(0, 16) 	+ "|" + (r.getTotalPrice() + padding).substring(0, 10) + "|");
			}
		}
		System.out.println("+-------------------------------------+----------------+----------+");
		System.out.println("");
		System.out.println("");		
	}

	private void adjust(Product p, Map<String, Report> reportMap) {
		
		for (Map.Entry<String, Report> entry : reportMap.entrySet()) {
			Report r = (Report)entry.getValue();
			if (r.getName() != null && p.getName().equals(r.getName())) {
				beforeAdjustment(r);
				afterAdjustment(r, p);
			}
		}
	}
	
	private void beforeAdjustment(Report r) {
		System.out.println("Before adjustment: " + r.getName());
		logAdjust(r);
	}
	
	private void afterAdjustment(Report r, Product p) {
		System.out.println("After adjustment: " + p.getName() + " " +  p.getType().getOperationType() + " " + p.getAdjustPrice() + "p");
		switch(p.getType()) {
		case ADD:
			r.setTotalPrice(r.getTotalPrice() + (r.getQuantity() * p.getAdjustPrice()));
			logAdjust(r);
			break;
		case MULTIPLY:
			r.setTotalPrice( r.getQuantity() * ((r.getTotalPrice()/r.getQuantity()) * p.getAdjustPrice()) );
			logAdjust(r);
			break;
		case SUBSTRACT:
			r.setTotalPrice(r.getTotalPrice() - (r.getQuantity() * p.getAdjustPrice()));
			logAdjust(r);
			break;
		case LOG:
		case RECORD:
		case PRPOCESS:
			break;
		}
	}
	
	private void logAdjust(Report r) {
		System.out.println("+-------------------------------------+----------------+----------+");
		System.out.println("|        NAME                         |	 QUAMTITY      |   TOTAL  |");
		System.out.println("+-------------------------------------+----------------+----------+");
		System.out.println("|" + (r.getName() + padding).substring(0, 37)  + "|"	+ (r.getQuantity() + padding).substring(0, 16) 	+ "|" + (r.getTotalPrice() + padding).substring(0, 10) + "|");
		System.out.println("+-------------------------------------+----------------+----------+");
		System.out.println("");
	}

	private Map<String, Report> populateReportMap() {

		Map<String, Report> reportMap = new HashMap<String,Report>();
		for (Product p : pList) {
			if (p!=null && p.getName() != null && p.getName().length() > 0 && p.getPrice() > 0 ) {
				Report r = reportMap.get(p.getName());
				if (r != null) {
					r.setQuantity(r.getQuantity() + p.getQuantity());
					r.setTotalPrice(r.getTotalPrice() + (r.getQuantity() * p.getPrice()));
				} else {
					r = new Report(p.getName(), p.getQuantity(), (p.getQuantity() * p.getPrice()));
					reportMap.put(p.getName(), r);
				}
			}
		}
		return reportMap;
	}
	
	public int messageCount() {
		return pList.size();
	}
	
	public int adjustmentCount() {
		return adjustList.size();
	}
	
	public int getQuantityByName(String productName) {
		int qty = 0;
		Map<String, Report> repMap = populateReportMap();
		Report r = (Report)repMap.get(productName);
		if (r != null && r.getName() != null ) {
			qty = r.getQuantity();
		}
		return qty;
	}
	
	public float getTotalSalePriceByName(String productName) {
		float sales = 0;
		Map<String, Report> repMap = populateReportMap();
		Report r = (Report)repMap.get(productName);
		if (r != null && r.getName() != null ) {
			sales = r.getTotalPrice();
		}
		return sales;
	}
}
package org.jpm.msg;

import org.jpm.product.Product;

/**
 * @author Ashok Das
 * @since 21-10-2017
 * @see <a href="mailto:jackwenttohill@yahoo.co.uk?Subject=Hello%20World">Hello World</a>
 */
public class XMLMessage implements Message {

	private String msg = null;
	
	public XMLMessage(String msg) {
		this.msg = msg;
	}
	
	@Override
	public Product construct() {
		//@TODO implement xml construct if received message type is xml format.
		Product p = new Product();
		p.setName(msg);
		return p;
	}

	@Override
	public boolean validate() {
		return false;
	}
}

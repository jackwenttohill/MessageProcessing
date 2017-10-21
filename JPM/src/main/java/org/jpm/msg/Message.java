package org.jpm.msg;

import org.jpm.product.Product;

/**
 * @author Ashok Das
 * @since 21-10-2017
 * @see <a href="mailto:jackwenttohill@yahoo.co.uk?Subject=Hello%20World">Hello World</a>
 */
public interface Message {

	public Product construct();
	public boolean validate();
}

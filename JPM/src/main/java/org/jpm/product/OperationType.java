package org.jpm.product;

/**
 * @author Ashok Das
 * @since 21-10-2017
 * @see <a href="mailto:jackwenttohill@yahoo.co.uk?Subject=Hello%20World">Hello World</a>
 */
public enum OperationType {

	ADD("Add"),
	SUBSTRACT("Substract"),
	MULTIPLY("Multiply"),
	LOG("Log"),
	PRPOCESS("Process"),
	RECORD("Record");
	
	private String type;
	
	private OperationType(String type) {
		this.type = type;
	}
	
	public String getOperationType() {
	  return this.type;
	}
}
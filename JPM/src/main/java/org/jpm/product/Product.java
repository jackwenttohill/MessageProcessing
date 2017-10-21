package org.jpm.product;

/**
 * @author Ashok Das
 * @since 21-10-2017
 * @see <a href="mailto:jackwenttohill@yahoo.co.uk?Subject=Hello%20World">Hello World</a>
 */
public class Product {
	
	private String name;
	private float price;
	private int quantity;
	private OperationType type;
	private float adjustPrice;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getAdjustPrice() {
		return adjustPrice;
	}
	public void setAdjustPrice(float adjustPrice) {
		this.adjustPrice = adjustPrice;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public OperationType getType() {
		return type;
	}
	public void setType(OperationType type) {
		this.type = type;
	}
}
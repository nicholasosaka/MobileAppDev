package edu.uncc.cci.mobileapps;

/*
ASSIGNMENT#: ICA1
STUDENT NAME: Nicholas Osaka
FILE NAME: StoreItem.java
 */

public class StoreItem {

	private String name;
	private int id;
	private int price;

	StoreItem(String str){
		String[] arr = str.split(",");

		this.name = arr[0];
		this.id = Integer.parseInt(arr[1]);
		this.price = Integer.parseInt(arr[2]);
	}

	@Override
	public String toString() {
		return "ID: " + getId() + "\tItem: " + getName() + "\tPrice: " + getPrice();
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public int getPrice() {
		return price;
	}
}

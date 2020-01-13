package edu.uncc.cci.mobileapps;

//import java.util.HashMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainPart4{
    /*
      Question 4
      You are provided with the Data class that contains an items array (Data.items) which is an array of items in a store. Each element in the array represents a single item record. Each record in the array represents a single item record. Each record is a string formatted as : Name, ID, Price. Also, you are provided with an array called shoppingCart (Data.shoppingCart) which is an array of items’ quantities. Each element in the array represents a single item record. Each record is a string formatted as : ID, quantity. You are asked to perform the following tasks:
      You are provided with the Data class that contains an items array (Data.items) which is an array of items in a store. Each element in the array represents a single item record.
      Each record in the array represents a single item record. Each record is a string formatted as : Name, ID, Price. Also, you are provided with an array called
      shoppingCart (Data.shoppingCart) which is an array of items’ quantities. Each element the array represents a single item record. Each record is a string formatted as : ID,quantity. You are asked to perform the following tasks:
     1. Your implementation for this question should be included in MainPart3.java file.
     2. Create a StoreItem class that should parse all the parameters for each item. Hint: extract each value from a item's record using Java's String.split() method and set the
        delimiter to a comma, see provided code below. Each item record should to be assigned to a StoreItem object.
     3. Create the most efficient data structure that best fit the goal. Hint: The selected data structure should facilitate the retrieval of the item details based on the ID.
     4. The goal is to print out the receipt bill in the following format:
        ID  Name    Quantity    Price * Quantity
        123 Tomatoes 10         $30
        .
        .
        Total Bill: $400
    */

    public static void main(String[] args) {

    	Map<Integer, StoreItem> items = new HashMap<Integer, StoreItem>();

	    for(String str : Data.items){
		    StoreItem item = new StoreItem(str);

		    items.put(item.getId(), item);
	    }

	    Map<Integer, Integer> cart = new HashMap<Integer, Integer>();

	    for(String str : Data.shoppingCart){
	    	String[] tokenized = str.split(","); //tokenize shopping cart strings

		    //parse to int
	    	int ID = Integer.parseInt(tokenized[0]);
	    	int quantity = Integer.parseInt(tokenized[1]);

	    	//add to cart
	    	cart.put(ID, quantity);
	    }

	    //total counter for sum
	    int total = 0;

	    System.out.format("%-5s%-15s%-16s%-24s%n", "ID", "NAME", "QUANTITY", "PRICE * QUANTITY");

	    //map -> entry set -> iterator
	    Iterator<Map.Entry<Integer, Integer>> iterator = cart.entrySet().iterator();

	    while(iterator.hasNext()){
	    	//grab each entry
	    	Map.Entry entry = iterator.next();
	    	//seperate key/values
	    	int ID = (int) entry.getKey();
	    	int quantity = (int) entry.getValue();

	    	//get item from id
		    StoreItem item = items.get(ID);
		    int cost = item.getPrice() * quantity;
		    System.out.format("%-5d%-15s%-16d$%-24d%n", item.getId(), item.getName(), quantity, cost);
		    total += cost;

	    }
	    System.out.println("TOTAL: $" + total);

    }
}
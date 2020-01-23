package edu.uncc.cci.mobileapps;

/*
ASSIGNMENT#: ICA1
STUDENT NAME: Nicholas Osaka
FILE NAME: MainPart2.java
 */

import java.util.*;

public class MainPart2 {
    /*
    * Question 2:
    * - In this question you will use the Data.users array that includes
    * a list of users. Formatted as : firstname,lastname,age,email,gender,city,state
    * - Create a User class that should parse all the parameters for each user.
    * - The goal is to count the number of users living each state.
    * - Print out the list of State, Count order in ascending order by count.
    * */

    public static void main(String[] args) {

        //example on how to access the Data.users array.
	    List<User> users = new ArrayList<>();
	    for (String str : Data.users) {
		    users.add(new User(str));
	    }

	    //map creation
	    HashMap<String, Integer> map = new HashMap<>();

	    //count users
	    for(User user : users){ //for each user

	    	String state = user.getState();

	    	if(map.containsKey(state)){   //if the map already contains their state
	    		map.put(state, map.get(state) + 1);
		    } else {
			    map.put(state, 1);    //add state to map
		    }

	    }


	    //get all entries of the map as a linkedlist for sorting
		List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());

	    //sort by values of the key/value entries
	    Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
		    @Override
		    public int compare(Map.Entry<String, Integer> i, Map.Entry<String, Integer> j) {
			    return i.getValue().compareTo(j.getValue());
		    }
	    });

	    //iterators are fun!
	    Iterator iterator = list.iterator();

	    System.out.println("Population of States in ascending order:");
	    while(iterator.hasNext()){
	    	Map.Entry i = (Map.Entry) iterator.next();
		    System.out.println(i.getKey() + "\t" + i.getValue());
	    }
    }
}

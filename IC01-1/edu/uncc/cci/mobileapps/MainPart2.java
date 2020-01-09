package edu.uncc.cci.mobileapps;

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

	    //maps state to integer of how many users are in each state.
	    Map<String, Integer> map = new HashMap<String, Integer>();

	    //count users
	    for(User user : users){ //for each user
	    	if(map.containsKey(user.getState())){   //if the map already contains their state
	    		int currVal = map.get(user.getState()); //grab current value and add incremented value to map
	    		map.put(user.getState(), ++currVal);
		    } else {
			    map.put(user.getState(), 1);    //add state to map
		    }
	    }

	    //print map
	    Iterator statesIterator = map.entrySet().iterator();
	    while(statesIterator.hasNext()){
		    Map.Entry e = (Map.Entry) statesIterator.next();
		    System.out.println(e.getKey() + "\t" + e.getValue());
	    }
    }
}

package edu.uncc.cci.mobileapps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainPart1 {
    /*
    * Question 1:
    * - In this question you will use the Data.users array that includes
    * a list of users. Formatted as : firstname,lastname,age,email,gender,city,state
    * - Create a User class that should parse all the parameters for each user.
    * - Insert each of the users in a list.
    * - Print out the TOP 10 oldest users.
    * */

    public static void main(String[] args) {

        //example on how to access the Data.users array.
	    List<User> users = new ArrayList<>();
        for (String str : Data.users) {
            users.add(new User(str));
        }

        Collections.sort(users);
	    System.out.println("Top 10 oldest users:");
	    for(int i = 0; i < 10; i++){
		    System.out.println(users.get(i).toString());
	    }
    }
}
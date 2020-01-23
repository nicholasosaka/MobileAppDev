package edu.uncc.cci.mobileapps;

/*
ASSIGNMENT#: ICA1
STUDENT NAME: Nicholas Osaka
FILE NAME: MainPart3.java
 */

import java.util.HashSet;
import java.util.Set;

public class MainPart3 {
    /*
    * Question 3:
    * - In this question you will use the Data.users and Data.otherUsers array that includes
    * a list of users. Formatted as : firstname,lastname,age,email,gender,city,state
    * - Create a User class that should parse all the parameters for each user.
    * - The goal is to print out the users that are exist in both the Data.users and Data.otherUsers.
    * Two users are equal if all their attributes are equal.
    * - Print out the list of users which exist in both Data.users and Data.otherUsers.
    * */

    public static void main(String[] args) {

    	//initialization
	    Set<User> users = new HashSet<>();
	    Set<User> otherUsers = new HashSet<>();


	    //population of respective sets
	    for (String str : Data.users) {
		    users.add(new User(str));
	    }

	    for (String str : Data.otherUsers){
	    	otherUsers.add(new User(str));
	    }

	    //set to contain users from both sets
	    Set<User> both = new HashSet<>();

	    for(User u : users){
	    	if(otherUsers.contains(u)){ //if otherUsers contains a given user from the users set, it must be bidirectional.
	    		both.add(u);
		    }
	    }


	    System.out.println("Users that exist in both lists:");
	    for (User u : both) {
		    System.out.println(u);
	    }


    }
}
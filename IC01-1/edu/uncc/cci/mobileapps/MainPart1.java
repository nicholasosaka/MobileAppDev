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
	    System.out.println("Printing top 10 oldest users:");
	    for(int i = 0; i < 10; i++){
		    System.out.println(users.get(i).toString());
	    }
    }
}

class User implements Comparable<User>{
	private String fname, lname, email, gender, city, state;
	private int age;


	User(String user){
		String[] tokens = user.split(",");
		for(int i = 0; i < tokens.length; i++){
			String trimmedToken = tokens[i].trim();
			switch(i){
				case 0: fname = trimmedToken;
					break;
				case 1: lname = trimmedToken;
					break;
				case 2: age = Integer.parseInt(trimmedToken);
					break;
				case 3: email = trimmedToken;
					break;
				case 4: gender = trimmedToken;
					break;
				case 5: city = trimmedToken;
					break;
				case 6: state = trimmedToken;
			}
		}
	}

	@Override
	public int compareTo(User user) {
		return (user.getAge() - this.getAge());
	}

	@Override
	public String toString() {
		return fname + " " + lname + ", " + age + ", " + email + ", " + gender + ", " + city + ", " + state;
	}

	private int getAge() {
		return age;
	}
}
package edu.uncc.cci.mobileapps;

/*
ASSIGNMENT#: ICA1
STUDENT NAME: Nicholas Osaka
FILE NAME: User.java
 */

import java.util.Objects;

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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return getAge() == user.getAge() &&
				getFname().equals(user.getFname()) &&
				getLname().equals(user.getLname()) &&
				getEmail().equals(user.getEmail()) &&
				getGender().equals(user.getGender()) &&
				getCity().equals(user.getCity()) &&
				getState().equals(user.getState());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFname(), getLname(), getEmail(), getGender(), getCity(), getState(), getAge());
	}

	@Override
	public int compareTo(User user) {
		return (user.getAge() - this.getAge());
	}

	@Override
	public String toString() {
		return fname + " " + lname + ", " + age + ", " + email + ", " + gender + ", " + city + ", " + state;
	}

	public String getFname() {
		return fname;
	}

	public String getLname() {
		return lname;
	}

	public String getEmail() {
		return email;
	}

	public String getGender() {
		return gender;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public int getAge() {
		return age;
	}
}

package edu.uncc.cci.mobileapps;

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

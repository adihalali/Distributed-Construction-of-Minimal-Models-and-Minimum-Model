package Rules;

import java.util.ArrayList;

public class Clause{
	ArrayList<String> literals;

	public Clause(){
		this.literals = new ArrayList<String>();
	}

	public void addLiteral(String literal){
		this.literals.add(literal);
	}

	String printClause(){

		String clause = "[";
		boolean first = true;
		for(String l : this.literals){
			if(first){
				clause += l;
				first = false;
			}
			else
				clause += " || "+l;
		}
		return clause+"]";
	}
}

package Rules;

import java.util.ArrayList;
import java.util.HashMap;

public class DavisPutnamHelper{
	
	/*  Returns first literal found.
	 * (To be improved by choosing literal with most ocurrences
	 * wich will allow to remove more clauses at once.)
	 */
	public static String pickLiteral(ArrayList<Clause> Clauses){
		for(Clause c: Clauses)
			return c.literals.get(0);
		return "";
	}
	
	/*	Checks if the formula has any empty clauses. 
	 *  It should not happen. If a clause with one literal is deminished/cut
	 *  then the formula is not satisfactable.
	 */
	public static boolean hasEmptyClause(ArrayList<Clause> Clauses){
		for(Clause c: Clauses)
			if(c.literals.size() == 0)
				return true;
		return false;
	}
	
	// This is a simple function to print a formula in a readable fashion.
	public static void printClauses(ArrayList<Clause> Clauses){
		String formula = "{";
		boolean first = true;
		
		if(Clauses.size() == 0)formula = "{EMPTY}";
		
		else{
			for(Clause c: Clauses){
				if(first){
					formula += c.printClause();
					first = false;
				}
				else formula += " && "+c.printClause();
			}
			formula += "}";
		}
		System.out.println(formula);
	}
	
	/* This function gathers all single literals and then searches all
	 * the clauses for single opposites. If one is found, then the whole
	 * formula must be false. (True is returned).
	 */
	public static boolean hasFalsehood(ArrayList<Clause> Clauses){
		ArrayList<String> singleLiterals = new ArrayList<String>();
		
		for(Clause c: Clauses)
			if(c.literals.size() == 1)
				singleLiterals.add(c.literals.get(0));
		
		for(String sl : singleLiterals){
			String sl_opposite;
			
			if(sl.startsWith("-")) sl_opposite = sl.substring(1);
			
			else sl_opposite = "-"+sl;
			
			for(Clause c: Clauses)
				if(c.literals.size() == 1)
					if(c.literals.get(0).equals(sl_opposite))
						return true;
		}
		return false;
	}
	
	/*	This function takes in a literal and removes -literal
	 * 	from all clauses that have it.
	 * 	It should be noted that if a clause is left empty by this function
	 * 	then the formula is not satisfactable.
	 */
	public static void cutClauses(String literal, ArrayList<Clause> Clauses){
		String cutLiteral;
		
		if(literal.startsWith("-")) 
			cutLiteral = literal.substring(1);
		else
			cutLiteral = "-"+literal;
		
		for(Clause c: Clauses)
			c.literals.remove(cutLiteral);
	}
	
	/*	This function takes in a literal and removes all 
	 * 	clauses where it occurs.  
	 */
	public static void removeClauses(String literal, ArrayList<Clause> Clauses){
		ArrayList<Clause> clausesToRemove = new ArrayList<Clause>();
		
		for(Clause c: Clauses)
			for(String l: c.literals)
				if(l.equals(literal))
					clausesToRemove.add(c);
			
		for(Clause c : clausesToRemove)
			Clauses.remove(c);
	}
	
	/*	This function finds a single literal.
	 * (To be optimized by finding the single literal with most occurrences).	 *
	 */
	public static String searchSingleLiteral(ArrayList<Clause> Clauses, HashMap<Integer,Boolean> literalMap ){
		String literalToRemove = "NotFoundYet";
		
		for(Clause c: Clauses){
			if(c.literals.size() == 1){
				literalToRemove = c.literals.get(0);
				if(literalToRemove.startsWith("-"))
					literalMap.put(Integer.parseInt(literalToRemove.substring(1)),false);
				
				else
					literalMap.put(Integer.parseInt(literalToRemove),true);
				break;
			}
		}
		return literalToRemove;
	}
}

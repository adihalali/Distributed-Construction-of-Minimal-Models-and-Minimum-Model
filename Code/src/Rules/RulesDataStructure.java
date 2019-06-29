package Rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import Graph.Vertex;

//import Graph.LinkedList1;
//import Graph.LinkedList1.Node;

import Rules.LinkedList.Node;
//import Rules.LinkeList;

public class RulesDataStructure extends DavisPutnamHelper{
	public Rule[] RulesArray ;
	public int rulesNum;
	Hashtable<Integer, LinkedList> varHT ;
	private HashMap<Integer, Boolean> literalMap;// We will store the value of literals in this structure as we go along
	public int dpCalls;
	public ArrayList<Integer> minModel;
	public int placedValueCounter;
	public int SIZE;

	/**
	 * Constructor
	 **/
	public RulesDataStructure (int numOfRules){
		this.rulesNum = numOfRules;
		this.SIZE = numOfRules;
		this.RulesArray = new Rule[numOfRules];
		for (int i = 0; i < this.RulesArray.length; i++)
			this.RulesArray[i] = new Rule();

		this.varHT = new Hashtable<Integer, LinkedList>();

		literalMap = new HashMap<Integer, Boolean>();
		this.minModel = new ArrayList<>();
		this.dpCalls = 0;
		this.placedValueCounter = 0;
	}

	public RulesDataStructure(RulesDataStructure ds) {
		rulesNum = ds.rulesNum;
		SIZE = ds.SIZE;

		RulesArray = ds.copyRulesDS();

		varHT = ds.copyVarHT();
		literalMap = ds.copyLiteralMap();

		minModel = new ArrayList<>();
//		minModel.addAll(ds.minModel);
		dpCalls = ds.dpCalls;
		placedValueCounter = ds.placedValueCounter;
	}

	public HashMap<Integer, Boolean> getLiteralMap(){
		return literalMap;
	}
	

	public void setLiteralMap(HashMap<Integer, Boolean> hm) {
		literalMap = new HashMap<Integer, Boolean>();
		Set<Integer> keys = hm.keySet();
		for(int key: keys) {
			if(hm.get(key))
				literalMap.put(key, true);
			else
				literalMap.put(key, false);
		}
	}

	public Hashtable<Integer, LinkedList> getVarHT(){
		return varHT;
	}

	public void setVarHT(Hashtable<Integer, LinkedList> ht){
		varHT = new Hashtable<>();

		Set<Integer> keys = ht.keySet();

		for(int key: keys) {
			LinkedList ls = new LinkedList();
			Node n = ht.get(key).head;
			while(n!=null) {
				ls.addAtTail(n.var);
				n = n.next;
			}
			varHT.put(key, ls);
		}
	}
	
	public Set<Integer> getLiterals(){
		Set<Integer> set = new HashSet<Integer>();
		for(int i=0; i<this.RulesArray.length; i++) {
			if(this.RulesArray[i]!=null) {
				Node n = this.RulesArray[i].body.head;
				while(n!=null) {
					set.add(n.var);
					n=n.next;
				}
				n=this.RulesArray[i].head.head;
				while(n!=null) {
					set.add(n.var);
					n=n.next;
				}
			}
		}
		return set;
	}
	



	/**
	 * Add methods
	 **/
	public void addToRulsArray(int index , int var){
		if(var == 0)//can't be because its checked when reading the file
			return;

		else if(var < 0){
			var *= -1;
			this.RulesArray[index].addToBody(var);
		}

		else
			this.RulesArray[index].addToHead(var);

		addToHashTable(var,index);
		//need to check if the same variable appears more than once in the same clause
	}

	public void addToHashTable(int var, int ruleIndex) {
		LinkedList ls;

		if(this.varHT.containsKey(var)){//key exist
			ls = this.varHT.get(var);
			if(VariableExistInLinkedList(ruleIndex, ls))
				return;

			ls.addAtTail(ruleIndex);
			this.varHT.put(var, ls); 		
		}
		else{//key does not exist
			ls = new LinkedList();
			ls.addAtTail(ruleIndex);
			this.varHT.put(var, ls); 	
		}
	}

	/**
	 * Checks methods 
	 **/
	public boolean variableExist(int var){								//checks if variable exist in the set of rules
		LinkedList l = this.varHT.get(var);
		if(l == null)
			return false;
		return true;
	}

	private boolean VariableExistInLinkedList(int var, LinkedList l){	//checks if variable exist in the linked list
		Node n = l.head;

		while(n != null){
			if(n.var == var)
				return true;
			n = n.next;
		}
		return false;
	}

	private boolean existInBody(int var, int ruleNum){					//checks if variable exist in the body of the rule
		return VariableExistInLinkedList(var, this.RulesArray[ruleNum].body);
	}

	private boolean existInHead(int var , int ruleNum) {				//checks if variable exist in the head of the rule
		return VariableExistInLinkedList(var, this.RulesArray[ruleNum].head);
	}

	private boolean allExistInList(int ruleNum , LinkedList l){			//checks if all vars in rule exist in List
		Rule r = this.RulesArray[ruleNum];
		Node nBody = r.body.head;
		Node nHead =r.head.head;

		while(nBody != null){
			if(!VariableExistInLinkedList(nBody.var, l))
				return false;
			nBody = nBody.next;
		}
		while(nHead != null){
			if(!VariableExistInLinkedList(nHead.var, l))
				return false;
			nHead = nHead.next;
		}
		return true;
	}

	public LinkedList checkFormat(){
		LinkedList l = new LinkedList();
		for (int i = 0; i < this.RulesArray.length; i++) {
			int SizeBody = this.RulesArray[i].body.getSize();
			int SizeHead = this.RulesArray[i].head.getSize();

			if(SizeBody>0 && SizeHead==0)
				l.addAtTail(i+2);
		}
		return l;
	}

	public HashMap<Integer, Boolean> checkForUnits(){
		HashMap<Integer, Boolean> hm = new HashMap<>();
		boolean flag;
		do{
			Rule r;
			flag = false;
			for (int i = 0; i < this.RulesArray.length; i++){
				r = this.RulesArray[i];
				if(r != null){
					if(r.getSize() == 1){
						flag = true;
						if(r.body.getSize() ==1) { //body size is 1 and head size is 0s
							literalMap.put(this.RulesArray[i].body.head.var, false);
							hm.put(this.RulesArray[i].body.head.var, false);
						}

						else{//head size is 1 and body size is 0
							literalMap.put(this.RulesArray[i].head.head.var, true);
							hm.put(this.RulesArray[i].head.head.var, true);
						}
						updateRuleDS();
					}
				}
			}
		}while(flag);
		return hm;
	}


	//check if we return false if we put value inside the variable by the rules of logic
	public boolean conflictWithAssignment(int var ,boolean val){
		LinkedList l = this.varHT.get(var);
		if(l == null)
			return false;

		Node n = l.head;
		while(n!=null){
			int sizeOfBody, sizeOfHead;
			sizeOfBody = this.RulesArray[n.var].body.getSize();
			sizeOfHead = this.RulesArray[n.var].head.getSize();
			if( (existInBody(var,n.var )) && sizeOfBody==1 && val &&sizeOfHead==0)
				return true;

			else if((existInHead(var,n.var)) && sizeOfHead==1 && !val &&sizeOfBody==0)
				return true;

			n = n.next;
		}		
		return false;   	
	}

	// Check if there is conflict in the theory . 
	// for example " a and not a "
	public boolean isConflict(){
		Rule r1,r2;
		for (int i = 0; i < this.RulesArray.length; i++){
			r1 = this.RulesArray[i];

			if(r1!=null && r1.getSize()==1){
				boolean isPositive;
				int var;
				if(r1.body.getSize()==1){
					isPositive=false;
					var=r1.body.head.var;
				}
				else{
					isPositive=true;
					var=r1.head.head.var;
				}

				for(int j=i+1;j<RulesArray.length;j++){
					r2 = RulesArray[j];
					if(r2!=null && r2.getSize()==1){
						if(r2.body.getSize()==1){
							if(var==r2.body.head.var && isPositive)
								return true;//conflict exist
						}
						else{
							if(var==r2.head.head.var && !isPositive)
								return true; //conflict exist
						}
					}		
				}
			}
		}
		return false;
	}

	public boolean isTheoryPositive(){
		for (int i = 0; i < this.RulesArray.length; i++){
			if(this.RulesArray[i] != null){
				Rule r = this.RulesArray[i];
				if(r.head.getSize()==0)
					return false;
			}
		}
		return true;
	}

	/**
	 * Delete methods 
	 **/
	private void deleteRule(int ruleNum){					//receive a rule number an delete the rule from rules array
		updateHT(0, ruleNum);
		this.RulesArray[ruleNum].body.deleteList();
		this.RulesArray[ruleNum].head.deleteList();
		this.RulesArray[ruleNum] = null;
	}

	private void deleteVar(int var, LinkedList l) {			//delete variable from rule
		int index = 0;
		Node n = l.head;

		while(n!=null){
			if(n.var==var){
				l.deleteAtIndex(index);
				index--;
				break;
			}
			index++;
			n=n.next;
		}
	}

	private void deleteVarFromBody(int var, int ruleNum){	//delete variable from body inside rules array
		updateHT(var, ruleNum);
		deleteVar(var, this.RulesArray[ruleNum].body);
	}

	private void deleteVarFromHead(int var, int ruleNum){	//delete variable from head inside rules array
		updateHT(var, ruleNum);
		deleteVar(var, this.RulesArray[ruleNum].head);
	}


	private void updateHTRule(LinkedList rule, int ruleNum) {
		Node n = rule.head;
		while(n != null){
			deleteVar(ruleNum, this.varHT.get(n.var));
			n = n.next;
		}
	}

	/**update the hash table of the variables
	 * on every changes we make */
	private void updateHT(int var , int ruleNum){
		if(var == 0){//from deleteRule method
			updateHTRule(this.RulesArray[ruleNum].body, ruleNum);
			updateHTRule(this.RulesArray[ruleNum].head, ruleNum);
		}

		else
			deleteVar(ruleNum, varHT.get(var));  	
	}

	public void updateRuleDS(){
		Set<Integer> keys = literalMap.keySet();

		for(int key: keys){
			if(literalMap.get(key) && !minModel.contains(key))
				minModel.add(key);
			ChangeDataStrucureByPlacingValueInVar(key, literalMap.get(key));
			this.placedValueCounter++;
		}
		literalMap.clear();
	}

	public void ChangeDataStrucureByPlacingValueInVar(int var , boolean value){
		if(conflictWithAssignment(var, value)){
			System.out.println(var + " CONFLICT "+ value);
			return ;
		}

		if(!variableExist(var))
			return ;

		LinkedList l = varHT.get(var);
		Node n = l.head;
		while(n!=null){
			if((existInBody(var, n.var)&& !value)||(existInHead(var, n.var)&& value)){
				deleteRule(n.var);
				this.SIZE--;
			}
			else if((existInBody(var, n.var)&& value))
				deleteVarFromBody(var,n.var);

			else if (existInHead(var, n.var)&& !value)
				deleteVarFromHead(var,n.var);

			n=n.next;
		}
		return;
	}
	/******************************************************************************************************************/

	public LinkedList Ts(LinkedList s){
		LinkedList Ts = new LinkedList();

		Node Snode =s.head;
		DefaultHashMap<Integer, Boolean> map = new DefaultHashMap<Integer, Boolean>(false);//on default we did not check the rules
		boolean addToTs;
		for (int i = 0; i < s.getSize() ; i++){
			literalMap.put(Snode.var, false);//init all vars of s to falses
			LinkedList l = varHT.get(Snode.var);

			if(l!=null){
				Node n =l.head; 
				while(n!=null){
					if(!map.get(n.var)){//if we did not check this rule, lets check
						map.put(n.var, true);
						addToTs = true;
						if(!allExistInList(n.var, s))
							addToTs=false;
						if(addToTs)
							Ts.addAtTail(n.var);
					}
					n=n.next;
				}
			}
			Snode= Snode.next;
		}
//		System.out.println("######");
//		Ts.printList();
//		printValueOfVariables();
//		System.out.println("######");
		return Ts;
	}
	
	public LinkedList TsWASP(LinkedList s){
		LinkedList Ts = new LinkedList();

		Node Snode =s.head;
		DefaultHashMap<Integer, Boolean> map = new DefaultHashMap<Integer, Boolean>(false);//on default we did not check the rules
		boolean addToTs;

		for (int i = 0; i < s.getSize() ; i++){
			literalMap.put(Snode.var, false);//init all vars of s to falses
			LinkedList l = varHT.get(Snode.var);
			
			if(l!=null){
				Node n =l.head; 
				while(n!=null){
					if(!map.get(n.var)){//if we did not check this rule, lets check
						map.put(n.var, true);
						addToTs = true;
//						if(!allExistInList(n.var, s))
//							addToTs=false;
						if(addToTs)
							Ts.addAtTail(n.var);
					}
					n=n.next;
				}
			}
			Snode= Snode.next;
		}
//		System.out.println("######");
//		Ts.printList();
//		printValueOfVariables();
//		printRulesArray();
//		System.out.println("######");
		return Ts;
	}

	public boolean FindMinimalModelForTs(LinkedList Ts){
		ArrayList<Clause> clauses = new ArrayList<>();
		Node nTs = Ts.head;

		while(nTs!=null){
			Node nBody =RulesArray[nTs.var].body.head;
			Node nHead = RulesArray[nTs.var].head.head;
			Clause clause = new Clause();
			String literal;

			while(nBody!=null){//first put the negative literals in order to calculate a minimal model (because of the way that DLL works)
				literal = "-";
				literal+= String.valueOf(nBody.var);
				clause.addLiteral(literal);
				nBody=nBody.next;
			}

			while(nHead!=null){
				literal = String.valueOf(nHead.var); 
				clause.addLiteral(literal);
				nHead=nHead.next;
			}

			clauses.add(clause);
			nTs=nTs.next;	
		}

		if(DLL(clauses)==true)
			return true;
		return false;
	}

	private boolean DLL(ArrayList<Clause> Clauses){
		this.dpCalls++;//count how many times we called to DP
		if(Clauses.size() == 0) 
			return true;

		//Unitary Propagation
		while(true){	
			String literalToRemove =searchSingleLiteral(Clauses, literalMap);
			if(!literalToRemove.equals("NotFoundYet")){
				removeClauses(literalToRemove,Clauses);
				cutClauses(literalToRemove,Clauses);
				if(Clauses.size() == 0)
					return true;

				if(hasFalsehood(Clauses))
					return false;

				else if(hasEmptyClause(Clauses))
					return false;
			}
			else
				break;
		}

		ArrayList<Clause> copy1 = new ArrayList<Clause>();
		ArrayList<Clause> copy2 = new ArrayList<Clause>();

		for(Clause c: Clauses){
			Clause c2 = new Clause();
			for(String s: c.literals)
				c2.addLiteral(s);
			copy1.add(c2);
		}

		for(Clause c: Clauses){
			Clause c2 = new Clause();
			for(String s: c.literals)
				c2.addLiteral(s);
			copy2.add(c2);
		}

		Clause clause1 = new Clause();
		Clause clause2 = new Clause();
		String l1 = pickLiteral(Clauses);//most of time pick a negative literal because the order of a clause (first body then head)
		String l2 = "";

		if(l1.startsWith("-")){
			l2 = l1.substring(1);
			clause1.addLiteral(l1);
			clause2.addLiteral(l2);
			copy1.add(clause1);
			copy2.add(clause2);
		}

		else{
			l2 = "-"+l1;
			clause1.addLiteral(l2);
			clause2.addLiteral(l1);
			copy1.add(clause1);
			copy2.add(clause2);
		}

		if(DLL(copy1) == true)
			return true;
		else
			return DLL(copy2);
	}

	public void putMinModelInLiteralMap(LinkedList minmodel){
		Node n =minmodel.head;
		while(n!=null){
			literalMap.put(n.var, true);
			n=n.next;
		}
	}

	public HashMap<Integer, Boolean> putMinModelInLiteral(ArrayList<String[]> minmodel){
		HashMap<Integer, Boolean> hm = new HashMap<>();
		for(String[] str: minmodel) {
			for(int i=0; i<str.length; i++) {
//				System.out.println(Arrays.toString(str));
				literalMap.put(Integer.parseInt(str[i]), true);
				hm.put(Integer.parseInt(str[i]), true);
			}
		}
		return hm;
	}







	/**return a string of the minimal model */
	public String StringMinimalModel(){
		String str= "[ ";
		for(int var : this.minModel)
			str+= "{"+var+"}" + " ";

		str+= "]" + "\r\n" +" |MM| = "+ minModel.size();
		return str;
	}

	/***Receive a set of vertexes from graph ds and put values on each vertex
    also checks if the values we put in the variables return SAT if so we change rules ds 
    by the values we found and if not we try different values for the variables
	 ***/
	public void SplitConnectedComponent(ArrayList<Vertex<Integer>> v, LinkedList Ts){
		ArrayList<Clause> clauses = new ArrayList<>();
		Node n = Ts.head;
		while(n!=null){
			Node nBody =RulesArray[n.var].body.head;
			Node nHead = RulesArray[n.var].head.head;
			Clause clause = new Clause();
			String literal;
			while(nBody!=null){//first put the negative literals in order to calculate a minimal model (because of the way that DLL works)

				literal = "-";
				literal+= String.valueOf(nBody.var);
				clause.addLiteral(literal);
				nBody=nBody.next;
			}
			while(nHead!=null){
				literal = String.valueOf(nHead.var); 
				clause.addLiteral(literal);
				nHead=nHead.next;
			}

			clauses.add(clause);
			n=n.next;
		}

		int size = v.size();
		int N = (int)Math.pow(2,size); 
		boolean[] binaryArray ;
		String literal;
		Clause clause;
		ArrayList<Clause> copy;
		for (int i = 0; i < N; i++){//from 0 to 2^n -1
			copy = new ArrayList<Clause>();//
			for(Clause c: clauses){
				Clause c2 = new Clause();
				for(String s: c.literals)
					c2.addLiteral(s);

				copy.add(c2);
			}// copy original to not make any changes
			binaryArray = toBinary(i,size);//returns array
			for (int j = 0; j < size; j++){		
				clause= new Clause();
				if(binaryArray[j])
					literal = String.valueOf(v.get(j).getId());

				else
					literal = "-"+String.valueOf(v.get(j).getId());
				clause.addLiteral(literal);
				copy.add(clause);
			}

			if(DLL(copy)){
				updateRuleDS();
				return;
			}   
			literalMap.clear();
		}
	}

	/**return a binary value of the number 
	 * ,the (base) last bits */
	public boolean[] toBinary(int number, int base){
		final boolean[] ret = new boolean[base];
		for (int i = 0; i < base; i++) 
			ret[base - 1 - i] = (1 << i & number) != 0;

		return ret;
	}

	public void removeDoubles(){
		for (int i = 0; i < RulesArray.length; i++){
			LinkedList body = RulesArray[i].body;
			Node nBody =body.head;
			while(nBody!=null){
				int var =nBody.var;
				Node nBody2=nBody.next;
				int index=1;
				while(nBody2!=null){
					if(var==nBody2.var){
						body.deleteAtIndex(index);
						index--;
					}
					index++;
					nBody2=nBody2.next;
				}
				nBody=nBody.next;		
			}
			LinkedList head = RulesArray[i].head;
			Node nHead =head.head;
			while(nHead!=null){
				int var =nHead.var;
				Node nHead2=nHead.next;
				int index=1;
				while(nHead2!=null){
					if(var==nHead2.var){
						head.deleteAtIndex(index);
						index--;
					}
					index++;
					nHead2=nHead2.next;
				}
				nHead=nHead.next;
			}
		}
	}





	public void IntegrityConstraint(ArrayList<Integer> array){
		for (int i = 0; i < RulesArray.length; i++){
			Rule r = RulesArray[i];
			if(r!=null){
				if(r.head.getSize()==0){
					Node n = r.body.head;
					while(n!=null){
						if(!array.contains(n.var)) 
							array.add(n.var);
						n=n.next;
					}
				}
			}
		}
	}


	/**
	 * Print methods
	 * **/
	public void printValueOfVariables(){
		System.out.println("-------THE VALUE TABLE--------");
		Set<Integer> keys = literalMap.keySet();
		for(int key: keys){
			System.out.print("Value of " + key +" is ");
			if(literalMap.get(key))
				System.out.println("TRUE");
			else
				System.out.println("FALSE");
		}
		System.out.println("------------------------------");
	}

	public void printRulesArray(){
		int i;
		Node tempBody;
		Node tempHead;

		for (i = 0; i < this.RulesArray.length; i++){
			if(this.RulesArray[i]!=null){
				System.out.println("RULE NUMBER " + i);
				System.out.print("\t Body Of Rule : ");
				tempBody = this.RulesArray[i].body.head;
				tempHead = this.RulesArray[i].head.head;

				printRule(tempBody);
				System.out.print("\t Head Of Rule : ");
				printRule(tempHead);
			}
		}
	}
	private void printRule(Node rule) {
		while(rule!=null){
			System.out.print(rule.var);
			if(rule.next!=null)
				System.out.print(" --> ");
			rule = rule.next;
		}
		System.out.println("\n");
	}

	public void printHashTable(){
		Set<Integer> keys = this.varHT.keySet();

		for(int key: keys) {
			System.out.println("Value of " + key +" is: ");
			this.varHT.get(key).printList();
		}
	}


	//*******************************check copy, another split method****************************************//
	//     public void splitConnectedComponent2(ArrayList<Vertex<Integer>> VertexSeperatorArray)
	//     {
	//     	System.out.println("enter split 2");
	//     	int size = VertexSeperatorArray.size();
	//        System.out.println("size of array is: " + size);
	//         int N = (int)Math.pow(2,size); 
	//         boolean[] binaryArray ;
	//         HashMap<Integer, Boolean> valuesForVertexSeperatorArray = new HashMap<>();
	//         for (int i = 0; i < N; i++) 
	//         {
	//        	//System.out.println("copy the DS");
	//        	Rule[] copy = copyRulesDS();
	//            boolean conflict=false;
	//         	binaryArray = toBinary(i,size);//returns array
	// 			//System.out.println("INDEX "+i);
	//         	valuesForVertexSeperatorArray.clear();
	//         	for(int j =0;j<size;j++)
	//         	{
	//         		//System.out.println("check conflict with assigment " +(int)VertexSeperatorArray.get(j).getId()+"  "+ binaryArray[j]);
	//         		if(conflictWithAssignment2(copy,(int)VertexSeperatorArray.get(j).getId(), binaryArray[j]))
	//         		{
	//         			conflict = true;
	//         			System.out.println("111");
	//         			break;
	//         		}
	//         		else
	//         		{
	//         			//System.out.println("not found conflict");
	//         			ChangeDataStrucureByPlacingValueInVar2(copy,(int)VertexSeperatorArray.get(j).getId(), binaryArray[j]);
	////         			if(isConflict2(copy))
	////         			{
	////         				conflict=true;
	////         				System.out.println("222");
	////         				break;
	////         			}
	////         			if(!isTheoryPositive2(copy))
	////         			{
	////         				conflict = true;
	////         				System.out.println("333");
	////         				break;
	////         			}
	//         			//System.out.println("hi");
	//         		}
	//         	}     
	//         	
	//         	if(!conflict)
	//         	{
	//             	checkForUnits2(copy, valuesForVertexSeperatorArray);/////After we check that there are no units we do not need to check for conflict, but we steal need to check if theory remains positive
	//         		//System.out.println("we found one in index: "+ i);
	//         		if(isTheoryPositive2(copy))
	//         		{
	//         			System.out.println(isTheoryPositive2(copy));
	//         			for (int j = 0; j < size; j++)
	//         			{
	//         				//System.out.println("change DS variable: "+(int)VertexSeperatorArray.get(j).getId()+" value: "+binaryArray[j]);
	//         				//ChangeDataStrucureByPlacingValueInVar((int)VertexSeperatorArray.get(j).getId(), binaryArray[j]);
	//         				literalMap.put((int)VertexSeperatorArray.get(j).getId(), binaryArray[j]);
	//         				//checkForUnits();
	//         			}
	//         			Set<Integer> keys = valuesForVertexSeperatorArray.keySet();
	//         			for(int key:keys)
	//         			{
	//         				literalMap.put(key, valuesForVertexSeperatorArray.get(key));
	//         			}
	//
	//         			updateRuleDS();
	//         			//	printRulesArray();
	//         			//System.out.println("VAR_HT");
	//         			//printHashTable();
	//         			return;
	//         		}
	//         	}
	// 		}
	//   
	//     	
	//     }
	//     
	public Rule[] copyRulesDS()
	{
		Rule[] rules = new Rule[rulesNum];
		for (int i = 0; i < rulesNum; i++)
		{
			rules[i]= new Rule();
		}
		for (int i = 0; i < RulesArray.length; i++) 
		{
			if(RulesArray[i]==null)
			{
				rules[i]=null;
			}
			else
			{
				Node nBody=RulesArray[i].body.head;
				Node nHead=RulesArray[i].head.head;
				rules[i].body=new LinkedList();
				rules[i].head= new LinkedList();
				while(nBody!=null)
				{
					rules[i].body.addAtTail(nBody.var);
					nBody=nBody.next;
				}
				while(nHead!=null)
				{
					rules[i].head.addAtTail(nHead.var);
					nHead=nHead.next;
				}
			}
		}
		return rules;
	}

	public HashMap<Integer, Boolean> copyLiteralMap(){ 
		HashMap<Integer, Boolean>lm = new HashMap<Integer, Boolean>();
		Set<Integer> keys = literalMap.keySet();
		for(int key: keys) {
			if(literalMap.get(key))
				lm.put(key, true);
			else
				lm.put(key, false);
		}
		return lm;
	}

	public Hashtable<Integer,LinkedList> copyVarHT(){
		Hashtable<Integer, LinkedList> ht = new Hashtable<>();

		Set<Integer> keys = this.varHT.keySet();

		for(int key: keys) {
			LinkedList ls = new LinkedList();
			Node n = this.varHT.get(key).head;
			while(n!=null) {
				ls.addAtTail(n.var);
				n = n.next;
			}
			ht.put(key, ls);
		}
		return ht;
	}

	//     public void print(Rule[] array)
	//     {
	//
	//     	int i;
	//     	Node tempBody;
	//     	Node tempHead;
	//     	for (i = 0; i < array.length; i++)
	//     	{
	//     		if(array[i]!=null)
	//     		{
	//     			System.out.println("RULE NUMBER " + i);
	//     			System.out.print("\t Body Of Rule : ");
	//     			tempBody = array[i].body.head;
	//     			tempHead =array[i].head.head;
	//     			while(tempBody!=null)
	//     			{
	//     				System.out.print(tempBody.var);
	//     				if(tempBody.next!=null)
	//     					System.out.print(" --> ");
	//     				tempBody=tempBody.next;
	//     			}
	//     			System.out.println("\n");
	//     			System.out.print("\t Head Of Rule : ");
	//     			while(tempHead!=null)
	//     			{
	//     				System.out.print(tempHead.var);
	//     				if(tempHead.next!=null)
	//     					System.out.print(" --> ");
	//     				tempHead=tempHead.next;
	//     			}
	//     			System.out.println("\n");
	//     		}
	//
	// 		}
	//     }
	//     public boolean conflictWithAssignment2(Rule[] array, int var ,boolean val)
	//     {
	//     	LinkedList l = varHT.get(var);
	//     	Node n = l.head;
	//     	while(n!=null)
	//     	{
	//     		int sizeOfBody, sizeOfHead;
	//     		if(array[n.var]!=null)
	//     		{
	//     			sizeOfBody = array[n.var].body.getSize();
	//     			sizeOfHead = array[n.var].head.getSize();
	//     			if( sizeOfBody==1  && array[n.var].body.head.var==var  && val &&sizeOfHead==0)
	//     			{
	//     				return true;
	//     			}
	//     			else if(sizeOfHead==1 && array[n.var].head.head.var==var && !val &&sizeOfBody==0)
	//     			{
	//     				return true;
	//     			}
	//     		}
	//     		
	//     			n=n.next;
	//     	}		
	//     	
	//     	return false;   	
	//     }
	//     public void ChangeDataStrucureByPlacingValueInVar2(Rule[] array, int var , boolean value)
	//     {
	//    	 
	//     	LinkedList l = varHT.get(var);
	// 	    Node n = l.head;
	//     	while(n!=null)
	//     	{
	//     		if((existInBody2(array,var, n.var)&& !value)||(existInHead2(array,var, n.var)&& value))
	//     		{
	//     			deleteRule2(array,n.var);
	//     			//System.out.println("DELETE RULE NUMBER " + n.var);
	//     		}
	//     		else if((existInBody2(array, var, n.var)&& value))
	//     		{
	//     			deleteVarFromBody2(array,var,n.var);
	//     			//System.out.println( "DELETE VARIABLE FROM BODY" + var + " IN RULE " + n.var);
	//     		}
	//     		else if (existInHead2(array, var, n.var)&& !value)
	//     		{
	//     			deleteVarFromHead2(array, var,n.var);
	//     			//System.out.println("DELETE VARIABLE FROM HEAD"+var+" IN RULE " + n.var);
	//     		}	
	//     		n=n.next;
	//     		
	//     	}
	//     	return;
	//     	
	//     }
	//     
	//     private void deleteRule2(Rule[] array, int ruleNum)
	//     {
	//    	 if(array[ruleNum]==null)
	//    		 return;
	//     	array[ruleNum].body.deleteList();
	//     	array[ruleNum].head.deleteList();
	//     	array[ruleNum]=null;
	//     }
	//     
	//     /**delete variable from body inside rules array */
	//     private void deleteVarFromBody2(Rule[] array, int var, int ruleNum)
	//     {
	//     	LinkedList l = array[ruleNum].body;
	//     	int index = 0;
	//     	Node n = l.head;
	//     	while(n!=null)
	//     	{
	//     		if(n.var==var)
	//     		{
	//     			l.deleteAtIndex(index);
	//     			index--;
	//     			break;
	//     		}
	//     		index++;
	//     		n=n.next;
	//     	}
	//     }
	//     /**delete variable from head inside rules array*/
	//     private void deleteVarFromHead2(Rule[] array, int var, int ruleNum)
	//     {
	//     	LinkedList l = array[ruleNum].head;
	//     	int index = 0;
	//     	Node n = l.head;
	//     	while(n!=null)
	//     	{
	//     		if(n.var==var)
	//     		{
	//     			l.deleteAtIndex(index);
	//     			index--;
	//     			break;
	//     		}
	//     		index++;
	//     		n=n.next;
	//     	}
	//     }
	//     
	//     private boolean existInBody2(Rule[] array, int var, int ruleNum)
	//     {
	//     	if(array[ruleNum]==null)
	//     	{
	//     		return false;
	//     	}
	//     	Node n;
	//     	n = array[ruleNum].body.head;
	//     	while(n!=null)
	//     	{
	//     		if(n.var == var)
	//     			return true;
	//     		n=n.next;
	//     	}
	//     	return false;
	//     }
	//     
	//     private boolean existInHead2(Rule[] array, int var , int ruleNum)
	//     {
	//    	 if(array[ruleNum]==null)
	//      	{
	//      		return false;
	//      	}
	//     	Node n;
	//     	n = array[ruleNum].head.head;
	//     	while(n!=null)
	//     	{
	//     		if(n.var == var)
	//     			return true;
	//     		n=n.next;
	//     	}
	//     	return false;
	//     }
	//     
	//     public boolean isConflict2(Rule[] array)
	//     {
	//     	Rule r1,r2;
	//     	for (int i = 0; i < array.length; i++) 
	//     	{
	//     		r1=array[i];
	//     		if(r1!=null&&r1.getSize()==1)
	//     		{
	//     			boolean isPositive;
	//     			int var;
	//     			if(r1.body.getSize()==1)
	//     			{
	//     				isPositive=false;
	//     				var=r1.body.head.var;
	//     			}
	//     			else
	//     			{
	//     				isPositive=true;
	//     				var=r1.head.head.var;
	//     			}
	//     			
	//     			for(int j=i+1;j<array.length;j++)
	//     			{
	//     				r2=array[j];
	//     				if(r2!=null&&r2.getSize()==1)
	//     				{
	//     					if(r2.body.getSize()==1)
	//     					{
	//     						if(var==r2.body.head.var && isPositive)
	//     							return true;//conflict exist
	//     					}
	//     					else
	//     					{
	//     						if(var==r2.head.head.var && !isPositive)
	//     							return true; //conflict exist
	//     					}
	//     				}		
	//     			}
	//     			
	//     		}
	// 			
	// 		}
	//     	
	//     	return false;
	//     }
	//     
	//     public void checkForUnits2(Rule[] array, HashMap<Integer, Boolean> map)
	//     {
	//    	 boolean flag;
	//    	 do
	//    	 {
	//    		 Rule r;
	//    		 flag=false;
	//    		 for (int i = 0; i < array.length; i++)
	//    		 {
	//    			 r=array[i];
	//    			 if(r!=null)
	//    			 {
	//    				 if(r.getSize()==1)
	//    				 {
	//    					 flag = true;
	//    					 if(r.body.getSize() ==1)//body size is 1 and head size is 0s
	//    					 {
	//    						 map.put( array[i].body.head.var, false);
	//    						 ChangeDataStrucureByPlacingValueInVar2(array, array[i].body.head.var, false);
	//    						 
	//    					 }
	//    					 else//head size is 1 and body size is 0
	//    					 {
	//    						 map.put(array[i].head.head.var, true);
	//    						 ChangeDataStrucureByPlacingValueInVar2(array, array[i].head.head.var, true);
	//    						
	//    					 }
	//    				 }
	//    			 }
	//    		 }
	//    	 }while(flag);
	//     	//System.out.println("after unit check");
	//
	//     }
	//     
	//     
	//     public boolean isTheoryPositive2(Rule[] array)
	//     {
	//    	 for (int i = 0; i < array.length; i++) 
	//    	 {
	//			if(array[i]!=null)
	//			{
	//				Rule r = array[i];
	//				if(r.head.getSize()==0)
	//				{
	//					return false;
	//				}
	//			}
	//		}
	//    	 return true;
	//     }
	//     
	//     
}

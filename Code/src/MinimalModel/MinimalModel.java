package MinimalModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import Graph.Graph;
import Graph.StronglyConnectedComponent;
import Graph.SuperGraph;
import Graph.Vertex;
import Rules.LinkedList;
import Rules.RulesDataStructure;


public class MinimalModel extends Graph<Integer>{
	private static final double MEGABYTE = 1024L * 1024L;

	private RulesDataStructure DS;
	private int rulesNum, varsNum;
	private int count_unsat;
	ArrayList<String[]> list;
	private List<Set<Vertex<Integer>>> SCC;

	public MinimalModel() {
		super(true);
		this.rulesNum = 0;
		this.varsNum = 0;
		this.count_unsat = 0;
		list = new ArrayList<>();
	}

	public List<Set<Vertex<Integer>>> getSCC(){
		return this.SCC;
	}


	public void readfile(String path){
		Scanner sc;
		int var ;
		int index = 0;
		int numOfRules=0;
		int numOfVariables=0;

		try {
			sc = new Scanner(new File(path));//read file
			if(sc.hasNext())
				numOfRules = sc.nextInt();
			rulesNum = numOfRules;
			if(sc.hasNext())
				numOfVariables = sc.nextInt();
			varsNum=numOfVariables;
			DS = new RulesDataStructure(numOfRules);

			while (sc.hasNext()) {
				var = sc.nextInt();
				if(var!=0)
					DS.addToRulsArray(index, var);
				else
					index++;
			}
		}catch (FileNotFoundException ex){}
	}

	public SuperGraph createModelGraph(){
		int size = DS.SIZE;
		DS.removeDoubles();
		Graph<Integer> graph = initGraph(DS, size);

		System.out.println("---------------------Theory Graph---------------------\n"+graph+"\n");

		StronglyConnectedComponent scc = new StronglyConnectedComponent();
		this.SCC = scc.scc(graph);
		System.out.println("----------Strongly Connected Component Graph----------\n"+this.SCC+"\n\n");

		System.out.println("----------------------Super Graph----------------------");
		SuperGraph super_graph = new SuperGraph(graph);
		super_graph.printGraph();
		System.out.println("\n-------------------------------------------------------");

		return super_graph;
	}

	/**WASP**/
	public void WASP(){
		LinkedList Ts=new LinkedList();

		for (int i = 0; i < rulesNum ; i++)
			Ts.addAtTail(i);

		BufferedWriter bw = null;
		FileWriter fw = null;
		String FILENAME=".//alviano-wasp-f3fed39/build/release/ex";
		String[] cnfContent=getCnfContent(Ts);	

		try{
			fw = new FileWriter(FILENAME);
			bw = new BufferedWriter(fw);
			for (int i = 0; i < cnfContent.length; i++) {
				bw.write(cnfContent[i]);
				bw.newLine();
			}
		} catch (IOException e ) {
			e.printStackTrace();
		}

		finally {
			try {
				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		MinimalModelFromScript();
		if(count_unsat>0 && list.size()==count_unsat)
			System.out.println("unsatisfiable");
	}

	public String[] getCnfContent(LinkedList Ts){
		int size = Ts.getSize();
		String[] toReturn= new String[size];
		Rules.LinkedList.Node nTs ;
		Rules.LinkedList.Node nBody;
		Rules.LinkedList.Node nHead;

		nTs=Ts.head;
		for (int i = 0; i < size; i++) {
			String oneRule="";
			nBody=DS.RulesArray[nTs.var].body.head;
			while(nBody!=null){
				oneRule+="-"+nBody.var+" ";
				nBody=nBody.next;
			}
			nHead=DS.RulesArray[nTs.var].head.head;
			while(nHead!=null){
				oneRule+=nHead.var+" ";
				nHead=nHead.next;
			}
			oneRule+="0";
			toReturn[i]=oneRule;
			nTs=nTs.next;
		}
		return toReturn;
	}

	public void checkModels(String[] lst) {
		if(list.size()==0) {
			list.add(lst);
			return;
		}

		for(int i=0; i<list.size(); i++) {
			int res = containsAll(list.get(i), lst);

			if(res == 0)
				return;	// do not insert

			else if(res == 1) {	// replace
				list.remove(i);
				if(i!=0)
					i--;
			}
		}
		list.add(lst);
	}

	public int containsAll(String[] a, String[] b){
		if(a.length==b.length)
			return -1;	//same size

		String[] str1 = a, str2 = b;
		int counter = 0;
		boolean replace = false;

		if(b.length <a.length){
			str1 = b;
			str2 = a;
			replace = true;
		}

		for(int i=0; i<str1.length; i++){
			for(int j=0; j<str2.length; j++) {
				if(str2[j].equals(str1[i])) {
					counter++;
					break;
				}
			}
		}

		if(replace && b.length==counter)
			return 1; // equals and b is smaller

		if(!replace && a.length==counter)
			return 0; // equals and a is smaller

		return 2; // not equals
	}










	/*****************************************************************/


	public void MinimalModelFromScript(){
		String s ="python3 cnf2lparse.py ex | ./wasp -n=100" ;

		String[] cmd = {"/bin/sh", "-c", s};
		String path = ".//alviano-wasp-f3fed39/build/release";

		try {
			Process p = Runtime.getRuntime().exec(cmd,null,new File(path));
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = in.readLine();

			while(line!=null) {
				if(line.equals("INCOHERENT")) {
					String[] tmp = {""};
					list.add(tmp);
					count_unsat++;
				}

				if(line.length()>0 && line.charAt(0)=='{') {
					if(line.equals("{}"))
						continue;					

					else {
						String[] str = line.substring(1, line.length()-1).split(", ");
						checkModels(str);
						//						for (int i = 0; i < str.length; i++) 			
						//							lst.addAtTail(Integer.parseInt(str[i]));
					}
				}
				line = in.readLine();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		System.out.println("Models: "+list.size());
		for(int i=0; i<list.size(); i++) {
			System.out.println(list.get(i).length);
			System.out.println(Arrays.toString(list.get(i)));
		}
	}









	public void graphTest() {
		int size = DS.SIZE;			

		Graph<Integer> g = initGraph(DS, size);

		StronglyConnectedComponent scc = new StronglyConnectedComponent();
		List<Set<Vertex<Integer>>> result = scc.scc(g);
		//		System.out.println("******\n"+result+"\n******\n");

		System.out.println("Original CC: ");
		//print the result
		result.forEach(set -> {
			set.forEach(v -> System.out.print(v.getId() + " "));
			System.out.println();
		});
		if(result.get(0).size()!=g.getAllVertex().size()) {
			System.out.println("not cc");
			return ;
		}

		ArrayList<Vertex<Integer>> min_array=new ArrayList<>();
		min_array= Graph.vertexSeparator(g);

		if(min_array!=null)
			System.out.println("Min Vertex to remove: " + min_array + " Size of the Seperator: "+ min_array.size());			
	}

	/******************************/
	public void writeToFile(LinkedList Ts)
	{
		//System.out.println("writing to file");
		BufferedWriter bw = null;
		FileWriter fw = null;
		String FILENAME=".//alviano-wasp-f3fed39/build/release/ex";
		String[] cnfContent=getCnfContent(Ts);	

		try
		{
			fw = new FileWriter(FILENAME);
			bw = new BufferedWriter(fw);
			for (int i = 0; i < cnfContent.length; i++) 
			{
				bw.write(cnfContent[i]);
				bw.newLine();
			}

		}
		catch (IOException e ) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}

	}
	public void MinimalModelScript()
	{
		//System.out.println("reading minimal model");
		String s ="python3 cnf2lparse.py ex | ./wasp --minimize-predicates=a --minimization-algorithm=guess-check-split --silent" ;

		String[] cmd = {
				"/bin/sh",
				"-c",
				s
		};

		String path = ".//alviano-wasp-f3fed39/build/release";
		LinkedList l = new LinkedList();
		try {
			Process p =Runtime.getRuntime().exec(cmd,null,new File(path));
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

			//	String line;
			//System.out.println(in.readLine());
			String line = in.readLine();
			list = new ArrayList<>();
			while(line!=null) {
				//				System.out.println("kkk "+line);
				if(line.equals("INCOHERENT")) {
					String[] tmp = {""};
					list.add(tmp);
					count_unsat++;
				}


				if(line.length()>0 && line.charAt(0)=='{') {
					if(!line.equals("{}")) {

						String[] str = line.substring(1, line.length()-1).split(", ");
						checkModels(str);
						//						for (int i = 0; i < str.length; i++) 			
						//							lst.addAtTail(Integer.parseInt(str[i]));
					}
				}
				line = in.readLine();
			}


			//			System.out.println("Models: "+list.size());
			//			for(int i=0; i<list.size(); i++)
			//				System.out.println(Arrays.toString(list.get(i)));


			//			if(line.equals("{}"))
			//			{
			//				return l;
			//			}
			//			if(line.equals("INCOHERENT"))
			//			{
			//				l.addAtTail(-1);
			//				return l;
			//			}
			//			String[] str = line.split(" ");
			//			for (int i = 0; i < str.length; i++) {
			//				str[i]=str[i].replace("a", "");
			//				str[i]=str[i].replace("{", "");
			//				str[i]=str[i].replace("}", "");
			//				str[i]=str[i].replace("(", "");
			//				str[i]=str[i].replace(")", "");
			//				str[i]=str[i].replace(",", "");
			//			}
			//			for (int j = 0; j < str.length; j++) 
			//			{				
			//				l.addAtTail(Integer.parseInt(str[j]));
			//			}

		}
		catch(IOException e) {
			e.printStackTrace();
		}
		//		return l;
	}

	public LinkedList MinimalModelFromScript1()
	{
		//System.out.println("reading minimal model");
		String s ="python3 cnf2lparse.py ex | ./wasp --minimize-predicates=a --minimization-algorithm=guess-check-split --silent" ;

		String[] cmd = {
				"/bin/sh",
				"-c",
				s
		};

		String path = ".//alviano-wasp-f3fed39/build/release";
		LinkedList l = new LinkedList();
		try {
			Process p =Runtime.getRuntime().exec(cmd,null,new File(path));
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

			//	String line;
			//System.out.println(in.readLine());
			String line = in.readLine();
			System.out.println(line);
			if(line.equals("{}"))
			{
				return l;
			}
			if(line.equals("INCOHERENT"))
			{
				l.addAtTail(-1);
				return l;
			}
			String[] str = line.split(" ");
			for (int i = 0; i < str.length; i++) {
				str[i]=str[i].replace("a", "");
				str[i]=str[i].replace("{", "");
				str[i]=str[i].replace("}", "");
				str[i]=str[i].replace("(", "");
				str[i]=str[i].replace(")", "");
				str[i]=str[i].replace(",", "");
			}
			for (int j = 0; j < str.length; j++) 
			{				
				l.addAtTail(Integer.parseInt(str[j]));
			}

		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return l;
	}

	public boolean ModuminUsingWASP()
	{
		DS.removeDoubles();
		int size = DS.SIZE;		
		Graph<Integer> g;
		LinkedList source ,Ts, minmodel;
		double sumSorceSize=0.0;
		double numOfSources=0.0;
		while(DS.SIZE!=0)
		{
			//DS.printRulesArray();
			/**unity check*/
			DS.checkForUnits();
			/**create graph*/
			g = initGraph(DS, size);
			/**find source*/
			source = sourceOfGraph(g);
			//System.out.println("ver size: "+g.getAllVertex().size()+ " source size; " +source.getSize());
			numOfSources++;
			sumSorceSize+=source.getSize();
			/**Find Ts*/
			//			DS.printRulesArray();
			Ts=DS.Ts(source);
			//			Ts.printList();
			if(Ts.getSize()>0)
			{
				/**Write cnf to file */
				writeToFile(Ts);			
				/**find minimal model for Ts*/
				MinimalModelScript();

				if(list.size()==0) {
					System.out.println("UNSAT");
					return false;
				}
				else {
					DS.putMinModelInLiteral(list);
				}

				//				if(minmodel.head!=null)
				//				{
				//					if(minmodel.head.var==-1)//unsat
				//					{
				//						System.out.println("UNSAT");
				//						return false;
				//					}
				//					/**put the minimal model in the literal map*/
				//					DS.putMinModelInLiteralMap(minmodel);
				//
				//				}

			}		
			/**Update the rules data structure*/
			DS.updateRuleDS();	
		}
		//System.out.println(numOfSources);
		//		this.avgSourceSize=sumSorceSize/numOfSources;
		return true;
	}

	public boolean DP()
	{
		DS.removeDoubles();
		LinkedList Ts=new LinkedList();
		for (int i = 0; i < rulesNum ; i++) {
			Ts.addAtTail(i);
		}

		if(!DS.FindMinimalModelForTs(Ts)){
			//			System.out.println("UNSAT");
			return false;
		}
		DS.updateRuleDS();
		return true;
	}


	public boolean ModuMinUsingDP()
	{
		int size = DS.SIZE;	
		DS.removeDoubles();
		int biggestSource=0;
		while(DS.SIZE!=0)
		{

			DS.checkForUnits();//remove empty sources

			Graph<Integer> g = initGraph(DS, size);
			LinkedList s = sourceOfGraph(g);

			if(s.getSize()> biggestSource)
			{
				biggestSource= s.getSize();
			}
			LinkedList Ts=DS.Ts(s);

			if(!DS.FindMinimalModelForTs(Ts))
			{
				System.out.println("UNSAT");
				return false;
			}
			DS.updateRuleDS();
		}		
		Collections.sort(DS.minModel);
		return true;
	}



	public RulesDataStructure getDS() {
		return this.DS;
	}
	/******************************/

	public static void main(String[] args) {
		MinimalModel m = new MinimalModel();
		String path=".//CnfFile.txt";
		long startTime,endTime,totalTime;

		m.readfile(path);

		startTime = System.nanoTime();
		m.WASP();
		endTime = System.nanoTime();
		System.out.println("\nWASP:\n\tstart: "+startTime+" end: "+endTime +" total: "+(endTime-startTime)/1000000);
		//
		//
		//		m.graphTest();
		//

//		System.out.println("is conflict "+m.DS.isConflict());
//		m.DS.checkFormat().printList();
//		System.out.println("jhghjghjg");
		m.readfile(path);
		startTime = System.nanoTime();
		if(m.DP())
			System.out.println("if   "+m.DS.StringMinimalModel());
		endTime = System.nanoTime();
		System.out.println("\nDP:\n\tstart: "+startTime+" end: "+endTime +" total: "+(endTime-startTime)/1000000);
		//
//		System.out.println("******");
		//		m.graphTest();



//		System.out.println(m.DS.isTheoryPositive());
		//		System.out.print(m.avgSourceSize);
//		System.out.print(",");
		m.readfile(path);
		//		m.createModelGraph();

		/***run time checking*/



		startTime = System.nanoTime();
		m.ModuMinUsingDP();
		endTime = System.nanoTime();
		System.out.println("\nModuMinUsingDP:\n\tstart: "+startTime+" end: "+endTime +" total: "+(endTime-startTime)/1000000);
		//
//		System.out.println("strt: "+startTime+" end: "+endTime);
//		System.out.print(m.DS.placedValueCounter);
		System.out.println(m.DS.StringMinimalModel());
		//		System.out.print(m.avgSourceSize);
		System.out.println("#########");
		m.readfile(path);
		//
		//		startTime = System.nanoTime();
		m.ModuminUsingWASP();
		System.out.println(m.DS.minModel);

	}
}
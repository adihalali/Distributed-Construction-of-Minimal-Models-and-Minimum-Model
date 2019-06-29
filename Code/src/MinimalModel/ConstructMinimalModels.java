package MinimalModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import Graph.Graph;
import Graph.Vertex;
import Rules.LinkedList;
import Rules.RulesDataStructure;
import Rules.LinkedList.Node;

public class ConstructMinimalModels {
	String ID;
	RulesDataStructure DS;
	HashMap<Integer, Boolean> literalMap;
	LinkedList vertexCC;
	List<Set<Vertex<Integer>>> SCC;
	Boolean lastVertex;

	public ConstructMinimalModels(String id, RulesDataStructure ds, LinkedList vCC, List<Set<Vertex<Integer>>> scc, Boolean last) {
		this.ID = id;
		this.DS = ds;
		this.literalMap = ds.copyLiteralMap();
		this.vertexCC = vCC;

		this.SCC = new ArrayList<>();
		for(int i=0; i<scc.size(); i++)
			this.SCC.add(scc.get(i));

		this.lastVertex = last;
	}

	public HashMap<Integer, Boolean> getLiteralMap(){
		return this.literalMap;
	}

	public ArrayList<Integer> getMinimalModels(){
		return DS.minModel;
	}

	public void updateLiteralMap(HashMap<Integer, Boolean> hm) {
		Set<Integer> keys = hm.keySet();
		for(int key: keys) 
			literalMap.put(key, hm.get(key));
	}

	/*****************************DP*****************************/
	public void minimalModelsUsingDP() {
		HashMap<Integer, Boolean> hm;
		int size = DS.SIZE;
//		printLiteralMap();
		while(DS.SIZE!=0) {	
			hm = DS.checkForUnits();
			updateLiteralMap(hm);

			DS.setLiteralMap(literalMap);
			DS.updateRuleDS();

			LinkedList Ts=DS.Ts(vertexCC);
			
			if(!DS.FindMinimalModelForTs(Ts)) {
				System.out.println("UNSAT");
				break;
			}

			literalMap = DS.copyLiteralMap();
			DS.setLiteralMap(literalMap);
			DS.updateRuleDS();
			if(!this.lastVertex)
				break;

			else {
//				printLiteralMap();
				Graph<Integer> g = Graph.initGraph(DS, size);
				vertexCC = Graph.sourceOfGraph(g);
//				System.out.println("vCC "+ID);
//				vertexCC.printList();
			}
		}

	}


	/*****************************WASP*****************************/
	public String[] getCnfContent(LinkedList Ts){
		int size = Ts.getSize();
		String[] toReturn= new String[size];
		Rules.LinkedList.Node nTs ;
		Rules.LinkedList.Node nBody;
		Rules.LinkedList.Node nHead;

		//		if(ID.equals("vertex4")) {
		//			Ts.printList();
		//			DS.printRulesArray();
		//			printLiteralMap();
		//		}

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
	public void writeToFile(LinkedList Ts)
	{
		//System.out.println("writing to file");
		BufferedWriter bw = null;
		FileWriter fw = null;
		String FILENAME=".//alviano-wasp-f3fed39/build/release/"+this.ID;
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
	public ArrayList<String[]> MinimalModelFromScript(){
		String s ="python3 cnf2lparse.py "+this.ID+" | ./wasp -n=10" ;

		String[] cmd = {"/bin/sh", "-c", s};
		String path = ".//alviano-wasp-f3fed39/build/release";

		ArrayList<String[]> list = new ArrayList<>();
		try {
			Process p =Runtime.getRuntime().exec(cmd,null,new File(path));
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = in.readLine();
			while(line!=null) {
//				System.out.println(ID+" kkk "+line);
				if(line.equals("INCOHERENT")) {
					String[] tmp = {""};
					list.add(tmp);
					//					count_unsat++;
				}

				if(line.length()>0 && line.charAt(0)=='{') {
					if(!line.equals("{}")) {

						String[] str = line.substring(1, line.length()-1).split(", ");
						int count = 0;
						for(int i=0; i< str.length; i++) {
							int key = Integer.parseInt(str[i]);
							if(literalMap.containsKey(key)) {
								if(!literalMap.get(key))
									count++;
							}

						}
						if(count!=str.length) {

							//						checkModels(str);
							if(list.size()==0) {
								list.add(str);

								//							return list;
							}
							else {
								Boolean isSmaller = false;
								for(int i=0; i<list.size(); i++) {
									int res = containsAll(list.get(i), str);

									if(res == 0) {
										isSmaller = false;
										break;	// do not insert
									}

									else if(res == 1) {	// replace
										isSmaller = true;
										list.remove(i);
										if(i!=0)
											i--;
									}
								}
								if(isSmaller)
									list.add(str);
							}
							//						for (int i = 0; i < str.length; i++) 			
							//							lst.addAtTail(Integer.parseInt(str[i]));
						}
					}
				}
				line = in.readLine();
			}


//						System.out.println("Models: "+list.size());
//						for(int i=0; i<list.size(); i++)
//							System.out.println(Arrays.toString(list.get(i)));

		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return list;
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

	public boolean ModuminUsingWASP(){
		ArrayList<String[]> minmodel;
		HashMap<Integer, Boolean> hm;
		int size = DS.SIZE;
//		printLiteralMap();
		while(DS.SIZE!=0){
//			for(int i=0; i<this.SCC.size(); i++) {
//				if(this.SCC.get(i).size()==this.vertexCC.getSize()) {
//					int count = 0;
//					for(Vertex<Integer> v: this.SCC.get(i)) {
//						if(this.vertexCC.contains((int)v.getId()))
//							count++;
//					}
//					if(count==this.vertexCC.getSize()) {
//						this.SCC.remove(i);
//						break;
//					}
//				}
//			}

			hm = DS.checkForUnits();
			updateLiteralMap(hm);

			DS.setLiteralMap(literalMap);
			DS.updateRuleDS();

//			System.out.println(ID);
			//			DS.printRulesArray();
			//			DS.printValueOfVariables();

			LinkedList Ts=DS.Ts(vertexCC);
			//			vertexCC.printList();
			//			Ts.printList();
			//			DS.printRulesArray();
			//			DS.printValueOfVariables();
//			hm = DS.copyLiteralMap();
//			updateLiteralMap(hm);
			//			printLiteralMap();
//			DS.setLiteralMap(literalMap);
//			DS.updateRuleDS();

			if(Ts.getSize()>0){
				writeToFile(Ts);	
				minmodel = MinimalModelFromScript();
				if(minmodel.size()==0) {
					System.out.println("UNSAT");
					return false;
				}
//				System.out.println("**");
//				for(int i=0; i<minmodel.size(); i++)
//					System.out.println(Arrays.toString(minmodel.get(i)));
//				System.out.println("**");

				DS.putMinModelInLiteral(minmodel);
				//				updateLiteralMap(hm);
				//				DS.setLiteralMap(literalMap);
				//				DS.updateRuleDS();

			}
			hm = DS.copyLiteralMap();
			updateLiteralMap(hm);
			DS.setLiteralMap(hm);
			DS.updateRuleDS();

			//			else {
			//				boolean val;
			//				if(this.lastVertex)
			//					val = true;
			//				else
			//					val = false;
			//				
			//				Node temp = this.vertexCC.head;
			//
			//				while(temp != null){
			//					literalMap.put(temp.var, val);
			//					temp = temp.next;
			//				}
			//
			//
			//				DS.setLiteralMap(literalMap);
			//				literalMap = DS.copyLiteralMap();
			//			}



			if(!this.lastVertex)
				break;

			else {
				Graph<Integer> g = Graph.initGraph(DS, size);
				vertexCC = Graph.sourceOfGraph(g);
//				System.out.println("vCC");
//				vertexCC.printList();
			}

			/**Update the rules data structure*/


		}
		//System.out.println(numOfSources);
		return true;
	}

	public void printLiteralMap() {
		Set<Integer> keys = literalMap.keySet();
		for(int key: keys){
			System.out.print("Value of " + key +" is ");
			if(literalMap.get(key))
				System.out.println("TRUE");
			else
				System.out.println("FALSE");
		}
	}
}

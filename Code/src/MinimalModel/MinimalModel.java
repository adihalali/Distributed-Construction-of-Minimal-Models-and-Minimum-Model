package MinimalModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import Graph.Graph;
import Graph.StronglyConnectedComponent;
import Graph.Vertex;
import Rules.LinkedList;
import Rules.RulesDataStructure;


public class MinimalModel extends Graph<Integer>{
	private static final double MEGABYTE = 1024L * 1024L;
	
	private RulesDataStructure DS;
	private int rulesNum, varsNum;
	private int count_unsat;
	
	public MinimalModel() {
		super(true);
		this.rulesNum = 0;
		this.varsNum = 0;
		this.count_unsat = 0;
	}

	public static double bytesToMegabytes(double bytes) {
		return bytes / MEGABYTE;
	}

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

		ArrayList<LinkedList> minmodel = MinimalModelFromScript();

		if(count_unsat>0 && minmodel.size()==count_unsat)
			System.out.println("unsatisfiable");

		//		minmodel.printList();
		//		System.out.println("size: "+minmodel.getSize());
	}

	public ArrayList<LinkedList> MinimalModelFromScript(){
		String s ="python3 cnf2lparse.py ex | ./wasp -n=5" ;

		String[] cmd = {"/bin/sh", "-c", s};
		String path = ".//alviano-wasp-f3fed39/build/release";

		ArrayList<LinkedList> list = new ArrayList<>();
		try {
			Process p = Runtime.getRuntime().exec(cmd,null,new File(path));
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = in.readLine();
			
			while(line!=null) {
				if(line.length()>0 && line.charAt(0)=='{') {
					if(line.equals("{}"))
						continue;

					LinkedList lst = new LinkedList();
					if(line.equals("INCOHERENT")) {
						lst.addAtTail(-1);
						count_unsat++;
					}

					else {
						String[] str = line.substring(1, line.length()-1).split(", ");
						for (int i = 0; i < str.length; i++) 			
							lst.addAtTail(Integer.parseInt(str[i]));
						lst.printList();
						list.add(lst);
					}
				}
				line = in.readLine();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return list;
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

	public void graphTest() {
		int size = DS.SIZE;			

		Graph<Integer> g = initGraph(DS, size);

		StronglyConnectedComponent scc = new StronglyConnectedComponent();
		List<Set<Vertex<Integer>>> result = scc.scc(g);
		System.out.println("******\n"+result+"\n******\n");

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

		System.out.println("Min Vertex to remove: " + min_array + " Size of the Seperator: "+ min_array.size());			
	}
	
	public static void main(String[] args) {
		MinimalModel m = new MinimalModel();
		String path=".//CnfFile.txt";

		m.readfile(path);

		m.WASP();
		
		System.out.println("##\n"+m.DS.StringMinimalModel()+"\n##");

		m.graphTest();
	}
}
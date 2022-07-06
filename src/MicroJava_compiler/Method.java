package rs.ac.bg.etf.pp1;

import java.util.ArrayList;

import rs.etf.pp1.symboltable.concepts.Struct;

public class Method {
		
	private String mName;
	private ArrayList<Struct> mParams; 
			
	public Method(String name) {		
		this.mName = name;
		this.mParams = new ArrayList<Struct>();
	}				
	
	public Method(String name, ArrayList<Struct> params) {		
		this.mName = name;
		this.mParams = params;
	}				

	public String getMethodName() {
		return mName;
	}
	
	public ArrayList<Struct> getParameters() {
		return mParams;
	}		
}

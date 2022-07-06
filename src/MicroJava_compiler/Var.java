package rs.ac.bg.etf.pp1;

public class Var {
	
	private String name;
	private boolean isArray;
	private Object value;
	
	public Var(String name, boolean isArray) {
		this.name = name;
		this.isArray = isArray;
	}		
	
	public Var(String name) {		
		this.name = name;
		this.isArray = false;
	}	
	
	public Var(Object value) {	
		this.value = value;
		this.isArray = false;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isArray() {
		return isArray;
	}
	
	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}				
		
}

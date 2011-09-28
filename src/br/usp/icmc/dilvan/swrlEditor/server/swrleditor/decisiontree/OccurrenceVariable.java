package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.decisiontree;

public class OccurrenceVariable {
	
	private String variable;
	private String predicate;
	private int cont;
	
	
	public OccurrenceVariable(String predicate, String variable) {
		super();
		this.predicate = predicate;
		this.variable = variable;
	}
	public OccurrenceVariable(String predicate, String variable, int cont) {
		this(predicate, variable);
		this.cont = cont;
	}
		
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getVariable() {
		return variable;
	}
	public void setVariable(String variable) {
		this.variable = variable;
	}
	public int getCont() {
		return cont;
	}
	public void setCont(int cont) {
		this.cont = cont;
	}
}

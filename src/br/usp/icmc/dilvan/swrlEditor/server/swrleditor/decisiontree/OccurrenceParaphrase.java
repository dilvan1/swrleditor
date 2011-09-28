package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.decisiontree;

public class OccurrenceParaphrase {
	
	private String paraphrase;
	private int cont;
	
	
	public OccurrenceParaphrase(String paraphrase) {
		super();
		this.paraphrase = paraphrase;
	}
	public OccurrenceParaphrase(String paraphrase, int cont) {
		this(paraphrase);
		this.cont = cont;
	}
		
	public String getParaphrase() {
		return paraphrase;
	}
	public void setParaphrase(String paraphrase) {
		this.paraphrase = paraphrase;
	}
	public int getCont() {
		return cont;
	}
	public void setCont(int cont) {
		this.cont = cont;
	}
}

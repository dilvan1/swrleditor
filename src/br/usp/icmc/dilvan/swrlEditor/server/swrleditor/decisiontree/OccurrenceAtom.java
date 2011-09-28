package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.decisiontree;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom;


public class OccurrenceAtom {

	private Atom atom;
	
	private int cont;
		
	public OccurrenceAtom(Atom atom) {
		super();
		this.atom = atom;
	}
	public OccurrenceAtom(Atom atom, int cont) {
		super();
		this.atom = atom;
		this.cont = cont;
	}
	
	public Atom getAtom() {
		return atom;
	}
	public void setAtom(Atom atom) {
		this.atom = atom;
	}
		
	public int getCont() {
		return cont;
	}
	public void setCont(int cont) {
		this.cont = cont;
	}
}

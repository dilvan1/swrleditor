package br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule;


import java.io.Serializable;
import java.util.List;


public interface Rule extends Serializable{

	public void setNameRule(String nameRule);
	public String getNameRule();
	
	public void addAntecedent(Atom atom);
	public void addConsequent(Atom atom);
	
	public void setAntecedentParaphrase(List<String> antecedentParaphrase);
	public List<String> getAntecedentParaphrase();

	public void setConsequentParaphrase(String consequentParaphrase);
	public String getConsequentParaphrase();

	// TODO retirar m�todo de set
	public void setParaphrase(String paraphrase);
	public String getParaphrase();
	
	public List<Atom> getAtoms();
	public List<Atom> getAntecedent();
	public List<Atom> getConsequent();

	// TODO Verificar nomes dos m�todos
	public int getNumVariables();
	public int getNumVariablesAntecedent();
	public int getNumVariablesConsequent();
	
	// TODO Verificar nomes dos m�todos
	public int getNumAtoms();
	public int getNumAntecedent();
	public int getNumConsequent();
	
	public String getFormatedRuleID();
	public String getFormatedRuleLabel();

	public Atom getAtomByValue(String value);
	
	// TODO Verificar nomes dos m�todos
	public int getNumVariablesDistinct();
	public int getNumVariablesDistinctAntecedent();
	public int getNumVariablesDistinctConsequent();
	
	public void setEnabled(boolean enabled);
	public boolean isEnabled();
	
	
	public boolean removeAtom(Atom atom);
	
	public Rule cloneOnlyID();
	
	public boolean existsAtomAntecedent(Atom atom);

	public boolean existsAtomConsequent(Atom atom);
	
}

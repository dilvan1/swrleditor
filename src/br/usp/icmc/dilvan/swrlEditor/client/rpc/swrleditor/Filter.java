package br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Rule;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom.TYPE_ATOM;



@SuppressWarnings("serial")
public class Filter implements Serializable {
	private String filterType;
	private String rulePart;
	private String qryString;
	
	public Filter(){
		// TODO Comments with javadoc this class
	}
	
	public Filter(String filterType, String rulePart, String qryString){
		this.setFilterType(filterType);
		this.setRulePart(rulePart);
		this.setQryString(qryString);
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setRulePart(String rulePart) {
		this.rulePart = rulePart;
	}

	public String getRulePart() {
		return rulePart;
	}

	public void setQryString(String qryString) {
		this.qryString = qryString;
	}

	public String getQryString() {
		return qryString;
	}
	
	public boolean equals(Object o){
		if(o.getClass().equals(this.getClass())){
			Filter f = (Filter) o;
			return getFilterType().equals(f.getFilterType()) && getRulePart().equals(f.getRulePart()) && getQryString().equals(f.getQryString());
		} else {
			return false;
		}
	}

	public boolean contains(Rule rule) {
		boolean ret = false;
		String qry = qryString.toLowerCase();
		
		boolean showAll     = filterType.equalsIgnoreCase("all");
		boolean showClasses = filterType.equalsIgnoreCase("classes");
		boolean showProp  = filterType.equalsIgnoreCase("Properties");
		boolean showBuiltin = filterType.equalsIgnoreCase("builtin(s)");
		
		// Rule Name
		if(filterType.equalsIgnoreCase("rule name") || showAll)
			ret = (rule.getNameRule().toLowerCase().contains(qry)) || ret;

		//Classes, Datatype properties, Object properties, builtins
		List<Atom> list = new ArrayList<Atom>();
		if(rulePart.equalsIgnoreCase("all parts"))
			list = rule.getAtoms();
		else if(rulePart.equalsIgnoreCase("antecedent"))
			list = rule.getAntecedent();
		else if(rulePart.equalsIgnoreCase("consequent"))
			list = rule.getConsequent();
		
		for(Atom a : list){
			if(ret) break;
			if( showAll ||
				(a.getAtomType() == TYPE_ATOM.CLASS && showClasses) || 
				(a.getAtomType() == TYPE_ATOM.DATAVALUE_PROPERTY && showProp) ||
				(a.getAtomType() == TYPE_ATOM.INDIVIDUAL_PROPERTY && showProp) ||
				(a.getAtomType() == TYPE_ATOM.BUILTIN && showBuiltin)){
				ret = (a.getAtomID().toLowerCase().contains(qry)||a.getAtomLabel().toLowerCase().contains(qry));
			}
		}
		
		return ret;
	}
}

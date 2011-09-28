package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.decisiontree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleSet;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTree;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Rule;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Variable;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom.TYPE_ATOM;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;

public class GenerateNodeRootDecisionTreeRankingDependency implements GenerateNodeRootDecisionTree {

	private int valuePriorityClasses = 10000;
	private int valuePriorityProperties = 1000;

	private RuleSet rules;

	private static RulesInTreeRanking rulesTree;

	public GenerateNodeRootDecisionTreeRankingDependency() {
		super();
	}
	public GenerateNodeRootDecisionTreeRankingDependency(RuleSet rules) {
		this();
		this.rules = rules;
	}

	@Override
	public String getAlgorithmName() {
		return "Occurrence Ranking with Dependence of Variables";
	}

	@Override
	public void run() {
		List<OccurrenceVariable> listStatOcurVariable = new ArrayList<OccurrenceVariable>();


		for (Rule rule : rules)
			rankingVariables(listStatOcurVariable, rule.getAntecedent());

		Comparator<OccurrenceVariable> comparadorOcurrVariable = new Comparator<OccurrenceVariable>() {  
			public int compare(OccurrenceVariable s1, OccurrenceVariable s2) {  
				return s1.getCont() < s2.getCont() ? +1 : (s1.getCont() > s2.getCont() ? -1 : 0);  
			}  
		};
		Collections.sort(listStatOcurVariable, comparadorOcurrVariable); 	


		Comparator<OccurrenceAtom> comparadorOcurrAtom = new Comparator<OccurrenceAtom>() {  
			public int compare(OccurrenceAtom s1, OccurrenceAtom s2) {  
				return s1.getCont() < s2.getCont() ? +1 : (s1.getCont() > s2.getCont() ? -1 : 0);  
			}  
		};
		rulesTree = new RulesInTreeRanking();

		for (Rule rule : rules){
			List<OccurrenceAtom> listAnt = new ArrayList<OccurrenceAtom>();

			for (Atom atom: rule.getAntecedent())
				listAnt.add(new OccurrenceAtom(atom));

			for (int j = 0; j < listAnt.size(); j++){
				for (int k = 0; k < listStatOcurVariable.size(); k++){
					if (listStatOcurVariable.get(k).getPredicate().equals(listAnt.get(j).getAtom().getPredicateID())){

						listAnt.get(j).setCont(listStatOcurVariable.size()-k);
						//break;
					}
				}
			}

			Collections.sort(listAnt, comparadorOcurrAtom);
			rulesTree.addRule(listAnt, rule.getConsequent());
		}

	}
	private void rankingVariables(List<OccurrenceVariable> listOcurr, List<Atom> ruleAnt){
		boolean found;

		for (Atom atomAnt :ruleAnt){
			found = false;
			for (Variable parameter: atomAnt.getVariables()){
				for (OccurrenceVariable occurAtom : listOcurr){
					if (occurAtom.getVariable().equals(parameter.getFormatedID()) && 
							occurAtom.getPredicate().equals(atomAnt.getPredicateID()) ){
						occurAtom.setCont(occurAtom.getCont()+1);
						found = true;
					}
				}
				if (!found)
					if (atomAnt.getAtomType() == TYPE_ATOM.CLASS)
						listOcurr.add(new OccurrenceVariable(atomAnt.getPredicateID(), parameter.getFormatedID(), valuePriorityClasses+1));
					else if ((atomAnt.getAtomType() == TYPE_ATOM.DATAVALUE_PROPERTY)||(atomAnt.getAtomType() == TYPE_ATOM.INDIVIDUAL_PROPERTY))
						listOcurr.add(new OccurrenceVariable(atomAnt.getPredicateID(), parameter.getFormatedID(), valuePriorityProperties+1));
					else if ((atomAnt.getAtomType() == TYPE_ATOM.BUILTIN) && 
							(atomAnt.getPredicateID().toLowerCase().startsWith("createowlthing")||atomAnt.getPredicateID().toLowerCase().startsWith("makeowlthing")))
						listOcurr.add(new OccurrenceVariable(atomAnt.getPredicateID(), parameter.getFormatedID(), 0));
					else
						listOcurr.add(new OccurrenceVariable(atomAnt.getPredicateID(), parameter.getFormatedID(), 1));
			}
		}
	}


	@Override
	public void setSWRLFactory(SWRLFactory swrlFactory) {
		// TODO Auto-generated method stub

	}

	@Override
	public NodeDecisionTree getRootNode() {
		return rulesTree.getRoot();
	}

}

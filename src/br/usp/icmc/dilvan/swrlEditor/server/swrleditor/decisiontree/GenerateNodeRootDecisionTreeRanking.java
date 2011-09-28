package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.decisiontree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleSet;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTree;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Rule;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;


public class GenerateNodeRootDecisionTreeRanking implements GenerateNodeRootDecisionTree {
	private RuleSet rules;

	private static RulesInTreeRanking rulesTree;

	public GenerateNodeRootDecisionTreeRanking() {
		super();
	}
	public GenerateNodeRootDecisionTreeRanking(RuleSet rules) {
		this();
		this.rules = rules;
	}

	@Override
	public String getAlgorithmName() {
		return "Occurrence Ranking";
	}
	
	@Override
	public void run(){
		List<OccurrenceAtom> listaStat = new ArrayList<OccurrenceAtom>();
		
		for (Rule rule : rules)
			rankingRule(listaStat, rule.getAntecedent());

		Comparator<OccurrenceAtom> comparador = new Comparator<OccurrenceAtom>() {  
			public int compare(OccurrenceAtom s1, OccurrenceAtom s2) {  
				return s1.getCont() < s2.getCont() ? +1 : (s1.getCont() > s2.getCont() ? -1 : 0);  
			}  
		};
		
		Collections.sort(listaStat, comparador); 	
		
		rulesTree = new RulesInTreeRanking();
		
		for (Rule rule : rules){
			List<OccurrenceAtom> listAnt = new ArrayList<OccurrenceAtom>();
			
			for (Atom atom: rule.getAntecedent())
				listAnt.add(new OccurrenceAtom(atom));
			
			for (int j = 0; j < listAnt.size(); j++){
				for (int k = 0; k < listaStat.size(); k++){
					if (listaStat.get(k).getAtom().getAtomID().equals(listAnt.get(j).getAtom().getAtomID())){
						
						listAnt.get(j).setCont(listaStat.size()-k);
						break;
					}
				}
			}

			Collections.sort(listAnt, comparador);
			rulesTree.addRule(listAnt, rule.getConsequent());
		}
	}

	@Override
	public void setSWRLFactory(SWRLFactory factory){
		
	}

	private void rankingRule(List<OccurrenceAtom> listOcurr, List<Atom> ruleAnt){
		boolean found;

		for (Atom atomAnt :ruleAnt){
			found = false;
			for (OccurrenceAtom occurAtom : listOcurr){
				if (occurAtom.getAtom().getAtomID().equals(atomAnt.getAtomID())){
					occurAtom.setCont(occurAtom.getCont()+1);
					found = true;
				}
			}
			if (!found)
				listOcurr.add(new OccurrenceAtom(atomAnt, 1));
			
		}
	}

	@Override
	public NodeDecisionTree getRootNode() {
		return rulesTree.getRoot();
	}
}

	
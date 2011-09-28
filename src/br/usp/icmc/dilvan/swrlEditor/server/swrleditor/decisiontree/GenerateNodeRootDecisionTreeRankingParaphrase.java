package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.decisiontree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleSet;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTree;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Rule;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;


public class GenerateNodeRootDecisionTreeRankingParaphrase implements GenerateNodeRootDecisionTree {
	private RuleSet rules;

	private static RulesInTreeRankingParaphrase rulesTree;

	public GenerateNodeRootDecisionTreeRankingParaphrase() {
		super();
	}
	public GenerateNodeRootDecisionTreeRankingParaphrase(RuleSet rules) {
		this();
		this.rules = rules;
	}

	@Override
	public String getAlgorithmName() {
		return "Occurrence Ranking with Paraphrase";
	}
	
	@Override
	public void run(){
		List<OccurrenceParaphrase> listaStat = new ArrayList<OccurrenceParaphrase>();
		
		for (Rule rule : rules)
			rankingRule(listaStat, rule.getAntecedentParaphrase());

		Comparator<OccurrenceParaphrase> comparador = new Comparator<OccurrenceParaphrase>() {  
			public int compare(OccurrenceParaphrase s1, OccurrenceParaphrase s2) {  
				return s1.getCont() < s2.getCont() ? +1 : (s1.getCont() > s2.getCont() ? -1 : 0);  
			}  
		};
		Collections.sort(listaStat, comparador); 	

		rulesTree = new RulesInTreeRankingParaphrase();
		
		for (Rule rule : rules){
			List<OccurrenceParaphrase> listAnt = new ArrayList<OccurrenceParaphrase>();
			
			for (String para: rule.getAntecedentParaphrase())
				listAnt.add(new OccurrenceParaphrase(para));
			
			for (int j = 0; j < listAnt.size(); j++){
				for (int k = 0; k < listaStat.size(); k++){
					if (listaStat.get(k).getParaphrase().equals(listAnt.get(j).getParaphrase())){
						
						listAnt.get(j).setCont(listaStat.size()-k);
						break;
					}
				}
			}

			Collections.sort(listAnt, comparador);
			rulesTree.addRule(listAnt, rule.getConsequentParaphrase());
		}
	}

	@Override
	public void setSWRLFactory(SWRLFactory factory){
		
	}

	private void rankingRule(List<OccurrenceParaphrase> listOcurr, List<String> ruleAnt){
		boolean found;

		for (String atomAnt :ruleAnt){
			found = false;
			for (OccurrenceParaphrase occurAtom : listOcurr){
				if (occurAtom.getParaphrase().equals(atomAnt)){
					occurAtom.setCont(occurAtom.getCont()+1);
					found = true;
				}
			}
			if (!found)
				listOcurr.add(new OccurrenceParaphrase(atomAnt, 1));
			
		}
	}

	@Override
	public NodeDecisionTree getRootNode() {
		return rulesTree.getRoot();
	}
}

	
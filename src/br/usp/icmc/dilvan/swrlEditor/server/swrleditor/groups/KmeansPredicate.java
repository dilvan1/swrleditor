package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.groups;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleSet;


public class KmeansPredicate extends KmeansAtom {

	public static String getDescritveAlgorithmName(){
		return new KmeansPredicate().getAlgorithmName();
	}
	
	public KmeansPredicate(){
	}
	
	public KmeansPredicate(RuleSet rules, int numGroups){
		this.rules = rules;
		matriz = new MatrizPredicateCharacteristic();
		createGroups(numGroups);
		setCentersDefault();
		matriz.addRule(rules);
	}
	
	public KmeansPredicate(RuleSet rules, MatrizPredicateCharacteristic m, int numGroups){
		this.rules = rules;
		matriz = m;
		createGroups(numGroups);
		setCentersDefault();
	}
	
	/*@Override
	public void setRuleSet(RuleSet rules) {
		this.rules = rules;
		if(matriz == null){
			matriz = new MatrizPredicateCharacteristic();
		}
		matriz.addRule(rules);
	}*/
	
	@Override
	public String getAlgorithmName() {
		return "K-Means (Similarity predicate)";
	}
}

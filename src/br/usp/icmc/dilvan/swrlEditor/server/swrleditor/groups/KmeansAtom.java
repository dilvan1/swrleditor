package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.groups;

import java.util.ArrayList;
import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleSet;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Rule;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;

public class KmeansAtom implements GroupRules {
	protected MatrizPredicateCharacteristic matriz;
	protected RuleSet rules;
	protected List<Rule> centers;
	protected List<List<Rule>> groups;

	public static String getDescritveAlgorithmName(){
		return new KmeansAtom().getAlgorithmName();
	}

	public KmeansAtom(){
	}

	public KmeansAtom(RuleSet rules, int numGroups){
		this.rules = rules;
		matriz = new MatrizAtomCharacteristic();
		createGroups(numGroups);
		this.setCentersDefault();
		matriz.addRule(rules);
	}

	public KmeansAtom(RuleSet rules, MatrizPredicateCharacteristic m, int numGroups){
		this.rules = rules;
		matriz = m;
		createGroups(numGroups);
		this.setCentersDefault();
	}

	@Override
	public String getAlgorithmName() {
		return "K-Means (Similarity atom)";
	}

	@Override
	public boolean canSetNumberOfGroups(){
		return true;
	}

	@Override
	public void setNumberOfGroups(int numGroups){
		createGroups(numGroups);
		this.setCentersDefault();
	}	
	@Override
	public void setOWLModel(OWLModel model){}

	/*@Override
	public void setRuleSet(RuleSet rules) {
		this.rules = rules;
		if(matriz == null){
			matriz = new MatrizAtomCharacteristic();
		}
		matriz.addRule(rules);
	}*/

	@Override
	public int getNumberOfGroups(){
		return this.groups.size();
	}


	@Override
	public void run(){
		boolean repeat = true;

		while(repeat){
			for(int i = 0; i < rules.size(); i++)
				this.closerCenter(rules.get(i));

			repeat = this.hasNewCenters();
			if(repeat){
				createGroups(groups.size());
			}
		}

		/*System.out.println(" **** Step "+step+" ****");
		for(int i=0; i<groups.size(); i++){
			System.out.println("G"+i+": "+groups.get(i).size()+" rules ");
			if(groups.get(i).size() == 4){
				for(Rule r : groups.get(i)){
					System.out.println(r.getNameRule()+": "+r);
				}
			}
		}*/
	}

	@Override
	public List<List<String>> getGroups() {
		List<List<String>> ret = new ArrayList<List<String>>();
		
		List<String> retAux;

		for(List<Rule> groups : this.getRuleGroups()){
			retAux = new ArrayList<String>();
			for(Rule rule : groups){
				retAux.add(rule.getNameRule());
			}
			ret.add(retAux);
		}
		return ret;
	}

	protected boolean hasNewCenters() {
		boolean modified = false;
		String[] predic = matriz.getCharacteristic();
		int numGroups = this.groups.size();
		int[] base = new int[predic.length];

		for (int i = 0; i < numGroups; i++) {

			if( this.groups.get(i).size() > 1 ){
				// Full groups
				for(int j = 0; j<predic.length; j++){
					base[j] = 0;
				}

				// Calculating the new centers
				//System.out.println("\n[ Group "+i+"]");
				for (Rule r : this.groups.get(i)) {
					//matriz.showValues(r);
					int[] vals = matriz.getValues(r);
					for(int j=0; j < vals.length; j++){
						base[j] += vals[j];
					}
				}


				// Verifying that the new center is equal to previous
				Rule r = this.centers.get(i);
				int[] refs = matriz.getValues(r);
				/*
				matriz.showValues(r);
				for(int aux=0; aux < base.length; aux++)
					System.out.print("-");
				System.out.println(" = Somatorio");
				 */
				for(int j = 0; j<predic.length; j++){
					int avg = base[j] / this.groups.get(i).size();
					if(refs[j] != avg){
						modified = true;
						matriz.addRule(r, predic[j], avg);
					}
				}
				//matriz.showValues(r);
			} else {
				// Empty groups
				modified = true;
				matriz.removeRule(this.centers.get(i));
				Rule r = getRandomRule("n"+this.centers.get(i).getNameRule());
				matriz.addRule(r);

				this.centers.remove(i);
				this.centers.add(i, r);
			}
		}
		return modified;
	}

	protected void closerCenter(Rule rule) {
		double min = 10000;
		int c = 0;
		for(int i = 0; i < this.groups.size(); i++){
			double dif = matriz.getManhathanDistance(rule, centers.get(i));
			if(dif < min){
				min = dif;
				c = i;
			}
		}
		this.groups.get(c).add(rule);

	}

	protected void setCentersDefault() {
		this.centers = new ArrayList<Rule>();
		ArrayList<String> textRules = new ArrayList<String>();
		for (int i = 0; i < this.groups.size(); i++) {
			Rule r;
			do {
				r = getRandomRule("Center "+i);
			} while(textRules.contains(r.getFormatedRuleID()));
			textRules.add(r.getFormatedRuleID());
			matriz.addRule(r);
			this.centers.add(r);	
		}	
	}

	protected Rule getRandomRule(String ruleName) {

		int a = (int)((rules.size() - 1)*Math.random());


		Rule newRule = rules.get(a).cloneOnlyID();
		newRule.setNameRule(ruleName);

		return newRule;

		// Groups are non deterministic, so the solution is make a pseudo random
		//return new RuleIMP(ruleName, rules.get(this.pseudoRand ++).toString()); don't work very well
	}

	protected void createGroups(int numGroups) {
		this.groups = new ArrayList<List<Rule>>();
		for (int i = 0; i < numGroups; i++) {
			this.groups.add(new ArrayList<Rule>());
		} 
	}

	public List<List<Rule>> getRuleGroups(){
		return groups;
	}

	public List<Rule> getRuleGroup(int i){
		return groups.get(i);
	}
}

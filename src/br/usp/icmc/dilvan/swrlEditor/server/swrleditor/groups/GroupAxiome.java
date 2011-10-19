package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;

public class GroupAxiome implements GroupRules {

	private OWLModel owlModel;
	private Map<String, List<String>> rules;
	
	@Override
	public void run() {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> list = null;
		
		RDFProperty hrg = owlModel.getRDFProperty("http://swrl.stanford.edu/ontologies/3.3/swrla.owl#hasRuleCategory");
		SWRLFactory factory = new SWRLFactory(owlModel);

		for (SWRLImp rule : factory.getImps()) {
			
			list = map.get(rule.getPropertyValue(hrg));
			if (list != null){
				list.add(rule.getLocalName());
			}else{
				list = new ArrayList<String>();
				list.add(rule.getLocalName());
				map.put(rule.getPropertyValue(hrg)+"", list);
			}
		}
		rules = map;
	}

	@Override
	public String getAlgorithmName() {
		return "Axiome Groups";
	}

	@Override
	public boolean canSetNumberOfGroups() {
		return false;
	}

	@Override
	public void setNumberOfGroups(int numGroups) {}

	@Override
	public void setOWLModel(OWLModel model) {
		this.owlModel = model;
	}

	@Override
	public int getNumberOfGroups() {
		return rules.size();
	}

	@Override
	public List<List<String>> getGroups() {
		List<List<String>> result = new ArrayList<List<String>>();
		for (List<String> l : rules.values())
			result.add(l);

		return result;
	}

}

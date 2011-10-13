package br.usp.icmc.dilvan.swrlEditor.server.swrleditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.Errors;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.NameGroupAlgorithm;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleEvents;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleSet;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTree;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.AtomImpl;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Rule;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.RuleImpl;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Variable;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.VariableImpl;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom.TYPE_ATOM;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.SWRLService;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.decisiontree.GenerateNodeRootDecisionTree;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.decisiontree.GenerateNodeRootDecisionTreeRanking;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.decisiontree.GenerateNodeRootDecisionTreeRankingDependency;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.decisiontree.GenerateNodeRootDecisionTreeRankingParaphrase;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.groups.GroupRules;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.groups.KmeansAtom;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.groups.KmeansPredicate;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.groups.MatrizPredicateCharacteristic;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.groups.MatrizPredicateCharacteristic.DISTANCE_MODE;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.manager.OntologyManager;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.manager.SWRLManager;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.manager.protege3.OntologyManagerProtege3;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.suggestterms.SuggestTerms;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;



/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SWRLEditorServiceImpl extends RemoteServiceServlet implements SWRLService {
	private Map<String,OntologyManager> ontologyManager = new HashMap<String,OntologyManager>();
	private Map<String,SWRLManager> swrlManager = new HashMap<String,SWRLManager>();

	/**
	 * Return the OntologyManager to manipulates the ontology
	 */
	protected OntologyManager getOntologyManager(String projectName){
		if(!ontologyManager.containsKey(projectName)){
			ontologyManager.put(projectName, new OntologyManagerProtege3(projectName));
		}
		return ontologyManager.get(projectName);
	}

	/**
	 * Return the SWRLManager to manipulates rules in projectName
	 */
	protected SWRLManager getSWRLManager(String projectName){
		if(!swrlManager.containsKey(projectName)){
			swrlManager.put(projectName, getOntologyManager(projectName).getSWRLManager());
		}
		return swrlManager.get(projectName);
	}

	/**
	 * Return the List of Entity presents in the ontology used in projectName
	 * The Entity can be class, object and individual properties and builtins 
	 */
	@Override
	public ArrayList<String> getListBuiltins(String projectName) {
		SWRLManager swrlManager = getSWRLManager(projectName); 
		return (ArrayList<String>) swrlManager.getBuiltins();
	}

	/**
	 * Return all rules presents in projectName 
	 */
	@Override
	public RuleSet getRules(String projectName) {
		return getSWRLManager(projectName).getRules();
	}

	@Override
	public RuleEvents getRuleEvents(String projectName, Long fromVersion) {
		return getSWRLManager(projectName).getRuleEvents(fromVersion);
	}
	
	
	@Override
	public Rule getRule(String projectName, String ruleName) {
		for (Rule rule : getSWRLManager(projectName).getRules())
			if (ruleName.equals(rule.getNameRule()))
				return rule;		
		
		return null;
	} 

	@Override
	public Rule getStringToRule(String projectName, String rule) {
		String parts[] = rule.split("->");
		Rule newRule = new RuleImpl();
		
		if(parts.length>=1){
			if(!parts[0].trim().isEmpty()){
				for(Atom a : getAtoms(projectName, parts[0].trim()))
					newRule.addAntecedent(a);
			}
		}

		if(parts.length==2){
			if(!parts[1].trim().isEmpty()){
				for(Atom a : getAtoms(projectName, parts[1].trim()))
					newRule.addConsequent(a);
			}
		}
		return newRule;
	}
	
	private List<Atom> getAtoms(String projectName, String atoms){
		List<Atom> result = new ArrayList<Atom>();

		String listAtoms[] = atoms.split("\\^");
		for(String atom :listAtoms){
			result.add(getAtom(projectName, atom));
		}
		return result;
	}
	private Atom getAtom(String projectName, String atom){
		Atom newAtom = new AtomImpl();
		
		if (atom.contains("(")){
			
			newAtom.setPredicateID(atom.substring(0,atom.indexOf("(")).trim());
			
			newAtom.setAtomType(setAtomType(projectName, newAtom.getPredicateID()));
			if (newAtom.getAtomType() == TYPE_ATOM.NULL){
				List<String> listId = getOntologyManager(projectName).getIDsForLabel(newAtom.getPredicateID(), true);
				if (listId.size() > 0){
					newAtom.setAtomType(setAtomType(projectName, listId.get(0)));
				}
			}
			
			String aux;
			if (atom.contains(")"))
				aux = atom.substring(atom.indexOf("(")+1,atom.indexOf(")"));
			else
				aux = atom.substring(atom.indexOf("(")+1,atom.length());
			
			if(aux.indexOf("'")>-1){
				StringBuilder newAux = new StringBuilder();
	 			boolean b = false;
				char []vetAux = aux.toCharArray();
				for(int i = 0; i < aux.length(); i++){
					if( vetAux[i] == '\'' )
						b = !b;
					if(vetAux[i] == ',' & b)
						newAux.append("[comma]");
					else
						newAux.append(vetAux[i]);
				}
				aux = newAux.toString();
			}
			
			Variable newVar;
			
			int count = 1;
			for(String v: aux.split(",")){
				if( v.indexOf("[comma]")>-1)
					v = v.replace("[comma]", ",");

				newVar = new VariableImpl();
				
				if(v.trim().startsWith("?"))
					newVar.setSimpleID(v.trim().substring(1));
				else if(v.trim().startsWith("\"")){
					if (v.trim().endsWith("\""))
						newVar.setSimpleID(v.trim().substring(1, v.trim().length()-1));
					else
						newVar.setSimpleID(v.trim().substring(1, v.trim().length()));
				}else
					newVar.setSimpleID(v.trim());
				
				newVar.setTypeVariable(VariableImpl.getTYPE_VARIABLE(newAtom.getAtomType(), newAtom.getPredicateID(), v, count++));
				
				newAtom.addVariable(newVar);
			}
			
			
		}else{
			newAtom.setPredicateID(atom);
			newAtom.setAtomType(TYPE_ATOM.NULL);
		}
		return newAtom;
	}
	
	
	private TYPE_ATOM setAtomType(String projectName, String predicate){
		if (getOntologyManager(projectName).hasOWLClass(predicate))
			return TYPE_ATOM.CLASS;
		else if (getOntologyManager(projectName).hasOWLDatatypePropertie(predicate))
			return TYPE_ATOM.DATAVALUE_PROPERTY;
		else if (getOntologyManager(projectName).hasOWLObjectPropertie(predicate))
			return TYPE_ATOM.INDIVIDUAL_PROPERTY;
		else if (getSWRLManager(projectName).hasBuiltin(predicate))
			return TYPE_ATOM.BUILTIN;
		else if (getOntologyManager(projectName).hasOWLDatatype(predicate))
			return TYPE_ATOM.DATARANGE;
		else if( predicate.equalsIgnoreCase("sameas") || predicate.equalsIgnoreCase("differentfrom") )
			return TYPE_ATOM.SAME_DIFERENT;
		else
			return TYPE_ATOM.NULL;
	}
	
	@Override
	public Errors getErrorsList(String projectName, Rule rule, boolean isNew){
		return getSWRLManager(projectName).getErrorsList(rule, isNew);
	}

	@Override
	public ArrayList<Rule> getSimilarRules(String projectName, Rule base, float distance, boolean isNew) {
		ArrayList<Rule> list = new ArrayList<Rule>();
		MatrizPredicateCharacteristic mpc = new MatrizPredicateCharacteristic();
		mpc.addRule(getRules(projectName));

		Rule rule = null;
		if(isNew){
			rule = base.cloneOnlyID();
			mpc.addRule(rule);
		} else {
			for(Rule r : getRules(projectName)){
				if(r.getNameRule().equals(base.getNameRule())){
					rule = r;
					break;
				}
			}
		}
		if(rule != null){
			for(Rule r : mpc.getIdenticalRules(rule, DISTANCE_MODE.MANHATHAN, distance))
				list.add(r);
		}
		return list;
	}

	@Override
	public ArrayList<Atom> getSuggestTerms(String projectName, Rule rule) {
		rule.setNameRule("suggestRule");
		SuggestTerms st = new SuggestTerms(getRules(projectName), rule);

		ArrayList<Atom> listAtom = new ArrayList<Atom>();
		for(String aux : st.getTermsCombination(true)){
			Atom a = getAtom(projectName, aux);
			listAtom.add(a);
		}
		return listAtom;
	}
	
	//TODO fazer usando http://download.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html
	@Override
	public ArrayList<NameGroupAlgorithm> getGroupAlgorithmsList() {

		ArrayList<NameGroupAlgorithm> ret = new ArrayList<NameGroupAlgorithm>();

		GroupRules grpRules;

		grpRules = new KmeansAtom();
		ret.add(new NameGroupAlgorithm(grpRules.getAlgorithmName(), grpRules.canSetNumberOfGroups()));

		grpRules = new KmeansPredicate();
		ret.add(new NameGroupAlgorithm(grpRules.getAlgorithmName(), grpRules.canSetNumberOfGroups()));

		return ret;
	}

	@Override
	public ArrayList<ArrayList<String>> getGroups(String projectName,
			String algorithmName, int numGroups) {
		ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
		
		GroupRules algorithm = null;

		if ((new KmeansAtom()).getAlgorithmName().equals(algorithmName))
			algorithm = new KmeansAtom(getRules(projectName), numGroups);
		else if ((new KmeansPredicate()).getAlgorithmName().equals(algorithmName))
			algorithm = new KmeansPredicate(getRules(projectName), numGroups);

		if (algorithm != null){
			algorithm.run();
			List<List<String>> original = algorithm.getGroups();
			for (List<String> aux : original)
				ret.add((ArrayList<String>) aux);
		}
			
			
		return ret;
	}

	@Override
	public ArrayList<String> getDecisionTreeAlgorithmsList() {
		
		ArrayList<String> ret = new ArrayList<String>();

		GenerateNodeRootDecisionTree dtRules;

		dtRules = new GenerateNodeRootDecisionTreeRanking();
		ret.add(dtRules.getAlgorithmName());

		dtRules = new GenerateNodeRootDecisionTreeRankingDependency();
		ret.add(dtRules.getAlgorithmName());
		
		dtRules = new GenerateNodeRootDecisionTreeRankingParaphrase();
		ret.add(dtRules.getAlgorithmName());

		return ret;

	}

	@Override
	public NodeDecisionTree getDecisionTree(String projectName, String algorithmName) {
		 
		GenerateNodeRootDecisionTree algorithm = null;

		if ((new GenerateNodeRootDecisionTreeRanking()).getAlgorithmName().equals(algorithmName))
			algorithm = new GenerateNodeRootDecisionTreeRanking(getRules(projectName));
		else if ((new GenerateNodeRootDecisionTreeRankingDependency()).getAlgorithmName().equals(algorithmName))
			algorithm = new GenerateNodeRootDecisionTreeRankingDependency(getRules(projectName));
		else if ((new GenerateNodeRootDecisionTreeRankingParaphrase()).getAlgorithmName().equals(algorithmName))
			algorithm = new GenerateNodeRootDecisionTreeRankingParaphrase(getRules(projectName));

		if (algorithm != null){
			algorithm.run();
			return algorithm.getRootNode();
		}else
			return null;
	}

	/*@Override
	public Options getOptions(String User) {
		if (User.isEmpty()){
			Options opt = new Options();
			
			opt.setAlgorithmSimilarRules(getSimilarRulesAlgorithmsList().get(0));
			opt.setAlgorithmSuggestTerms(getSuggestTermsAlgorithmsList().get(0));
			
			opt.setAllAlgorithmDecisionTree(getDecisionTreeAlgorithmsList());
			
			
			ArrayList<String> GroupAlgorithm = new ArrayList<String>();
			for (NameGroupAlgorithm nga : getGroupAlgorithmsList())
				GroupAlgorithm.add(nga.getName());
			opt.setAllAlgorithmGroups(GroupAlgorithm);
			opt.setAllAlgorithmSimilarRules(getSimilarRulesAlgorithmsList());
			opt.setAllAlgorithmSuggestTerms(getSuggestTermsAlgorithmsList());
			
			opt.setDefaultAlgorithmDecisionTree(getDecisionTreeAlgorithmsList().get(0));
			opt.setDefaultAlgorithmGroups(getGroupAlgorithmsList().get(0).getName());
			opt.setDefaultTabComp("List");
			opt.setDefaultTabVisua("Editor");
			opt.setTypeViewSimilarRulesComp("List");
			opt.setVisibleAlgorithmDecisionTree(getDecisionTreeAlgorithmsList());
			
			
					
			opt.setVisibleAlgorithmGroups(GroupAlgorithm);
			
			opt.setVisibleAutismComp(true);
			opt.setVisibleAutismVisua(true);
			opt.setVisibleDecisionTreeVisua(true);

			opt.setVisibleEditorComp(true);
			opt.setVisibleGroupsVisua(true);
			opt.setVisibleListVisua(true);
			opt.setVisibleSWRLComp(true);
			opt.setVisibleSWRLVisua(true);
			opt.setVisibleTextVisua(true);

			return opt;
		}else{
			return new Options();	
		}
	}*/
	
	@Override
	public boolean deleteRule(String projectName, String ruleName) {
		return getSWRLManager(projectName).deleteRule(ruleName);
	}

	@Override
	public boolean saveNewRule(String projectName, String ruleName, Rule rule) {
		return getSWRLManager(projectName).insertRule(ruleName, rule);
	}

	@Override
	public boolean saveEditRule(String projectName, String ruleName, String oldRuleName, Rule rule) {
		return getSWRLManager(projectName).updateRule(ruleName, oldRuleName, rule);
	}


	@Override
	public ArrayList<String> getSimilarRulesAlgorithmsList() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("Default");

		return ret;
	}
	
	@Override
	public ArrayList<String> getSuggestTermsAlgorithmsList() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("Default");

		return ret;
	}

	@Override
	public boolean runRules(String projectName) {
		return  getSWRLManager(projectName).runRules();
	}

}

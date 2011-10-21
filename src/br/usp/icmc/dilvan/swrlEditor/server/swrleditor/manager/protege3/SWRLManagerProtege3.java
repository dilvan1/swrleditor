package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.manager.protege3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.Errors;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleEvent;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleEvents;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleSet;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleSetImpl;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleEvent.TYPE_EVENT;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.AtomImpl;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Rule;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.RuleImpl;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Variable;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.VariableImpl;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom.TYPE_ATOM;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Variable.TYPE_VARIABLE;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.manager.SWRLManager;

import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltinAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDataRangeAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDifferentIndividualsAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividualPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLSameIndividualAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLVariable;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;

public class SWRLManagerProtege3 implements SWRLManager {
	
	//private RuleSet rules;

	private SWRLFactory factory;
	private OntologyManagerProtege3 ontologyManager;

	private ParaphraseRuleProtege3 paraphraseRule;

	private List<RuleEvent> events;
	
	private String prefix = "";

	public SWRLManagerProtege3(OntologyManagerProtege3 ontologyManager) {
		this.factory = new SWRLFactory(ontologyManager.getOwlModel());
		this.ontologyManager = ontologyManager;
		paraphraseRule = new ParaphraseRuleProtege3(ontologyManager.getOwlModel());
		events = new ArrayList<RuleEvent>();
	}

	@Override
	public Long getVersion() {
		if (events.size() == 0)
			return new Long(0);
		else
			return events.get(events.size()-1).getVersionOntology();
	}

	@Override
	public RuleSet getRules() {
		return createRuleSet();
	}
	
	@Override
	public RuleEvents getRuleEvents(long version) {
		RuleEvents result = new RuleEvents();
		result.setVersionOntology(getVersion());

		for (long i = version; i< result.getVersionOntology(); i++){
			result.add(events.get((int) i));
		}

		return result;
	}

	public SWRLFactory getSWRLFactory(){
		return factory;
	}
	
	private RuleSet createRuleSet() {
		
		RuleSet rulesSet = new RuleSetImpl();
		//int count = 0;
		for (SWRLImp rule : factory.getImps())
			if (!rule.getBrowserText().trim().equals("<EMPTY_RULE>")) {
				
				//count++;
				
				rule.getName();
				rulesSet.add(createRule(rule));
				//if (count == 5)
				//	break;
			}
		rulesSet.setVersionOntology(getVersion());
		
		
		Comparator<Rule> comparador = new Comparator<Rule>() {  
			public int compare(Rule r1, Rule r2) { 
				String name1 = r1.getNameRule().toLowerCase();
				String name2 = r2.getNameRule().toLowerCase();
				return (name1.compareTo(name2) > 0) ? +1 : ((name1.compareTo(name2) < 0) ? -1 : 0);  
			}  
		};
		
		Collections.sort(rulesSet, comparador); 	
		
		
		return rulesSet;
	}

	@SuppressWarnings("unchecked")
	private Rule createRule(SWRLImp rule) {

		Rule newRule = new RuleImpl();

		
		if (prefix.isEmpty())
			if (rule.getName().contains("#"))
				prefix = rule.getName().substring(0, rule.getName().indexOf("#")+1).trim();
		
		
		newRule.setNameRule(rule.getName().substring(rule.getName().indexOf("#") + 1).trim());

		String paraphrase = paraphraseRule.createParaphrase(rule);
		newRule.setParaphrase(ParaphraseRuleProtege3.getFormatedParaphrase(factory, paraphrase)); 
		newRule.setAntecedentParaphrase(ParaphraseRuleProtege3
				.getFormatedParaphraseAntecendent(paraphrase));
		newRule.setConsequentParaphrase(ParaphraseRuleProtege3
				.getFormatedParaphraseConsequent(paraphrase));
		newRule.setEnabled(rule.isEnabled());

		for (SWRLAtom swrlAtom : (Collection<SWRLAtom>) rule.getBody()
				.getValues())
			newRule.addAntecedent(createAtom(swrlAtom));

				for (SWRLAtom swrlAtom : (Collection<SWRLAtom>) rule.getHead()
						.getValues())
					newRule.addConsequent(createAtom(swrlAtom));

						return newRule;

	}

	@SuppressWarnings("unchecked")
	private Atom createAtom(SWRLAtom swrlAtom) {

		Atom newAtom = new AtomImpl();

		if (swrlAtom instanceof SWRLClassAtom) {

			SWRLClassAtom atom = (SWRLClassAtom) swrlAtom;
			
			//newAtom.setPredicateID(atom.getClassPredicate().getBrowserText());
			newAtom.setPredicateID(getUriToName(atom.getClassPredicate().getURI()));
			
			newAtom.setPredicateLabel(UtilProtege3
					.parseCollectionArrayList(atom.getClassPredicate()
							.getLabels()));
			newAtom.setPredicateComment(UtilProtege3
					.parseCollectionArrayList(atom.getClassPredicate()
							.getComments()));

			newAtom.setAtomType(TYPE_ATOM.CLASS);

			newAtom.addVariable(createVariable(newAtom.getAtomType(), newAtom
					.getPredicateID(), atom.getArgument1().getBrowserText(), 1,
					atom.getArgument1().getLabels(), atom.getArgument1()
					.getComments()));

		} else if (swrlAtom instanceof SWRLDatavaluedPropertyAtom) {

			SWRLDatavaluedPropertyAtom atom = (SWRLDatavaluedPropertyAtom) swrlAtom;

			//newAtom.setPredicateID(atom.getPropertyPredicate().getBrowserText());
			newAtom.setPredicateID(getUriToName(atom.getPropertyPredicate().getURI()));

			newAtom.setPredicateLabel(UtilProtege3
					.parseCollectionArrayList(atom.getPropertyPredicate()
							.getLabels()));
			newAtom.setPredicateComment(UtilProtege3
					.parseCollectionArrayList(atom.getPropertyPredicate()
							.getComments()));

			newAtom.setAtomType(TYPE_ATOM.DATAVALUE_PROPERTY);

			newAtom.addVariable(createVariable(newAtom.getAtomType(), newAtom
					.getPredicateID(), atom.getArgument1().getBrowserText(), 1,
					atom.getArgument1().getLabels(), atom.getArgument1()
					.getComments()));

			newAtom.addVariable(createVariable(newAtom.getAtomType(), newAtom
					.getPredicateID(), atom.getArgument2().getBrowserText(), 2,
					null, null));

		} else if (swrlAtom instanceof SWRLIndividualPropertyAtom) {

			SWRLIndividualPropertyAtom atom = (SWRLIndividualPropertyAtom) swrlAtom;

			//newAtom.setPredicateID(atom.getPropertyPredicate().getBrowserText());
			newAtom.setPredicateID(getUriToName(atom.getPropertyPredicate().getURI()));

			newAtom.setPredicateLabel(UtilProtege3
					.parseCollectionArrayList(atom.getPropertyPredicate()
							.getLabels()));
			newAtom.setPredicateComment(UtilProtege3
					.parseCollectionArrayList(atom.getPropertyPredicate()
							.getComments()));

			newAtom.setAtomType(TYPE_ATOM.INDIVIDUAL_PROPERTY);

			newAtom.addVariable(createVariable(newAtom.getAtomType(), newAtom
					.getPredicateID(), atom.getArgument1().getBrowserText(), 1,
					atom.getArgument1().getLabels(), atom.getArgument1()
					.getComments()));

			newAtom.addVariable(createVariable(newAtom.getAtomType(), newAtom
					.getPredicateID(), atom.getArgument2().getBrowserText(), 2,
					atom.getArgument2().getLabels(), atom.getArgument2()
					.getComments()));

		} else if (swrlAtom instanceof SWRLSameIndividualAtom) {

			SWRLSameIndividualAtom atom = (SWRLSameIndividualAtom) swrlAtom;

			newAtom.setPredicateID("sameAs");
			newAtom.setPredicateLabel(new ArrayList<String>());
			newAtom.setPredicateComment(new ArrayList<String>());

			newAtom.setAtomType(TYPE_ATOM.SAME_DIFERENT);

			newAtom.addVariable(createVariable(newAtom.getAtomType(), newAtom
					.getPredicateID(), atom.getArgument1().getBrowserText(), 1,
					atom.getArgument1().getLabels(), atom.getArgument1()
					.getComments()));

			newAtom.addVariable(createVariable(newAtom.getAtomType(), newAtom
					.getPredicateID(), atom.getArgument2().getBrowserText(), 2,
					atom.getArgument2().getLabels(), atom.getArgument2()
					.getComments()));

		} else if (swrlAtom instanceof SWRLDifferentIndividualsAtom) {
			SWRLDifferentIndividualsAtom atom = (SWRLDifferentIndividualsAtom) swrlAtom;

			newAtom.setPredicateID("differentFrom");
			newAtom.setPredicateLabel(new ArrayList<String>());
			newAtom.setPredicateComment(new ArrayList<String>());

			newAtom.setAtomType(TYPE_ATOM.SAME_DIFERENT);

			newAtom.addVariable(createVariable(newAtom.getAtomType(), newAtom
					.getPredicateID(), atom.getArgument1().getBrowserText(), 1,
					atom.getArgument1().getLabels(), atom.getArgument1()
					.getComments()));

			newAtom.addVariable(createVariable(newAtom.getAtomType(), newAtom
					.getPredicateID(), atom.getArgument2().getBrowserText(), 2,
					atom.getArgument2().getLabels(), atom.getArgument2()
					.getComments()));

		} else if (swrlAtom instanceof SWRLDataRangeAtom) {
			// TODO verificar um caso pratico de convers›a
			SWRLDataRangeAtom atom = (SWRLDataRangeAtom) swrlAtom;

			//newAtom.setPredicateID(atom.getDataRange().getBrowserText());
			newAtom.setPredicateID(getUriToName(atom.getDataRange().getURI()));

			newAtom.setPredicateLabel(UtilProtege3
					.parseCollectionArrayList(atom.getDataRange().getLabels()));
			newAtom.setPredicateComment(UtilProtege3
					.parseCollectionArrayList(atom.getDataRange().getComments()));

			newAtom.setAtomType(TYPE_ATOM.DATARANGE);

			newAtom.addVariable(createVariable(newAtom.getAtomType(), newAtom
					.getPredicateID(), atom.getArgument1().getBrowserText(), 1,
					null, null));

		} else if (swrlAtom instanceof SWRLBuiltinAtom) {

			SWRLBuiltinAtom atom = (SWRLBuiltinAtom) swrlAtom;

			//newAtom.setPredicateID(atom.getBuiltin().getBrowserText());
			newAtom.setPredicateID(getUriToName(atom.getBuiltin().getURI()));
			
			newAtom.setPredicateLabel(UtilProtege3
					.parseCollectionArrayList(atom.getBuiltin().getLabels()));
			newAtom.setPredicateComment(UtilProtege3
					.parseCollectionArrayList(atom.getBuiltin().getComments()));

			newAtom.setAtomType(TYPE_ATOM.BUILTIN);

			int count = 1;
			for (Object arg : atom.getArguments().getValues()) {

				if (arg instanceof Integer) {
					newAtom.addVariable(createVariable(newAtom.getAtomType(),
							newAtom.getPredicateID(),
							Integer.toString((Integer) arg), count++, null,
							null));
				} else if (arg instanceof DefaultRDFSLiteral) {
					newAtom.addVariable(createVariable(newAtom.getAtomType(),
							newAtom.getPredicateID(),
							((DefaultRDFSLiteral) arg).getBrowserText(),
							count++, null, null));
				} else if (arg instanceof DefaultSWRLVariable) {
					newAtom.addVariable(createVariable(newAtom.getAtomType(),
							newAtom.getPredicateID(),
							((DefaultSWRLVariable) arg).getBrowserText(),
							count++, ((DefaultSWRLVariable) arg).getLabels(),
							((DefaultSWRLVariable) arg).getComments()));
				} else if (arg instanceof Float) {
					newAtom.addVariable(createVariable(newAtom.getAtomType(),
							newAtom.getPredicateID(),
							Float.toString((Float) arg), count++, null, null));
				} else
					System.out.println("FALTOU:" + arg.getClass());
			}
		} else {
			newAtom = null;
			System.out.println("Atom type of unexpected (SWRLManagerProtege3)");
		}
		return newAtom;
	}

	private Variable createVariable(Atom.TYPE_ATOM typeAtom, String atomID,
			String variableID, int orderVariable, Collection<String> label,
			Collection<String> comment) {
		Variable newVariable = new VariableImpl();

		if (variableID.trim().startsWith("?"))
			newVariable.setSimpleID(variableID.substring(1));
		else
			newVariable.setSimpleID(variableID);

		newVariable.setTypeVariable(VariableImpl.getTYPE_VARIABLE(typeAtom,
				atomID, variableID, orderVariable));

		newVariable
		.setSimpleLabel(UtilProtege3.parseCollectionArrayList(label));
		newVariable.setComment(UtilProtege3.parseCollectionArrayList(comment));

		return newVariable;
	}

	@Override
	public List<String> getBuiltins() {

		List<String> list = new ArrayList<String>();

		SWRLBuiltin builtin;
		Iterator<SWRLBuiltin> builtins = factory.getBuiltins().iterator();
		while (builtins.hasNext()) {
			builtin = builtins.next();
			list.add(builtin.getBrowserText());
		}
		Collections.sort(list);

		return list;

	}

	@Override
	public boolean insertRule(String ruleName, Rule rule) {
		SWRLImp ruleProtege = searchRule(ruleName);

		String swrlRule = replaceLabelToID(rule);

		if (ruleProtege == null) {
			try {
				if (!ruleName.contains("#"))
					ruleName = prefix + ruleName;
				
				ruleProtege = factory.createImp(ruleName, swrlRule);
				addEvent(new RuleEvent("", createRule(ruleProtege), TYPE_EVENT.INSERT, getVersion()+1));
			} catch (SWRLParseException e) {
				e.printStackTrace();
				System.out.println("Regra n‹o foi inserida! Erro: "
						+ e.getMessage());
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean updateRule(String ruleName, String oldRuleName, Rule rule) {
		SWRLImp ruleProtege = searchRule(oldRuleName);

		String swrlRule = replaceLabelToID(rule);

		if (ruleProtege != null) {
			try {
				
				if (!ruleName.contains("#"))
					ruleName = prefix + ruleName;
				
				ruleProtege.deleteImp();
				ruleProtege = factory.createImp(ruleName, swrlRule);
				
				addEvent(new RuleEvent(oldRuleName, createRule(ruleProtege), TYPE_EVENT.EDIT, getVersion()+1));
			} catch (SWRLParseException e) {
				e.printStackTrace();
				System.out.println("Regra n‹o foi alterada! Erro: "
						+ e.getMessage());
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteRule(String ruleName) {
		SWRLImp rule = searchRule(ruleName);

		if (rule != null) {
			rule.deleteImp();
			addEvent(new RuleEvent(ruleName, null, TYPE_EVENT.DELETE, getVersion()+1));
			return true;
		}
		return false;
	}

	private SWRLImp searchRule(String ruleName) {
		for (SWRLImp rule : factory.getImps()) {
			if (rule.getName().trim().endsWith(ruleName.trim()))
				return rule;
		}
		return null;
	}

	private String replaceLabelToID(Rule rule) {
		List<String> Ids, IdsVar;
		for (Atom a : rule.getAtoms()) {
			boolean aux = ontologyManager.hasOWLClass(a.getPredicateID())
					|| ontologyManager.hasOWLDatatype(a.getPredicateID())
					|| ontologyManager.hasOWLDatatypePropertie(a.getPredicateID())
					|| ontologyManager.hasOWLObjectPropertie(a.getPredicateID())
					|| hasBuiltin(a.getPredicateID());
			if (!aux){
				Ids = ontologyManager.getIDsForLabel(a.getPredicateID(), false);
				if (Ids.size() != 1){
					System.out.println("O Label: "+a.getPredicateID()+" possui mais de um ID.");
					if(Ids.size() > 1)
						a.setPredicateID(Ids.get(0));
				}else{
					a.setPredicateID(Ids.get(0));
				}

				for (Variable v : a.getVariables()){
					if (v.getTypeVariable() == TYPE_VARIABLE.INDIVIDUALID){
						IdsVar = ontologyManager.getIDsForLabel(a.getPredicateID(), false);
						if (IdsVar.size() != 1){
							System.out.println("O Label da variavel: "+v.getSimpleID()+" possui mais de um ID.");
							if(IdsVar.size() > 1)
								v.setSimpleID(IdsVar.get(0));
						}else{
							v.setSimpleID(IdsVar.get(0));
						}
					}
				}
			}
		}
		return rule.getFormatedRuleID();
	}

	@Override
	public Errors getErrorsList(Rule rule, boolean isNew) {
		Errors err;
		ErrorsAnalises ea = new ErrorsAnalises(getRules(), ontologyManager,	this);
		err = ea.getErrors(rule, isNew);
		return err;
	}

	@Override
	public boolean hasBuiltin(String builtin) {
		return getBuiltins().contains(builtin);
	}

	private void addEvent(RuleEvent re){
		events.add(re);
	}
	
	private String getUriToName(String uri){
		if (uri.contains("#")){
			if (ontologyManager.getOwlModel().getPrefixForResourceName(uri).isEmpty())
				return NamespaceUtil.getLocalName(uri);
			else
				return ontologyManager.getOwlModel().getPrefixForResourceName(uri) +":"+NamespaceUtil.getLocalName(uri);
		}else
			return uri;
	}

	@Override
	public boolean runRules() {
		try {
			SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create(ontologyManager.getOwlModel());
			/*ruleEngine.reset();
			ruleEngine.importSWRLRulesAndOWLKnowledge();
			ruleEngine.run();
			ruleEngine.writeInferredKnowledge2OWL();
			*/
			ruleEngine.infer();
			
			return true;
		} catch (SWRLRuleEngineException e) {
			System.out.println("Error:"+e.getMessage());
			e.printStackTrace();
			return false;
		}
		
	}


}

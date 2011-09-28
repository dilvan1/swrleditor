package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.decisiontree;


import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTree;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTreeRanking;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTree.ATOM_TYPE;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom.TYPE_ATOM;


public class RulesInTreeRanking {
	

	private NodeDecisionTreeRanking root;
	
	public RulesInTreeRanking() {
		super();
		this.root = new NodeDecisionTreeRanking(null,"");
		this.root.setValue(NodeDecisionTreeRanking.ROOT_VALUE);
		this.root.setAtomType(ATOM_TYPE.ROOT);
	}

	public void addRule(List<OccurrenceAtom> antecedent, List<Atom> consequent){
		
		int i = 0, j, k;
		NodeDecisionTreeRanking nodeTemp = this.root;
		boolean found;
		/* Primeira parte da func�o localiza a posic�o em que os atomos ser�o inseridos na arvore */
		if (this.root.getNumberOfChildren() != 0)
			for (i = 0; i < antecedent.size(); i++){
				found = false;
				//nodeTemp.IncreaseSheetNumber();
				for (j = 0; j < nodeTemp.getChildren().size(); j++){
					if (nodeTemp.getChildren().get(j).getValue().equals(antecedent.get(i).getAtom().getAtomID())){
						found = true;
						break;
					}
				}
				if (found == true){
					nodeTemp = (NodeDecisionTreeRanking) nodeTemp.getChildren().get(j);
				}else{
					break;
				}
			}
		//else
			//nodeTemp.IncreaseSheetNumber();
		
		/* Segunda parte da func�o a partir da posic�o localizada comeca a inserir todos os �tomos */
		NodeDecisionTreeRanking node;
		for (k = i; k < antecedent.size(); k++){
			
			node = new NodeDecisionTreeRanking(nodeTemp, "");

			node.setValue(antecedent.get(k).getAtom().getAtomID());
			node.setAtomType(ConvertToTypeAtom(antecedent.get(k).getAtom().getAtomType()));
			
			nodeTemp.addChildNodes(node);
			nodeTemp = (NodeDecisionTreeRanking) nodeTemp.getChildren().get(nodeTemp.getChildren().size()-1);
		}
	
		node = new NodeDecisionTreeRanking(nodeTemp, consequentToString(consequent));
				
		node.setValue(NodeDecisionTreeRanking.CONSEQUENT_VALUE);
		node.setAtomType(ATOM_TYPE.CONSEQUENT);
		
	
		nodeTemp.addChildNodes(node);
		nodeTemp = (NodeDecisionTreeRanking) nodeTemp.getChildren().get(nodeTemp.getChildren().size()-1);
	}
	private String consequentToString(List<Atom> consequent){
		String result = consequent.get(0).getAtomID();
		for (int i = 1; i < consequent.size(); i++){
			result = result + " ^ <br>" + consequent.get(i).getAtomID();
		}
		
		
		return result;
	}
	
	private ATOM_TYPE ConvertToTypeAtom(TYPE_ATOM type){
		if (type == TYPE_ATOM.CLASS)
			return ATOM_TYPE.CLASS;
		else if (type == TYPE_ATOM.DATAVALUE_PROPERTY)
			return ATOM_TYPE.DATAVALUE_PROPERTY;
		else if (type == TYPE_ATOM.INDIVIDUAL_PROPERTY)
			return ATOM_TYPE.INDIVIDUAL_PROPERTY;
		else if (type == TYPE_ATOM.SAME_DIFERENT)
			return ATOM_TYPE.SAME_DIFERENT;
		else if (type == TYPE_ATOM.BUILTIN)
			return ATOM_TYPE.BUILTIN;
		else if (type == TYPE_ATOM.DATARANGE)
			return ATOM_TYPE.DATARANGE;

		
		return ATOM_TYPE.ROOT;
		
	}
	
	public NodeDecisionTree getRoot() {
		return root;
	}
	
	
}



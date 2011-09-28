package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.decisiontree;


import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTree;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTreeRanking;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTree.ATOM_TYPE;


public class RulesInTreeRankingParaphrase {
	

	private NodeDecisionTreeRanking root;
	
	public RulesInTreeRankingParaphrase() {
		super();
		this.root = new NodeDecisionTreeRanking(null,"");
		this.root.setValue(NodeDecisionTreeRanking.ROOT_VALUE);
		this.root.setAtomType(ATOM_TYPE.ROOT);
	}

	public void addRule(List<OccurrenceParaphrase> antecedent, String consequent){
		
		int i = 0, j, k;
		NodeDecisionTreeRanking nodeTemp = this.root;
		boolean found;
		/* Primeira parte da func�o localiza a posic�o em que os atomos ser�o inseridos na arvore */
		if (this.root.getNumberOfChildren() != 0)
			for (i = 0; i < antecedent.size(); i++){
				found = false;
				//nodeTemp.IncreaseSheetNumber();
				for (j = 0; j < nodeTemp.getChildren().size(); j++){
					if (nodeTemp.getChildren().get(j).getValue().equals(antecedent.get(i).getParaphrase())){
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

			node.setValue(antecedent.get(k).getParaphrase());
			node.setAtomType(ATOM_TYPE.ROOT);
//			node.setAtomType(ConvertToTypeAtom(antecedent.get(k).getAtom().getAtomType()));
			
			nodeTemp.addChildNodes(node);
			nodeTemp = (NodeDecisionTreeRanking) nodeTemp.getChildren().get(nodeTemp.getChildren().size()-1);
		}
	
		node = new NodeDecisionTreeRanking(nodeTemp, consequent);
				
		node.setValue(NodeDecisionTreeRanking.CONSEQUENT_VALUE);
		node.setAtomType(ATOM_TYPE.CONSEQUENT);
		
	
		nodeTemp.addChildNodes(node);
		nodeTemp = (NodeDecisionTreeRanking) nodeTemp.getChildren().get(nodeTemp.getChildren().size()-1);
	}
	
	public NodeDecisionTree getRoot() {
		return root;
	}
	
	
}



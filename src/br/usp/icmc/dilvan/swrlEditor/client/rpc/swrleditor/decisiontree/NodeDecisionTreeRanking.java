package br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree;


import java.util.ArrayList;
import java.util.List;

public class NodeDecisionTreeRanking implements NodeDecisionTree {

	//private defaultNodeInRulesTreeLabel nodeLabel;


	/* Default label to represent the root node */
	public final static String ROOT_VALUE = "root";


	/* Default label to represent the consequent node */
	public final static String CONSEQUENT_VALUE = "Consequent";


	private static final long serialVersionUID = 1L;

	private String value;
	private ATOM_TYPE type;
	private String toolTip;

	private NodeDecisionTree parentNode;

	/* Nodos filhos */
	private List<NodeDecisionTree> childNodes = new ArrayList<NodeDecisionTree>();


	public NodeDecisionTreeRanking() {
		super();
	}

	public NodeDecisionTreeRanking(NodeDecisionTree parentNode, String toolTip) {
		super();
		this.value = "";
		this.toolTip = toolTip;
		//this.nodeLabel = new defaultNodeInRulesTreeLabel(this);
		this.parentNode = parentNode;
	}

	public String getValue(){
		return value;
	}
	public void setValue(String value){
		this.value = value;
		//nodeLabel.setText(value);
	}

	@Override
	public ATOM_TYPE getAtomType(){
		return type;
	}
	public void setAtomType(ATOM_TYPE type){
		this.type = type;
	}

	public NodeDecisionTree getParentNode() {
		return parentNode;
	}

	public void setParentNode(NodeDecisionTree parentNode) {
		this.parentNode = parentNode;
	}

	public String getToolTip() {
		return toolTip;
	}
	public void setToolTip(String toolTip){
		this.toolTip = toolTip;
	}

	public void addChildNodes(NodeDecisionTree node) {
		this.childNodes.add(node);
	}

	public NodeDecisionTree removeChildNodes(int index) {
		return this.childNodes.remove(index);
	}

	public NodeDecisionTree getChild(int index) {
		return this.childNodes.get(index);
	}

	public void setChildNodes(int index, NodeDecisionTree node) {
		this.childNodes.set(index, node);
	}

	public int getNumberOfChildren() {
		return this.childNodes.size();
	}

	@Override
	public List<NodeDecisionTree> getChildren() {
		
		return childNodes;
	}
}

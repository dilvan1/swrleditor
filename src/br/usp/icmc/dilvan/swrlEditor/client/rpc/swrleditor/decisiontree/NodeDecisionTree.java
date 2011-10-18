package br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree;

import java.io.Serializable;
import java.util.List;

/**
 * Interface to represent a Decision Tree for the SWRL Editor GUI.
 * 
 * @author Joao Orlando
 * @author Dilvan Moreira
 *
 */
public interface NodeDecisionTree extends Serializable {
	
	/* Default label to represent the root node */
	public final static String ROOT_VALUE = "root";


	/* Default label to represent the consequent node */
	public final static String CONSEQUENT_VALUE = "Consequent";
	
	
	/**
	 * Enum listing all the possible types of the tree nodes.
	 * All names are self explanatory. 
	 * 
	 * ROOT is only used once for the root node.
	 * 
	 * The CONSEQUENT node represents all atoms of the consequent part of the rule. 
	 * In our Decision Trees, antecedent atoms are represented as individual nodes, but
	 * consequent atoms are represented as one leaf node (see GUI interface).
	 * Actually we show only antecedents, consequents appear as 
	 * blank nodes with their contents shown as tooltips.
	 */
	public enum ATOM_TYPE {CLASS, INDIVIDUAL_PROPERTY, SAME_DIFERENT, DATAVALUE_PROPERTY, BUILTIN, DATARANGE, ROOT, CONSEQUENT};


	/**
	 *  Gets string representing the text to be shown in each node of the tree.
	 *  @return text to be shown.
	 */
	public String getValue();
	
	/**
	 * AtomType represents the type of each rule atom. It is used by the GUI to assign colors to the nodes.
	 * @return The atom type 
	 * @see NodeDecisionTree.ATOM_TYPE
	 */
	public ATOM_TYPE getAtomType();
	
	/**
	 * Text to be shown as tooltip.
	 * In the case of consequent nodes, they would usually show the string representation of the rule,
	 * but implementations can change this behavior.
	 */
	public String getToolTip();

	/**
	 * Gets parent node.
	 * @return parent node.
	 */
	public NodeDecisionTree getParentNode();
	
	/**Gets all children nodes.
	 * 
	 * @return List containing all children nodes
	 */
	public List<NodeDecisionTree> getChildren();
	
//		Do you think that these two methods would also be useful?
//	
//	/**
//	 * Gets a child node.
//	 * @param i index of the child node.
//	 * @return The child node with index i.
//	 */
//	public NodeDecisionTree getChild(int i);
//	/**
//	 * Gets the number of children nodes.
//	 * @return number of children nodes.
//	 */
//	public int getNumberOfChildren();
}

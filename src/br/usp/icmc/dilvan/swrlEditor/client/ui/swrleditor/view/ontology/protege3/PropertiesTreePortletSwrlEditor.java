package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.ontology.protege3;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom.TYPE_ATOM;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.OntologyView.Presenter;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.event.TreeNodeListenerAdapter;

import edu.stanford.bmir.protege.web.client.model.Project;
import edu.stanford.bmir.protege.web.client.rpc.data.EntityData;
import edu.stanford.bmir.protege.web.client.ui.ontology.properties.PropertiesTreePortlet;

public class PropertiesTreePortletSwrlEditor extends PropertiesTreePortlet {

	private TreeNodeListenerAdapter nodeListener;

	public PropertiesTreePortletSwrlEditor(Project project) {
		super(project);
		setTitle("");
	}

	public void setPresenter(final Presenter presenter){
		nodeListener = new TreeNodeListenerAdapter() {
			@Override
			public void onClick(Node node, EventObject e) {
				if ( ((TreeNode)node).getIconCls().equals("protege-datatype-property-icon"))
					presenter.setSelectedPredicate(TYPE_ATOM.DATAVALUE_PROPERTY, ((TreeNode)node).getText());
				else
					presenter.setSelectedPredicate(TYPE_ATOM.INDIVIDUAL_PROPERTY, ((TreeNode)node).getText());
			}
		};
	}

	@Override
	protected TreeNode createTreeNode(EntityData entityData) {
		TreeNode node = super.createTreeNode(entityData);
		node.addListener(nodeListener);
		return node;
	}

	@Override
	protected void addToolbarButtons() { }

    @Override
    protected Tool[] getTools() {
        return new Tool[] {};
    }
}

package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.impl;

import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom.TYPE_ATOM;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.OntologyView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.ontology.protege3.ClassTreePortletSwrlEditor;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.ontology.protege3.PropertiesTreePortletSwrlEditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.logical.shared.SelectionEvent;

import edu.stanford.bmir.protege.web.client.model.Project;

public class OntologyViewImpl extends Composite implements OntologyView {

	
	@UiField SimplePanel pnlClasses;
	@UiField ScrollPanel pnlProperties;
	@UiField Tree treeBuiltins;

	private ClassTreePortletSwrlEditor classTree;
	private PropertiesTreePortletSwrlEditor propertiesTree;
	
	private Presenter presenter;
	
	private static OntologyViewImplUiBinder uiBinder = GWT
			.create(OntologyViewImplUiBinder.class);

	interface OntologyViewImplUiBinder extends
			UiBinder<Widget, OntologyViewImpl> {
	}

	public OntologyViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		this.presenter.getBuiltins();

		classTree.setPresenter(this.presenter);
		propertiesTree.setPresenter(this.presenter);
	}

	@Override
	public void setBuiltins(List<String> builtins) {
		treeBuiltins.clear();
		for(String s : builtins){
			treeBuiltins.addItem(s);
		}
	}
	
	@UiHandler("treeBuiltins")
	void onTreeBuiltinsSelection(SelectionEvent<TreeItem> event) {

		
		int left = event.getSelectedItem().getAbsoluteLeft() + 50;
		int top = event.getSelectedItem().getAbsoluteTop() + 20;
		
		presenter.setSelectedPredicate(TYPE_ATOM.BUILTIN, event.getSelectedItem().getText(), left, top);
				
	}

	@Override
	public void setProjectProtege(Project project) {
		classTree = new ClassTreePortletSwrlEditor(project);
		//classTree.setSize("98%", "510px");
		classTree.setSize("98%", "510px");
		propertiesTree = new PropertiesTreePortletSwrlEditor(project);
		//propertiesTree.setSize("98%", "510px");
		propertiesTree.setSize("94%", "100%");
		propertiesTree.setAutoWidth(true);
		
		pnlClasses.clear();
		pnlProperties.clear();
		pnlClasses.add(classTree);
		pnlProperties.add(propertiesTree);
	}

}

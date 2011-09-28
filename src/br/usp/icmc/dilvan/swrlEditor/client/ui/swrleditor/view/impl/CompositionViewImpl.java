package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.impl;

import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Rule;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom.TYPE_ATOM;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.CompositionView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.OntologyView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.VisualizationView.TYPE_VIEW;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.composition.CompositionTabEditorView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.composition.CompositionTabSWRLView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.composition.CompositionTabView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.composition.SuggestTerm;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.similarrules.SimilarRulePanel;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.similarrules.SimilarRulePanel.TYPE_VIEW_SIMILAR;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class CompositionViewImpl extends Composite implements CompositionView {

	
	@UiField SimplePanel pnlProperties;
	@UiField Button btnSimilarRules;
	@UiField TextBox txtRuleName;
	@UiField Button btnSave;
	@UiField TabLayoutPanel tabsComposition;
	@UiField SimplePanel tabList;
	@UiField SimplePanel tabSWRL;

	@UiField TabLayoutPanel tabButton;
	@UiField VerticalPanel pnlAddErrors;
	@UiField VerticalPanel lstSuggestTerms;
	
	private static CompositionViewImplUiBinder uiBinder = GWT
			.create(CompositionViewImplUiBinder.class);

	
	private Presenter presenter;
	private boolean writePermission;
	
	private OntologyView ontologyView;

	private CompositionTabView editorSelected = null;
	private CompositionTabEditorView editorList;
	private CompositionTabSWRLView editorSWRL;
	
	private Rule rule;
	private TYPE_VIEW typeView;
	
	interface CompositionViewImplUiBinder extends
			UiBinder<Widget, CompositionViewImpl> {
	}

	public CompositionViewImpl(OntologyView ontologyView, CompositionTabEditorView editorList, CompositionTabSWRLView editorSWRL) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.ontologyView = ontologyView;
		pnlProperties.add(this.ontologyView);
		
		this.editorList = editorList;
		tabList.add(this.editorList);
		
		this.editorSWRL = editorSWRL;
		tabSWRL.add(editorSWRL);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		ontologyView.setPresenter((OntologyView.Presenter) presenter);
		editorList.setPresenter((CompositionTabView.Presenter) presenter);
		editorSWRL.setPresenter((CompositionTabView.Presenter) presenter);
	}

	@Override
	public void setWritePermission(boolean permission) {
		this.writePermission = permission;
		btnSave.setEnabled(this.writePermission);
	}
	
	@Override
	public void setTypeView(TYPE_VIEW typeView) {
		this.typeView = typeView;
		editorList.setTypeView(typeView);
		editorSWRL.setTypeView(typeView);
	}
	
	@Override
	public void setRule(Rule rule) {
		this.rule = rule;
		txtRuleName.setText(this.rule.getNameRule());
		
		showRule();
	}
	
	private void showRule(){
		editorSelected = null;
		switch (tabsComposition.getSelectedIndex()) {
		case 0:
			editorSelected = editorList;
			editorList.setRule(this.rule);
			break;
			
		case 1:
			editorSelected = editorSWRL;
			editorSWRL.setRule(this.rule);
			break;

		default:
			break;
		}
	}

	@Override
	public void setBuiltins(List<String> builtins) {
		ontologyView.setBuiltins(builtins);
	}

	@Override
	public void setSelectedOntologyItem(TYPE_ATOM typeAtom, String predicate, boolean isAntecedent) {
		editorSelected.addPredicate(typeAtom, predicate, isAntecedent);
	}

	@Override
	public void setErrosAndWarnings(List<String> listErros, List<String> listWarnings) {
		btnSave.setEnabled((this.writePermission)&&(listErros.size()+listWarnings.size() == 0));

		pnlAddErrors.clear();
		tabButton.setTabText(0, "Errors ("+listErros.size()+") Warnings ("+listWarnings.size()+")");
		
		if( (listErros == null && listWarnings == null) || (listErros.size() == 0 && listWarnings.size() == 0) ){
			Label lbl = new Label("No Error :)");
			lbl.setStyleName("noerror");
			pnlAddErrors.add(lbl);
			if (this.writePermission)
				btnSave.setEnabled(true);
			return;
		}
		btnSave.setEnabled(false);
		for(String error : listErros){
			HTML lbl = new HTML(error);
			lbl.setStyleName("error");
			pnlAddErrors.add(lbl);
		}
		for(String error : listWarnings){
			HTML lbl = new HTML(error);
			lbl.setStyleName("warning");
			pnlAddErrors.add(lbl);
		}
		
	}

	@Override
	public void showSimilarTerms(List<Atom> listTerms) {
		tabButton.setTabText(1, "Suggest Terms ("+listTerms.size()+")");
		
		lstSuggestTerms.clear();
		if(listTerms == null  || listTerms.isEmpty()){
			lstSuggestTerms.add(new Label("No terms"));
			return ;
		}
		SuggestTerm st;
		for(Atom term : listTerms){
			
			st = new SuggestTerm(term, (SuggestTerm.Presenter) presenter, typeView);
			lstSuggestTerms.insert(st, 0);
		}		
	}
	
	@Override
	public void addAtom(Atom atom, boolean isAntecedent) {
		editorSelected.addAtom(atom, isAntecedent);
	}
	
	@Override
	public void showSimilarRules(String ruleName, List<Rule> rules) {
		DialogBox panel = new DialogBox(true);
		SimilarRulePanel similarRulePanel = new SimilarRulePanel(rules);
		similarRulePanel.setPopUp(panel);
		if (tabsComposition.getSelectedIndex() == 0)
			similarRulePanel.showListRules(ruleName, typeView, null, TYPE_VIEW_SIMILAR.EDITOR);
		else
			similarRulePanel.showListRules(ruleName, typeView, null, TYPE_VIEW_SIMILAR.SWRL);
			
		similarRulePanel.setPopUpConfiguration(panel);
	}
	
	@UiHandler("txtRuleName")
	void onRuleNameFocus(FocusEvent event) {
		txtRuleName.selectAll();
	}
	
	@UiHandler("btnSave")
	void onBtnSaveClick(ClickEvent event) {
		if(this.writePermission && btnSave.isEnabled()){
			presenter.saveRule();
		}
	}
	
	@UiHandler("btnSimilarRules")
	void onBtnSimilarRulesClick(ClickEvent event) {
		presenter.getSimilarRules();
	}
	
	//TODO melhor fazer quando perder o foco ou quando pressionar uma tecla, 
	//se manter na tecla fazer o nome da regra separado da validação do resto no server
	@UiHandler("txtRuleName")
	void onRuleNameBlur(BlurEvent event) {
		//presenter.setRuleName(txtRuleName.getText());
	}
	@UiHandler("txtRuleName")
	void onTxtRuleNameKeyUp(KeyUpEvent event) {
		presenter.setRuleName(txtRuleName.getText());
	}
	
	@UiHandler("tabsComposition")
	void onTabsCompositionSelection(SelectionEvent<Integer> event) {
		showRule();
	}
}

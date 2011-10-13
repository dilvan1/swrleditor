package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.impl;

import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.client.resources.Resources;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.Filter;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom.TYPE_ATOM;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.FilterView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.OntologyView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.UtilView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;


public class FilterViewImpl extends Composite implements FilterView{

	@UiField SimplePanel pnlProperties;
	@UiField ListBox listTypes;
	@UiField ListBox listParts;
	@UiField SuggestBox txtFilter;
	@UiField Button btnAddFilter;
	@UiField Button btnFilterRun;
	@UiField VerticalPanel listFilters;

	private OntologyView ontologyView;


	private static FilterViewImplUiBinder uiBinder = GWT
			.create(FilterViewImplUiBinder.class);

	private Presenter presenter;

	interface FilterViewImplUiBinder extends UiBinder<Widget, FilterViewImpl> {
	}

	public FilterViewImpl(OntologyView ontologyView) {
		initWidget(uiBinder.createAndBindUi(this));
		fillFilterBox();

		this.ontologyView = ontologyView;
		pnlProperties.add(this.ontologyView);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		ontologyView.setPresenter((OntologyView.Presenter) presenter);
	}

	@Override
	public void setFilters(List<Filter> filters){
		listFilters.clear();
		for (final Filter  filter : filters)
			addFilter(filter);
	}

	private void addFilter(final Filter filter){
		HorizontalPanel pnlFilter = new HorizontalPanel();

		HTML btn = new HTML("<img src=\""+Resources.INSTANCE.fechar().getURL()+"\">");

		btn.setStyleName(Resources.INSTANCE.swrleditor().linkRemove());
		btn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				removeFilter(filter);
			}
		});

		HTML lbl = new HTML(UtilView.formatFilter(filter));
		lbl.addStyleName(Resources.INSTANCE.swrleditor().itemFilter());

		pnlFilter.add(btn);
		pnlFilter.add(lbl);

		listFilters.add(pnlFilter);

	}

	private void removeFilter(Filter f){
		presenter.removeFilter(f);
	}

	/**
	 * Fill the filter box showed in Visualization mode
	 */
	private void fillFilterBox() {
		String listFilter[] = new String[] { "All", "Rule name", "Classes","Datatype properties", "Object properties", "Builtin(s)" };
		String listRulePart[] = new String[] {"All parts", "Antecedent", "Consequent"};

		for (String aux : listFilter)
			listTypes.addItem(aux);

		for (String aux : listRulePart)
			listParts.addItem(aux);
	}

	@Override
	public void setBuiltins(List<String> builtins) {
		ontologyView.setBuiltins(builtins);
	}

	@Override
	public void populateFields(TYPE_ATOM typeAtom, String value) {
		if (typeAtom == TYPE_ATOM.CLASS)
			listTypes.setSelectedIndex(2);
		else if (typeAtom == TYPE_ATOM.DATAVALUE_PROPERTY)
			listTypes.setSelectedIndex(3);
		else if (typeAtom == TYPE_ATOM.INDIVIDUAL_PROPERTY)
			listTypes.setSelectedIndex(4);
		else if (typeAtom == TYPE_ATOM.BUILTIN)
			listTypes.setSelectedIndex(5);
		txtFilter.setText(value);
	}

	@UiHandler("btnAddFilter")
	void onBtnAddFilterClick(ClickEvent event) {
		if (txtFilter.getValue().equals("")){
			Window.alert("Inform the Query");
			txtFilter.setFocus(true);
		}else{

			if (presenter.addFilter(listTypes.getValue(listTypes.getSelectedIndex()), 
					listParts.getValue(listParts.getSelectedIndex()), txtFilter.getText())){

				listTypes.setSelectedIndex(0);
				listParts.setSelectedIndex(0);
				txtFilter.setValue("");
				listTypes.setFocus(true);
			}

		}

	}

	@UiHandler("btnFilterRun")
	void onBtnFilterRunClick(ClickEvent event) {
		presenter.goToVisualization();	
	}

	@UiHandler("listTypes")
	void onFilterTypeChange(ChangeEvent event) {
		if("Rule name".equals(listTypes.getItemText(listTypes.getSelectedIndex()))){
			listParts.setEnabled(false);
		} else {
			listParts.setEnabled(true);
		}
	}

	@UiHandler("txtFilter")
	void onFilterStringKeyUp(KeyUpEvent event) {
		if( event.getNativeKeyCode() == 13 && !txtFilter.getValue().isEmpty())
			btnAddFilter.click();
		
	}
}

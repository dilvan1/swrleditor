package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.visualization;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Rule;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.VisualizationView.Presenter;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.VisualizationView.TYPE_VIEW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;


public abstract class SimpleRuleView extends Composite {

	//TODO criar um presenter para esta classe
	
	protected TYPE_VIEW typeView;
	protected Rule rule;
	protected Presenter presenter;
	protected ClickHandler clickHandlerAtom = null;
	
	private static final Binder binder = GWT.create(Binder.class);
	@UiField DisclosurePanel panelContent;
	@UiField FlowPanel contentRule;
	@UiField Button btnEditRule;
	@UiField Button btnDuplicateRule;
	@UiField Button btnDeleteRule;
	@UiField Button btnSimilarRule;
	@UiField HorizontalPanel panelButtons;

	interface Binder extends UiBinder<Widget, SimpleRuleView> {
	}

	/**
	 * Create the rule visualizer called SimpleRuleView
	 * @param panelMain the properties panel
	 */	
	public SimpleRuleView() {
		initWidget(binder.createAndBindUi(this));
	}

	/**
	 * Create the rule visualizer called SimpleRuleView
	 * @param panelMain the properties panel
	 * @param rule The rule that this widget represents and showing
	 */	
	public SimpleRuleView(Rule rule, Presenter presenter, TYPE_VIEW typeView){
		this();
		this.presenter =  presenter;
		this.typeView = typeView;
		panelContent.getHeader().setStyleName("header-name-class");
		setRule(rule);
	}
	
	public void setRule(Rule rule){
		this.rule = rule;
		getElement().setId("list_"+rule.getNameRule());
		panelContent.getHeader().setTitle(rule.getFormatedRuleID());
		panelContent.getHeaderTextAccessor().setText(rule.getNameRule());
	}

	public void setWritePermission(boolean permission){
		btnEditRule.setEnabled(permission);
		btnDuplicateRule.setEnabled(permission);
		btnDeleteRule.setEnabled(permission);
	}
	
	/**
	 * Define if the action buttons is visible or not 
	 * @param visible True if visible and False when is not visible
	 */
	public void setPanelButtonVisible(boolean visible){
		panelButtons.setVisible(visible);
	}
	
	/**
	 * Determine if the action buttons is visible or not
	 * @return true if visible and false when is not visible
	 */
	public boolean getPanelButtonVisible(){
		return panelButtons.isVisible();
	}
	
	/**
	 * Set the widget to show the rule in a format view 
	 */
	public abstract void show();

	/**
	 * Define the auto height to the widget 
	 */
	public void setHeight(){
		onPanelContentOpen(null);
	}
	
	/**
	 * This method is called when the widget is maximized and calculate the height 
	 * @param event The event passed to the event handler
	 */
	@UiHandler("panelContent")
	void onPanelContentOpen(OpenEvent<DisclosurePanel> event) {
		if(panelButtons.isVisible())
			this.setHeight(String.valueOf(panelContent.getOffsetHeight() + 35)+"px");
		else
			this.setHeight(String.valueOf(panelContent.getOffsetHeight())+"px");
	}
	
	/**
	 * This method is called when the widget is minimized and calculate the height 
	 * @param event The event passed from the event handler
	 */
	@UiHandler("panelContent")
	void onPanelContentClose(CloseEvent<DisclosurePanel> event) {
		if(panelButtons.isVisible())
			this.setHeight("65px");
		else
			this.setHeight("25px");
	}
	
	/**
	 * Called when the user click in the edit rule button
	 * @param event The event passed from the event handler
	 */
	@UiHandler("btnEditRule")
	void onBtEditRuleClick(ClickEvent event) {
		presenter.goToEditRule(rule.getNameRule());
	}
	
	/**
	 * Called when the user click in the duplicate rule button
	 * @param event The event passed from the event handler
	 */
	@UiHandler("btnDuplicateRule")
	void onBtDuplicatedRuleClick(ClickEvent event) {
		presenter.goToDuplicateAndEditRule(rule.getNameRule());
	}

	@UiHandler("btnDeleteRule")
	void onBtnDeleteRuleClick(ClickEvent event) {
		presenter.deleteRule(rule.getNameRule());
	}

	/**
	 * Called when the user click in the similar rule button
	 * @param event The event passed from the event handler
	 */
	@UiHandler("btnSimilarRule")
	void onBtnSimilarRuleClick(ClickEvent event) {
		presenter.getSimilarRules(getRule(),false);
	}
	
	/**
	 * Minimize (close) the widget
	 */
	public void closeRule() {
		panelContent.setOpen(false);
	}
	
	/**
	 * Maximize (open) the widget
	 */
	public void openRule(){
		panelContent.setOpen(true);
	}
	
	/**
	 * Define if the rule is open or not
	 * @return True if the widget is opened (maximized) or False if it is closed (minimized)
	 */
	public boolean isOpenRule(){
		return panelContent.isOpen();
	}

	/**
	 * Return the primitive widget that this class use
	 * @return The DisclosurePanel
	 */
	public DisclosurePanel getPanelContent(){
		return panelContent;
	}
	
	/**
	 * Set a click event to the atoms in the rule
	 * @param clickHandlerAtom The method that will be called when the user click in some atom
	 */
	public void setClickHandlerAtom(ClickHandler clickHandlerAtom) {
		this.clickHandlerAtom = clickHandlerAtom;
	}

	/**
	 * Get the click event to the atoms in the rule 
	 * @return The method that will be called when the user click in some atom
	 */
	public ClickHandler getClickHandlerAtom() {
		return clickHandlerAtom;
	}
	
	public String getRuleName(){
		return rule.getNameRule();
	}
	
	public Rule getRule(){
		return rule;
	}
	
	

}
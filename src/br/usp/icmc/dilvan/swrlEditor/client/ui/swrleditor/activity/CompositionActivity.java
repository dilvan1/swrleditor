package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.activity;

import java.util.ArrayList;


import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.Errors;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Rule;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.RuleImpl;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom.TYPE_ATOM;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.ClientFactory;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.mvp.AppActivityMapper;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.CompositionPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.VisualizationPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.CompositionPlace.COMPOSITION_MODE;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.CompositionView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.OntologyView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.VisualizationView.TYPE_VIEW;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.composition.CompositionTabView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.composition.PopUpLocationView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.composition.SuggestTerm;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;


public class CompositionActivity extends AbstractActivity implements
		CompositionView.Presenter, OntologyView.Presenter,
		CompositionTabView.Presenter, SuggestTerm.Presenter {
	
	private ClientFactory clientFactory;
	private AppActivityMapper activityMapper;

	private COMPOSITION_MODE mode;

	private String originalNameRule;
	private Rule rule = null;
	private boolean modifiedRule = false;
	private boolean blockedAlterRule = false; 

	public CompositionActivity(CompositionPlace place, ClientFactory clientFactory, AppActivityMapper activityMapper) {
		this.clientFactory = clientFactory;
		this.activityMapper = activityMapper;
		
		mode = place.getModeComposition();

		getRule(place.getRuleName(), place.getModeComposition());
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		CompositionView compositionView = clientFactory.getCompositionView();
		compositionView.setTypeView(TYPE_VIEW.ID);
		compositionView.setPresenter(this);
		compositionView.setWritePermission(clientFactory.getProject().hasWritePermission());

		containerWidget.setWidget(compositionView.asWidget());
	}

	private void getRule(final String ruleName, final COMPOSITION_MODE mode) {
		clientFactory.getRpcService().getRule(clientFactory.getProjectName(),
				ruleName, new AsyncCallback<Rule>() {

					public void onSuccess(Rule result) {
						if (mode == COMPOSITION_MODE.NEW) {
							rule = new RuleImpl();
						} else if (mode == COMPOSITION_MODE.EDIT) {
							rule = result;
						} else if (mode == COMPOSITION_MODE.DUPLICATE) {
							rule = result.cloneOnlyID();
							rule.setNameRule(rule.getNameRule() + "_1");
						}
						
						
						
						originalNameRule = rule.getNameRule();
						clientFactory.getCompositionView().setRule(rule);
					}

					public void onFailure(Throwable caught) {
						Window.alert("Error fetching rule : " + ruleName);
					}
				});
	}

	@Override
	public void setSelectedPredicate(TYPE_ATOM typeAtom, String predicate) {
		PopUpLocationView panel = new PopUpLocationView(clientFactory.getCompositionView());
		panel.setItemName(predicate, typeAtom);
		panel.setAutoHideEnabled(true);
		panel.show();
		panel.center();
	}

	@Override
	public void getBuiltins() {
		clientFactory.getRpcService().getListBuiltins(
				clientFactory.getProjectName(),
				new AsyncCallback<ArrayList<String>>() {

					@Override
					public void onSuccess(ArrayList<String> result) {
						clientFactory.getCompositionView().setBuiltins(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error fetching builtins");
					}
				});
	}

	@Override
	public void setRuleName(String text) {
		rule.setNameRule(text);
		getErrors();
		setModifyRule();
	}

	@Override
	public void saveRule() {
		if (mode == COMPOSITION_MODE.EDIT){
			clientFactory.getRpcService().saveEditRule(clientFactory.getProjectName(), rule.getNameRule(), originalNameRule, rule, new AsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					activityMapper.forcedSWRLEvents();
					if (!result)
						Window.alert("Was not possible to saved the rule.");
					else{
						Window.alert("The rule "+ rule.getNameRule()+" has been saved successfully!.");
						goToVisualization();
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Error saving rule");
				}
			});
		}else{
			clientFactory.getRpcService().saveNewRule(clientFactory.getProjectName(), rule.getNameRule(), rule, new AsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					activityMapper.forcedSWRLEvents();
					if (!result)
						Window.alert("Was not possible to saved the rule.");
					else{
						Window.alert("The rule "+ rule.getNameRule()+" has been saved successfully!.");
						goToVisualization();
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Error saving rule");
				}
			});
		}
	}

	@Override
	public void getErrors() {
		boolean isNew = (mode != COMPOSITION_MODE.EDIT);
		clientFactory.getRpcService().getErrorsList(
				clientFactory.getProjectName(), rule, isNew,
				new AsyncCallback<Errors>() {
					@Override
					public void onSuccess(Errors result) {
						clientFactory.getCompositionView().setErrosAndWarnings(
								result.getErrors(), result.getWarnings());
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error to load the list of errors and warnings");
					}
				});

		getSuggestTerms();
	}

	@Override
	public void setModifyRule() {
		modifiedRule = true;
	}

	@Override
	public boolean isModifiedRule() {
		return modifiedRule;
	}

	@Override
	public void setRuleString(String newRule) {
		blockedAlterRule = true;
		clientFactory.getRpcService().getStringToRule(
				clientFactory.getProjectName(), newRule,
				new AsyncCallback<Rule>() {
					@Override
					public void onSuccess(Rule result) {
						result.setNameRule(rule.getNameRule());
						result.setEnabled(rule.isEnabled());
						rule = result;
						clientFactory.getCompositionView().setRule(rule);
						getErrors();
						setModifyRule();
						blockedAlterRule = false;
					}

					@Override
					public void onFailure(Throwable caught) {
						blockedAlterRule = false;
						Window.alert("Error to load the list of errors and warnings");
					}
				});
	}
	

	@Override
	public boolean isBlockedAlterRule() {
		return blockedAlterRule;
	}
	
	@Override
	public void getSimilarRules() {
		getSimilarRules(2, mode != COMPOSITION_MODE.EDIT);
	}
	
	private void getSimilarRules(final int distance, final boolean isNew) {

		if (rule.getNumAntecedent() == 0 && rule.getNumConsequent() == 0) {
			clientFactory.getCompositionView().showSimilarRules(null, null);
			return;
		}
		clientFactory.getRpcService().getSimilarRules(
				clientFactory.getProjectName(), rule, distance, isNew,
				new AsyncCallback<ArrayList<Rule>>() {

					@Override
					public void onSuccess(ArrayList<Rule> result) {
						if (result.size() == 0 && distance < 10) {
							// This can be better if the auto ajust the correct
							// limiar
							getSimilarRules(distance + 2, isNew);
							return;
						}
						clientFactory.getCompositionView().showSimilarRules(
								rule.getNameRule(), result);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error fetching similar rules");
					}
				});
	}
	
	
	private void getSuggestTerms() {
		clientFactory.getRpcService().getSuggestTerms(clientFactory.getProjectName(), rule, new AsyncCallback<ArrayList<Atom>>() {
			@Override
			public void onSuccess(ArrayList<Atom> result) {
				clientFactory.getCompositionView().showSimilarTerms(result);
			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error to load the suggest terms");
			}
		});
		
	}

	@Override
	public void addAtom(Atom atom, boolean isAntecedent) {
		clientFactory.getCompositionView().addAtom(atom, isAntecedent);
	}
	
	private void goToVisualization(){
		clientFactory.getPlaceController().goTo(new VisualizationPlace(VisualizationPlace.ID_RULE_NAME+"="+rule.getNameRule()+"&"+clientFactory.getURLWebProtege()));
	}


}
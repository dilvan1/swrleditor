package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.activity;

import java.util.ArrayList;
import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.Filter;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleSet;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTree;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Rule;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.ClientFactory;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.activity.info.GeneratedRulesInfo;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.activity.visualization.AutismModel;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.mvp.AppActivityMapper;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.CompositionPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.DefaultPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.FilterPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.OptionsPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.VisualizationPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.CompositionPlace.COMPOSITION_MODE;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.util.Options;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.util.UtilLoading;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.InfoView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.OptionsView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.VisualizationView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.VisualizationView.TYPE_VIEW;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.impl.InfoViewImpl;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;


public class VisualizationActivity extends AbstractActivity implements VisualizationView.Presenter, InfoView.Presenter {

	private ClientFactory clientFactory;
	private AppActivityMapper activityMapper;

	private String ruleSelected;

	public VisualizationActivity(VisualizationPlace place, ClientFactory clientFactory, AppActivityMapper activityMapper) {
		this.ruleSelected = place.getRuleSelected();
		this.clientFactory = clientFactory;
		this.activityMapper = activityMapper;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		UtilLoading.showLoadSWRLEditor();	
		
		getDecisionTreeAlgorithmList();
		getGroupAlgorithmList();
		
		VisualizationView visualizationView = clientFactory.getVisualizationView();
		visualizationView.setPresenter(this);
		visualizationView.setWritePermission(clientFactory.getProject().hasWritePermission());
		visualizationView.setConfiguration(clientFactory.getPortletConfiguration());
		
		if (!ruleSelected.isEmpty())
			visualizationView.setRuleSelected(ruleSelected);
		
		if (Options.getStringOption(clientFactory.getPortletConfiguration(), OptionsView.UsingIDorLabelStr, OptionsView.viewUsingLabel).equals(OptionsView.viewUsingID)){
			visualizationView.setTypeView(TYPE_VIEW.ID);
		}else{
			visualizationView.setTypeView(TYPE_VIEW.LABEL);
		}

		visualizationView.refreshRulesView();
		getRuleSet();

		containerWidget.setWidget(visualizationView.asWidget());
	}

	
	@Override
	public void goToNewRule() {
		activityMapper.setNewAntecedent("");
		DefaultPlace newPlace = new CompositionPlace(CompositionPlace.ID_MODE+"="+COMPOSITION_MODE.NEW+"&"+clientFactory.getURLWebProtege());
		clientFactory.getPlaceController().goTo(newPlace);
	}
	
	@Override
	public void goToNewRule(String antecedent) {
		activityMapper.setNewAntecedent(antecedent);
		DefaultPlace newPlace = new CompositionPlace(CompositionPlace.ID_MODE+"="+COMPOSITION_MODE.NEW+"&"+clientFactory.getURLWebProtege());
		clientFactory.getPlaceController().goTo(newPlace);
	}

	@Override
	public void goToOptions() {
		clientFactory.getPlaceController().goTo(new OptionsPlace(clientFactory.getURLWebProtege()));
	}

	@Override
	public void goToInfo() {
		InfoView info = clientFactory.getInfoView();

		info.setPresenter(this);
		info.setRuleInfo(new GeneratedRulesInfo(activityMapper.getRules()).getRulesInfo());
		((InfoViewImpl)info).show();
	}

	@Override
	public void goToFilter() {
		clientFactory.getPlaceController().goTo(new FilterPlace(clientFactory.getURLWebProtege()));
	}
	
	
	@Override
	public void goToEditRule(String ruleName) {
		DefaultPlace newPlace = new CompositionPlace(CompositionPlace.ID_MODE+"="+COMPOSITION_MODE.EDIT+"&"
				+CompositionPlace.ID_RULE_NAME+"="+ruleName+"&"
				+clientFactory.getURLWebProtege());
		clientFactory.getPlaceController().goTo(newPlace);
	}
	
	@Override
	public void goToEditRule(String antecedent, String consequent) {
		clientFactory.getRpcService().getRuleName(clientFactory.getProjectName(), antecedent, consequent, new AsyncCallback<String>() {
			public void onSuccess(String result) {
				if (result.equals("")){
					Window.alert("Could not find this rule!");
				}else{
	
					DefaultPlace newPlace = new CompositionPlace(CompositionPlace.ID_MODE+"="+COMPOSITION_MODE.EDIT+"&"
							+CompositionPlace.ID_RULE_NAME+"="+result+"&"
							+clientFactory.getURLWebProtege());
					clientFactory.getPlaceController().goTo(newPlace);
				}

			}

			public void onFailure(Throwable caught) {
				Window.alert("Could not find this rule!");
			}
		});
	}
	
	
	@Override
	public void goToDuplicateAndEditRule(String ruleName) {
		DefaultPlace newPlace = new CompositionPlace(CompositionPlace.ID_MODE+"="+COMPOSITION_MODE.DUPLICATE+"&"
				+CompositionPlace.ID_RULE_NAME+"="+ruleName+"&"
				+clientFactory.getURLWebProtege());
		clientFactory.getPlaceController().goTo(newPlace);
	}
	
	@Override
	public void getRuleSet() {
		if (activityMapper.getRules() == null) {
			clientFactory.getRpcService().getRules(clientFactory.getProjectName(), new AsyncCallback<RuleSet>() {
						public void onSuccess(RuleSet result) {
							activityMapper.setRules(result);
							clientFactory.getVisualizationView().setRuleSet(result);
						}

						public void onFailure(Throwable caught) {
							UtilLoading.hide();
							Window.alert("Error fetching rule information details");
						}
					});
		}else{
			UtilLoading.hide();
		}
	}

	@Override
	public AutismModel getAutismModel(Rule rule, TYPE_VIEW typeView) {
		return new AutismModel(rule, typeView);
	}

	
	private void getGroupAlgorithmList() {
		Timer timer = new Timer() {
			@Override
			public void run() {
				if (activityMapper.getGroupsAlgorithms() == null){
					schedule(100);
				}else{
					clientFactory.getVisualizationView().setGroupAlgorithmList(activityMapper.getGroupsAlgorithms(), clientFactory.getPortletConfiguration());
				}
			
			}
		};
		timer.schedule(100);
	}

	@Override
	public void getGroups(String algorithmName, int numberGroups) {
		clientFactory.getRpcService().getGroups(clientFactory.getProjectName(),
				algorithmName, numberGroups,
				new AsyncCallback<ArrayList<ArrayList<String>>>() {
			public void onSuccess(ArrayList<ArrayList<String>> result) {
				clientFactory.getVisualizationView().setGroups(result);
			}

			public void onFailure(Throwable caught) {
				UtilLoading.hide();
				Window.alert("Error fetching Group Algorithm");
			}
		});
	}


	private void getDecisionTreeAlgorithmList() {
		Timer timer = new Timer() {
			@Override
			public void run() {
				if (activityMapper.getDecisionTreeAlgorithms() == null){
					schedule(100);
				}else{
					clientFactory.getVisualizationView().setDecisionTreeAlgorithmList(activityMapper.getDecisionTreeAlgorithms(), clientFactory.getPortletConfiguration());
				}
			
			}
		};
		timer.schedule(100);
	}

	@Override
	public void getDecisionTree(String algorithmName) {
		clientFactory.getRpcService().getDecisionTree(
				clientFactory.getProjectName(), algorithmName,
				new AsyncCallback<NodeDecisionTree>() {
					public void onSuccess(NodeDecisionTree result) {
						clientFactory.getVisualizationView().setDecisionTree(
								result);
					}

					public void onFailure(Throwable caught) {
						UtilLoading.hide();
						Window.alert("Error fetching DecisionTree Algorithm");
					}
				});
	}

	@Override
	public Rule getRuleByName(String ruleName) {
		Rule ret = null;
		for (Rule rule : activityMapper.getRules()) {
			if (ruleName.equals(rule.getNameRule())) {
				ret = rule;
				break;
			}
		}
		return ret;
	}

	@Override
	public List<Filter> getFilters() {
		return activityMapper.getFilters();
	}

	@Override
	public void getSimilarRules(Rule rule, boolean isNew) {
		getSimilarRules(rule, 2, isNew);
	}

	private void getSimilarRules(final Rule rule, final int distance, final boolean isNew) {

		if (rule.getNumAntecedent() == 0 && rule.getNumConsequent() == 0) {
			clientFactory.getVisualizationView().showSimilarRules(null, null);
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
							getSimilarRules(rule, distance + 2, isNew);
							return;
						}
						clientFactory.getVisualizationView().showSimilarRules(
								rule.getNameRule(), result);
					}

					@Override
					public void onFailure(Throwable caught) {
						UtilLoading.hide();
						Window.alert("Error fetching similar rules");
					}
				});
	}

	@Override
	public void deleteRule(String nameRule) {
		clientFactory.getRpcService().deleteRule(clientFactory.getProjectName(), nameRule, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				activityMapper.forcedSWRLEvents();
				
				if (!result)
					Window.alert("Was not possible to delete the rule.");
			}
			@Override
			public void onFailure(Throwable caught) {
				UtilLoading.hide();
				Window.alert("Error to deleting rule.");
			}

		});
	}

	@Override
	public void runRules() {
		Window.alert("The rules will be executed in the background.");

		clientFactory.getRpcService().runRules(clientFactory.getProjectName(), new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				clientFactory.getVisualizationView().finishedRun();
				Window.alert("Rules execute successfully");
			}
			@Override
			public void onFailure(Throwable caught) {
				UtilLoading.hide();
				clientFactory.getVisualizationView().finishedRun();
				Window.alert("Error to run rules");
			}

		});
		
	}

	@Override
	public void goToVisualization() {
		((InfoViewImpl) clientFactory.getInfoView()).hide();
	}

}

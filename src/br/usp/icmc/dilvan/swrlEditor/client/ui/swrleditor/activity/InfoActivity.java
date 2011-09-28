package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.activity;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleSet;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.ClientFactory;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.activity.info.GeneratedRulesInfo;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.mvp.AppActivityMapper;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.InfoPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.VisualizationPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.InfoView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;


public class InfoActivity extends AbstractActivity implements InfoView.Presenter {
	private ClientFactory clientFactory;
	private AppActivityMapper activityMapper;

	public InfoActivity(InfoPlace place, ClientFactory clientFactory, AppActivityMapper activityMapper) {
		this.clientFactory = clientFactory;
		this.activityMapper = activityMapper;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		InfoView InfoView = clientFactory.getInfoView();
		
		getRules();
		
		InfoView.setPresenter(this);
		containerWidget.setWidget(InfoView.asWidget());
	}
	
	@Override
	public void goToVisualization() {
		clientFactory.getPlaceController().goTo(new VisualizationPlace(clientFactory.getURLWebProtege()));
	}
	
	
	private void getRules(){
		if (activityMapper.getRules() != null)
			clientFactory.getInfoView().setRuleInfo(new GeneratedRulesInfo(activityMapper.getRules()).getRulesInfo());
		else{
			clientFactory.getRpcService().getRules(clientFactory.getProjectName(), new AsyncCallback<RuleSet>() {
				public void onSuccess(RuleSet result) {
					activityMapper.setRules(result);
					clientFactory.getInfoView().setRuleInfo(new GeneratedRulesInfo(result).getRulesInfo());
				}

				public void onFailure(Throwable caught) {
					Window.alert("Error fetching rule information details");
				}
			});
		}
	}
	
}

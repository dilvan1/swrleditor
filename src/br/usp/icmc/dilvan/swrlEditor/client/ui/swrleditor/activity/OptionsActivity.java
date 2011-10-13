package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.activity;

import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.ClientFactory;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.mvp.AppActivityMapper;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.OptionsPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.VisualizationPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.util.UtilLoading;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.OptionsView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.gwtext.client.widgets.MessageBox;


import edu.stanford.bmir.protege.web.client.rpc.AbstractAsyncHandler;

public class OptionsActivity extends AbstractActivity implements OptionsView.Presenter {
	private ClientFactory clientFactory;
	private AppActivityMapper activityMapper;

	public OptionsActivity(OptionsPlace place, ClientFactory clientFactory, AppActivityMapper activityMapper) {
		this.clientFactory = clientFactory;
		this.activityMapper = activityMapper;
	}

	
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {

		final OptionsView optionsView = clientFactory.getOptionsView();
		
		Timer timer = new Timer() {
			@Override
			public void run() {
				if (activityMapper.getGroupsAlgorithmsStr() == null && 
						activityMapper.getDecisionTreeAlgorithms() == null && 
						activityMapper.getSimilarRulesAlgorithms() == null && 
						activityMapper.getSuggestTermsAlgorithms() == null){
					schedule(100);
				}else{
					optionsView.setOptions(clientFactory.getPortletConfiguration(), 
							activityMapper.getGroupsAlgorithmsStr(), 
							activityMapper.getDecisionTreeAlgorithms(), 
							activityMapper.getSimilarRulesAlgorithms(), 
							activityMapper.getSuggestTermsAlgorithms());
					UtilLoading.hide();
				}
			
			}
		};
		timer.schedule(100);
		
		optionsView.setPresenter(this);
		optionsView.setWritePermission(clientFactory.getProject().hasWritePermission());

		containerWidget.setWidget(optionsView.asWidget());
	}
	@Override
	public void goToVisualization() {
		clientFactory.getPlaceController().goTo(new VisualizationPlace(clientFactory.getURLWebProtege()));
	}
	
	@Override
	public void setStringOption(String name, String value) {
		clientFactory.setOption(name, (Object) value);
	}

	@Override
	public void setBooleanOption(String name, Boolean value) {
		clientFactory.setOption(name, (Object) value);
	}

	@Override
	public void saveOptions() {
		clientFactory.saveOptions(new SaveConfigHandler());
	}

	
	class SaveConfigHandler extends AbstractAsyncHandler<Void> {
        @Override
        public void handleFailure(Throwable caught) {
            GWT.log("Error in saving configurations (UI Layout)", caught);
            MessageBox.alert("There were problems at saving the options. Please try again later.");
        }

        @Override
        public void handleSuccess(Void result) {
            MessageBox.alert("Options saved successfully.");
        	goToVisualization();
        }
    }
	
}

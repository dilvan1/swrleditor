package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.activity;

import java.util.ArrayList;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.Filter;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom.TYPE_ATOM;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.ClientFactory;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.mvp.AppActivityMapper;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.FilterPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.VisualizationPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.FilterView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.OntologyView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;


public class FilterActivity extends AbstractActivity implements FilterView.Presenter, OntologyView.Presenter  {
	private ClientFactory clientFactory;
	private AppActivityMapper activityMapper;
	
	public FilterActivity(FilterPlace place, ClientFactory clientFactory, AppActivityMapper activityMapper) {
		this.clientFactory = clientFactory;
		this.activityMapper = activityMapper;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		FilterView filterView = clientFactory.getFilterView();
		filterView.setPresenter(this);
		containerWidget.setWidget(filterView.asWidget());
	}

	@Override
	public void goToVisualization() {
		clientFactory.getPlaceController().goTo(new VisualizationPlace(clientFactory.getURLWebProtege()));
	}

	@Override
	public boolean addFilter(String filterType, String rulePart, String qryString) {
		Filter newFilter = new Filter(filterType, rulePart, qryString);
		if (activityMapper.getFilters().contains(newFilter)){
			Window.alert("This filter already exists.");
			return false;
		}else{
			activityMapper.getFilters().add(newFilter);
			clientFactory.getFilterView().setFilters(activityMapper.getFilters());
			return true;
		}
	}

	@Override
	public void removeFilter(Filter filter) {
		activityMapper.getFilters().remove(filter);
		clientFactory.getFilterView().setFilters(activityMapper.getFilters());
	}
	
	
	@Override
	public void setSelectedPredicate(TYPE_ATOM typeAtom, String value) {
		clientFactory.getFilterView().populateFields(typeAtom, value);
	}

	@Override
	public void getBuiltins() {
		clientFactory.getRpcService().getListBuiltins(clientFactory.getProjectName(), new AsyncCallback<ArrayList<String>>() {
			
			@Override
			public void onSuccess(ArrayList<String> result) {
				clientFactory.getFilterView().setBuiltins(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error fetching builtins");
			}
		});		
	}
}

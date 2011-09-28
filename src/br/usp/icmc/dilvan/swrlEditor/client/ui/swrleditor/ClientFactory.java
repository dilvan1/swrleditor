package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor;

import java.util.Map;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleEvents;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.RuleSet;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.CompositionView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.FilterView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.InfoView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.OptionsView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.VisualizationView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

import edu.stanford.bmir.protege.web.client.model.Project;
import edu.stanford.bmir.protege.web.client.rpc.AbstractAsyncHandler;
import edu.stanford.bmir.protege.web.client.rpc.data.layout.PortletConfiguration;


public interface ClientFactory
{
	EventBus getEventBus();
	PlaceController getPlaceController();
	SWRLServiceAsync getRpcService();
	
	VisualizationView getVisualizationView();
	CompositionView getCompositionView();
	OptionsView getOptionsView();
	InfoView getInfoView();
	FilterView getFilterView();
	
	void setProject(Project project);
	Project getProject();
	String getProjectName();
	void setPortletConfiguration(PortletConfiguration portletConfiguration);
	Map<String, Object> getPortletConfiguration();
	
	
	void setWritePermission(boolean hasWritePermission);
	void addEventsViews(RuleSet rules, RuleEvents events);
	
	void setUserLogged(String User);
	String getUserLogged();
	
	
	void setURLWebProtege(String urlWebProtege);
	String getURLWebProtege();
	
	
	void setOption(String name, Object value);
	void saveOptions(AbstractAsyncHandler<Void> saveHandler);
}

package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor;

import java.util.Collection;

import br.usp.icmc.dilvan.swrlEditor.client.resources.Resources;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.mvp.AppActivityMapper;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.mvp.AppPlaceHistoryMapper;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.CompositionPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.FilterPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.InfoPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.OptionsPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.VisualizationPlace;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.stanford.bmir.protege.web.client.model.GlobalSettings;
import edu.stanford.bmir.protege.web.client.model.Project;
import edu.stanford.bmir.protege.web.client.model.event.EntityCreateEvent;
import edu.stanford.bmir.protege.web.client.model.event.EntityDeleteEvent;
import edu.stanford.bmir.protege.web.client.model.event.EntityRenameEvent;
import edu.stanford.bmir.protege.web.client.model.event.OntologyEvent;
import edu.stanford.bmir.protege.web.client.model.event.PropertyValueEvent;
import edu.stanford.bmir.protege.web.client.model.listener.OntologyListener;
import edu.stanford.bmir.protege.web.client.rpc.AbstractAsyncHandler;
import edu.stanford.bmir.protege.web.client.rpc.ProjectConfigurationServiceManager;
import edu.stanford.bmir.protege.web.client.rpc.data.EntityData;
import edu.stanford.bmir.protege.web.client.rpc.data.layout.ProjectConfiguration;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractEntityPortlet;

@SuppressWarnings("unchecked")
public class SwrlEditorPortlet extends AbstractEntityPortlet {

	private ClientFactory clientFactory;

	private SimplePanel swrlEditorWidget;
	private Place defaultPlace;

	HorizontalPanel page;
	SimplePanel pnl1;
	SimplePanel pnl2;

	public SwrlEditorPortlet(final Project project) {
		super(project, true);
	}

	@Override
	public Collection<EntityData> getSelection() {
		return null;
	}

	@Override
	public void reload() {
	}

	@Override
	public void initialize() {
		Resources.INSTANCE.swrleditor().ensureInjected();

		setTitle("SWRL Editor");

		swrlEditorWidget = new SimplePanel();
		add(swrlEditorWidget);

		WaitingCreateToRun waiting = new WaitingCreateToRun(swrlEditorWidget, 1000) {
			@Override
			public void run() {
				SwrlEditorPortlet.this.loadSWRLEditor();
			}
		};
		waiting.start();
	}

	private void loadSWRLEditor() {
		String urlWebProtege = "ontology="
				+ project.getProjectName().replace(" ", "+")
				+ "&tab=SwrlEditorTab";

		String newURL;

		if (Window.Location.getHref().contains("?gwt.codesvr=127.0.0.1")){
			String href = Window.Location.getHref();

			href =  href.substring(href.indexOf("?gwt.codesvr=127.0.0.1"));

			if (href.indexOf("#") >= 0)
				href =  href.substring(0, href.indexOf("#"));

			newURL = href+"#visualization:"
					+ urlWebProtege;
		}else
			newURL = "#visualization:" + urlWebProtege;

		if (!Window.Location.getHref().trim().contains(newURL) && 
				Window.Location.getHref().trim().contains("#"+CompositionPlace.getNamePlace()+":") &&
				Window.Location.getHref().trim().contains("#"+FilterPlace.getNamePlace()+":") &&
				Window.Location.getHref().trim().contains("#"+InfoPlace.getNamePlace()+":") &&
				Window.Location.getHref().trim().contains("#"+OptionsPlace.getNamePlace()+":") &&
				Window.Location.getHref().trim().contains("#"+VisualizationPlace.getNamePlace()+":") 
				)
			Window.Location.replace(newURL);
		

		defaultPlace = new VisualizationPlace("");

		// Create ClientFactory using deferred binding so we can replace with
		// different
		// impls in gwt.xml
		clientFactory = GWT.create(ClientFactory.class);
		clientFactory.setProject(project);
		clientFactory.setUserLogged(GlobalSettings.getGlobalSettings().getUserName());

		clientFactory.setWritePermission(project
				.hasWritePermission(GlobalSettings.getGlobalSettings()
						.getUserName()));
		clientFactory.setURLWebProtege(urlWebProtege);
		clientFactory.setPortletConfiguration(getPortletConfiguration());

		EventBus eventBus = clientFactory.getEventBus();
		PlaceController placeController = clientFactory.getPlaceController();

		// Start ActivityManager for the main widget with our ActivityMapper
		ActivityMapper activityMapper = new AppActivityMapper(clientFactory);
		ActivityManager activityManager = new ActivityManager(activityMapper,
				eventBus);
		activityManager.setDisplay(swrlEditorWidget);

		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		AppPlaceHistoryMapper historyMapper = GWT
				.create(AppPlaceHistoryMapper.class);
		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(
				historyMapper);
		historyHandler.register(placeController, eventBus, defaultPlace);

		// Goes to place represented on URL or default place
		historyHandler.handleCurrentHistory();


	}

	@Override
	public void onPermissionsChanged(Collection<String> permissions) {
		if (clientFactory != null){
			clientFactory.setUserLogged(GlobalSettings.getGlobalSettings().getUserName());
			clientFactory.setWritePermission(project
					.hasWritePermission(GlobalSettings.getGlobalSettings()
							.getUserName()));
		}
	}

}
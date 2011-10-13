package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor;

import java.util.Collection;

import br.usp.icmc.dilvan.swrlEditor.client.resources.Resources;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.mvp.AppActivityMapper;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.mvp.AppPlaceHistoryMapper;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.CompositionPlace;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place.FilterPlace;
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
import edu.stanford.bmir.protege.web.client.rpc.AbstractAsyncHandler;
import edu.stanford.bmir.protege.web.client.rpc.ProjectConfigurationServiceManager;
import edu.stanford.bmir.protege.web.client.rpc.data.EntityData;
import edu.stanford.bmir.protege.web.client.rpc.data.layout.PortletConfiguration;
import edu.stanford.bmir.protege.web.client.rpc.data.layout.ProjectConfiguration;
import edu.stanford.bmir.protege.web.client.rpc.data.layout.TabColumnConfiguration;
import edu.stanford.bmir.protege.web.client.rpc.data.layout.TabConfiguration;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractEntityPortlet;
import edu.stanford.bmir.protege.web.client.ui.util.UIUtil;

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
	public void reload() {}

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
		
		String urlWebProtege = createHashURL();


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
		
		clientFactory.setPortlet(this);
		clientFactory.setURLWebProtege(urlWebProtege);
		

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

	private String createHashURL(){

		String ontology = project.getProjectName().replace(" ", "+");
		String tab = "SwrlEditorTab";
		
		String urlWebProtege = "ontology="
				+ ontology
				+ "&tab="+tab;

		String newURL;

		String href = Window.Location.getHref();

		if (href.contains("?gwt.codesvr=127.0.0.1")){

			href =  href.substring(href.indexOf("?gwt.codesvr=127.0.0.1"));

			if (href.indexOf("#") >= 0)
				href =  href.substring(0, href.indexOf("#"));

			newURL = href+"#visualization:"
					+ urlWebProtege;
		}else
			newURL = "#visualization:" + urlWebProtege;

		String hash = Window.Location.getHash();

		if ((!hash.contains(ontology)) || (!hash.contains(tab))){
			Window.Location.replace(newURL);
			//System.out.println("replace1");
		}else if (hash.trim().contains("#"+CompositionPlace.getNamePlace()+":") ||
				hash.trim().contains("#"+FilterPlace.getNamePlace()+":") ||
				hash.trim().contains("#"+OptionsPlace.getNamePlace()+":")){
			
		}else if (!hash.trim().contains(newURL)){
			Window.Location.replace(newURL);
			//System.out.println("replace2");
		}else{
			Window.Location.replace(newURL);
			//System.out.println("replace3");
		}
		return urlWebProtege;
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

	@Override
	public void onLogin(String userName) {
		ProjectConfigurationServiceManager.getInstance().getProjectConfiguration(project.getProjectName(),
				userName, new GetProjectConfigurationHandler(project));
	}

	@Override
	public void onLogout(String userName) {
		ProjectConfigurationServiceManager.getInstance().getProjectConfiguration(project.getProjectName(),
				userName, new GetProjectConfigurationHandler(project));
	}

	class GetProjectConfigurationHandler extends AbstractAsyncHandler<ProjectConfiguration> {
		private final Project project;

		public GetProjectConfigurationHandler(Project project) {
			this.project = project;
		}

		@Override
		public void handleFailure(Throwable caught) {
			GWT.log("There were errors at loading project configuration for " + project.getProjectName(), caught);
			UIUtil.hideLoadProgessBar();
			com.google.gwt.user.client.Window.alert("Load project configuration for " + project.getProjectName()
					+ " failed. " + " Message: " + caught.getMessage());
		}

		@Override
		public void handleSuccess(ProjectConfiguration config) {
			project.setProjectConfiguration(config);
			for (TabConfiguration tab: config.getTabs()){
				if (tab.getName().equals(SwrlEditorTab.class.getName())){
					//System.out.println(tab.getProperties());
					//System.out.println(tab.getStringProperty(OptionsView.AlgorithmSimilarRulesStr, "putz"));

					for(TabColumnConfiguration colTab : tab.getColumns()){
						for(PortletConfiguration portletConfig : colTab.getPortlets()){
							if(portletConfig.getName().equals(SwrlEditorPortlet.class.getName())){
								//System.out.println(portletConfig.getProperties());
								//System.out.println(portletConfig.getStringProperty(OptionsView.AlgorithmDecisionTreeStr_, "putz"));
							}
						}
					}
				}

			}
		}
	}
	
	/*
	 protected void getProjectConfiguration(Project project) {
		UIUtil.showLoadProgessBar("Loading " + project.getProjectName() + " configuration", "Loading");
		ProjectConfigurationServiceManager.getInstance().getProjectConfiguration(project.getProjectName(),
				GlobalSettings.getGlobalSettings().getUserName(), new GetProjectConfigurationHandler(project));
	 */


}
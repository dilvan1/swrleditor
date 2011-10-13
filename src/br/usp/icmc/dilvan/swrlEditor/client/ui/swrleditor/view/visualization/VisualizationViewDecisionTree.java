package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.visualization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.usp.icmc.dilvan.swrlEditor.client.resources.Resources;
import br.usp.icmc.dilvan.swrlEditor.client.resources.UtilResource;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTree;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTree.ATOM_TYPE;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.WaitingCreateToRun;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.util.Options;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.util.UtilLoading;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.OptionsView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.VisualizationView.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;



import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.uibinder.client.UiHandler;

public class VisualizationViewDecisionTree extends Composite {

	private static VisualizationViewDecisionTreeUiBinder uiBinder = GWT
			.create(VisualizationViewDecisionTreeUiBinder.class);
	@UiField AbsolutePanel panelPathLabels;
	@UiField ListBox listAlgorithm;
	@UiField Button btnRefresh;
	@UiField AbsolutePanel panelPath;
	@UiField AbsolutePanel panelTree;
	@UiField ScrollPanel scroolPath;
	@UiField ScrollPanel scroolTree;
	@UiField VerticalPanel pnlBase;
	@UiField HorizontalPanel pnlOptions;


	private final int NUMBER_LEVELS_DISPLAYED = 3;
	private final int PANELTREE_HEIGHT_INITIAL = 30;
	private final int NODE_HEIGHT = 17;
	//private final int NODE_WIDTH = 500;
	private final int  SPACE_BETWEEN_NODES = 100;

	
	private double widthChar = 0;
	
	private NodeDecisionTree tree;

	private AbsolutePanel panelTreeLabels;
	private GWTCanvas canvasTree;

	private Presenter presenter;

	interface VisualizationViewDecisionTreeUiBinder extends
	UiBinder<Widget, VisualizationViewDecisionTree> {
	}

	public VisualizationViewDecisionTree() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public VisualizationViewDecisionTree(Presenter presenter) {
		this(); 

		this.presenter = presenter;

		panelPath.addStyleName(Resources.INSTANCE.swrleditor().decisionTreeBackground());
		panelPathLabels.addStyleName(Resources.INSTANCE.swrleditor().decisionTreeBackground());
		panelTree.addStyleName(Resources.INSTANCE.swrleditor().decisionTreeBackground());
		scroolTree.addStyleName(Resources.INSTANCE.swrleditor().decisionTreeBackground());

		canvasTree = new GWTCanvas(1000, 1000);
		canvasTree.setSize("100%", "100%");
		canvasTree.setLineWidth(1);
		canvasTree.setStrokeStyle(Color.BLACK);

		panelTreeLabels = new AbsolutePanel();
		panelTreeLabels.setSize("100px", "100%");

		panelTree.add(canvasTree, 0, 0);
		panelTree.add(panelTreeLabels, 0, 0);

	}


	public void setTree(NodeDecisionTree treeNode) {
		this.tree = treeNode;	
		if (tree != null){
			
			
			WaitingCreateToRun waiting = new WaitingCreateToRun(panelPathLabels, 10) {
				@Override
				public void run() {
					showDecisionTree();
				}
			};
			waiting.start();
		}
	}


	private void showDecisionTree() {
		scroolPath.setWidth(Integer.toString(this.getOffsetWidth())+"px");
		scroolTree.setWidth(Integer.toString(this.getOffsetWidth())+"px");
		scroolTree.setHeight(Integer.toString(pnlBase.getOffsetHeight()-(scroolPath.getOffsetHeight()+pnlOptions.getOffsetHeight()))+"px");

		drawTree(tree, 50, NUMBER_LEVELS_DISPLAYED);

		UtilLoading.hide();
	}

	private void drawTree(NodeDecisionTree node, int left, int viewLevel){

		scroolTree.setHeight(Integer.toString(pnlBase.getOffsetHeight()-(scroolPath.getOffsetHeight()+pnlOptions.getOffsetHeight()))+"px");

		panelPathLabels.clear();
		canvasTree.clear();
		panelTreeLabels.clear();

		NodeTreeInt sheetNumber = new NodeTreeInt(0);

		int[] majorWidth = new int[NUMBER_LEVELS_DISPLAYED];
		for (int i = 0; i < NUMBER_LEVELS_DISPLAYED; i++)
			majorWidth[i] = 0;

		calculatesSizeNode(node, sheetNumber, viewLevel, majorWidth);

		int sizePath = drawPath(node)+50;

		setWidthPath(sizePath);
		
		int sumWidth = 0;

		for (int i = 0; i < majorWidth.length; i++)
			sumWidth = (int)(sumWidth + (majorWidth[i]*widthChar)+SPACE_BETWEEN_NODES);

		sumWidth += 200; 


		setSizePanelTree(sumWidth, NODE_HEIGHT * (sheetNumber.getValue()+2)+PANELTREE_HEIGHT_INITIAL);

		int scroolTop = drawNode(-1, -1, node, sheetNumber, left, PANELTREE_HEIGHT_INITIAL, viewLevel, majorWidth);

		scroolTree.setVerticalScrollPosition(scroolTop-(scroolTree.getOffsetHeight()/2));
		scroolTree.setHorizontalScrollPosition(0);

	}

	private int calculatesSizeNode(NodeDecisionTree node, NodeTreeInt sheetNumber, int viewLevel, int[] majorWidth){

		if (viewLevel == 0){
			sheetNumber.setValue(1);
			return 1;
		}
		int sum = 0;

		for (int i = 0; i < node.getChildren().size(); i++){
			NodeTreeInt newSheetNumber = sheetNumber.addChildNodes(new NodeTreeInt(0));

			sum = sum + calculatesSizeNode(node.getChildren().get(i), newSheetNumber, viewLevel-1, majorWidth);

			if (majorWidth[NUMBER_LEVELS_DISPLAYED-viewLevel] < node.getChildren().get(i).getValue().length())
				majorWidth[NUMBER_LEVELS_DISPLAYED-viewLevel] = node.getChildren().get(i).getValue().length();

		}
		if (sum == 0){
			sheetNumber.setValue(1);
			return 1;
		}
		sheetNumber.setValue(sum);
		return sum;
	}

	private int drawPath(NodeDecisionTree node){

		if (node.getParentNode() == null){
			DefaultNodeInRulesTreeLabel label = new DefaultNodeInRulesTreeLabel(node);
			addEventsLabel(label);
			panelPathLabels.add(label, 10, 0);
			
			widthChar = (label.getOffsetWidth()*1.0)/(node.getValue().length()*1.0);
			
			return label.getAbsoluteLeft()+label.getOffsetWidth();
		}

		int pos = drawPath(node.getParentNode());
		
		Label lblAnd = new Label("^");
		
		panelPathLabels.add(lblAnd, pos-10, 0);

		DefaultNodeInRulesTreeLabel label = new DefaultNodeInRulesTreeLabel(node);
		addEventsLabel(label);
		panelPathLabels.add(label, lblAnd.getAbsoluteLeft()+lblAnd.getOffsetWidth()-10, 0);
		return label.getAbsoluteLeft()+label.getOffsetWidth();
	}


	private int drawNode(int leftParent, int topParent, NodeDecisionTree node, NodeTreeInt sheetNumber,
			int left, int beginTop, int viewLevel, int[] majorWidthLevel){
		if (node.getChildren().size() == 0)
			return 0;

		if (viewLevel == 0){

			if ((node.getChildren().size() == 1) && node.getChildren().get(0).getAtomType() == ATOM_TYPE.CONSEQUENT){		

				DefaultNodeInRulesTreeLabel label = new DefaultNodeInRulesTreeLabel(node.getChildren().get(0));
				addEventsLabel(label);
				panelTreeLabels.add(label, left, topParent);
				canvasTree.beginPath();
				canvasTree.moveTo(leftParent+(widthChar*node.getChildren().get(0).getValue().length()+5), topParent+8);
				canvasTree.lineTo(left-5, topParent+8);
				canvasTree.closePath();
				canvasTree.stroke(); 


			}else{

				DefaultNodeInRulesTreeLabel labelAux = new DefaultNodeInRulesTreeLabel("...", node);
				labelAux.addStyleName(Resources.INSTANCE.swrleditor().whiteBackground());
				labelAux.addStyleName(Resources.INSTANCE.swrleditor().swrlRule());

				labelAux.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						drawTree(((DefaultNodeInRulesTreeLabel)event.getSource()).getNode(), 50, NUMBER_LEVELS_DISPLAYED);
					}
				});

				panelTreeLabels.add(labelAux, left, topParent);

				canvasTree.beginPath();
				canvasTree.moveTo(leftParent+(widthChar*labelAux.getText().length()+5), topParent+8);
				canvasTree.lineTo(left-5, topParent+8);
				canvasTree.closePath();
				canvasTree.stroke();

			}
			return 0;
		}

		int top = 0, sumChilds = 0, topForChild = 0;

		for (int i = 0; i < node.getChildren().size(); i++){

			top = beginTop+(NODE_HEIGHT*(sumChilds))+(NODE_HEIGHT*(sheetNumber.getChildNodes(i).getValue())/2);
			topForChild = beginTop+(NODE_HEIGHT*(sumChilds));

			sumChilds = sumChilds + sheetNumber.getChildNodes(i).getValue();

			if (leftParent > -1){
				canvasTree.beginPath();
				canvasTree.moveTo(leftParent+(widthChar*node.getValue().length()+5), topParent+8);
				canvasTree.lineTo(left-5, top+8);
				canvasTree.closePath();
				canvasTree.stroke();   
			}

			DefaultNodeInRulesTreeLabel label = new DefaultNodeInRulesTreeLabel(node.getChildren().get(i));
			addEventsLabel(label);
			panelTreeLabels.add(label, left, top);

			drawNode(left, top, node.getChildren().get(i), sheetNumber.getChildNodes(i), 
					(int)(left+(majorWidthLevel[NUMBER_LEVELS_DISPLAYED-viewLevel]*widthChar))+SPACE_BETWEEN_NODES, 
					topForChild, viewLevel-1, majorWidthLevel);

		}
		return beginTop+(NODE_HEIGHT*sumChilds/2);
	}	


	private void setWidthPath(int width){
		if (width < scroolPath.getOffsetWidth()){
			panelPath.setWidth("100%");
			panelPathLabels.setWidth("100%");			
		}else{
			panelPath.setWidth(Integer.toString(width)+"px");
			panelPathLabels.setWidth(Integer.toString(width)+"px");		
		}

	}

	public void setSizePanelTree(int widthPanelTree, int heightPanelTree) {
		canvasTree.resize(widthPanelTree, heightPanelTree);
		panelTree.setSize(Integer.toString(widthPanelTree)+"px", Integer.toString(heightPanelTree)+"px");
		panelTreeLabels.setSize(Integer.toString(widthPanelTree)+"px", Integer.toString(heightPanelTree)+"px");			
	}

	@SuppressWarnings("deprecation")
	private void addEventsLabel(DefaultNodeInRulesTreeLabel label){
		if (label == null)
			return;

		label.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				drawTree(((DefaultNodeInRulesTreeLabel)event.getSource()).getNode(), 50, NUMBER_LEVELS_DISPLAYED);
			}
		});
		if (!label.getNode().getToolTip().trim().equals(""))
			label.addMouseListener(
					new TooltipListener(
							label.getNode().getToolTip(), 5000, Resources.INSTANCE.swrleditor().hint()));
	}

	public String getAlgorithmName(){
		String result = "Default";
		if (listAlgorithm.getItemCount() != 0)
			result = listAlgorithm.getItemText(listAlgorithm.getSelectedIndex());

		return result;
	}
	@UiHandler("btnRefresh")
	void onBtnRefreshClick(ClickEvent event) {
		loadDecisionTree();
	}
	
	public void loadDecisionTree(){
		UtilLoading.showLoadDecisionTree();
		presenter.getDecisionTree(listAlgorithm.getItemText(listAlgorithm.getSelectedIndex()));
	}

	public void setListAlgorithm(List<String> list, Map<String, Object> config){
		listAlgorithm.clear();
		
		String defaultAlg = Options.getStringOption(config, OptionsView.DefaultAlgorithmDecisionTreeStr, "");
		int count = 0;
		
		for (String algorithm: list){
			if (Options.getBooleanOption(config, Options.removeCharInvalidForNameOptions(OptionsView.AlgorithmDecisionTreeStr_, algorithm), true)){
				listAlgorithm.addItem(algorithm);
				
				if (defaultAlg.equals(algorithm))
					listAlgorithm.setSelectedIndex(count);
				
				count++;
			}
		}
			
		if (defaultAlg.equals(""))
			listAlgorithm.setSelectedIndex(0);
	}
	
	private class NodeTreeInt {

		int value;
		
		private List<NodeTreeInt> childNodes = new ArrayList<NodeTreeInt>();
		
		public NodeTreeInt(int value) {
			super();
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		
		public NodeTreeInt addChildNodes(NodeTreeInt node){
			if (this.childNodes.add(node))
				return this.childNodes.get(this.childNodes.size()-1);
			else
				return null;	
		}
		public NodeTreeInt getChildNodes(int index){
			return this.childNodes.get(index);
		}
	}
	
	
	public class DefaultNodeInRulesTreeLabel extends Label{
		private NodeDecisionTree node;

		private DefaultNodeInRulesTreeLabel(String label, NodeDecisionTree node) {
			super(label);
			this.node = node;
			addEvent();
			
			this.addStyleName(Resources.INSTANCE.swrleditor().swrlRule());
			if ((this.node.getAtomType() != ATOM_TYPE.ROOT) && (this.node.getAtomType() != ATOM_TYPE.CONSEQUENT))
				this.addStyleName(UtilResource.getCssTypeNodeAtom(this.node.getAtomType()));
			
			this.addStyleName(Resources.INSTANCE.swrleditor().whiteBackground());
		}
		
		public DefaultNodeInRulesTreeLabel(NodeDecisionTree node) {
			this(node.getValue(), node);
		}
		
		private void addEvent(){
			this.addMouseMoveHandler(new MouseMoveHandler() {

				public void onMouseMove(MouseMoveEvent event) {
					((DefaultNodeInRulesTreeLabel)event.getSource()).addStyleName(Resources.INSTANCE.swrleditor().pointerCursor());
					
				}
			});

			this.addMouseOutHandler(new MouseOutHandler() {

				public void onMouseOut(MouseOutEvent event) {
					((DefaultNodeInRulesTreeLabel)event.getSource()).removeStyleName("pointer-cursor");


				}
			});
		}

		public NodeDecisionTree getNode() {
			return node;
		}
	}
}

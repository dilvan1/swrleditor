package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.visualization;

import java.util.List;
import java.util.Map;

import br.usp.icmc.dilvan.swrlEditor.client.resources.Resources;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTree;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.decisiontree.NodeDecisionTree.ATOM_TYPE;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.WaitingCreateToRun;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.util.Options;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.util.UtilLoading;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.OptionsView;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.VisualizationView.Presenter;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.visualization.decisiontree.NodeLabelListener;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.visualization.decisiontree.DefaultNodeInRulesTreeLabel;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.visualization.decisiontree.NodeTreeInt;
import br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.visualization.decisiontree.TooltipListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.uibinder.client.UiHandler;

public class VisualizationViewDecisionTree extends Composite {

	private static VisualizationViewDecisionTreeUiBinder uiBinder = GWT
			.create(VisualizationViewDecisionTreeUiBinder.class);
	@UiField
	AbsolutePanel panelPathLabels;
	@UiField
	ListBox listAlgorithm;
	@UiField
	Button btnRefresh;
	@UiField
	AbsolutePanel panelPath;
	@UiField
	AbsolutePanel panelTree;
	@UiField
	ScrollPanel scroolPath;
	@UiField
	ScrollPanel scroolTree;
	@UiField
	VerticalPanel pnlBase;
	@UiField
	HorizontalPanel pnlOptions;

	final private PopupPanel rightClickMenuInsert = new PopupPanel(true);
	final private PopupPanel rightClickMenuEdit = new PopupPanel(true);

	private final int NUMBER_LEVELS_DISPLAYED = 3;
	private final int PANELTREE_HEIGHT_INITIAL = 30;
	private final int NODE_HEIGHT = 17;
	// private final int NODE_WIDTH = 500;
	private final int SPACE_BETWEEN_NODES = 100;

	private double widthChar = 0;

	private NodeDecisionTree tree;

	private AbsolutePanel panelTreeLabels;
	private GWTCanvas canvasTree;

	private Presenter presenter;

	private final String SUSPENSION_POINTS = "...";

	interface VisualizationViewDecisionTreeUiBinder extends
			UiBinder<Widget, VisualizationViewDecisionTree> {
	}

	public VisualizationViewDecisionTree() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public VisualizationViewDecisionTree(Presenter presenter) {
		this();

		this.presenter = presenter;

		panelPath.addStyleName(Resources.INSTANCE.swrleditor()
				.decisionTreeBackground());
		panelPathLabels.addStyleName(Resources.INSTANCE.swrleditor()
				.decisionTreeBackground());
		panelTree.addStyleName(Resources.INSTANCE.swrleditor()
				.decisionTreeBackground());
		scroolTree.addStyleName(Resources.INSTANCE.swrleditor()
				.decisionTreeBackground());

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
		if (tree != null) {

			WaitingCreateToRun waiting = new WaitingCreateToRun(
					panelPathLabels, 10) {
				@Override
				public void run() {
					showDecisionTree();
				}
			};
			waiting.start();
		}
	}

	private void showDecisionTree() {
		drawTree(tree, 50, NUMBER_LEVELS_DISPLAYED);

		UtilLoading.hide();
	}

	private void drawTree(NodeDecisionTree node, int left, int viewLevel) {
		scroolPath.setWidth(Integer.toString(this.getOffsetWidth()) + "px");
		scroolTree.setWidth(Integer.toString(this.getOffsetWidth()) + "px");
		scroolTree
				.setHeight(Integer.toString(pnlBase.getOffsetHeight()
						- (scroolPath.getOffsetHeight() + pnlOptions
								.getOffsetHeight()))
						+ "px");

		scroolTree
				.setHeight(Integer.toString(pnlBase.getOffsetHeight()
						- (scroolPath.getOffsetHeight() + pnlOptions
								.getOffsetHeight()))
						+ "px");

		panelPathLabels.clear();
		canvasTree.clear();
		panelTreeLabels.clear();

		NodeTreeInt sheetNumber = new NodeTreeInt(0);

		int[] majorWidth = new int[NUMBER_LEVELS_DISPLAYED];
		for (int i = 0; i < NUMBER_LEVELS_DISPLAYED; i++)
			majorWidth[i] = 0;

		calculatesSizeNode(node, sheetNumber, viewLevel, majorWidth);

		int sizePath = drawPath(node) + 50;

		setWidthPath(sizePath);

		int sumWidth = 0;

		for (int i = 0; i < majorWidth.length; i++)
			sumWidth = (int) (sumWidth + (majorWidth[i] * widthChar) + SPACE_BETWEEN_NODES);

		sumWidth += 200;

		setSizePanelTree(sumWidth, NODE_HEIGHT * (sheetNumber.getValue() + 2)
				+ PANELTREE_HEIGHT_INITIAL);

		int scroolTop = drawNode(-1, -1, node, sheetNumber, left,
				PANELTREE_HEIGHT_INITIAL, viewLevel, majorWidth);

		scroolTree.setVerticalScrollPosition(scroolTop
				- (scroolTree.getOffsetHeight() / 2));
		scroolTree.setHorizontalScrollPosition(0);

	}

	private int calculatesSizeNode(NodeDecisionTree node,
			NodeTreeInt sheetNumber, int viewLevel, int[] majorWidth) {

		if (viewLevel == 0) {
			sheetNumber.setValue(1);
			return 1;
		}
		int sum = 0;

		for (int i = 0; i < node.getChildren().size(); i++) {
			NodeTreeInt newSheetNumber = sheetNumber
					.addChildNodes(new NodeTreeInt(0));

			sum = sum
					+ calculatesSizeNode(node.getChildren().get(i),
							newSheetNumber, viewLevel - 1, majorWidth);

			if (majorWidth[NUMBER_LEVELS_DISPLAYED - viewLevel] < node
					.getChildren().get(i).getValue().length())
				majorWidth[NUMBER_LEVELS_DISPLAYED - viewLevel] = node
						.getChildren().get(i).getValue().length();

		}
		if (sum == 0) {
			sheetNumber.setValue(1);
			return 1;
		}
		sheetNumber.setValue(sum);
		return sum;
	}

	private int drawPath(NodeDecisionTree node) {

		if (node.getParentNode() == null) {
			DefaultNodeInRulesTreeLabel label = new DefaultNodeInRulesTreeLabel(
					node);
			addEventsLabel(label);
			panelPathLabels.add(label, 10, 0);

			widthChar = (label.getOffsetWidth() * 1.0)
					/ (node.getValue().length() * 1.0);

			return label.getAbsoluteLeft() + label.getOffsetWidth();
		}

		int pos = drawPath(node.getParentNode());

		Label lblAnd = new Label("^");

		panelPathLabels.add(lblAnd, pos - 10, 0);

		DefaultNodeInRulesTreeLabel label = new DefaultNodeInRulesTreeLabel(
				node);
		addEventsLabel(label);
		panelPathLabels.add(label,
				lblAnd.getAbsoluteLeft() + lblAnd.getOffsetWidth() - 10, 0);
		return label.getAbsoluteLeft() + label.getOffsetWidth();
	}

	private int drawNode(int leftParent, int topParent, NodeDecisionTree node,
			NodeTreeInt sheetNumber, int left, int beginTop, int viewLevel,
			int[] majorWidthLevel) {
		if (node.getChildren().size() == 0)
			return 0;

		if (viewLevel == 0) {

			if ((node.getChildren().size() == 1)
					&& node.getChildren().get(0).getAtomType() == ATOM_TYPE.CONSEQUENT) {

				DefaultNodeInRulesTreeLabel label = new DefaultNodeInRulesTreeLabel(
						node.getChildren().get(0));
				addEventsLabel(label);
				panelTreeLabels.add(label, left, topParent);
				canvasTree.beginPath();
				canvasTree.moveTo(
						leftParent
								+ (widthChar
										* node.getChildren().get(0).getValue()
												.length() + 5), topParent + 8);
				canvasTree.lineTo(left - 5, topParent + 8);
				canvasTree.closePath();
				canvasTree.stroke();

			} else {

				DefaultNodeInRulesTreeLabel labelAux = new DefaultNodeInRulesTreeLabel(
						SUSPENSION_POINTS, node);
				labelAux.addStyleName(Resources.INSTANCE.swrleditor()
						.whiteBackground());
				labelAux.addStyleName(Resources.INSTANCE.swrleditor()
						.swrlRule());

				addEventsLabel(labelAux);

				panelTreeLabels.add(labelAux, left, topParent);

				canvasTree.beginPath();
				canvasTree.moveTo(leftParent
						+ (widthChar * labelAux.getText().length() + 5),
						topParent + 8);
				canvasTree.lineTo(left - 5, topParent + 8);
				canvasTree.closePath();
				canvasTree.stroke();

			}
			return 0;
		}

		int top = 0, sumChilds = 0, topForChild = 0;

		for (int i = 0; i < node.getChildren().size(); i++) {

			top = beginTop
					+ (NODE_HEIGHT * (sumChilds))
					+ (NODE_HEIGHT * (sheetNumber.getChildNodes(i).getValue()) / 2);
			topForChild = beginTop + (NODE_HEIGHT * (sumChilds));

			sumChilds = sumChilds + sheetNumber.getChildNodes(i).getValue();

			if (leftParent > -1) {
				canvasTree.beginPath();
				canvasTree.moveTo(leftParent
						+ (widthChar * node.getValue().length() + 5),
						topParent + 8);
				canvasTree.lineTo(left - 5, top + 8);
				canvasTree.closePath();
				canvasTree.stroke();
			}

			DefaultNodeInRulesTreeLabel label = new DefaultNodeInRulesTreeLabel(
					node.getChildren().get(i));
			addEventsLabel(label);
			panelTreeLabels.add(label, left, top);

			drawNode(left, top, node.getChildren().get(i),
					sheetNumber.getChildNodes(i),
					(int) (left + (majorWidthLevel[NUMBER_LEVELS_DISPLAYED
							- viewLevel] * widthChar))
							+ SPACE_BETWEEN_NODES, topForChild, viewLevel - 1,
					majorWidthLevel);

		}
		return beginTop + (NODE_HEIGHT * sumChilds / 2);
	}

	private void setWidthPath(int width) {
		if (width < scroolPath.getOffsetWidth()) {
			panelPath.setWidth("100%");
			panelPathLabels.setWidth("100%");
		} else {
			panelPath.setWidth(Integer.toString(width) + "px");
			panelPathLabels.setWidth(Integer.toString(width) + "px");
		}

	}

	public void setSizePanelTree(int widthPanelTree, int heightPanelTree) {
		canvasTree.resize(widthPanelTree, heightPanelTree);
		panelTree.setSize(Integer.toString(widthPanelTree) + "px",
				Integer.toString(heightPanelTree) + "px");
		panelTreeLabels.setSize(Integer.toString(widthPanelTree) + "px",
				Integer.toString(heightPanelTree) + "px");
	}

	@SuppressWarnings("deprecation")
	private void addEventsLabel(DefaultNodeInRulesTreeLabel label) {
		if (label == null)
			return;

		if (!label.getNode().getToolTip().trim().equals(""))
			label.addMouseListener(new TooltipListener(label.getNode()
					.getToolTip(), 5000, Resources.INSTANCE.swrleditor().hint()));

		if (label.getText().equals(NodeDecisionTree.ROOT_VALUE)) {
			label.addClickListener(new NodeLabelListener() {

				@Override
				public void onClick(Widget sender, Event event) {
					clickNodeTree((DefaultNodeInRulesTreeLabel) sender);
				}

				@Override
				public void onRightClick(Widget sender, Event event) {
				}

				@Override
				public void onMouseMove(Widget sender, Event event) {
					moveMouseNodeTree((DefaultNodeInRulesTreeLabel) sender);
				}

				@Override
				public void onMouseOut(Widget sender, Event event) {
					exitMouseNodeTree((DefaultNodeInRulesTreeLabel) sender);
				}

			});

		} else if (label.getText().equals(SUSPENSION_POINTS)) {
			label.addClickListener(new NodeLabelListener() {

				@Override
				public void onClick(Widget sender, Event event) {
					clickNodeTree((DefaultNodeInRulesTreeLabel) sender);
				}

				@Override
				public void onRightClick(Widget sender, Event event) {
					rigthClickNodeTree((DefaultNodeInRulesTreeLabel) sender, event);
				}

				@Override
				public void onMouseMove(Widget sender, Event event) {
					moveMouseNodeTree((DefaultNodeInRulesTreeLabel) sender);
				}

				@Override
				public void onMouseOut(Widget sender, Event event) {
					exitMouseNodeTree((DefaultNodeInRulesTreeLabel) sender);
				}

			});
		} else if (label.getText().equals(NodeDecisionTree.CONSEQUENT_VALUE)) {

			label.addClickListener(new NodeLabelListener() {

				@Override
				public void onClick(Widget sender, Event event) {
				}

				@Override
				public void onRightClick(Widget sender, Event event) {
					rigthClickNodeTree((DefaultNodeInRulesTreeLabel) sender, event);
				}

				@Override
				public void onMouseMove(Widget sender, Event event) {
					moveMouseNodeTree((DefaultNodeInRulesTreeLabel) sender);
				}

				@Override
				public void onMouseOut(Widget sender, Event event) {
					exitMouseNodeTree((DefaultNodeInRulesTreeLabel) sender);
				}

			});
		} else {
			label.addClickListener(new NodeLabelListener() {

				@Override
				public void onClick(Widget sender, Event event) {
					clickNodeTree((DefaultNodeInRulesTreeLabel) sender);
				}

				@Override
				public void onRightClick(Widget sender, Event event) {
					rigthClickNodeTree((DefaultNodeInRulesTreeLabel) sender, event);
				}

				@Override
				public void onMouseMove(Widget sender, Event event) {
					moveMouseNodeTree((DefaultNodeInRulesTreeLabel) sender);
				}

				@Override
				public void onMouseOut(Widget sender, Event event) {
					exitMouseNodeTree((DefaultNodeInRulesTreeLabel) sender);
				}

			});
		}

	}

	private void clickNodeTree(DefaultNodeInRulesTreeLabel label) {
		drawTree(label.getNode(), 50, NUMBER_LEVELS_DISPLAYED);
	}

	private void rigthClickNodeTree(final DefaultNodeInRulesTreeLabel label, Event event) {
		int x = DOM.eventGetClientX(event);
		int y = DOM.eventGetClientY(event);
		if (label.getText().equals(NodeDecisionTree.CONSEQUENT_VALUE)) {
			Command editRule = new Command() {
				@Override
				public void execute() {
					rightClickMenuEdit.hide();
					
					String antecedent = "";
					String consequent = label.getNode().getToolTip();
					NodeDecisionTree node = label.getNode().getParentNode();
					while (node.getAtomType() !=  ATOM_TYPE.ROOT){
						
						if (antecedent.isEmpty())
							antecedent = node.getValue();
						else
							antecedent = antecedent + " ^ " + node.getValue();
						
						node = node.getParentNode();
					}
					
					presenter.goToEditRule(antecedent, consequent);
				}
			};
			
			MenuBar popupMenuBarEdit = new MenuBar(true);
			MenuItem insertEdit = new MenuItem("Edit Rule", true, editRule);
			insertEdit.setStyleName("popup");
			popupMenuBarEdit.addItem(insertEdit);
			popupMenuBarEdit.setVisible(true);
			
			rightClickMenuEdit.clear();
			rightClickMenuEdit.add(popupMenuBarEdit);
			
			rightClickMenuEdit.setPopupPosition(x, y);
			rightClickMenuEdit.show();
		} else {

			Command insertNewRule = new Command() {
				@Override
				public void execute() {
					rightClickMenuInsert.hide();

					String antecedent = "";
					NodeDecisionTree node = label.getNode();
					while (node.getAtomType() !=  ATOM_TYPE.ROOT){
						
						if (antecedent.isEmpty())
							antecedent = node.getValue();
						else
							antecedent = antecedent + " ^ " + node.getValue();
						
						node = node.getParentNode();
					}
					
					presenter.goToNewRule(antecedent);
				}
			};
			
			MenuBar popupMenuBarInsert = new MenuBar(true);
			MenuItem insertItem = new MenuItem("Insert in New Rule", true, insertNewRule);
			insertItem.setStyleName("popup");
			popupMenuBarInsert.addItem(insertItem);
			popupMenuBarInsert.setVisible(true);
			rightClickMenuInsert.clear();
			rightClickMenuInsert.add(popupMenuBarInsert);

			
			rightClickMenuInsert.setPopupPosition(x, y);
			rightClickMenuInsert.show();
		}

	}

	private void moveMouseNodeTree(DefaultNodeInRulesTreeLabel label) {
		label.addStyleName(Resources.INSTANCE.swrleditor().pointerCursor());
	}

	private void exitMouseNodeTree(DefaultNodeInRulesTreeLabel label) {
		label.removeStyleName(Resources.INSTANCE.swrleditor().pointerCursor());
	}

	public String getAlgorithmName() {
		String result = "Default";
		if (listAlgorithm.getItemCount() != 0)
			result = listAlgorithm
					.getItemText(listAlgorithm.getSelectedIndex());

		return result;
	}

	@UiHandler("btnRefresh")
	void onBtnRefreshClick(ClickEvent event) {
		loadDecisionTree();
	}

	public void loadDecisionTree() {
		UtilLoading.showLoadDecisionTree();
		presenter.getDecisionTree(getAlgorithmName());
	}

	public void setListAlgorithm(List<String> list, Map<String, Object> config) {
		listAlgorithm.clear();
		String defaultAlg = Options.getStringOption(config,
				OptionsView.DefaultAlgorithmDecisionTreeStr, "");
		
		if (defaultAlg == null)
			defaultAlg = "";
			
		int count = 0;

		for (String algorithm : list) {
			if (Options.getBooleanOption(config, Options
					.removeCharInvalidForNameOptions(
							OptionsView.AlgorithmDecisionTreeStr_, algorithm),
					true)) {
				listAlgorithm.addItem(algorithm);

				if (defaultAlg.equals(algorithm))
					listAlgorithm.setSelectedIndex(count);

				count++;
			}
		}

		if (defaultAlg.equals(""))
			listAlgorithm.setSelectedIndex(0);
	}

}

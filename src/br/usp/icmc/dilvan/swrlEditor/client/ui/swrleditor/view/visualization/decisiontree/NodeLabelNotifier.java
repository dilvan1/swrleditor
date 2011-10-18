package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.visualization.decisiontree;

public interface NodeLabelNotifier {
	public void addClickListener(NodeLabelListener listener);
	public void removeClickListener(NodeLabelListener listener);
}

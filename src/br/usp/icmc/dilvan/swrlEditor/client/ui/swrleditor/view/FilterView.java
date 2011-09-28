package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view;

import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.Filter;
import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom.TYPE_ATOM;

import com.google.gwt.user.client.ui.IsWidget;


/**
 *
 * @author Jo‹o Paulo Orlando
 */
public interface FilterView extends IsWidget
{
	
	void setPresenter(Presenter presenter);
	void setFilters(List<Filter> filters);
	void setBuiltins(List<String> builtins);
	void populateFields(TYPE_ATOM typeAtom, String value);
	
	public interface Presenter
	{
		void goToVisualization();
		
		boolean addFilter(String filterType, String rulePart, String qryString);
		void removeFilter(Filter filter);
		
	}

	

	
}
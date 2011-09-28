package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view;

import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.client.rpc.swrleditor.rule.Atom.TYPE_ATOM;

import com.google.gwt.user.client.ui.IsWidget;

import edu.stanford.bmir.protege.web.client.model.Project;

/**
 *
 * @author Jo‹o Paulo Orlando
 */
public interface OntologyView extends IsWidget
{
	void setPresenter(Presenter presenter);
	
	void setProjectProtege(Project project);
	
	void setBuiltins(List<String> builtins);
	
	public interface Presenter
	{
		void setSelectedPredicate(TYPE_ATOM typeAtom, String predicate);
		void getBuiltins();
	}
}
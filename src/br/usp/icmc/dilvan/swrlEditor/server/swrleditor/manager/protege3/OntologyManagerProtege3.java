package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.manager.protege3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.manager.OntologyManager;
import br.usp.icmc.dilvan.swrlEditor.server.swrleditor.manager.SWRLManager;

import edu.stanford.bmir.protege.web.client.model.event.OntologyEvent;
import edu.stanford.bmir.protege.web.server.Protege3ProjectManager;
import edu.stanford.bmir.protege.web.server.ServerProject;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;


public class OntologyManagerProtege3 implements OntologyManager{

	private JenaOWLModel owlModel;
	private SWRLManager swrlManager;
	private ServerProject<Project> serverProject;
	private String projectName;

	public OntologyManagerProtege3(String projectName) {
		serverProject = Protege3ProjectManager.getProjectManager().getServerProject(projectName, false);
		owlModel = (JenaOWLModel) this.serverProject.getProject().getKnowledgeBase();
		swrlManager = new SWRLManagerProtege3(this);
		this.projectName = projectName;
		
	}
	
/*	@Override
	public Long getVersion() {
        if (serverProject == null) {
            return null;
        }
        return Long.valueOf(serverProject.getServerVersion());
	}*/

	@Override
	public SWRLManager getSWRLManager() {
		return swrlManager;
	}

	public JenaOWLModel getOwlModel(){
		return owlModel;
	}

	//TODO rever esses Exception
	@Override
	public boolean hasOWLClass(String resource) {
		try {
			return (owlModel.getOWLNamedClass(resource) != null);
		} catch (Exception e) {
			return false;
		}
	}
	@Override
	public boolean hasOWLDatatypePropertie(String resource) {
		try {
			return (owlModel.getOWLDatatypeProperty(resource) != null);
		} catch (Exception e) {
			return false;
		}
	}
	@Override
	public boolean hasOWLDatatype(String resource) {
		try{
			return (owlModel.getOWLIndividual(resource) != null);
		} catch (Exception e) {
			return false;
		}
	}
	@Override
	public boolean hasOWLObjectPropertie(String resource) {
		try{
			return (owlModel.getOWLObjectProperty(resource) != null);
		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getIDsForLabel(String label, boolean returnFirst){

		List<String> result = new ArrayList<String>();
		
		for (RDFSClass cls : (Collection<RDFSClass>) owlModel.getRDFSClasses()){
			for (String lbl : (Collection<String>)cls.getLabels()){
				if (lbl.equals(label))
					if (!result.contains(cls.getBrowserText())){
						result.add(cls.getBrowserText());
						if (returnFirst)
							return result;
					}
			}
		}

		for (RDFProperty property: (Collection<RDFProperty>) owlModel.getRDFProperties()){
			
			for (Object obj : (Collection<Object>)property.getLabels()){
				String lbl = "";
				if (obj instanceof String){
					lbl = (String) obj;
				}else if (obj instanceof DefaultRDFSLiteral){
					DefaultRDFSLiteral rdfsLiteral = (DefaultRDFSLiteral) obj;
					lbl = rdfsLiteral.getBrowserText();
				}
				
				if (!lbl.isEmpty()){
					if (lbl.equals(label))
						if (!result.contains(property.getBrowserText())){
							result.add(property.getBrowserText());
							if (returnFirst)
								return result;
						}
				}
			}
		}

		for (RDFSDatatype dataType: owlModel.getRDFSDatatypes()){
			for (String lbl : (Collection<String>)dataType.getLabels()){
				if (lbl.equals(label))
					if (!result.contains(dataType.getBrowserText())){
						result.add(dataType.getBrowserText());
						if (returnFirst)
							return result;
					}
			}
			
		}

		return result;
	}
	
	
    public List<OntologyEvent> getEvents(long fromVersion) {
        if (serverProject == null) {
            throw new RuntimeException("Could not get ontology: " + projectName + " from server.");
        }
        return serverProject.isLoaded() ? serverProject.getEvents(fromVersion) : null;
    }
}
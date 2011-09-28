package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.manager;

import java.util.List;

public interface OntologyManager {
	
	//public Long getVersion();
	
	public SWRLManager getSWRLManager();
	
	public boolean hasOWLClass(String resource);

	public boolean hasOWLDatatypePropertie(String resource);

	public boolean hasOWLDatatype(String resource);

	public boolean hasOWLObjectPropertie(String resource);

	public List<String> getIDsForLabel(String label, boolean returnFirst);
	
}

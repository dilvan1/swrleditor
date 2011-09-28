package br.usp.icmc.dilvan.swrlEditor.server.swrleditor.manager.protege3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UtilProtege3 {
	
	public static List<String> parseCollectionArrayList(Collection<String> collection){
		return (collection != null) ? new ArrayList<String>(collection) : new ArrayList<String>();
	}

}

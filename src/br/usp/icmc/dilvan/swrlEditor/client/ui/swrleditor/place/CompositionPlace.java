package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;



public class CompositionPlace extends DefaultPlace{
	
	public enum COMPOSITION_MODE {NEW, EDIT, DUPLICATE};
	
	public CompositionPlace(String token)
	{
		super(token);
	}
	
	public COMPOSITION_MODE getModeComposition(){
		String mode = getParameter(ID_MODE);
		if (mode.equals(COMPOSITION_MODE.NEW.name()))
			return COMPOSITION_MODE.NEW;
		else if (mode.equals(COMPOSITION_MODE.EDIT.name()))
			return COMPOSITION_MODE.EDIT;
		else if (mode.equals(COMPOSITION_MODE.DUPLICATE.name()))
			return COMPOSITION_MODE.DUPLICATE;
		else
			return null;
	}
	
	public String getRuleName() {
		return getParameter(ID_RULE_NAME);
	}


	public static String getNamePlace() {
		return "composition";
	}
	
	@Prefix (value="composition")
	public static class Tokenizer implements PlaceTokenizer<CompositionPlace>
	{
		@Override
		public String getToken(CompositionPlace place)
		{
			return place.getToken();
		}
		@Override
		public CompositionPlace getPlace(String token)
		{
			return new CompositionPlace(token);
		}

	}

}

package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.place;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class InfoPlace extends DefaultPlace{
	
	public InfoPlace(String token)
	{
		super(token);
	}
	
	public static String getNamePlace() {
		return "informations";
	}
	
	@Prefix (value="informations")
	public static class Tokenizer implements PlaceTokenizer<InfoPlace>
	{

		@Override
		public String getToken(InfoPlace place)
		{
			return place.getToken();
		}

		@Override
		public InfoPlace getPlace(String token)
		{
			return new InfoPlace(token);
		}

	}
}

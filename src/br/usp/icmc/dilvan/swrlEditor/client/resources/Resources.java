package br.usp.icmc.dilvan.swrlEditor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {

	public static final Resources INSTANCE = GWT.create(Resources.class);
	
	ImageResource builtin();

	ImageResource class_();

	@Source("class-antecedent.png")
	ImageResource classAntecedent();

	ImageResource edit();

	ImageResource error();

	ImageResource fechar();

	ImageResource filter();

	ImageResource individuo();

	ImageResource loading();

	ImageResource ok();

	@Source("OWLDatatypeProperty.gif")
	ImageResource oWLDatatypeProperty();

	@Source("OWLObjectProperty.gif")
	ImageResource oWLObjectProperty();

	@Source("same-different.png")
	ImageResource sameDifferent();

	Swrleditor swrleditor();

	ImageResource warning();

}

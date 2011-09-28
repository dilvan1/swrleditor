package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.view.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;

public class lixo extends Composite{

	private static lixoUiBinder uiBinder = GWT.create(lixoUiBinder.class);
	@UiField CheckBox checkBox;

	interface lixoUiBinder extends UiBinder<Widget, lixo> {
	}

	public lixo() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	@UiHandler("checkBox")
	void onCheckBoxAttachOrDetach(AttachEvent event) {
	}
	@UiHandler("checkBox")
	void onCheckBoxBlur(BlurEvent event) {
	}
	@UiHandler("checkBox")
	void onCheckBoxClick(ClickEvent event) {
	}
}

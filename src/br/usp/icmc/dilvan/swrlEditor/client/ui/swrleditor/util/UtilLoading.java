package br.usp.icmc.dilvan.swrlEditor.client.ui.swrleditor.util;

import edu.stanford.bmir.protege.web.client.ui.util.UIUtil;

public class UtilLoading {

	public static void showLoadSWRLEditor(){
		show("Loading SWRLEditor", "Loading");
	}
	
	public static void showLoadRules(){
		show("Loading Rules", "Loading");
	}

	public static void showLoadGroups(){
		show("Loading Groups", "Loading");
	}

	public static void showLoadDecisionTree(){
		show("Loading Decision Tree", "Loading");
	}

	public static void show(String message, String title){
		UIUtil.showLoadProgessBar(message, title);
	}

	public static void hide(){
		UIUtil.hideLoadProgessBar();
	}
	
}

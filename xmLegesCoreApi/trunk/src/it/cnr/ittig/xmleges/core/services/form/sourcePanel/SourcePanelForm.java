package it.cnr.ittig.xmleges.core.services.form.sourcePanel;

import javax.swing.JEditorPane;

import it.cnr.ittig.services.manager.Service;


public interface SourcePanelForm extends Service{

	public boolean openForm();
	
	public void setSourceText(String text);
	
	public String getSourceText();
	
	public JEditorPane getTextPane();

}

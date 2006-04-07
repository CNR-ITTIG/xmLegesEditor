package it.cnr.ittig.xmleges.editor.services.form.meta.inquadramento;

import it.cnr.ittig.services.manager.Service;

public interface InquadramentoForm extends Service{

	public boolean openForm();
	
	/**
	 * Imposta la DTD del documento (base, completo...)
	 * 
	 * @param tipoDTD DTD del documento
	 */
	public void setTipoDTD(String tipoDTD);

public void setInfodoc();
	
	public void setInfomancanti();
	
	public void setOggetto();
	
	public void setProponenti();
	
	public void getInfodoc();
	
	public void getInfomancanti();
	
	public void getOggetto();
	
	public void getProponenti();


}

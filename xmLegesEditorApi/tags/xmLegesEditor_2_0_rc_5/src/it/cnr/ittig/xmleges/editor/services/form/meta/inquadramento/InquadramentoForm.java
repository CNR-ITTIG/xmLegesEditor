package it.cnr.ittig.xmleges.editor.services.form.meta.inquadramento;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento.InfoMancanti;
import it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento.Infodoc;
import it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento.Oggetto;

public interface InquadramentoForm extends Service{

	public boolean openForm();
	
	/**
	 * Imposta la DTD del documento (base, completo...)
	 * 
	 * @param tipoDTD DTD del documento
	 */
	public void setTipoDTD(String tipoDTD);

	public void setInfodoc(Infodoc infodoc);
	
	public void setInfomancanti(InfoMancanti infomancanti);
	
	public void setOggetto(Oggetto oggetto);
	
	public void setProponenti(String[] proponenti);
	
	public Infodoc getInfodoc();
	
	public InfoMancanti getInfomancanti();
	
	public Oggetto getOggetto();
	
	public String[] getProponenti();


}

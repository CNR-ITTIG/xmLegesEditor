package it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento;

import it.cnr.ittig.services.manager.Service;


public interface MetaInquadramento extends Service {
	
	public void setInfodoc(Infodoc infodoc);
	
	public void setInfomancanti(InfoMancanti infomancanti);
	
	public void setOggetto(Oggetto oggetto);
	
	public void setProponenti(String[] proponenti);
	
	public Infodoc getInfodoc();
	
	public InfoMancanti getInfomancanti();
	
	public Oggetto getOggetto();
	
	public String[] getProponenti();

}

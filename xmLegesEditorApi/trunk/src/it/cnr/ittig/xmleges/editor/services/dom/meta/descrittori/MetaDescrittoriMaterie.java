package it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori;

import it.cnr.ittig.services.manager.Service;

public interface MetaDescrittoriMaterie extends Service{

	public void setVocabolari(Vocabolario[] vocabolari);
	public Vocabolario[] getVocabolari();
	
	
}

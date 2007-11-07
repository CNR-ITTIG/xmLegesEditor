package it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori;

import org.w3c.dom.Node;

import it.cnr.ittig.services.manager.Service;
import it.ipiu.digest.parse.Vocabolario;

public interface MetaDescrittoriMaterie extends Service{

	public void setVocabolari(Node node, Vocabolario[] vocabolari);
	public Vocabolario[] getVocabolari(Node node);
	
	
}

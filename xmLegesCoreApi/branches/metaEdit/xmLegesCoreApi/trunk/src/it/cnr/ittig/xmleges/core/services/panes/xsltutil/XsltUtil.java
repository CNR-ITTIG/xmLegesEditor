package it.cnr.ittig.xmleges.core.services.panes.xsltutil;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Servizio per 
 */
public interface XsltUtil extends Service {

	public void setUrn(String urn, String colore);

	/**
	 * Restituisce la urn memorizzata
	 * 
	 * @param urn da cercare
	 * @return null se non presente altrimenti il valore memorizzato
	 */
	public String getUrn(String urn);
	
	/**
	 * Rimuove le urn memorizzate
	 * 
	 */
	public void clearUrn();
}

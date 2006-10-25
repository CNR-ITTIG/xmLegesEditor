package it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento;

import org.w3c.dom.Node;

import it.cnr.ittig.services.manager.Service;
/**
 * Servizio per l'inserimento dell'inquadramento del documento.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * 
 */

public interface MetaInquadramento extends Service {
	
	public void setInfodoc(Infodoc infodoc);
	
	public void setInfomancanti(InfoMancanti infomancanti);
	
	public void setOggetto(Oggetto oggetto);
	
	public void setProponenti(String[] proponenti);
	
	public Infodoc getInfodoc();
	
	public InfoMancanti getInfomancanti();
	
	public Oggetto getOggetto();
	
	public String[] getProponenti();
	
	public void setActiveNode(Node node);
	
	
}

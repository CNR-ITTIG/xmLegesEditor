package it.cnr.ittig.xmleges.core.services.panes.attributes;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.frame.Pane;

/**
 * Servizio per la visualizzazione e la modifica degli attributi di un nodo.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface AttributesPane extends Service, Pane {

	/**
	 * Disabilita l'aggiornamento del pannello se <code>ignore</code> &egrave;
	 * <code>true</code>
	 * 
	 * @param ignore
	 */
	public void setIgnoreInteractions(boolean ignore);

	/**
	 * Imposta il nome del pannello.
	 * 
	 * @param name nome del pannello
	 */
	public void setName(String name);

	/**
	 * Aggiunge l'<code>editor</code> come modificatore specializzato su
	 * determinanti nodi.
	 * 
	 * @param editor editor specializzato che deve essere inserito
	 */
	public void addEditor(AttributeEditor editor);

	/**
	 * Rimuove l'<code>editor</code> come modificatore specializzato su
	 * determinanti nodi.
	 * 
	 * @param editor editor specializzato che deve essere rimosso
	 */
	public void removeEditor(AttributeEditor editor);

}

package it.cnr.ittig.xmleges.core.services.action.edit.cutcopypaste;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per le azioni di cut/copy/paste sul testo.
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface EditCutCopyPasteAction extends Service {

	/**
	 * Taglia il testo o i nodi correnti.
	 */
	public void doCut();

	/**
	 * Copia il testo o i nodi correnti.
	 */
	public void doCopy();

	/**
	 * Incolla il testo o i nodi precedentemente copiati o tagliati.
	 */
	public void doPaste();

	/**
	 * Incolla come solo testo il testo o i nodi precedentemente copiati o
	 * tagliati.
	 */
	public void doPasteText();

	/**
	 * Elimina il testo o i nodi correnti.
	 */
	public void doDelete();
}

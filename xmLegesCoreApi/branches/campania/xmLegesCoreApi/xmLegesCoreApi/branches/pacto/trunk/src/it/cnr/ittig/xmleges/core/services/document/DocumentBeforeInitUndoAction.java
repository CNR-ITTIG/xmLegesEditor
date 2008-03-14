package it.cnr.ittig.xmleges.core.services.document;

import org.w3c.dom.Document;

/**
 * Classe per effettuare un'azione sul documento prima dell'inizializzazione
 * dell'UNDO.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface DocumentBeforeInitUndoAction {

	/**
	 * Motodo invocato da DocumentManager prima dell'inizializzazione del
	 * sistema di UNDO. Se il metodo restituisce <code>false</code>,
	 * l'apertura del documento fallisce.
	 * 
	 * @param dom documento aperto
	 * @return <code>true</code> per continuare l'apertura del documento
	 *         nell'applicazione, <code>false</code> per non aprire il
	 *         documento
	 */
	public boolean beforeInitUndo(Document dom);

}

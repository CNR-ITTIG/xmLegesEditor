package it.cnr.ittig.xmleges.core.services.document;

import org.w3c.dom.Document;

/**
 * Classe evento per notificare che l'intero documento &egrave; stato
 * modificato.
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
 * @see it.cnr.ittig.xmleges.core.services.document.DocumentManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class DocumentChangedEvent extends DocumentManagerEvent {

	/** Transazione. */
	EditTransaction transaction;

	/** Flag per indicare se &egrave; un evento di UNDO */
	boolean undo;

	/**
	 * Costruttore dell'evento.
	 * 
	 * @param source sorgente che emette l'evento
	 * @param transaction transazione
	 * @param document documento modificato
	 */
	public DocumentChangedEvent(Object source, EditTransaction transaction, Document document, boolean undo) {
		super(source, document);
		this.transaction = transaction;
		this.undo = undo;
	}

	/**
	 * Restituisce la transazione.
	 * 
	 * @return transazione
	 */
	public EditTransaction getTransaction() {
		return this.transaction;
	}

	/**
	 * Indica se l'evento &egrave; emesso durante l'operazione di UNDO.
	 * 
	 * @return <code>true</code> se &egrave; durante l'operazione di UNDO
	 */
	public boolean isUndo() {
		return this.undo;
	}

}

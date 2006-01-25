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
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class DocumentClosedEvent extends DocumentManagerEvent {

	/**
	 * Costruttore dell'evento.
	 * 
	 * @param source sorgente che emette l'evento
	 * @param document documento modificato
	 */
	public DocumentClosedEvent(Object source, Document document) {
		super(source, document);
	}

}

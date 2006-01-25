package it.cnr.ittig.xmleges.core.services.document;

import java.util.EventObject;

import org.w3c.dom.Document;

/**
 * Classe base per definire gli eventi di modifica o selezione dei nodi del
 * documento.
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
public class DocumentManagerEvent extends EventObject {

	/** Documento. */
	Document document;

	/**
	 * Costruttore dell'evento.
	 * 
	 * @param source sorgente che ha emesso l'evento
	 * @param transaction transazione
	 */
	public DocumentManagerEvent(Object source, Document document) {
		super(source);
		this.document = document;
	}

	/**
	 * Restituisce il documento modificato.
	 * 
	 * @return documento modificato
	 */
	public Document getDocument() {
		return this.document;
	}

}

package it.cnr.ittig.xmleges.core.services.frame;

/**
 * Classe per notificare attraverso l'EventManager che un pannello ha ricevuto
 * il focus.
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
 * @see it.cnr.ittig.xmleges.core.services.frame.Frame
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 */
public class PaneFocusGainedEvent extends PaneEvent {

	/**
	 * Costruisce l'evento impostando la sorgente e il pannello che ha subito la
	 * variazione di stato.
	 * 
	 * @param source sorgente dell'evento
	 * @param pane pannello che ha subito la variazione
	 */
	public PaneFocusGainedEvent(Object source, Pane pane) {
		super(source, pane);
	}

}

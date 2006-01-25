package it.cnr.ittig.xmleges.core.services.frame;

import java.util.EventObject;

/**
 * Classe base per notificare gli eventi emessi dai pannelli o
 * dall'implementazione di Frame.
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
public class PaneEvent extends EventObject {

	/** Pannello oggetto dell'evento. */
	Pane pane;

	/**
	 * Costruisce l'evento impostando la sorgente e il pannello oggetto
	 * dell'evento.
	 * 
	 * @param source sorgente dell'evento
	 * @param pane pannello che ha subito la variazione
	 */
	public PaneEvent(Object source, Pane pane) {
		super(source);
		this.pane = pane;
	}

	/**
	 * Restituisce il pannello oggetto dell'evento.
	 * 
	 * @return pannello oggetto dell'evento
	 */
	public Pane getPane() {
		return this.pane;
	}

}

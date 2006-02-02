package it.cnr.ittig.xmleges.core.services.action;

import java.util.EventObject;

import javax.swing.Action;

/**
 * Evento emesso da ActionManager per indicare che un'azione &egrave; stata
 * registrata.
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
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ActionRegisteredEvent extends EventObject {

	/** Nome dell'azione registrata nell'ActionManager. */
	String name;

	/** Azione registrata nell'ActionManager. */
	Action action;

	/**
	 * Costruttore di <code>ActionRegisteredEvent</code>.
	 * 
	 * @param source sorgente dell'evento
	 * @param name nome dell'azione
	 * @param action azione
	 */
	public ActionRegisteredEvent(Object source, String name, Action action) {
		super(source);
		this.name = name;
		this.action = action;
	}

	/**
	 * Restituisce il nome dell'azione registrata nell'ActionManager.
	 * 
	 * @return nome dell'azione registrata nell'ActionManager
	 */
	public String getActionName() {
		return this.name;
	}

	/**
	 * Restituisce l'azione registrata nell'ActionManager.
	 * 
	 * @return azione registrata nell'ActionManager
	 */
	public Action getAction() {
		return this.action;
	}

	public String toString() {
		return this.action.toString();
	}
}

package it.cnr.ittig.xmleges.core.services.event;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Interfaccia per ricevere un evento da <code>EventManager</code>.
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
public interface EventManagerListener extends EventListener {

	/**
	 * Metodo invocato per notificare l'evento all'ascoltatore che si &egrave;
	 * registrato opportumente nell'EventManager .
	 * 
	 * @param event oggetto evento
	 */
	public void manageEvent(EventObject event);

}

package it.cnr.ittig.xmleges.core.services.event;

import it.cnr.ittig.services.manager.Service;

import java.util.EventObject;

/**
 * Servizio per la gestione degli eventi. Un qualsiasi oggetto si pu&ograve;
 * registrare come <i>ascoltatore </i> su tutti gli eventi o solo su alcuni in
 * particolare in base alla classe dell'evento. <br>
 * Esempio:
 * 
 * <pre>
 *    public class MyClass implements EventManagerListener {
 *        ...
 *        public void myInitMethod() {
 *            eventManager.addListener(this, DocumentEvent.class);
 *            eventManager.addListener(this, FrameEvent.class);
 *        }
 *    
 *        public void manageEvent(EventObject event) {
 *            if (event instanceof DocumentEvent) {
 *                ...
 *            }
 *            if (event instanceof FrameEvent) {
 *                ...
 *            }
 *        ...
 *    }
 * </pre>
 * 
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
public interface EventManager extends Service {

	/**
	 * Registra il listener <code>listener</code> per tutti gli eventi.
	 * 
	 * @param listener oggetto che riceve l'evento
	 */
	public void addListener(EventManagerListener listener);

	/**
	 * Registra il listener <code>listener</code> solo su eventi di tipo
	 * <code>eventClass</code>.
	 * 
	 * @param listener oggetto che riceve l'evento
	 * @param eventClass tipo di evento
	 */
	public void addListener(EventManagerListener listener, Class eventClass);

	/**
	 * Rimuove il listener <code>listener</code> su tutti gli eventi.
	 * 
	 * @param listener oggetto da rimuover
	 */
	public void removeListener(EventManagerListener listener);

	/**
	 * Rimuove il listener <code>listener</code> per gli eventi di tipo
	 * <code>eventClass</code>.
	 * 
	 * @param listener oggetto da rimuovere
	 * @param eventClass tipo di evento
	 */
	public void removeListener(EventManagerListener listener, Class eventClass);

	/**
	 * Avverte i listener registrati precedentemente tramite
	 * <code>addListener</code>.
	 * 
	 * @param event evento
	 */
	public void fireEvent(EventObject event);

	/**
	 * Avverte i listener registrati precedentemente tramite
	 * <code>addListener</code> in modo sequenziale. Il thread che invoca
	 * questo metodo attende la notifica di tutti gli eventi.
	 * 
	 * @param event evento
	 */
	public void fireEventSerially(EventObject eventObject);

}

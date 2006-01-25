package it.cnr.ittig.xmleges.core.services.action;

import it.cnr.ittig.services.manager.Service;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.Action;

/**
 * Servizio per la gestione delle azioni. Le azione registrate possono essere
 * recuperate tramite il nome usanto il metodo <code>getAction</code>.
 * Inoltre &egrave; possibile ottenere i nomi delle azioni che si sono
 * registrate attraverso i metodi <code>getActionNames</code>.
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
 * @see javax.swing.Action
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface ActionManager extends Service {

	/**
	 * Indica se l'azione di nome <code>name</code> &egrave; stata registrata.
	 * 
	 * @param name nome dell'azione
	 * @return true se &egrave; stata registrata
	 */
	public boolean hasAction(String name);

	/**
	 * Registra l'azione <code>action</code> con nome <code>actionName</code>
	 * nel catalogo.
	 * 
	 * @param actionName nome dell'azione
	 * @param action azione da registrare
	 * @return <code>true</code> se la registrazione &egrave; stata eseguita
	 */
	public boolean registerAction(String actionName, Action action);

	/**
	 * Recupera l'azione tramite il nome <code>name</code>.
	 * 
	 * @param name nome dell'azione
	 * @return azione
	 */
	public Action getAction(String name);

	/**
	 * Restituisce i nomi di tutte le azioni registrate precedentemente con il
	 * metodo <code>registerAction</code>.
	 * 
	 * @return nomi delle azioni
	 */
	public String[] getActionNames();

	/**
	 * Restituisce i nomi delle azioni che soddisfano l'espressione regolare
	 * <code>regexp</code>.
	 * 
	 * @param regexp espressione regolare per filtrare i nomi
	 * @return nomi delle azioni
	 */
	public String[] getActionNames(String regexp);

	/**
	 * Attiva l'azione di nome <code>name</code> con l'evento
	 * <code>event</code>. La classe EventObject non ha n&eacute; l'id
	 * (getID()) n&eacute; il nome del comando (getActionCommand()) quindi sono
	 * impostati automaticamente con 0 e con il nome dell'azione (
	 * <code>name</code>).
	 * 
	 * @param name nome dell'azione
	 * @param event evento
	 */
	public void fireAction(String name, EventObject event);

	/**
	 * Attiva l'azione di nome <code>name</code> con l'evento
	 * <code>event</code>. La classe AWTEvent non ha il nome del comando
	 * (getActionCommand()), quindi &egrave; impostato automaticamente con il
	 * nome dell'azione.
	 * 
	 * @param name nome dell'azione
	 * @param event evento
	 */
	public void fireAction(String name, AWTEvent event);

	/**
	 * Attiva l'azione di nome <code>name</code> con l'evento
	 * <code>event</code>.
	 * 
	 * @param name nome dell'azione
	 * @param event evento
	 */
	public void fireAction(String name, ActionEvent event);

}

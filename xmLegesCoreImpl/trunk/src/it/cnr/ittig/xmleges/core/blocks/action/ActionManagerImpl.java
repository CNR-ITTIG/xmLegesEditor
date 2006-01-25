package it.cnr.ittig.xmleges.core.blocks.action;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.ActionRegisteredEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Action;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.ActionManager</code>.</h1>
 * <h1>Descrizione</h1>
 * Le propriet&agrave; delle azioni (icona, tooltip, testo ecc) sono impostate tramite il
 * metodo <code>UtilUI.applyI18n</code>.
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.util.ui.UtilUI:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * Nessuno. (Le azioni sono configurate tramite UtilUI.applyI18n)
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @see it.cnr.ittig.xmleges.core.services.util.ui.UtilUI
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ActionManagerImpl implements ActionManager, Loggable, Serviceable {
	Logger logger;

	EventManager eventManager;

	UtilUI utilUI;

	Hashtable actions = new Hashtable();

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
	}

	// ///////////////////////////////////////////////// ActionManager Interface
	public boolean hasAction(String name) {
		return name != null && actions.containsKey(name);
	}

	public boolean registerAction(String actionName, Action action) {
		if (actionName == null || action == null) {
			logger.error("Null actionName or action");
			return false;
		}
		actions.put(actionName, utilUI.applyI18n(actionName, action));
		logger.debug("Action registered: " + actionName);
		eventManager.fireEvent(new ActionRegisteredEvent(this, actionName, action));
		return true;
	}

	public Action getAction(String name) {
		if (name == null || !actions.containsKey(name)) {
			logger.warn("Action not found:" + name);
			return null;
		}
		logger.debug("Action found: " + name);
		return (Action) actions.get(name);
	}

	public String[] getActionNames() {
		return getActionNames(".*");
	}

	public String[] getActionNames(String regexp) {
		Vector vet = new Vector(100);
		Enumeration en = actions.keys();
		while (en.hasMoreElements()) {
			String name = (String) en.nextElement();
			if (regexp.matches(name))
				vet.addElement(name);
		}
		String[] ret = new String[vet.size()];
		vet.copyInto(ret);
		return ret;
	}

	public void fireAction(String name, EventObject event) {
		fireAction(name, new ActionEvent(event.getSource(), 0, name));
	}

	public void fireAction(String name, AWTEvent event) {
		fireAction(name, new ActionEvent(event.getSource(), event.getID(), name));

	}

	public void fireAction(String name, ActionEvent event) {
		Action action = getAction(name);
		action.actionPerformed(event);
		logger.debug("Action fired: " + name);
	}

}

package it.cnr.ittig.xmleges.editor.blocks.action.rinumerazione;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.bars.StatusBar;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.editor.services.action.rinumerazione.RinumerazioneAction;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.rinumerazione.RinumerazioneAction</code>.
 * </h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class RinumerazioneActionImpl implements RinumerazioneAction, EventManagerListener, Loggable, Serviceable, Initializable, Startable {

	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	DocumentManager documentManager;

	Rinumerazione rinumerazione;

	AbstractAction renumAction = new RenumAction();

	UtilMsg utilMsg;

	Bars bars;

	StatusBar statusBar;

	PreferenceManager preferenceManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		statusBar = ((Bars) serviceManager.lookup(Bars.class)).getStatusBar();
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("editor.rinumerazione", renumAction);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
	}

	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws java.lang.Exception {
		renumAction.setEnabled(false);
		String attiva = preferenceManager.getPreferenceAsString(getClass().getName());
		doSetRinumerazione(Boolean.valueOf(attiva).booleanValue());
	}

	public void stop() throws java.lang.Exception {
		preferenceManager.setPreference(getClass().getName(), "" + rinumerazione.isRinumerazione());
	}

	public void manageEvent(EventObject event) {
		renumAction.setEnabled(!documentManager.isEmpty());
	}

	// /////////////////////////////////////////////// Azioni
	public class RenumAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			boolean rin = rinumerazione.isRinumerazione();
			String str = "editor.rinumerazione.msg." + (rin ? "enable" : "disable");
			boolean ris = utilMsg.msgYesNo(str);
			doSetRinumerazione((rin && !ris) || (!rin && ris));
		}
	}

	public void doSetRinumerazione(boolean attiva) {
		statusBar.setText("editor.rinumerazione." + (attiva ? "enable" : "disable"), "renum");
		logger.debug("attivazione rinumerazione: " + attiva);
		rinumerazione.setRinumerazione(attiva);
		if (attiva && !documentManager.isEmpty()) {
			try {
				EditTransaction t = documentManager.beginEdit();
				rinumerazione.aggiorna(documentManager.getDocumentAsDom());
				documentManager.commitEdit(t);
			} catch (DocumentManagerException ex) {
			}
		}
	}

}

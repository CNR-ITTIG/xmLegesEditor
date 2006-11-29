package it.cnr.ittig.xmleges.core.blocks.action.tool.spellcheck;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.tool.spellcheck.ToolSpellCheckAction;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.bars.StatusBar;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.spellcheck.SpellCheck;
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheck;
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheckEvent;
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.form.SpellCheckForm;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.tool.spellcheck.ToolSpellCheckAction</code>.
 * </h1>
 * <h1>Descrizione</h1>
 * Servizio per l'avvio del controllo ortografico e l'attivazione del controllo
 * ortografico automatico.
 * <h1>Configurazione</h1>
 * Nessuna
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheck:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.util.msg.utilMsg:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.bars.Bars:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.preference.PreferenceManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.spellcheck.dom.form.SpellCheckForm:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.spellcheck.SpellCheck:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li><code>tool.spellcheck</code>: descrizione dell'azione come specificato nell'ActionManager; </li>
 * <li><code>tool.spellcheck.auto</code>: descrizione dell'azione come specificato nell'ActionManager; </li> 
 * <li><code>tool.spellcheck.auto.status.enable</code>: messaggio di abilitazione spellcheck;</li>
 * <li><code>tool.spellcheck.auto.status.disable</code>: messaggio di disabilitazione spellcheck;</li> 
 * <li><code>tool.spellcheck.auto.msg.enable</code>: messaggio di abilitazione spellcheck;</li> 
 * <li><code>tool.spellcheck.auto.msg.disable</code>: messaggio di abilitazione spellcheck;</li>
 * </ul>
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>

 *  * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheck:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.util.msg.utilMsg:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.bars.Bars:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.preference.PreferenceManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.spellcheck.dom.form.SpellCheckForm:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.spellcheck.SpellCheck:1.0</li>

 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @see it.cnr.ittig.xmleges.core.services.preference.PreferenceManager
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ToolSpellCheckActionImpl implements ToolSpellCheckAction, Loggable, Serviceable, Initializable, EventManagerListener, Startable {

	Logger logger;

	ActionManager actionManager;

	DomSpellCheck domSpellCheck;

	SpellCheck spellCheck;
	
	UtilMsg utilMsg;

	Bars bars;

	AbstractAction spellAction = new SpellAction();

	StatusBar statusBar;

	PreferenceManager preferenceManager;

	SpellCheckForm spellCheckForm;

	EventManager eventManager;

	DocumentManager documentManager;

	Node activeNode;

	int start, end;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		domSpellCheck = (DomSpellCheck) serviceManager.lookup(DomSpellCheck.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		statusBar = ((Bars) serviceManager.lookup(Bars.class)).getStatusBar();
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		spellCheckForm = (SpellCheckForm) serviceManager.lookup(SpellCheckForm.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		spellCheck = (SpellCheck) serviceManager.lookup(SpellCheck.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("tool.spellcheck", spellAction);
		actionManager.registerAction("tool.spellcheck.auto", new AutoSpellAction());
		spellAction.setEnabled(false);
		eventManager.addListener(this, DocumentClosedEvent.class);
		eventManager.addListener(this, DocumentOpenedEvent.class);
	}

	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws java.lang.Exception {
		String enable = preferenceManager.getPreferenceAsString(getClass().getName());
		setAutoSpellCheck(Boolean.valueOf(enable).booleanValue());
	}

	public void stop() throws java.lang.Exception {
		preferenceManager.setPreference(getClass().getName(), "" + domSpellCheck.isEnabled());
	}

	public void manageEvent(EventObject event) {
		if (event instanceof DocumentOpenedEvent)
			//controllo se la libreria dello SpellCk. è stata caricata
			//spellAction.setEnabled(true);
			spellAction.setEnabled(spellCheck.isLoad());

		if (event instanceof DocumentClosedEvent)
			spellAction.setEnabled(false);

	}

	// ////////////////////////////////////////// ToolSpellCheckAction Interface
	public void doSpellCheck() {
		logger.debug("spellCheck pressed");
		spellCheckForm.openForm();
	}

	public void setAutoSpellCheck(boolean auto) {
		domSpellCheck.setEnabled(auto);
		statusBar.setText("tool.spellcheck.auto.status." + (auto ? "enable" : "disable"), "spellcheck");
		if (auto && !documentManager.isEmpty()) {
			Node nirNode = documentManager.getRootElement();
			if (nirNode != null) {
				DomSpellCheckEvent scEvt = new DomSpellCheckEvent(this, domSpellCheck.spellCheck(nirNode));
				if (scEvt.hasWords())
					eventManager.fireEvent(scEvt);
			}
		}
	}

	/**
	 * Azione per attivare la form per il controllo ortografico guidato.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
	 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General
	 * Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
	 */
	class SpellAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doSpellCheck();
		}
	}

	/**
	 * Azione per attivare o disabilitare il controllo ortografico automatico.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
	 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General
	 * Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */

	class AutoSpellAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			boolean rin = domSpellCheck.isEnabled();
			String str = "tool.spellcheck.auto.msg." + (rin ? "disable" : "enable");
			boolean ris = utilMsg.msgYesNo((Component) e.getSource(), str);
			setAutoSpellCheck((rin && !ris) || (!rin && ris));
		}
	}
}

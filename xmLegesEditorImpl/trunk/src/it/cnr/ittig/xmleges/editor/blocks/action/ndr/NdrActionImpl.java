package it.cnr.ittig.xmleges.editor.blocks.action.ndr;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheckEvent;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.ndr.NdrAction;
import it.cnr.ittig.xmleges.editor.services.dom.ndr.Ndr;
import it.cnr.ittig.xmleges.editor.services.dom.ndr.Nota;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.form.ndr.NdrForm;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.ndr.NdrAction</code>.</h1>
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */

public class NdrActionImpl implements NdrAction, Loggable, EventManagerListener, Serviceable, Initializable {
	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;

	AbstractAction ndrAction = new ndrAction();

	Ndr ndr;

	Node activeNode;

	NdrForm ndrForm;

	DtdRulesManager dtdRulesManager;

	Rinumerazione rinumerazione;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		ndr = (Ndr) serviceManager.lookup(Ndr.class);
		ndrForm = (NdrForm) serviceManager.lookup(NdrForm.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("editor.ndr", ndrAction);
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DomSpellCheckEvent.class);
		ndrAction.setEnabled(false);
	}

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof SelectionChangedEvent) {
			activeNode = ((SelectionChangedEvent) event).getActiveNode();
			ndrAction.setEnabled(ndr.canSetNdr(activeNode));
		}
	}

	protected void setModified(Node modified) {
		if (modified != null) {
			selectionManager.setActiveNode(this, modified);
			activeNode = modified;
			ndrAction.setEnabled(ndr.canSetNdr(activeNode));
			logger.debug(" set modified " + UtilDom.getPathName(modified));
		} else
			logger.debug(" modified null in set modified ");
	}

	private void setNumerazione() {
		if (ndrForm.isCardinale())
			rinumerazione.setRinumerazioneNdr("cardinale");
		else if (ndrForm.isLetterale())
			rinumerazione.setRinumerazioneNdr("letterale");
		else
			rinumerazione.setRinumerazioneNdr("romano");
	}

	// //////////////////////////////////////////// NdrAction Interface
	public void doNewNdr() {
		Node node = selectionManager.getActiveNode();
		int start = selectionManager.getTextSelectionStart();
		int end = selectionManager.getTextSelectionEnd();

		String textSelected = null;
		if (start != end)
			textSelected = node.getNodeValue().trim().substring(start, end);
		if (ndrForm.openForm(textSelected, ndr.getNotesFromDocument())) {
			setNumerazione();
			if (ndrForm.getNdrSelezionato() != null)
				setModified(ndr.setNdr(node, start, end, ((Nota) ndrForm.getNdrSelezionato()).getId(), null));
			else
				setModified(ndr.setNdr(node, start, end, null, ndrForm.getTestoNota()));
		}
	}

	// /////////////////////////////////////////////// Azioni
	public class ndrAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			doNewNdr();
		}
	}
}

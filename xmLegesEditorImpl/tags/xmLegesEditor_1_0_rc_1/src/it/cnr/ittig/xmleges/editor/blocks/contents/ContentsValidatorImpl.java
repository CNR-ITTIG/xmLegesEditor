package it.cnr.ittig.xmleges.editor.blocks.contents;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.contents.ContentsValidator;
import it.cnr.ittig.xmleges.core.services.document.DocumentChangedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.panes.problems.Problem;
import it.cnr.ittig.xmleges.core.services.panes.problems.ProblemsPane;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.util.EventObject;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.contents.ContentsValidator</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>document-manager</li>
 * <li>event-manager</li>
 * <li>selection-manager</li>
 * <li>util-msgr</li>
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
 * @see
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>, <a
 *         href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class ContentsValidatorImpl implements ContentsValidator, EventManagerListener, Loggable, Serviceable, Initializable {

	Logger logger;

	EventManager eventManager;

	SelectionManager selectionManager;

	UtilMsg utilMsg;

	ProblemsPane problemsPane;

	public Node changedNode = null;

	public int changeType = -1;

	boolean enabled = true;

	private boolean changedDataDoc = false;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		problemsPane = (ProblemsPane) serviceManager.lookup(ProblemsPane.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		eventManager.addListener(this, DocumentChangedEvent.class);
		eventManager.addListener(this, SelectionChangedEvent.class);
	}

	// ///////////////////////////////////////////// ContentsValidator Interface
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {

		if (!enabled)
			return;

		DocumentChangedEvent e;

		if (event instanceof DocumentChangedEvent) {
			e = (DocumentChangedEvent) event;

			// SINCRONIZZAZIONE DATADOC
			if (e.getTransaction().getEdits()[0].getParentNode().getNodeName().equalsIgnoreCase("dataDoc")
					|| e.getTransaction().getEdits()[0].getNode().getNodeName().equalsIgnoreCase("dataDoc")) {

				changedNode = e.getTransaction().getEdits()[0].getNode();
				changeType = e.getTransaction().getEdits()[0].getType();
				changedDataDoc = true;

			}
		}

		if (event instanceof SelectionChangedEvent) {

			// SINCRONIZZAZIONE DATADOC
			if (((SelectionChangedEvent) event).isActiveNodeChanged() && changedDataDoc) {
				changedDataDoc = false;
				// gestione della forma testuale della data malformata
				if (UtilDate.textualFormatToDate(UtilDom.getText(UtilDom.findParentByName(changedNode, "dataDoc"))) == null) {
					// FIXME mettere delle chiavi invece delle stringhe
					DataDocProblemImpl dataDocProblem = new DataDocProblemImpl(Problem.WARNING, "data malformata", this);
					dataDocProblem.setCanRemoveByUser(true);
					dataDocProblem.setCanResolveProblem(false);
					problemsPane.addProblem(dataDocProblem);
				}
				// verifica sincronizzazione dataDoc - norm
				else {
					// FIXME mettere delle chiavi invece delle stringhe
					DataDocProblemImpl dataDocProblem = new DataDocProblemImpl(Problem.ERROR, "data non sincronizzata", this);
					dataDocProblem.setCanRemoveByUser(false);
					dataDocProblem.setCanResolveProblem(true);
					if (!dataDocProblem.test())
						problemsPane.addProblem(dataDocProblem);
				}
			}

			// SINCRONIZZAZIONE URN

		}

	}
}

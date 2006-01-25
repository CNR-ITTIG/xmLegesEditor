package it.cnr.ittig.xmleges.core.blocks.action.file.print;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.file.print.FilePrintAction;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.PaneActivatedEvent;
import it.cnr.ittig.xmleges.core.services.printer.Printer;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.file.print.FilePrintAction</code>. </h1>
 * <h1>Descrizione</h1>
 * Questa implementazione registra l'azione <code>file.print</code> nell'ActionManager.
 * L'azione si attiva se il pannello corrente di modifica &egrave; stampabile (<code>Pane.canPrint()</code>).
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.frame.Frame:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.printer.Printer:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>file.print: descrizione dell'azione come specificato nell'ActionManager. </li>
 * </ul>
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
 * @see java.awt.print.Printable
 * @see it.cnr.ittig.xmleges.core.services.printer.Printer
 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class FilePrintActionImpl extends AbstractAction implements FilePrintAction, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	Frame frame;

	EventManager eventManager;

	ActionManager actionManager;

	Printer printer;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		printer = (Printer) serviceManager.lookup(Printer.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("file.print", this);
		eventManager.addListener(this, PaneActivatedEvent.class);
		enableAction();
	}

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		enableAction();
	}

	// /////////////////////////////////////////////// FilePrintAction Interface
	public void doPrint() {
		logger.debug("Calling Printer...");
		printer.print(frame.getActivePane().getComponentToPrint());
		logger.debug("Calling Printer OK");
	}

	// ////////////////////////////////////////////////// AbstractAction Extends
	public void actionPerformed(ActionEvent e) {
		doPrint();
	}

	protected void enableAction() {
		try {
			setEnabled(frame.getActivePane().canPrint());
		} catch (Exception ex) {
			setEnabled(false);
		}
	}

}

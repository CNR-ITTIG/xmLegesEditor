package it.cnr.ittig.xmleges.core.blocks.action.edit.findreplace;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.edit.findreplace.FindReplaceAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.Pane;
import it.cnr.ittig.xmleges.core.services.frame.PaneActivatedEvent;
import it.cnr.ittig.xmleges.core.services.frame.PaneEvent;
import it.cnr.ittig.xmleges.core.services.frame.PaneStatusChangedEvent;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.edit.findreplace.FindReplaceAction</code>. </h1>
 * <h1>Descrizione</h1>
 * Questa implementazione registra le azioni <code>edit.find</code>,
 * <code>edit.findnext</code> e <code>edit.replace</code> nell'ActionManager. <br>
 * L'attivazione delle varie azioni dipendono dal pannello corrente. Questa
 * implementazione attende l'evento <code>PaneActivatedEvent</code> emesso da
 * <code>Frame</code> per determinare il pannello attivo e attivare le azioni se
 * supportate.
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * Dipendente dall'implementazione dell'ActionManager per i nomi delle azioni: edit.find,
 * edit.findnext e edit.replace.
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
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class FindReplaceActionImpl implements FindReplaceAction, EventManagerListener, CaretListener, Loggable, Serviceable, Initializable {
	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;
	
	UtilMsg utilMsg;

	Frame frame;

	FindAction findAction = new FindAction();

	FindNextAction findNextAction = new FindNextAction();

	ReplaceAction replaceAction = new ReplaceAction();

	BtnFindAction btnFindAction = new BtnFindAction();

	BtnReplaceAction btnReplaceAction = new BtnReplaceAction();

	BtnReplaceAllAction btnReplaceAllAction = new BtnReplaceAllAction();

	Form form;

	JTextField find;

	JTextField replace;

	JCheckBox caseSensitive;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		form = (Form) serviceManager.lookup(Form.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("edit.find", findAction);
		actionManager.registerAction("edit.findnext", findNextAction);
		actionManager.registerAction("edit.replace", replaceAction);
		eventManager.addListener(this, PaneActivatedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		eventManager.addListener(this, PaneStatusChangedEvent.class);
		form.setMainComponent(getClass().getResourceAsStream("FindReplace.jfrm"));
		form.setCustomButtons(new String[] { "action.edit.findreplace.btn.close" });
		form.setName("action.edit.findreplace");
		
		form.setHelpKey("help.contents.form.findreplace");
		
		// form.setSize(300, 250);
		find = (JTextField) form.getComponentByName("action.edit.findreplace.find");
		find.addCaretListener(this);
		replace = (JTextField) form.getComponentByName("action.edit.findreplace.replace");
		caseSensitive = (JCheckBox) form.getComponentByName("action.edit.findreplace.case");
		AbstractButton btn;
		btn = (AbstractButton) form.getComponentByName("action.edit.findreplace.btn.find");
		btn.setAction(btnFindAction);
		btn = (AbstractButton) form.getComponentByName("action.edit.findreplace.btn.replace");
		btn.setAction(btnReplaceAction);
		btn = (AbstractButton) form.getComponentByName("action.edit.findreplace.btn.replaceall");
		btn.setAction(btnReplaceAllAction);
		clearFields();
		enableActions(null);
	}

	private void clearFields(){
		find.setText("");
		replace.setText("");
	}
	
	// ///////////////////////////////////////// EditFindReplaceAction Interface
	String toFind = null;

	boolean hasNext = false;

	boolean canReplace = false;

	public void doFind() {
		toFind = null;
		hasNext = false;
		canReplace = false;
		openForm();
	}

	public void doFindNext() {
		if (find.getText().length() == 0)
			return;
		FindIterator it = activePane.getFindIterator();
		if (toFind == null || !toFind.equals(find.getText())) {
			toFind = find.getText();
			it.initFind(toFind);
		}
		it.setCaseSensitive(caseSensitive.isSelected());
		hasNext = it.next();
		canReplace = it.canReplace(replace.getText());
		enableActions(activePane);
		if (!hasNext) {
			if(!utilMsg.msgYesNo("action.edit.findreplace.msg.endofdocument")){
				btnFindAction.setEnabled(false);
				btnReplaceAction.setEnabled(false);
				btnReplaceAllAction.setEnabled(false);
			}
		//	findAction.setEnabled(false);
		//	btnFindAction.setEnabled(false);
		}
	}

	public void doReplace() {
		String r = replace.getText();
		FindIterator it = activePane.getFindIterator();
		if (it.canReplace(r)) {
			logger.debug("doReplace:" + toFind + "->" + r);
			it.replace(r);
			doFindNext();
		} else
			logger.debug("doReplace: cannot replace");
	}

	Pane activePane = null;

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof PaneEvent) {
			activePane = frame.getActivePane();
			enableActions(activePane);
		} else
			enableActions(null);
	}

	protected void enableActions(Pane pane) {
		if (pane == null) {
			findAction.setEnabled(false);
			findNextAction.setEnabled(false);
			replaceAction.setEnabled(false);
		} else {
			findAction.setEnabled(activePane.canFind());
			findNextAction.setEnabled(activePane.canFind());
			replaceAction.setEnabled(activePane.canFind() && canReplace);
			btnFindAction.setEnabled( find.getText().length() > 0);   							  // hasNext &&
			btnReplaceAction.setEnabled(find.getText().length() > 0 && canReplace);               // hasNext &&   
			btnReplaceAllAction.setEnabled(find.getText().length() > 0 && canReplace);            // hasNext &&
		}
	}

	protected void openForm() {
		form.showDialog();
	}

	protected class FindAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doFind();
		}
	}

	protected class FindNextAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doFindNext();
		}
	}

	protected class ReplaceAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doReplace();
		}
	}

	/**
	 * Azione su pressione del tasto <code>find</code> della form.
	 * 
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class BtnFindAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doFindNext();
		}
	}

	/**
	 * Azione su pressione del tasto <code>replace</code> della form.
	 * 
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class BtnReplaceAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doReplace();
		}
	}

	/**
	 * Azione su pressione del tasto <code>replace all</code> della form.
	 * 
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class BtnReplaceAllAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (find.getText().length() != 0) {
				FindIterator it = activePane.getFindIterator();
				it.initFind(find.getText());
				it.setCaseSensitive(caseSensitive.isSelected());
				while (it.next())
					it.replace(replace.getText());
			}
		}
	}

	// /////////////////////////////////////////////// CaretListener Interface
	public void caretUpdate(CaretEvent e) {
		btnFindAction.setEnabled(find.getText().length() > 0);
		btnReplaceAction.setEnabled(find.getText().length() > 0);
		btnReplaceAllAction.setEnabled(find.getText().length() > 0);
	}
}

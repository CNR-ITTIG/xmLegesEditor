package it.cnr.ittig.xmleges.core.blocks.action.edit.findreplace;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.blocks.panes.source.SourceTextPane;
import it.cnr.ittig.xmleges.core.services.action.edit.findreplace.SourceFindReplaceAction;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.sourcePanel.SourcePanelForm;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
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
 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @version 1.0
 * @author <a href="mailto:francesca.uccheddu@ittig.cnr.it">Francesca Uccheddu</a>
 */
public class SourceFindReplaceActionImpl implements SourceFindReplaceAction, CaretListener, Loggable, Serviceable, Initializable {
	Logger logger;
	
	UtilMsg utilMsg;

	FindAction findAction = new FindAction();

	FindNextAction findNextAction = new FindNextAction();

	BtnFindAction btnFindAction = new BtnFindAction();

	BtnReplaceAction btnReplaceAction = new BtnReplaceAction();

	BtnReplaceAllAction btnReplaceAllAction = new BtnReplaceAllAction();

	Form form;

	JTextField find;

	JTextField replace;

	JCheckBox caseSensitive;
	
	SourcePanelForm sourcePanelForm;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		sourcePanelForm = (SourcePanelForm) serviceManager.lookup(SourcePanelForm.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
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
		enableActions();
		
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
		FindIterator it = ((SourceTextPane) sourcePanelForm.getTextPane()).getSourceFindIterator();
		if (toFind == null || !toFind.equals(find.getText())) {
			toFind = find.getText();
			it.initFind(toFind);
		}
		it.setCaseSensitive(caseSensitive.isSelected());
		hasNext = it.next();
		canReplace = it.canReplace(replace.getText());
		if (!hasNext) {
			if(!utilMsg.msgYesNo("action.edit.findreplace.msg.endofdocument")){
				btnFindAction.setEnabled(false);
				btnReplaceAction.setEnabled(false);
				btnReplaceAllAction.setEnabled(false);
			}

		}
	}

	public void doReplace() {
		String r = replace.getText();
		FindIterator it = ((SourceTextPane) sourcePanelForm.getTextPane()).getSourceFindIterator();
		if (it.canReplace(r)) {
			logger.debug("doReplace:" + toFind + "->" + r);
			it.replace(r);
			doFindNext();
		} else
			logger.debug("doReplace: cannot replace");
	}

	

	protected void enableActions() {
			findAction.setEnabled(true);
			findNextAction.setEnabled(true);
			btnFindAction.setEnabled( find.getText().length() > 0);   							  // hasNext &&
			btnReplaceAction.setEnabled(find.getText().length() > 0 && canReplace);               // hasNext &&   
			btnReplaceAllAction.setEnabled(find.getText().length() > 0 && canReplace);            // hasNext &&
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


	/**
	 * Azione su pressione del tasto <code>find</code> della form.
	 * 
	 *  
	 */
	protected class BtnFindAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doFindNext();
		}
	}

	/**
	 * Azione su pressione del tasto <code>replace</code> della form.
	 * 
	 *  
	 */
	protected class BtnReplaceAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doReplace();
		}
	}

	/**
	 * Azione su pressione del tasto <code>replace all</code> della form.
	 * 
	 * 
	 */
	protected class BtnReplaceAllAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
				if (find.getText().length() != 0) {
					FindIterator it = ((SourceTextPane) sourcePanelForm.getTextPane()).getSourceFindIterator();
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

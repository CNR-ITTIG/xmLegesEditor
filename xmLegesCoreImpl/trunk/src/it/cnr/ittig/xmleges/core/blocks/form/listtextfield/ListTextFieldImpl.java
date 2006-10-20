package it.cnr.ittig.xmleges.core.blocks.form.listtextfield;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormException;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextField;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldEditor;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldElementEvent;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldElementListener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.listtextfield.ListTextField</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * Nessuna.
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
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
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ListTextFieldImpl implements MouseListener, ListTextField, ListSelectionListener, Loggable, Serviceable, Initializable {
	Logger logger;

	Form form;

	ListTextFieldEditorForm editorForm;

	JList list;

	DefaultListModel listModel = new DefaultListModel();
	
	// ListTextFieldEditor listEditor;
	AddAction addAction = new AddAction();

	ModifyAction modifyAction = new ModifyAction();

	DeleteAction deleteAction = new DeleteAction();

	MoveUpAction moveUpAction = new MoveUpAction();

	MoveDownAction moveDownAction = new MoveDownAction();

	EventListenerList listTextFieldElementListeners = new EventListenerList();

	boolean moveButtons = true;
	
	private int lastselected=-1;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		editorForm = new ListTextFieldEditorForm(serviceManager);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("ListTextField.jfrm"));
		list = (JList) form.getComponentByName("form.listtextfield.list");
		list.addListSelectionListener(this);
		list.addMouseListener(this);		
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setEnabled(true);
		list.setModel(listModel);
		AbstractButton btn;
		btn = (AbstractButton) form.getComponentByName("form.listtextfield.add");
		btn.setAction(addAction);
		btn = (AbstractButton) form.getComponentByName("form.listtextfield.modify");
		btn.setAction(modifyAction);
		btn = (AbstractButton) form.getComponentByName("form.listtextfield.delete");
		btn.setAction(deleteAction);
		btn = (AbstractButton) form.getComponentByName("form.listtextfield.moveup");
		btn.setName("form.listtextfield.moveup");
		btn.setAction(moveUpAction);
		btn = (AbstractButton) form.getComponentByName("form.listtextfield.movedown");
		btn.setName("form.listtextfield.movedown");
		btn.setAction(moveDownAction);
		modifyAction.setEnabled(false);
		deleteAction.setEnabled(false);
		moveUpAction.setEnabled(false);
		moveDownAction.setEnabled(false);
	}

	// //////////////////////////////////////////////////// CommonForm Interface
	public Component getAsComponent() {
		return form.getAsComponent();
	}

	// //////////////////////////////////////////////// SourceDestList Interface
	public void setEditor(ListTextFieldEditor listEditor) {
		// this.listEditor = listEditor;
		// try {
		// editorForm.replaceComponent("form.listtextfieldeditor.editor",
		// listEditor.getAsComponent());
		// } catch (Exception ex) {
		// logger.error("error setting ListTextFieldEditor", ex);
		// }
		editorForm.setEditor(listEditor);
	}

	public void setListElements(Vector elem) {
		listModel.clear();
		addListElements(elem);
	}

	public void addListElements(Vector elem) {
		for (Enumeration en = elem.elements(); en.hasMoreElements();)
			listModel.addElement(en.nextElement());
		list = (JList) form.getComponentByName("form.listtextfield.list");
	}

	public Vector getListElements() {
		Vector ret = new Vector();
		if (listModel != null)
			for (Enumeration en = listModel.elements(); en.hasMoreElements();)
				ret.addElement(en.nextElement());
		return ret;
	}

	// ///////////////////////////////////////// ListSelectionListener Interface
	public void valueChanged(ListSelectionEvent e) {
		
		
		int selectedElement = list.getSelectedIndex();
		
		boolean enab = selectedElement >= 0 && selectedElement < listModel.size();
		if (enab) {
			editorForm.getEditor().setElement(listModel.getElementAt(selectedElement));
		}
		modifyAction.setEnabled(enab);
		deleteAction.setEnabled(enab);
		moveUpAction.setEnabled(enab && selectedElement > 0);
		moveDownAction.setEnabled(enab && selectedElement < listModel.size() - 1);
		
	}
	

	
	public void setMoveButtons(boolean moveButtons) {

		if (moveButtons && !this.moveButtons) {

			// Aggiungi pulsanti

			try {

				AbstractButton moveUp = (AbstractButton) form.getComponentByName("form.listtextfield.moveup");
				moveUp.setName("form.listtextfield.moveup");
				moveUp.setAction(moveUpAction);

				AbstractButton moveDown = (AbstractButton) form.getComponentByName("form.listtextfield.movedown");
				moveDown.setName("form.listtextfield.movedown");
				moveDown.setAction(moveDownAction);

				form.replaceComponent("form.listtextfield.dummymoveup", moveUp);
				form.replaceComponent("form.listtextfield.dummymovedown", moveDown);

			} catch (FormException e) {
				logger.warn("unable to add move buttons in ListTextField");
			}

			this.moveButtons = moveButtons;

		} else if (!moveButtons && this.moveButtons) {

			// Rimuovi pulsanti

			try {

				JLabel dummyMoveUp = new JLabel();
				dummyMoveUp.setSize(0, 0);
				dummyMoveUp.setName("form.listtextfield.dummymoveup");

				JLabel dummyMoveDown = new JLabel();
				dummyMoveDown.setSize(0, 0);
				dummyMoveDown.setName("form.listtextfield.dummymovedown");

				form.replaceComponent("form.listtextfield.moveup", dummyMoveUp);
				form.replaceComponent("form.listtextfield.movedown", dummyMoveDown);

			} catch (FormException e) {
				logger.warn("unable to remove move buttons from ListTextField");
			}

			this.moveButtons = moveButtons;
		}
	}

	public void addListTextFieldElementListener(ListTextFieldElementListener listener) {
		listTextFieldElementListeners.add(ListTextFieldElementListener.class, listener);
	}

	public void removeListTextFieldElementListener(ListTextFieldElementListener listener) {
		listTextFieldElementListeners.remove(ListTextFieldElementListener.class, listener);
	}

	public Object getSelectedItem() {
		return list.getSelectedValue();
	}
	
	public void setSelectedValue(Object obj) {
		list.setSelectedValue(obj,true);
	}

	/**
	 * Invoca il metodo elementChanged sui listener
	 * 
	 * @param listTextFieldElementEvent evento contenente il tipo di cambiamento
	 *            effettuato
	 */
	protected void fireListTextFieldElementEvent(ListTextFieldElementEvent listTextFieldElementEvent) {

		Object[] listeners = listTextFieldElementListeners.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListTextFieldElementListener.class) {
				((ListTextFieldElementListener) listeners[i + 1]).elementChanged(listTextFieldElementEvent);
			}
		}
	}

	/**
	 * Azione per l'inserimento di una nuova riga nella lista.
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
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class AddAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {

			// Pulisci i campi del form
			editorForm.getEditor().clearFields();

			if (editorForm.openForm(form.getAsComponent())) {

				// Notifica al ListTextFieldEditor l'incombente aggiunta dell'elemento
				fireListTextFieldElementEvent(new ListTextFieldElementEvent(this, ListTextFieldElementEvent.ELEMENT_ADD));

				// Recupera il nuovo elemento
				Object element = editorForm.getEditor().getElement();

				// Se ? stato restituito un elemento valido, aggiorna la lista
				if (element != null) {
					int selectedElement = list.getSelectedIndex();
					if (selectedElement == -1) {
						listModel.addElement(element);
						// FIXME  Tommaso: aggiunto per selezionare l'elemento inserito
						setSelectedValue(element);
					} else {
						listModel.insertElementAt(element, selectedElement + 1);
						list.setSelectedIndex(selectedElement + 1);
					}
				}
			}
		}
	}

	/**
	 * Azione per la modifica della riga selezionata.
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
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class ModifyAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			
			int selectedElement = list.getSelectedIndex();
			if (selectedElement != -1) {
				if (editorForm.openForm(form.getAsComponent())) {

					// Notifica al ListTextFieldEditor l'incombente modifica dell'elemento
					fireListTextFieldElementEvent(new ListTextFieldElementEvent(this, ListTextFieldElementEvent.ELEMENT_MODIFY));

					// Recupera l'elemento modificato
					Object element = editorForm.getEditor().getElement();

					// Se ? stato restituito un elemento valido, aggiorna la lista
					if (element != null) {
						listModel.remove(selectedElement);
						listModel.add(selectedElement, element);
						list.setSelectedIndex(selectedElement);
					}
				}
			}
		}
	}

	/**
	 * Azione per la cancellazione della riga selezionata.
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
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class DeleteAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			int selectedElement = list.getSelectedIndex();
			if (selectedElement != -1) {
				listModel.remove(selectedElement);
				fireListTextFieldElementEvent(new ListTextFieldElementEvent(this, ListTextFieldElementEvent.ELEMENT_REMOVE));
			}
		}
	}

	/**
	 * Azione per spostare un elemento verso l'alto.
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
	 * @author <a href="mailto:t.paba">Tommaso Paba</a>
	 */
	protected class MoveUpAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			int selectedElement = list.getSelectedIndex();
			if (selectedElement > 0) {
				Object o = listModel.remove(selectedElement);
				listModel.insertElementAt(o, selectedElement - 1);
				fireListTextFieldElementEvent(new ListTextFieldElementEvent(this, ListTextFieldElementEvent.ELEMENT_MOVEUP));
				list.setSelectedIndex(selectedElement - 1);
			}
		}
	}

	/**
	 * Azione per spostare un elemento verso l'alto.
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
	 * @author <a href="mailto:t.paba">Tommaso Paba</a>
	 */
	protected class MoveDownAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			int selectedElement = list.getSelectedIndex();
			if (selectedElement <= listModel.getSize() - 2) {
				Object o = listModel.remove(selectedElement);
				listModel.insertElementAt(o, selectedElement + 1);
				fireListTextFieldElementEvent(new ListTextFieldElementEvent(this, ListTextFieldElementEvent.ELEMENT_MOVEDOWN));
				list.setSelectedIndex(selectedElement + 1);
			}
		}
	}

	public void mousePressed(MouseEvent e) {		
	}
	
	
	public void mouseClicked(MouseEvent e) {
		int selectedElement = list.getSelectedIndex();
		if (lastselected!=-1 && selectedElement==lastselected){
			lastselected=-1;
			list.clearSelection();			
			return;
		}
		lastselected=selectedElement; 
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {	

	}
}

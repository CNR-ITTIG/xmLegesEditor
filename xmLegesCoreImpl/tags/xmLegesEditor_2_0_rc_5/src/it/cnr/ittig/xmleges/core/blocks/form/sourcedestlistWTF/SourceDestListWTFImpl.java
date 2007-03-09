package it.cnr.ittig.xmleges.core.blocks.form.sourcedestlistWTF;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.sourcedestlistWTF.SourceDestListWTF;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextField;

/**
 * <h1>Implementazione del servizio <code>form.sourcedestlistWTF.SourceDestListWTF</code>.</h1>
 * <h1>Componente grafica che gestisce due JList, la lista di sinistra serve da origine
 * dati, mentre la lista di destra serve da destinazione. Gli elementi appartenenti alla
 * lista sorgente possono essere aggiunti alla lista destinazione attraverso un apposito
 * pulsante</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.util.ui.UtilUI:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
 * 
 * @see
 * @version 1.0
 * @author Lorenzo Sarti
 */
public class SourceDestListWTFImpl implements SourceDestListWTF, Loggable, Serviceable, Configurable, Initializable {
	Logger logger;

	Form form;

	UtilUI utilUi;

	JList sourceList;

	JList destList;

	AbstractButton addButton;

	AbstractButton remButton;

	JTextField sourcetxtfield;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		logger.info("form:" + form.toString());
		utilUi = (UtilUI) serviceManager.lookup(UtilUI.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("SourceDestListWTF.jfrm"));
		
		//TODO verificare se questo HELP serve
		form.setHelpKey("help.contents.form.sourcedestlistwtf");
		
		sourceList = (JList) form.getComponentByName("editor.forms.sourcedestlist.list.sourcelist");
		destList = (JList) form.getComponentByName("editor.forms.sourcedestlist.list.destlist");
		sourcetxtfield = (JTextField) form.getComponentByName("editor.forms.sourcedestlistWTF.txtfield.sourcetxtfield");
		addButton = (AbstractButton) form.getComponentByName("editor.forms.sourcedestlist.button.add");
		remButton = (AbstractButton) form.getComponentByName("editor.forms.sourcedestlist.button.remove");
		// addButton.setAction(utilUi.applyI18n("editor.forms.sourcelist.button.add", new
		// AddAction()));
		// remButton.setAction(utilUi.applyI18n("editor.forms.sourcelist.button.remove",new
		// RemAction()));
	}

	// //////////////////////////////////////////////////// CommonForm Interface
	public Component getAsComponent() {
		return form.getAsComponent();
	}

	// //////////////////////////////////////////////// SourceDestList Interface
	public Vector getDestination() {
		java.util.Vector destlistVector = new java.util.Vector();
		for (int i = 0; i < destList.getModel().getSize(); i++)
			destlistVector.add(destList.getModel().getElementAt(i));
		return destlistVector;

	}

	public void setSource(Vector source) {
		DefaultListModel listmodel = new DefaultListModel();
		for (int i = 0; i < source.size(); i++)
			listmodel.addElement(source.get(i));
		sourceList.setModel(listmodel);

	}

	private void addDestElement(Object element) {
		((DefaultListModel) destList.getModel()).addElement(element);
	}

	protected void addDestElements(Object[] elements) {
		for (int i = 0; i < elements.length; i++) {
			addDestElement(elements[i]);
			((DefaultListModel) sourceList.getModel()).removeElement(elements[i]);
		}

	}

	protected void addSourceElement(Object element) {
		((DefaultListModel) sourceList.getModel()).addElement(element);
	}

	protected void removeDestElements() {
		Object[] selectedValues = destList.getSelectedValues();
		for (int i = 0; i < selectedValues.length; i++) {
			((DefaultListModel) destList.getModel()).removeElement(selectedValues[i]);
			addSourceElement(selectedValues[i]);
		}
	}

	protected class AddAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			addDestElements(sourceList.getSelectedValues());
			if (!sourcetxtfield.getText().equals("")) {
				addDestElement(sourcetxtfield.getText());
				sourcetxtfield.setText("");
			}

		}
	}

	protected class RemAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			removeDestElements();
		}

	}
}

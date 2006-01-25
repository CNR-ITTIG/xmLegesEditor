package it.cnr.ittig.xmleges.core.blocks.form.table;

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
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldEditor;
import it.cnr.ittig.xmleges.core.services.form.table.Table;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JTable;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.form.table.Table</code>.</h1>
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
 * <dd>2003 - 2005</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author Lorenzo Pasquinelli
 */
public class TableImpl implements Table, Loggable, Serviceable, Configurable, Initializable {

	Logger logger;

	Form form;

	UtilUI utilUi;

	JTable jtable;

	AbstractButton addButton;

	AbstractButton remButton;

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
		form.setMainComponent(getClass().getResourceAsStream("Table.jfrm"));
		form.setSize(350, 250);
		form.setName("editor.form.meta.descrittori");

		jtable = (JTable) form.getComponentByName("editor.forms.table.table");
		addButton = (AbstractButton) form.getComponentByName("editor.forms.table.button.add");
		remButton = (AbstractButton) form.getComponentByName("editor.forms.table.button.remove");
		addButton.setAction(utilUi.applyI18n("editor.forms.table.button.add", new AddAction()));
		remButton.setAction(utilUi.applyI18n("editor.forms.table.button.remove", new RemAction()));
	}

	// //////////////////////////////////////////////////// CommonForm Interface
	public Component getAsComponent() {
		return form.getAsComponent();
	}

	// //////////////////////////////////////////////// SourceDestList Interface
	public void setEditor(ListTextFieldEditor listEditor) {
		// TODO
	}

	public void setSource(Vector source) {
	}

	public Vector getDestination() {
		return null;
	}

	/*
	 * Metodo che aggiunge un nuovo elemento nella tabella.
	 */
	private void addRow(Object element) {
		// TODO
	}

	private void removeRow(Object element) {
		// TODO
	}

	protected class AddAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			// Invoco il metodo di aggiunta di un elemento
			// addRow();
		}
	}

	protected class RemAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			// Invoco il metodo di rimozione dell'elemento
			// removeRow();
		}

	}
}

package it.cnr.ittig.xmleges.editor.blocks.form.ndr;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.editor.services.form.ndr.NdrForm;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.ndr.NdrForm</code>.</h1>
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class NdrFormImpl implements NdrForm, ListSelectionListener, CaretListener, Loggable, Serviceable, Initializable {
	Logger logger;

	Form form;

	UtilUI utilui;

	UtilMsg utilmsg;

	JList lista;

	JRadioButton cardinale;

	JRadioButton romano;

	JRadioButton letterale;

	JTextArea testo;

	boolean isOKclicked;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		logger.debug("Avvio servizi");
		form = (Form) serviceManager.lookup(Form.class);
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		logger.debug("Fine attivazione servizi");
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("Ndr.jfrm"));
		form.setSize(300, 400);
		form.setName("editor.form.ndr");
		
		form.setHelpKey("help.contents.form.ndr");
		
		lista = (JList) form.getComponentByName("editor.form.ndr.lista");
		cardinale = (JRadioButton) form.getComponentByName("editor.form.ndr.radio.cardinale");
		romano = (JRadioButton) form.getComponentByName("editor.form.ndr.radio.romano");
		letterale = (JRadioButton) form.getComponentByName("editor.form.ndr.radio.letterale");
		testo = (JTextArea) form.getComponentByName("editor.form.ndr.testo");
		form.addCutCopyPastePopupMenu(testo);
		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(cardinale);
		grupporadio.add(romano);
		grupporadio.add(letterale);
		lista.addListSelectionListener(this);
		testo.addCaretListener(this);
	}

	public boolean openForm(String textSelected, Object[] ndrs) {
		if (!form.hasMainComponent())
			try {
				initialize();
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
		DefaultListModel model = new DefaultListModel();
		for (int i = 0; i < ndrs.length; i++)
			model.addElement(ndrs[i]);
		lista.setModel(model);
		if (textSelected != null)
			testo.setText(textSelected);
		else
			testo.setText("");
		form.showDialog();
		return form.isOk();
	}

	public Object getNdrSelezionato() {
		return lista.getSelectedValue();
	}

	public String getTestoNota() {
		return testo.getText();
	}

	public void valueChanged(ListSelectionEvent e) {
		if (lista.getSelectedIndex() != -1)
			testo.setText("");
	}

	public void caretUpdate(CaretEvent e) {
		if (testo.getText().length() > 0 && lista.getSelectedIndex() != -1)
			lista.removeSelectionInterval(lista.getSelectedIndex(), lista.getSelectedIndex());
	}

	public boolean isCardinale() {
		return (cardinale.isSelected());
	}

	public boolean isLetterale() {
		return (letterale.isSelected());
	}

	public boolean isRomano() {
		return (romano.isSelected());
	}
}

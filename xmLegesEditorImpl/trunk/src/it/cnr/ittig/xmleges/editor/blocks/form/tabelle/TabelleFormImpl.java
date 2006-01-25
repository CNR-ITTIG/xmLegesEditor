package it.cnr.ittig.xmleges.editor.blocks.form.tabelle;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.editor.services.form.tabelle.TabelleForm;

import javax.swing.AbstractButton;
import javax.swing.JSpinner;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmlegis.editor.services.form.tabelle</code>.</h1>
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
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class TabelleFormImpl implements TabelleForm, Loggable, Serviceable {
	Logger logger;

	Form form;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("Tabelle.jfrm"));
		form.setName("editor.form.tabelle");
		((JSpinner) form.getComponentByName("editor.form.tabelle.rows")).setValue(new Integer(3));
		((JSpinner) form.getComponentByName("editor.form.tabelle.cols")).setValue(new Integer(3));
	}

	// /////////////////////////////////////////////////// TabelleForm Interface
	public boolean openForm() {
		if (!form.hasMainComponent())
			try {
				initialize();
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
		form.showDialog();
		return form.isOk();
	}

	public boolean hasCaption() {
		return ((AbstractButton) form.getComponentByName("editor.form.tabelle.caption")).isSelected();
	}

	public boolean hasHead() {
		return ((AbstractButton) form.getComponentByName("editor.form.tabelle.head")).isSelected();
	}

	public boolean hasFoot() {
		return ((AbstractButton) form.getComponentByName("editor.form.tabelle.foot")).isSelected();

	}

	public int getRows() {
		Object val = ((JSpinner) form.getComponentByName("editor.form.tabelle.rows")).getValue();
		return ((Integer) val).intValue();
	}

	public int getCols() {
		Object val = ((JSpinner) form.getComponentByName("editor.form.tabelle.cols")).getValue();
		return ((Integer) val).intValue();
	}

}

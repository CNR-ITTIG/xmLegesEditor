package it.cnr.ittig.xmleges.editor.blocks.form.urn;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.newrinvii.NewRinviiForm;
import it.cnr.ittig.xmleges.editor.services.form.urn.UrnForm;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.urn.UrnForm</code>.</h1>
 * <h1>Permette l'inserimento di un annesso</h1>
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
 * @see
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class UrnFormImpl implements UrnForm, CaretListener, Loggable, Serviceable, Initializable {

	Logger logger;

	Form form;

	NewRinviiForm rinviiForm;

	JTextField textField;

	Color normalColor;

	Urn urn = new Urn();

	AbstractButton openFormBtn;

	boolean partizioni = true;

	boolean annessi = true;

	boolean citati = true;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		rinviiForm = (NewRinviiForm) serviceManager.lookup(NewRinviiForm.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("Urn.jfrm"));
		textField = (JTextField) form.getComponentByName("editor.form.urn.urn");
		textField.addCaretListener(this);
		normalColor = textField.getForeground();
		openFormBtn = (AbstractButton) form.getComponentByName("editor.form.urn.modifica");
		openFormBtn.setAction(new UrnFormAction());
	}

	// /////////////////////////////////////////////////////// UrnForm Interface
	public Component getAsComponent() {
		return form.getAsComponent();
	}

	public void setEnabled(boolean enabled) {
		textField.setEnabled(enabled);
		openFormBtn.setEnabled(enabled);
	}

	public void setPartizioni(boolean partizioni) {
		this.partizioni = partizioni;
	}

	public boolean isPartizioni() {
		return this.partizioni;
	}

	public void setAnnessi(boolean annessi) {
		this.annessi = annessi;
	}

	public boolean isAnnessi() {
		return this.annessi;
	}

	public void setAttiGiaCitati(boolean citati) {
		this.citati = citati;
	}

	public boolean isAttiGiaCitati() {
		return this.citati;
	}

	public void setUrn(Urn urn) {
		try {
			this.urn.parseUrn(urn.toString());
			textField.setText(urn.toString());
			updateTextFieldColor();
		} catch (ParseException e) {
			this.urn = null;
			textField.setText("");
		}
	}

	public Urn getUrn() {
		try {
			return new Urn(this.urn.toString());
		} catch (ParseException e) {
			return null;
		}
	}

	public void setOpenFormVisible(boolean visible) {
		openFormBtn.setVisible(visible);
	}

	public void openForm() {
		openForm(partizioni, annessi, citati);
	}

	public void openForm(boolean allowPartizioni, boolean allowAnnessi, boolean allowAttigiacitati) {
		if (rinviiForm.openForm(urn.toString(), false, allowPartizioni, allowAnnessi, allowAttigiacitati)) {
			Vector v = rinviiForm.getUrn();
			if (v.size() > 0) {
				urn = (Urn) v.get(0);
				textField.setText(urn.toString());
				updateTextFieldColor();
			}
		}
	}

	protected void updateTextFieldColor() {
		textField.setForeground(urn.isValid() ? normalColor : Color.RED);
	}

	// ///////////////////////////////////////////////// CaretListener Interface
	public void caretUpdate(CaretEvent e) {
		try {
			urn.parseUrn(textField.getText());
		} catch (ParseException exc) {
		}
		updateTextFieldColor();
	}

	protected class UrnFormAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			openForm();
		}
	}

}

package it.cnr.ittig.xmleges.editor.blocks.form.meta.urn;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextField;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldEditor;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.form.meta.urn.UrnDocumentoForm;
import it.cnr.ittig.xmleges.editor.services.form.urn.UrnForm;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.meta.urn.UrnDocumentoForm</code>.
 * </h1>
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class UrnDocumentoFormImpl implements UrnDocumentoForm, Initializable, Serviceable, ActionListener, FormVerifier {

	Form form;

	UrnForm urnForm;

	ListTextField listtextfield;

	UrnListTextFieldEditor urnEditor;

	Urn[] urns;

	String errorMessage = "";

	/**
	 * Editor per il ListTextField con la lista delle urn
	 */
	private class UrnListTextFieldEditor implements ListTextFieldEditor {
		UrnForm urnFormEdit;

		String errorMessage = "";

		Urn urnBase = new Urn();

		public UrnListTextFieldEditor(UrnForm form) {
			this.urnFormEdit = form;
		}

		public void setUrnBase(Urn urnBase) {
			this.urnBase = urnBase;
		}

		public Component getAsComponent() {
			return urnFormEdit.getAsComponent();
		}

		public Object getElement() {
			if (!checkData())
				return null;
			else
				return urnFormEdit.getUrn();
		}

		public void setElement(Object object) {
			this.urnFormEdit.setUrn((Urn) object);
		}

		public void clearFields() {
			if(listtextfield.getSelectedItem()==null){
				this.urnFormEdit.setUrn(new Urn());
			}else{
				setElement(listtextfield.getSelectedItem());
			}
			

						
//			this.urnFormEdit.setUrn(urnBase);
		}

		public boolean checkData() {
			boolean ret = this.urnFormEdit.getUrn().isValid();
			if (!ret)
				errorMessage = "editor.form.meta.urn.error";
			return ret;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public Dimension getPreferredSize() {
			return new Dimension(500, 150);
		}
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		form.setName("editor.form.meta.urn");
		listtextfield = (ListTextField) serviceManager.lookup(ListTextField.class);
		urnForm = (UrnForm) serviceManager.lookup(UrnForm.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		urnForm.setAnnessi(false);
		urnForm.setPartizioni(false);
		urnForm.setAttiGiaCitati(false);
		urnEditor = new UrnListTextFieldEditor(urnForm);
		listtextfield.setEditor(urnEditor);

		form.setMainComponent(getClass().getResourceAsStream("UrnDocumento.jfrm"));
		form.replaceComponent("editor.meta.urn.list", listtextfield.getAsComponent());
		form.addFormVerifier(this);
		
		form.setHelpKey("help.contents.form.urndocumento");
	}

	public Urn[] getUrn() {
		Urn[] ret = new Urn[this.listtextfield.getListElements().size()];
		this.listtextfield.getListElements().toArray(ret);
		return ret;
	}

	public void setUrn(Urn[] urn) {

		if (urn != null && urn.length > 0)
			urnEditor.setUrnBase(urn[0]);

		Vector v = new Vector();
		if (urn != null) {
			for (int i = 0; i < urn.length; i++) {
				v.add(urn[i]);
			}
		}
		this.listtextfield.setListElements(v);
	}

	public void actionPerformed(ActionEvent e) {
	}

	// ////////////////////////////////////////////// MetaDescrittoriForm
	// Interface

	public boolean openForm(Urn[] urn) {
		this.setUrn(urn);
		return this.openForm();
	}

	public boolean openForm() {
		form.setSize(540, 300);
		form.showDialog();
		return form.isOk();
	}

	// ////////////////////////////////////////////// FormVerifier Interface
	public boolean verifyForm() {
		return true;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}

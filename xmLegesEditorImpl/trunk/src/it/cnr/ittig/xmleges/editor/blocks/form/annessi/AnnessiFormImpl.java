package it.cnr.ittig.xmleges.editor.blocks.form.annessi;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.editor.services.form.annessi.AnnessiForm;
import it.cnr.ittig.xmleges.editor.services.form.filenew.FileNewForm;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.Provvedimenti;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.annessi.AnnessiForm</code>.</h1>
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
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:sarti@dii.unisi.it">Lorenzo Sarti </a>
 */
public class AnnessiFormImpl implements AnnessiForm, Loggable, Serviceable, Initializable, ActionListener {

	Logger logger;

	Form form;

	UtilMsg utilMsg;

	FileNewForm fileNewForm;

	DtdRulesManager dtdRulesManager;

	JRadioButton internoTemplate;

	JRadioButton internoFile;

	JRadioButton esterno;

	JTextField txtDenominazione;

	JTextField txtTitolo;

	JTextField txtPreambolo;

	JTextField txtNomeFile;

	JLabel labelNomeFile;

	JFileChooser fileChooser;

	JButton btnApriFile;

	File selectedFile;

	String nomeFile = "";

	Provvedimenti provvedimenti;

	DocumentManager documentManager;

	boolean isOKclicked;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		fileNewForm = (FileNewForm) serviceManager.lookup(FileNewForm.class);
		provvedimenti = (Provvedimenti) serviceManager.lookup(Provvedimenti.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("Annessi.jfrm"));
		form.setSize(550, 250);
		form.setName("editor.form.annessi");
		
		form.setHelpKey("help.contents.form.annessi");
		
		internoTemplate = (JRadioButton) form.getComponentByName("editor.form.annessi.radio.internotemplate");
		internoFile = (JRadioButton) form.getComponentByName("editor.form.annessi.radio.internofile");
		esterno = (JRadioButton) form.getComponentByName("editor.form.annessi.radio.esterno");
		labelNomeFile = (JLabel) form.getComponentByName("editor.form.annessi.label.nomefile");
		txtDenominazione = (JTextField) form.getComponentByName("editor.form.annessi.textfield.denominazione");
		form.addCutCopyPastePopupMenu(txtDenominazione);
		txtTitolo = (JTextField) form.getComponentByName("editor.form.annessi.textfield.titolo");
		form.addCutCopyPastePopupMenu(txtTitolo);
		txtPreambolo = (JTextField) form.getComponentByName("editor.form.annessi.textfield.preambolo");
		form.addCutCopyPastePopupMenu(txtPreambolo);
		txtNomeFile = (JTextField) form.getComponentByName("editor.form.annessi.textfield.nomefile");
		form.addCutCopyPastePopupMenu(txtNomeFile);
		btnApriFile = (JButton) form.getComponentByName("editor.form.annessi.button.aprifile");
		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(internoTemplate);
		grupporadio.add(internoFile);
		grupporadio.add(esterno);
		internoTemplate.addActionListener(this);
		internoFile.addActionListener(this);
		esterno.addActionListener(this);
		esterno.setSelected(true);
		manageTxtNomeFile(false);
		btnApriFile.setAction(new ApriAction());
	}

	public void openForm() {
		txtDenominazione.setText("");
		txtTitolo.setText("");
		txtPreambolo.setText("");
		txtNomeFile.setText("");
		internoFile.setEnabled(canAnnessoInterno());
		internoTemplate.setEnabled(canAnnessoInterno());
		if (!canAnnessoInterno()) {
			manageTxtNomeFile(false);
		}

		form.showDialog();
		if (form.isOk()) {
			isOKclicked = true;
			if (txtDenominazione.getText().equals("")) {
				utilMsg.msgError("editor.form.annessi.msg.error.denominazione");
				logger.debug("Annullato l'inserimento annessi - manca la denominazione");
				openForm();
				// form.showDialog();
			}
			if (isInternoTemplate() && txtNomeFile.getText().equals("")) {
				utilMsg.msgError("editor.form.annessi.msg.error.template.nomefile");
				logger.debug("Annullato l'inserimento annessi - manca il nome del template");
				openForm();
				// form.showDialog();
			}
			if (isInternoFile() && txtNomeFile.getText().equals("")) {
				utilMsg.msgError("editor.form.annessi.msg.error.file.nomefile");
				logger.debug("Annullato l'inserimento annessi - manca il nome del file");
				// form.showDialog();
				openForm();
			}
		} else
			isOKclicked = false;

	}

	public String getDenominazione() {
		return txtDenominazione.getText();
	}

	public String getPreambolo() {
		return txtPreambolo.getText();
	}

	public String getTemplate() {
		return nomeFile;
	}

	public String getTitolo() {
		return txtTitolo.getText();
	}

	public boolean isEsterno() {
		return esterno.isSelected();
	}

	public boolean isInternoFile() {
		return internoFile.isSelected();
	}

	public boolean isInternoTemplate() {
		return internoTemplate.isSelected();
	}

	protected class ApriAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (isInternoFile()) {
				fileChooser = new JFileChooser();
				// filechooser.setFileFilter(new RegexpFileFilter("Xml
				// File","*.xml"));
				fileChooser.showOpenDialog(null);
				nomeFile = fileChooser.getSelectedFile().getAbsolutePath();
				txtNomeFile.setText(nomeFile);
				selectedFile = fileChooser.getSelectedFile();
			} else if (isInternoTemplate()) {
				fileNewForm.openForm(documentManager.getDtdName());
				if (fileNewForm.isOKClicked()) {
					nomeFile = fileNewForm.getSelectedTemplate();
					txtNomeFile.setText(fileNewForm.getSelectedProvvedimento());
				}
			}

		}

	}

	private void manageTxtNomeFile(boolean visible) {
		txtNomeFile.setVisible(visible);
		labelNomeFile.setVisible(visible);
		btnApriFile.setVisible(visible);
		txtNomeFile.setText("");
	}

	private boolean canAnnessoInterno() {
		Node annesso = (Node) documentManager.getDocumentAsDom().createElement("annesso");
		try {
			return ((dtdRulesManager.queryAppendable(annesso)).contains("DocumentoNIR"));
		} catch (DtdRulesManagerException e) {
			return false;
		}
	}

	public void actionPerformed(ActionEvent e) {
		if ((e.getSource().equals(internoTemplate)) || (e.getSource().equals(internoFile)))
			manageTxtNomeFile(true);
		else if (e.getSource().equals(esterno))
			manageTxtNomeFile(false);

	}

	public boolean isOKClicked() {

		return isOKclicked;
	}

	public File getSelectedFile() {
		return selectedFile;
	}
}

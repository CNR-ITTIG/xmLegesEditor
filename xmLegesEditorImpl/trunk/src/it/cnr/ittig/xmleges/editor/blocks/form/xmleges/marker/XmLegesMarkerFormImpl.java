package it.cnr.ittig.xmleges.editor.blocks.form.xmleges.marker;

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
import it.cnr.ittig.xmleges.core.services.form.filetextfield.FileTextField;
import it.cnr.ittig.xmleges.core.services.form.filetextfield.FileTextFieldListener;
import it.cnr.ittig.xmleges.core.services.mswordconverter.MSWordConverter;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.file.RegexpFileFilter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.form.xmleges.marker.XmLegesMarkerForm;
import it.cnr.ittig.xmleges.editor.services.xmleges.marker.XmLegesMarker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.xmleges.marker.XmLegesMarkerForm</code>.
 * </h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>Form</li>
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
public class XmLegesMarkerFormImpl implements XmLegesMarkerForm, FileTextFieldListener, ActionListener, Loggable, Serviceable, Configurable, Initializable {
	Logger logger;

	Form form;

	XmLegesMarker parser;

	FileTextField fileTextField;

	MSWordConverter wordConverter;

	UtilMsg utilMsg;

	String parserResult;

	boolean parseOk = false;

	// JComboBox tipoInput;

	JComboBox tipoDtd;

	JComboBox tipoDoc;

	JTextField tipoDocAltro;

	JComboBox tipoComma;

	JComboBox tipoRubrica;

	JCheckBox senquenza;

	JComboBox encoding;

	// JComboBox loggerLevel;

	JTabbedPane tabbedPane;

	JTextArea sorgente;

	JTextArea risultati;

	JButton analizza;

	DefaultComboBoxModel encodingModel = new DefaultComboBoxModel();

	File wordFileConv;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		parser = (XmLegesMarker) serviceManager.lookup(XmLegesMarker.class);
		fileTextField = (FileTextField) serviceManager.lookup(FileTextField.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		wordConverter = (MSWordConverter) serviceManager.lookup(MSWordConverter.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		if (configuration != null) {
			Configuration cs[] = configuration.getChildren();
			for (int i = 0; i < cs.length; i++)
				if ("encoding".equals(cs[i].getName()))
					encodingModel.addElement(cs[i].getValue());
		}
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("XmLegesMarker.jfrm"));
		form.setSize(700, 550);
		form.setName("editor.form.xmleges.marker");

		form.replaceComponent("editor.form.xmleges.marker.file", fileTextField.getAsComponent());
		fileTextField.addFileTextFieldListener(this);
		fileTextField.setFileFilter(new RegexpFileFilter("doc, html, txt", ".*\\.(doc|html?|txt)"));
		tipoDtd = (JComboBox) form.getComponentByName("editor.form.xmleges.marker.tipodtd");
		tipoDtd.setModel(new DefaultComboBoxModel(XmLegesMarker.TIPO_DTD));
		tipoDoc = (JComboBox) form.getComponentByName("editor.form.xmleges.marker.tipodoc");
		tipoDoc.setModel(new DefaultComboBoxModel(XmLegesMarker.TIPO_DOC));
		tipoDoc.addActionListener(this);
		popolaTipoDtd();
		tipoDocAltro = (JTextField) form.getComponentByName("editor.form.xmleges.marker.altro");
		tipoComma = (JComboBox) form.getComponentByName("editor.form.xmleges.marker.tipocomma");
		tipoComma.setModel(new DefaultComboBoxModel(XmLegesMarker.TIPO_COMMA));
		tipoRubrica = (JComboBox) form.getComponentByName("editor.form.xmleges.marker.tiporubrica");
		tipoRubrica.setModel(new DefaultComboBoxModel(XmLegesMarker.TIPO_RUBRICA));
		senquenza = (JCheckBox) form.getComponentByName("editor.form.xmleges.marker.sequenza");
		encoding = (JComboBox) form.getComponentByName("editor.form.xmleges.marker.encoding");
		encoding.setModel(encodingModel);
		tabbedPane = (JTabbedPane) form.getComponentByName("editor.form.xmleges.marker.tab");
		sorgente = (JTextArea) form.getComponentByName("editor.form.xmleges.marker.sorgente");
		sorgente.setWrapStyleWord(true);
		risultati = (JTextArea) form.getComponentByName("editor.form.xmleges.marker.risultati");
		risultati.setWrapStyleWord(true);
		JButton analizza;

		analizza = (JButton) form.getComponentByName("editor.form.xmleges.marker.analizza");
		analizza.setAction(new AnalyzeAction());
	}

	// /////////////////////////////////////////////// ParserStruttura Interface
	public boolean parse() {
		form.showDialog();
		return isParseOk();
	}

	public boolean parse(File file) {
		fileTextField.set(file);
		return parse();
	}

	public boolean isParseOk() {
		return parseOk && form.isOk();
	}

	public InputStream getResult() {
		return new ByteArrayInputStream(parserResult.getBytes());
	}

	// ///////////////////////////////////////////////// FileTextField Interface
	public void fileSelected(FileTextField src, File file) {
		try {
			if (UtilFile.hasExtension(file, "doc", false)) {
				wordFileConv = wordConverter.convert(file);
				sorgente.setText(UtilFile.fileToString(wordFileConv));
			} else {
				sorgente.setText(UtilFile.fileToString(file));
			}

		} catch (Exception ex) {
			sorgente.setText("Error");
		}
		tabbedPane.setSelectedIndex(0);
		sorgente.setCaretPosition(0);
	}

	protected class AnalyzeAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			form.setDialogWaiting(true);
			parseOk = false;
			parserResult = "";
			risultati.setText("");
			new Thread() {
				public void run() {
					try {
						if (UtilFile.hasExtension(fileTextField.getFile(), "html", false) || UtilFile.hasExtension(fileTextField.getFile(), "htm", false)) {
							parser.setTipoInput(XmLegesMarker.TIPO_INPUT_VALORE[0]);
						} else {
							parser.setTipoInput(XmLegesMarker.TIPO_INPUT_VALORE[1]);
						}
						parser.setTipoDoc(XmLegesMarker.TIPO_DOC_VALORE[tipoDoc.getSelectedIndex()]);
						parser.setTipoDtd(XmLegesMarker.TIPO_DTD_VALORE[getSelectedDtd()]);
						if ("nir".equalsIgnoreCase(XmLegesMarker.TIPO_DOC_VALORE[tipoDoc.getSelectedIndex()]))
							parser.setTipoDocAltro(tipoDocAltro.getText());
						else
							parser.setTipoDocAltro(null);
						parser.setTipoComma(XmLegesMarker.TIPO_COMMA_VALORE[tipoComma.getSelectedIndex()]);
						parser.setTipoRubrica(XmLegesMarker.TIPO_RUBRICA_VALORE[tipoRubrica.getSelectedIndex()]);
						parser.setControlloSequenza(senquenza.isSelected());
						parser.setEncoding(encoding.getSelectedItem().toString());
						// parser.setLoggerLevel(ParserStruttura.LOGGER_VALORE[loggerLevel.getSelectedIndex()]);
						InputStream res;
						if (UtilFile.hasExtension(fileTextField.getFile(), "doc", false))
							res = parser.parse(wordFileConv);
						else
							res = parser.parse(fileTextField.getFile());
						if (parser.getError() == null) {
							if (res != null) {
								parserResult = UtilFile.inputStreamToString(res);
								risultati.setText(parserResult);
								// tabbedPane.setSelectedIndex(1);
								// TODO I18n
								parseOk = true;
								utilMsg.msgInfo(form.getAsComponent(), "Analisi del file completata.");
							} else
								// TODO I18n
								utilMsg.msgError(form.getAsComponent(), "Errore durante l'esecuzione dell'analizzatore.");
						} else {
							// TODO I18n
							utilMsg.msgError(form.getAsComponent(), "Errore durante l'esecuzione dell'analizzatore:\n" + parser.getError());
						}
					} catch (Exception ex) {
						logger.error(ex.toString(), ex);
					}
					form.setDialogWaiting(false);
				}
			}.start();
		}
	}

	private void popolaTipoDtd(){
		// tipoDoc Disegno di Legge vincola alla scelta della dtd dl (4' item)
		if(tipoDoc.getSelectedItem().toString().toLowerCase().startsWith("disegn")){
			tipoDtd.removeAllItems();
			tipoDtd.addItem(XmLegesMarker.TIPO_DTD[3]);
			tipoDtd.setSelectedIndex(0);
		}
		else if(tipoDoc.getSelectedItem().toString().toLowerCase().indexOf("cnr")!=-1){
			tipoDtd.removeAllItems();
			tipoDtd.addItem(XmLegesMarker.TIPO_DTD[4]);
			tipoDtd.setSelectedIndex(0);
		}
		// per tutti gli altri provvedimenti ...
		else{
			tipoDtd.removeAllItems();
			for(int i=0;i<3;i++)
			   tipoDtd.addItem(XmLegesMarker.TIPO_DTD[i]);
			tipoDtd.setSelectedIndex(0);			
		}
	}
	
	private int getSelectedDtd(){
		if(tipoDoc.getSelectedItem().toString().toLowerCase().startsWith("disegn"))
			return 3;
		else if(tipoDoc.getSelectedItem().toString().toLowerCase().indexOf("cnr")!=-1)
			return 4;
		else
			return tipoDtd.getSelectedIndex();
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(tipoDoc)) {
			popolaTipoDtd();	
		}
		// TODO Auto-generated method stub		
	}
}
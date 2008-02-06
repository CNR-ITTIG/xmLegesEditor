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
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.form.filetextfield.FileTextField;
import it.cnr.ittig.xmleges.core.services.form.filetextfield.FileTextFieldListener;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.mswordconverter.MSWordConverter;
import it.cnr.ittig.xmleges.core.services.pdfconverter.PDFConverter;
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
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class XmLegesMarkerFormImpl implements XmLegesMarkerForm, FileTextFieldListener, ActionListener, Loggable, Serviceable, Configurable, Initializable, FormVerifier {
	Logger logger;

	Form form;

	XmLegesMarker parser;

	FileTextField fileTextField;

	MSWordConverter wordConverter;
	
	PDFConverter pdfConverter;

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
	
	File pdfFileConv;

	I18n i18n;
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
		pdfConverter = (PDFConverter) serviceManager.lookup(PDFConverter.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
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

		form.setHelpKey("help.contents.form.xmlegesmarker");
		
		form.replaceComponent("editor.form.xmleges.marker.file", fileTextField.getAsComponent());
		
		form.addFormVerifier(this);
		
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
			  } else if (UtilFile.hasExtension(file, "pdf", false)) {
				 pdfFileConv = pdfConverter.convert(file, true);	 		    
				 sorgente.setText(UtilFile.fileToString(pdfFileConv)); 
			} else {
				sorgente.setText(UtilFile.fileToString(file));
			}
			
			setComboTipoDoc(getUnknownTipoDoc());
			
		} catch (Exception ex) {
			sorgente.setText("Error");
		}
		tabbedPane.setSelectedIndex(0);
		sorgente.setCaretPosition(0);
	}
	
	private String getUnknownTipoDoc(){
		
		try{
			if (UtilFile.hasExtension(fileTextField.getFile(), "html", false) || UtilFile.hasExtension(fileTextField.getFile(), "htm", false)) {
				parser.setTipoInput(XmLegesMarker.TIPO_INPUT_VALORE[0]);
			} else {
				parser.setTipoInput(XmLegesMarker.TIPO_INPUT_VALORE[1]);
			}
			
		    String unknownTipoDoc = null;
			
			if (UtilFile.hasExtension(fileTextField.getFile(), "doc", false))
				unknownTipoDoc = UtilFile.inputStreamToString(parser.parseAutoTipoDoc(wordFileConv));
			else if  (UtilFile.hasExtension(fileTextField.getFile(), "pdf", false)) 
		 	    unknownTipoDoc = UtilFile.inputStreamToString(parser.parseAutoTipoDoc(pdfFileConv)); 
			else
				unknownTipoDoc = UtilFile.inputStreamToString(parser.parseAutoTipoDoc(fileTextField.getFile()));
			
			return unknownTipoDoc;
		}
		catch(Exception ex){
			logger.error(ex.getMessage(),ex);
		}
		return null;
	}
	

	private void setComboTipoDoc(String unknownTipoDoc){
		
		/**
		 0   Legge
		 1   Legge Costituzionale
		 2   Decreto Legge
		 3   Decreto Legislativo
		 4   Regio Decreto
		 5   Decreto Presidente Repubblica
		 6   Decreto Presidente Repubblica - non numerato
		 7   Decreto Pres. Cons. Ministri
		 8   Decreto Pres. Cons. Ministri - non numerato
		 9   Decreto Ministeriale
		 10   Decreto Ministeriale - non numerato
		 11   Legge Regionale
		 	12	Regolamento Regionale 
		 	13	Regolamento
		 	14	Circolare
		 	15	Provvedimento
		 16   Disegno di Legge
		 17   Documento NIR
		 18   Provvedimento CNR
		 19	  Statuto Comunale	
		 20	  Regolamento Comunale
		 21	  Delibera Comunale
	 */
		
		String comboItem = null;
		
		if(unknownTipoDoc != null){
			unknownTipoDoc = unknownTipoDoc.toLowerCase().trim();
			if(unknownTipoDoc.indexOf("regionale")!=-1 || unknownTipoDoc.indexOf("regione")!=-1 || unknownTipoDoc.indexOf("lr")!=-1 || unknownTipoDoc.indexOf("l.r.")!=-1 || unknownTipoDoc.indexOf("l r")!=-1)
				comboItem = parser.TIPO_DOC[11];
			else if(unknownTipoDoc.startsWith("legge") && unknownTipoDoc.indexOf("costituzionale")==-1)
				comboItem = parser.TIPO_DOC[0];
			else if(unknownTipoDoc.indexOf("costituzionale")!=-1)
				comboItem = parser.TIPO_DOC[1];
			else if((unknownTipoDoc.startsWith("decreto") && unknownTipoDoc.indexOf("legislativo")!=-1) || unknownTipoDoc.indexOf("lgs")!=-1 || unknownTipoDoc.indexOf("dlgs")!=-1)
				comboItem = parser.TIPO_DOC[3];
			else if((unknownTipoDoc.startsWith("decreto") && unknownTipoDoc.indexOf("legge")!=-1) || unknownTipoDoc.indexOf("d.l")!=-1 || unknownTipoDoc.indexOf("dl")!=-1)
				comboItem = parser.TIPO_DOC[2];	
			else if((unknownTipoDoc.indexOf("regio")!=-1))
				comboItem = parser.TIPO_DOC[4];
			else if(unknownTipoDoc.indexOf("repubblica")!=-1 || unknownTipoDoc.indexOf("dpr")!=-1 || unknownTipoDoc.indexOf("d.p.r")!=-1)
				comboItem = parser.TIPO_DOC[5];
			else if(unknownTipoDoc.indexOf("consiglio")!=-1 || unknownTipoDoc.indexOf("pcm")!=-1 || unknownTipoDoc.indexOf("p.c.m")!=-1)
				comboItem = parser.TIPO_DOC[7];
			else if(unknownTipoDoc.indexOf("ministeriale")!=-1 || unknownTipoDoc.indexOf("ministero")!=-1 || unknownTipoDoc.indexOf("dm")!=-1 || unknownTipoDoc.indexOf("d.m")!=-1)
				comboItem = parser.TIPO_DOC[9];
			else if(unknownTipoDoc.indexOf("disegno")!=-1 || unknownTipoDoc.indexOf("ddl")!=-1 || unknownTipoDoc.indexOf("d.d.l.")!=-1)
				comboItem = parser.TIPO_DOC[16];
			else if(unknownTipoDoc.indexOf("provvedimento")!=-1)
				comboItem = parser.TIPO_DOC[18];
			else if(unknownTipoDoc.startsWith("statuto") && unknownTipoDoc.indexOf("comunale")!=-1 || unknownTipoDoc.indexOf("stc")!=-1)
				comboItem = parser.TIPO_DOC[19];
			else if(unknownTipoDoc.startsWith("regolamento") && unknownTipoDoc.indexOf("comunale")!=-1 || unknownTipoDoc.indexOf("regc")!=-1)
				comboItem = parser.TIPO_DOC[20];
			else if(unknownTipoDoc.startsWith("delibera"))
				comboItem = parser.TIPO_DOC[21];
			else 
				comboItem = parser.TIPO_DOC[0];
			
			tipoDoc.setSelectedItem(comboItem);
			
		}
		else
			tipoDoc.setSelectedIndex(0);
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
						if (UtilFile.hasExtension(fileTextField.getFile(), "html", false) 
								|| UtilFile.hasExtension(fileTextField.getFile(), "htm", false)
								|| UtilFile.hasExtension(fileTextField.getFile(), "pdf", false)) {
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
                        else if (UtilFile.hasExtension(fileTextField.getFile(), "pdf", false)) 
                            res = parser.parse(pdfFileConv); 
						else
							res = parser.parse(fileTextField.getFile());
						
						if (parser.getError() == null) {
							if (res != null) {
								parserResult = UtilFile.inputStreamToString(res);
								risultati.setText(parserResult);
								// tabbedPane.setSelectedIndex(1);
								parseOk = true;
								utilMsg.msgInfo("editor.form.xmleges.marker.msg.warning.complete","editor.form.xmleges.marker.text");
							} else
								utilMsg.msgError("editor.form.xmleges.marker.msg.error.errore","editor.form.xmleges.marker.text");
						} else {
							utilMsg.msgError(i18n.getTextFor("xmlegesmarker.errore") + parser.getError(),"editor.form.xmleges.marker.text");
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
			for(int i=0;i<2;i++)
				   tipoDtd.addItem(XmLegesMarker.TIPO_DTD[i]);
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
		else
			return tipoDtd.getSelectedIndex();
	}
	
	public void actionPerformed(ActionEvent e) {
			
		if (e.getSource().equals(tipoDoc)) {
			popolaTipoDtd();	
		}
	}

	public String getErrorMessage() {
		return "editor.form.xmleges.marker.msg.error.analizza";
	}

	public boolean verifyForm() {
		if(null==parserResult || parserResult.length()==0)
			return false;
		return true;
	}
}
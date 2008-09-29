package it.cnr.ittig.xmleges.editor.blocks.action.cambiadtd;

import java.awt.event.ActionEvent;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;

import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.action.cambiadtd.CambiaDtdAction;

public class CambiaDtdActionImpl implements CambiaDtdAction, EventManagerListener, Loggable, Serviceable, Initializable, ErrorHandler {

	Logger logger;
	Form form;
	ActionManager actionManager;
	DocumentManager documentManager;
	EventManager eventManager;
	
	JLabel from;
	JLabel dtdfrom;
	JLabel to;
	JLabel dtdto;
	JTextArea test;
	JButton change;
	JButton verify;
	
	ChangeAction changeAction;
	VerifyAction verifyAction;
	ChangeDtdOpenAction changedtdOpenAction;
	
	TransformerFactory factory = TransformerFactory.newInstance();
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder db;
	
	boolean valido;
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		form = (Form) serviceManager.lookup(Form.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
	}


	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		
		changedtdOpenAction = new ChangeDtdOpenAction();
		actionManager.registerAction("edit.changedtd", changedtdOpenAction);
		changedtdOpenAction.setEnabled(!documentManager.isEmpty());
		
		form.setSize(330, 350);
		form.setName("edit.changedtd.form");
		form.setMainComponent(getClass().getResourceAsStream("CambiaDtd.jfrm"));
		form.setCustomButtons(new String[] { "generic.close" });
		
		form.setHelpKey("help.contents.form.changedtd");
		
		from = (JLabel) form.getComponentByName("editor.form.changedtd.from");
		dtdfrom = (JLabel) form.getComponentByName("editor.form.changedtd.dtdfrom");
		to = (JLabel) form.getComponentByName("editor.form.changedtd.to");
		dtdto = (JLabel) form.getComponentByName("editor.form.changedtd.dtdto");
		test = (JTextArea) form.getComponentByName("editor.form.changedtd.test");				
		changeAction = new ChangeAction();
		change = (JButton) form.getComponentByName("editor.form.changedtd.change");
		change.addActionListener(changeAction);
		change.setEnabled(false);
		verifyAction = new VerifyAction();
		verify = (JButton) form.getComponentByName("editor.form.changedtd.verify");
		verify.addActionListener(verifyAction);
		
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		
		dbf.setValidating(true);
		dbf.setNamespaceAware(true);
		db = dbf.newDocumentBuilder();
		db.setErrorHandler(this);
	}
	
	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		changedtdOpenAction.setEnabled(!documentManager.isEmpty());
	}
	
	protected class ChangeAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			logger.debug("CAMBIO dtd da " + from.getText() + " a " + to.getText());
			
			if (UtilFile.copyFile(UtilFile.getFileFromTemp("tempChangeDtdTo.xml").getAbsolutePath(), documentManager.getSourceName())) {
					test.setText("Cambio effettuato");
					documentManager.openSource(documentManager.getSourceName());
					change.setEnabled(false);
					verify.setEnabled(false);
			}		
			else
				test.setText("Cambio non riuscito");
		}
	}
	
	protected class VerifyAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			
			logger.debug("test cambio dtd da " + from.getText() + " a " + to.getText());
			String encoding;
			if (documentManager.getEncoding() == null) {
				logger.warn("No encoding found. Using default: UTF-8");
				encoding = "UTF-8";
			} else
				encoding = documentManager.getEncoding();
			try {
				valido = true;
				
				//non dovrebbe essere necessario ma senza ha uno stranissimo comportamento
				//Transformer converti = factory.newTransformer();				
				
				String trasformazioneIdentita = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:template match=\"*|@*|comment()|processing-instruction()|text()\"><xsl:copy><xsl:apply-templates select=\"*|@*|comment()|processing-instruction()|text()\"/></xsl:copy></xsl:template></xsl:stylesheet>";
				char [] xslt = new char[trasformazioneIdentita.length()];
				for (int i=0; i<xslt.length; i++)
					xslt[i] = trasformazioneIdentita.charAt(i);
				Transformer converti = factory.newTransformer(new StreamSource(new CharArrayReader(xslt)));
				
				UtilFile.copyFileInTemp(new FileInputStream(documentManager.getSourceName()), "tempChangeDtdFrom.xml");
				
				Source source = new StreamSource(new File(UtilFile.getTempDirName()+ File.separatorChar +"tempChangeDtdFrom.xml"));
				Result dest = new StreamResult(UtilFile.getTempDirName()+ File.separatorChar +"tempChangeDtdTo.xml");
				converti.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdto.getText());
				converti.setOutputProperty(OutputKeys.ENCODING, encoding);
				converti.transform(source,dest);
				db.parse(UtilFile.getFileFromTemp("tempChangeDtdTo.xml"));
				if (valido) { 
					test.setText("Si può effettuare il cambio di DTD");
					logger.debug("Test superato");
				}	
				else
					logger.debug("Test NON superato");
				
				change.setEnabled(valido);
				
			} catch (Exception ex) {
				change.setEnabled(false);
				test.setText(ex.getMessage());
			}
			
			

		}
	}
	
	protected class ChangeDtdOpenAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			verify.setEnabled(true);
			change.setEnabled(false);
			test.setText("");
			dtdfrom.setText("");
			dtdto.setText("");
			dtdfrom.setText(documentManager.getDtdName());
			if (documentManager.isChanged()) {
				test.setText("Bisogna salvare il documento prima di effettuare il cambio di DTD.");
				verify.setEnabled(false);
			}	
			if (dtdfrom.getText().equals("nirflessibile.dtd"))
				dtdto.setText("nircompleto.dtd");
			else 
				if (dtdfrom.getText().equals("nircompleto.dtd"))
					dtdto.setText("nirflessibile.dtd");
				else {
					test.setText("CONVERSIONE NON DISPONIBILE.");
					verify.setEnabled(false);	
				}	
			form.showDialog();
		}
	}
	
	public void error(SAXParseException e) throws SAXException {
		test.setText(test.getText() + "\n" + e.getMessage());	
		valido=false;
	}
	public void fatalError(SAXParseException e) throws SAXException {
		test.setText(test.getText() + "\n" + e.getMessage());	
		valido=false;
	}
	public void warning(SAXParseException e) throws SAXException {}

}

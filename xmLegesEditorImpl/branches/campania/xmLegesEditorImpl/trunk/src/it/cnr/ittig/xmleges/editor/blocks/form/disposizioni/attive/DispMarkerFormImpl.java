package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.attive;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.domwriter.DOMWriter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DispMarkerForm;

import java.io.File;
import java.io.FileInputStream;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code> * Servizio per la visualizzazione della form per la marcatura (tramite marker)
 * di una virgoletta di tipo struttura</code>.
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
 */
public class DispMarkerFormImpl implements DispMarkerForm, Loggable, Serviceable, Initializable {

	Logger logger;
	Form form;
	JLabel testo;
	JTextArea disposizione;
	SelectionManager selectionManager;
	Rinumerazione rinumerazione;
	DocumentManager documentManager;
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("MarkerAttive.jfrm"));
		form.setName("editor.form.disposizioni.marker");
		form.setHelpKey("help.contents.form.markerdisposizioni");

		testo = (JLabel) form.getComponentByName("editor.disposizioni.marker.testo");
		disposizione = (JTextArea) form.getComponentByName("editor.disposizioni.marker.disposizione");
		UtilFile.copyFileInTemp(getClass().getResourceAsStream("nir-marker-virgoletta.xsl"), "nir-marker-virgoletta.xsl");
	}

	public boolean openForm() {
		
		if (!analizza())
			return false;
		form.setSize(420, 400);
		form.showDialog();
		return form.isOk();
	}
	
	private boolean analizza() {

		Node vir = UtilDom.findParentByName(selectionManager.getActiveNode(), "virgolette");
		String id = UtilDom.getAttributeValueAsString(vir, "id");
		DOMWriter domWriter = new DOMWriter();
		domWriter.setCanonical(false);
		domWriter.setFormat(false);
		TransformerFactory factory = TransformerFactory.newInstance();
		try {
			domWriter.setOutput(UtilFile.getTempDirName() + "/mod.html", documentManager.getEncoding());
			domWriter.write(documentManager.getDocumentAsDom());
			Source xsl = new StreamSource(new FileInputStream(UtilFile.getTempDirName()+ File.separatorChar +"nir-marker-virgoletta.xsl"));
			Transformer converti = factory.newTransformer(xsl);	
			Source source = new StreamSource(new File(UtilFile.getTempDirName() + "/mod.html"));
			Result dest = new StreamResult(UtilFile.getTempDirName() + "/modEntita.html");	
			converti.setParameter("idvir", id);
			converti.transform(source,dest);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		
		File commandFile = UtilFile.getFileFromTemp("xmLeges-Marker.exe");
		if (commandFile==null)
			return false;	//non ho l'eseguibile in temp.... per ora mi fermo
		
		UtilFile.setExecPermission(commandFile);
		
		try {
			String toExec = commandFile.toString() + " -i html -f " + UtilFile.getTempDirName() + "/modEntita.html -o " + UtilFile.getTempDirName() + "/mod.xml -Y";
			logger.debug("command=" + toExec);
			Process p = Runtime.getRuntime().exec(toExec);
			p.waitFor();
			//leggo il file di risposta, creo il DOM, estraggo i figli di Articolato
			Document doc = UtilXml.readXML(UtilFile.getTempDirName() + "/mod.xml",false);
			if (doc == null)
				disposizione.setText("");
			else {
				rinumerazione.aggiorna(doc);
				UtilDom.trimTextNode(doc, true);
				Node articolato = (Node) doc.getElementsByTagName("articolato").item(0);
				NodeList figli = articolato.getChildNodes();
				disposizione.setText("");
				for (int i=0; i<figli.getLength(); i++)
					disposizione.setText(disposizione.getText()+UtilDom.domToString(figli.item(i),true,"    "));
			}	
			
			
		} catch (Exception ex) {
			disposizione.setText("");
			logger.error("error", ex);
		}
		return true;
	}

	public void setMeta() {
						
		Node vir = UtilDom.findParentByName(selectionManager.getActiveNode(), "virgolette");
		Document docNuovo = UtilXml.readXML(UtilFile.getTempDirName() + "/mod.xml",false);
		rinumerazione.aggiorna(docNuovo);
		UtilDom.trimTextNode(docNuovo, true);
		Node articolato = (Node) docNuovo.getElementsByTagName("articolato").item(0);
		NodeList figli = articolato.getChildNodes();
		Document doc = documentManager.getDocumentAsDom();
		EditTransaction t = null;
		try {
			t = documentManager.beginEdit();
			UtilDom.removeAllChildren(vir);
			for (int i=0; i<figli.getLength(); i++) {
				Node nuovo = doc.importNode(figli.item(i), true);
				vir.appendChild(nuovo);
			}	
			rinumerazione.aggiorna(doc);
			documentManager.commitEdit(t);
		} catch (Exception ex) {
			documentManager.rollbackEdit(t);
		}
	}
		
}

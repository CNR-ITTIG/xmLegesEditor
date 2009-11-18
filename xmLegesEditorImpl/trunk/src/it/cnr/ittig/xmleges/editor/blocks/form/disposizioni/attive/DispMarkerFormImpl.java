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
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.domwriter.DOMWriter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DispMarkerForm;

import java.io.File;
import java.io.FileInputStream;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
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
	JTextArea disposizione;
	JRadioButton usaMarker;
	JRadioButton usaSelect;
	JLabel etiMarker;
	JLabel etiSelect;
	JComboBox sceltaPartizione;
	
	SelectionManager selectionManager;
	Rinumerazione rinumerazione;
	DocumentManager documentManager;
	UtilRulesManager utilRulesManager;
	
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
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("MarkerAttive.jfrm"));
		form.setName("editor.form.disposizioni.marker");
		form.setHelpKey("help.contents.form.markerdisposizioni");

		disposizione = (JTextArea) form.getComponentByName("editor.disposizioni.marker.disposizione");
		UtilFile.copyFileInTemp(getClass().getResourceAsStream("nir-marker-virgoletta.xsl"), "nir-marker-virgoletta.xsl");
		
		etiMarker = (JLabel) form.getComponentByName("editor.disposizioni.markerattive.testo");
		etiSelect = (JLabel) form.getComponentByName("editor.disposizioni.partizione.testo");
		sceltaPartizione = (JComboBox) form.getComponentByName("editor.disposizioni.partizione.select");
		sceltaPartizione.addItem("libro");
		sceltaPartizione.addItem("parte");
		sceltaPartizione.addItem("titolo");
		sceltaPartizione.addItem("capo");
		sceltaPartizione.addItem("sezione");		
		sceltaPartizione.addItem("articolo");
		sceltaPartizione.addItem("comma");
		sceltaPartizione.addItem("lettera");
		sceltaPartizione.addItem("numero");
		sceltaPartizione.addItem("punto");
		
		usaMarker = (JRadioButton) form.getComponentByName("editor.disposizioni.markerattive");
		usaSelect = (JRadioButton) form.getComponentByName("editor.disposizioni.partizione");
		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(usaMarker);
		grupporadio.add(usaSelect);
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
			return false;
		
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
			//faccio una considerazione empirica sul riconoscimento
			if (disposizione.getText().indexOf("===== IL TESTO DEL PROVVEDIMENTO NON E' ARTICOLATO =====")!=-1)
				usaSelect.setSelected(true);
			else
				usaMarker.setSelected(true);

			
			
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
		
		NodeList figliVir = vir.getChildNodes();		
		try {
			t = documentManager.beginEdit();
			
			if (usaMarker.isSelected()) { //Uso l'output del marker
				UtilDom.removeAllChildren(vir);
				for (int i=0; i<figli.getLength(); i++) {
					Node nuovo = doc.importNode(figli.item(i), true);
					vir.appendChild(nuovo);
				}
			}	
			else { //Creo la partizione suggerita
				Node partizioneSottoArticolo = null; //comma, lettera, numero, punto.
				Node partizioneSopraArticolo = null; //sezione, capo, titolo, parte, libro.
				Node partizioneArticolo = null;
				String partizione = (String) sceltaPartizione.getSelectedItem();
				if ("lettera".equals(partizione))
					partizioneSottoArticolo = utilRulesManager.getNodeTemplate(doc,"el");
				else if ("numero".equals(partizione))					
					partizioneSottoArticolo = utilRulesManager.getNodeTemplate(doc,"en");
				else if ("punto".equals(partizione))
					partizioneSottoArticolo = utilRulesManager.getNodeTemplate(doc,"ep");
				else //compreso il comma
					partizioneSottoArticolo = utilRulesManager.getNodeTemplate(doc,"comma");
				
				//completo la partizione sotto l'Articolo.
				Node num = UtilDom.findRecursiveChild(partizioneSottoArticolo,"num");
				if (num==null) {
					num = utilRulesManager.getNodeTemplate(doc,"num");
					if (partizioneSottoArticolo.getFirstChild()!=null)
						partizioneSottoArticolo.insertBefore(num,partizioneSottoArticolo.getFirstChild());
					else
						partizioneSottoArticolo.appendChild(num);
				}
				Node corpo = UtilDom.findRecursiveChild(partizioneSottoArticolo,"corpo");
				if (corpo==null) {
					corpo = utilRulesManager.getNodeTemplate(doc,"corpo");
					partizioneSottoArticolo.appendChild(corpo);
				}
				
				for (int i=0; i<figliVir.getLength(); i++)
					if (!"h:br".equals(figliVir.item(i).getNodeName()))
						corpo.appendChild(doc.createTextNode(figliVir.item(i).getTextContent()));
					else {
						Node nuovo = doc.createTextNode(" ");
						corpo.appendChild(nuovo);
					}
						
						
						
				UtilDom.mergeTextNodes(corpo, true);
				
				//completo, se indicato, la partizione sopra al comma
				if ("sezione".equals(partizione))					
					partizioneSopraArticolo = utilRulesManager.getNodeTemplate(doc,"sezione");
				else if ("capo".equals(partizione))
					partizioneSopraArticolo = utilRulesManager.getNodeTemplate(doc,"capo");
				else if ("titolo".equals(partizione))					
					partizioneSopraArticolo = utilRulesManager.getNodeTemplate(doc,"titolo");
				else if ("parte".equals(partizione))
					partizioneSopraArticolo = utilRulesManager.getNodeTemplate(doc,"parte");
				else if ("libro".equals(partizione))
					partizioneSopraArticolo = utilRulesManager.getNodeTemplate(doc,"libro");
				
				//creo, se necessario, la partizione articolo
				if (partizioneSopraArticolo!=null || "articolo".equals(partizione)) {
					partizioneArticolo = utilRulesManager.getNodeTemplate(doc,"articolo");
					num = UtilDom.findRecursiveChild(partizioneArticolo,"num");
					if (num==null) {
						num = utilRulesManager.getNodeTemplate(doc,"num");
						partizioneArticolo.appendChild(num);
					}
					Node comma = UtilDom.findRecursiveChild(partizioneArticolo,"comma");
					if (comma!=null) //messo dal template minimale
						partizioneArticolo.replaceChild(partizioneSottoArticolo,comma);
					else
						partizioneArticolo.appendChild(partizioneSottoArticolo);
				}
				if (partizioneSopraArticolo!=null) {
					UtilDom.removeAllChildren(partizioneSopraArticolo);
					Node numA = utilRulesManager.getNodeTemplate(doc,"num");
					partizioneSopraArticolo.appendChild(numA);
					partizioneSopraArticolo.appendChild(partizioneArticolo);
					UtilDom.removeAllChildren(vir);
					vir.appendChild(partizioneSopraArticolo);
				}
				else if (partizioneArticolo!=null) {
					UtilDom.removeAllChildren(vir);
					vir.appendChild(partizioneArticolo);
				} else {
					UtilDom.removeAllChildren(vir);
					vir.appendChild(partizioneSottoArticolo);
				}
			}	
			//modifica il tipo di virgoletta (se non era struttura)
			if ("parola".equals(UtilDom.getAttributeValueAsString(vir, "tipo")))
				UtilDom.setAttributeValue(vir, "tipo", "struttura");

			rinumerazione.aggiorna(doc);
			documentManager.commitEdit(t);
		} catch (Exception ex) {
			documentManager.rollbackEdit(t);
		}
	}


		
}

package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.attive;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.RegexpFileFilter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DispTrasferisciForm;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive</code>.
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
public class DispTrasferisciFormImpl implements DispTrasferisciForm, Loggable, ActionListener, Serviceable, Initializable, ErrorHandler {

	Logger logger;
	UtilMsg utilmsg;
	Form form;
	
	JList listaEventi;
	DefaultListModel listModel = new DefaultListModel();
	JLabel etichetta;
	JButton file;
	JButton repo;
	JButton salta;
	JButton scrivi;
	JLabel messaggio;
	JLabel norma;
	JList trovati;
	DefaultListModel listModel2 = new DefaultListModel();

	DocumentManager documentManager;

	Disposizioni domDisposizioni;

	Vector eventiAttivi = new Vector();
	int correnteEvento;
	int numeroEventiNorma;
	
	Document docModificato;
	File nomeFileModificato;
	String encodingFileModificato;
	Node modifichePassive;
	
	JFileChooser fileChooser;
	boolean valido;
	
	UtilRulesManager utilRulesManager;
	NirUtilUrn nirUtilUrn;
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		domDisposizioni  = (Disposizioni) serviceManager.lookup(Disposizioni.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("DispTrasferisci.jfrm"));
		form.setName("editor.form.disposizioni.trasferisci");
		form.setCustomButtons(new String[] {"generic.close" });
		form.setHelpKey("help.contents.form.disposizionitrasferisci");

		listaEventi = (JList) form.getComponentByName("editor.disposizioni.trasferisci.eventi");
		listaEventi.setModel(listModel);
		etichetta = (JLabel)form.getComponentByName("editor.disposizioni.trasferisci.eti");
		file = (JButton)form.getComponentByName("editor.disposizioni.trasferisci.file");
		repo = (JButton) form.getComponentByName("editor.disposizioni.trasferisci.repo");
		salta = (JButton) form.getComponentByName("editor.disposizioni.trasferisci.salta");
		scrivi = (JButton) form.getComponentByName("editor.disposizioni.trasferisci.scrivi");
		messaggio = (JLabel) form.getComponentByName("editor.disposizioni.trasferisci.messaggio");
		norma = (JLabel) form.getComponentByName("editor.disposizioni.trasferisci.norma");	
		trovati = (JList) form.getComponentByName("editor.disposizioni.trasferisci.trovati");
		trovati.setModel(listModel2);
		file.addActionListener(this);
		repo.addActionListener(this);
		salta.addActionListener(this);
		scrivi.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == salta)
			trasferisciMeta();
		
		if (e.getSource() == file) {
			fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new RegexpFileFilter("XML", ".*\\.xml"));
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				nomeFileModificato = fileChooser.getSelectedFile();
				parsaFile();
			}
		}
		
		if (e.getSource() == scrivi) {
			scriviFile();
			trasferisciMeta();
		}
		
		if (e.getSource() == repo) {
			//da implementare
		}
	}

	public void openForm() {
		popolaMaschera();
		scrivi.setEnabled(false);
		trasferisciMeta();

		form.setSize(420, 330);
		form.showDialog(true);
		
	}		
	
	private void settaMaschera(boolean a) {
		messaggio.setText("");
		norma.setText("");
		file.setEnabled(a);
		repo.setEnabled(false);
		salta.setEnabled(a);
	}
	private void trasferisciMeta() {
		listModel2.clear();
		scrivi.setEnabled(false);
		file.setEnabled(true);
		//repo.setEnabled(true);
		correnteEvento+=numeroEventiNorma+1;
		if (eventiAttivi.size()<=correnteEvento) {
			settaMaschera(false);
			messaggio.setText("Finito");
			return;
		}
		messaggio.setText("Devo trasferire i metadati nella norma");
		ModificaDaTrasferire corrente = (ModificaDaTrasferire) eventiAttivi.get(correnteEvento);
		norma.setText(corrente.getUrn());
		for (int i=correnteEvento; i<eventiAttivi.size(); i++)
			if (((ModificaDaTrasferire)eventiAttivi.get(i)).getUrn().equals(corrente.getUrn()))
				numeroEventiNorma = i;
		int[] selez = new int[numeroEventiNorma-correnteEvento+1];
		for (int i=0; i<selez.length; i++)
			selez[i]=correnteEvento+i;
		listaEventi.setSelectedIndices(selez);
		
	}
	
	private void popolaMaschera() {
		listModel.clear();
		listModel2.clear();
		eventiAttivi.clear();
		scrivi.setEnabled(false);
		Node[] relazioniAttive = UtilDom.getElementsByTagName(documentManager.getDocumentAsDom(), null, "attiva");
		Node[] eventi = UtilDom.getElementsByTagName(documentManager.getDocumentAsDom(), null, "evento");
		if (relazioniAttive!=null) {
			for (int i = 0; i < relazioniAttive.length; i++) {
				String normaDaModificare = UtilDom.getAttributeValueAsString(relazioniAttive[i], "xlink:href");
				String idRelazione = UtilDom.getAttributeValueAsString(relazioniAttive[i], "id");
				String dataEvento = "";
				String idEvento = "";
				for (int j=0; j<eventi.length; j++)
					if (idRelazione.equals(UtilDom.getAttributeValueAsString(eventi[j], "fonte"))) {
						dataEvento = UtilDom.getAttributeValueAsString(eventi[j], "data");
						idEvento = UtilDom.getAttributeValueAsString(eventi[j], "id");
						break;
					}
				dataEvento = UtilDate.normToString(dataEvento);
				ModificaDaTrasferire temp = new ModificaDaTrasferire(normaDaModificare,dataEvento,idEvento);
				if (!listModel.contains(normaDaModificare + " decorrenza " + dataEvento)) {
					boolean inserito = false;
					for (int k=0; k<listModel.getSize(); k++)
						if ((normaDaModificare + " decorrenza " + dataEvento).compareTo((String) listModel.getElementAt(k))<0) {
							listModel.add(k, normaDaModificare + " decorrenza " + dataEvento);
							eventiAttivi.add(k-1, temp);
							inserito = true;
							break;
						}
					if (!inserito) {
						listModel.add(listModel.getSize(), normaDaModificare + " decorrenza " + dataEvento);
						eventiAttivi.add(listModel.getSize()-1, temp);
					}
				}				
			}
			settaMaschera(true);
		}
		else
			settaMaschera(false);
		correnteEvento = -1;
		numeroEventiNorma = 0;
	}
	
	public void parsaFile() {

		//Pulizia form + Parsing del FILE
		file.setEnabled(false);
		//repo.setEnabled(false);
		
		listModel2.clear();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			dbf.setValidating(false);
			dbf.setNamespaceAware(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			UtilFile.copyFileInTemp(new FileInputStream(nomeFileModificato), "normaModificata.xml");
			encodingFileModificato = leggiEncoding();
			docModificato = db.parse(UtilFile.getFileFromTemp("normaModificata.xml"));
			NodeList meta = docModificato.getElementsByTagName("meta");
			NodeList figliMeta = meta.item(0).getChildNodes();
//			Node descrittori=null;
			Node ciclodivita=null;
			Node disposizioni=null;
			for (int z=0; z<figliMeta.getLength(); z++) {
//				if (figliMeta.item(z).getNodeName().equals("descrittori"))
//					descrittori=figliMeta.item(z);
				if (figliMeta.item(z).getNodeName().equals("ciclodivita"))
					ciclodivita=figliMeta.item(z);
				if (figliMeta.item(z).getNodeName().equals("disposizioni"))
					disposizioni=figliMeta.item(z);
			}
			if (disposizioni!=null) {
				NodeList figliDisposizioni = disposizioni.getChildNodes();
				modifichePassive = null;
				for (int z=0; z<figliDisposizioni.getLength(); z++)
					if (figliDisposizioni.item(z).getNodeName().equals("modifichepassive"))
						modifichePassive=figliDisposizioni.item(z);
				if (modifichePassive!=null) {
					NodeList dspModifica = modifichePassive.getChildNodes();
					for (int a=0; a<dspModifica.getLength(); a++) {
						//VISUALIZZO LE DISPOSIZIONE GIA' PRESENTI (per la norma modificante)
						String tipoModifica = null;
						String partizioneModifica = null;
						NodeList figliDspModifica = dspModifica.item(a).getChildNodes();
						for (int b=0; b<figliDspModifica.getLength(); b++) {
							if (figliDspModifica.item(b).getNodeName().equals("dsp:norma"))
								try {
									if(norma.getText().equals(figliDspModifica.item(b).getAttributes().getNamedItem("xlink:href").getNodeValue()))
										tipoModifica=dspModifica.item(a).getNodeName().substring(4);
								} catch (Exception e) {};
							if (figliDspModifica.item(b).getNodeName().equals("dsp:pos")) {
								try {
									partizioneModifica=figliDspModifica.item(b).getAttributes().getNamedItem("xlink:href").getNodeValue().substring(1);
								} catch (Exception e) {};
							}
						}
						if (tipoModifica!=null)
							listModel2.addElement("Disposizione: " + tipoModifica + " di " + partizioneModifica);
					}
				}
				else {
					//aggiungo tag Modifichepassive
					modifichePassive = disposizioni.appendChild(docModificato.importNode(utilRulesManager.getNodeTemplate("modifichepassive"),false)); 
				}
			} else {
				//aggiungo tag Disposizioni e xlink:type="simple"
				disposizioni = meta.item(0).appendChild(docModificato.importNode(utilRulesManager.getNodeTemplate("disposizioni"),false));
				//aggiungo tag Modifichepassive
				modifichePassive = disposizioni.appendChild(docModificato.importNode(utilRulesManager.getNodeTemplate("modifichepassive"),false));
			}
			if (ciclodivita!=null) {
				Node eventi=null;
				Node relazioni=null;
				NodeList figliCiclodivita =ciclodivita.getChildNodes();
				for (int z=0; z<figliCiclodivita.getLength(); z++) {
					if (figliCiclodivita.item(z).getNodeName().equals("eventi"))
						eventi=figliCiclodivita.item(z);
					if (figliCiclodivita.item(z).getNodeName().equals("relazioni"))
						relazioni=figliCiclodivita.item(z);
				}
				if (eventi!=null & relazioni!=null) {
					NodeList figliEventi = eventi.getChildNodes();
					NodeList figliRelazioni = relazioni.getChildNodes();
					String id;
					for (int a=0; a<figliRelazioni.getLength(); a++) {
						//VISUALIZZO GLI EVENTI GIA' PRESENTI (per la norma modificante)
						try {
							id = figliRelazioni.item(a).getAttributes().getNamedItem("id").getNodeValue();
							if (id.startsWith("rp"))
								if(norma.getText().equals(figliRelazioni.item(a).getAttributes().getNamedItem("xlink:href").getNodeValue()))
									for (int b=0; b<figliEventi.getLength(); b++)
										try {
											if (id.equals(figliEventi.item(b).getAttributes().getNamedItem("fonte").getNodeValue())) {
												listModel2.addElement("Evento: " + UtilDate.normToString(figliEventi.item(b).getAttributes().getNamedItem("data").getNodeValue()));
												break;
											}
										} catch (Exception e) {};
						} catch (Exception e) {};
					}
				}
			}
			if (!listModel2.isEmpty())
				listModel2.add(0,"Meta già introdotti dal modificante:\n");
			scrivi.setEnabled(true);
		} catch(Exception ex) {
			System.out.println(ex.getMessage());	
		}						
	}					
				
	public void scriviFile() {
		int corrente = correnteEvento;
		Node[] dspTermine = UtilDom.getElementsByTagName(documentManager.getDocumentAsDom(), null, "dsp:termine");
		try {
			ModificaDaTrasferire daTrasformare = (ModificaDaTrasferire) eventiAttivi.get(corrente);
			String normaCorrente = daTrasformare.getUrn();
			while (normaCorrente.equals(daTrasformare.getUrn())) {
				//TRASFORMO ATT->PASS e aggiungo nei META
				String id = "#"+daTrasformare.getId();
				for (int i=0; i<dspTermine.length; i++)
					if (id.equals(UtilDom.getAttributeValueAsString(dspTermine[i], "da")))	
						modifichePassive.appendChild(docModificato.importNode(daTrasformare.trasforma(dspTermine[i]), true));	
				corrente++;
				daTrasformare = (ModificaDaTrasferire) eventiAttivi.get(corrente);
			}				
			//SALVO IL FILE
			salva(docModificato);
			
			
			
			file.setEnabled(true);
			//repo.setEnabled(true);
			salta.setEnabled(true);
			scrivi.setEnabled(false);
		} catch(Exception ex) {
			System.out.println(ex.getMessage());	
		}						
	}	
	
	private String leggiEncoding() {
		String en = "UTF-8";
		try {
			FileInputStream fis = new FileInputStream(nomeFileModificato);
			InputStreamReader isr=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(isr);
			
			System.out.println("Encoding sarebbe: " + isr.getEncoding());
			String linea = br.readLine();
			br.close();
			isr.close();
			fis.close();
			StringTokenizer st;
			if (linea.indexOf("encoding=")!=-1) {
				linea = linea.substring(linea.indexOf("encoding")+8, linea.length());
				st = new StringTokenizer(linea, "\"");
				if (st.countTokens()>0) {
					st.nextToken();
					en = st.nextToken();
				}
				else {
					st = new StringTokenizer(linea, "'");
					if (st.countTokens()>0) {
						st.nextToken();
						en = st.nextToken();
					}
				}
			} 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("USO encoding: " + en);
		return en;
	}
	
	private void salva(Document document) {
		//Serializzazione del DOM
		OutputFormat format = new OutputFormat(document);
		format.setLineSeparator(LineSeparator.Unix);

		format.setIndenting(true);
		format.setLineWidth(0);
		format.setPreserveSpace(true);
		format.setEncoding(encodingFileModificato);
		FileWriter fw=null;
		try {
			fw = new FileWriter(nomeFileModificato);
			XMLSerializer serializer = new XMLSerializer(fw, format);
			serializer.serialize(document);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			fw.close();
		} catch (IOException e) {}
	}
	

	public void error(SAXParseException e) throws SAXException {
		System.out.println("ERROR: " + e.getMessage());	
		valido=false;
	}
	public void fatalError(SAXParseException e) throws SAXException {
		System.out.println("FATAL: " + e.getMessage());
		valido=false;
	}
	public void warning(SAXParseException e) throws SAXException {
		System.out.println("WARN: " + e.getMessage());
	}

	public class ModificaDaTrasferire {
		
		String urn;
		String dataNormalizzata;
		String id;
		
		public ModificaDaTrasferire(String urn, String dataNormalizzata, String id) {
			this.urn = urn;
			this.dataNormalizzata = dataNormalizzata;
			this.id = id;
		}
		
		public String getUrn() {
			return urn;
		}
	
		public String getData() {
			return dataNormalizzata;
		}
		
		public String getId() {
			return id;
		}
		
		public Node trasforma(Node nodo) {
			//trasformo nodo att in passivo e lo restituisco
			Node disposizioneModificante = nodo.getParentNode();
			//creo dsp:abrogazione/sostituzione/integrazione
			Node disposizioneModificato = utilRulesManager.getNodeTemplate(disposizioneModificante.getNodeName());
			//setto attributi completa e implicita
			UtilDom.setAttributeValue(disposizioneModificato, "completa", UtilDom.getAttributeValueAsString(disposizioneModificante, "completa"));
			UtilDom.setAttributeValue(disposizioneModificato, "implicita", UtilDom.getAttributeValueAsString(disposizioneModificante, "implicita"));
			//creo dsp:pos
			Node normaNode = UtilDom.findRecursiveChild(disposizioneModificante,"dsp:norma");
			Node posNorma = UtilDom.findRecursiveChild(normaNode,"dsp:pos");
			String partizione = UtilDom.getAttributeValueAsString(posNorma, "xlink:href");
			if (partizione.indexOf("#")!=-1)
				partizione = partizione.substring(partizione.indexOf("#"));
			else
				partizione = "";
			
			//è prematuro. anche se avessi un bordo "partizione" non saprei l'ID.
//			Considero partizioni: allegato,libro,parte,titolo,capo,sezione,articolo,comma,lettera,numero,punto	
//			String[] delimitatoriNonPartizioni = {"alinea","coda","periodo","parole","capoverso"};
//			Node bordoNorma = UtilDom.findRecursiveChild(norma,"dsp:subarg");
//			if (bordoNorma!=null) { //cerco il più piccolo bordo di tipo partizione (presupposto che siano ordinati)
//				boolean continua = true;
//				while (continua && bordoNorma!=null) {
//					bordoNorma = UtilDom.findRecursiveChild(bordoNorma,"ittig:bordo");
//					String tempPar = UtilDom.getAttributeValueAsString(posNorma, "tipo");
//					for (int i=0; i<delimitatoriNonPartizioni.length; i++)
//						if (delimitatoriNonPartizioni[i].equals(tempPar)) {
//							continua = false;
//							break;
//						}
//					if (continua)
//						partizione = tempPar;
//				}
//			}
			Node posNodeModificato = UtilDom.findDirectChild(disposizioneModificato, "dsp:pos");
			if (posNodeModificato != null) //è stato inserito dal template minimale
				disposizioneModificato.removeChild(posNodeModificato);
			posNodeModificato = utilRulesManager.getNodeTemplate("dsp:pos");
			disposizioneModificato.appendChild(posNodeModificato);
			UtilDom.setAttributeValue(posNodeModificato, "xlink:href", urn);
			//creo dsp:norma
			Node normaNodeModificato = UtilDom.findDirectChild(disposizioneModificato, "dsp:norma");
			if (normaNodeModificato != null) //è stato inserito dal template minimale
				disposizioneModificato.removeChild(normaNodeModificato);
			normaNodeModificato = utilRulesManager.getNodeTemplate("dsp:norma");
			disposizioneModificato.appendChild(normaNodeModificato);	
			UtilDom.setAttributeValue(normaNodeModificato, "xlink:href", norma.getText());
			//creo dsp:pos figlio di dsp:norma
			Node posNormaNodeModificato = UtilDom.findDirectChild(normaNodeModificato, "dsp:pos");
			if (posNormaNodeModificato != null) //è stato inserito dal template minimale
				normaNodeModificato.removeChild(posNormaNodeModificato);
			posNormaNodeModificato = utilRulesManager.getNodeTemplate("dsp:pos");
			normaNodeModificato.appendChild(posNormaNodeModificato);
			UtilDom.setAttributeValue(posNormaNodeModificato, "xlink:href", urn+partizione);
			//inserisco dsp:subarg e ittig:notavigenza
			Node subargNodeModificato = utilRulesManager.getNodeTemplate("dsp:subarg");
			normaNodeModificato.appendChild(subargNodeModificato);
			//Node notavigenzaNodeModificato = UtilDom.findDirectChild(normaNodeModificato, "ittig:notavigenza");
			Node notavigenzaNodeModificato = documentManager.getDocumentAsDom().createElementNS("http://www.ittig.cnr.it/provvedimenti/2.2", "ittig:notavigenza");
			UtilDom.setIdAttribute(notavigenzaNodeModificato, "");
			String autoNota = "";
			try {
				autoNota = nirUtilUrn.getFormaTestuale(new Urn(urn));
				if (!partizione.equals("")) 
					autoNota = partizione + " " + autoNota;
			} catch (Exception e) {}

			UtilDom.setAttributeValue(notavigenzaNodeModificato, "auto", autoNota);
			subargNodeModificato.appendChild(notavigenzaNodeModificato);
			//inserisco dsp:novella/novellando e ralativo dsp:pos, dove però xlink:href punta al mod di modifica
			Node posNode = UtilDom.findRecursiveChild(disposizioneModificante,"dsp:pos");
			String mod = UtilDom.getAttributeValueAsString(posNode, "xlink:href");
			if ("dsp:integrazione".equals(disposizioneModificante.getNodeName()) || "dsp:sostituzione".equals(disposizioneModificante.getNodeName())) {
				Node novellaNodeModificato = utilRulesManager.getNodeTemplate("dsp:novella");
				Node posNovellaNodeModificato = UtilDom.findDirectChild(normaNodeModificato, "dsp:pos");
				UtilDom.setAttributeValue(posNovellaNodeModificato, "xlink:href", mod);
				novellaNodeModificato.appendChild(posNovellaNodeModificato);
				disposizioneModificato.appendChild(novellaNodeModificato);
			}
			if ("dsp:abrogazione".equals(disposizioneModificante.getNodeName()) || "dsp:sostituzione".equals(disposizioneModificante.getNodeName())) {
				Node novellaNodeModificato = utilRulesManager.getNodeTemplate("dsp:novellando");
				Node posNovellaNodeModificato = UtilDom.findDirectChild(normaNodeModificato, "dsp:pos");
				UtilDom.setAttributeValue(posNovellaNodeModificato, "xlink:href", mod);
				novellaNodeModificato.appendChild(posNovellaNodeModificato);
				disposizioneModificato.appendChild(novellaNodeModificato);
			}			
			return disposizioneModificato;
		}
	}
}


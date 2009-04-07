package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.multivigente;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.domwriter.DOMWriter;
import it.cnr.ittig.xmleges.core.util.file.RegexpFileFilter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.MetaCiclodivita;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.multivigente.CreaMultivigenteForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.multivigente.PosizionamentoManualeForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import org.apache.xerces.dom.DeferredDocumentImpl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
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
 * <h1>Implementazione del <code>servizio per la visualizzazione della form per la creazione
 * del testo multivigente</code>.</h1>
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
public class CreaMultivigenteFormImpl implements CreaMultivigenteForm, Loggable, ActionListener, Serviceable, Initializable, FormClosedListener, FormVerifier {

	Logger logger;
	Form form;
	PosizionamentoManualeForm posizionamentoManuale;
	
	JTabbedPane tabbedPane;
	JEditorPane testoModifica;
	JEditorPane testoOriginale;
	JEditorPane testoModificato;
	JLabel etichetta;
	JLabel messaggio;
	JLabel errore;
	JLabel formatestuale;
	JLabel norma;
	JButton lista;
	JButton file;
	JButton repo;
	JButton cambia;
	JButton scrivi;
	JFileChooser fileChooser;
	JList eventi;
	DefaultListModel listModel = new DefaultListModel();
	
	Rinumerazione rinumerazione;
	DocumentManager documentManager;
	UtilRulesManager utilRulesManager;
	NirUtilUrn nirUtilUrn;
	Disposizioni domDisposizioni;
	
	File attivo;
	File originale;
	Document docAttivo;
	Document docEditor;
	Node nodoModificaCorrente;
	
	Vector nodiModifica;
	int correnteModifica;
	int vecchieModifiche;
	String dataEvento;
	String urnAttivo;
	String urnDocumento;
	Vector dateEventi;
	Vector idEventi;
	Vector disposizioniDaConvertire;
	int correnteEvento;
	int numMessaggiErrore;
	
	String partizione;
	
	TransformerFactory factory = TransformerFactory.newInstance();
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder db;
	
	String nomeTemp = "tempFile.xml";

	MetaCiclodivita ciclodivita;
	SelectionManager selectionManager;
	Evento eventoriginale;
	Evento eventovigore;
	NirUtilDom nirUtilDom;
	
	String[] elencoPartizioni;
	boolean parole;
	String tipoModifica;

	String idNovellando;
	String idNovella;
	Node nodeDisposizione;
	Node nodeNovellando;
	Node virgolettaDaInserire;
	Node virgolettaDaEliminare;
	
	boolean primaModifica;
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		domDisposizioni  = (Disposizioni) serviceManager.lookup(Disposizioni.class);
		ciclodivita = (MetaCiclodivita) serviceManager.lookup(MetaCiclodivita.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		posizionamentoManuale = (PosizionamentoManualeForm) serviceManager.lookup(PosizionamentoManualeForm.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("CreaMultivigente.jfrm"));
		form.setSize(780, 700);
		form.setName("editor.form.disposizioni.multivigente");
		form.setCustomButtons(new String[] {"generic.close" });
		form.setHelpKey("help.contents.form.creamultivigente");
		form.addFormVerifier(this);
		
		tabbedPane = (JTabbedPane) form.getComponentByName("editor.disposizioni.multivigente.tabbed");
		testoModifica = (JEditorPane) form.getComponentByName("editor.disposizioni.multivigente.modifica");
		testoOriginale = (JEditorPane) form.getComponentByName("editor.disposizioni.multivigente.prima");
		testoModificato = (JEditorPane) form.getComponentByName("editor.disposizioni.multivigente.dopo");
		eventi = (JList) form.getComponentByName("editor.disposizioni.multivigente.eventi");
		eventi.setModel(listModel);
		etichetta = (JLabel) form.getComponentByName("editor.disposizioni.multivigente.eti");
		messaggio = (JLabel) form.getComponentByName("editor.disposizioni.multivigente.messaggio");
		errore = (JLabel) form.getComponentByName("editor.disposizioni.multivigente.errore");
		formatestuale = (JLabel) form.getComponentByName("editor.disposizioni.multivigente.formatestuale");
		norma = (JLabel) form.getComponentByName("editor.disposizioni.multivigente.norma");
		lista = (JButton)form.getComponentByName("editor.disposizioni.multivigente.lista");
		file = (JButton)form.getComponentByName("editor.disposizioni.multivigente.file");
		repo = (JButton) form.getComponentByName("editor.disposizioni.multivigente.repo");
		cambia = (JButton) form.getComponentByName("editor.disposizioni.multivigente.cambia");
		scrivi = (JButton) form.getComponentByName("editor.disposizioni.multivigente.scrivi");
		lista.addActionListener(this);
		file.addActionListener(this);
		repo.addActionListener(this);
		cambia.addActionListener(this);
		scrivi.addActionListener(this);
		testoModifica.setEditorKit(new HTMLEditorKit());
		testoOriginale.setEditorKit(new HTMLEditorKit());
		testoModificato.setEditorKit(new HTMLEditorKit());
		UtilFile.copyFileInTemp(getClass().getResourceAsStream("nir-nocss.xsl"), "nir-nocss.xsl");
		elencoPartizioni = new String[]{"atto", "allegato", "libro", "parte", "titolo", "capo", "sezione", "articolo", "comma", "lettera", "numero", "punto"};
	}
	
	private void setMessagge() {
		messaggio.setText("Devo aprire " + nodiModifica.size() + " norme. Devi selezionare la norma (" + (correnteModifica+1) + " di " + nodiModifica.size() + " )");
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == lista) {
			//apro file lista.xml delle modifiche
			fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new RegexpFileFilter("XML", ".*\\.xml"));
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

				//spostare su apertura documento
				urnDocumento = UtilDom.getAttributeValueAsString(UtilDom.getElementsByTagName(docEditor,docEditor,"originale")[0], "xlink:href");
				
				listModel.clear();
				numMessaggiErrore = 0;
				try {
					Document listamodifiche = parsa(fileChooser.getSelectedFile());
					Node[] modifica = UtilDom.getElementsByAttributeValue(listamodifiche,listamodifiche,"urn", urnDocumento);
					if (modifica.length==0)
						settaForm(false, "Nessuna modifica da apportare");
					else {
						nodiModifica = UtilDom.getChildElements(modifica[0]);
						settaForm(false, "");
						correnteModifica = 0;
						settaModifica();
						settaTasti(false,true,false);
						setMessagge();
					}
				} catch (Exception ex) {
					settaForm(false, "Errore nel file aperto");
					messaggio.setText("");
				}
			}
		}
		
		if (e.getSource() == cambia) {
			form.close();
			String nomenodo = "";
			if (virgolettaDaInserire==null)
				if (virgolettaDaEliminare!=null)
					nomenodo = virgolettaDaEliminare.getNodeName();
				else {
					//caso di abrogazione di partizione. Non posso cercare il tipo di nodo nel documento attivo (caso integrazione e sostituzione)
					//Lo deduco dall'ID del rif.
					Node tempPos = UtilDom.findRecursiveChild(UtilDom.findRecursiveChild((Node)disposizioniDaConvertire.get(correnteEvento),"dsp:norma"),"dsp:pos");
					String partizione = UtilDom.getAttributeValueAsString(tempPos,"xlink:href");
					if (partizione.indexOf("#")!=-1) {
						partizione = partizione.substring(1+partizione.indexOf("#"));  //xex  art1-com3-leta (significativo è let)
						StringTokenizer st = new StringTokenizer(partizione, "-");
						String token = null;
						while (st.hasMoreTokens())
							token = st.nextToken();
						if (token.length()>3) { //uso Partizioni NIR
							token=token.substring(0,3);
							if ("lib".equals(token))
								nomenodo = "libro";
							else if ("par".equals(token))
								nomenodo = "parte";
							else if ("tit".equals(token))
								nomenodo = "titolo";
							else if ("cap".equals(token))
								nomenodo = "capo";
							else if ("sez".equals(token))
								nomenodo = "sezione";
							else if ("art".equals(token))
								nomenodo = "articolo";
							else if ("com".equals(token))
								nomenodo = "comma";
							else if ("el".equals(token))
								nomenodo = "let";
							else if ("en".equals(token))
								nomenodo = "num";
							else if ("ep".equals(token))
								nomenodo = "pun";
						}
						else 
							nomenodo = token.toLowerCase();	//non dovrebbe capitare mai
					}
					else {
						nomenodo = UtilDom.getElementsByTagName(docAttivo,docAttivo,"meta")[0].getParentNode().getNodeName(); //Legge, ...
					}
				}
					
			posizionamentoManuale.openForm(this, virgolettaDaInserire,nomenodo);
		}	 
			
		if (e.getSource() == file) {
			if ((attivo = apriFile())!=null) {
				vecchieModifiche = getPosLista();
				docAttivo = parsa(attivo);
				//effettuo un test per vedere se la norma aperta è quella che mi aspettavo
				if (!urnAttivo.equals(UtilDom.getAttributeValueAsString(UtilDom.getElementsByTagName(docAttivo,docAttivo,"originale")[0], "xlink:href"))) {
					settaForm(false, "Il file aperto non è corretto");
					return;
				}
				
				//azzero array e liste
				dateEventi = new Vector();
				idEventi = new Vector();
				disposizioniDaConvertire = new Vector();
				correnteEvento = 0;
				listModel.addElement("Apertura norma: " + urnAttivo);
				//recupero tutti i meta che lavorano su 'urnDocumento'  [ Teoricamente potrebbe essere sbagliato, devo guardare solo all'evento interessato !!!!
				//  													  Ci potrebbero essere altri eventi, di altre norme, che si accavallano a questi eventi. ]

				Node[] relazioniAttive = UtilDom.getElementsByTagName(docAttivo,docAttivo,"attiva");
				Node[] eventiAttivi = UtilDom.getElementsByTagName(docAttivo,docAttivo,"evento");
				Node[] dspTermine = UtilDom.getElementsByTagName(docAttivo,docAttivo,"dsp:termine");
				for (int i=0; i<relazioniAttive.length; i++) {
					String urnPuntata = UtilDom.getAttributeValueAsString(relazioniAttive[i], "xlink:href");
					if (urnDocumento.equals(urnPuntata)) {
						String idRelazione = UtilDom.getAttributeValueAsString(relazioniAttive[i], "id");
						for (int j=0; j<eventiAttivi.length; j++)
							if (idRelazione.equals(UtilDom.getAttributeValueAsString(eventiAttivi[j], "fonte"))) {
								String tempData = UtilDom.getAttributeValueAsString(eventiAttivi[j], "data");
								if (dataEvento.equals(tempData)) {
									String tempId = "#"+UtilDom.getAttributeValueAsString(eventiAttivi[j], "id");
									idEventi.add(tempId);
									dateEventi.add(tempData);
									//colleziono anche i nodi delle disposizioni interessate
									for (int k=0; k<dspTermine.length; k++)
										if (tempId.equals(UtilDom.getAttributeValueAsString(dspTermine[k], "da"))) {
											disposizioniDaConvertire.add(dspTermine[k].getParentNode());
											String tempPart = UtilDom.getAttributeValueAsString(UtilDom.findRecursiveChild(UtilDom.findRecursiveChild(dspTermine[k].getParentNode(),"dsp:norma"),"dsp:pos"),"xlink:href");
											if (tempPart.indexOf("#")==-1)
												tempPart = "intero atto";
											else	
												tempPart = tempPart.substring(tempPart.indexOf("#")+1, tempPart.length());
											String tipo = dspTermine[k].getParentNode().getNodeName();
											tipo = tipo.substring(tipo.indexOf(":")+1, tipo.length()).toUpperCase();
											listModel.addElement("Norma " + (correnteModifica+1) + ": " + tipo + " in data " + UtilDate.normToString(tempData) + " di " + tempPart);
										}
								}
								break;
							}
					}
				}
				if (idEventi.size()==0)	{//il file aperto non contiene nessun evento di modifica (non dovrebbe mai succedere)
					settaForm(false, "Nessun evento trovato");
					numMessaggiErrore++;
				} else {
					settaTasti(false,false,true);
					primaModifica=true;		//cambiare --- implementare la funzione di check sulle modifiche
					while (proponiModificaTesto(null));
				}
			}
		}
		
		if (e.getSource() == scrivi) {
			listModel.setElementAt(((String) listModel.get(getPosLista())) + " --> applicata", getPosLista());
			correnteEvento++;
			settaTasti(false,false,true);
			while (proponiModificaTesto(null));
		}
		
		if (e.getSource() == repo) {
			//da implementare
		}
	}

	private int getPosLista() {
		System.out.println("num Errore " + numMessaggiErrore + " +1 +correnteEvento " + correnteEvento + " + vecchieModifiche "+vecchieModifiche + " = " + (numMessaggiErrore+1+correnteEvento+vecchieModifiche));
		return numMessaggiErrore+1+correnteEvento+vecchieModifiche;
	}
	
	private boolean proponiModificaTesto(Node partizioneIndicata) {

		
		if (partizioneIndicata!=null && scrivi.isEnabled()) { //ho cambiato posizione e avevo già proposto qualcosa => cancella vecchia proposta
			Node corrente = domDisposizioni.doErase(idNovellando,idNovella,nodeDisposizione,nodeNovellando);
			if (corrente!=null)
				selectionManager.setActiveNode(this, corrente);
		}
//-----TEST	(fine/skip)	
		//test per capire quale evento devo applicare
		if (correnteEvento==disposizioniDaConvertire.size()) {
			//ho finito di scorrere le modifiche per il modificante corrente. Testo se ne ho altre norme modificanti da applicare			
			if (correnteModifica==nodiModifica.size()-1) {
				settaTasti(false,false,false);
				settaForm(true, "");
			}
			else {
				correnteModifica++;
				settaModifica();
				settaTasti(false,true,false);
				setMessagge();
			}
			return false;
		}
		
		
//-----FASE1 (processo il Meta, creo Mod/TestoPrima)
		
		eventi.setSelectedIndex(getPosLista());		
		Node nodoMeta = (Node)disposizioniDaConvertire.get(correnteEvento);
		String idMod = UtilDom.getAttributeValueAsString(UtilDom.findRecursiveChild(nodoMeta,"dsp:pos"),"xlink:href").substring(1);
		virgolettaDaInserire=null;
		virgolettaDaEliminare=null;
		Node tempNovella = UtilDom.findRecursiveChild(nodoMeta,"dsp:novella");
		if (tempNovella!=null) {
			String idVirgoletta = UtilDom.getAttributeValueAsString(UtilDom.findRecursiveChild(tempNovella,"dsp:pos"),"xlink:href").substring(1);
			virgolettaDaInserire = UtilDom.getElementsByAttributeValue(docAttivo,docAttivo,"id",idVirgoletta)[0];
		}
		Node tempNovellando = UtilDom.findRecursiveChild(nodoMeta,"dsp:novellando");
		if (tempNovellando!=null) {
			Node temPos = UtilDom.findRecursiveChild(tempNovellando,"dsp:pos");
			if (temPos!=null) { //xex abrogazione di parole
				String idVirgoletta = UtilDom.getAttributeValueAsString(temPos,"xlink:href").substring(1);
				virgolettaDaEliminare = UtilDom.getElementsByAttributeValue(docAttivo,docAttivo,"id",idVirgoletta)[0];
			}
			else  //xex per partizioni
				virgolettaDaEliminare = null; //la lascio a null, tanto se mi serve, me la calcolo in 'cambia partizione'						
		}
		
		//test per capire se la modifica corrente è stata già applicata
		//verifico la presenza dell'evento (visto che ci sono mi calcolo anche un nuovo id)
		Vector idEventiDaTestare = new Vector();
		Node relazioniNode = UtilDom.findRecursiveChild(nirUtilDom.findActiveMeta(docEditor,null),"relazioni");
		NodeList relazioniList = relazioniNode.getChildNodes();
		int maxPassiva=0;
		for (int i = 0; i < relazioniList.getLength(); i++) {
			Node relazioneNode = relazioniList.item(i);
			String nodeName = relazioneNode.getNodeName();
			String id = UtilDom.getAttributeValueAsString(relazioneNode, "id");
//			if ("attiva".equals(nodeName)) {				
//				Integer idValue = Integer.decode(id.substring(2));
//				if (idValue.intValue() > maxPassiva)
//					maxPassiva = idValue.intValue();
//			}
//			if ("passiva".equals(nodeName) && urnAttivo.equals(UtilDom.getAttributeValueAsString(relazioneNode, "xlink:href")))
//					idEventiDaTestare.add(id);
			if ("passiva".equals(nodeName)) {				
				Integer idValue = Integer.decode(id.substring(2));
				if (idValue.intValue() > maxPassiva)
					maxPassiva = idValue.intValue();
				if (urnAttivo.equals(UtilDom.getAttributeValueAsString(relazioneNode, "xlink:href")))
					idEventiDaTestare.add(id);					
			}			
		}
		if (partizioneIndicata==null)
			maxPassiva++;
			
			
		Node eventiNode = UtilDom.findRecursiveChild(nirUtilDom.findActiveMeta(docEditor,null),"eventi");
		int maxEventi = 1+UtilDom.findRecursiveChild(eventiNode,"eventi").getChildNodes().getLength();    //levare findrecursivechild
		
		if (primaModifica) { //implementare un controllo serio
			Vector listaDegliEventi = UtilDom.getChildElements(eventiNode);
			for (int j=0; j<idEventiDaTestare.size(); j++)
				for (int z=0; z<listaDegliEventi.size(); z++)
					if (((String)idEventiDaTestare.get(j)).equals(UtilDom.getAttributeValueAsString((Node)listaDegliEventi.get(z), "fonte"))) 
						if ((dataEvento).equals(UtilDom.getAttributeValueAsString((Node)listaDegliEventi.get(z), "data"))) {
						
							
							if (partizioneIndicata==null) {	
							//per ora mi fermo qui. Ho verificato che esiste già un evento 'identico' nel modificato. Presumo quindi sia già stato modificato.
							//In realtà dovrei prelevare l'attributo ID. Prendere tutte le partizioni che hanno attributo INIZIOVIGORE=id e/o
							//FINEVIGORE=id (a seconda del tipo di modifica che voglio apportare), prelevare ID (della partizione/span) e ricercarlo
							//fra i meta delle disposizioni passive. A questo punto DECIDERE se è stata già applicata oppure no.
								 
								//NO. o questo test lo faccio solo sulla prima modifica di ogni norma modificante, o scendo nel testo.
								
							
							listModel.setElementAt(((String) listModel.get(getPosLista())) + " --> saltata, già applicata", getPosLista());
							correnteEvento++;
							return true;
							}
							
						}
		}
		
		//creo un frammento con il mod per trasformarlo in HTML
		Node nodoMod = docAttivo.createElement("NIR");
		Node nodoModOriginale = UtilDom.getElementsByAttributeValue(docAttivo,docAttivo,"id",idMod)[0];
		nodoModOriginale = nirUtilDom.getContainer(nodoModOriginale);
		nodoMod.appendChild(nodoModOriginale.cloneNode(true));
		

		
		
		//MIGLIORARE: se indico una partizione uso quella (ho l'id, quindi la cerco), altrimenti uso cercaPosizione. 
		Node posizione = null;
		int start = 0; int end=0;
		if (partizioneIndicata==null)
			posizione = cercaPosizione(docEditor, docEditor.importNode(nodoMeta,true));
		else {
				posizione = partizioneIndicata;
				start = posizionamentoManuale.getInizioSelezione();
				end = posizionamentoManuale.getFineSelezione();
				if (start!=end) 
					parole=true;
				else
					parole=false;
		}
		if (posizione!=null) {	//NON PROPONGO LA MODIFICA (faccio vedere in ELSE il testo del MOD)

			scrivi.setEnabled(true);
			//creo un frammento con il 'nodo', PRIMA dell'applicazione della modifica, per trasformarlo in HTML
			Node nodoPrima = docEditor.createElement("NIR");
			nodoPrima.appendChild(posizione.cloneNode(true));
		
		
//-----FASE2 (creo relazione passiva)
		
			//nodi di comodo (buttare...migliorare)
			Node modifica1 = null;
			Node modifica2 = null;
			
			//Da qui effettuo modifiche sul documento. Se non sono confermate vanno poi annullate. !!!!!!!
			EditTransaction t = null;
			try {
				t = documentManager.beginEdit();
				
			if (primaModifica) {	//implementare altro meccanismo				
				//creo la relazione (passiva)
				Node nuovo = utilRulesManager.getNodeTemplate("passiva");
				UtilDom.setAttributeValue(nuovo, "id", "rp"+maxPassiva);
				UtilDom.setAttributeValue(nuovo, "xlink:href", norma.getText());
				relazioniNode.appendChild(nuovo);
				//creo l'evento (se non presente)
			
				nuovo = utilRulesManager.getNodeTemplate("evento"); 
					//cerco le info per l'evento sul documento attivo
					Node termine = UtilDom.findRecursiveChild(nirUtilDom.findActiveMeta(docAttivo,nodoMeta),"dsp:termine");
					String idtermine = UtilDom.getAttributeValueAsString(termine, "da");
					if (idtermine==null)
						idtermine = UtilDom.getAttributeValueAsString(termine, "a");
					idtermine=idtermine.substring(1);  //levo la #
					Node termineAttivo = UtilDom.getElementsByAttributeValue(docAttivo,docAttivo,"id",idtermine)[0];
					String decorrenza = UtilDom.getAttributeValueAsString(termineAttivo, "data");
				UtilDom.setAttributeValue(nuovo, "data", decorrenza);
				UtilDom.setAttributeValue(nuovo, "fonte", "rp"+maxPassiva);
				UtilDom.setAttributeValue(nuovo, "tipo", "modifica");
				UtilDom.setIdAttribute(nuovo, "t"+maxEventi);
				eventiNode.appendChild(nuovo);
				
				primaModifica = false;
			}
		
//-----FASE3 (TestoDopo/modifiche sul DOM)
		
			String posDisposizione = "#"+UtilDom.getAttributeValueAsString(nirUtilDom.getContainer(nodoModOriginale), "id");
			String nota;
			idNovellando = "";
			idNovella = "";
			//Aggiorno il Novellando e creo (se necessario [integrazione] [sostituzione]) la novella (per ora vuota)
			Evento[] eventi = ciclodivita.getEventi();
			eventovigore = eventi[eventi.length-1];
			eventovigore.setFonte(new Relazione("", "", norma.getText()));
			
			Node n=null;
			
			int numeroIterazioni = 1;
			
			
			if (parole) {	//cerco parole/capoverso/periodo/alinea/rubrica...
				if ("parole".equals(tipoModifica) && partizioneIndicata==null) {	
					//se è parole, cerco le occorrenze di parole
					//per ora solo la prima occorrenza della parola (numeroIterazioni=1)
					 String  parolaDaCercare = UtilDom.getText(virgolettaDaEliminare).trim();
					 Node nodoParola = cercaParola(parolaDaCercare, posizione);
					 if (nodoParola!=null) {
						 start = UtilDom.getText(nodoParola).indexOf(parolaDaCercare);
						 end = start+parolaDaCercare.length();
						 posizione = nodoParola;
					 }
					
				} else { //calcolo il valore di "end"
					
					if (partizioneIndicata==null)
						end = UtilDom.getTextNode(posizione).length();
					
					if (!UtilDom.isTextNode(posizione))
						posizione = posizione.getFirstChild();
				}
					
			}
			
			
			String tipoModifica = nodoMeta.getNodeName();
			if ("dsp:abrogazione".equals(tipoModifica) | "dsp:sostituzione".equals(tipoModifica)) {
				if (parole) {
					//n = domDisposizioni.setVigenza(posizione, "", start, end, makeVigenza(posizione,"novellando","abrogato"));
					n = domDisposizioni.setVigenza(posizione, "", start, end, makeVigenza(posizione,"novellando","abrogato"));
					
				} else {
					n = domDisposizioni.setVigenza(posizione, posizione.getNodeValue(), start, end, makeVigenza(posizione,"novellando","abrogato"));
				}
				modifica1 = n;
				idNovellando += UtilDom.getAttributeValueAsString(n, "id");
			}
			if ("dsp:integrazione".equals(tipoModifica) | "dsp:sostituzione".equals(tipoModifica)) {
				if (parole) {
					n = domDisposizioni.makeSpan(n, -1, makeVigenza(n,"novella","abrogato"),UtilDom.getText(virgolettaDaInserire));
					
				} else {				
					n = domDisposizioni.makePartition(posizione, docEditor.importNode((Node)UtilDom.getChildElements(virgolettaDaInserire).get(0),true), makeVigenza(posizione,"novella","abrogato"));
					UtilDom.mergeTextNodes(n);
				}
				if (modifica1 == null)
					modifica1 = n;
				else
					modifica2 = n;
				idNovella += UtilDom.getAttributeValueAsString(n, "id");
			}
	
			try {
				nota = nirUtilUrn.getFormaTestuale(new Urn(urnAttivo+posDisposizione));
			} catch (ParseException e) {
				nota = formatestuale.getText();
			}
				
			nodeNovellando = modifica1;	
			nodeDisposizione = domDisposizioni.setDOMDisposizioni(partizione, urnAttivo, urnAttivo+posDisposizione, "#"+idNovellando, "#"+idNovella, "", nota, "", true, eventoriginale, eventovigore);
			
			documentManager.commitEdit(t);
			
			} catch (Exception ex) {
				documentManager.rollbackEdit(t);
				ex.printStackTrace();
			} 

//-----FASE4 (Creazione frammenti xTab)		
		
			//creo un frammento con il 'nodo', DOPO l'applicazione della modifica, per trasformarlo in HTML
			Node nodoDopo = docEditor.createElement("NIR");
			nodoDopo.appendChild(modifica1.cloneNode(true));
			if (modifica2 != null)
				nodoDopo.appendChild(modifica2.cloneNode(true));
			
			//eseguo le trasf.in Html per farle vedere nei tab della maschera
			DOMWriter domWriter = new DOMWriter();
			domWriter.setCanonical(false);
			domWriter.setFormat(false);
			try {
				//domWriter.setOutput(UtilFile.getTempDirName()+ File.separatorChar +"tempMod.xml");
				domWriter.setOutput(UtilFile.getTempDirName()+ File.separatorChar +"tempMod.xml", getEncoding(docAttivo));
				domWriter.write(nodoMod);
				Source xsl = new StreamSource(new FileInputStream(UtilFile.getTempDirName()+ File.separatorChar +"nir-nocss.xsl"));
				Transformer converti = factory.newTransformer(xsl);
				converti.setOutputProperty(OutputKeys.ENCODING,documentManager.getEncoding());
				Source source = new StreamSource(new File(UtilFile.getTempDirName()+ File.separatorChar +"tempMod.xml"));
				Result dest = new StreamResult(UtilFile.getTempDirName()+ File.separatorChar +"tempHtmlMod.xml");
				converti.setParameter("datafine", "");
				converti.transform(source,dest);
				String html="";
				FileInputStream fis = new FileInputStream(UtilFile.getTempDirName()+ File.separatorChar +"tempHtmlMod.xml");
				InputStreamReader isr=new InputStreamReader(fis);
				BufferedReader bufline=new BufferedReader(isr);
		    	String str;
		    	while (null != ((str = bufline.readLine())))
		    		html = html+str+"\n";	
		    	bufline.close ();
		    	testoModifica.setText(html);
		    	
		    	domWriter.setOutput(UtilFile.getTempDirName()+ File.separatorChar +"tempPrima.xml", getEncoding(docEditor));
				domWriter.write(nodoPrima);
				source = new StreamSource(new File(UtilFile.getTempDirName()+ File.separatorChar +"tempPrima.xml"));
				dest = new StreamResult(UtilFile.getTempDirName()+ File.separatorChar +"tempHtmlPrima.xml");
				converti.setParameter("datafine", "");
				converti.transform(source,dest);
				html="";
				fis = new FileInputStream(UtilFile.getTempDirName()+ File.separatorChar +"tempHtmlPrima.xml");
				isr=new InputStreamReader(fis);
				bufline=new BufferedReader(isr);
		    	while (null != ((str = bufline.readLine())))
		    		html = html+str+"\n";	
		    	bufline.close ();
		    	testoOriginale.setText(html);
		    	
		    	domWriter.setOutput(UtilFile.getTempDirName()+ File.separatorChar +"tempDopo.xml", getEncoding(docEditor));
				domWriter.write(nodoDopo    /*!!!!!!!!!!!! posizione.getParentNode()*/);
				source = new StreamSource(new File(UtilFile.getTempDirName()+ File.separatorChar +"tempDopo.xml"));
				dest = new StreamResult(UtilFile.getTempDirName()+ File.separatorChar +"tempHtmlDopo.xml");
				converti.setParameter("datafine", "");
				converti.transform(source,dest);
				html="";
				fis = new FileInputStream(UtilFile.getTempDirName()+ File.separatorChar +"tempHtmlDopo.xml");
				isr=new InputStreamReader(fis);
				bufline=new BufferedReader(isr);
		    	while (null != ((str = bufline.readLine())))
		    		html = html+str+"\n";	
		    	bufline.close ();
		    	testoModificato.setText(html);
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
		}
		else { //solo il testo del MOD
			scrivi.setEnabled(false);
			DOMWriter domWriter = new DOMWriter();
			domWriter.setCanonical(false);
			domWriter.setFormat(false);
			try {
				domWriter.setOutput(UtilFile.getTempDirName()+ File.separatorChar +"tempMod.xml", getEncoding(docAttivo));
				domWriter.write(nodoMod);
				Source xsl = new StreamSource(new FileInputStream(UtilFile.getTempDirName()+ File.separatorChar +"nir-nocss.xsl"));
				Transformer converti = factory.newTransformer(xsl);
				converti.setOutputProperty(OutputKeys.ENCODING,documentManager.getEncoding());
				Source source = new StreamSource(new File(UtilFile.getTempDirName()+ File.separatorChar +"tempMod.xml"));
				Result dest = new StreamResult(UtilFile.getTempDirName()+ File.separatorChar +"tempHtmlMod.xml");
				converti.setParameter("datafine", "");
				converti.transform(source,dest);
				String html="";
				FileInputStream fis = new FileInputStream(UtilFile.getTempDirName()+ File.separatorChar +"tempHtmlMod.xml");
				InputStreamReader isr=new InputStreamReader(fis);
				BufferedReader bufline=new BufferedReader(isr);
		    	String str;
		    	while (null != ((str = bufline.readLine())))
		    		html = html+str+"\n";	
		    	bufline.close ();
		    	testoModifica.setText(html);
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
		}
		return false;
	}
	
	public void openForm() {
		
		vecchieModifiche = -1;
		//posiziona form vuota, tasto apertura lista
		settaForm(false, "");
		settaTasti(true, false, false);
		listModel.clear();
		//prendo evento originale
		ciclodivita.setActiveNode(selectionManager.getActiveNode());		//???????? serve ????
		eventoriginale = ciclodivita.getEventi()[0];
		//copia dell'originale
		docEditor = documentManager.getDocumentAsDom();
		form.showDialog(false);					
	}

	private void settaForm(boolean finito, String mess) {
		testoModificato.setText("");
		testoModifica.setText("");
		testoOriginale.setText("");
		formatestuale.setText("");
		norma.setText("");
		if (finito)
			formatestuale.setText("Finito");
		errore.setText(mess);
	}
	
	private void settaTasti(boolean elenco, boolean apri, boolean modifica) {
		messaggio.setVisible(apri);
		lista.setEnabled(elenco); 
		file.setEnabled(apri);
		repo.setEnabled(apri);
		cambia.setEnabled(modifica);
		scrivi.setEnabled(modifica);
	}
	
	private void settaModifica() {

		dataEvento = UtilDom.getAttributeValueAsString((Node) nodiModifica.get(correnteModifica), "data");
		urnAttivo = UtilDom.getAttributeValueAsString((Node) nodiModifica.get(correnteModifica), "fonte");
		
		testoModificato.setText("");
		testoModifica.setText("");
		testoOriginale.setText("");
		messaggio.setVisible(false);
		//norma è il "file" da aprire
		norma.setText(urnAttivo);
		try {
			formatestuale.setText(nirUtilUrn.getFormaTestuale(new Urn(norma.getText())));
		} catch (ParseException e) {
			formatestuale.setText("");
		}
	}
	
	private File apriFile() {
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new RegexpFileFilter("XML", ".*\\.xml"));
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			return fileChooser.getSelectedFile();
		return null;
	}
	
	private Document parsa(File file) {
		try {
			dbf.setValidating(false);
			dbf.setNamespaceAware(false);
			db = dbf.newDocumentBuilder();
			db.setErrorHandler(null);
			UtilFile.copyFileInTemp(new FileInputStream(file), nomeTemp);
			return db.parse(UtilFile.getFileFromTemp(nomeTemp));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Node cercaParola(String parolaDaCercare, Node nodoCorrente) {
		Node ret = null;
		if (nodoCorrente.getNodeType() == Node.TEXT_NODE)
			if ((UtilDom.getText(nodoCorrente)).indexOf(parolaDaCercare)!=-1)
				return nodoCorrente;

		if (nodoCorrente.getNodeType() == Node.ELEMENT_NODE) {
			NodeList list = nodoCorrente.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				ret = cercaParola(parolaDaCercare, list.item(i));
				if (ret != null)
					return ret;
			}
		}
		return null;
	}
	private Node cercaPosizione(Document doc, Node metaCorrente) {
		
		tipoModifica = "";	//è ciò che voglio modificare
		/*
		<dsp:novella>
			<dsp:pos xlink:href="#mod1-vir1" xlink:type="simple" />
			<dsp:subarg>
				<ittig:tipo valore="articolo" xmlns:ittig="http://www.ittig.cnr.it/provvedimenti/2.2" />
			</dsp:subarg>
		</dsp:novella>
		<dsp:novellando>
			<dsp:subarg>
				<ittig:tipo valore="articolo" xmlns:ittig="http://www.ittig.cnr.it/provvedimenti/2.2" />
			</dsp:subarg>
		</dsp:novellando>		 
	    */
		
		Node[] temp = null;
		if ("dsp:integrazione".equals(metaCorrente.getNodeName()))
			temp = UtilDom.getElementsByTagName(doc,metaCorrente,"dsp:novella");
		else
			temp = UtilDom.getElementsByTagName(doc,metaCorrente,"dsp:novellando");
		if (temp!=null)
			tipoModifica = UtilDom.getAttributeValueAsString(UtilDom.getElementsByTagName(doc,temp[0],"ittig:tipo")[0],"valore");
		else 
			tipoModifica = UtilDom.getAttributeValueAsString(UtilDom.getElementsByTagName(doc,UtilDom.getElementsByTagName(doc,metaCorrente,"dsp:novella")[0],"ittig:tipo")[0],"valore");
		
		parole = true;
		for (int i=0; i<elencoPartizioni.length; i++)
			if (tipoModifica.equals(elencoPartizioni[i])) {
				parole=false;
				break;
			}		
		
		Node norma = UtilDom.getElementsByTagName(doc,metaCorrente,"dsp:norma")[0];
		/*
	<dsp:norma xlink:href="urn:nir:stato:legge:1999;2" xlink:type="simple">
   		<dsp:pos xlink:href="urn:nir:stato:legge:1999;2#art1" xlink:type="simple"/>
   		<dsp:subarg>
    		<ittig:bordo xmlns:ittig="http://www.ittig.cnr.it/provvedimenti/2.2" num="a" tipo="lettera">
     			<ittig:bordo ord="2" tipo="periodo" xmlns:ittig="http://www.ittig.cnr.it/provvedimenti/2.2"/>
    		</ittg:bordo>
   		</dsp:subarg>
  	</dsp:norma>
		*/	
		boolean trovataPosizione = true;
		Node posiz = UtilDom.getElementsByTagName(doc,doc,"meta")[0].getParentNode(); 	//posizione su: intero documento
		Node pos = UtilDom.getElementsByTagName(doc,norma,"dsp:pos")[0];
		partizione = UtilDom.getAttributeValueAsString(pos,"xlink:href");
		if (partizione.indexOf("#")!=-1) {
			partizione = partizione.substring(1+partizione.indexOf("#"));
			Node[] cerco = UtilDom.getElementsByAttributeValue(doc,posiz,"id",partizione);
			if (cerco.length>0) {
				if (cerco[0]!=null)
					posiz = cerco[0];	//posizione su: partizione indicata nel rif
				else
					trovataPosizione=false;
			}
			else 
				trovataPosizione=false;
		}
		if (trovataPosizione) {
			Node[] bordi = UtilDom.getElementsByTagName(doc,norma,"ittig:bordo");
			posizionamentoManuale.azzeraBordi();
			for (int i=0; i<bordi.length; i++) {
				String tag=null;
				String id=null;
				boolean ordinale = false;	//NON USATO x ora !!!!
				String numOrd = UtilDom.getAttributeValueAsString(bordi[i],"num");
				if (numOrd==null) {
					numOrd = UtilDom.getAttributeValueAsString(bordi[i],"ord");
					ordinale = true;
				}
				String tipo = UtilDom.getAttributeValueAsString(bordi[i],"tipo");
				posizionamentoManuale.aggiungiBordo(numOrd,tipo);
				//tipo: atto|allegato|libro|parte|titolo|capo|sezione|articolo|comma|alinea|coda|lettera|numero|punto|periodo|parole|capoverso
				if (tipo.equals("atto")) {
					System.out.println("atto??");
				} else if (tipo.equals("allegato")) {
					tag = "annesso";
					id = "ann";
				} else if (tipo.equals("libro")) {
					tag = "libro";
					id = "lib";				
				} else if (tipo.equals("parte")) {
					tag = "parte";
					id = "prt";
				} else if (tipo.equals("titolo")) {
					tag = "titolo";
					id = "tit";				
				} else if (tipo.equals("capo")) {
					tag = "capo";
					id = "cap";
				} else if (tipo.equals("sezione")) {
					tag = "sezione";
					id = "sez";
				} else if (tipo.equals("articolo")) {
					tag = "articolo";
					id = "art";
				} else if (tipo.equals("comma")) {
					tag = "comma";
					id = "com";				
				} else if (tipo.equals("alinea")) {
					tag = "alinea";
				} else if (tipo.equals("rubrica")) {
					tag = "rubrica";
				} else if (tipo.equals("coda")) {
					tag = "cosa";
				} else if (tipo.equals("lettera")) {
					tag = "el";
					id = "let";
				} else if (tipo.equals("numero")) {
					tag = "en";
					id = "num";				
				} else if (tipo.equals("punto")) {
					tag = "ep";
					id = "pun";
				} else if (tipo.equals("periodo")) {
					System.out.println("periodo??");
				} else if (tipo.equals("parole")) {
					System.out.println("parole??");
				} else if (tipo.equals("capoverso")) {
					System.out.println("capoverso??");
				}
				if (tag!=null) {	//se è NULL per ora salto.
					Node[] cerco = UtilDom.getElementsByTagName(doc,posiz,tag);
					int da = 0;
					int a = cerco.length;
					int inc = 1;
					int nValidi = 0;
					int prendi = 0;
					if (numOrd.equals("ultimo") | numOrd.equals("penultimo")) {
						da = cerco.length;
						a = 0;
						inc = -1;
					}
					if (numOrd.equals("ultimo") | numOrd.equals("primo"))
						prendi = 1;
					if (numOrd.equals("penultimo") | numOrd.equals("secondo"))
						prendi = 2;				
					if (id==null && !"".equals(numOrd)) //esempio: rubrica, alinea, coda, ... non hanno cardinalità (è implicitamente 1)
						prendi = new Integer(numOrd).intValue();				
					for (int j=da; j<a; j+=inc) {
						if (null!=UtilDom.getAttributeValueAsString(cerco[j],"finevigore"))
							continue;	//non sto cercando tag con finevogore
						if (prendi==0 && !"".equals(numOrd)) {	//faccio considerazioni sull'id
							if (UtilDom.getAttributeValueAsString(cerco[j],"id").indexOf(id+numOrd)!=-1) {
								posiz = cerco[j];	//posizione su: bordo individuato
								break;
							}
						} else {	//non considero l'id
							if (nValidi++==prendi) {
								posiz = cerco[j];
								break;
							}
						}
					}
				}
			}
		}
		else
			return null;
		return posiz;
	} 
	
	private VigenzaEntity makeVigenza(Node node, String dsp, String status) {
		
		Evento eIniz=null;
		Evento efine=null;
		if (dsp.equals("novellando")) {		//Novellando
			efine=eventovigore;
			if (domDisposizioni.getVigenza(node,-1,-1)==null || domDisposizioni.getVigenza(node,-1,-1).getEInizioVigore()==null) 
				eIniz=eventoriginale;
			else
				eIniz=domDisposizioni.getVigenza(node,-1,-1).getEInizioVigore();
			return (new VigenzaEntity(node, eIniz,efine, status, ""));
		}
		//Novella
		eIniz=eventovigore;
		return (new VigenzaEntity(node, eIniz,efine, "--", ""));
	}

	public void formClosed() {
		form.showDialog(false);
		if (posizionamentoManuale.isChange()) {
			proponiModificaTesto(posizionamentoManuale.getNodoSelezionato());
		}
		
	}
	
	private String getEncoding(Document document) {
		DeferredDocumentImpl ddi = (DeferredDocumentImpl) document;
		//return ddi != null ? ddi.getXmlEncoding() : null;
		if (ddi == null)
			return "UTF-8";
		if ("UTF-8".equals(ddi.getXmlEncoding()))
			return "UTF-8";
		return "Cp1250";
	}

	public String getErrorMessage() {
		return "";
	}

	public boolean verifyForm() {
		if(scrivi.isEnabled()) { //ho un testo proposto ma non confermato => lo annullo
			Node corrente = domDisposizioni.doErase(idNovellando,idNovella,nodeDisposizione,nodeNovellando);
			if (corrente!=null)
				selectionManager.setActiveNode(this, corrente);
		}
		return true;
	}
}

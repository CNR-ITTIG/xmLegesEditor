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
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.domwriter.DOMWriter;
import it.cnr.ittig.xmleges.core.util.file.RegexpFileFilter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.core.util.lang.UtilLang;
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.w3c.dom.Element;
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
	UtilMsg utilmsg;
	
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
	String dataEvento;
	String urnAttivo;
	String urnCompletaAttivo;
	String urnDocumento;
	Vector dateEventi;
	Vector idEventi;
	Vector disposizioniDaConvertire;
	int correnteEvento;
	
	String partizione;
	String[] partizioni ={"lib","prt","tit","cap","sez","art","com","en","el","ep"};
	
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
	File lastPathFile = null;
	File lastPathList = null;
	String periodoIndividuato="";
	
	Vector figliVirgoletta = null;
	Vector nodiMeta = new Vector();
	Vector idMeta = new Vector();
	Vector nodiTesto = new Vector();
	
	String decorrenza;
	Node virgolettaModRimodificata;
	
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
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
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
			if (lastPathList!=null)
				fileChooser = new JFileChooser(lastPathList);
			else
				fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new RegexpFileFilter("XML", ".*\\.xml"));
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				lastPathList = fileChooser.getCurrentDirectory();
				
				//spostare su apertura documento
				urnDocumento = UtilDom.getAttributeValueAsString(UtilDom.getElementsByTagName(docEditor,docEditor,"originale")[0], "xlink:href");
				
				//Confronto le urn senza considerare giorno e mese, se presenti 
				String urnDocumentoSemplificata = urnDocumento;
				int posTrattino = urnDocumento.indexOf("-");
				if (posTrattino!=-1) 
					urnDocumentoSemplificata=urnDocumento.substring(0,posTrattino)+urnDocumento.substring(urnDocumento.indexOf(";"));

				
				listModel.clear();
				try {
					Document listamodifiche = parsa(fileChooser.getSelectedFile());
					Node[] modifica = UtilDom.getElementsByAttributeValue(listamodifiche,listamodifiche,"urn", urnDocumentoSemplificata);
					if (modifica.length==0)
						errore.setText("Nessuna modifica da apportare");
					else {
						nodiModifica = UtilDom.getChildElements(modifica[0]);
						settaForm(false);
						correnteModifica = 0;
						settaModifica();
						settaTasti(false,true,false);
						setMessagge();
					}
				} catch (Exception ex) {
					errore.setText("Errore nel file aperto");
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
				form.setDialogWaiting(true);
				docAttivo = parsa(attivo);
				//effettuo un test per vedere se la norma aperta è quella che mi aspettavo
				try {
					//Confronto le urn senza considerare giorno e mese, se presenti	
					//sarebbe INUTILE, urnAttivo è già semplificata in lista.xml
					String urnAttivoSemplificata = urnAttivo;
					int posTrattino = urnAttivo.indexOf("-");
					if (posTrattino!=-1) 
						urnAttivoSemplificata=urnAttivo.substring(0,posTrattino)+urnAttivo.substring(urnAttivo.indexOf(";"));

					urnCompletaAttivo = UtilDom.getAttributeValueAsString(UtilDom.getElementsByTagName(docAttivo,docAttivo,"originale")[0], "xlink:href");
					String urnDocAttivoSemplificata = urnCompletaAttivo;
					posTrattino = urnDocAttivoSemplificata.indexOf("-");
					if (posTrattino!=-1) 
						urnDocAttivoSemplificata=urnDocAttivoSemplificata.substring(0,posTrattino)+urnDocAttivoSemplificata.substring(urnDocAttivoSemplificata.indexOf(";"));
		
				if (!urnAttivoSemplificata.equals(urnDocAttivoSemplificata)) {
					errore.setText("Il file aperto non è corretto");
					form.setDialogWaiting(false);
					return;
				}
				} catch (Exception ex) { //arriva qui se non apro un documento NIR (xex)
					errore.setText("Il file aperto non è corretto");
					form.setDialogWaiting(false);
					return;
				}
				
				
				
				
				errore.setText(" ");
				//azzero array e liste
				dateEventi = new Vector();
				idEventi = new Vector();
				disposizioniDaConvertire = new Vector();
				correnteEvento = 0;
				listModel.clear();
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
											Node daAggiungere = dspTermine[k].getParentNode();
											//li ordino
											boolean inserito = false;
											String nMod = UtilDom.getAttributeValueAsString((Node)UtilDom.getChildElements(daAggiungere).get(0), "xlink:href");
											if (nMod!=null)
												for (int z=0; z<disposizioniDaConvertire.size(); z++) {
													String temp = UtilDom.getAttributeValueAsString((Node)(UtilDom.getChildElements((Node)disposizioniDaConvertire.get(z))).get(0), "xlink:href");
													if (temp==null || nMod.compareTo(temp)<0) {
														disposizioniDaConvertire.add(z, daAggiungere);
														inserito = true;
														break;
													}
												}
											if (!inserito)
												disposizioniDaConvertire.add(daAggiungere);
										}
									//popolo la finestra delle informazioni
									for (int k=0; k<disposizioniDaConvertire.size(); k++) {
										String tempPart = UtilDom.getAttributeValueAsString(UtilDom.findRecursiveChild(UtilDom.findRecursiveChild((Node)disposizioniDaConvertire.get(k),"dsp:norma"),"dsp:pos"),"xlink:href");
										if (tempPart.indexOf("#")==-1)
											tempPart = "intero atto";
										else	
											tempPart = tempPart.substring(tempPart.indexOf("#")+1, tempPart.length());
										String tipo = ((Node)disposizioniDaConvertire.get(k)).getNodeName();
										tipo = tipo.substring(tipo.indexOf(":")+1, tipo.length()).toUpperCase();
										listModel.addElement(tipo + " in data " + UtilDate.normToString(tempData) + " di " + tempPart);
									}
								}
								break;
							}
					}
				}
				if (idEventi.size()==0)	//il file aperto non contiene nessun evento di modifica (non dovrebbe mai succedere)
					errore.setText("Nessun evento trovato");
				else {
					settaTasti(false,false,true);
					primaModifica=true;		//cambiare --- implementare la funzione di check sulle modifiche
					while (proponiModificaTesto(null));
				}
				form.setDialogWaiting(false);
			}
		}
		
		if (e.getSource() == scrivi) {
			boolean creaMetadato = false;
			if (virgolettaModRimodificata!=null)				
				if ("virgolette".equals(virgolettaModRimodificata.getLocalName()))
					if (utilmsg.msgYesNo("Attenzione. Stai apportando modifiche all'interno di una virgoletta di una modifica. E' questo quello che vuoi fare? Se si creo il corrispondente evento attivo ma devi gestire manualmente la modifica passiva."))
						creaMetadato = true;
					else
						return;
				else //ho modificato in un mod, fuori dalle virgolette
					if (!utilmsg.msgYesNo("Attenzione. Stai apportando modifiche all'interno di una modifica. E' questo quello che vuoi fare? Se si gestisci eventuali eventi attivi e modifiche passive manualmente."))
						return;			
			if (creaMetadato) {
				Node disposiz = UtilDom.findRecursiveChild(nirUtilDom.findActiveMeta(docEditor,null),"disposizioni");
				Node modAtt = UtilDom.findRecursiveChild(disposiz,"modificheattive");
				//Attenzione, devo trovare chi punta la virgoletta, anche + di uno se siamo in un mmod (mmod potrei avere + urn modificate!?!?),
				//o se ï¿½ una modifica, di un mod, giï¿½ in precedenza modificato. 
				//In questo secondo caso, queste disposizioni dichiarano tutti di operare nello stesso mod, e quindi le considero una sola volta.
				String idVirgoletta = "#"+UtilDom.getAttributeValueAsString(virgolettaModRimodificata, "id");
				Node[] metaCoinvolti = UtilDom.getElementsByAttributeValue(docEditor,modAtt,"xlink:href", idVirgoletta);
				Vector daDuplicare = new Vector();
				for (int i=0; i<metaCoinvolti.length; i++) {
					Node dispoPos = metaCoinvolti[i].getParentNode().getParentNode().getFirstChild();
					String id = UtilDom.getAttributeValueAsString(dispoPos, "xlink:href");
					boolean trovato = false;
					for (int j=0; j<daDuplicare.size(); j++)
						if (id.equals(UtilDom.getAttributeValueAsString((Node)daDuplicare.get(j), "xlink:href"))) {
							trovato = true;
							break;
						}
					if (!trovato)
						daDuplicare.add(dispoPos);
				}
				//creazione evento e metadato
				int max=0;
				String urn = "";
				try {
					urn = UtilDom.getAttributeValueAsString(UtilDom.findRecursiveChild(((Node) daDuplicare.get(0)).getParentNode(),"dsp:norma"), "xlink:href");
				} catch (Exception ex) {}
				Node relazioniNode = UtilDom.findRecursiveChild(nirUtilDom.findActiveMeta(docEditor,null),"relazioni");
				NodeList relazioniList = relazioniNode.getChildNodes();
				Node ultimoAttiva = null;
				for (int i = 0; i < relazioniList.getLength(); i++) {
					Node relazioneNode = relazioniList.item(i);
					if ("attiva".equals(relazioneNode.getNodeName())) {
						String id = UtilDom.getAttributeValueAsString(relazioneNode, "id");
						Integer idValue = Integer.decode(id.substring(2));
						ultimoAttiva = relazioneNode;
						if (idValue.intValue() > max)
							max = idValue.intValue();
					}
				}
				max++;
				Node nuovo = utilRulesManager.getNodeTemplate("attiva");
				UtilDom.setAttributeValue(nuovo, "id", "ra"+max);
				UtilDom.setAttributeValue(nuovo, "xlink:href", urn);
				if (ultimoAttiva.getNextSibling()==null) 
					relazioniNode.appendChild(nuovo);
				else
					relazioniNode.insertBefore(nuovo, ultimoAttiva.getNextSibling());
				//evento
				Node eventiNode = UtilDom.findRecursiveChild(nirUtilDom.findActiveMeta(docEditor,null),"eventi");
				String idevento = "t" + (1 + eventiNode.getChildNodes().getLength());
				nuovo = utilRulesManager.getNodeTemplate("evento");
				UtilDom.setAttributeValue(nuovo, "data", decorrenza);
				UtilDom.setAttributeValue(nuovo, "fonte", "ra"+max);
				UtilDom.setAttributeValue(nuovo, "tipo", "modifica");
				UtilDom.setIdAttribute(nuovo, idevento);
				eventiNode.appendChild(nuovo);
				String idTermine = "#"+UtilDom.getAttributeValueAsString(nuovo, "id");
				//inserisco nuovo/i meta
				for (int j=0; j<daDuplicare.size(); j++) {
					Node meta = ((Node) daDuplicare.get(j)).getParentNode();
					Node nuovoMeta = meta.cloneNode(true);
					UtilDom.setAttributeValue(UtilDom.findRecursiveChild(nuovoMeta,"dsp:termine"), "da", idTermine);
					if (meta.getNextSibling()!=null)
						meta.getParentNode().insertBefore(nuovoMeta, meta.getNextSibling());
					else
						meta.getParentNode().appendChild(nuovoMeta);
				}
			}
			
			try {
			listModel.setElementAt(((String) listModel.get(getPosLista())) + " --> applicata", getPosLista());
			} catch (Exception ex) {}
			correnteEvento++;
			settaTasti(false,false,true);
			while (proponiModificaTesto(null));
		}
		
		if (e.getSource() == repo) {
			//da implementare
		}
	}

	private int getPosLista() {
		//System.out.println("num Errore " + numMessaggiErrore + " +1 +correnteEvento " + correnteEvento + " + vecchieModifiche "+vecchieModifiche + " = " + (numMessaggiErrore+1+correnteEvento+vecchieModifiche));
		return correnteEvento;
	}
	
	private boolean proponiModificaTesto(Node partizioneIndicata) {

		try {
		form.setDialogWaiting(true);
		
		if (partizioneIndicata!=null)
			if (scrivi.isEnabled()) { //ho cambiato posizione e avevo già proposto qualcosa => cancella vecchia proposta
				Node corrente = domDisposizioni.doErase(idNovellando,idNovella,nodeDisposizione,nodeNovellando);
				if (corrente!=null)
					selectionManager.setActiveNode(this, corrente);
				
				//Attenzione, se avevo più partizioni nella virgoletta => devo cancellare anche le altre integrazioni
				if (figliVirgoletta!=null)
					for (int i=1; i<figliVirgoletta.size(); i++) 
						domDisposizioni.doErase("",(String)idMeta.get(i-1),(Node)nodiMeta.get(i-1),null);

			}
			else 		//se ho selezionato delle parole manualmente, mi fido anche se nella realtà avevo chiesto di selezionarne altre
				try {
					periodoIndividuato = UtilDom.getText(selectionManager.getActiveNode()).substring(selectionManager.getTextSelectionStart(),selectionManager.getTextSelectionEnd());
				} 
				catch (Exception e) {}

//-----TEST	(fine/skip)	
		//test per capire quale evento devo applicare
		if (correnteEvento==disposizioniDaConvertire.size()) {
			//ho finito di scorrere le modifiche per il modificante corrente. Testo se ne ho altre norme modificanti da applicare			
			if (correnteModifica==nodiModifica.size()-1) {
				settaTasti(false,false,false);
				settaForm(true);
				errore.setText("");
			}
			else {
				correnteModifica++;
				settaModifica();
				settaTasti(false,true,false);
				setMessagge();
			}
			form.setDialogWaiting(false);
			return false;
		}
		
		
//-----FASE1 (processo il Meta, creo Mod/TestoPrima)
		
		eventi.setSelectedIndex(getPosLista());		
		Node nodoMeta = (Node)disposizioniDaConvertire.get(correnteEvento);
		boolean implicita = ("si".equals(UtilDom.getAttributeValueAsString(nodoMeta,"implicita").toLowerCase())) ? true : false;
		String idMod = UtilDom.getAttributeValueAsString(UtilDom.findRecursiveChild(nodoMeta,"dsp:pos"),"xlink:href").substring(1);
		virgolettaDaInserire=null;
		virgolettaDaEliminare=null;
		Node tempNovella = UtilDom.findRecursiveChild(nodoMeta,"dsp:novella");
		if (tempNovella!=null) {
			String idVirgoletta = UtilDom.getAttributeValueAsString(UtilDom.findRecursiveChild(tempNovella,"dsp:pos"),"xlink:href").substring(1);
			Node[] trovati = UtilDom.getElementsByAttributeValue(docAttivo,docAttivo,"id",idVirgoletta);
			if (trovati.length>0)
				virgolettaDaInserire = UtilDom.getElementsByAttributeValue(docAttivo,docAttivo,"id",idVirgoletta)[0].cloneNode(true);  //lo clono perchè potrei integrarlo con num e rubrica e poi rimosizionarmi manualmente altrove
			else {
				utilmsg.msgInfo("Attenzione. Hai informazioni nei metadati non presenti nel testo.");
				try {
					listModel.setElementAt(((String) listModel.get(getPosLista())) + " --> saltata", getPosLista());
				} catch (Exception ex) {}
				correnteEvento++;
				return true;
			}
		}
		Node tempNovellando = UtilDom.findRecursiveChild(nodoMeta,"dsp:novellando");
		if (tempNovellando!=null) {
			Node temPos = UtilDom.findRecursiveChild(tempNovellando,"dsp:pos");
			if (temPos!=null) { //xex abrogazione di parole
				String idVirgoletta = UtilDom.getAttributeValueAsString(temPos,"xlink:href").substring(1);
				try{
					virgolettaDaEliminare = UtilDom.getElementsByAttributeValue(docAttivo,docAttivo,"id",idVirgoletta)[0];
				} catch (Exception e) {
					System.out.println("Non ho trovato virgoletta puntata " + idVirgoletta + " -- " + e.getMessage());
					virgolettaDaEliminare = null;
				}
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
			if ("passiva".equals(nodeName)) {				
				Integer idValue = Integer.decode(id.substring(2));
				if (idValue.intValue() > maxPassiva)
					maxPassiva = idValue.intValue();
				if (urnCompletaAttivo.equals(UtilDom.getAttributeValueAsString(relazioneNode, "xlink:href")))
					idEventiDaTestare.add(id);					
			}			
		}
		if (partizioneIndicata==null || !scrivi.isEnabled())
			maxPassiva++;
			
		//controllo grezzo
		if (maxPassiva==0)
			maxPassiva=1;		
			
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
								
							try {
							listModel.setElementAt(((String) listModel.get(getPosLista())) + " --> saltata, giï¿½ applicata", getPosLista());
							} catch (Exception e) {}
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
		if (partizioneIndicata==null){
			Node importedNode = docEditor.importNode(nodoMeta,true);
			posizione = cerca_Rif_e_Bordo(docEditor, utilRulesManager.completeNamespaceFor(importedNode));
		}
		else {
				posizione = partizioneIndicata;
				start = posizionamentoManuale.getInizioSelezione();
				end = posizionamentoManuale.getFineSelezione();
				if (start!=end) 
					parole=true;
				else
					parole=false;
		}
		
		if (posizione!=null) {
			if (parole && partizioneIndicata==null) {	//cerco parole/capoverso/periodo/alinea/rubrica...
				if ("parole".equals(tipoModifica)) {	
					//se è parole, cerco le occorrenze di parole
					//per ora solo la prima occorrenza della parola (numeroIterazioni=1)
					 try {
					 String  parolaDaCercare = UtilDom.getText(virgolettaDaEliminare).trim().toLowerCase();
					 Node nodoParola = cercaParola(parolaDaCercare, posizione);
					 if (nodoParola!=null) {
						 start = UtilDom.getText(nodoParola).toLowerCase().indexOf(parolaDaCercare);
						 end = start+parolaDaCercare.length();
						 posizione = nodoParola;
					 }
					 else { //non ho trovato le parole cercate, effettuo una selezione casuale
						 posizione=null;
					}
					} catch (Exception e) {
						posizione=null;
					}
				} 
				else if ("periodo".equals(tipoModifica) || "capoverso".equals(tipoModifica)) {
					//cerco la posizione del periodo: 'periodoIndividuato'
					Node nodoParola = cercaParola(periodoIndividuato, posizione);
					if (nodoParola!=null) {
						 start = UtilDom.getText(nodoParola).indexOf(periodoIndividuato);
						 end = start+periodoIndividuato.length();
						 posizione = nodoParola;
					}
					else { //non sono capace di effettuare la selezione (xex selezione a cavallo su + tag), effettuo una selezione casuale
						posizione=null;
					}
					
				}	
			}	
		}
		if (posizione!=null			//se è null NON PROPONGO LA MODIFICA (faccio vedere in ELSE il testo del MOD)
				&& (parole || ("atto".equals(tipoModifica) || meta2tag(tipoModifica).equals(posizione.getNodeName())))) {		//stesso se non modifica lo stesso tipo di partizione. 

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
				documentManager.setChanged(true);
				
			if (primaModifica) {	//implementare altro meccanismo				
				//creo la relazione (passiva)
				Node nuovo = utilRulesManager.getNodeTemplate("passiva");
				UtilDom.setAttributeValue(nuovo, "id", "rp"+maxPassiva);
				UtilDom.setAttributeValue(nuovo, "xlink:href", urnCompletaAttivo);
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
					decorrenza = UtilDom.getAttributeValueAsString(termineAttivo, "data");
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
			eventovigore.setFonte(new Relazione("", "", urnCompletaAttivo));
			
			Node n=null;


			
			virgolettaModRimodificata = null;
			String tipoModifica = nodoMeta.getNodeName();
			if ("dsp:abrogazione".equals(tipoModifica) || "dsp:sostituzione".equals(tipoModifica)) {
				if (parole) {
					//n = domDisposizioni.setVigenza(posizione, "", start, end, makeVigenza(posizione,"novellando","abrogato"));
					n = domDisposizioni.setVigenza(posizione, "", start, end, makeVigenza(posizione,"novellando","abrogato"));
					end = -1; //devo fare ancora il nuovo span
				} else {
					n = domDisposizioni.setVigenza(posizione, posizione.getNodeValue(), start, end, makeVigenza(posizione,"novellando","abrogato"));
					//dovrebbe accadere solo su Abrogazione di intero atto
					String correzId = UtilDom.getAttributeValueAsString(n, "id");
					if (correzId==null) {	
						String prefisso = n.getLocalName();
						int occorrenze = documentManager.getDocumentAsDom().getElementsByTagName(prefisso).getLength();
						UtilDom.setIdAttribute(n, prefisso + occorrenze);
						correzId = UtilDom.getAttributeValueAsString(n, "id");
					}
				}
				modifica1 = n;
				idNovellando += UtilDom.getAttributeValueAsString(n, "id");
				
				//Un ulteriore controllo. Se ho modificato dentro ad un mod => avverti di possibili modifiche a catena (da gestire manualmente)
				if (UtilDom.findParentByName(n, "mod")!=null) {
					virgolettaModRimodificata = UtilDom.findParentByName(n, "virgolette");
					if (virgolettaModRimodificata==null) //Sto modificando in un mod, ma fuori da una virgoletta => cosa voleva fare il legislatore??
						virgolettaModRimodificata = UtilDom.findParentByName(n, "mod");
				}
				
			}
			figliVirgoletta = null;
			if ("dsp:integrazione".equals(tipoModifica) || "dsp:sostituzione".equals(tipoModifica)) {
				if (parole) {
					if (n==null)
						n = posizione;
					
					//n = domDisposizioni.makeSpan(n, -1, makeVigenza(n,"novella","abrogato"),UtilDom.getText(virgolettaDaInserire));
					n = domDisposizioni.makeSpan(n, end, makeVigenza(n,"novella","abrogato"),UtilDom.getText(virgolettaDaInserire));
					
				} else {
					//NEL CASO DI SOSTITUZIONE, se la partizione da inserire non ha NUM o RUBRICA le recupero da quella uscente (se presenti)
					if ("dsp:sostituzione".equals(tipoModifica)) {
						try {
						Vector figliIntegrati = UtilDom.getChildElements((Node)UtilDom.getChildElements(virgolettaDaInserire).get(0));
						Vector figliAbrogati = UtilDom.getChildElements(n);
						String testo = UtilDom.getText((Node) figliIntegrati.get(0));
						if (testo==null)
							testo = "";
						if ("".equals(testo)) {//non specifica il NUM il testo entrante
							testo = UtilDom.getText((Node) figliAbrogati.get(0));
							if (testo!=null)
								UtilDom.setTextNode((Node) figliIntegrati.get(0), testo);
						}
						Node rubricaUscente = (Node) figliAbrogati.get(1);
						if ("rubrica".equals(rubricaUscente.getNodeName())) {	//avevo una rubrica nel testo uscente
							Node rubricaEntrante = (Node) figliIntegrati.get(1);
							if (!"rubrica".equals(rubricaEntrante.getNodeName())) { //non sto inserento tutto il tag rubrica
								rubricaEntrante = utilRulesManager.getNodeTemplate(docAttivo,"rubrica");
								UtilDom.setTextNode(rubricaEntrante, UtilDom.getText(rubricaUscente));
								UtilDom.insertAfter(rubricaEntrante, (Node) figliIntegrati.get(0));
							}
							else  //sto inserendo un tag rubrica
								if ("".equals(rubricaEntrante)) //Tag vuoto
									UtilDom.setTextNode(rubricaEntrante, UtilDom.getText(rubricaUscente));
						}
						} catch (Exception e) {}		
					}
					figliVirgoletta = UtilDom.getAllChildElements(virgolettaDaInserire);
					Node importedNode = docEditor.importNode((Node)figliVirgoletta.get(0),true);
					importedNode = utilRulesManager.completeNamespaceFor(importedNode);
					n = domDisposizioni.makePartition(posizione, importedNode , makeVigenza(posizione,"novella","abrogato"));
					try {
						UtilDom.trimAndMergeTextNodes(n,true);
					} catch (Exception e) {}
					
					//Se ho modificato dentro ad un mod => avverti di possibili modifiche a catena (da gestire manualmente)
					if (virgolettaModRimodificata==null & UtilDom.findParentByName(n, "mod")!=null) {
						virgolettaModRimodificata = UtilDom.findParentByName(n, "virgolette");
						if (virgolettaModRimodificata==null) //Sto modificando in un mod, ma fuori da una virgoletta => cosa voleva fare il legislatore??
							virgolettaModRimodificata = UtilDom.findParentByName(n, "mod");
					}
					
					
				}
				if (modifica1 == null)
					modifica1 = n;
				else
					modifica2 = n;
				idNovella += UtilDom.getAttributeValueAsString(n, "id");
			}
	
			try {
				nota = nirUtilUrn.getFormaTestuale(new Urn(urnCompletaAttivo+posDisposizione));
			} catch (ParseException e) {
				nota = formatestuale.getText();
			}
				
			nodeNovellando = modifica1;	
			nodeDisposizione = domDisposizioni.setDOMDisposizioni("#"+partizione, urnCompletaAttivo, urnCompletaAttivo+posDisposizione, "#"+idNovellando, "#"+idNovella, "", nota, "", implicita, eventoriginale, eventovigore);
			
			//Se la virgoletta conteneva piï¿½ partizioni (per ora ho inserito solo 1ï¿½figlio di virgoletta) creo automaticamente
			//delle integrazioni per il 2ï¿½,... eventuale figlio
			if (figliVirgoletta!=null) {
				nodiMeta = new Vector();
				idMeta = new Vector();
				for (int i=1; i<figliVirgoletta.size(); i++) {
					//Node nuovaPosizione = posizione.getNextSibling();
					Node importedNode = docEditor.importNode((Node)figliVirgoletta.get(i),true);
					importedNode = utilRulesManager.completeNamespaceFor(importedNode);
					n = domDisposizioni.makePartition(n, importedNode , makeVigenza(n,"novella","abrogato"));
					UtilDom.trimAndMergeTextNodes(n,true);
					nodiMeta.add(domDisposizioni.setDOMDisposizioni("#"+partizione, urnCompletaAttivo, urnCompletaAttivo+posDisposizione, "#", "#"+UtilDom.getAttributeValueAsString(n, "id"), "", nota, "", implicita, eventoriginale, eventovigore));
					idMeta.add(UtilDom.getAttributeValueAsString(n, "id"));
					nodiTesto.add(n);
				}
			}
			
			
			//mi posiziono sul nodo USCITO/ENTRATO
			selectionManager.setActiveNode(this, modifica1);
			
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
			
			//Aggiungo... se la virgoletta conteneva più partizioni
			if (figliVirgoletta!=null)
				for (int i=1; i<figliVirgoletta.size(); i++)
					nodoDopo.appendChild(((Node)nodiTesto.get(i-1)).cloneNode(true));
					
			
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
				domWriter.write(nodoDopo);
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
		    	testoModificato.setText("");
		    	testoOriginale.setText("");
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
		}
		form.setDialogWaiting(false);
		} catch (Exception e) {
			System.out.println("Eccez. non gestita: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	public void openForm() {

		//posiziona form vuota, tasto apertura lista
		settaForm(false);
		errore.setText("");
		settaTasti(true, false, false);
		listModel.clear();
		//prendo evento originale
		ciclodivita.setActiveNode(selectionManager.getActiveNode());		//???????? serve ????
		try {
			eventoriginale = ciclodivita.getEventi()[0];
		}
		catch (Exception e) {
			utilmsg.msgInfo("Inserire l'evento originale");
			form.close();
			return;
		}
		//copia dell'originale
		docEditor = documentManager.getDocumentAsDom();
		form.showDialog(false);					
	}

	private void settaForm(boolean finito) {
		testoModificato.setText(" ");
		testoModifica.setText(" ");
		testoOriginale.setText(" ");
		formatestuale.setText(" ");
		norma.setText(" ");
		if (finito)
			formatestuale.setText("Finito");
		errore.setText(" ");
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
		if (lastPathFile!=null)
			fileChooser = new JFileChooser(lastPathFile);
		else
			fileChooser = new JFileChooser(lastPathList);
		fileChooser.setFileFilter(new RegexpFileFilter("XML", ".*\\.xml"));
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			lastPathFile = fileChooser.getCurrentDirectory();
			return fileChooser.getSelectedFile();
		}
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
			if ((UtilDom.getText(nodoCorrente).toLowerCase()).indexOf(parolaDaCercare)!=-1)
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
	
	private Node cerca_Ruolo_eo_Posizione(Document doc, Node metaCorrente) {
	
		if (!"dsp:integrazione".equals(metaCorrente.getNodeName())) {
			
		}
		return null;
	}	
	
	private Node cerca_Rif_e_Bordo(Document doc, Node metaCorrente) {
		
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
		
		//Verifica se sto effettuando una modifica su una partizione Nir o di parole (=parole/periodi/capoversi/rubriche/alinee/....)
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
		Node interaNorma = UtilDom.getElementsByTagName(doc,doc,"meta")[0].getParentNode();
		Node posiz = interaNorma; 																	//posizione su: intero documento
		Node pos = UtilDom.getElementsByTagName(doc,norma,"dsp:pos")[0];
		partizione = UtilDom.getAttributeValueAsString(pos,"xlink:href");
		if (partizione.indexOf("#")!=-1) {
			partizione = partizione.substring(1+partizione.indexOf("#"));
			
			//Questo meccanismo cerca id esatto della partizione. Per come numera l'editor, questo non è possibile
//			Node[] cerco = UtilDom.getElementsByAttributeValue(doc,posiz,"id",partizione); 
//			if (cerco.length>0) {
//				if (cerco[0]!=null)
//					posiz = cerco[0];	//posizione su: partizione indicata nel rif
//				else
//					trovataPosizione=false;
//			}
//			else 
//				trovataPosizione=false;
			
			Node posizioneRif = cercaPosizioneContando(doc,UtilDom.getElementsByTagName(doc,doc,"articolato")[0],partizione,false);
			if (posizioneRif==null)
				trovataPosizione = false;
			else
				posiz = posizioneRif;
			
		}
		if (trovataPosizione) {	//se ho individuato la partizione del rif, continuo nella ricerca di eventuali bordi
			Node[] bordi = UtilDom.getElementsByTagName(doc,norma,"ittig:bordo");
	//posizionamentoManuale.azzeraBordi(); 														BUTTATO TUTTO PER ORA
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
	//posizionamentoManuale.aggiungiBordo(numOrd,tipo); 										BUTTATO TUTTO PER ORA
				//tipo: atto|allegato|libro|parte|titolo|capo|sezione|articolo|comma|alinea|coda|lettera|numero|punto|periodo|parole|capoverso
				if (tipo.equals("atto")) {
					System.out.println("cerca_Rif_e_Bordo: atto??   ATTO non dovrebbe mai essere presente nei BORDI !!!!");
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
					tag = "coda";
				} else if (tipo.equals("lettera")) {
					tag = "el";
					id = "let";
				} else if (tipo.equals("numero")) {
					tag = "en";
					id = "num";				
				} else if (tipo.equals("punto")) {
					tag = "ep";
					id = "pun";
				} else if (tipo.equals("parole")) {
					System.out.println("cerca_Rif_e_Bordo: parole??   PAROLE non dovrebbe mai essere presente nei BORDI !!!!");
				}
				if (tag!=null) {   //Cerco il bordo
					Node[] cerco = UtilDom.getElementsByTagName(doc,posiz,tag);
					int da = 0;
					int a = cerco.length;
					int nValidi = 0;
					int prendi = 0;
					int ordineInverso = 0;
					if (numOrd.equals("ultimo") || numOrd.equals("penultimo"))
						ordineInverso = a-1;
					if (numOrd.equals("ultimo") || numOrd.equals("primo"))
						prendi = 1;
					if (numOrd.equals("penultimo") || numOrd.equals("secondo"))
						prendi = 2;				
					if (id==null && !"".equals(numOrd)) //esempio: rubrica, alinea, coda, ... non hanno cardinalità (è implicitamente 1)
						prendi = new Integer(numOrd).intValue();				
					for (int j=da; j<a; j++) {
						int indice = Math.abs(j-ordineInverso);
						if (null!=UtilDom.getAttributeValueAsString(cerco[indice],"finevigore"))
							continue;	//non sto cercando tag con finevogore
						if (prendi==0 && !"".equals(numOrd)) {	//faccio considerazioni sull'id
							if (UtilDom.getAttributeValueAsString(cerco[indice],"id").indexOf(id+numOrd)!=-1) {
								posiz = cerco[indice];	//posizione su: bordo individuato
								break;
							}
						} else {	//non considero l'id
							if (++nValidi>=prendi) {	// >= così se arrivo con 0 o 1 prendo comunque il primo
								posiz = cerco[indice];
								break;
							}
						}
					}
				} else { 	//verifico se sto cercando periodo o capoverso
					if (tipo.equals("periodo") || tipo.equals("capoverso")) {  //capoverso o bordo vanno intesi come un periodo che finisce con .
						String testoPosizione = getTesto(posiz);
						
						/* devo cercare '.', se questi sono in un numero o preceduti da un testo minore di 4 caratteri non li considero
						   marcatori di fine periodo (eccezione sepreceduto da spazio, in questo caso lo considero punto fermo).*/
						Vector periodo = new Vector();
						StringTokenizer st = new StringTokenizer(testoPosizione, ".");
						String temPeriodo = "";
						while (st.hasMoreTokens()) {
							String testPeriodo = temPeriodo+st.nextToken();
							
							String lastParola = testPeriodo;
							if (testPeriodo.indexOf(" ")!=-1)
								lastParola = testPeriodo.substring(testPeriodo.lastIndexOf(" "));
							
							if (lastParola.length()<2) {	//trovato ' .'
								periodo.add(testPeriodo);
								temPeriodo = "";
								continue;
							}
							else if (lastParola.length()>4)		//trovato 'parola.', con parola più lunga di 3 caratteri
								if (!isNumber(lastParola)) {	//non sono in un numero
									periodo.add(testPeriodo);
									temPeriodo = "";
									continue;
								}
							temPeriodo = testPeriodo+".";
						}
						//se numOrd mi indica un numero compreso nei periodi individuati lo seleziono
						if (periodo.size()>0) {
							int prendi = -1;
							if (numOrd.equals("primo")) 
								prendi = 0;
							else if (numOrd.equals("secondo"))
								prendi = 1;
							else if (numOrd.equals("penultimo"))
								prendi = periodo.size()-2;
							else if (numOrd.equals("ultimo"))
								prendi = periodo.size()-1;
							else  {
								try {
									prendi = new Integer(numOrd).intValue() - 1;
								} catch (Exception e) {}
							}
							if (prendi >= 0 && prendi <periodo.size())   //ho trovato un periodo ammissibile
								periodoIndividuato = ((String) periodo.get(prendi)) + ".";
							else
								periodoIndividuato = "";	
						}
					}
				}
			}
		}
		else
			return null;
		
		//test se sono posizionato su intera norma, ma ho giï¿½ un finevigore, deseleziono
		if (posiz!=interaNorma)
			return posiz;	
			
		if (UtilDom.getAttributeValueAsString(interaNorma,"finevigore")!=null)
			return null;
		else
			return interaNorma;
		
	} 
	
	private Node cercaPosizioneContando(Document doc, Node posiz, String partizione, boolean ordinale) {
		
		String token;
		StringTokenizer st = new StringTokenizer(partizione, "-");
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (token.startsWith("t"))	// -t1... non dichiara la partizione, ma l'evento.
				continue;
			for (int i=0; i<partizioni.length; i++)
				if (token.startsWith(partizioni[i])) {
					Node temp = conta(doc, posiz, id2tag(partizioni[i]), token.substring(partizioni[i].length()), ordinale);
					if (temp!=null)
						posiz = temp;
					else
						return posiz; //non ho trovato qualcosa. Mi blocco
				}
		}
		return posiz;
	}
	
	private Node conta(Document doc, Node dove, String nomeTag, String numeroTag, boolean ordinale) {
		
		//cerco il primo tag corrispondente in profondità
		Node[] posizioniTrovate = UtilDom.getElementsByTagName(doc,dove,nomeTag);
		if (posizioniTrovate.length>0) {
			dove = posizioniTrovate[0];
			//Da qui conto il numero di 'fratelli'
			//Nel caso di partizione espressa come numero CARDINALE e con nodi dom con NUM=null, devo guardare i meta per capire se sono abrogazioni/sostituzioni/integrazioni
			if (ordinale) {
				int conta = 1;
				try {
					conta = new Integer(numeroTag).intValue();
				}
				catch (Exception e) {}
				for (int i=0; i<posizioniTrovate.length; i++)		//restituisco il numeroTag° escludendo i testi Rossi 
					if (isTestoVigente(posizioniTrovate[i])) 
						if (--conta==0)
							return posizioniTrovate[i];
			}
			else { //cardinale
				Node[] dspSostituzione = UtilDom.getElementsByTagName(doc,doc,"dsp:sostituzione");
				int conta = 1;
				try {
					conta = new Integer(numeroTag).intValue();
				}
				catch (Exception e) {}
				if ("".equals(getNum(doc,dove))) {//NUM nulli
					//considero i meta (se è un testo rosso vale come uno nel conteggio solo se NON è una Sostituzione
					for (int i=0; i<posizioniTrovate.length; i++)
						if (isTestoVigente(posizioniTrovate[i])) {
							if (--conta==0)																//<=
								return posizioniTrovate[i];
						}
						else { //cerco nei meta
							String idTag = "#"+UtilDom.getAttributeValueAsString(posizioniTrovate[i],"id");
							boolean nonTrovato = true;
							for (int j=0; j<dspSostituzione.length; j++) {
								Node test[] = UtilDom.getElementsByTagName(doc,UtilDom.getElementsByTagName(doc,dspSostituzione[j],"dsp:novellando")[0],"dsp:pos");
								if (test.length==0) {	//caso abrogazioni
									nonTrovato = false;
									break;
								}
								String idMeta = UtilDom.getAttributeValueAsString(test[0],"xlink:href");
								if (idTag.equals(idMeta)) {
									nonTrovato = false;
									break;
								}
							}
							if (nonTrovato && --conta==0)												//<=
								return posizioniTrovate[i];
						}
				}
				else {
					for (int i=0; i<posizioniTrovate.length; i++)	//restituisco quello con NUM che inizia con numeroTag escludendo i testi Rossi 
						if (isTestoVigente(posizioniTrovate[i])) {
							conta--;
							String numero = getNum(doc,posizioniTrovate[i]).trim();
							if (numero.startsWith(numeroTag) || ("".equals(numero) && conta<=0))   //se trovo uno senza num , che è almeno numeroTag°, allora lo restituisco
								return posizioniTrovate[i];		
						}
				}
			}
		}
		return dove;
	}
	
	private boolean isTestoVigente(Node test) {
		try {
			return (UtilDom.getAttributeValueAsString(test, "finevigore")==null);
		}
		catch (Exception e) {}
		return true;
	}
	
	private String getNum(Document doc, Node test) {
		try {
			String numero = UtilDom.getTextNode(UtilDom.getElementsByTagName(doc,test,"num")[0]);
			//qualche correttivo
			if (numero.toLowerCase().indexOf("unico")!=-1)
				return "1";
			Pattern pattern = Pattern.compile ("[0-9]+");
			Matcher matcher = pattern.matcher (numero);
			while (matcher.find())
				return matcher.group();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String meta2tag(String tag) {
		//(atto|allegato|libro|parte|titolo|capo|sezione|articolo|comma|alinea|coda|lettera|numero|punto|periodo|parole|capoverso|rubrica)
		if (tag.equals("allegato"))
			return "annesso";
		else if (tag.equals("lettera")) 
			return "el";
		else if (tag.equals("numero")) 
			return "en";				
		else if (tag.equals("punto")) 
			return "ep";
		//altri rimangono uguali
		return tag;	
	}
	
	private String tag2id(String tag) {
		if (tag.equals("allegato")) 
			return "ann";
		else if (tag.equals("libro")) 
			return "lib";				
		else if (tag.equals("parte")) 
			return "prt";
		else if (tag.equals("titolo")) 
			return "tit";				
		else if (tag.equals("capo")) 
			return "cap";
		else if (tag.equals("sezione")) 
			return "sez";
		else if (tag.equals("articolo")) 
			return "art";
		else if (tag.equals("comma")) 
			return "com";				
		else if (tag.equals("lettera")) 
			return "let";
		else if (tag.equals("numero")) 
			return "num";				
		else if (tag.equals("punto")) 
			return "pun";
		//default (non dovrebbe arrivare mai)
		if (tag.length()>3)
			return tag.substring(0, 3);
		return tag;
	}
	
	private String id2tag(String id) {
		if (id.equals("ann"))
			return "annesso";
		else if (id.equals("lib"))
			return "libro";
		else if (id.equals("prt"))
			return "parte";
		else if (id.equals("tit"))
			return "titolo";
		else if (id.equals("cap"))
			return "capo";
		else if (id.equals("sez"))
			return "sezione";
		else if (id.equals("art"))
			return "articolo";
		else if (id.equals("com"))
			return "comma";
		else if (id.equals("let"))
			return "el";
		else if (id.equals("num"))
			return "en";
		else if (id.equals("pun"))
			return "ep";
		//default ritorno l'ID
		return id;	
	}
	
	private static String getTesto(Node elem) {

		if (elem.getNodeType() == Node.ELEMENT_NODE && elem.getNodeName() != "num") {
			String text = null;
			NodeList children = elem.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				String child_text = getTesto(children.item(i));
				if (child_text != null) {
					if (text != null)
						text = text + " " + child_text;
					else
						text = child_text;
				}
			}
			return text;
		} else {
			return UtilLang.trimText(elem.getNodeValue());
		}
	}
	
	private static Vector getNodi(Node elem) {
		Vector elements = new Vector();
		org.w3c.dom.NodeList children = elem.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName() != "num")
				elements.add(children.item(i));
		}
		return elements;
	}
	
	private static boolean isNumber(String s) {
		  boolean flag = false;
		  for (int x = 0; x < s.length(); x++) {
		    char c = s.charAt(x);
		    if (x == 0 && (c == '-')) continue;		// Negativo
		    if ((c >= '0') && (c <= '9')) {			// num: 0-9
		    	flag=true; 
		    	continue;
		    }
		    return false; 			//Non è un numero
		  }
		  return flag; 				//E' un numero
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
		
		return "Unicode";
		
//		DeferredDocumentImpl ddi = (DeferredDocumentImpl) document;
//		//return ddi != null ? ddi.getXmlEncoding() : null;
//		if (ddi == null)
//			//return "UTF-8";
//			return "UTF8";
//		if ("UTF-8".equals(ddi.getXmlEncoding()))
//			//return "UTF-8";
//			return "UTF8";
//		return "Cp1250";
		
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

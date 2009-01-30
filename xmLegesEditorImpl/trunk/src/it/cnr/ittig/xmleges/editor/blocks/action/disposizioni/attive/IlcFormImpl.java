package it.cnr.ittig.xmleges.editor.blocks.action.disposizioni.attive;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.domwriter.DOMWriter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;
import it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.attive.DispAttiveFormImpl;
import it.cnr.ittig.xmleges.editor.services.action.disposizioni.attive.IlcForm;
import it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.RiferimentoForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.disposizioni.attive.IlcForm</code>.
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
public class IlcFormImpl implements IlcForm, Loggable, ActionListener, Serviceable, Initializable {
	Logger logger;

	Form form;
	UtilMsg utilMsg;
	
	DocumentManager documentManager;
	EventManager eventManager;
	 
	TransformerFactory factory = TransformerFactory.newInstance();
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	
	JLabel etiBinario;
	JCheckBox binario;
	JLabel etiWebserver;
	JTextField webserver;
	JLabel etiUser;
	JTextField user;
	JButton inviaTutti;
	JButton inviaNuovi;
	JLabel etiMeta;
	JTextArea meta;
	JButton scriviMeta;
	
	Disposizioni domDisposizioni;
	RiferimentoForm riferimentoForm;
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		domDisposizioni  = (Disposizioni) serviceManager.lookup(Disposizioni.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		riferimentoForm = (RiferimentoForm) serviceManager.lookup(RiferimentoForm.class);
	}

	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("Ilc.jfrm"));
		form.setCustomButtons(new String[] { "generic.close" });		
		
		form.setName("editor.form.ilc");
		form.setHelpKey("help.contents.form.ilc");

		etiWebserver = (JLabel) form.getComponentByName("ilc.webserver.eti");
		webserver = (JTextField) form.getComponentByName("ilc.webserver");
		etiBinario = (JLabel) form.getComponentByName("ilc.binario.eti");
		binario = (JCheckBox) form.getComponentByName("ilc.binario");
		binario.setSelected(true);
		
		//non implementato
		binario.setEnabled(false);
		
		webserver.setEnabled(false);
		binario.addActionListener(this);
		etiUser = (JLabel) form.getComponentByName("ilc.user.eti");
		user = (JTextField) form.getComponentByName("ilc.user");
		inviaTutti = (JButton) form.getComponentByName("ilc.invia.tutti");
		inviaTutti.addActionListener(this);	
		inviaNuovi = (JButton) form.getComponentByName("ilc.invia.nuovi");
		inviaNuovi.addActionListener(this);	
		etiMeta = (JLabel) form.getComponentByName("ilc.meta.eti");
		meta = (JTextArea) form.getComponentByName("ilc.meta");
		scriviMeta = (JButton) form.getComponentByName("ilc.scrivi.meta");
		scriviMeta.addActionListener(this);	
		
		form.setSize(600, 630);
		dbf.setValidating(false);
		dbf.setNamespaceAware(false);
		UtilFile.copyFileInTemp(getClass().getResourceAsStream("nir-export-mod.xsl"), "nir-export-mod.xsl");
		
		String[] software = new String[]{"sf-ilc/ittig", "sf-ilc/ittig.exe", "sf-ilc/LESSICO.txt", "sf-ilc/MACRO.txt", "sf-ilc/RULE.txt"};
		// copio in software-ilc
		
		// No. l'eseguibile dell'ilc linka staticamente i vari moduli quindi devo lavorare nella cartalla corrente
		// (ovvero sia l'eseguibile che il file da trattare direttamente nella TEMP)    ---(A)(B)(C)
		
		for (int i = 0; i < software.length; i++)
			//(A) UtilFile.copyFileInTempDir(getClass().getResourceAsStream(software[i]), "software-ilc", software[i]);
			UtilFile.copyFileInTemp(getClass().getResourceAsStream(software[i]), software[i]);

		String[] data = new String[]{"sf-ilc/data/abbrevi_punto_intermedio", "sf-ilc/data/abbrevia", "sf-ilc/data/cond_subst", "sf-ilc/data/match", "sf-ilc/data/match.~1.1.1.1.~", "sf-ilc/data/nomi_propri.txt", "sf-ilc/data/orto", "sf-ilc/data/orto.~1.8.~"};
		// copio in software-ilc/data
		for (int i = 0; i < data.length; i++)
			//(B) UtilFile.copyFileInTempDir(getClass().getResourceAsStream(data[i]), "software-ilc/data", data[i]);
			UtilFile.copyFileInTempDir(getClass().getResourceAsStream(data[i]), "data", data[i]);

		String[] lists = new String[]{"sf-ilc/LISTS/C_IND", "sf-ilc/LISTS/C_LIST", "sf-ilc/LISTS/CL_DATA", "sf-ilc/LISTS/CL_IND", "sf-ilc/LISTS/CL_LIST", "sf-ilc/LISTS/E_DATA", "sf-ilc/LISTS/E_IND1", "sf-ilc/LISTS/E_IND1", "sf-ilc/LISTS/E_IND10", "sf-ilc/LISTS/E_IND11", "sf-ilc/LISTS/E_IND12", "sf-ilc/LISTS/E_IND13", "sf-ilc/LISTS/E_IND14", "sf-ilc/LISTS/E_IND15", "sf-ilc/LISTS/E_IND16", "sf-ilc/LISTS/E_IND17", "sf-ilc/LISTS/E_IND18", "sf-ilc/LISTS/E_IND19", "sf-ilc/LISTS/E_IND2", "sf-ilc/LISTS/E_IND20", "sf-ilc/LISTS/E_IND3", "sf-ilc/LISTS/E_IND4", "sf-ilc/LISTS/E_IND5", "sf-ilc/LISTS/E_IND6", "sf-ilc/LISTS/E_IND7", "sf-ilc/LISTS/E_IND8", "sf-ilc/LISTS/E_IND9", "sf-ilc/LISTS/E_LIST1", "sf-ilc/LISTS/E_LIST10", "sf-ilc/LISTS/E_LIST11", "sf-ilc/LISTS/E_LIST12", "sf-ilc/LISTS/E_LIST13", "sf-ilc/LISTS/E_LIST14", "sf-ilc/LISTS/E_LIST15", "sf-ilc/LISTS/E_LIST16", "sf-ilc/LISTS/E_LIST17", "sf-ilc/LISTS/E_LIST18", "sf-ilc/LISTS/E_LIST19", "sf-ilc/LISTS/E_LIST2", "sf-ilc/LISTS/E_LIST20", "sf-ilc/LISTS/E_LIST3", "sf-ilc/LISTS/E_LIST4", "sf-ilc/LISTS/E_LIST5", "sf-ilc/LISTS/E_LIST6", "sf-ilc/LISTS/E_LIST7", "sf-ilc/LISTS/E_LIST8", "sf-ilc/LISTS/E_LIST9", "sf-ilc/LISTS/E_LIST10", "sf-ilc/LISTS/L_LIST", "sf-ilc/LISTS/MG_C_LIST", "sf-ilc/LISTS/MG_multiwords", "sf-ilc/LISTS/MG_N_LIST", "sf-ilc/LISTS/MG_P_LIST", "sf-ilc/LISTS/R_IND1", "sf-ilc/LISTS/R_IND2", "sf-ilc/LISTS/R_IND3", "sf-ilc/LISTS/R_IND4.1", "sf-ilc/LISTS/R_IND4.1.gz", "sf-ilc/LISTS/R_IND4.2", "sf-ilc/LISTS/R_IND4.2.gz", "sf-ilc/LISTS/R_IND4seq", "sf-ilc/LISTS/R_LIST1", "sf-ilc/LISTS/R_LIST2", "sf-ilc/LISTS/R_LIST3", "sf-ilc/LISTS/R_LIST4", "sf-ilc/LISTS/R_LIST4.1", "sf-ilc/LISTS/R_LIST4.1.gz", "sf-ilc/LISTS/R_LIST4.2", "sf-ilc/LISTS/R_LIST4.2.gz", "sf-ilc/LISTS/R_LIST4seq", "sf-ilc/LISTS/R1_DATA", "sf-ilc/LISTS/R2_DATA", "sf-ilc/LISTS/RR_E_DATA", "sf-ilc/LISTS/RR_R_DATA", "sf-ilc/LISTS/RR_setvar1", "sf-ilc/LISTS/RR_setvar2", "sf-ilc/LISTS/S_DATA", "sf-ilc/LISTS/S_IND", "sf-ilc/LISTS/S_LIST", "sf-ilc/LISTS/setvar", "sf-ilc/LISTS/setvar1", "sf-ilc/LISTS/setvar2", "sf-ilc/LISTS/setvar3", "sf-ilc/LISTS/setvar4"};
		// copio in software-ilc/data
		for (int i = 0; i < lists.length; i++)
			//(C) UtilFile.copyFileInTempDir(getClass().getResourceAsStream(lists[i]), "software-ilc/lists", lists[i]);
			UtilFile.copyFileInTempDir(getClass().getResourceAsStream(lists[i]), "LISTS", lists[i]);
	}

	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == inviaTutti)
			if (binario.isSelected())
				analizzaInLocale("estrazioneTuttiMod.xml");
			else
				comunica("estrazioneTuttiMod.xml");
		
		if (e.getSource() == inviaNuovi) 
			if (binario.isSelected()) 
				analizzaInLocale("estrazioneNuoviMod.xml");
			else
				comunica("estrazioneNuoviMod.xml");
	
		if (e.getSource() == scriviMeta) 
			if (aggiornaMeta())
				form.close();
		
		if (e.getSource() == binario) { 
			boolean ena = !binario.isSelected(); 
			etiWebserver.setEnabled(ena);
			webserver.setEnabled(ena);
			etiUser.setEnabled(ena);
		}

	}

	private void initForm() {
		
		//imposto indirizzo WEBSERVLET (prenderla dalla configurazione) e pulisco altri campi
		webserver.setText("http://nonImplementato");
		user.setText("xmLegesEditor");
		inviaTutti.setEnabled(false);
		inviaNuovi.setEnabled(false);
		meta.setText("");
		scriviMeta.setEnabled(false);

		//estraggo i mod
		DOMWriter domWriter = new DOMWriter();
		domWriter.setCanonical(false);
		domWriter.setFormat(false);
		try {
			domWriter.setOutput(UtilFile.getTempDirName()+ File.separatorChar +"tempEstrazioneMod.xml", documentManager.getEncoding());
			domWriter.write(documentManager.getDocumentAsDom());
			Source xsl = new StreamSource(new FileInputStream(UtilFile.getTempDirName()+ File.separatorChar +"nir-export-mod.xsl"));
			Transformer converti = factory.newTransformer(xsl);
			Source source = new StreamSource(new File(UtilFile.getTempDirName()+ File.separatorChar +"tempEstrazioneMod.xml"));
			//preparo il file con l'estrazione di tutti i mod
			Result dest = new StreamResult(UtilFile.getTempDirName()+ File.separatorChar +"estrazioneTuttiMod.xml");
			converti.setParameter("azione", "tutti");			
			converti.setOutputProperty(OutputKeys.ENCODING,documentManager.getEncoding());
			converti.transform(source,dest);
			//preparo il file con l'estrazione dei mod che non hanno ancora il metadato
			dest = new StreamResult(UtilFile.getTempDirName()+ File.separatorChar +"estrazioneNuoviMod.xml");
			converti.setParameter("azione", "nuovi");
			converti.setOutputProperty(OutputKeys.ENCODING,documentManager.getEncoding());
			converti.transform(source,dest);
			//preparo il file con l'estrazione dei mod che hanno già il metadato
			dest = new StreamResult(UtilFile.getTempDirName()+ File.separatorChar +"estrazioneVecchiMod.xml");
			converti.setParameter("azione", "vecchi");
			converti.setOutputProperty(OutputKeys.ENCODING,documentManager.getEncoding());
			converti.transform(source,dest);
			inviaTutti.setEnabled(true);
			inviaNuovi.setEnabled(true);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}	
	}
	
	public void openForm() {

		initForm();
		form.showDialog();
	}

	private boolean aggiornaMeta() {
		Document doc = documentManager.getDocumentAsDom();
		Document dom = UtilXml.textToXML(meta.getText(), false);
		Element radice = dom.getDocumentElement();
		NodeList disposizioni = radice.getChildNodes();
		EditTransaction t = null;
		try {
			form.setDialogWaiting(true);	
			int conta = 0;
			for (int i=0; i<disposizioni.getLength(); i++) {
				if (disposizioni.item(i).getNodeType()==Node.TEXT_NODE)
					continue;
				conta++;
				t = documentManager.beginEdit();
				
				if (!analizzaMeta(doc, disposizioni.item(i))) {
					form.setDialogWaiting(false);
					utilMsg.msgError("Errore durante l'inserimento dei metadati.\nNon so valutare la " + conta +"° disposizione:\n\n"+UtilDom.domToString(disposizioni.item(i),true,"   "));
					documentManager.rollbackEdit(t);	//non funziona
					form.setDialogWaiting(true);
				}
				else {
					documentManager.setChanged(true);
					documentManager.commitEdit(t);
				}	
			}
		} catch (DocumentManagerException e) {
			logger.error(e.getMessage());
			return false;
		}		
		form.setDialogWaiting(false);
		return true;
	}
	
	private boolean analizzaMeta(Document doc, Node metaIlc) {
		
		try {	
			
			int operazioneIniziale = 0;
			if (metaIlc.getNodeName().equals("dsp:abrogazione"))
				operazioneIniziale = DispAttiveFormImpl.ABROGAZIONE;
			if (metaIlc.getNodeName().equals("dsp:sostituzione"))
				operazioneIniziale = DispAttiveFormImpl.SOSTITUZIONE;
			if (metaIlc.getNodeName().equals("dsp:integrazione"))
				operazioneIniziale = DispAttiveFormImpl.INTEGRAZIONE;
			
		//ricreo setMeta
			Node posIlc = UtilDom.findRecursiveChild(metaIlc,"dsp:pos");
			String idMod = UtilDom.getAttributeValueAsString(posIlc, "xlink:href");
			//recupero il mod e i rif dal documento
			Node modTesto = doc.getElementById(idMod);
			Node[] rif = UtilDom.getElementsByTagName(doc, modTesto, "rif");
			//recupero il rif che contiene la urn della norma
			Node normaIlc = UtilDom.findRecursiveChild(metaIlc,"dsp:norma");
			String idRifNorma = UtilDom.getAttributeValueAsString(normaIlc, "xlink:href");
			int numRif = Integer.parseInt(idRifNorma.substring(3, idRifNorma.length()));
			numRif = -1 + numRif/2;	//corregge un baco del foglio di stile nir-export-mod.xsl (conta 2,4,6.. invece di 1,2,3..)
			String partizione = UtilDom.getAttributeValueAsString(rif[numRif], "xlink:href");
			String urn;
			if (partizione.indexOf("#")!=-1) {
				urn = partizione.substring(0, partizione.indexOf("#"));
				partizione = partizione.substring(partizione.indexOf("#")+1, partizione.length());
			}				
			else {
				urn = partizione;	//Se passa di qua sicuramente ha riconosciuto qualcosa di sbagliato
				partizione ="";
			}
			//controllo se ho bordi aggiuntivi nel testo
			String[] bordiAggiuntivi =null;
			bordiAggiuntivi = riferimentoForm.getBordiDaNota(rif[numRif]);
			//controllo se ho bordi
			Node pun = UtilDom.findRecursiveChild(metaIlc,"ittig:pun");
			String[] delimitatori = bordiAggiuntivi;
			if (pun!=null) {
				int nBordi = -1;
				Node bordo = pun;
				while (bordo != null) {
					bordo = UtilDom.findDirectChild(bordo,"ittig:bordo");
					nBordi++;
				}		
				delimitatori = new String[nBordi*3+bordiAggiuntivi.length];
				for (int j=0; j<nBordi; j++) {
					pun = UtilDom.findDirectChild(pun,"ittig:bordo");
					delimitatori[j*3]= UtilDom.getAttributeValueAsString(pun, "tipo");
					try {
						delimitatori[j*3+1]= UtilDom.getAttributeValueAsString(pun, "num");
						delimitatori[j*3+2]= "";
					}
					catch (Exception e) {
						delimitatori[j*3+1]= UtilDom.getAttributeValueAsString(pun, "ord");
						delimitatori[j*3+2]= "ordinale";
					}
				}
				for (int j=0; j<bordiAggiuntivi.length; j++)
					delimitatori[nBordi*3+j]=bordiAggiuntivi[j];
			}
			//recupero eventuale condizione
			boolean condizionata;
			Node condizione = UtilDom.findRecursiveChild(metaIlc,"dsp:condizione");
			if (condizione!=null)
				condizionata = true;		//per ora non so far di meglio
			else
				condizionata = false;
			//recupero la decorrenza
			Node termine = UtilDom.findRecursiveChild(metaIlc,"dsp:termine");
			String idevento = "t1";
			String decorrenza;
			if (termine!=null) {
				decorrenza = UtilDom.getAttributeValueAsString(termine, "da");			//potrebbe essere 'a'
				//recupero l' id dell'evento attivo con data = decorrenza
				
				
				// TODO: fare	idevento = ...
				
				
				
			}
			else 	//TODO: id = t1  (dovrei però prenderlo da <originale> recuperare fonte evento = id originale)
				decorrenza = UtilDom.getAttributeValueAsString(doc.getElementById("t1"), "data");
			//verifico se il metadato esisteva
			Node modificoMetaEsistenti = null;
			Node modificheAttive = UtilDom.findRecursiveChild(doc,"modificheattive");
			if (modificheAttive!=null) {
				NodeList disposizioni = modificheAttive.getChildNodes();
				if (disposizioni!=null)
					for (int j=0; j<disposizioni.getLength(); j++) {
						try {
							if (("#"+idMod).equals(UtilDom.getAttributeValueAsString(UtilDom.findDirectChild(disposizioni.item(j), "dsp:pos"), "xlink:href"))) {
								modificoMetaEsistenti = disposizioni.item(j);
								break;
							}
						} catch (Exception e) {}
					}
			}
			String completa = "si";
			for (int i=0; i<delimitatori.length; i++)
				if ("capoverso".equals(delimitatori[i]))
					completa="no";
			
			//invoco la prima chiamata alle funzioni DOM	
			Node nuovoMeta = domDisposizioni.setDOMDispAttive(false, modificoMetaEsistenti, "#"+idMod, operazioneIniziale, completa, condizionata, decorrenza, idevento, urn, partizione, delimitatori);
			
		//setmeta di novella
			Node novellaIlc = UtilDom.findRecursiveChild(metaIlc,"dsp:novella");
			if (novellaIlc!=null) {
				String posizione = null;
				String virgolettaA = null;
				String virgolettaB = null;
				Node posizioneIlc = UtilDom.findRecursiveChild(metaIlc,"dsp:posizione");
				if (posizioneIlc!=null) {
					pun = UtilDom.findRecursiveChild(posizioneIlc,"ittig:pun");
					if (pun!=null) {
						virgolettaA = UtilDom.getAttributeValueAsString(pun, "xlink:href");
						try {
							posizione = UtilDom.getAttributeValueAsString(pun, "dove");
						}
						catch (Exception e) {}
						pun = pun.getNextSibling();
						if (pun!=null)
							virgolettaB = UtilDom.getAttributeValueAsString(pun, "xlink:href");

					}			
				}
				
				String tipo = UtilDom.getAttributeValueAsString(UtilDom.findRecursiveChild(novellaIlc,"ittig:tipo"), "valore");
				posIlc = UtilDom.findRecursiveChild(novellaIlc,"dsp:pos");
				String virgolettaContenuto = UtilDom.getAttributeValueAsString(posIlc, "xlink:href");
	
				domDisposizioni.setDOMNovellaDispAttive(nuovoMeta, virgolettaContenuto, tipo, posizione, virgolettaA, virgolettaB);
			}
		
		//setmeta di novellando
			String tipo = "contenuto";
			String tipoPartizione = null; 
			String ruoloA = null; 
			String virgolettaA = null; 
			String ruoloB = null;
			String virgolettaB = null;
			boolean parole=false;
			Node novellandoIlc = UtilDom.findRecursiveChild(metaIlc,"dsp:novellando");
			if (novellandoIlc!=null) {
				tipoPartizione = UtilDom.getAttributeValueAsString(UtilDom.findRecursiveChild(novellandoIlc,"ittig:tipo"), "valore");
				pun = UtilDom.findRecursiveChild(novellandoIlc,"ittig:pun");
				if (pun!=null) {
					virgolettaA = UtilDom.getAttributeValueAsString(pun, "xlink:href");
					try {
						ruoloA = UtilDom.getAttributeValueAsString(pun, "ruolo");
					}
					catch (Exception e) {}
					pun = pun.getNextSibling();
					if (pun!=null) {
						virgolettaB = UtilDom.getAttributeValueAsString(pun, "xlink:href");
						try {
							ruoloB = UtilDom.getAttributeValueAsString(pun, "ruolo");
						}
						catch (Exception e) {}
						pun = pun.getNextSibling();
					}
				}
				if (ruoloA!=null)	
					tipo = "delimitatori";
				//if ("parole".equals(tipo))	
				if ("parole".equals(tipoPartizione)) {
					parole=true;
					tipoPartizione = "parole";
				}
				domDisposizioni.setDOMNovellandoDispAttive(nuovoMeta, parole, tipoPartizione, tipo, ruoloA, virgolettaA, ruoloB, virgolettaB);
			}
			
		} catch (Exception ex) {
			return false;
		}
		return true;
	}
	
	private boolean comunica(String file) {
		
		file = correggiCaratteri(file);
		if (documentManager.getEncoding().toLowerCase().startsWith("utf"))
			file = correggiInterrogativo(file);
		
		URL url;
	    URLConnection urlConn;
	    DataOutputStream printout;

	    try{
	    	url = new URL (webserver.getText());
	    	urlConn = url.openConnection();
	    	urlConn.setDoInput (true);
	    	urlConn.setDoOutput (true);
	    	urlConn.setUseCaches (false);
	    	urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	    	// spedisce al server i dati via POST
	    	printout = new DataOutputStream (urlConn.getOutputStream());
	    	String content = "user=" + URLEncoder.encode (user.getText(),"UTF-8") +
	                         "&xml=" + URLEncoder.encode (UtilFile.fileToString(UtilFile.getTempDirName()+File.separatorChar+file),"UTF-8");
	    	printout.writeBytes (content);
	    	printout.flush ();
	    	printout.close ();

	    	// leggo la risposta
	    	meta.setText("");	
	    	BufferedReader bufline = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
	    	String str;
	    	while (null != ((str = bufline.readLine())))
	    		meta.setText(meta.getText()+str+"\n");	
	    	bufline.close ();

	    }catch(Exception e){
	    	return false;
	    }
	    inviaTutti.setEnabled(false);
		inviaNuovi.setEnabled(false);
		scriviMeta.setEnabled(true);
		return true;
	}
	
	private String correggiCaratteri(String file) {
		/*
		 *  Sostituzioni effettuate: apici word convertiti in ' 
		 */
		String apiceAlto = "\u2018";
		String apiceBasso = "\u2019";
		
        String apiceAltoCodificato="'";
        String apiceBassoCodificato="'";
		try {
			apiceAltoCodificato = new String(apiceAlto.getBytes(documentManager.getEncoding()));
			apiceBassoCodificato = new String(apiceBasso.getBytes(documentManager.getEncoding()));	
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if ("?".equals(apiceAltoCodificato))
			apiceAltoCodificato=apiceAlto;
		if ("?".equals(apiceBassoCodificato))
			apiceBassoCodificato=apiceBasso;
        
		String ilcFile = "ilc_CorCar_"+file;
		String strLine;
		InputStream in;
		OutputStream out;
		try {
			in = new FileInputStream(UtilFile.getTempDirName() + File.separatorChar + file);
			out = new FileOutputStream(UtilFile.getTempDirName() + File.separatorChar + ilcFile);	
			Reader r = new BufferedReader(new InputStreamReader(in, documentManager.getEncoding()));
		    Writer w = new BufferedWriter(new OutputStreamWriter(out, "ISO-8859-1"));
		    char[] buffer = new char[4096];
		    int len;
		    while ((len = r.read(buffer)) != -1) {
		    	strLine = new String(buffer, 0, len);
		    	strLine = strLine.replaceAll(apiceAltoCodificato, "'");
	    		strLine = strLine.replaceAll(apiceBassoCodificato, "'");
		    	w.write(strLine);
		    }
		    r.close();
		    w.close();
		} catch (Exception e1) {
			e1.printStackTrace();
			return file;
		}
		return ilcFile;
	}
	
	private String correggiInterrogativo(String file) {
		/*
		 *  Sostituzioni effettuate:
		 *  carattere ? in bianco 
		 */
		String ilcFile = "ilc_CorPun_"+file;
		String strLine;
		BufferedReader vecchioFile;
		Writer nuovoFile;
	    try{
	    	vecchioFile = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(UtilFile.getTempDirName() + File.separatorChar + file))));
	    	nuovoFile = new OutputStreamWriter(new FileOutputStream(UtilFile.getTempDirName() + File.separatorChar + ilcFile), "ISO-8859-1");

	    	while ((strLine = vecchioFile.readLine())!=null) {
	    		strLine = strLine.replace('?', ' ');
	    		nuovoFile.write(strLine+"\n");
	    	}
    	    vecchioFile.close();    
    	    nuovoFile.close();
	    } catch (Exception e){
	         System.err.println("Error: " + e.getMessage());
	    }
		return ilcFile;

	}
	
	private boolean analizzaInLocale(String file) {

		form.setDialogWaiting(true);
		file = correggiCaratteri(file);
		if (documentManager.getEncoding().toLowerCase().startsWith("utf"))
			file = correggiInterrogativo(file);
		
		String eseguibileIlc = "";
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().matches("windows.*"))
			eseguibileIlc = "ittig.exe";
		else
			eseguibileIlc = "ittig";
		
		File command = new File(UtilFile.getTempDirName() + File.separatorChar + eseguibileIlc);
		UtilFile.setExecPermission(command);
		
		try {
			String toExec = command.toString() + " -i " + UtilFile.getTempDirName() + File.separatorChar + file + " -p " + UtilFile.getTempDirName();
			logger.debug("command=" + toExec);
			Process p = Runtime.getRuntime().exec(toExec);
			p.waitFor();
			//leggo il file di risposta
			meta.setText("");
			BufferedReader r = new BufferedReader(new FileReader("ittig.xml"));
			String line;
			while ((line = r.readLine())!=null)
				meta.setText(meta.getText()+line+"\n");

		} catch (Exception ex) {
			logger.error("error", ex);
		}
	    inviaTutti.setEnabled(false);
		inviaNuovi.setEnabled(false);
		scriviMeta.setEnabled(true);
		form.setDialogWaiting(false);
		return true;
	}
}

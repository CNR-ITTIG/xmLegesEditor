package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.attive;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DecorrenzaForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DelimitatoreForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DispAttiveForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.NovellaForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.NovellandoForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.RiferimentoForm;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.partizioni.PartizioniForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive</code>.
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
public class DispAttiveFormImpl implements DispAttiveForm, EventManagerListener, Loggable, ActionListener, Serviceable, Initializable, FormClosedListener {

	Logger logger;
	EventManager eventManager;
	UtilMsg utilmsg;
	Form form;
	JLabel info;
	JLabel metaPresente;
	JLabel etiDecorenza;
	JLabel etiAtto;
	JLabel etiPartizione;
	JLabel etiInteroAtto;
	JLabel etiDelimitatori;
	JButton sceltaDecorrenza;
	JButton sceltaAtto;
	JButton sceltaPartizione;
	JButton sceltaDelimitatore;
	JTextField decorrenza;
	JTextField atto;
	JTextField partizione;
	JList delimitatori;
	DefaultListModel listModel = new DefaultListModel();
	JButton abrogazione;
	JButton sostituzione;
	JButton integrazione;
	JRadioButton interoAtto;
	JRadioButton soloPartizione;
	
	JCheckBox implicita;

	PartizioniForm partizioniForm;
	DecorrenzaForm decorrenzaForm;
	RiferimentoForm riferimentoForm;
	DelimitatoreForm delimitatoreForm;
	NovellandoForm novellandoForm;
	NovellaForm novellaForm;

	Node activeNode;	
	Node modNode;
	DocumentManager documentManager;
	SelectionManager selectionManager;

	int operazioneIniziale;
	int operazioneCorrente;
	int operazioneProssima;

	NirUtilDom nirUtilDom;
	
	String[] delimitatoriScelti;
	
	boolean listenerFormClosed;
	Node modificoMetaEsistenti=null;
	Disposizioni domDisposizioni;
	String modCorrente;
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		partizioniForm = (PartizioniForm) serviceManager.lookup(PartizioniForm.class);
		decorrenzaForm = (DecorrenzaForm) serviceManager.lookup(DecorrenzaForm.class);
		riferimentoForm = (RiferimentoForm) serviceManager.lookup(RiferimentoForm.class);
		delimitatoreForm = (DelimitatoreForm) serviceManager.lookup(DelimitatoreForm.class);
		novellandoForm = (NovellandoForm) serviceManager.lookup(NovellandoForm.class);
		novellaForm = (NovellaForm) serviceManager.lookup(NovellaForm.class);
		domDisposizioni  = (Disposizioni) serviceManager.lookup(Disposizioni.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, DocumentClosedEvent.class);
		form.setMainComponent(this.getClass().getResourceAsStream("DispAttive.jfrm"));
		form.setName("editor.form.disposizioni.attive");
		form.setCustomButtons(new String[] {"generic.close" });
		form.setHelpKey("help.contents.form.disposizioniattive");

		info = (JLabel) form.getComponentByName("editor.disposizioni.attive.info");
		metaPresente = (JLabel) form.getComponentByName("editor.disposizioni.attive.metapresente");
 		etiDecorenza = (JLabel) form.getComponentByName("editor.disposizioni.attive.decorrenza.eti");
		etiAtto = (JLabel) form.getComponentByName("editor.disposizioni.attive.atto.eti");
		etiPartizione = (JLabel) form.getComponentByName("editor.disposizioni.attive.partizione.eti");
		etiInteroAtto = (JLabel) form.getComponentByName("editor.disposizioni.attive.interoatto.eti");
		etiDelimitatori = (JLabel) form.getComponentByName("editor.disposizioni.attive.delimitatori.eti");
		sceltaDecorrenza = (JButton) form.getComponentByName("editor.disposizioni.attive.decorrenza.scelta");
		sceltaAtto = (JButton) form.getComponentByName("editor.disposizioni.attive.atto.scelta");
		sceltaPartizione = (JButton) form.getComponentByName("editor.disposizioni.attive.partizione.scelta");
		sceltaDelimitatore = (JButton) form.getComponentByName("editor.disposizioni.attive.delimitatori.scelta");
		decorrenza = (JTextField) form.getComponentByName("editor.disposizioni.attive.decorrenza");
		atto = (JTextField) form.getComponentByName("editor.disposizioni.attive.atto");
		partizione = (JTextField) form.getComponentByName("editor.disposizioni.attive.partizione");
		delimitatori = (JList) form.getComponentByName("editor.disposizioni.attive.delimitatori");
		delimitatori.setModel(listModel);
		abrogazione = (JButton) form.getComponentByName("editor.disposizioni.attive.abrogazione");
		sostituzione = (JButton) form.getComponentByName("editor.disposizioni.attive.sostituzione");
		integrazione = (JButton) form.getComponentByName("editor.disposizioni.attive.integrazione");
		
		implicita = (JCheckBox) form.getComponentByName("editor.disposizioni.attive.implicita");

		interoAtto = (JRadioButton) form.getComponentByName("editor.disposizioni.attive.sceltainteroatto");
		soloPartizione = (JRadioButton) form.getComponentByName("editor.disposizioni.attive.sceltapartizione");
		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(interoAtto);
		grupporadio.add(soloPartizione);
		interoAtto.setSelected(true);
		
		abrogazione.addActionListener(this);
		sostituzione.addActionListener(this);
		integrazione.addActionListener(this);
		sceltaDecorrenza.addActionListener(this);
		sceltaAtto.addActionListener(this);
		sceltaPartizione.addActionListener(this);
		sceltaDelimitatore.addActionListener(this);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == interoAtto) {
			delimitatori.setEnabled(false);
			sceltaDelimitatore.setEnabled(false);
			sceltaPartizione.setEnabled(false);
		}
		
		if (e.getSource() == soloPartizione){
			delimitatori.setEnabled(true);
			sceltaDelimitatore.setEnabled(true);
			sceltaPartizione.setEnabled(true);
		}
		
		if (e.getSource() == sceltaDecorrenza) 
			if (decorrenzaForm.openForm())
				decorrenza.setText(decorrenzaForm.getDecorrenza());
		
		if (e.getSource() == sceltaAtto) 
			if (riferimentoForm.openForm())
				atto.setText(riferimentoForm.getRiferimento());
		
		if (e.getSource() == sceltaPartizione) {
			if (partizioniForm.openForm()) {
				String partizioneSelezionata = partizioniForm.getPartizioneEstesa();
				partizione.setText(makeSub(partizioneSelezionata));
			}	
		}
		
		if (e.getSource() == sceltaDelimitatore) {
			if (delimitatoreForm.openForm(delimitatoriScelti)) {
				delimitatoriScelti = delimitatoreForm.getDelimitatore();
				listModel.clear();
				for (int i=0; i<delimitatoriScelti.length/3; i++)
					listModel.addElement(delimitatoriScelti[i*3] + " " + delimitatoriScelti[i*3+1] + " " + delimitatoriScelti[i*3+2]);
			}
		}	
		
		if (e.getSource() == abrogazione || e.getSource() == sostituzione || e.getSource() == integrazione) {
			// verifiche per proseguire con la seconda maschera
			if ("".equals(decorrenza.getText().trim()))
				utilmsg.msgInfo("editor.disposizioni.attive.nodecorrenza");
			else if ("".equals(atto.getText().trim()))
				utilmsg.msgInfo("editor.disposizioni.attive.noatto");
			else if (soloPartizione.isSelected() && "".equals(partizione.getText().trim()))
				utilmsg.msgInfo("editor.disposizioni.attive.nopartizione");
			else {
				if (e.getSource() == abrogazione) {
					operazioneIniziale = ABROGAZIONE;
					operazioneCorrente = NOVELLANDO;
					operazioneProssima = NOVELLANDO;
					form.close();
					novellandoForm.openForm(this,modificoMetaEsistenti);
				}
				if (e.getSource() == sostituzione) {
					operazioneIniziale = SOSTITUZIONE;
					operazioneCorrente = NOVELLANDO;
					operazioneProssima = NOVELLANDO;
					form.close();
					novellandoForm.openForm(this,modificoMetaEsistenti);
				}
				if (e.getSource() == integrazione) {
					operazioneIniziale = INTEGRAZIONE;
					operazioneCorrente = NOVELLA;
					operazioneProssima = NOVELLA;
					form.close();
					novellaForm.openForm(this,modificoMetaEsistenti);
				}
			}
		}
	}

	public Node getModCorrente() {
		return modNode;
	}
	
	public void openForm(boolean cancellaCampi) {
		
		if (form.isDialogVisible())
			return;		//questa form è già aperta
		if (operazioneIniziale != NO_OPERAZIONE )
			return;		//una form successiva è già aperta
		
		Node nodoAttivo = selectionManager.getActiveNode();
		modNode = UtilDom.findParentByName(nodoAttivo, "mod");
		
		if (cancellaCampi) {
			implicita.setSelected(false);
			interoAtto.setSelected(true);
			decorrenzaForm.initForm(nodoAttivo);
			riferimentoForm.initForm(nodoAttivo);
			delimitatoreForm.setDelimitatore();
			//se ho metadati per questo MOD, li setto.
			modificoMetaEsistenti = trovaMeta(nodoAttivo);
			if (modificoMetaEsistenti != null)
				recuperaMeta(modificoMetaEsistenti);
			else {
				metaPresente.setText("NB: metadati non presenti");
				info.setText("Inserisci i metadati della modifica attiva:");
				decorrenza.setText(decorrenzaForm.getDecorrenza());
				atto.setText(riferimentoForm.getRiferimento());
				partizione.setText(riferimentoForm.getPartizionePrimoAtto());
				listModel.clear();
				//delimitatoriScelti=new String[0];	recupero delimitatori dal commento <!-- frammento:part1-part2-->
				delimitatoriScelti= riferimentoForm.getBordi();
				listModel.clear();
				for (int i=0; i<delimitatoriScelti.length/3; i++)
					listModel.addElement(delimitatoriScelti[i*3] + " " + delimitatoriScelti[i*3+1] + " " + delimitatoriScelti[i*3+2]);
			}
			if (!"".equals(partizione.getText()))
				soloPartizione.setSelected(true);
		}	

		activeNode = null;
		operazioneIniziale = NO_OPERAZIONE;
		operazioneProssima = NO_OPERAZIONE;
		form.setSize(420, 440);
		form.showDialog(false);
	}
		
	private void recuperaMeta(Node disposizione) {
		if ("si".equals(UtilDom.getAttributeValueAsString(disposizione, "implicita")))
			implicita.setSelected(true);
		metaPresente.setText("NB: metadati già presenti (" + disposizione.getLocalName() + ")");
		info.setText("Cambia i metadati della modifica attiva:");
		//setto Decorrenza
		Node termine = UtilDom.findRecursiveChild(disposizione,"dsp:termine");
		decorrenza.setText("");
		try { 
			Node attributo = termine.getAttributes().getNamedItem("da");
			if (attributo==null)
				 attributo = termine.getAttributes().getNamedItem("a");
			String valAttributo = attributo.getNodeValue();
			valAttributo = valAttributo.substring(1, valAttributo.length());
			Document doc = documentManager.getDocumentAsDom();
			Node evento = doc.getElementById(valAttributo);
			valAttributo = UtilDom.getAttributeValueAsString(evento, "data");
			decorrenza.setText(UtilDate.normToString(valAttributo));
		} catch (Exception e) {}
		Node norma = UtilDom.findRecursiveChild(disposizione,"dsp:norma");
		//setto Atto e Partizione
		partizione.setText("");
		atto.setText("");
		try {
			Node pos = UtilDom.findRecursiveChild(norma,"dsp:pos");
			String valore = UtilDom.getAttributeValueAsString(pos,"xlink:href");
			if (valore.indexOf("#")!=-1) {
				partizione.setText(valore.substring(valore.indexOf("#")+1,valore.length()));
				atto.setText(valore.substring(0, valore.indexOf("#")));
			}	
			else 
				atto.setText(valore);
		} catch (Exception e) {}
		//setto Delimitatori
		try {
			delimitatoriScelti= prendiDelimitatori(new String[0], UtilDom.findRecursiveChild(norma,"ittig:bordo"));
		} catch (Exception e) {
			delimitatoriScelti=new String[0];
		}
		listModel.clear();
		for (int i=0; i<delimitatoriScelti.length/3; i++)
			listModel.addElement(delimitatoriScelti[i*3] + " " + delimitatoriScelti[i*3+1] + " " + delimitatoriScelti[i*3+2]);
	}
	
	private String[] prendiDelimitatori(String[] attuale, Node bordo) {
		if (bordo==null)
			return attuale;
		else {
			int lunghezza = attuale.length;
			String[] nuova = new String[lunghezza+3];
			for (int i=0; i<lunghezza; i++)
				nuova[i] = attuale [i];
			try {
				nuova[lunghezza] = UtilDom.getAttributeValueAsString(bordo,"tipo");
				nuova[lunghezza+1] = UtilDom.getAttributeValueAsString(bordo,"num");
				if (nuova[lunghezza+1]==null) {
					nuova[lunghezza+1] = UtilDom.getAttributeValueAsString(bordo,"ord");
					nuova[lunghezza+2] = "ordinale";
				}
				else 
					nuova[lunghezza+2] = "";
			} catch (Exception e) {}	
			return prendiDelimitatori(nuova, bordo.getFirstChild());
		} 
	}
	
	private Node trovaMeta(Node nodoAttivo) {
		Document doc = documentManager.getDocumentAsDom(); 
		modCorrente = "";
		try {
			modCorrente = "#"+UtilDom.getAttributeValueAsString(UtilDom.findParentByName(nodoAttivo, "mod"),"id");
		} catch (Exception e) {
			return null;
		}	
		Node activeMeta = nirUtilDom.findActiveMeta(doc,nodoAttivo);
		Node modificheAttive = UtilDom.findRecursiveChild(activeMeta,"modificheattive");
		if (modificheAttive==null)
			return null;
		NodeList disposizioni = modificheAttive.getChildNodes();
		if (disposizioni==null)
			return null;
		for (int j=0; j<disposizioni.getLength(); j++)
			try {
				if (modCorrente.equals(UtilDom.getAttributeValueAsString(UtilDom.findDirectChild(disposizioni.item(j), "dsp:pos"), "xlink:href")))
					return disposizioni.item(j);
			} catch (Exception e) {}
		return null;
	}
	
	private String trovaEvento(Node nodoAttivo) {
		String data = UtilDate.dateToNorm(UtilDate.textualFormatToDate(decorrenza.getText()));
		
		Document doc = documentManager.getDocumentAsDom();
		Node activeMeta = nirUtilDom.findActiveMeta(doc,nodoAttivo);
		Node eventi = UtilDom.findRecursiveChild(activeMeta,"eventi");
		Node relazioni = UtilDom.findRecursiveChild(activeMeta,"relazioni");
		NodeList eventiList = eventi.getChildNodes();
		NodeList relazioniList = relazioni.getChildNodes();
		for (int i = 0; i < relazioniList.getLength(); i++) {
			Node relazioneNode = relazioniList.item(i);
			if ("attiva".equals(relazioneNode.getNodeName()))
				if (atto.getText().equals(UtilDom.getAttributeValueAsString(relazioneNode, "xlink:href"))) {
					
					//manca ancora test su data !!!!!!!!!!!!!!!
					
					String id = UtilDom.getAttributeValueAsString(relazioneNode, "id");
					for (int j = 0; j < eventiList.getLength(); j++)
						if (id.equals(UtilDom.getAttributeValueAsString(eventiList.item(j), "fonte")))
							//return UtilDate.normToString(UtilDom.getAttributeValueAsString(eventiList.item(j), "data"));
							return UtilDom.getAttributeValueAsString(eventiList.item(j), "id");
				}
		}
		return null;	//evento non trovato
	}
	
	
	public void setMeta() {
			
		
		EditTransaction t = null;
		try {
			t = documentManager.beginEdit();	
			
			//decorrenza
			String termine = decorrenza.getText();
			String idevento=null;
			if (!decorrenzaForm.isDecorrenzaCondizionata()) 	//voglio l'id dell'evento con data 'termine'.
				idevento = trovaEvento(selectionManager.getActiveNode()); 										
			
			String completa = "si"; 
			if (decorrenzaForm.isDecorrenzaCondizionata())
				completa = "no";
			else {
				String[] bordi = delimitatoreForm.getDelimitatore();
				for (int i=0; i<bordi.length; i++)
					if ("capoverso".equals(bordi[i]))
						completa="no";	
			}
			
			Node nuovoMeta = domDisposizioni.setDOMDispAttive(implicita.isSelected(), modificoMetaEsistenti, modCorrente, operazioneIniziale, completa, decorrenzaForm.isDecorrenzaCondizionata(), termine, idevento, atto.getText(), partizione.getText(), delimitatoreForm.getDelimitatore());
			
			if (operazioneIniziale!=ABROGAZIONE)
				novellaForm.setMeta(nuovoMeta);
			if (operazioneIniziale!=INTEGRAZIONE) {
				String tipo = null;
				if (interoAtto.isSelected())
					tipo = "atto";
				else {
					tipo = partizione.getText();
					if (listModel.getSize()>0) 
						tipo = delimitatoreForm.getDelimitatore()[delimitatoreForm.getDelimitatore().length-3];
					else {
						String[] scelteTipo = new String[] {"allegato","libro","parte","titolo","capo","sezione","articolo","comma","lettera","numero","punto","periodo","parole"};
						//cerco l'ultimo pezzo dell'id
						int pos = tipo.lastIndexOf("-");
						if (pos!=-1)
							tipo = tipo.substring(pos+1);
						if (tipo.length()>3)
							tipo = tipo.substring(0, 3);
						for (int i=0; i<scelteTipo.length; i++)
							if (tipo.equals(scelteTipo[i].substring(0, 3))) {
								tipo = scelteTipo[i];
								break;
							}
					}
				}
				novellandoForm.setMeta(nuovoMeta, tipo);
			}
			documentManager.commitEdit(t);
		} catch (Exception ex) {
			documentManager.rollbackEdit(t);
		}
		operazioneIniziale = NO_OPERAZIONE;
	}
	
	public void setListenerFormClosed(boolean flag) {
		listenerFormClosed = flag;
	}
	
	public void formClosed() {

		if (!listenerFormClosed)
			return;					//sto aprendo maschere diverse da Novella/Novellando
		
		if (operazioneCorrente==operazioneProssima) {	//Ho scelto Indietro o chiuso con X
			if (operazioneProssima == NOVELLA) {
				if (operazioneIniziale == SOSTITUZIONE) 
					operazioneProssima = NOVELLANDO;
				else
					operazioneProssima = INIZIO;
				
			} else if (operazioneProssima == NOVELLANDO) {
				operazioneProssima = INIZIO;
			}
		}
		
		if (operazioneProssima==NOVELLANDO) {
			operazioneCorrente = NOVELLANDO;
			novellandoForm.openForm(this,modificoMetaEsistenti);
		}	
		if (operazioneProssima==NOVELLA) {
			operazioneCorrente = NOVELLA;
			novellaForm.openForm(this,modificoMetaEsistenti);
		}	
		
		if (operazioneProssima == INIZIO) {
			operazioneIniziale = NO_OPERAZIONE;
			openForm(false);
		}
		
		if (operazioneProssima == FINE) 
			setMeta();
	}

	public String getPartizione() {
		return partizione.getText();
	}
	
	public String[] getDelimitatori() {
		return delimitatoriScelti;
	}

	private String makeSub(String partizione) {
		StringTokenizer st = new StringTokenizer(partizione, " ");
		String token;
		String ret ="";
		boolean dispari = ((st.countTokens() % 2)==0) ? true : false;
		
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (dispari) {
				dispari = false;
				if (token.length()>3)
					ret = ret + token.substring(0, 3).toLowerCase();
				else ret = ret + token.toLowerCase();	//non dovrebbe capitare mai
			}
			else {
				dispari = true;
				ret = ret + token.toLowerCase() + "-";
			}
		}	
		if (ret.length()>1)
			return ret.substring(0, ret.length()-1);
		else
			return ret;
	}
	
	public void manageEvent(EventObject event) {
		if (event instanceof DocumentClosedEvent) 
			if (form.isDialogVisible())
				form.close();
	}

	public void setOperazioneProssima(int operazione) {
		operazioneProssima=operazione;
	}

	public int getOperazioneIniziale() {
		return operazioneIniziale;
	}
}

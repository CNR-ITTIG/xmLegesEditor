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
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;
import it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.MetaCiclodivita;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.Vigenza;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DispAttiveForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.NovellandoNovellaForm;
import it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita.CiclodiVitaForm;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.partizioni.PartizioniForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.StringTokenizer;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

import org.w3c.dom.Node;

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
	NirUtilDom nirUtilDom;
	NirUtilUrn nirUtilUrn;
	EventManager eventManager;
	UtilUI utilui;
	UtilMsg utilmsg;
	MetaCiclodivita ciclodivita;
	CiclodiVitaForm ciclodivitaForm;
	int eventoselezionato = -1;
	Form form;
	JLabel finevigoretesto;
	JLabel datatesto;
	JButton abrogazione;
	JButton sostituzione;
	JButton integrazione;
	JButton sceltaevento;
	JTextField evento;
	JLabel dovetesto;
	JButton sceltadove;
	JTextField dove;
	JLabel implicitatesto;
	JCheckBox implicita; 
	PartizioniForm partizioniForm;
	NovellandoNovellaForm novellandoNovellaForm;
	Node activeNode;
	VigenzaEntity vigenzaEntity;
	Vigenza vigenza;
	String errorMessage = "";
	
	DocumentManager documentManager;

	int operazioneIniziale;
	int operazioneCorrente;
	int operazioneProssima;
	
	String partizione="";
	String posDisposizione = "";
	String idNovellando = "";
	String idNovella = "";
	String preNota = "";
	String autoNota = "";
	String status;
	
	Disposizioni domDisposizioni;
	Evento eventoriginale;
	Evento eventovigore;
	
	JTextField data;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		utilui = (UtilUI) serviceManager.lookup(UtilUI.class);
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		ciclodivita = (MetaCiclodivita) serviceManager.lookup(MetaCiclodivita.class);
		partizioniForm = (PartizioniForm) serviceManager.lookup(PartizioniForm.class);
		ciclodivitaForm = (CiclodiVitaForm) serviceManager.lookup(CiclodiVitaForm.class);
		novellandoNovellaForm = (NovellandoNovellaForm) serviceManager.lookup(NovellandoNovellaForm.class);
		domDisposizioni  = (Disposizioni) serviceManager.lookup(Disposizioni.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, DocumentClosedEvent.class);
		form.setMainComponent(this.getClass().getResourceAsStream("DispAttive.jfrm"));
		form.setName("editor.form.disposizioni.attive");
		form.setCustomButtons(new String[] { "editor.form.disposizioni.attive.btn.cancel" });
		form.setHelpKey("help.contents.form.disposizioniattive");
		finevigoretesto = (JLabel) form.getComponentByName("editor.disposizioni.attive.finevigoretesto");
		datatesto = (JLabel) form.getComponentByName("editor.disposizioni.attive.datatesto");
		abrogazione = (JButton) form.getComponentByName("editor.disposizioni.attive.abrogazione");
		sostituzione = (JButton) form.getComponentByName("editor.disposizioni.attive.sostituzione");
		integrazione = (JButton) form.getComponentByName("editor.disposizioni.attive.integrazione");
		abrogazione.addActionListener(this);
		sostituzione.addActionListener(this);
		integrazione.addActionListener(this);
		evento = (JTextField) form.getComponentByName("editor.disposizioni.attive.evento");
		sceltaevento = (JButton) form.getComponentByName("editor.disposizioni.attive.sceltaevento");
		sceltaevento.addActionListener(this);
		dovetesto = (JLabel) form.getComponentByName("editor.disposizioni.attive.dovetesto");
		dove = (JTextField) form.getComponentByName("editor.disposizioni.attive.dove");
		sceltadove = (JButton) form.getComponentByName("editor.disposizioni.attive.sceltadove");
		sceltadove.addActionListener(this);
		
		implicitatesto = (JLabel) form.getComponentByName("editor.disposizioni.attive.implicitatesto");
		implicita = (JCheckBox)  form.getComponentByName("editor.disposizioni.attive.implicita");
		data = (JTextField) form.getComponentByName("editor.disposizioni.attive.data");

	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sceltaevento) {
			ciclodivita.setActiveNode(activeNode);
			Evento[] eventiOnDom = ciclodivita.getEventi();
			ciclodivitaForm.setEventi(eventiOnDom);
			
			if (ciclodivitaForm.openForm()) {
				eventoselezionato = ciclodivitaForm.getEventoSelezionato();
				if (eventoselezionato != -1) {	
					if (eventiOnDom.length!=0)
						eventoriginale = eventiOnDom[0];
					else
						eventoriginale = ciclodivitaForm.getEventi()[0];
					eventovigore=ciclodivitaForm.getEventi()[eventoselezionato];
					if (eventovigore.getFonte().getTagTipoRelazione().equalsIgnoreCase("attiva")) {
						evento.setText(eventovigore.getFonte().getLink());
						data.setText(UtilDate.normToString(eventovigore.getData()));
					}	
					else {
						evento.setText(eventovigore.getFonte().getLink());
						utilmsg.msgInfo("Dovresti selezionare un evento attivo");
						evento.setText("");
						data.setText("");
					}	
				}	
				Evento[] newEventi = ciclodivitaForm.getEventi();
				Relazione[] newRelazioni = null;
				if(newEventi!=null){
					newRelazioni = new Relazione[newEventi.length];
					for(int i=0;i<newEventi.length;i++){
						newRelazioni[i]=newEventi[i].getFonte();
					}
				}
				ciclodivita.setCiclodiVita(newEventi,newRelazioni);
	   		    if (ciclodivitaForm.getVigToUpdate()!=null && ciclodivitaForm.getVigToUpdate().length>0) {
	   		    	VigenzaEntity[] elenco =ciclodivitaForm.getVigToUpdate();
	   		    	for(int i=0; i<elenco.length;i++)
	   		    		vigenza.updateVigenzaOnDoc(elenco[i]);
	   		    }
			}
		}
		if (e.getSource() == sceltadove) {
			partizioniForm.openForm();
			if (partizioniForm.getPartizioneEstesa().length() > 0) {
				partizione = partizioniForm.getPartizioneEstesa();
				dove.setText(makeSub(partizione));
			}	
		}
		if (e.getSource() == abrogazione || e.getSource() == sostituzione
				|| e.getSource() == integrazione) {
			// verifiche per proseguire con la seconda maschera
			if (evento.getText().equals(""))
				utilmsg.msgInfo("editor.form.disposizioni.attive.noevento");
			else {
				
				
				//eliminare questa gestione ormai inutile
				
				
				if (e.getSource() == abrogazione) {
					operazioneIniziale = ABROGAZIONE;
					operazioneCorrente = NOVELLANDO;
					operazioneProssima = NOVELLANDO;
					form.close();
					novellandoNovellaForm.openForm(this);
				}
				if (e.getSource() == sostituzione) {
					operazioneIniziale = SOSTITUZIONE;
					operazioneCorrente = NOVELLANDO;
					operazioneProssima = NOVELLANDO;
					form.close();
					novellandoNovellaForm.openForm(this);
				}
				if (e.getSource() == integrazione) {
					operazioneIniziale = INTEGRAZIONE;
					operazioneCorrente = NOVELLA;
					operazioneProssima = NOVELLA;
					form.close();
					novellandoNovellaForm.openForm(this);
				}
			}
		}
	}

	public void openForm(boolean cancellaCampi) {
		
		if (form.isDialogVisible())
			return;		//questa form è già aperta
		if (operazioneIniziale != NO_OPERAZIONE )
			return;		//una form successiva è già aperta
			
		idNovellando="";
		idNovella="";
		if (cancellaCampi) {
			evento.setText("");
			data.setText("");
			dove.setText("");
			partizione="";
			implicita.setSelected(false);
		}	
		posDisposizione="";
		activeNode = null;
		operazioneIniziale = NO_OPERAZIONE;
		operazioneProssima = NO_OPERAZIONE;
		form.setSize(420, 280);
		form.showDialog(false);
	}

	public int getTipoDisposizione() {
		return operazioneIniziale;
	}
	
	public void setPosdisposizione(Node nodo) {
		if (posDisposizione.equals(""))				//il controllo sul "" non dovrebbe essere più necessario perchè la sostituzione ora non agisce più sulla maschera della novella ........ CONTROLLARE
			if (nirUtilDom.getNirContainer(nodo)!=null)
				posDisposizione = UtilDom.getAttributeValueAsString(nirUtilDom.getNirContainer(nodo), "id");
			else
				posDisposizione = "??";
	}
	
	public void setNovellando(String id) {
		idNovellando = (id!=null) ? id : "";
	}
	
	public void setNovella(String id) {
		idNovella = (id!=null) ? id : "";
	}
		
	public void setOperazioneProssima() {
		operazioneProssima = FINE;					//ELIMINARE IL METODO !!!!!!!!!!!!!!!!
	}
	
	public void formClosed() {

		
		
		///////////////////non ha + senso
		
		if (operazioneCorrente==operazioneProssima) {	//Ho scelto Indietro o chiuso con X
			if (operazioneProssima == NOVELLA) {
				operazioneProssima = INIZIO;				
			}
		    else 
		    	if (operazioneProssima == NOVELLANDO) {
		    		operazioneProssima = INIZIO;
		    	}
		}
		
		
		
		//eliminare questa gestione
		if (operazioneProssima==NOVELLANDO) {
			operazioneCorrente = NOVELLANDO;
			novellandoNovellaForm.openForm(this);
		}
		//eliminare questa gestione
		if (operazioneProssima==NOVELLA) {
			operazioneCorrente = NOVELLA;
			novellandoNovellaForm.openForm(this);	
		}	
		
		
		if (operazioneProssima == FINE) {
			
			String urn = eventovigore.getFonte().getLink();
			try {
				urn = nirUtilUrn.getFormaTestuale(new Urn(urn));
				if (!partizione.equals("")) 
					urn = partizione + " " + urn;
			} catch (Exception e) {}
			autoNota = urn;
			
			String partizione = evento.getText();
			if (!dove.getText().equals("")) 
				partizione = partizione + "#" + dove.getText();	//TODO testare se ho già una urn con partizioni specificate e decidere come comportarsi
			
			if (!domDisposizioni.setDOMDispAttive(posDisposizione, evento.getText(), partizione, idNovellando, idNovella, autoNota, implicita.isSelected()))
				utilmsg.msgError("editor.form.disposizioni.attive.erroremetadati");
			
			operazioneIniziale = NO_OPERAZIONE;
		}
		if (operazioneProssima == INIZIO) {
			operazioneIniziale = NO_OPERAZIONE;
			openForm(false);
		}
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
	
}

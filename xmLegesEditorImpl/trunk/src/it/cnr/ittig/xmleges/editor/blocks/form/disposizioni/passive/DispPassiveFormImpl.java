package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.passive;

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
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.DispPassiveForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.NovellandoForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.NovellaForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.NotaForm;
import it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita.CiclodiVitaForm;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.partizioni.PartizioniForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

import org.w3c.dom.Node;

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
public class DispPassiveFormImpl implements DispPassiveForm, EventManagerListener, Loggable, ActionListener, Serviceable, Initializable, FormClosedListener {

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
	JButton abrogazione;
	JButton sostituzione;
	JButton integrazione;
	JButton sceltaevento;
	JTextField evento;
	JLabel dovetesto;
	JButton sceltadove;
	JTextField dove;
	PartizioniForm partizioniForm;
	NovellandoForm novellandoForm;
	NovellaForm novellaForm;
	NotaForm notaForm;
	Node activeNode;
	VigenzaEntity vigenzaEntity;
	Vigenza vigenza;
	String errorMessage = "";
	
	DocumentManager documentManager;

	int operazioneIniziale;
	int operazioneCorrente;
	int operazioneProssima;
	
	String posDisposizione = "";
	String idNovellando = "";
	String iniziovigoreNovellando;
	String finevigoreNovellando;
	String statusNovellando;
	boolean nuovoNovellando;
	String idNovella = "";
	String preNota = "";
	String autoNota = "";
	String postNota = "";
	boolean implicita;
	String status;
	
	Disposizioni domDisposizioni;
	Evento eventoriginale;
	Evento eventovigore;
	
	

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
		novellandoForm = (NovellandoForm) serviceManager.lookup(NovellandoForm.class);
		novellaForm = (NovellaForm) serviceManager.lookup(NovellaForm.class);
		notaForm = (NotaForm) serviceManager.lookup(NotaForm.class);
		domDisposizioni  = (Disposizioni) serviceManager.lookup(Disposizioni.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, DocumentClosedEvent.class);
		form.setMainComponent(this.getClass().getResourceAsStream("DispPassive.jfrm"));
		form.setName("editor.form.disposizioni.passive");
		form.setCustomButtons(new String[] { "editor.form.disposizioni.passive.btn.cancel" });
		form.setHelpKey("help.contents.form.disposizionipassive");
		finevigoretesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.finevigoretesto");
		abrogazione = (JButton) form.getComponentByName("editor.disposizioni.passive.abrogazione");
		sostituzione = (JButton) form.getComponentByName("editor.disposizioni.passive.sostituzione");
		integrazione = (JButton) form.getComponentByName("editor.disposizioni.passive.integrazione");
		abrogazione.addActionListener(this);
		sostituzione.addActionListener(this);
		integrazione.addActionListener(this);
		evento = (JTextField) form.getComponentByName("editor.disposizioni.passive.evento");
		sceltaevento = (JButton) form.getComponentByName("editor.disposizioni.passive.sceltaevento");
		sceltaevento.addActionListener(this);
		dovetesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.dovetesto");
		dove = (JTextField) form.getComponentByName("editor.disposizioni.passive.dove");
		sceltadove = (JButton) form.getComponentByName("editor.disposizioni.passive.sceltadove");
		sceltadove.addActionListener(this);
	}
	
	public VigenzaEntity makeVigenza(Node node, String dsp, String status) {
		
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
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sceltaevento) {
			ciclodivita.setActiveNode(activeNode);
			Evento[] eventiOnDom = ciclodivita.getEventi();
			ciclodivitaForm.setEventi(eventiOnDom);
			
			if (ciclodivitaForm.openForm()) {
				eventoselezionato = ciclodivitaForm.getEventoSelezionato();
				if (eventoselezionato != -1) {	
					eventoriginale = ciclodivitaForm.getEventi()[0];	//eventiOnDom[0];
					eventovigore=ciclodivitaForm.getEventi()[eventoselezionato];
					if (eventovigore.getFonte().getTagTipoRelazione().equalsIgnoreCase("passiva"))
						evento.setText(eventovigore.getFonte().getLink());	
					else {
						evento.setText(eventovigore.getFonte().getLink());
						utilmsg.msgInfo("Dovresti selezionare un evento passivo");
						evento.setText("");
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
			if (partizioniForm.getPartizioneEstesa().length() > 0)
				dove.setText(makeSub(partizioniForm.getPartizioneEstesa()));
		}
		if (e.getSource() == abrogazione || e.getSource() == sostituzione
				|| e.getSource() == integrazione) {
			// verifiche per proseguire con la seconda maschera
			if (evento.getText().equals(""))
				utilmsg.msgInfo("editor.form.disposizioni.passive.noevento");
			else {
				if (e.getSource() == abrogazione) {
					operazioneIniziale = ABROGAZIONE;
					operazioneCorrente = NOVELLANDO;
					operazioneProssima = NOVELLANDO;
					form.close();
					novellandoForm.openForm(this);
				}
				if (e.getSource() == sostituzione) {
					operazioneIniziale = SOSTITUZIONE;
					operazioneCorrente = NOVELLANDO;
					operazioneProssima = NOVELLANDO;
					form.close();
					novellandoForm.openForm(this);
				}
				if (e.getSource() == integrazione) {
					operazioneIniziale = INTEGRAZIONE;
					operazioneCorrente = NOVELLA;
					operazioneProssima = NOVELLA;
					form.close();
					novellaForm.openForm(this);
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
			dove.setText("");
		}	
		posDisposizione="";
		activeNode = null;
		operazioneIniziale = NO_OPERAZIONE;
		operazioneProssima = NO_OPERAZIONE;
		form.setSize(400, 250);
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
	
	public void setNovellando(String id, String iniziovigore, String finevigore, String status, boolean nuovo) {
		idNovellando = (id!=null) ? id : "??";
		iniziovigoreNovellando = iniziovigore;
		finevigoreNovellando = finevigore;
		statusNovellando = status;
		nuovoNovellando = nuovo;
	}
	
	public void setNovella(String id) {
		idNovella = (id!=null) ? id : "??";
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	public void setPrenota(String nota) {
		preNota = nota;
	}
	
	public void setPostnota(String nota) {
		postNota = nota;
	}
	
	public void setImplicita(boolean implicita) {
		this.implicita = implicita;
	}
	
	public void setOperazioneProssima() {
	  switch (operazioneCorrente) {
		case NOVELLANDO: {
			operazioneProssima = NOTA;
			break;
		}
		case NOVELLA: {
			operazioneProssima = NOTA;
			break;
		}
		case NOTA: {
			operazioneProssima = FINE;
			break;
		}
	  }
	}

	private void undo(int operazione) {
		if (operazione==NOVELLANDO) {
			logger.debug("Undo Novellando: (" + idNovellando + " " + iniziovigoreNovellando + " " + finevigoreNovellando + " " + statusNovellando);
			if (nuovoNovellando)
				domDisposizioni.doUndo(idNovellando, false);
			else	
				domDisposizioni.doUndo(idNovellando, iniziovigoreNovellando, finevigoreNovellando, statusNovellando);
			if (operazioneIniziale==SOSTITUZIONE) {//se era una sostituzione undo anche di novella
				logger.debug("Undo Novella: (" + idNovella + ")");
				domDisposizioni.doUndo(idNovella, true);
			} 
		}	
		if (operazione==NOVELLA) {
			logger.debug("Undo Novella: (" + idNovella + ")");
			domDisposizioni.doUndo(idNovella, true);
		}	
	}
	
	public void formClosed() {

		if (operazioneCorrente==operazioneProssima) {	//Ho scelto Indietro o chiuso con X
			if (operazioneProssima == NOVELLA) {
				operazioneProssima = INIZIO;				
			} else if (operazioneProssima == NOTA) {
				if (operazioneIniziale == INTEGRAZIONE)
					operazioneProssima = NOVELLA;
				else
					operazioneProssima = NOVELLANDO;
			} else if (operazioneProssima == NOVELLANDO) {
				operazioneProssima = INIZIO;
			}
			undo(operazioneProssima);
		}
		
		if (operazioneProssima==NOVELLANDO) {
			operazioneCorrente = NOVELLANDO;
			novellandoForm.openForm(this);
		}	
		if (operazioneProssima==NOVELLA) {
			operazioneCorrente = NOVELLA;
			novellaForm.openForm(this);	
		}	
		if (operazioneProssima==NOTA) {
			operazioneCorrente = NOTA;
			String urn = eventovigore.getFonte().getLink();
			try {
				urn = nirUtilUrn.getFormaTestuale(new Urn(urn));
			} catch (Exception e) {}
			if (operazioneIniziale == INTEGRAZIONE)
				autoNota = "Integrato da: " + urn + ".\nIn vigore dal " + UtilDate.normToString(eventovigore.getData());
			else
				autoNota = status + " da: " + urn + ".\nIn vigore dal " + UtilDate.normToString(eventoriginale.getData()) + " al " + UtilDate.normToString(eventovigore.getData());
			notaForm.openForm(this, autoNota);	
		}	
		
		if (operazioneProssima == FINE) {
			String partizione = evento.getText();
			if (!dove.getText().equals("")) 
				partizione = partizione + "#" + dove.getText();	//TODO testare se ho già una urn con partizioni specificate e decidere come comportarsi
			
			if (!domDisposizioni.setDOMDisposizioni(posDisposizione, evento.getText(), partizione, idNovellando, idNovella, preNota, autoNota, postNota, implicita))
				utilmsg.msgError("editor.form.disposizioni.passive.erroremetadati");
			
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

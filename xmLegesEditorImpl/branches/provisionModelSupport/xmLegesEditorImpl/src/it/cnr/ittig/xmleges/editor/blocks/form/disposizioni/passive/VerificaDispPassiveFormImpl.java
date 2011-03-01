package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.passive;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
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
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.VerificaDispPassiveForm;
import it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita.CiclodiVitaForm;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.partizioni.PartizioniForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
public class VerificaDispPassiveFormImpl implements VerificaDispPassiveForm, Loggable, ActionListener, Serviceable, Initializable {

	Logger logger;
	NirUtilDom nirUtilDom;
	NirUtilUrn nirUtilUrn;
	UtilMsg utilmsg;
	MetaCiclodivita ciclodivita;
	CiclodiVitaForm ciclodivitaForm;
	int eventoselezionato = -1;
	Form form;
	JButton modifica;
	JButton elimina;
	JButton sceltaevento;
	JTextField evento;
	JLabel dovetesto;
	JButton sceltadove;
	JTextField dove;
	JLabel modificaelimina;
	JLabel statustesto;
	JComboBox vigenzaStatus;
	JLabel implicitatesto;
	JCheckBox implicita; 
	PartizioniForm partizioniForm;
	Node activeNode;
	VigenzaEntity vigenzaEntity;
	Vigenza vigenza;
	String errorMessage = "";
	
	DocumentManager documentManager;
	
	String partizione="";
	String posDisposizione = "";
	
	Disposizioni domDisposizioni;
	Evento eventoriginale;
	Evento eventovigore;
	
	String tipoDisposizione;
	Document  doc;
	Node disposizione;
	Node norma;
	Node ittigNota;
	Node pos;
	Node novellando;
	String id;
	String idNovella;
	String idNovellando;
	String dispCorrente;
	String partCorrente;
	String autoCorrente;
	String statusCorrente;
	String urnTestoCorrente;
	String partTestoCorrente;
	boolean implicitaCorrente;
	
	JTextField data;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		ciclodivita = (MetaCiclodivita) serviceManager.lookup(MetaCiclodivita.class);
		partizioniForm = (PartizioniForm) serviceManager.lookup(PartizioniForm.class);
		ciclodivitaForm = (CiclodiVitaForm) serviceManager.lookup(CiclodiVitaForm.class);
		domDisposizioni  = (Disposizioni) serviceManager.lookup(Disposizioni.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("ModificaAnnulla.jfrm"));
		form.setName("editor.form.disposizioni.modificapassive");
		form.setCustomButtons(new String[] { "editor.form.disposizioni.passive.btn.close" });
		form.setHelpKey("help.contents.form.modificadisposizionipassive");
		modifica = (JButton) form.getComponentByName("editor.disposizioni.passive.modifica");
		elimina = (JButton) form.getComponentByName("editor.disposizioni.passive.elimina");
		modifica.addActionListener(this);
		elimina.addActionListener(this);
		modificaelimina = (JLabel) form.getComponentByName("editor.disposizioni.passive.modificaelimina");
		evento = (JTextField) form.getComponentByName("editor.disposizioni.passive.evento");
		sceltaevento = (JButton) form.getComponentByName("editor.disposizioni.passive.sceltaevento");
		sceltaevento.addActionListener(this);
		dovetesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.dovetesto");
		dove = (JTextField) form.getComponentByName("editor.disposizioni.passive.dove");
		sceltadove = (JButton) form.getComponentByName("editor.disposizioni.passive.sceltadove");
		sceltadove.addActionListener(this);
		statustesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.statustesto");
		vigenzaStatus = (JComboBox) form.getComponentByName("editor.disposizioni.passive.status");
		vigenzaStatus.addItem("abrogato");
		vigenzaStatus.addItem("omissis");
		vigenzaStatus.addItem("annullato");
		vigenzaStatus.addItem("sospeso");
		implicitatesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.implicitatesto");
		implicita = (JCheckBox)  form.getComponentByName("editor.disposizioni.passive.implicita");
		data = (JTextField) form.getComponentByName("editor.disposizioni.passive.data");
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sceltaevento) {
			ciclodivita.setActiveNode(activeNode);
			Evento[] eventiOnDom = ciclodivita.getEventi();
			ciclodivitaForm.setEventi(eventiOnDom);
			
			if (ciclodivitaForm.openForm()) {
				eventoselezionato = ciclodivitaForm.getEventoSelezionato();
				if (eventoselezionato != -1) {	
					eventoriginale = eventiOnDom[0];
					eventovigore=ciclodivitaForm.getEventi()[eventoselezionato];
					if (eventovigore.getFonte().getTagTipoRelazione().equalsIgnoreCase("passiva")) {
						evento.setText(eventovigore.getFonte().getLink());
						data.setText(UtilDate.normToString(eventovigore.getData()));
					}	
					else {
						evento.setText(eventovigore.getFonte().getLink());
						utilmsg.msgInfo("Dovresti selezionare un evento passivo");
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
//	   		    if (ciclodivitaForm.getVigToUpdate()!=null && ciclodivitaForm.getVigToUpdate().length>0) {
//	   		    	VigenzaEntity[] elenco =ciclodivitaForm.getVigToUpdate();
//	   		    	for(int i=0; i<elenco.length;i++)
//	   		    		vigenza.updateVigenzaOnDoc(elenco[i]);
//	   		    }
			}
		}
	}

	public void openForm(Node activeNode) {
		
		this.activeNode = activeNode;
		doc =documentManager.getDocumentAsDom();
		Node[] modifichePassive = UtilDom.getElementsByTagName(doc, nirUtilDom.findActiveMeta(doc,activeNode), "modifichepassive");
		if (modifichePassive.length==0) {
			//nessun metadato di modifica passiva (non ho il tag Modifiche Passive)
		}
		else {
			boolean tuttOk = true;
			NodeList modifiche = modifichePassive[0].getChildNodes();
			for (int i=0; i<modifiche.getLength(); i++) {
				//cerco nel testo se la modifica è presente
				Node disposizione = modifiche.item(i);	//dsp:sostituzione, dsp:abrogazione, dsp:integrazione
				String tipoDisposizione = disposizione.getNodeName();
				String idPos="";
				String urnNorma="";
				String idNovella="";
				String idNovellando="";
				NodeList metaDisposizione = disposizione.getChildNodes();
				for (int j=0; j<metaDisposizione.getLength(); j++) {
					if (metaDisposizione.item(j).getNodeName().equals("dsp:pos"))
						idPos = UtilDom.getAttributeValueAsString(metaDisposizione.item(j),"xlink:href");
					if (metaDisposizione.item(j).getNodeName().equals("dsp:norma"))
						urnNorma = UtilDom.getAttributeValueAsString(metaDisposizione.item(j),"xlink:href");
					if (metaDisposizione.item(j).getNodeName().equals("dsp:novella"))
						try {
							idNovella = UtilDom.getAttributeValueAsString(metaDisposizione.item(j).getFirstChild(),"xlink:href");
						}
						catch (Exception e) {};
					if (metaDisposizione.item(j).getNodeName().equals("dsp:novellando"))
						try {
							idNovellando = UtilDom.getAttributeValueAsString(metaDisposizione.item(j).getFirstChild(),"xlink:href");
						}
						catch (Exception e) {};
				}
				logger.debug("DISPO: "+ tipoDisposizione + " id:" + idPos + " norma:" + urnNorma + " novella:" + idNovella + " novellando:" + idNovellando);
				Node modificaTesto = doc.getElementById(idPos);
				if (modificaTesto==null) {
					//non riesco a trovare la modifica nel testo dichiarata dal metadato
					System.err.println("non riesco a trovare la modifica nel testo dichiarata dal metadato");
				}
				else {
					Node novella = null;
					Node novellando = null;
					if (!idNovella.equals("") && !idNovella.equals(idPos)) {
						novella = doc.getElementById(idNovella);
						if (novella==null)
							//non riesco a trovare la novella
							System.err.println("non riesco a trovare la novella");
					}	
					if (!idNovellando.equals("") && !idNovellando.equals(idPos)) {
						novellando = doc.getElementById(idNovellando);
						if (novellando==null)
							//non riesco a trovare il novellando
							System.err.println("non riesco a trovare il novellando");
					}
					//Eseguo test su novella/novellando
					String finevigore="";
					String iniziovigore="";	
					if 	(tipoDisposizione.equals("dsp:sostituzione"))
						if (novella!=null && novellando!=null) 
							//errore devo avere una novella e un novellando
							System.err.println("errore devo avere una novella e un novellando");
						else {
							finevigore = UtilDom.getAttributeValueAsString(novellando,"finevigore");
							iniziovigore = UtilDom.getAttributeValueAsString(novellando,"finevigore");	
						}	
					if 	(tipoDisposizione.equals("dsp:integrazione"))
						if (novella!=null && novellando==null) 
							//errore devo avere una novella e no novellando
							System.err.println("errore devo avere una novella e no novellando");
						else
							iniziovigore = UtilDom.getAttributeValueAsString(novella,"iniziovigore");
					if 	(tipoDisposizione.equals("dsp:abrogazione"))
						if (novella!=null && novellando==null) 
							//errore devo avere un novellando e no novella
							System.err.println("errore devo avere un novellando e no novella");
						else {
							finevigore = UtilDom.getAttributeValueAsString(novellando,"finevigore");
							iniziovigore = UtilDom.getAttributeValueAsString(novellando,"finevigore");	
						}					
					//controllo se ho una relazione passiva che corrisponde
					
							
				}
			}
			if (tuttOk) {
				if (modifiche.getLength()!=0) {
					//nessun metadato di modifica passiva (ho il tag vuoto)
					System.err.println("nessun metadato di modifica passiva (ho il tag vuoto)");
				}
				else {
					//tutte le modifiche dichiarate nei metadati sono presenti nel testo
					System.err.println("tutte le modifiche dichiarate nei metadati sono presenti nel testo");
				}
			}	
		}	
		
	}	
}

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
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.ModificaDispPassiveForm;
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
public class ModificaDispPassiveFormImpl implements ModificaDispPassiveForm, Loggable, ActionListener, Serviceable, Initializable {

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
	String vigenzaTestoCorrente;
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
		if (e.getSource() == elimina) {
			domDisposizioni.doErase(idNovellando,idNovella,disposizione,novellando);
			form.close();
		}	
		if (e.getSource() == modifica)
			if (dispCorrente.equals(evento.getText()) && 
				partCorrente.equals(dove.getText()) &&
				implicita.isSelected()==implicitaCorrente &&
				(!vigenzaStatus.isEnabled() || statusCorrente.equals(vigenzaStatus.getSelectedItem())))
					 utilmsg.msgError("Non hai effettuato nessuna modifica");
			else {
				//ricalcolo nota automatica
				String autoNota;
				String urn = urnTestoCorrente;
				String vigore = vigenzaTestoCorrente;
				if (!dispCorrente.equals(evento.getText())) {
					if (tipoDisposizione.equalsIgnoreCase("INTEGRAZIONE"))
						vigore = "In vigore dal " + UtilDate.normToString(eventovigore.getData());
					else
						vigore = "In vigore dal " + UtilDate.normToString(eventoriginale.getData()) + " al " + UtilDate.normToString(eventovigore.getData());
					urn =  evento.getText();
					try {
						urn = nirUtilUrn.getFormaTestuale(new Urn(urn));
					} catch (Exception ex) {}
				}
				if (!partCorrente.equals(dove.getText()))
					urn = urn + " " + partizione + ".\n";
				else	
					urn = urn + partTestoCorrente;
				if (tipoDisposizione.equalsIgnoreCase("INTEGRAZIONE"))
					//autoNota = "Integrato da: " + urn + vigore;
					autoNota = urn + vigore;
				else
					//autoNota = (String) vigenzaStatus.getSelectedItem() + " da: " + urn + vigore;
					autoNota = urn + vigore;
						
				domDisposizioni.doChange(evento.getText(),dove.getText(),disposizione,autoNota, implicita.isSelected(),novellando, (String) vigenzaStatus.getSelectedItem());
				form.close();
			}	
	}

	public void openForm(Node activeNode) {
		
		this.activeNode = activeNode;
		id = UtilDom.getAttributeValueAsString(activeNode,"id");
		
		doc =documentManager.getDocumentAsDom();
		
		if (UtilDom.getElementsByTagName(doc, UtilDom.findParentByName(activeNode,"NIR"), "modifichepassive").length>0) {
			disposizione = ricercaDisposizione(UtilDom.getElementsByTagName(doc, UtilDom.findParentByName(activeNode,"NIR"), "modifichepassive")[0].getFirstChild());
			if (disposizione!=null) {
				implicitaCorrente = UtilDom.getAttributeValueAsString(disposizione, "implicita").equalsIgnoreCase("si");
				norma = UtilDom.getElementsByTagName(doc, disposizione, "dsp:norma")[0];
				pos = UtilDom.getElementsByTagName(doc, norma, "dsp:pos")[0];
				ittigNota = UtilDom.getElementsByTagName(doc, norma, "ittig:notavigenza")[0];
				dispCorrente=UtilDom.getAttributeValueAsString(norma, "xlink:href");
				partCorrente=UtilDom.getAttributeValueAsString(pos, "xlink:href");
				if (partCorrente.endsWith(dispCorrente))
					partCorrente="";
				else
					partCorrente=partCorrente.substring(1+dispCorrente.length());		
				modificaelimina.setText("Hai selezionato una " + tipoDisposizione + ".");
				evento.setText(dispCorrente);
				
				//per il momento lo recupero dal testo... dopo recuperarlo dai metadati (dsp:tempi ....o simile)
				NodeList eventi = UtilDom.findRecursiveChild(doc, "eventi").getChildNodes();
				String evento;
				if (!"".equals(UtilDom.getAttributeValueAsString(activeNode, "finevigore")))
					evento = UtilDom.getAttributeValueAsString(activeNode, "finevigore");
				else
					evento = UtilDom.getAttributeValueAsString(activeNode, "iniziovigore");
				for (int i=0; i<eventi.getLength(); i++)
					if (UtilDom.getAttributeValueAsString(eventi.item(i),"id").equals(evento))
						data.setText(UtilDate.normToString(UtilDom.getAttributeValueAsString(eventi.item(i),"data")));
					
					
				dove.setText(partCorrente);
				implicita.setSelected(implicitaCorrente);				
				autoCorrente = UtilDom.getAttributeValueAsString(ittigNota, "auto");
				vigenzaStatus.setEnabled(true);
				statusCorrente = "";
				if (!idNovellando.equals("") && UtilDom.getAttributeValueAsString(activeNode,"status")==null)
					novellando = ricercaNovellando(activeNode.getParentNode(), idNovellando);
				else
					novellando = activeNode;
				if (!idNovellando.equals("") && UtilDom.getAttributeValueAsString(novellando,"status")!=null) {
					statusCorrente = UtilDom.getAttributeValueAsString(novellando,"status");
					vigenzaStatus.setSelectedItem(statusCorrente);
					
					//x ora vogliono bloccato sempre lo status
					vigenzaStatus.setEnabled(false);
				}	
				else
					vigenzaStatus.setEnabled(false);
				
				try {
					urnTestoCorrente = nirUtilUrn.getFormaTestuale(new Urn(dispCorrente));
				} catch (Exception ex) {
					urnTestoCorrente="";
				}
				vigenzaTestoCorrente = autoCorrente.substring(autoCorrente.indexOf("In vigore"), autoCorrente.length());
				partTestoCorrente = autoCorrente.substring(autoCorrente.indexOf(urnTestoCorrente)+urnTestoCorrente.length(),autoCorrente.indexOf(vigenzaTestoCorrente));

				form.setSize(400, 280);
				form.showDialog();
			}
			else utilmsg.msgInfo("editor.disposizioni.passive.nometadati");
		}	
		else utilmsg.msgInfo("editor.disposizioni.passive.nometadati");
	}
	
	private Node ricercaNovellando(Node padre, String idNovellando) {
		NodeList figli = padre.getChildNodes(); 
		for (int i=0; i< figli.getLength(); i++)
			if (idNovellando.equals(UtilDom.getAttributeValueAsString(figli.item(i), "id")))
				return figli.item(i);
		return padre;
	}
	
	private Node ricercaDisposizione(Node cerca) {
		
		try {
			if (cerca==null)
				return null;
			else {
				if (cerca.getNodeName().equals("dsp:abrogazione")) {
					if (UtilDom.getAttributeValueAsString(UtilDom.getElementsByTagName(doc,UtilDom.getElementsByTagName(doc, cerca, "dsp:novellando")[0], "dsp:pos")[0],"xlink:href").equals(id)) {
						tipoDisposizione = "abrogazione";
						idNovellando = id;
						idNovella = "";
						return cerca; 
					}	
				}
				else 
					if (cerca.getNodeName().equals("dsp:sostituzione")) {
						if (UtilDom.getAttributeValueAsString(UtilDom.getElementsByTagName(doc, UtilDom.getElementsByTagName(doc, cerca, "dsp:novellando")[0], "dsp:pos")[0],"xlink:href").equals(id)) {
							tipoDisposizione = "sostituzione";
							idNovellando = id;							
							idNovella = UtilDom.getAttributeValueAsString(UtilDom.getElementsByTagName(doc, UtilDom.getElementsByTagName(doc, cerca, "dsp:novella")[0], "dsp:pos")[0],"xlink:href");
							return cerca; 
						}
						if (UtilDom.getAttributeValueAsString(UtilDom.getElementsByTagName(doc, UtilDom.getElementsByTagName(doc, cerca, "dsp:novella")[0], "dsp:pos")[0],"xlink:href").equals(id)) {
							tipoDisposizione = "sostituzione";
							idNovellando = UtilDom.getAttributeValueAsString(UtilDom.getElementsByTagName(doc, UtilDom.getElementsByTagName(doc, cerca, "dsp:novellando")[0], "dsp:pos")[0],"xlink:href");							
							idNovella = id;
							return cerca; 
						}	
					}
					else 
						if (cerca.getNodeName().equals("dsp:integrazione")) {
							if (UtilDom.getAttributeValueAsString(UtilDom.getElementsByTagName(doc, UtilDom.getElementsByTagName(doc, cerca, "dsp:novella")[0], "dsp:pos")[0],"xlink:href").equals(id)) {
								tipoDisposizione = "integrazione";
								idNovellando = "";
								idNovella = id;
								return cerca;
							}	
						}
				return ricercaDisposizione(cerca.getNextSibling());
			}
		} catch (Exception e) {
			return null;
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
	
}

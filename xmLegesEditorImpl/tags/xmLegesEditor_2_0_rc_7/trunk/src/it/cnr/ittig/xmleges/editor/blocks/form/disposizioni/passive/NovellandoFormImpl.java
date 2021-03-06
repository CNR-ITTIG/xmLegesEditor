package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.passive;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.NovellandoForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.DispPassiveForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.NovellandoForm</code>.
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
public class NovellandoFormImpl implements NovellandoForm, EventManagerListener, Loggable, ActionListener, Serviceable, Initializable {
	Logger logger;

	String idSelezionato;
	int start;
	int end;
	
	Form form;
	DispPassiveForm disposizioni;
	
	// Verificare
	NirUtilDom nirUtilDom;
	NirUtilUrn nirUtilUrn;
	UtilUI utilui;
	UtilMsg utilmsg;

	
	EventManager eventManager;
	 
	JTextField idotesto;
	JLabel novellandotesto;
	JLabel testo;
	JTextArea spiegazione;
	JButton avanti;
	JButton indietro;
	JRadioButton sceltopartizione;
	JRadioButton sceltotesto;

	Disposizioni domDisposizioni;
	
	Node activeNode;
	
	DtdRulesManager dtdRulesManager;

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		utilui = (UtilUI) serviceManager.lookup(UtilUI.class);
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		disposizioni = (DispPassiveForm) serviceManager.lookup(DispPassiveForm.class);
		domDisposizioni  = (Disposizioni) serviceManager.lookup(Disposizioni.class);
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
	}

	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		form.setMainComponent(this.getClass().getResourceAsStream("Novellando.jfrm"));
		form.setCustomButtons(null);		
		
		form.setName("editor.form.disposizioni.passive");
		form.setHelpKey("help.contents.form.disposizionipassive.novellando");
		idotesto = (JTextField) form.getComponentByName("editor.disposizioni.passive.novellando");
		testo = (JLabel) form.getComponentByName("editor.disposizioni.passive.stepnovellando");
		spiegazione = (JTextArea) form.getComponentByName("editor.disposizioni.passive.spiegazionenovellando");
		novellandotesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.novellandotesto");
		avanti = (JButton) form.getComponentByName("editor.form.disposizioni.passive.btn.avanti");
		indietro = (JButton) form.getComponentByName("editor.form.disposizioni.passive.btn.indietro");
		sceltotesto = (JRadioButton) form.getComponentByName("editor.disposizioni.passive.testo"); 
		sceltopartizione = (JRadioButton) form.getComponentByName("editor.disposizioni.passive.partizione");
		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(sceltotesto);
		grupporadio.add(sceltopartizione);
		avanti.addActionListener(this);
		indietro.addActionListener(this);
		form.setSize(250, 220);
	}

	public void manageEvent(EventObject event) {
		if (form.isDialogVisible()) {
		if (event instanceof DocumentClosedEvent) 
				form.close();
		else {
			SelectionChangedEvent e = (SelectionChangedEvent) event;
			activeNode = ((SelectionChangedEvent) event).getActiveNode();
			if (activeNode!=null) {
				start = ((SelectionChangedEvent) event).getTextSelectionStart();
				end = ((SelectionChangedEvent) event).getTextSelectionEnd();
			}
			else {
				start=-1;
				end=-1;
			}	
			updateContent(e.getActiveNode(), start, end);	
		}
		}
	}

	private boolean canSelec(Node node) {
		
		//TODO 	teoricamnete credo sia giusto. Non fare selezionare testo che ha gi� una fine vigenza;
		//		magari ripristinrlo dopo averlo testato un p�.
//		if (domDisposizioni.getVigenza(node,-1,-1)==null)
//			return true;
//		else
//			if (domDisposizioni.getVigenza(activeNode,-1,-1).getEFineVigore()==null)
//				return true;
//			else
//				return false;
			
		return true;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == avanti) {
			if (idotesto.getText().equals(""))
				utilmsg.msgInfo("editor.disposizioni.passive.noselezione");
			else {	
				Node n;
				if (sceltopartizione.isSelected()) {	//partizione
					String iniziovigore = UtilDom.getAttributeValueAsString(activeNode, "iniziovigore");
					String finevigore = UtilDom.getAttributeValueAsString(activeNode, "finevigore");
					String statusvigore = UtilDom.getAttributeValueAsString(activeNode, "status");
					n = domDisposizioni.setVigenza(activeNode, activeNode.getNodeValue(), start, end, disposizioni.makeVigenza(activeNode,"novellando"));
					idSelezionato = UtilDom.getAttributeValueAsString(n, "id");
					disposizioni.setNovellando(idSelezionato, iniziovigore, finevigore, statusvigore, false);
				}
				else  {//creo un nuovo span
					n = domDisposizioni.setVigenza(activeNode, "", start, end, disposizioni.makeVigenza(activeNode,"novellando"));
					idSelezionato = UtilDom.getAttributeValueAsString(n, "id");
					disposizioni.setNovellando(idSelezionato, "", "", "", true);
				}	
				disposizioni.setOperazioneProssima();
				disposizioni.setPosdisposizione(n);
				form.close();
			}
		}
		if (e.getSource() == indietro) {
			form.close();
		}
	}

	private void updateContent(Node activeNode, int start, int end) {
		idotesto.setText("");
		if (activeNode != null) {
		  if (canSelec(activeNode)) {	
			 if (UtilDom.isTextNode(activeNode)) {	//test se ha fatto una selezione di testo
				 if (start!=end) {
				 	idSelezionato="";
				 	if (activeNode.getNodeValue()!=null) {
						logger.debug("selezione testo");
						if (end - start > 30)
							idotesto.setText(activeNode.getNodeValue().substring(start, start + 30) + " ...");
						else	
							idotesto.setText(activeNode.getNodeValue().substring(start, end));
						sceltotesto.setSelected(true);
					}
			    }	
			 }
			 else {
					try {
						if (activeNode.getNodeName()!=null && dtdRulesManager.queryIsValidAttribute(activeNode.getNodeName(), "iniziovigore")) {
							logger.debug("selezione non testo");
							idSelezionato = UtilDom.getAttributeValueAsString(activeNode, "id");
							if (idSelezionato==null)
								idotesto.setText(activeNode.getNodeName());
							else
								idotesto.setText(activeNode.getNodeName() + " (" + idSelezionato + " )");
							sceltopartizione.setSelected(true);
						}
					} catch (DtdRulesManagerException e) {}				 
			 }
			 
			 
		  }	
		  else utilmsg.msgInfo("editor.disposizioni.passive.selezionErrata"); 
		}
	}

	public void openForm(FormClosedListener listener) {

		idotesto.setText("");
		form.showDialog(listener);	
	}
}

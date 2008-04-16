package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.passive;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.NovellandoForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.DispPassiveForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

	DocumentManager documentManager;
	EventManager eventManager;
	 
	JTextField idotesto;
	JLabel novellandotesto;
	JLabel testo;
	JLabel spiegazione;
	JButton avanti;
	JButton indietro;
	JRadioButton sceltopartizione;
	JRadioButton sceltotesto;
	JTextArea testoaggiunto;
	JLabel testoaggiuntotesto;
	JLabel statustesto;
	JComboBox vigenzaStatus;

	
	Disposizioni domDisposizioni;
	
	Node activeNode;
	
	DtdRulesManager dtdRulesManager;
	
	SelectionManager selectionManager;

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
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
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
		spiegazione = (JLabel) form.getComponentByName("editor.disposizioni.passive.spiegazionenovellando");
		novellandotesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.novellandotesto");
		avanti = (JButton) form.getComponentByName("editor.form.disposizioni.passive.btn.avanti");
		indietro = (JButton) form.getComponentByName("editor.form.disposizioni.passive.btn.indietro");
		sceltotesto = (JRadioButton) form.getComponentByName("editor.disposizioni.passive.testo"); 
		sceltopartizione = (JRadioButton) form.getComponentByName("editor.disposizioni.passive.partizione");
		testoaggiunto = (JTextArea) form.getComponentByName("editor.disposizioni.passive.testoaggiunto");
		testoaggiuntotesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.testoaggiuntotesto");
		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(sceltotesto);
		grupporadio.add(sceltopartizione);
		statustesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.statustesto");
		vigenzaStatus = (JComboBox) form.getComponentByName("editor.disposizioni.passive.status");
		vigenzaStatus.addItem("abrogato");
		vigenzaStatus.addItem("omissis");
		vigenzaStatus.addItem("annullato");
		vigenzaStatus.addItem("sospeso");
		avanti.addActionListener(this);
		indietro.addActionListener(this);
		form.setSize(290, 350);
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
		
		//TODO 	teoricamnete credo sia giusto. Non fare selezionare testo che ha già una fine vigenza;
		//		magari ripristinrlo dopo averlo testato un pò.
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
			Node n;
			if (sceltopartizione.isSelected()) {	//partizione
				String iniziovigore = UtilDom.getAttributeValueAsString(activeNode, "iniziovigore");
				String finevigore = UtilDom.getAttributeValueAsString(activeNode, "finevigore");
				String statusvigore = UtilDom.getAttributeValueAsString(activeNode, "status");
				n = domDisposizioni.setVigenza(activeNode, activeNode.getNodeValue(), start, end, disposizioni.makeVigenza(activeNode,"novellando",(String) vigenzaStatus.getSelectedItem()));
				
				
				//questo inseriva una ndr come notaDIvigenza
				//domDisposizioni.makeNotaVigenza(n);
				
				
				
				idSelezionato = UtilDom.getAttributeValueAsString(n, "id");
				if (idSelezionato==null) {
					String prefisso = n.getLocalName();
					int occorrenze = documentManager.getDocumentAsDom().getElementsByTagName(prefisso).getLength();
					if (prefisso.length()>3)
						prefisso = prefisso.substring(0,3);
					UtilDom.setIdAttribute(n, prefisso + occorrenze);
					idSelezionato = UtilDom.getAttributeValueAsString(n, "id");
				}
				
				disposizioni.setNovellando(idSelezionato, iniziovigore, finevigore, statusvigore, false);
				disposizioni.setPosdisposizione(n);
				
				if (disposizioni.getTipoDisposizione()==disposizioni.SOSTITUZIONE) {
					n = domDisposizioni.makePartition(activeNode, false, disposizioni.makeVigenza(activeNode,"novella",(String) vigenzaStatus.getSelectedItem()));				
					selectionManager.setActiveNode(this, n);
					if (n!=null) {
						String id = UtilDom.getAttributeValueAsString(n, "id");
//						if (id==null)
//							id = getIDNotArticoloByPosition(n);
						disposizioni.setNovella(id);
					}	
				}
			}
			else  {//creo un nuovo span
				n = domDisposizioni.setVigenza(activeNode, "", start, end, disposizioni.makeVigenza(activeNode,"novellando",(String) vigenzaStatus.getSelectedItem()));
				
				
				//questo inseriva una ndr come notaDIvigenza
				//domDisposizioni.makeNotaVigenza(n);
				
				
				idSelezionato = UtilDom.getAttributeValueAsString(n, "id");
				disposizioni.setNovellando(idSelezionato, "", "", "", true);
				disposizioni.setPosdisposizione(n);
									
				if (disposizioni.getTipoDisposizione()==disposizioni.SOSTITUZIONE) {
					n = domDisposizioni.makeSpan(n, -1, disposizioni.makeVigenza(n,"novella",(String) vigenzaStatus.getSelectedItem()),testoaggiunto.getText());
					selectionManager.setActiveNode(this, n);	
					if (n!=null) {
						String id = UtilDom.getAttributeValueAsString(n, "id");
//						if (id==null)
//							id = getIDNotArticoloByPosition(n);
						disposizioni.setNovella(id);
					}	
				}					
			}	
			disposizioni.setOperazioneProssima();
			disposizioni.setStatus((String) vigenzaStatus.getSelectedItem());
			
			form.close();			
		}
		if (e.getSource() == indietro) {
			form.close();
		}
	}

	private void updateContent(Node activeNode, int start, int end) {
		idotesto.setText("");
		avanti.setEnabled(false);
		testoaggiuntotesto.setEnabled(false);
		testoaggiunto.setEnabled(false);
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
						if (disposizioni.getTipoDisposizione()==disposizioni.SOSTITUZIONE)
							testoaggiuntotesto.setEnabled(true);
						testoaggiunto.setEnabled(true);
						avanti.setEnabled(true);
					}
			    }	
			 }
			 else {
					try {
						if (activeNode.getNodeName()!=null && dtdRulesManager.queryIsValidAttribute(activeNode.getNodeName(), "iniziovigore")) 
							if (disposizioni.getTipoDisposizione()==disposizioni.ABROGAZIONE || dtdRulesManager.queryCanAppend(activeNode.getParentNode(), activeNode.getOwnerDocument().createElement(activeNode.getNodeName()))) {
								logger.debug("selezione non testo");
								idSelezionato = UtilDom.getAttributeValueAsString(activeNode, "id");
								if (idSelezionato==null)
									idotesto.setText(activeNode.getNodeName());
								else
									idotesto.setText(activeNode.getNodeName() + " (" + idSelezionato + " )");
								sceltopartizione.setSelected(true);
								avanti.setEnabled(true);
							}
					} catch (DtdRulesManagerException e) {}				 
			 }
			 
			 
		  }	
		  else utilmsg.msgInfo("editor.disposizioni.passive.selezionErrata"); 
		}
	}

	public void openForm(FormClosedListener listener) {

		
		//x ora vogliono bloccato sempre lo status
		vigenzaStatus.setEnabled(false);
		
		idotesto.setText("");
		avanti.setEnabled(false);					
		testoaggiuntotesto.setEnabled(false);
		testoaggiunto.setText("");
		testoaggiunto.setEnabled(false);
		if (disposizioni.getTipoDisposizione()==disposizioni.ABROGAZIONE)  
			testoaggiunto.setVisible(false);
		else 
			testoaggiunto.setVisible(true);
		form.showDialog(listener);	
	}
}

package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.passive;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.NovellaForm;
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
public class NovellaFormImpl implements NovellaForm, EventManagerListener, Loggable, ActionListener, Serviceable, Initializable {
	Logger logger;
	DtdRulesManager dtdRulesManager;
	
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
	
	SelectionManager selectionManager;
	
	UtilRulesManager utilRulesManager;

	JLabel novellatesto;
	JLabel testo;
	JLabel novellanuovo;
	JLabel nuovo;
	JLabel spiegazione;
	JRadioButton prima;
	JRadioButton dopo;
	String genericaPartizione=null;
	JButton avanti;
	JButton indietro;
	JTextArea testoaggiunto;
	JLabel testoaggiuntotesto;

	Node activeNode;
	
	Disposizioni domDisposizioni;

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
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		domDisposizioni  = (Disposizioni) serviceManager.lookup(Disposizioni.class);
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}

	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		form.setMainComponent(this.getClass().getResourceAsStream("Novella.jfrm"));
		form.setName("editor.form.disposizioni.passive");
		form.setCustomButtons(null);
		form.setHelpKey("help.contents.form.disposizionipassive.novellando");
		testo = (JLabel) form.getComponentByName("editor.disposizioni.passive.stepnovella");
		spiegazione = (JLabel) form.getComponentByName("editor.disposizioni.passive.spiegazionenovella");
		novellatesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.novellatesto");
		novellanuovo = (JLabel) form.getComponentByName("editor.disposizioni.passive.novellanuovo");
		nuovo = (JLabel) form.getComponentByName("editor.disposizioni.passive.nuovo");
		testoaggiunto = (JTextArea) form.getComponentByName("editor.disposizioni.passive.testoaggiunto");
		testoaggiuntotesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.testoaggiuntotesto");
		dopo = (JRadioButton) form.getComponentByName("editor.disposizioni.passive.novelladopo");
		prima = (JRadioButton) form.getComponentByName("editor.disposizioni.passive.novellaprima");
		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(prima);
		grupporadio.add(dopo);
		avanti = (JButton) form.getComponentByName("editor.form.disposizioni.passive.btn.avanti");
		indietro = (JButton) form.getComponentByName("editor.form.disposizioni.passive.btn.indietro");
		avanti.addActionListener(this);
		indietro.addActionListener(this);
		dopo.setSelected(true);
		form.setSize(270, 300);
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

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == avanti) {

			EditTransaction t = null;
			try {
				t = documentManager.beginEdit();
				
				Node n;
				if (UtilDom.isTextNode(activeNode)) {  	//	creo un nuovo span
					int posizione;
					if (prima.isSelected())
						posizione = start;
					else
						posizione = end;
					n = domDisposizioni.makeSpan(activeNode, posizione, disposizioni.makeVigenza(activeNode,"novella",""),testoaggiunto.getText());	
					selectionManager.setActiveNode(this, n);
				}
				else {		//	creo una partizione nuova
					n = domDisposizioni.makePartition(activeNode, prima.isSelected(), disposizioni.makeVigenza(activeNode,"novella",""));				
					selectionManager.setActiveNode(this, n);
				} 					
				
				if (n!=null) {
					String id = UtilDom.getAttributeValueAsString(n, "id");
//					if (id==null)
//						id = getIDNotArticoloByPosition(n);
					disposizioni.setNovella(id);
				}	
				disposizioni.setPosdisposizione(n);
				
				disposizioni.setMeta();
				documentManager.commitEdit(t);	
				form.close();
			
			
			} catch (Exception ex) {
				documentManager.rollbackEdit(t);
			}
		}
		if (e.getSource() == indietro) {
			form.close();
		}
	}

	private void updateContent(Node activeNode, int start, int end) {
		nuovo.setText("");
		avanti.setEnabled(false);
		testoaggiuntotesto.setEnabled(false);
		testoaggiunto.setEnabled(false);
		if (activeNode != null) {
			 if (UtilDom.isTextNode(activeNode)) { 	//test se ha fatto una selezione di testo
					if (activeNode.getNodeValue()!=null) {
						logger.debug("selezione testo");
						nuovo.setText("testo");
						avanti.setEnabled(true);
						testoaggiuntotesto.setEnabled(true);
						testoaggiunto.setEnabled(true);
					}
			 }		
			 else {
					try {
						if (activeNode.getNodeName()!=null && dtdRulesManager.queryIsValidAttribute(activeNode.getNodeName(), "iniziovigore") && !activeNode.getNodeName().equals("h:span")) {
						 	logger.debug("selezione non testo");
							if (dtdRulesManager.queryAppendable(activeNode.getParentNode()).contains(activeNode.getNodeName())) {
								if (activeNode==nirUtilDom.getNirContainer(activeNode)) {
								
								
									
									String tempId = UtilDom.getAttributeValueAsString(activeNode, "id");
									String temp = nirUtilUrn.getFormaTestualeById(tempId, tempId);
									if (temp.lastIndexOf(" ")!=-1)
										nuovo.setText(temp.substring(0, temp.lastIndexOf(" ")));
									else
										nuovo.setText(temp); 
									avanti.setEnabled(true);
								}	
								else {	
									nuovo.setText(activeNode.getNodeName());
									avanti.setEnabled(true);
								}									
							}	
						}
					} catch (DtdRulesManagerException e) {}		
			}				
		}
	}
	
	public void openForm(FormClosedListener listener) {

		form.showDialog(listener);
		nuovo.setText("");
		testoaggiuntotesto.setEnabled(false);
		testoaggiunto.setText("");
		testoaggiunto.setEnabled(false);
		avanti.setEnabled(false);
		activeNode = selectionManager.getActiveNode();
		if (activeNode!=null) {
			start = selectionManager.getTextSelectionStart();
			end = selectionManager.getTextSelectionEnd();
		}
		else {
			start=-1;
			end=-1;
		}
		updateContent(activeNode, start, end);
	}
}

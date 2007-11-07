package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.attive;

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
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.NovellandoNovellaForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DispAttiveForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.NovellandoNovellaForm</code>.
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
public class NovellandoNovellaFormImpl implements NovellandoNovellaForm, EventManagerListener, Loggable, ActionListener, Serviceable, Initializable {
	Logger logger;

	String idVir1;
	String idVir2;
	
	Form form;
	DispAttiveForm disposizioni;
	
	// Verificare
	NirUtilDom nirUtilDom;
	NirUtilUrn nirUtilUrn;
	UtilUI utilui;
	UtilMsg utilmsg;

	
	EventManager eventManager;
	 
	JLabel novellandotesto;
	JLabel novellatesto;
	JTextField novellando;
	JTextField novella;
	JLabel spiegazione;
	JButton avanti;
	JButton indietro;
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
		disposizioni = (DispAttiveForm) serviceManager.lookup(DispAttiveForm.class);
		domDisposizioni  = (Disposizioni) serviceManager.lookup(Disposizioni.class);
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
	}

	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		form.setMainComponent(this.getClass().getResourceAsStream("NovellandoNovella.jfrm"));
		form.setCustomButtons(null);		
		
		form.setName("editor.form.disposizioni.attive");
		form.setHelpKey("help.contents.form.disposizioniattive.novellandonovella");
		
		spiegazione = (JLabel) form.getComponentByName("editor.disposizioni.attive.nnspiegazione");
		novellandotesto = (JLabel) form.getComponentByName("editor.disposizioni.attive.novellandotesto");
		novellatesto = (JLabel) form.getComponentByName("editor.disposizioni.attive.novellatesto");
		novellando = (JTextField) form.getComponentByName("editor.disposizioni.attive.novellando");
		novella = (JTextField) form.getComponentByName("editor.disposizioni.attive.novella");
		avanti = (JButton) form.getComponentByName("editor.form.disposizioni.attive.btn.avanti");
		indietro = (JButton) form.getComponentByName("editor.form.disposizioni.attive.btn.indietro");
		statustesto = (JLabel) form.getComponentByName("editor.disposizioni.attive.statustesto");
		vigenzaStatus = (JComboBox) form.getComponentByName("editor.disposizioni.attive.status");
		vigenzaStatus.addItem("abrogato");
		vigenzaStatus.addItem("omissis");
		vigenzaStatus.addItem("annullato");
		vigenzaStatus.addItem("sospeso");
		avanti.addActionListener(this);
		indietro.addActionListener(this);
		form.setSize(290, 290);
	}

	public void manageEvent(EventObject event) {
		if (form.isDialogVisible()) {
			if (event instanceof DocumentClosedEvent) 
				form.close();
			else {
				SelectionChangedEvent e = (SelectionChangedEvent) event;
				activeNode = ((SelectionChangedEvent) event).getActiveNode();
				updateContent(e.getActiveNode());	
			}
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == avanti) {
			disposizioni.setNovellando(idVir1);
			disposizioni.setNovella(idVir2);
			disposizioni.setOperazioneProssima();			
			form.close();			
		}
		if (e.getSource() == indietro) {
			form.close();
		}
	}

	private void updateContent(Node activeNode) {
		avanti.setEnabled(false);
		if (activeNode != null) {	
			 if (UtilDom.isTextNode(activeNode)) {	//test se ha fatto una selezione di testo
			 }
			 else
				if (activeNode.getNodeName()=="mod") {
					int conta = 0;
					String testo1 = null;
					String testo2 = null;
					String id1 = null;
					String id2 = null;
					Vector children = UtilDom.getAllChildElements(activeNode);
					for (int i = 0; i < children.size(); i++)
						if (((Node) children.get(i)).getNodeName()=="virgolette") {
							conta++;
							if (conta==1) {
								testo1 = ((Node) children.get(i)).getFirstChild().getNodeValue();
								id1 = UtilDom.getAttributeValueAsString(((Node) children.get(i)), "id");
							}
							if (conta==2) {
								testo2 = ((Node) children.get(i)).getFirstChild().getNodeValue();
								id2 = UtilDom.getAttributeValueAsString(((Node) children.get(i)), "id");
							}
						}
					if (disposizioni.getTipoDisposizione()==disposizioni.SOSTITUZIONE && conta==2) {
						logger.debug("selezione virgolette " + id1 + " e " + id2 + " della modifica");
						idVir1 = id1;
						idVir2 = id2;
						if (testo1.length() > 30)
							novellando.setText(testo1.substring(0, 30) + " ...");
						else
							novellando.setText(testo1);						
						if (testo2.length() > 30)
							novella.setText(testo2.substring(0, 30) + " ...");
						else
							novella.setText(testo2);						
						avanti.setEnabled(true);
					}
					else
						if (conta==1) {
							logger.debug("selezione virgolette " + id1 + " della modifica");
							if (disposizioni.getTipoDisposizione()==disposizioni.ABROGAZIONE) {
								idVir1 = id1;
								idVir2 = null;
								if (testo1.length() > 30)
									novellando.setText(testo1.substring(0, 30) + " ...");
								else	
									novellando.setText(testo1);						
							}	
							if (disposizioni.getTipoDisposizione()==disposizioni.INTEGRAZIONE) {
								idVir1 = null;
								idVir2 = id1;
								if (testo1.length() > 30)
									novella.setText(testo1.substring(0, 30) + " ...");
								else	
									novella.setText(testo1);						
							}
							avanti.setEnabled(true);
						}
				}				 
		}
	}

	public void openForm(FormClosedListener listener) {
		
		//x ora vogliono bloccato sempre lo status
		vigenzaStatus.setEnabled(false);
		
		if (disposizioni.getTipoDisposizione()==disposizioni.INTEGRAZIONE) {
			statustesto.setVisible(false);
			vigenzaStatus.setVisible(false);
		}
		else {
			statustesto.setVisible(true);
			vigenzaStatus.setVisible(true);
		}
		
		idVir1=null;
		idVir2=null;
		novella.setText("");
		novellando.setText("");
		avanti.setEnabled(false);					
		form.showDialog(listener);	
	}
}

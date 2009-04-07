package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.multivigente;

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
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.disposizioni.Disposizioni;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.multivigente.PosizionamentoManualeForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.NovellandoForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.DispPassiveForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio per la visualizzazione della form per la creazione del testo multivigente
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
public class PosizionamentoManualeFormImpl implements PosizionamentoManualeForm, EventManagerListener, Loggable, ActionListener, Serviceable, Initializable {
	Logger logger;

	String idSelezionato;
	int start;
	int end;
	int startReturned;
	int endReturned;
	Node daInserire;
	Node daEliminare;
	
	Form form;
	JComboBox bordoOrdTipo;
	JButton cambia;
	JButton annulla;
	
	DocumentManager documentManager;
	EventManager eventManager;
	 
	JTextField idotesto;
	JLabel spiegazione;
	JRadioButton sceltopartizione;
	JRadioButton sceltotesto;

	Node activeNode;
	SelectionManager selectionManager;
	DtdRulesManager dtdRulesManager;
	
	String nomeDomNodo;

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
	}

	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		form.setMainComponent(this.getClass().getResourceAsStream("PosizionamentoManuale.jfrm"));
		form.setCustomButtons(null);		
		
		form.setName("editor.form.disposizioni.multivigente.posizionamento");
		cambia = (JButton) form.getComponentByName("editor.disposizioni.multivigente.cambiapartizione");
		annulla = (JButton) form.getComponentByName("editor.disposizioni.multivigente.annullapartizione");
		idotesto = (JTextField) form.getComponentByName("editor.disposizioni.multivigente.selezione");
		sceltotesto = (JRadioButton) form.getComponentByName("editor.disposizioni.multivigente.testo"); 
		sceltopartizione = (JRadioButton) form.getComponentByName("editor.disposizioni.multivigente.partizione");
		bordoOrdTipo = (JComboBox) form.getComponentByName("editor.disposizioni.multivigente.selez.ordTipo");
		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(sceltotesto);
		grupporadio.add(sceltopartizione);
		cambia.addActionListener(this);
		annulla.addActionListener(this);
		form.setSize(300, 220);
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
		
		//Non deve essere selezionabile ciò che già ha un finefigore!?!?! (e se sto puntando per una integrazione)????
		//Non deve essere puntabile un qualcosa che non può avere un fratello del tipo di quello che voglio inserire
		if (UtilDom.isTextNode(node))
			return "".equals(nomeDomNodo);
			
		if (!nomeDomNodo.equals(activeNode.getNodeName()))
			return false;
		
		NamedNodeMap attList = node.getAttributes();
		if (attList == null)
			return true;
		return (attList.getNamedItem("finevigore")==null);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cambia) {
			startReturned = start;
			endReturned = end;
			form.close();
		}
		if (e.getSource() == annulla) {
			idotesto.setText("");
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
		}
	}

	public void openForm(FormClosedListener listener, Node inserire, String nomeDomNodo) {
		
		this.nomeDomNodo = nomeDomNodo;
		if (inserire!=null) {
			Vector temp = UtilDom.getChildElements(inserire);
			if (temp.size()>0) {
				daInserire = (Node) temp.get(0);
				this.nomeDomNodo = daInserire.getNodeName();
			}
			else {	//sarà un nodo di testo (modifica di parole)
				daInserire =  inserire.getFirstChild();
				this.nomeDomNodo = "";
			}
		}
		else
			daInserire = null;
		
		idSelezionato="";
		idotesto.setText("");
		form.showDialog(listener);	
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
	
	public void azzeraBordi() {
		bordoOrdTipo.removeAll();
		bordoOrdTipo.setEnabled(false);
	}
	
	public void aggiungiBordo(String ord, String tipo) {
		bordoOrdTipo.addItem(ord + " " + tipo);
		bordoOrdTipo.setEnabled(true);
	}
	
	public boolean isChange() {
		return !"".equals(idotesto.getText());
	}

	public int getFineSelezione() {
		return endReturned;
	}

	public int getInizioSelezione() {
		return startReturned;
	}

	public Node getNodoSelezionato() {
		return activeNode;
	}
}

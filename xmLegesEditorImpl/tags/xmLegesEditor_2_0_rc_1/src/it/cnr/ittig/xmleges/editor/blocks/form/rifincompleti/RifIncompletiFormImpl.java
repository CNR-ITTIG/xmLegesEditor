package it.cnr.ittig.xmleges.editor.blocks.form.rifincompleti;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Vector;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.date.DateForm;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextField;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.rifincompleti.RifIncompleti;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.form.rifincompleti.RifIncompletiForm;
import it.cnr.ittig.xmleges.editor.services.form.urn.UrnForm;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.w3c.dom.Node;


/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.riferimenti.RiferimentiForm</code>.</h1>
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
 * @see
 * @version 1.0
 * @author <a href="mailto:tommaso.agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public class RifIncompletiFormImpl implements RifIncompletiForm, Loggable, Serviceable, Configurable, Initializable, ActionListener {
	
	Logger logger;

    Form form;

    Form sottoForm;

    DateForm dataFormDatiEvento;

    JTextField tagTipoEvento;

    JComboBox tagTipoRelazioneSottoFormDatiEvento;

    UrnForm urnFormRifIncompleti;

    JComboBox tagEffettoTipoSFormDatiEvento;

    Evento[] eventi;

    String tipoDocumento;

    String tipoDTD;

    String errorMessage = "";

    ListTextField eventi_listtextfield;
    
    Relazione[] rel_totali;
    
    String[] eventiOnVigenze;
    
    VigenzaEntity[] vigenze;
    
    VigenzaEntity[] vigToUpdate;
    
    UtilMsg utilMsg;
	
	EventManager eventManager;
	
	JTextArea textArea;
	
	DocumentManager documentManager;
	
	SelectionManager selectionManager;
    
	JButton ignoreButton;

	JButton ignoreAllButton;

	JButton replaceButton;

	JButton nextButton;
	
	Node[] riferimenti = null;
	
	Vector resolved = new Vector();
	
	Vector jumped = new Vector();

	int missRif;
	
	EditTransaction tr;
	
	int contaTr;
	
	RifIncompleti rifincompleti;
	
	NirUtilUrn nirutilurn;
	
	int rifiniziale = 0;
	
	boolean ricomincia = false;
	
    public boolean openForm(String testo, String urn) {

    	
    	//vettore dei riferimenti incompleti
    	riferimenti = rifincompleti.getList();
    
    	rifiniziale = 0;
		for (int i=0; i<riferimenti.length; i++)
		    if (riferimenti[i].equals(selectionManager.getActiveNode())) {
		    	 rifiniziale = i;
		    	 if (i==0) ricomincia = false; 
		    	 else ricomincia = true; 
		    	 break;
		    }
			
		resolved = new Vector();
		jumped = new Vector();
		missRif = 0;
		tr = null;
		contaTr = 0;
		
		form.setSize(650, 180);
		form.setName("editor.rifincompleto");
		missRif = showRif(riferimenti);		
		form.showDialog();


		if (form.isOk()) {
			try {
				if (tr != null)
					documentManager.commitEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
			}
		} else {
			if (tr != null)
				for (int j=0; j<contaTr; j++)
				  documentManager.rollbackEdit(tr);
		}
		//selectionManager.setSelectedText(this, selectionManager.getActiveNode(), 0, 0);		
		
		return form.isOk();	
	}


	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		form = (Form) serviceManager.lookup(Form.class);
        form.setName("editor.rifincompleto");

        urnFormRifIncompleti = (UrnForm) serviceManager.lookup(UrnForm.class);       
        utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
    
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		rifincompleti = (RifIncompleti) serviceManager.lookup(RifIncompleti.class);
		
		nirutilurn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);

	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("RifIncompleti.jfrm"));		
		form.replaceComponent("editor.form.meta.urn", urnFormRifIncompleti.getAsComponent());		
		form.setName("editor.rifincompleti");
		textArea = (JTextArea) form.getComponentByName("editor.form.rifincompleto.testo");
		
		ignoreButton = (JButton) form.getComponentByName("editor.form.rifincompleto.ignore");
		ignoreButton.addActionListener(this);
		ignoreAllButton = (JButton) form.getComponentByName("editor.form.rifincompleto.ignoreall");
		ignoreAllButton.addActionListener(this);
		replaceButton = (JButton) form.getComponentByName("editor.form.rifincompleto.replace");
		replaceButton.addActionListener(this);
		nextButton = (JButton) form.getComponentByName("editor.form.rifincompleto.next");
		nextButton.addActionListener(this);
		
	}
	
	private boolean isResolved(Node node) {

		int i = 0;
		while (i < resolved.size()) {
			if (resolved.get(i).equals(node))
				return true;
			i++;
		}
		return false;
	}
	
	private boolean isJumped(Node node) {

		int i = 0;
		while (i < jumped.size()) {
			if (jumped.get(i).equals(node))
				return true;
			i++;
		}
		return false;
	}
	
	
	private int showRif(Node[] riferimenti) {
		int i = rifiniziale;
		
		do {
			if (!isResolved(riferimenti[i]) && !isJumped(riferimenti[i])) {
				
				textArea.setText(rifincompleti.getText(riferimenti[i]));
				try{
					urnFormRifIncompleti.setUrn(new Urn(rifincompleti.getUrn(riferimenti[i])));
				}
				catch(ParseException e){urnFormRifIncompleti.setUrn(new Urn());}	
				selectionManager.setSelectedText(this, riferimenti[i], 0, riferimenti[i].getNodeValue().length());
				return i;
			}
			i++;
		} while (i < riferimenti.length);
		return (-1);
	}

	private int doIgnoreAll(Node[] riferimenti) {
		int i = 0;  //lo faccio su tutti
		Node modificato=null;
		textArea.setText("");
		urnFormRifIncompleti.setUrn(new Urn());
		do {
			if (!isResolved(riferimenti[i])) {
			  try {
				tr = documentManager.beginEdit();
				modificato = rifincompleti.setPlainText(riferimenti[i], rifincompleti.getText(riferimenti[i])); 
				if (null != modificato) {
					selectionManager.setSelectedText(this, modificato, 0, modificato.getNodeValue().length());
					contaTr++;					
					resolved.add(riferimenti[i]);
				}
				documentManager.commitEdit(tr);
			  } catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
			  }
			}
			i++;
		} while (i < riferimenti.length);
		ricomincia = false;
		return -1;
	}
	
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == nextButton) {
		    if (missRif != -1 && missRif < riferimenti.length) {
		    	jumped.add(riferimenti[missRif]);
				missRif = showRif(riferimenti);
				if (missRif == -1)
					if (ricomincia) 
						if (utilMsg.msgYesNo("rifincompleti.error.endrepeat")) {
							rifiniziale = 0;   
							ricomincia = false;
							missRif = showRif(riferimenti);
							if (missRif == -1) utilMsg.msgInfo("rifincompleti.error.endstop");									
						}
						else utilMsg.msgInfo("rifincompleti.error.endstop");
					else utilMsg.msgInfo("rifincompleti.error.endstop");								
			} else {
				if (ricomincia) 
					if (utilMsg.msgYesNo("rifincompleti.error.endrepeat")) {
						rifiniziale = 0;   
						ricomincia = false;
						missRif = showRif(riferimenti);
						if (missRif == -1) utilMsg.msgInfo("rifincompleti.error.endstop");									
					}
					else utilMsg.msgInfo("rifincompleti.error.endstop");								
				else utilMsg.msgInfo("rifincompleti.error.endstop");								
			  } 
		} 
		else if (evt.getSource() == ignoreButton) {
			if (missRif != -1 && missRif < riferimenti.length) {
			  try {
		 		tr = documentManager.beginEdit();
				if (null != rifincompleti.setPlainText(riferimenti[missRif], textArea.getText())) {
					contaTr++;					
					resolved.add(riferimenti[missRif]);
					missRif = showRif(riferimenti);
					if (missRif == -1)
						if (ricomincia) 
							if (utilMsg.msgYesNo("rifincompleti.error.endrepeat")) {
								rifiniziale = 0;   
								ricomincia = false;
								missRif = showRif(riferimenti);
								if (missRif == -1) utilMsg.msgInfo("rifincompleti.error.endstop");									
							}
							else utilMsg.msgInfo("rifincompleti.error.endstop");
						else utilMsg.msgInfo("rifincompleti.error.endstop");											
				}
				documentManager.commitEdit(tr);
 			  } catch (DocumentManagerException ex) {
 				logger.error(ex.getMessage(), ex);
 			  }
			} else {
				if (ricomincia) 
					if (utilMsg.msgYesNo("rifincompleti.error.endrepeat")) {
						rifiniziale = 0;   
						ricomincia = false;
						missRif = showRif(riferimenti);
						if (missRif == -1) utilMsg.msgInfo("rifincompleti.error.endstop");									
					}
					else utilMsg.msgInfo("rifincompleti.error.endstop");								
				else utilMsg.msgInfo("rifincompleti.error.endstop");								
			  } 
		} 
		else if (evt.getSource() == ignoreAllButton) {
			if (missRif != -1 && missRif < riferimenti.length) {
			  missRif = doIgnoreAll(riferimenti);
			  utilMsg.msgInfo("rifincompleti.error.endstop");
			}
			else utilMsg.msgInfo("rifincompleti.error.endstop");
		} 
		else if (evt.getSource() == replaceButton) {
			if (missRif != -1 && missRif < riferimenti.length) {
			 if (urnFormRifIncompleti.getUrn().isValid()) {
			  try {
		 		tr = documentManager.beginEdit();
		 		if (null != rifincompleti.setRif(riferimenti[missRif], nirutilurn.getFormaTestuale(urnFormRifIncompleti.getUrn()), urnFormRifIncompleti.getUrn())) {
					contaTr++;					
					resolved.add(riferimenti[missRif]);
					missRif = showRif(riferimenti);
					if (missRif == -1)
						if (ricomincia) 
							if (utilMsg.msgYesNo("rifincompleti.error.endrepeat")) {
								rifiniziale = 0;   
								ricomincia = false;
								missRif = showRif(riferimenti);
								if (missRif == -1) utilMsg.msgInfo("rifincompleti.error.endstop");									
							}
							else utilMsg.msgInfo("rifincompleti.error.endstop");
						else utilMsg.msgInfo("rifincompleti.error.endstop");								
				}
				documentManager.commitEdit(tr);
 			    } catch (DocumentManagerException ex) {
 				  logger.error(ex.getMessage(), ex);
 			    }
		     }
			 else {
				 utilMsg.msgError("rifincompleti.error.urn");
			 }
			} else {
				if (ricomincia) 
					if (utilMsg.msgYesNo("rifincompleti.error.endrepeat")) {
						rifiniziale = 0;   
						ricomincia = false;
						missRif = showRif(riferimenti);
						if (missRif == -1) utilMsg.msgInfo("rifincompleti.error.endstop");									
					}
					else utilMsg.msgInfo("rifincompleti.error.endstop");								
				else utilMsg.msgInfo("rifincompleti.error.endstop");								
			  } 			
		} 
	}

}

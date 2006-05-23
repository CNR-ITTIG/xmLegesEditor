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
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheckWord;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.form.rifincompleti.RifIncompletiForm;
import it.cnr.ittig.xmleges.editor.services.form.urn.UrnForm;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Element;


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
	
	Vector riferimenti = null;
	
	Vector ignored = new Vector();

	Vector ignoredAll = new Vector();

	Vector inserted = new Vector();
	
	int missRif;
	
	EditTransaction tr;
	
	int contaTr;
	
    public boolean openForm(String testo, String urn) {

    	
//    	//volevo fare la collezione dei riferimenti sbagliati...non sono riuscito a far di meglio
//    	NodeList nodi = documentManager.getDocumentAsDom().getElementsByTagName("*");
//		riferimenti = new Vector();
//		for (int i=0; i<nodi.getLength(); i++)
//			if ((nodi.item(i).getNodeType()==Node.PROCESSING_INSTRUCTION_NODE) && ((ProcessingInstruction) nodi.item(i)).getTarget().equals("rif")) {
//		    	riferimenti.add(nodi.item(i));
//		    }
//		
//		System.err.println("rif trovati n° " + riferimenti.size());
//		int rifcorrente = 0;
//		for (int i=0; i<riferimenti.size(); i++)
//		    if (riferimenti.get(i).equals(selectionManager.getActiveNode())) {
//		    	 rifcorrente = i;
//		    	 break;
//		    }
//		System.err.println("rif n° " + rifcorrente);	
    	    	
		Document document = documentManager.getDocumentAsDom();
		Element newRif = null;
		riferimenti = new Vector();
		//nell'ipotetico for di sopra faccio
//		  newRif = (Element) selectionManager.getActiveNode();
//		  riferimenti.add(newRif);
//		  contaTr = 1;
		
		  
		ignored = new Vector();
		ignoredAll = new Vector();
		inserted = new Vector();
		missRif = 0;
		tr = null;
		contaTr = 0;
		
		form.setSize(650, 180);
		form.setName("editor.rifincompleto");
		textArea.setText(testo);
		try{
			urnFormRifIncompleti.setUrn(new Urn(urn));
		}
		catch(ParseException e){urnFormRifIncompleti.setUrn(new Urn());}
		form.showDialog();

/////////
		
		

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
		//selectionManager.setSelectedText(this, activeNode, start, start);
		
/////////		
		
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
        //utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);

		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		//eventManager.addListener(this, SelectionChangedEvent.class);				
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
	
	private boolean isIgnored(Element node) {

		int i = 0;
		while (i < ignored.size()) {
			if (ignored.get(i).equals(node))
				return true;
			i++;
		}
		return false;
	}

	private boolean isIgnoredAll(Element node) {

        //non ha senso (è lo stesso di isIgnored) 
		//cambiare il comportamento come "esci dalla form e APPIATTISCI TUTTO"
		return false;
	}

	private boolean isInserted(Element node) {

		
		//anche questo ha poco senso...basterebbe un unico vettore per tutto (isIndex)
		int i = 0;
		while (i < inserted.size()) {
			if (inserted.get(i).equals(node))
				return true;
			i++;
		}
		return false;
	}
	
	private int showRif(Vector riferimenti) {
		int i = 0;
		do {
			if (!isIgnored((Element) riferimenti.get(i)) && !isIgnoredAll((Element) riferimenti.get(i)) && !isInserted((Element) riferimenti.get(i))) {
				return i;
			}
			i++;
		} while (i < riferimenti.size());
		return (-1);
	}

	
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == nextButton) {
			 System.err.println("next");
		} 
		else if (evt.getSource() == ignoreButton) {
			System.err.println("ignore");
		} 
		else if (evt.getSource() == ignoreAllButton) {
			System.err.println("ignoreall");
		} 
		else if (evt.getSource() == replaceButton) {
			System.err.println("replace");
		} 
	}

}

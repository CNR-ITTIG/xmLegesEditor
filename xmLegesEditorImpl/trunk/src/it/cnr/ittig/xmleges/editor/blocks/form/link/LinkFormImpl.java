package it.cnr.ittig.xmleges.editor.blocks.form.link;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.editor.services.dom.link.Link;
import it.cnr.ittig.xmleges.editor.services.form.link.LinkForm;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.link.linkForm</code>.</h1>
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
public class LinkFormImpl implements LinkForm, Loggable, Serviceable, Configurable, Initializable, ActionListener, FormVerifier {
	
	Logger logger;

    Form form;
    
    Node nodoCorrente;    
    
    boolean vecchioLink;
   
    int start;
    
    int end;
    
    UtilMsg utilMsg;
	
	EventManager eventManager;
	
	JTextField textUrl;
	
	DocumentManager documentManager;
	
	SelectionManager selectionManager;
    
	JButton verificaButton;
	
	EditTransaction tr;
	
	Link link;
	
	String[] browsers;
	
	String parteTestuale;
	
    public boolean openForm(Node node, String testo, String url) {

    	nodoCorrente = node;		
		if (node.getNodeType()==Node.TEXT_NODE) {
			if (node.getParentNode().getNodeName().equals("h:a"))
				vecchioLink=true;
			else 	
				vecchioLink=false;
		}
		else 
			vecchioLink = (node.getNodeName().equals("h:a"));
			
    	start = selectionManager.getTextSelectionStart();
    	end = selectionManager.getTextSelectionEnd();
		form.setSize(450, 150);
		form.setName("editor.link");		
		
		textUrl.setText(url);
		form.addFormVerifier(this);
		form.showDialog();

		if (form.isOk()) {
			if (testo!=null && testo.length()!=0)
				parteTestuale = testo;
			else
				parteTestuale = textUrl.getText().trim();
			settaLink();
		}
		selectionManager.setSelectedText(this, nodoCorrente, 0, 0);				
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
        form.setName("editor.link");
        utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		link = (Link) serviceManager.lookup(Link.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration conf) throws ConfigurationException {
		Configuration bs = conf.getChild("browsers");
		if (bs != null) {
			Configuration[] b = bs.getChildren("browser");
			browsers = new String[b.length];
			for (int i = 0; i < b.length; i++)
				browsers[i] = b[i].getValue();
		}
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("Link.jfrm"));				
		form.setName("editor.link");
		textUrl = (JTextField) form.getComponentByName("editor.form.link.url");
		verificaButton = (JButton) form.getComponentByName("editor.form.link.verifica");
		verificaButton.addActionListener(this);		
		form.setHelpKey("help.contents.index.link");
	}
			
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == verificaButton) {
			logger.debug("Eseguo browser");
			for (int i = 0; i < browsers.length; i++)
				try {
					String cmd = browsers[i] + " " + textUrl.getText();
					Runtime.getRuntime().exec(cmd);
					break;
				} catch (Exception ex) {
				}
		} 
	}

	private void settaLink() {
		try {
	 		tr = documentManager.beginEdit();
	 		
	 		if (vecchioLink && nodoCorrente!=null) { //aggiorno i valori del link
                try {
                	Node nodoDAggiornare = nodoCorrente;
                	nodoDAggiornare = link.setText(nodoDAggiornare, parteTestuale);
                	nodoDAggiornare = link.setUrl(nodoDAggiornare, textUrl.getText());
                	nodoDAggiornare = link.setType(nodoDAggiornare, "simple");
                	nodoCorrente = nodoDAggiornare;
                	logger.debug("aggiornamento link riuscito");
                } catch (Exception ex) {
    				logger.debug("aggiornamento link non riuscito");
    				documentManager.rollbackEdit(tr);
    			}
	 		}
	 		else { //eseguo un insert
	 				Node nodoDAggiornare = nodoCorrente;
                	nodoDAggiornare = link.insert(nodoDAggiornare, start, end, parteTestuale ,textUrl.getText().trim(),"simple");
                	if (nodoDAggiornare != null) {
                		nodoCorrente = nodoDAggiornare;	
                	    logger.debug("inserimento link riuscito");
                	}    
                	else {
                		logger.debug("inserimento link non riuscito");
                		documentManager.rollbackEdit(tr);
                	}	
	 		}

	 		documentManager.commitEdit(tr);
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			documentManager.rollbackEdit(tr);
		}
		form.close();
	}	

	public boolean verifyForm() {
		return (textUrl.getText().trim().length() != 0);
	}

	public String getErrorMessage() {
		return ("editor.form.link.datinonvalidi");
	}	
}

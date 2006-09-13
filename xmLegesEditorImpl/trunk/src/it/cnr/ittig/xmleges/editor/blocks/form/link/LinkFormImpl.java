package it.cnr.ittig.xmleges.editor.blocks.form.link;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
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
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.form.date.DateForm;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextField;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.link.Link;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.form.link.LinkForm;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
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
public class LinkFormImpl implements LinkForm, Loggable, Serviceable, Configurable, Initializable, ActionListener, FormClosedListener {
	
	Logger logger;

    Form form;
    
    Node nodoCorrente;    
    
    boolean vecchioLink;
   
    int start;
    
    int end;
    
    UtilMsg utilMsg;
	
	EventManager eventManager;
	
	JTextArea textOriginal;

	JTextArea textUrl;
	
	JTextArea textType;
	
	DocumentManager documentManager;
	
	SelectionManager selectionManager;
    
	JButton eliminaButton;

	JButton verificaButton;
		
	EditTransaction tr;
	
	Link link;
	
	String[] browsers;
	
    public boolean openForm(Node node, String testo, String url) {
		
    	nodoCorrente = node;
    	vecchioLink =  node.getNodeName().equals("rif");
    	start = selectionManager.getTextSelectionStart();
    	end = selectionManager.getTextSelectionEnd();
    	eliminaButton.setEnabled(vecchioLink);
		form.setSize(450, 200);
		form.setName("editor.link");		
		
		textOriginal.setText(testo);
		textUrl.setText(url);
		textType.setText("simple");     //CHIEDERE se necessita di combo o altro !!!!!
		form.showDialog();


		if (form.isOk()) {
			try {
		 		tr = documentManager.beginEdit();
		 		if (vecchioLink) { //aggiorno i valori del link
	                try {
	                	Node nodoDAggiornare = nodoCorrente;
	                	nodoDAggiornare = link.setText(nodoDAggiornare, textOriginal.getText());
	                	nodoDAggiornare = link.setUrl(nodoDAggiornare, textUrl.getText());
	                	nodoDAggiornare = link.setType(nodoDAggiornare, textType.getText());
	                	nodoCorrente = nodoDAggiornare;
	                	logger.debug("aggiornamento link riuscito");
	                } catch (Exception ex) {
	    				logger.debug("aggiornamento link non riuscito");
	    				documentManager.rollbackEdit(tr);
	    			}
		 		}
		 		else { //eseguo un insert
		 				Node nodoDAggiornare = nodoCorrente;
	                	nodoDAggiornare = link.insert(nodoDAggiornare, start, end, textOriginal.getText(),textUrl.getText(),textType.getText());
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
		textOriginal = (JTextArea) form.getComponentByName("editor.form.link.testo");
		textUrl = (JTextArea) form.getComponentByName("editor.form.link.url");
		textType = (JTextArea) form.getComponentByName("editor.form.link.type");
		eliminaButton = (JButton) form.getComponentByName("editor.form.link.elimina");
		eliminaButton.addActionListener(this);
		verificaButton = (JButton) form.getComponentByName("editor.form.link.verifica");
		verificaButton.addActionListener(this);		
		form.setHelpKey("help.contents.index.link",this);
	}
			
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == eliminaButton) {
			try {
				tr = documentManager.beginEdit();
				Node modificato = link.setPlainText(nodoCorrente, textOriginal.getText());
				if (null != modificato) {
					logger.debug("Eliminazione link riuscita");
					nodoCorrente = modificato;
					documentManager.commitEdit(tr);
				}
				else {
					logger.debug("Eliminazione link fallita");
					documentManager.rollbackEdit(tr);
				}
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
				documentManager.rollbackEdit(tr);
			}
			form.close();
		} 
		else if (evt.getSource() == verificaButton) {
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


	public void formClosed() {
		// TODO Auto-generated method stub
		
	}



}

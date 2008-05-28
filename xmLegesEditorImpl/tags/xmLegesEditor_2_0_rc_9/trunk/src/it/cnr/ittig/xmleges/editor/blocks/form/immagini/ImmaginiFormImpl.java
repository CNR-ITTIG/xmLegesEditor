package it.cnr.ittig.xmleges.editor.blocks.form.immagini;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.editor.services.form.immagini.ImmaginiForm;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.RegexpFileFilter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.dom.immagini.Immagini;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

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
 */
public class ImmaginiFormImpl implements ImmaginiForm, Loggable, Serviceable, Initializable, ActionListener {
	
	Logger logger;

	Form form;

	Node nodoCorrente;

	UtilMsg utilMsg;

	EventManager eventManager;

	JTextField nomeFile;

	JTextField width;

	JTextField height;
	
	JTextField alt;

	DocumentManager documentManager;

	SelectionManager selectionManager;

	JFileChooser fileChooser;
	
	JComboBox widthChooser;
	
	JComboBox heightChooser;
	
	JButton aprifile;

	EditTransaction tr;
	
	Immagini domimmagini;

	public boolean openForm(Node node) {

		nodoCorrente = node;
		form.setSize(550, 200);
		
		if (node.getNodeName().equals("h:img")) {
			
			String baseUrl = UtilFile.getFolderPath(documentManager.getSourceName());
			
			nomeFile.setText(baseUrl.substring(0,baseUrl.length()-1)+ File.separatorChar + UtilDom.getAttributeValueAsString(node, "src"));
			width.setText(UtilDom.getAttributeValueAsString(node, "width"));
			widthChooser.setSelectedIndex(0);
			if (width.getText().indexOf("px")!=-1)
				width.setText(width.getText().substring(0,width.getText().indexOf("px")));
			if (width.getText().indexOf("%")!=-1) {
				width.setText(width.getText().substring(0,width.getText().indexOf("%")));
				widthChooser.setSelectedIndex(1);
			}
			height.setText(UtilDom.getAttributeValueAsString(node, "height"));
			heightChooser.setSelectedIndex(0);
			if (height.getText().indexOf("px")!=-1)
				height.setText(height.getText().substring(0,height.getText().indexOf("px")));
			if (height.getText().indexOf("%")!=-1) {
				height.setText(height.getText().substring(0,height.getText().indexOf("%")));
				heightChooser.setSelectedIndex(1);
			}
			alt.setText(UtilDom.getAttributeValueAsString(node, "alt"));
		}	
		else {
			nomeFile.setText("");
			width.setText("");
			height.setText("");
			alt.setText("");
			widthChooser.setSelectedIndex(0);
			heightChooser.setSelectedIndex(0);
		}
		form.showDialog();
		if (form.isOk()) {

		try {	
			String nome="";
			// inserisco immagine nella cartella
			if (!(nome = UtilFile.copyInFolder(documentManager.getSourceName(),nomeFile.getText())).equals("")) {
				int altezza, larghezza;
				try {
					altezza = new Integer(height.getText()).intValue();
				} catch (Exception e) {altezza=-1;}
				try {
					larghezza = new Integer(width.getText()).intValue();
				} catch (Exception e) {larghezza=-1;}
				//inserisco l'immagine nel dom
				domimmagini.insert(node, nome, larghezza, widthChooser.getSelectedIndex()==0?"px":"%", altezza, heightChooser.getSelectedIndex()==0?"px":"%", alt.getText());
			}	
		} catch (IOException ex) {
			return form.isCancel();
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
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		domimmagini = (Immagini) serviceManager.lookup(Immagini.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("Immagini.jfrm"));
		form.setName("editor.insert.immagini");
		alt = (JTextField) form.getComponentByName("editor.form.immagini.alt");
		width = (JTextField) form.getComponentByName("editor.form.immagini.width");
		height = (JTextField) form.getComponentByName("editor.form.immagini.height");
		width = (JTextField) form.getComponentByName("editor.form.immagini.width");
		nomeFile = (JTextField) form.getComponentByName("editor.form.immagini.nomefile");
		heightChooser = (JComboBox) form.getComponentByName("editor.form.immagini.heightChooser");
		widthChooser = (JComboBox) form.getComponentByName("editor.form.immagini.widthChooser");
		heightChooser.addItem(" px ");
		heightChooser.addItem(" % ");
		widthChooser.addItem(" px ");
		widthChooser.addItem(" % ");
		aprifile = (JButton) form.getComponentByName("editor.form.immagini.aprifile");
		aprifile.addActionListener(this);
		
		form.setHelpKey("help.contents.form.immagini");
	}

	public void actionPerformed(ActionEvent evt) {

		if (evt.getSource() == aprifile) {
			fileChooser = new JFileChooser();
			//fileChooser.setCurrentDirectory();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setAcceptAllFileFilterUsed(false);
			String[] masks = new String[2];
			masks[0] = ".*\\.jpg";
			masks[1] = ".*\\.gif";
			fileChooser.setFileFilter(new RegexpFileFilter("Image File", masks));
			
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				nomeFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		}
	}

}

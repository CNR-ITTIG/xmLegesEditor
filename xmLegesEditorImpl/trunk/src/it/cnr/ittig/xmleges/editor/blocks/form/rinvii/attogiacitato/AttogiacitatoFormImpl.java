package it.cnr.ittig.xmleges.editor.blocks.form.rinvii.attogiacitato;

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
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.autorita.Autorita;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.attogiacitato.AttogiacitatoForm;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.Provvedimenti;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.text.ParseException;
import java.util.Collections;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.rinvii.attogiacitato.AttogiacitatoForm</code>.</h1>
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
 * @author <a href="sarti@dii.unisi.it">Lorenzo Sarti </a>
 */
public class AttogiacitatoFormImpl implements AttogiacitatoForm, Loggable, Serviceable, Configurable, Initializable, FormVerifier {
	Logger logger;

	Form form;

	UtilUI utilui;

	UtilMsg utilmsg;

	DocumentManager dm;

	Provvedimenti provvedimenti;

	Autorita registroautorita;

	NirUtilUrn nirutilurn;

	Vector urnVector;

	// Oggetti grafici
	JList elenco;

	DefaultListModel lmelenco;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		logger.debug("Avvio servizi");
		form = (Form) serviceManager.lookup(Form.class);
		utilui = (UtilUI) serviceManager.lookup(UtilUI.class);
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		dm = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		provvedimenti = (Provvedimenti) serviceManager.lookup(Provvedimenti.class);
		registroautorita = (Autorita) serviceManager.lookup(Autorita.class);
		nirutilurn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		logger.debug("Fine attivazione servizi");
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		logger.debug("Inizializzazione Form Attogiacitato");
		form.setMainComponent(this.getClass().getResourceAsStream("Attogiacitato.jfrm"));
		form.setSize(600, 350);
		form.setName("editor.form.rinvii.attogiacitato");
		
		form.setHelpKey("help.contents.form.attogiacitato");
		
		elenco = (JList) form.getComponentByName("editor.form.rinvii.attogiacitato.list.elenco");
		JLabel labelelenco = (JLabel) form.getComponentByName("editor.form.rinvii.attogiacitato.label.elenco");

		logger.debug("Fine inizializzazione");
		logger.debug("Inizio internazionalizzazione");
		utilui.applyI18n(labelelenco);
		logger.debug("Fine internazionalizzazione");
	}

	private void preparaElenco(NamedNodeMap[] elenconodi, int index) {

		urnVector = new Vector();
		lmelenco = new DefaultListModel();
		if (lmelenco.size() > 0)
			lmelenco.removeAllElements();
		elenco.setModel(lmelenco);
		for (int i = 0; i < elenconodi.length; i++) {
			try {
				if ((elenconodi[i].item(index).getNodeValue().length() > 0) && (elenconodi[i].item(index).getNodeValue().length() > 3)
						&& (!elenconodi[i].item(index).getNodeValue().substring(0, 4).equals("http"))
						&& (!elenconodi[i].item(index).getNodeValue().equals("rif1")) && (!elenconodi[i].item(index).getNodeValue().equals("simple"))
						&& (!elenconodi[i].item(index).getNodeValue().startsWith("#"))) { // elimino i rif interni?
					String urn = elenconodi[i].item(index).getNodeValue();
					String[] urnbase = urn.split("#");
					Urn nuovaurn = new Urn();
					nuovaurn.parseUrn(urn);
					nuovaurn.setFormaTestuale(nirutilurn.getFormaTestuale(nuovaurn));
					if(!urnVector.contains(nuovaurn))
						urnVector.add(nuovaurn);
					if ((urnbase.length > 1) && (!(urnbase[0].equals("")))) {
						Urn nuovaurnbase = new Urn();
						nuovaurnbase.parseUrn(urnbase[0]);
						nuovaurnbase.setFormaTestuale(nirutilurn.getFormaTestuale(nuovaurnbase));
						if(!urnVector.contains(nuovaurnbase))
							urnVector.add(nuovaurnbase);
					}
				}
			} catch (Exception e) {
				// utilmsg.msgError(e.toString());
			}
		}
		
        Collections.sort(urnVector);
        for(int i=0;i<urnVector.size();i++)
        	lmelenco.addElement(((Urn)urnVector.get(i)).getFormaTestuale());
        
	}
	
	
	// FIXME: dopo qualche test questo metodo si puo eliminare
	private void preparaElencoOLD(NamedNodeMap[] elenconodi, int index) {

		
		urnVector = new Vector();
		lmelenco = new DefaultListModel();
		if (lmelenco.size() > 0)
			lmelenco.removeAllElements();
		elenco.setModel(lmelenco);
		for (int i = 0; i < elenconodi.length; i++) {
			try {
				if ((elenconodi[i].item(index).getNodeValue().length() > 0) && (elenconodi[i].item(index).getNodeValue().length() > 3)
						&& (!elenconodi[i].item(index).getNodeValue().substring(0, 4).equals("http"))
						&& (!elenconodi[i].item(index).getNodeValue().equals("rif1")) && (!elenconodi[i].item(index).getNodeValue().equals("simple"))
						&& (!elenconodi[i].item(index).getNodeValue().startsWith("#"))) { // elimino
																							// i
																							// rif
																							// interni?
					String urn = elenconodi[i].item(index).getNodeValue();
					String[] urnbase = urn.split("#");
					Urn nuovaurn = new Urn();
					nuovaurn.parseUrn(urn);
					nuovaurn.setFormaTestuale(nirutilurn.getFormaTestuale(nuovaurn));
					lmelenco.addElement(nuovaurn.getFormaTestuale());
					urnVector.add(nuovaurn.toString());
					if ((urnbase.length > 1) && (!(urnbase[0].equals("")))) {
						Urn nuovaurnbase = new Urn();
						nuovaurnbase.parseUrn(urnbase[0]);
						nuovaurnbase.setFormaTestuale(nirutilurn.getFormaTestuale(nuovaurnbase));
						lmelenco.addElement(nuovaurnbase.getFormaTestuale());
						urnVector.add(nuovaurnbase.toString());
					}
				}
			} catch (Exception e) {
				// utilmsg.msgError(e.toString());
			}
		}
		System.err.println("elenco NON ordinato; size: "+urnVector.size()); 
	}

	

	private void popolaElenco() {
		int indexofAttribute = 0;
		NodeList listarif = dm.getDocumentAsDom().getElementsByTagName("rif"); 
		
		// nota: in questo modo prende anche i rif dentro  gli mrif
		
		Vector rifVect = new Vector(listarif.getLength());

		for (int i = 0; i < listarif.getLength(); ++i) {
			if (!containsAttribute(rifVect, UtilDom.getAttributeValueAsString(listarif.item(i), "xlink:href")))
				rifVect.add(listarif.item(i));
		}

		NamedNodeMap[] nodemap = new NamedNodeMap[rifVect.size()];

		for (int i = 0; i < rifVect.size(); ++i)
			nodemap[i] = ((Node) rifVect.get(i)).getAttributes();

		if (rifVect.size() > 0)
			if (nodemap[0].item(0).getNodeValue().equals("simple"))
				indexofAttribute = 1;

		/* Trasforma le urn in citazioni testuali */
		preparaElenco(nodemap, indexofAttribute);
	}

	private boolean containsAttribute(Vector v, String attr) {
		for (int j = 0; j < v.size(); j++) {
			if (UtilDom.getAttributeValueAsString((Node) v.get(j), "xlink:href").equalsIgnoreCase(attr))
				return true;
		}
		return false;
	}

	public String getUrn() {
		return (((Urn)urnVector.get(elenco.getSelectedIndex())).toString());
	}

	public boolean openForm() {
		form.setDialogResizable(false);
		form.addFormVerifier(this);
		popolaElenco();
		if (lmelenco.size() > 0) {
			elenco.setSelectedIndex(0);
			form.showDialog();
			if (form.isOk())
				return true;
			else
				return false;
		} else {
			utilmsg.msgError("editor.form.rinvii.attogiacitato.msg.noitems");
			return false;
		}

	}

	public String getErrorMessage() {
		return "editor.form.rinvii.attogiacitato.msg.noitemselected";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.cnr.ittig.xmleges.editor.services.form.FormVerifier#verifyForm()
	 */
	public boolean verifyForm() {
		if (elenco.getSelectedValue() != null)
			return true;
		else
			return false;
	}
}

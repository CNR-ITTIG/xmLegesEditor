package it.cnr.ittig.xmleges.editor.blocks.dom.rinumerazione;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.document.DocumentBeforeInitUndoAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione</code>.
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class RinumerazioneImpl implements Rinumerazione, DocumentBeforeInitUndoAction, Loggable, Serviceable, Startable, Initializable {
	Logger logger;

	AggiornaIdFrozenLaw aggiornaIdFrozenLaw;

	AggiornaNumerazioneAndLink aggiornaNumerazioneAndLink;

	DocumentManager documentManager;

	RulesManager rulesManager;

	PreferenceManager preferenceManager;

	NirUtilDom nirUtilDom;

	NirUtilUrn nirUtilUrn;

	boolean renum = false;

	String tipo;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
	}

	public void start() throws Exception {
	}

	public void stop() throws Exception {
		Properties props = preferenceManager.getPreferenceAsProperties(this.getClass().getName());
		props.setProperty("ndr", tipo);
		preferenceManager.setPreference(this.getClass().getName(), props);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		documentManager.addBeforeInitUndoAction(this);
		Properties props = preferenceManager.getPreferenceAsProperties(this.getClass().getName());
		if (props.containsKey("ndr")) {
			tipo = props.get("ndr").toString().trim();
			logger.debug("tipo: " + tipo);
		} else {
			tipo = "cardinale";
			props.setProperty("ndr", tipo);
			preferenceManager.setPreference(this.getClass().getName(), props);
			logger.debug("tipo: " + tipo);
		}
		aggiornaNumerazioneAndLink = new AggiornaNumerazioneAndLink(this);
	}

	// ///////////////////////////////////////////////// Rinumerazione Interface
	public void aggiorna(Document document) {
		logger.debug("rinumerazione START");
		NodeList nir = document.getElementsByTagName("NIR");
		try{
			if (renum)
				aggiornaNumerazioneAndLink.aggiornaNum(nir.item(0));
			else
				aggiornaNumerazioneAndLink.aggiornaID(nir.item(0));
			logger.debug("rinumerazione END");
		}catch(Exception ex){
			logger.error("rinumerazione non applicabile");
		}
	}

	public void setRinumerazione(boolean renum) {
		this.renum = renum;
	}

	public boolean isRinumerazione() {
		return this.renum;
	}

	protected Logger getLogger() {
		return this.logger;
	}

	protected RulesManager getRulesManager() {
		return this.rulesManager;
	}

	public String getRinumerazioneNdr() {
		return this.tipo;
	}

	public void setRinumerazioneNdr(String tipo) {
		this.tipo = tipo;
	}

	// ////////////////////////////////// DocumentBeforeInitUndoAction Interface
	public boolean beforeInitUndo(Document dom) {
		boolean isRenum = isRinumerazione();
		setRinumerazione(false);
		aggiorna(dom);
		setRinumerazione(isRenum);
		return true;
	}

}

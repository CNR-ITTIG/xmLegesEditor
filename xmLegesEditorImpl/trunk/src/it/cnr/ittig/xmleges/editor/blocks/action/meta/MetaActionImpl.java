package it.cnr.ittig.xmleges.editor.blocks.action.meta;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.meta.MetaAction;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.MetaCiclodivita;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.cnr.MetaCnr;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.MetaDescrittori;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.MetaDescrittoriMaterie;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Vocabolario;
import it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento.MetaInquadramento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.urndocumento.MetaUrnDocumento;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.Vigenza;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita.CiclodiVitaForm;
import it.cnr.ittig.xmleges.editor.services.form.meta.cnr.CnrProprietariForm;
import it.cnr.ittig.xmleges.editor.services.form.meta.descrittori.MaterieVocabolariForm;
import it.cnr.ittig.xmleges.editor.services.form.meta.descrittori.MetaDescrittoriForm;
import it.cnr.ittig.xmleges.editor.services.form.meta.inquadramento.InquadramentoForm;
import it.cnr.ittig.xmleges.editor.services.form.meta.urn.UrnDocumentoForm;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.newrinvii.NewRinviiForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.meta.MetaAction</code>.</h1>
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class MetaActionImpl implements MetaAction, EventManagerListener, Loggable, Serviceable, Initializable {

	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	DocumentManager documentManager;

	AbstractAction descrittoriAction = new DescrittoriAction();
	
	AbstractAction ciclodivitaAction = new CiclodiVitaAction();
	
	AbstractAction inquadramentoAction = new InquadramentoAction();
	
	AbstractAction materieAction = new MaterieAction();

	AbstractAction urnAction = new urnAction();
	
	AbstractAction cnrAction = new cnrAction();

	MetaDescrittori descrittori;
	
	MetaCiclodivita ciclodivita; //dom
	
	MetaInquadramento inquadramento;
	
	MetaUrnDocumento metaUrnDocumento;
	
	MetaDescrittoriMaterie materie;
	
	MetaCnr metaCnr;
	
	MetaDescrittoriForm descrittoriForm;
	
	CiclodiVitaForm ciclodivitaForm;

	UrnDocumentoForm urnDocumentoForm;
	
	CnrProprietariForm cnrForm;
	
	InquadramentoForm inquadramentoForm;
	
	MaterieVocabolariForm materieForm;

	Rinumerazione rinumerazione;
	
	SelectionManager selectionManager;

	UtilRulesManager utilRulesManager;

	NewRinviiForm newrinvii;

	NirUtilUrn nirUtilUrn;

	NirUtilDom nirUtilDom;
	
	Vigenza vigenza;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		descrittori = (MetaDescrittori) serviceManager.lookup(MetaDescrittori.class);
		ciclodivita = (MetaCiclodivita) serviceManager.lookup(MetaCiclodivita.class);
		inquadramento = (MetaInquadramento) serviceManager.lookup(MetaInquadramento.class);
		materie = (MetaDescrittoriMaterie) serviceManager.lookup(MetaDescrittoriMaterie.class);
		metaCnr = (MetaCnr) serviceManager.lookup(MetaCnr.class);
		metaUrnDocumento = (MetaUrnDocumento) serviceManager.lookup(MetaUrnDocumento.class);
		descrittoriForm = (MetaDescrittoriForm) serviceManager.lookup(MetaDescrittoriForm.class);
		ciclodivitaForm = (CiclodiVitaForm) serviceManager.lookup(CiclodiVitaForm.class);
		inquadramentoForm = (InquadramentoForm) serviceManager.lookup(InquadramentoForm.class);
		materieForm = (MaterieVocabolariForm) serviceManager.lookup(MaterieVocabolariForm.class);
		newrinvii = (NewRinviiForm) serviceManager.lookup(NewRinviiForm.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		urnDocumentoForm = (UrnDocumentoForm) serviceManager.lookup(UrnDocumentoForm.class);
		cnrForm = (CnrProprietariForm) serviceManager.lookup(CnrProprietariForm.class);
		vigenza = (Vigenza) serviceManager.lookup(Vigenza.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("editor.meta.descrittori", descrittoriAction);
		actionManager.registerAction("editor.meta.ciclodivita", ciclodivitaAction);
		actionManager.registerAction("editor.meta.inquadramento", inquadramentoAction);
		actionManager.registerAction("editor.meta.descrittori.materie", materieAction);
		actionManager.registerAction("editor.meta.urn", urnAction);
		actionManager.registerAction("editor.meta.cnr", cnrAction);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		descrittoriAction.setEnabled(false);
		ciclodivitaAction.setEnabled(false);
		inquadramentoAction.setEnabled(false);
		materieAction.setEnabled(false);
		urnAction.setEnabled(false);
		cnrAction.setEnabled(false);		
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		descrittoriAction.setEnabled(!documentManager.isEmpty() && !utilRulesManager.isDtdDL());
		ciclodivitaAction.setEnabled(!documentManager.isEmpty() && !utilRulesManager.isDtdDL());
		inquadramentoAction.setEnabled(!documentManager.isEmpty() && !utilRulesManager.isDtdDL() && !utilRulesManager.isDtdBase());
			
		materieAction.setEnabled(!documentManager.isEmpty() && !utilRulesManager.isDtdDL() && !utilRulesManager.isDtdBase());
	
		urnAction.setEnabled(!documentManager.isEmpty());		
		cnrAction.setEnabled(!documentManager.isEmpty() && documentManager.getDtdName().equals("cnr.dtd"));
	}
	
	

	// //////////////////////////////////////////// MetaGeneraliAction Interface
	public void doDescrittori() {
		logger.debug("Metadati Descrittori");
		
		Node node = selectionManager.getActiveNode();
		Document doc = documentManager.getDocumentAsDom();
		descrittoriForm.setTipoDTD(documentManager.getDtdName());
		descrittoriForm.setAlias(descrittori.getAlias(node));
		descrittoriForm.setAltrePubblicazioni(descrittori.getAltrePubblicazioni(node));
		descrittoriForm.setPubblicazione(descrittori.getPubblicazione(node));
		descrittoriForm.setRedazione(descrittori.getRedazione(node));

		if (descrittoriForm.openForm()) {
			try {
				EditTransaction tr = documentManager.beginEdit();
				UtilDom.setAttributeValue(doc.getDocumentElement(), "tipo", descrittoriForm.getTipoPubblicazione());
				descrittori.setAlias(node, descrittoriForm.getAlias());
				descrittori.setPubblicazione(node, descrittoriForm.getPubblicazione());
				descrittori.setAltrePubblicazioni(node, descrittoriForm.getAltrePubblicazioni());
				descrittori.setRedazione(node, descrittoriForm.getRedazione());
				rinumerazione.aggiorna(doc);
				documentManager.commitEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}		
	}
	
	public void doInquadramento() {
		Document doc = documentManager.getDocumentAsDom();
		Node node = selectionManager.getActiveNode();
		inquadramento.setActiveNode(node);
		inquadramentoForm.setInfodoc(inquadramento.getInfodoc());
		inquadramentoForm.setInfomancanti(inquadramento.getInfomancanti());
		inquadramentoForm.setOggetto(inquadramento.getOggetto());
		inquadramentoForm.setProponenti(inquadramento.getProponenti());
		inquadramentoForm.setTipoDTD(documentManager.getDtdName());
		
		if (inquadramentoForm.openForm()) {
			try {
				EditTransaction tr = documentManager.beginEdit();
				inquadramento.setInfodoc(inquadramentoForm.getInfodoc());
				inquadramento.setInfomancanti(inquadramentoForm.getInfomancanti());
				inquadramento.setOggetto(inquadramentoForm.getOggetto());
				inquadramento.setProponenti(inquadramentoForm.getProponenti());
				rinumerazione.aggiorna(doc);
				documentManager.commitEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}

 
		
	}
	public void doMaterie() {
		Node node = selectionManager.getActiveNode();
		Vocabolario[] vocabolariOnDoc=materie.getVocabolari(node);		
		materieForm.setVocabolari(vocabolariOnDoc);

		if (materieForm.openForm()) {
				Vocabolario[] vocabolariOnForm=materieForm.getVocabolari();
				materie.setVocabolari(node, vocabolariOnForm);												
		}
		
	}
	public void doCiclodiVita() {
		
		Node node = selectionManager.getActiveNode();
		ciclodivita.setActiveNode(node);
		Evento[] eventiOnDom = ciclodivita.getEventi();
//		Relazione[] relazioniOnDom = ciclodivita.getRelazioni();
		
		ciclodivitaForm.setEventi(eventiOnDom);
//		ciclodivitaForm.setRelazioniUlteriori(ciclodivita.getRelazioniUlteriori(eventiOnDom,relazioniOnDom));
		
		String[] id_eventiOnVigenze =ciclodivita.getEventiOnVigenza();
		VigenzaEntity[] vigenze = ciclodivita.getVigenze();
		ciclodivitaForm.setEventiOnVigenze(id_eventiOnVigenze, vigenze);
				
		if(ciclodivitaForm.openForm()){
			Evento[] newEventi = ciclodivitaForm.getEventi();
//			Relazione[] relazioniUlteriori = ciclodivitaForm.getRelazioniUlteriori();
			
//			Relazione[] newRelazioni = ciclodivita.mergeRelazioni(newEventi,relazioniUlteriori);
			Relazione[] newRelazioni = null;
			if(newEventi!=null){
				newRelazioni = new Relazione[newEventi.length];
				for(int i=0;i<newEventi.length;i++){
					newRelazioni[i]=newEventi[i].getFonte();
				}
			}
			

			// SETTA SUL DOM:
			ciclodivita.setEventi( newEventi);
   		    ciclodivita.setRelazioni( newRelazioni);
   		    
   		    if (ciclodivitaForm.getVigToUpdate()!=null && ciclodivitaForm.getVigToUpdate().length>0) {
   		    	VigenzaEntity[] elenco =ciclodivitaForm.getVigToUpdate();
   		    	for(int i=0; i<elenco.length;i++)
   		    		vigenza.updateVigenzaOnDoc(elenco[i]);
   		    }
		}
		
	}


	/////////////////////////////////////////
	//////////////////////////////  CNR 
	
	public void doCnr() {
		Document doc = documentManager.getDocumentAsDom();
		Node node = selectionManager.getActiveNode();
		cnrForm.setProprietari(metaCnr.getProprietario(node));
		
		
		if (cnrForm.openForm()) {
			try {
				EditTransaction tr = documentManager.beginEdit();
				metaCnr.setProprietario(node, cnrForm.getProprietari());
				rinumerazione.aggiorna(doc);
				documentManager.commitEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
		
	}
	

	/////////////////////////////////////////
	//////////////////////////////  URN 
	
	public void doUrn() {
		Document doc = documentManager.getDocumentAsDom();
		Urn[] set = metaUrnDocumento.getUrnFromDocument(doc);

		if (urnDocumentoForm.openForm(set)) {
			Urn[] getUrn = urnDocumentoForm.getUrn();
			metaUrnDocumento.setUrnOnDocument(doc, getUrn, true, true); 
			// true: aggiorna il documento
			// solo se richiesto (set false per documenti preesistenti)
		}
	}

	
	// ///////////////////////////////////////////////// Azioni
	public class DescrittoriAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doDescrittori();
		}
	}
	
	
	public class CiclodiVitaAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doCiclodiVita();
		}
	}
	
	public class InquadramentoAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doInquadramento();
		}
	}
	
	public class MaterieAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doMaterie();
		}
	}

	public class urnAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doUrn();
		}
	}
	
	public class cnrAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doCnr();
		}
	}

	
	
	
	

	

}

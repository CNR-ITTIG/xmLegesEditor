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
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.meta.MetaAction;
import it.cnr.ittig.xmleges.editor.services.autorita.Autorita;
import it.cnr.ittig.xmleges.editor.services.autorita.Istituzione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.MetaCiclodivita;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.cnr.MetaCnr;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.MetaDescrittori;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.MetaDescrittoriMaterie;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Vocabolario;
import it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento.MetaInquadramento;
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
import it.cnr.ittig.xmleges.editor.services.provvedimenti.Provvedimenti;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.ProvvedimentiItem;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.TemplateItem;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractAction;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

	Provvedimenti provvedimenti;

	Autorita regAutorita;

	DtdRulesManager dtdRulesManager;
	
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
		descrittoriForm = (MetaDescrittoriForm) serviceManager.lookup(MetaDescrittoriForm.class);
		ciclodivitaForm = (CiclodiVitaForm) serviceManager.lookup(CiclodiVitaForm.class);
		inquadramentoForm = (InquadramentoForm) serviceManager.lookup(InquadramentoForm.class);
		materieForm = (MaterieVocabolariForm) serviceManager.lookup(MaterieVocabolariForm.class);
		newrinvii = (NewRinviiForm) serviceManager.lookup(NewRinviiForm.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		provvedimenti = (Provvedimenti) serviceManager.lookup(Provvedimenti.class);
		regAutorita = (Autorita) serviceManager.lookup(Autorita.class);
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
			
		materieAction.setEnabled(true);
		urnAction.setEnabled(!documentManager.isEmpty());		
		cnrAction.setEnabled(!documentManager.isEmpty() && documentManager.getDtdName().equals("cnr.dtd"));
	}

	// //////////////////////////////////////////// MetaGeneraliAction Interface
	public void doDescrittori() {
		logger.debug("Metadati Descrittori");
		
		Node node = selectionManager.getActiveNode();
		Document doc = documentManager.getDocumentAsDom();
		//TODO: da cambiare non è piu tipodoc ma tipopubblicazione con default GU
//		descrittoriForm.setTipoPubblicazione(UtilDom.getAttributeValueAsString(doc.getDocumentElement(), "tipo"));
//		descrittoriForm.setTipoPubblicazione(descrittori.getPubblicazione().getTipo());
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
				documentManager.commitEdit(tr);
				rinumerazione.aggiorna(doc);
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
				
				documentManager.commitEdit(tr);
				rinumerazione.aggiorna(doc);
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
		Relazione[] relazioniOnDom = ciclodivita.getRelazioni();
		
		ciclodivitaForm.setEventi(eventiOnDom);
		ciclodivitaForm.setRelazioniUlteriori(ciclodivita.getRelazioniUlteriori(eventiOnDom,relazioniOnDom));
		
		String[] id_eventiOnVigenze =ciclodivita.getEventiOnVigenza();
		VigenzaEntity[] vigenze = ciclodivita.getVigenze();
		ciclodivitaForm.setEventiOnVigenze(id_eventiOnVigenze, vigenze);
				
		if(ciclodivitaForm.openForm()){
			Evento[] newEventi = ciclodivitaForm.getEventi();
			Relazione[] relazioniUlteriori = ciclodivitaForm.getRelazioniUlteriori();
			
			Relazione[] newRelazioni = ciclodivita.mergeRelazioni(newEventi,relazioniUlteriori);

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
				documentManager.commitEdit(tr);
				rinumerazione.aggiorna(doc);
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
		
	}
	



	
	/////////////////////////////////////////
	//////////////////////////////  URN 
	
	public void doUrn() {
		Document doc = documentManager.getDocumentAsDom();
		Urn[] set = getUrnFromDocument(doc);

		// TODO portare fuori un po'di funzioni piu' generiche; aggiustare la
		// funzione
		// removeEmananteTemplate()

		if (urnDocumentoForm.openForm(set)) {
			Urn[] getUrn = urnDocumentoForm.getUrn();
			setUrnOnDocument(doc, getUrn, true, true); // true: aggiorna il
			// documento
			// solo se richiesto (documenti
			// preesistenti)
		}
	}

	private boolean setUrnOnDocument(Document doc, Urn[] urn, boolean updateIntestazione, boolean updateEmananti) {

		EditTransaction tr = null;

		//Node activeMeta = nirUtilDom.findActiveMeta(doc,selectionManager.getActiveNode());
		//Node descrittori = UtilDom.findRecursiveChild(activeMeta,"descrittori");
		
		
		// TODO sulle urn lasciare ancora che si riferisca al documento principale
		
		Node descrittori = doc.getElementsByTagName("descrittori").item(0);
		
		// TODO chiedere qualcosa al rulesManager ?
		if (descrittori != null) {
			try {
				tr = documentManager.beginEdit();
				Node urnNode = UtilDom.checkAndCreate(descrittori, "urn");
				UtilDom.setAttributeValue(urnNode,"value", urn[0].toString());

				Node[] otherUrn = UtilDom.getAllNextSibling(urnNode);
				for (int i = 0; i < otherUrn.length; i++) {
					if (otherUrn[i].getNodeName().equals("urn"))
						descrittori.removeChild(otherUrn[i]);
				}

				Node next = urnNode;
				for (int i = 1; i < urn.length; i++) {
					urnNode = doc.createElement("urn");
					UtilDom.setAttributeValue(urnNode,"value", urn[i].toString());
					UtilDom.insertAfter(urnNode, next);
					next = urnNode;
				}

				Node intestazione = null;
				Node atto = nirUtilDom.getTipoAtto(doc);
				if (atto != null)
					intestazione = UtilDom.checkAndCreate(atto, "intestazione");

				// //////////////// AGGIORNAMENTO DELL'INTESTAZIONE
				// ////////////////

				if (updateIntestazione) {
					String dataDoc = null;
					String extendedDataDoc = null;
					String numDoc = null;
					if (null != urn[0].getDate() && urn[0].getDate().size() > 0) {
						dataDoc = UtilDate.urnFormatToNorm((String) urn[0].getDate().get(0));
						extendedDataDoc = (String) urn[0].getExtendedDate().get(0);
					}

					if (null != urn[0].getNumeri() && urn[0].getNumeri().size() > 0)
						numDoc = (String) urn[0].getNumeri().get(0);

					if (intestazione != null) {
						Node numDocNode = UtilDom.checkAndCreate(intestazione, "numDoc");
						Node dataDocNode = UtilDom.checkAndCreate(intestazione, "dataDoc");
						if (dataDoc != null && extendedDataDoc != null) {
							UtilDom.setAttributeValue(dataDocNode, "norm", dataDoc);
							UtilDom.setTextNode(dataDocNode, extendedDataDoc);
						}
						if (numDoc != null)
							UtilDom.setTextNode(numDocNode, numDoc);
					}
				}

				// /////////////// AGGIORNAMENTO DEGLI EMANANTI ////////////////

				if (updateEmananti) {
					boolean needsEmanante = false;
					ProvvedimentiItem prov = findProvvedimento(doc);
					if (prov != null)
						needsEmanante = prov.getEmananti();

					if (needsEmanante) {
						Node newEmanante = null;
						Node emanante = UtilDom.findDirectChild(intestazione, "emanante");
						if (emanante == null) { // non ci sono emananti
							emanante = UtilDom.checkAndCreate(intestazione, "emanante");
						} else { // ci sono gia' degli emananti
							NodeList emanantiList = doc.getElementsByTagName("emanante");
							emanante = emanantiList.item(emanantiList.getLength() - 1);
							newEmanante = doc.createElement("emanante");
							UtilDom.insertAfter(newEmanante, emanante);
							emanante = newEmanante;
						}

						for (int i = 0; i < urn.length; i++) {
							Vector auto = urn[i].getAutorita();
							for (int j = 0; j < auto.size(); j++) {
								// inserirlo solo se non e' presente
								String newAutorita = regAutorita.getNomeIstituzioneFromUrnIstituzione((String) auto.get(j));
								if (!findEmanante(doc, newAutorita)) {
									UtilDom.setTextNode(emanante, newAutorita);
									newEmanante = doc.createElement("emanante");
									UtilDom.insertAfter(newEmanante, emanante);
									emanante = newEmanante;
								}
							}
						}
						intestazione.removeChild(emanante);
						removeEmananteTemplate(intestazione);
					}
				}

				documentManager.commitEdit(tr);
				return true;
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				documentManager.rollbackEdit(tr);
				return false;
			}
		}
		return false;
	}

	private void removeEmananteTemplate(Node emananteParent) {
		NodeList children = emananteParent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals("emanante") && UtilDom.getTextNode(children.item(i)).indexOf("[") != -1) {
				try {
					if (dtdRulesManager.queryCanDelete(emananteParent, children.item(i)))
						emananteParent.removeChild(children.item(i));
				} catch (DtdRulesManagerException e) {
				}
			}
		}
	}

	private boolean findEmanante(Document doc, String autorita) {
		if (doc != null) {
			NodeList emanantiList = doc.getElementsByTagName("emanante");
			for (int i = 0; i < emanantiList.getLength(); i++) {
				if (null != UtilDom.getTextNode(emanantiList.item(i)) && UtilDom.getTextNode(emanantiList.item(i)).equalsIgnoreCase(autorita))
					return true;
			}
		}
		return false;
	}

	/**
	 * Restituisce le urn presenti nel documento
	 * 
	 * @param doc
	 * @return urn del documento
	 */
	private Urn[] getUrnFromDocument(Document doc) {

		Vector v = new Vector();
		Urn urnDoc, urnFromDoc;

		urnFromDoc = createUrnFromDocument(doc);

		NodeList urn = doc.getElementsByTagName("urn");
		for (int i = 0; i < urn.getLength(); i++) {
			try {
				if (UtilDom.findParentByName(urn.item(i), "annessi") == null) {

					urnDoc = new Urn(UtilDom.getAttributeValueAsString(urn.item(i),"value"));

					if (null != urnDoc.getDate() && urnDoc.getDate().size() > 0 && ((String) urnDoc.getDate().get(0)).startsWith("aaaa"))
						if (urnFromDoc.getDate() != null)
							urnDoc.setUniqueData((String) urnFromDoc.getDate().get(0));

					if (null != urnDoc.getNumeri() && urnDoc.getNumeri().size() > 0 && ((String) urnDoc.getNumeri().get(0)).startsWith("nn"))
						if (urnFromDoc.getNumeri() != null)
							urnDoc.setUniqueNumero((String) urnFromDoc.getNumeri().get(0));

					if (urnDoc.isValid())
						v.add(urnDoc);
				}
			} catch (ParseException e) {
				logger.error("urn parse error in getUrnFromDocument");
			}
		}
		if (v.size() == 0) // se non trova nessuna urn valida nel documento
			// cerca di
			// crearla sulla base degli altri dati
			v.add(urnFromDoc);
		Urn[] ret = new Urn[v.size()];
		v.copyInto(ret);
		return ret;
	}

	private ProvvedimentiItem findProvvedimento(Document doc) {

		String tipoDoc = UtilDom.getTextNode(doc.getElementsByTagName("tipoDoc").item(0));
		String tag = nirUtilDom.getTipoAtto(doc).getNodeName();

		ProvvedimentiItem prov = null;
		if (tipoDoc != null && tipoDoc.trim().length() > 0) {
			prov = provvedimenti.getProvvedimentoByName(tipoDoc);
			if (prov == null)
				prov = provvedimenti.getProvvedimentoByUrn(tipoDoc);
		}

		if (prov == null && provvedimenti.getProvvedimentiByTag(tag).length > 0)
			prov = provvedimenti.getProvvedimentiByTag(tag)[0];

		return prov;
	}

	/**
	 * Restituisce la urn del documento costruita in base alle informazioni
	 * recuperabili dal documento: tipo, numero, data
	 * 
	 * @param doc
	 * @return urn del documento
	 */
	private Urn createUrnFromDocument(Document doc) {

		String dataDoc = doc.getElementsByTagName("dataDoc").getLength() > 0 ? UtilDom.getAttributeValueAsString(doc.getElementsByTagName("dataDoc").item(0),
				"norm").trim() : null;
		String numDoc = doc.getElementsByTagName("numDoc").getLength() > 0 ? UtilDom.getTextNode(doc.getElementsByTagName("numDoc").item(0)) : null;
		String tag = nirUtilDom.getTipoAtto(doc).getNodeName();

		String data;
		String provvedimento = " ";
		String autorita = " ";
		String numero = " ";

		Urn ret = null;

		// //////////////////// RECUPERO DATA
		if (dataDoc != null && dataDoc.trim().length() > 0) {
			data = dataDoc;
		} else
			data = UtilDate.dateToNorm(UtilDate.getCurrentDate());
        
		
		ProvvedimentiItem prov = findProvvedimento(doc);

		if (prov != null) {
			provvedimento = prov.getUrnAtto()!=null?prov.getUrnAtto():provvedimento;
			Istituzione[] ist = regAutorita.getIstituzioniValideFromProvvedimenti(data, prov.getUrnAutorita());
			if (ist!=null && ist.length == 1) // una sola possibile autorita emenante
				autorita = ist[0].getUrn();
		}

		// ////////////////////RECUPERO NUMDoc
		if (numDoc != null && numDoc.trim().length() > 0) {
			numero = numDoc;
		} else {
			if (isRequiredNumero(tag))
				numero = "da assegnare";
			else
				numero = "nir-da assegnare";
		}

		try {
			ret = new Urn("urn:nir:" + autorita + ":" + provvedimento + ":" + UtilDate.normToUrnFormat(data) + ";" + numero);
		} catch (ParseException e) {
			return null;
		}
		return ret;

	}

	private boolean isRequiredNumero(String tag) {

		ProvvedimentiItem[] prList = provvedimenti.getProvvedimentiByTag(tag);
		boolean ret = false;
		if (prList.length > 0) {
			TemplateItem[] tempList = prList[0].getTemplateList();
			for (int i = 0; i < tempList.length; i++) {
				if (tempList[i].isNumerato())
					ret = true;
			}
		}
		return ret;
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

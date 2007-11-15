package it.cnr.ittig.xmleges.editor.blocks.dom.meta.descrittori;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.MetaDescrittoriMaterie;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.ipiu.digest.parse.Archivio;
import it.ipiu.digest.parse.ParseXmlToVocabolario;
import it.ipiu.digest.parse.Vocabolario;
import it.jaime.configuration.ConfigLoadedException;
import it.jaime.configuration.ConfigurationFacade;
import it.jaime.configuration.MalFormedParameterExpression;
import it.jaime.utilities.file.FileUtility;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MetaDescrittoriMaterieImpl implements MetaDescrittoriMaterie,
		Loggable, Serviceable {
	Logger logger;

	DocumentManager documentManager;

	DtdRulesManager dtdRulesManager;

	UtilRulesManager utilRulesManager;

	Rinumerazione rinumerazione;

	NirUtilDom nirUtilDom;


	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);

	}

	//IPIU-TODO comment
	public void setVocabolari(Node node, Vocabolario[] vocabolari) {

		Document doc = documentManager.getDocumentAsDom();
		try {
			EditTransaction tr = documentManager.beginEdit();
			Node activeMeta = nirUtilDom.findActiveMeta(doc, node);
			Node descrittoriNode = UtilDom.findRecursiveChild(activeMeta,
					"descrittori");

			removeMetaByName("materie", node);

			for (int i = 0; i < vocabolari.length; i++) {

				Node vocabTag;
				vocabTag = utilRulesManager.getNodeTemplate("materie");

				UtilDom.setAttributeValue(vocabTag, "vocabolario",
						vocabolari[i].getNome());
				String[] materieVocab = vocabolari[i].getMaterie();
				if (materieVocab != null && materieVocab.length > 0) {
					vocabTag.removeChild(vocabTag.getChildNodes().item(0));
					for (int j = 0; j < materieVocab.length; j++) {
						Element materiaTag;
						materiaTag = doc.createElement("materia");
						UtilDom.setAttributeValue(materiaTag, "valore",
								vocabolari[i].getMaterie()[j]);
						utilRulesManager.orderedInsertChild(vocabTag,
								materiaTag);

					}
				} else {
					UtilDom.setAttributeValue(vocabTag.getChildNodes().item(0),
							"valore", null);
				}
				utilRulesManager.orderedInsertChild(descrittoriNode, vocabTag);
			}
			documentManager.commitEdit(tr);
			rinumerazione.aggiorna(doc);
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return;
		}
	}

	public Vocabolario[] getVocabolari(Node node) {

		/* Modifica I+ */
		Vocabolario[] vocabolariOnDoc = this.getVocabolario();

		/* Codice ITTIG */

		Document doc = documentManager.getDocumentAsDom();
		Node activeMeta = nirUtilDom.findActiveMeta(doc, node);

		Node[] vocabolariList = UtilDom.getElementsByTagName(doc, activeMeta,
				"materie");

		Vocabolario[] vocabolariOnDoctmp = new Vocabolario[vocabolariList.length];
		for (int i = 0; i < vocabolariList.length; i++) {
			vocabolariOnDoctmp[i] = new Vocabolario();
			String nomeVocabolario = vocabolariList[i].getAttributes()
					.getNamedItem("vocabolario").getNodeValue();
			if (nomeVocabolario.equals(""))
				nomeVocabolario = "-- name absent --";
			vocabolariOnDoctmp[i].setNome(nomeVocabolario);
			NodeList materieList = vocabolariList[i].getChildNodes();
			boolean isEmpty = (materieList.getLength() == 1 && (materieList
					.item(0).getAttributes().getNamedItem("valore") == null || materieList
					.item(0).getAttributes().getNamedItem("valore")
					.getNodeValue().equals("")));
			if (materieList != null && !isEmpty) {
				String[] materieVocabolario = new String[materieList
						.getLength()];
				for (int j = 0; j < materieList.getLength(); j++) {
					materieVocabolario[j] = materieList.item(j).getAttributes()
							.getNamedItem("valore").getNodeValue();
				}
				vocabolariOnDoctmp[i].setMaterie(materieVocabolario);
			}
		}

		/**
		 * Effettuando il merge tra due array di Vcabolari ottengo un unico array.
		 */
		Vocabolario[] vocabolarios = Archivio.merge(vocabolariOnDoc, vocabolariOnDoctmp);

		return vocabolarios;
	}

	

	/**
	 * Il metodo costruisce un array di Vocabolari e i relativi insieme di
	 * materie
	 * 
	 * @return <code>Vocabolario[]</code>
	 */
	private Vocabolario[] getVocabolario() {

		try {
			//IPIU-TODO configuration
			String filename = ConfigurationFacade.get("config.properties/archivio.path");
			String path = FileUtility.getInstance().getPath(filename);
			
			Archivio archivio = ParseXmlToVocabolario.parse(path);
			
			return archivio.getVocabolari();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ConfigLoadedException e) {
			e.printStackTrace();
		} catch (MalFormedParameterExpression e) {
			e.printStackTrace();
		}
		return new Vocabolario[0];
	}

	/**
	 * Rimuove i tag con un determinato nome
	 */
	private void removeMetaByName(String nome, Node node) {
		Document doc = documentManager.getDocumentAsDom();
		Node toRemove;

		Node activeMeta = nirUtilDom.findActiveMeta(doc, node);

		do {
			toRemove = UtilDom.findRecursiveChild(activeMeta, nome);
			if (toRemove != null) {
				toRemove.getParentNode().removeChild(toRemove);
			}
		} while (toRemove != null);
	}
}
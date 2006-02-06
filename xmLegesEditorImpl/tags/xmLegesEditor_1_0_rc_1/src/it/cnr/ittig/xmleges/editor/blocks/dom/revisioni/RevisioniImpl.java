package it.cnr.ittig.xmleges.editor.blocks.dom.revisioni;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.revisioni.Revisioni;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.revisioni.Revisioni</code>.</h1>
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class RevisioniImpl implements Revisioni, Loggable, Serviceable {
	Logger logger;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;

	NirUtilDom nirUtilDom;

	NirUtilUrn nirUtilUrn;

	UtilRulesManager utilRulesManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
	}

	// restituisce: null se non si puo' settare la modifca;
	// altrimenti restituisce lo status (inserito|identico|soppresso)

	public String canSetModifica(Node node, String status, int start, int end) {

		if (documentManager.isEmpty() || !utilRulesManager.isDtdDL())
			return null;
		if (node == null)
			return null;

		Node statusNode = null;

		if (getStatusSpanContainer(node) != null) // sono dentro uno span con
			// status
			statusNode = getStatusSpanContainer(node);
		else if (!isParentModified(getContainer(node))) { // non sono sotto un
			// contenitore con
			// status modificato
			statusNode = getContainer(node);
			// FIXME prevenire l'accensione su un nodo testo vuoto
			if (UtilDom.isTextNode(node) && start == end && status.equals(Revisioni.SOPPRESSO)) // impedisce
				// l'inserimento di
				// un tag soppresso
				// vuoto
				return null;
		}

		if (statusNode != null) {
			String statoAttuale = UtilDom.getAttributeValueAsString(statusNode, "status");
			if (statoAttuale != null) {
				if (statoAttuale.equals(status))
					return (Revisioni.IDENTICO);
				if (statoAttuale.equals(Revisioni.IDENTICO))
					return status;
				return null;
			}
			return null;
		}
		return null;
	}

	public boolean canTestoaFronte() {
		if (!documentManager.isEmpty() && utilRulesManager.isDtdDL())
			if (isDocumentModificato())
				return true;
		return false;
	}

	public boolean canPassaggio() {
		if (!documentManager.isEmpty() && utilRulesManager.isDtdDL())
			return true;
		return false;
	}

	public boolean canEmendamenti() {
		return canTestoaFronte() && isModified(); // &&
		// metodoModificheGestite()
	}

	public Node setModifica(Node node, int start, int end, String status) {

		if (!UtilDom.isTextNode(node)) { // sono su un nodo contenitore
			try {
				EditTransaction tr = documentManager.beginEdit();
				applyStatus(getContainer(node), status);
				setAttributeModificato();
				documentManager.commitEdit(tr);
				return node;
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
				return null;
			}
		} else if (getStatusSpanContainer(node) != null) { // sono dentro uno
			// span
			try {
				EditTransaction tr = documentManager.beginEdit();
				Node spanNode = getStatusSpanContainer(node);
				Node modified = null;
				if (UtilDom.getAttributeValueAsString(spanNode, "status") != null
						&& UtilDom.getAttributeValueAsString(spanNode, "status").equals(Revisioni.SOPPRESSO))
					modified = removeContainer(spanNode, false);
				else
					modified = removeContainer(spanNode, true);
				setAttributeModificato();
				documentManager.commitEdit(tr);
				return (modified);
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
				return null;
			}
		} else { // sono su un testo fuori span
			try {
				EditTransaction tr = documentManager.beginEdit();
				Element span = (Element) utilRulesManager.encloseTextInTag(node, start, end, "h:span", "h");
				// Assegnazione attributo status allo span creato
				if (span != null) {
					span.setAttribute("status", status);
					setAttributeModificato();
					documentManager.commitEdit(tr);
					return span;
				}
				documentManager.rollbackEdit(tr);
				return null;
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
				return null;
			}
		}
	}

	private boolean isDocumentModificato() {
		Node atto = nirUtilDom.getTipoAtto(documentManager.getDocumentAsDom());
		if (UtilDom.getAttributeValueAsString(atto, "tipo").equalsIgnoreCase("modificato"))
			return true;
		return false;
	}

	private void setAttributeModificato() {
		Node atto = nirUtilDom.getTipoAtto(documentManager.getDocumentAsDom());
		if (UtilDom.getAttributeValueAsString(atto, "tipo").equalsIgnoreCase("originale"))
			UtilDom.setAttributeValue(atto, "tipo", "modificato");
	}

	// rimuove il contenitore (h:span); se removeContained = true allora rimuove
	// anche il contenuto
	private Node removeContainer(Node container, boolean removeContained) {
		Node parent = container.getParentNode();
		if (parent != null) {
			try {
				if (removeContained) {
					EditTransaction tr = documentManager.beginEdit();
					parent.removeChild(container);
					UtilDom.mergeTextNodes(parent);
					documentManager.commitEdit(tr);
					return parent;
				} else {
					EditTransaction tr = documentManager.beginEdit();
					NodeList nl = container.getChildNodes();
					for (int i = 0; i < nl.getLength(); i++) {
						parent.insertBefore(nl.item(i), container);
					}
					parent.removeChild(container);
					UtilDom.mergeTextNodes(parent, " ");
					documentManager.commitEdit(tr);
					return parent;
				}
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
				return null;
			}
		}
		return null;
	}

	private void applyStatus(Node node, String status) {

		if (nirUtilDom.isContainer(node)) // applica anche a tutti gli span
			// contenuti lo stesso status
			UtilDom.setAttributeValue(node, "status", status);
		if (isStatusSpan(node)) {
			if (UtilDom.getAttributeValueAsString(node, "status") != null && UtilDom.getAttributeValueAsString(node, "status").equals(status))
				removeContainer(node, false);
			else
				removeContainer(node, true);
		}

		NodeList nl = node.getChildNodes();
		Vector lista = new Vector(nl.getLength());
		for (int i = 0; i < nl.getLength(); i++) {
			lista.add(i, nl.item(i));
		}
		for (int i = 0; i < lista.size(); i++)
			applyStatus((Node) lista.get(i), status);
	}

	private boolean isParentModified(Node node) {
		Node container = getParentContainer(node);
		// logger.debug("isParentModified container
		// "+container!=null?container.getNodeName():null);
		while (container != null && !container.getNodeName().equalsIgnoreCase("articolato")) {
			if (null != UtilDom.getAttributeValueAsString(container, "status")
					&& !UtilDom.getAttributeValueAsString(container, "status").equals(Revisioni.IDENTICO))
				return true;
			container = getParentContainer(container);
		}
		return false;
	}

	private Node getContainer(Node node) {
		if (node != null) {
			Node container = node;
			while (container != null && !nirUtilDom.isContainer(container))
				container = container.getParentNode();
			return container;
		}
		return (null);
	}

	private Node getStatusSpanContainer(Node node) {
		if (node != null) {
			Node container = node;
			logger.debug("getStatusSpanContainer: " + container.getNodeName());
			while (container != null && !isStatusSpan(container)) {
				container = container.getParentNode();
			}
			return container;
		}
		return (null);
	}

	private boolean isStatusSpan(Node node) {
		return (node != null && (node.getNodeName().toLowerCase().indexOf("span") != -1) && null != UtilDom.getAttributeValueAsString(node, "status"));
	}

	// FIXME fuori dall'articolato non funziona bene perche' non trova i
	// container non in articolato. usare rulesManager ?

	private Node getParentContainer(Node node) {
		if (node != null) {
			Node container = node.getParentNode();
			while (container != null && !nirUtilDom.isContainer(container))
				container = container.getParentNode();
			return container;
		}
		return (null);
	}

	public Node getFinalVersion(Node node) {

		if (nirUtilDom.isContainer(node) && UtilDom.getAttributeValueAsString(node, "status") != null
				&& UtilDom.getAttributeValueAsString(node, "status").equals(Revisioni.INSERITO))
			UtilDom.setAttributeValue(node, "status", Revisioni.IDENTICO);
		else if (nirUtilDom.isContainer(node) && UtilDom.getAttributeValueAsString(node, "status") != null
				&& UtilDom.getAttributeValueAsString(node, "status").equals(Revisioni.SOPPRESSO)) {
			try {
				if (null != node.getParentNode() && dtdRulesManager.queryCanDelete(node.getParentNode(), node))
					node.getParentNode().removeChild(node);
			} catch (DtdRulesManagerException ex) {
				return null;
			}
		} else if (isStatusSpan(node)) {
			if (UtilDom.getAttributeValueAsString(node, "status") != null && UtilDom.getAttributeValueAsString(node, "status").equals(Revisioni.INSERITO))
				removeContainer(node, false);
			else if (UtilDom.getAttributeValueAsString(node, "status") != null && UtilDom.getAttributeValueAsString(node, "status").equals(Revisioni.SOPPRESSO))
				removeContainer(node, true);
		}

		NodeList nl = node.getChildNodes();
		Vector lista = new Vector(nl.getLength());
		for (int i = 0; i < nl.getLength(); i++) {
			lista.add(i, nl.item(i));
		}
		for (int i = 0; i < lista.size(); i++)
			getFinalVersion((Node) lista.get(i));
		return node;
	}

	public Node[] getEmendamenti(Document oldDoc, Document newDoc) {
		Vector output = new Vector();
		Node[] ret = null;
		String status = "", rif = "";
		String soppresso = "", inserito = "";
		int i = 0, tipopart = 0; // 1: comma 2: articolo 3: capo
		int sost = 0; // caso parole, sostituzione o inserimento?
		// sost=1 indica che si deve saltare il prossimo h:span status=inserito
		// perche' c'? stata
		// una sostituzione; nel caso di partizioni si usa il riferimento
		// restituito da getNextPart();

		Node ntmp = null;
		NodeList nlist = null; // , clist = null;
		Node node = null, tmpNode = null, parent = null, prev = null, next = null; // ,
		// child
		// =
		// null;
		Node part_salta = null;

		nlist = oldDoc.getElementsByTagName("*"); // Lista di tutti i nodi

		for (i = 0; i < nlist.getLength(); i++) {
			node = (Node) nlist.item(i);
			// In caso di partizione soppressa, salta fino alla prossima
			// partizione dello stesso tipo
			if (part_salta != null && part_salta != node)
				continue;
			part_salta = null;
			// Analizza solo nodi di tipo Element:
			if (node.getNodeType() != 1)
				continue;
			// Analizza solo nodi in cui ? presente l'attributo 'status':
			if (((Element) node).hasAttribute("status") == false)
				continue;
			status = ((Element) node).getAttribute("status"); // Valore
			// dell'attributo
			// status
			// Non analizzare tag con status 'identico'
			if (status.equals("identico"))
				continue;
			parent = node.getParentNode();

			// Parole o partizioni ?
			if (node.getNodeName().equals("h:span")) { // Caso 'PAROLE'
				// Si cercano soltanto parole contenute nel tag <corpo>:
				if (parent.getNodeName().equals("corpo") == false) {
					logger.debug("Warning! h:span tag non figlio di <corpo> (" + parent.getNodeName() + ").");
					continue;
				}
				tipopart = 0;
				// Trova il riferimento articolo/comma/lettera...:
				while (parent != null) {
					// partizione qualsiasi, basta che abbia ID !
					if (parent.getNodeType() == 1 && ((Element) parent).hasAttribute("id")) {
						rif = ((Element) parent).getAttribute("id");
						break;
					}
					parent = parent.getParentNode();
				}
				prev = node.getPreviousSibling();
				next = node.getNextSibling();
				inserito = "";
				soppresso = "";

				if (status.equals("soppresso")) {
					soppresso = UtilDom.getText(node);

					// Controlla se segue uno span di tipo inserito
					tmpNode = getSost(node);
					if (tmpNode != null) { // 'sostituzione'
						// prendi il testo contenuto nel tag <h:span> :
						inserito = UtilDom.getText(tmpNode);

						ntmp = writeMod(0, 2, rif, 0, soppresso, inserito, "", "", newDoc, null);
						if (ntmp != null)
							output.add(ntmp);
						sost = 1;
					} else { // 'soppressione'
						ntmp = writeMod(0, 0, rif, 0, soppresso, inserito, "", "", newDoc, null);
						if (ntmp != null)
							output.add(ntmp);
					}
				}
				if (status.equals("inserito"))
					if (sost == 1) // salta per una volta l'inserimento in caso
						// di sostituzione
						sost = 0;
					else {
						// prendi il testo contenuto nel tag <h:span> :
						inserito = UtilDom.getText(node);

						// Si deve distinguere inserimento all'inizio del comma,
						// alla fine o dopo le parole...
						// All'inizio...
						if (prev == null
								|| (prev.getNodeName().equals("#text") == false || (prev.getNodeName().equals("#text") == true && prev.getNodeValue().trim()
										.length() == 0))) {
							ntmp = writeMod(0, 1, rif, 0, soppresso, inserito, "", "", newDoc, null);
							if (ntmp != null)
								output.add(ntmp);
						} else if (next == null
								|| (next.getNodeName().equals("#text") == false || (next.getNodeName().equals("#text") == true && next.getNodeValue().trim()
										.length() == 0))) {
							// Alla fine...
							ntmp = writeMod(0, 1, rif, 2, soppresso, inserito, "", "", newDoc, null);
							if (ntmp != null)
								output.add(ntmp);
						} else {
							// Dopo le parole...
							String dopostr = prev.getNodeValue();
							if (dopostr.length() > 50)
								dopostr = dopostr.substring(dopostr.indexOf(' ', dopostr.length() - 50));
							ntmp = writeMod(0, 1, rif, 1, soppresso, inserito, dopostr, "", newDoc, null);
							if (ntmp != null)
								output.add(ntmp);
						}
					}
			} else { // Caso 'PARTIZIONI'
				String nodename = node.getNodeName();
				if (isPartGestita(nodename) == false)
					continue; // controlla se la partizione ? gestita
				if (nodename.equals("comma"))
					tipopart = 1;
				if (nodename.equals("articolo"))
					tipopart = 2;
				if (nodename.equals("capo"))
					tipopart = 3;
				if (nodename.equals("el"))
					tipopart = 4;

				if (((Element) node).hasAttribute("id") == false) {// TUTTE LE
					// PARTIZIONI
					// HANNO ID
					// (Anche
					// el!!)
					logger.debug("Warning!! Paragraph " + node.getNodeName() + " has no ID attribute!");
					continue;
				}
				rif = ((Element) node).getAttribute("id");

				if (status.equals("soppresso")) {
					tmpNode = getSost(node);

					if (tmpNode != null) { // 'sostituzione'
						inserito = getInserito(tmpNode, tipopart);
						ntmp = writeMod(tipopart, 2, rif, 0, soppresso, inserito, "", ((Element) tmpNode).getAttribute("id"), newDoc, tmpNode);
						if (ntmp != null)
							output.add(ntmp);
						part_salta = getNextPart(tmpNode); // salta l'analisi
						// fino a questo
						// nodo

						if (part_salta == null) { // Documento finito (!?)
							// logger.debug("WARNING: part_salta = null!
							// (Documento Finito!?)");
							break;
						}
					} else { // 'soppressione'
						ntmp = writeMod(tipopart, 0, rif, 0, soppresso, inserito, "", "", newDoc, null);
						if (ntmp != null)
							output.add(ntmp);
						// Salta tutte le sottopartizioni della partizione
						// soppressa (sono soppresse!)
						part_salta = getNextPart(node); // salta l'analisi fino
						// a questo nodo
						if (part_salta == null) { // Documento finito (!?)
							// logger.debug("WARNING: part_salta = null!
							// (Documento Finito!?)");
							break;
						}
					}
				}
				if (status.equals("inserito")) { // 'inserimento'
					inserito = getInserito(node, tipopart);
					// In questo caso si deve distinguere inserimento 'in testa'
					// o 'in mezzo'
					String doporif = getPartPos(node);
					// logger.debug("doporif: "+doporif);
					if (doporif.equals("")) {
						ntmp = writeMod(tipopart, 1, rif, 0, soppresso, inserito, "", ((Element) parent).getAttribute("id"), newDoc, node);
						if (ntmp != null)
							output.add(ntmp);
					} else {
						ntmp = writeMod(tipopart, 1, rif, 1, soppresso, inserito, "", doporif, newDoc, node);
						if (ntmp != null)
							output.add(ntmp);
					}

					part_salta = getNextPart(node); // salta l'analisi fino a
					// questo nodo

					if (part_salta == null) { // Documento finito (!?)
						// logger.debug("WARNING: part_salta = null! (Documento
						// Finito!?)");
						break;
					}
				}
			}
		}

		if (output.size() > 0) {
			ret = new Node[output.size()];
			output.copyInto(ret);
		}
		return ret;
	}

	/*
	 * Prepara le stringhe che formeranno il testo dei commi relativi alle
	 * modifiche. t: 0 parole 1 comma 2 articolo 3 capo 4 lettera mod: 0
	 * soppresso 1 inserito 2 sostituito rif: (artXcomY) (riferimento alla
	 * partizione) pos: 0 (all'inizio...) 1 (dopo le parole...) 2 (alla fine...)
	 * sop: stringa soppressa ins: stringa inserita dopo: stringa "Dopo le
	 * parole -dopo-, ..." doporif: (artXcomY) (riferimento alla partizione che
	 * sostituisce, precedente o contenitrice)
	 */
	private Node writeMod(int t, int mod, String rif, int pos, String sop, String ins, String dopo, String doporif, Document newDoc, Node node) {
		String str1 = "", str2 = "", str3 = "", virg1 = "", virg2 = "";
		String part = "", cont = "", tipostr = "";
		String sopstr = "le parole", insstr = "le parole", dopostr = "le parole";
		String sopsopstr = " sono soppresse ", insinsstr = " sono inserite ";
		String soststr = " sono sostituite con ";
		String vA = "?", vC = "?"; // Simbolo di virgolette Aperte e Chiuse
		Node newNode = null;

		// logger.debug("t: " + t + " mod: " + mod + " rif: "+rif+" pos: "+pos+"
		// sop: "+sop+" ins: "+ins+" dopo: "+dopo+
		// " doporif: "+doporif + " rr: " +nirUtilUrn.getFormaTestualeById(rif)+
		// " dr: "+nirUtilUrn.getFormaTestualeById(doporif));

		rif = nirUtilUrn.getFormaTestualeById(rif);
		doporif = nirUtilUrn.getFormaTestualeById(doporif);
		sop = sop.trim();
		ins = ins.trim();
		if (sop.indexOf(" ") == -1) {
			sopstr = "la parola";
			sopsopstr = " ? soppressa ";
			soststr = " ? sostituita con ";
		}
		if (ins.indexOf(" ") == -1) {
			insstr = "la parola";
			insinsstr = " ? inserita ";
		}
		if (dopo.indexOf(" ") == -1)
			dopostr = "la parola";
		if (t == 0) { // caso PAROLE
			if (mod == 0) {// soppresso
				str1 = "Nell'" + rif + sopsopstr + sopstr + " " + vA;
				virg1 = sop;
				str2 = vC;
			}
			if (mod == 1) // inserito
				if (pos == 0) {
					str1 = "All'inizio dell'" + rif + insinsstr + insstr + " " + vA;
					virg1 = ins;
					str2 = vC;
				}
			if (pos == 1) {
				str1 = "Nell'" + rif + ", dopo " + dopostr + " " + vA;
				virg1 = dopo;
				str2 = vC + " " + insinsstr + insstr + " " + vA;
				virg2 = ins;
				str3 = vC;
			}
			if (pos == 2) {
				str1 = "Alla fine dell' " + rif + insinsstr + insstr + " " + vA;
				virg1 = ins;
				str2 = vC;
			}
			if (mod == 2) { // sostituito
				str1 = "Nell'" + rif + " " + sopstr + " " + vA;
				virg1 = sop;
				str2 = vC + " " + soststr + insstr + " " + vA;
				virg2 = ins;
				str3 = vC;
			}
			// Maiuscola all'inizio di ogni comma dell'emendamento
			String str1a = str1.trim().substring(0, 1);
			str1a = str1a.toUpperCase();
			str1 = str1a.concat(str1.trim().substring(1));
			// Aggiunge il punto alla fine del comma
			if (virg1.equals(""))
				str1 = str1.concat(".");
			else if (virg2.equals(""))
				str2 = str2.concat(".");
			else
				str3 = str3.concat(".");
			// Crea il nodo
			newNode = makeModParole(newDoc, str1, virg1, str2, virg2, str3);
		} else { // caso PARTIZIONI

			// Nel caso di partizioni, usare <virgolette tipo="struttura"> e
			// appendere come figlio di
			// <virgolette> il nodo della nuova partizione (con tutti i suoi
			// figli, ecc...).
			if (t == 1 || t == 2 || t == 4)
				part = "l'"; // l'articolo 1 comma 10
			if (t == 1) {
				part = "l'";
				tipostr = "comma";
			}
			if (t == 2) {
				part = "l'";
				tipostr = "articolo";
			}
			if (t == 3) {
				part = "il"; // il capo 3
				tipostr = "capo";
			}
			if (t == 4) {
				part = "l'";
				tipostr = "lettera";
			}
			if (mod == 0) { // soppresso
				str1 = part + rif + " ? soppresso.";
				if (t == 4)
					str1 = part + rif + " ? soppressa.";
			}
			if (mod == 1) { // inserito
				if (pos == 0) {
					// Non sempre ? corretto mettere "all'inizio della
					// partizione" ecc...?!
					if (t == 1)
						cont = "dell'" + doporif;
					if (t == 2)
						cont = "del " + doporif;
					if (t == 3)
						cont = "dell'articolato";
					str1 = "All'inizio " + cont + " ? inserito il seguente "/*
																			 * +
																			 * tipostr
																			 */
							+ ": " + vA;
					if (t == 4)
						str1 = "Nell'" + doporif + " ? inserita la seguente lettera: " + vA;
				}
				if (pos == 1) {
					str1 = "Dopo " + part + doporif + " ? inserito il seguente "/*
																				 * +
																				 * tipostr
																				 */+ ": " + vA;
					if (t == 4)
						str1 = "Dopo " + part + doporif + " ? inserita la seguente lettera: " + vA;
				}
			}
			if (mod == 2) { // sostituito
				str1 = part + rif + " ? sostituito dal seguente "/* + tipostr */
						+ ": " + vA;
				if (t == 4)
					str1 = part + rif + " ? sostituita dalla seguente lettera: " + vA;
			}
			// Maiuscola all'inizio di ogni comma dell'emendamento
			String str1a = str1.trim().substring(0, 1);
			str1a = str1a.toUpperCase();
			str1 = str1a.concat(str1.trim().substring(1));
			newNode = makeModStruttura(newDoc, str1, node, tipostr);
		}
		return newNode;
	}

	/*
	 * CASO PAROLE (ovvero <virgolette tipo="parole"> ) Verifica se ? possibile
	 * costruire un nodo 'comma' che rappresenti la modifica nel nuovo
	 * documento. In caso affermativo restituisci il nodo comma (con 'mod' ed
	 * eventuali 'virgolette').
	 */
	private Node makeModParole(Document newDoc, String str1, String virg1, String str2, String virg2, String str3) {
		Node newComma = utilRulesManager.getNodeTemplate(newDoc, "comma"); // va
		// bene
		// anche
		// se
		// il
		// primo
		// ?
		// vuoto?
		Node newCorpo = UtilDom.findDirectChild(newComma, "corpo");
		Node newMod = utilRulesManager.getNodeTemplate(newDoc, "mod");
		Node newVirg = null;
		Node txt = null;
		// logger.debug("makeMod str1:"+str1+" virg1:"+virg1+" str2:"+str2+"
		// virg2:"+virg2+" str3:"+str3);
		try {
			if (dtdRulesManager.queryCanAppend(newCorpo, newMod))
				newCorpo.appendChild(newMod);
		} catch (DtdRulesManagerException ex) {
			System.out.println("Warning! canAppend Exception");
			return null;
		}
		UtilDom.setTextNode(newMod, str1);

		if (virg1.equals("") == false) {
			newVirg = utilRulesManager.getNodeTemplate(newDoc, "virgolette");
			try {
				if (dtdRulesManager.queryCanAppend(newMod, newVirg))
					newMod.appendChild(newVirg);
			} catch (DtdRulesManagerException ex) {
				System.out.println("Warning! canAppend Exception");
				return null;
			}
			UtilDom.setTextNode(newVirg, virg1);
			txt = newDoc.createTextNode(str2); // setTextNode si usa solo con
			// il primo figlio di testo
			newMod.appendChild(txt);
			if (virg2.equals("") == false) {
				newVirg = utilRulesManager.getNodeTemplate(newDoc, "virgolette");
				try {
					if (dtdRulesManager.queryCanAppend(newMod, newVirg))
						newMod.appendChild(newVirg);
				} catch (DtdRulesManagerException ex) {
					System.out.println("Warning! canAppend Exception");
					return null;
				}
				UtilDom.setTextNode(newVirg, virg2);
				txt = newDoc.createTextNode(str3);
				newMod.appendChild(txt);
			}
		}
		return newComma;
	}

	/*
	 * CASO PARTIZIONI (ovvero <virgolette tipo="struttura"> ) Verifica se ?
	 * possibile costruire un nodo 'comma' che rappresenti la modifica nel nuovo
	 * documento. In caso affermativo restituisci il nodo comma (con 'mod' ed
	 * eventuali 'virgolette' e i nodi/struttura sottostanti).
	 */
	private Node makeModStruttura(Document newDoc, String str1, Node nNode, String tipo) {
		Node newComma = utilRulesManager.getNodeTemplate(newDoc, "comma"); // va
		// bene
		// anche
		// se
		// il
		// primo
		// ?
		// vuoto?
		Node newCorpo = UtilDom.findDirectChild(newComma, "corpo");
		Node newMod = utilRulesManager.getNodeTemplate(newDoc, "mod");
		Node newVirg = null, txt = null, node = null;
		// Copia e importa il nodo sul nuovo documento per essere attaccato
		// sotto <virgolette>
		if (nNode != null) {
			node = newDoc.createElement(tipo);
			node = nNode.cloneNode(true);
			node = newDoc.importNode(nNode, true);
			node = getFinalVersion(node); // rimuove gli
			// status="inserito",ecc..., e gli
			// <h:span>...
		}
		try {
			if (dtdRulesManager.queryCanAppend(newCorpo, newMod))
				newCorpo.appendChild(newMod);
		} catch (DtdRulesManagerException ex) {
			System.out.println("Warning! canAppend Exception!");
			return null;
		}
		UtilDom.setTextNode(newMod, str1);

		if (node != null) {
			newVirg = utilRulesManager.getNodeTemplate(newDoc, "virgolette");
			((Element) newVirg).setAttribute("tipo", "struttura"); // attributo
			// tipo="struttura"
			// !!
			try {
				if (dtdRulesManager.queryCanAppend(newMod, newVirg))
					newMod.appendChild(newVirg);
				if (dtdRulesManager.queryCanAppend(newVirg, node))
					newVirg.appendChild(node);
				txt = newDoc.createTextNode("?.");
				if (dtdRulesManager.queryCanAppend(newMod, txt))
					newMod.appendChild(txt);
			} catch (DtdRulesManagerException ex) {
				System.out.println("Warning! canAppend Exception!!");
				return null;
			}
		}
		return newComma;
	}

	/*
	 * Se esiste, restituisce il nodo successivo con status='inserito' (dello
	 * stesso tipo).
	 */
	private Node getSost(Node node) {
		Node tmpNode = nextRealSibling(node); // salta un eventuale nodo di
		// testo vuoto
		String nodename = node.getNodeName();
		if (tmpNode != null && tmpNode.getNodeName().equals(nodename) && ((Element) tmpNode).getAttribute("status").equals("inserito"))
			return tmpNode;
		return null;
	}

	/*
	 * Se esiste, restituisce il riferimento alla partizione precedente dello
	 * stesso tipo nello stessa partizione contenitrice (il comma precedente nel
	 * medesimo articolo) (altrimenti stringa vuota).
	 */
	private String getPartPos(Node node) {
		Node prev = node.getPreviousSibling();
		String nodename = node.getNodeName();
		while (prev != null) {
			if (prev.getNodeName().equals(nodename))
				return ((Element) prev).getAttribute("id");
			prev = prev.getPreviousSibling();
		}
		return "";
	}

	/*
	 * Restituisce il nodo non di testo successivo Cerca sullo stesso livello
	 * gerarchico (nextSibling) Se non trova niente risali di un livello e
	 * analizza il nextSibling e cosi' via Se non trova niente non ci sono piu'
	 * partizioni da analizzare (doc.finito) (null).
	 */
	private Node getNextPart(Node node) {
		Node tmpNode = nextRealSibling(node); // salta un eventuale nodo di
		// testo vuoto (vero fratello)
		Node parent = node.getParentNode();
		if (tmpNode != null)
			return tmpNode; // vero fratello OK
		if (parent == null)
			return null; // parent null?

		while (tmpNode == null) {
			tmpNode = nextRealSibling(parent); // vero fratello del padre
			if (tmpNode != null)
				return tmpNode; // vero fratello del padre OK
			parent = parent.getParentNode();
			if (parent == null)
				return null; // parent null?
		}
		return null;
	}

	/*
	 * Restituisce true se la partizione modificata a cui si riferisce il nodo ?
	 * attualmente gestita (comma, articolo, capo, el...).
	 */
	private boolean isPartGestita(String nodename) {
		if ((nodename.equals("comma") == false) && (nodename.equals("articolo") == false) && (nodename.equals("capo") == false)
				&& (nodename.equals("el") == false)) {
			logger.debug("Warning!! Caso non gestito: " + nodename);
			return false;
		}
		return true;
	}

	/*
	 * Restituisce il testo relativo al tipo di partizione che rappresenta il
	 * nodo.
	 */
	private String getInserito(Node node, int tipopart) {
		String ins = "";
		// Nel caso di comma o lettera, mettere il testo di <corpo> nella
		// variabile 'inserito'
		if (tipopart == 1 || tipopart == 4) {
			Node tnode = UtilDom.findDirectChild(node, "corpo");
			ins = UtilDom.getText(tnode);
		}
		return ins;
	}

	/*
	 * Restituisce il next sibling escluso un eventuale nodo di testo vuoto
	 * (formato da " " /n /t ecc...)
	 */
	private Node nextRealSibling(Node node) {
		Node next = node.getNextSibling();
		if (next == null)
			return null;
		if (next.getNodeName().equals("#text") && next.getNodeValue().trim().length() == 0)
			return next.getNextSibling();
		return next;
	}

	/*
	 * True se sono state fatte delle modifiche al testo attualmente gestite da
	 * getEmendamenti()
	 */
	private boolean isModified() {

		Document doc = documentManager.getDocumentAsDom();
		Node node = null, parent = null;
		String status = "", nodename = "";

		NodeList nlist = doc.getElementsByTagName("*"); // Lista di tutti i nodi

		for (int i = 0; i < nlist.getLength(); i++) {
			node = (Node) nlist.item(i);
			// Analizza solo nodi di tipo Element:
			if (node.getNodeType() != 1)
				continue;
			// Analizza solo nodi in cui ? presente l'attributo 'status':
			if (((Element) node).hasAttribute("status") == false)
				continue;
			status = ((Element) node).getAttribute("status"); // Valore
			// dell'attributo
			// status
			// Non analizzare tag con status 'identico'
			if (status.equals("identico"))
				continue;

			// Parole
			if (node.getNodeName().equals("h:span")) {
				// Solo testo all'interno del tag 'corpo'
				parent = node.getParentNode();
				if (parent.getNodeName().equals("corpo") == false)
					continue;
				if (status.equals("soppresso") || status.equals("inserito"))
					return true;
			}

			// Partizioni
			nodename = node.getNodeName();
			if (isPartGestita(nodename) && (status.equals("soppresso") || status.equals("inserito")))
				return true;
		}
		return false;
	}
}

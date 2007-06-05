/*
 * Created on Mar 3, 2005
 */
package it.cnr.ittig.xmleges.editor.blocks.dom.rinumerazione;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;

import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Enrico Francesconi
 */

public class AggiornaNumerazioneAndLink extends AggiornaIdFrozenLaw {

	Logger logger;

	NirUtilUrn nirUtilUrn;

	NirUtilDom nirUtilDom;

	DtdRulesManager dtdRulesManager;

	// Vettore dei nodi xlink:href interni
	Vector InternalXlinkHref = new Vector(50, 10);

	// Vettore gli attributi che sono un IDRef
	Vector IDRefAttributes = new Vector(50, 10);

	public AggiornaNumerazioneAndLink(RinumerazioneImpl rinumerazioneImpl) {
		super(rinumerazioneImpl);
		this.logger = rinumerazioneImpl.getLogger();
		this.dtdRulesManager = rinumerazioneImpl.dtdRulesManager;
		this.nirUtilUrn = rinumerazioneImpl.nirUtilUrn;
		this.nirUtilDom = rinumerazioneImpl.nirUtilDom;
	}

	public void aggiornaNum(Node nodo) {

		doc = nodo.getOwnerDocument();
		NodeList nir = doc.getElementsByTagName("NIR");

		// aggiornamento relativo alle note:
		// le note vengono ordinate in base a come compaiono nel testo
		Vector ndrId = getNdrIdFromDoc();
		sortNotes(ndrId);

		// Aggiornamento ID e <num> degli elementi da rinumerare
		// Recupero i nodi contenenti XlinkHref a elementi interni
		InternalXlinkHref = new Vector(50, 10);
		getInternalXlinkHref(nir.item(0));
		for (int i = 0; i < InternalXlinkHref.size(); i++) {
			logger.debug("external call InternalXlinkHref: " + UtilDom.getAttributeValueAsString((Node) InternalXlinkHref.get(i), "xlink:href"));
		}

		// Recupero gli attributi che sono di tipo idref per poi aggiornarli
		// nel caso avessero puntato a nodi che hanno cambiato posizione
		// esempio idref: "fonte"; aggiungere gli altri
		getIDRefAttributes(nir.item(0));

		updateIDsByPosition(nir.item(0));
		updateNumByID();

		// Assegnazione degli ID alle partizioni di una Modifica
		// cio? dentro le virgolette
		NodeList virgoletteList = doc.getElementsByTagName("virgolette");
		for (int i = 0; i < virgoletteList.getLength(); i++) {
			updateIDs(virgoletteList.item(i).getFirstChild());
		}
		setNdrLink(ndrId, true);
	}

	/**
	 * Aggiorna gli ID sulla base delle posizioni
	 */
	private void updateIDsByPosition(Node nodo) {
		String IDValue; // Valore del nuovo ID

		if (nodo == null)
			return;

		/*
		 * Se un elemento e' dentro le virgolette di una modifica la sua
		 * numerazione non deve essere aggiornata
		 */
		if (isElementWithinModifica(nodo))
			return;
		if (isElementToBeIDUpdated(nodo)) {
			IDValue = getIDByPosition(nodo);
			if (isElementWithID(nodo)) {
				// IDValue = getIDByPosition(figlio);
				// Se esiste gi? un id in qualunque elemento
				// lo prendo come OldID
				String OldID = ((Element) nodo).getAttribute("id");
				// Controllo se esiste un riferimento interno che punta
				// all'elemento corrente (figlio)
				// Il controllo viene fatto solo se il nuovo ID (IDValue) !=
				// OldID
				// In questo caso si aggiorna il riferimento interno e la
				// formulazione linguistica
				// Lo stesso si fa per gli attributi di tipo IDRif
				if (!UtilDom.hasIdAttribute(nodo) || !IDValue.equals(OldID)) {
					updateIDRefAttributes(OldID, IDValue);
					logger.debug("idChanged: new  " + IDValue + " old " + OldID);
					UtilDom.setIdAttribute(nodo, IDValue);
					updateInternalXlinkHref(OldID, IDValue);
				}
			} else { // siamo comunque tra gli elementi di tipo
						// ElementToBeIdUpdated
				logger.debug("nuovo attributo: " + IDValue);
				UtilDom.setIdAttribute(nodo, IDValue);
				// va aggiornato anche qui: chi e' l'oldID ?
				// updateInternalXlinkHref(OldID, IDValue);
			}
		}// Fine if isElementToBeIDUpdated

		NodeList figliNodo = nodo.getChildNodes();
		for (int i = 0; i < figliNodo.getLength(); i++) {
			Node figlio = figliNodo.item(i);
			updateIDsByPosition(figlio);

		}// Fine del for
	}

	/**
	 * Aggiorna il contenuto del <num> sulla base dell'id
	 */
	private void updateNumByID() {
		String numVal; // Valore dell'elemento <num>

		NodeList numList = doc.getElementsByTagName("num");
		for (int i = 0; i < numList.getLength(); i++) {
			Node num = numList.item(i);
			if (!isElementWithinModifica(num)) {
				Node parentNum = num.getParentNode();
				// Suddivido l'ID nelle parti individuate da "-"
				// per poi prelevare il numero dell'ultima che rappresenta la
				// posizione
				// sulla base della quale calcolare il contenuto del <num>
				// (es:art1-com3 => <num>3.</num>)
				String partID[] = ((Element) parentNum).getAttribute("id").split("-");
				String lastPartID = partID[partID.length - 1];

				// Genero il valore del <num> in funzione
				// del tipo del nome del contenitore
				try{
					int val = Integer.parseInt(lastPartID.substring(3));
					numVal = getValoreNum(parentNum, val);
				}
				catch(NumberFormatException e){
					// e' il caso delle lettere che hanno id letterale
					numVal= lastPartID.substring(3)+")";
				}
				// Creo un nodo testo che diventera' il figlio di <num>
				// e sostituisco l'unico nodo figlo di <num> con quello creato
				// solo se num e' cambiato
				if (num.getChildNodes().item(0) != null) {
					if (!numVal.equals(num.getChildNodes().item(0).getNodeValue())) {
						logger.debug("CHANGED NUM: Old num: " + num.getChildNodes().item(0).getNodeValue() + "  New num: " + numVal);
						num.replaceChild(doc.createTextNode(numVal), num.getChildNodes().item(0));
					}
				} else
					num.appendChild(doc.createTextNode(numVal));
			}
		}
	}

	private void getInternalXlinkHref(Node node) {
		if (node == null)
			return;
		if (node.getNodeType() == Node.ELEMENT_NODE)
			if (node.getAttributes().getNamedItem("xlink:href") != null && node.getAttributes().getNamedItem("xlink:href").getNodeValue().trim().length() != 0)
				if (node.getAttributes().getNamedItem("xlink:href").getNodeValue().charAt(0) == '#')
					InternalXlinkHref.addElement(node);
		NodeList figli = node.getChildNodes();
		for (int i = 0; i < figli.getLength(); i++) {
			getInternalXlinkHref(figli.item(i));
		}
		return;
	}

	private void updateInternalXlinkHref(String OldID, String NewID) {
		logger.debug("updating Internal Xlink Href: oldID " + OldID + " newID " + NewID);
		logger.debug("IternalXlinHref: size " + InternalXlinkHref.size());

		// Vettore degli XlinkHref interni aggiornato
		// Dovranno essere tolti dal vettore dopo che sono stati aggiornati
		// altrimenti si rischia di aggiornarli piu' volte avendo modificato il
		// proprio xlink:href
		Vector UpdatedIntXlinkHref = new Vector(50, 10);

		for (int i = 0; i < InternalXlinkHref.size(); i++) {
			if (((Node) InternalXlinkHref.get(i)).getAttributes().getNamedItem("xlink:href").getNodeValue().substring(1).equals(OldID)) {
				// Si verifica se esiste un xlink:href interno ("#..."
				// (substring(1))
				// e' uguale al vecchio ID dell'elemento corrente, in questo
				// caso si
				// aggiorna l'xlink:href interno
				// Ho trovato un xlink:href interno uguale al vecchio ID
				// dell'elemento corrente
				// Aggiorno l'xlink:href interno

				Element parentXlinkHref = (Element) InternalXlinkHref.get(i);
				parentXlinkHref.setAttribute("xlink:href", "#" + NewID);
				if (parentXlinkHref.getNodeName().equals("rif")) {
					// Se il nodo che ha come attributo xlink:href e' un rif
					// allora si aggiorna anche il testo del rif
					Node IntRifTextNode = doc.createTextNode(getTextInternalRifFromID(parentXlinkHref, NewID));
					// FIXME non e' detto che la nuova forma testuale debba
					// essere quella standard; se il rif interno aveva una
					// forma testuale personalizzata, va lasciata quella
					((Node) parentXlinkHref).replaceChild(IntRifTextNode, parentXlinkHref.getFirstChild());
				}
				UpdatedIntXlinkHref.addElement(InternalXlinkHref.get(i));

			}
		}

		// Un riferimento aggiornato non deve piu' essere aggiornato
		// percio' lo tolgo dal vettore dei rif interni
		for (int i = 0; i < UpdatedIntXlinkHref.size(); i++) {
			logger.debug("removed updatedIntXlink: " + UtilDom.getAttributeValueAsString((Node) UpdatedIntXlinkHref.get(i), "xlink:href"));
			InternalXlinkHref.removeElement(UpdatedIntXlinkHref.get(i));
		}

	}

	/*
	 * Prendo i nodi che sono attributi di tipo IDRef (es: fonte)
	 */
	private void getIDRefAttributes(Node node) {
		if (node == null)
			return;

		NamedNodeMap attrList = node.getAttributes();
		if (attrList != null)
			for (int i = 0; i < attrList.getLength(); i++) {
				if (isIDRefAttribute((Attr) (attrList.item(i)))) {
					// Inserisco dentro il vettore il nodo che e' un IDRef
					IDRefAttributes.addElement(attrList.item(i));
				}
			}
		NodeList figli = node.getChildNodes();
		for (int i = 0; i < figli.getLength(); i++)
			getIDRefAttributes(figli.item(i));
		return;
	}

	private boolean isIDRefAttribute(Attr attr) {

		String attrName = attr.getName();
		// Aggiungere altri nomi di IDRef quando ci saranno
		if (attrName.equals("fonte"))
			return true;
		else
			return false;
	}

	/*
	 * Aggiorna gli attributi di tipo IDRef se c'e' stato un cambiamento negli
	 * ID dell'elemento corrente ai quali si riferivano che ha avuto l'ID
	 * cambiato da OldID a NewID
	 */
	private void updateIDRefAttributes(String OldID, String NewID) {
		// Vettore degli XlinkHref interni aggiornato
		// Dovranno essere tolti dal vettore dopo che sono stati aggiornati
		// altrimenti si rischia di aggiornarli piu' volte avendo modificato il
		// proprio xlink:href
		Vector UpdatedIDRefAttributes = new Vector();

		for (int i = 0; i < IDRefAttributes.size(); i++) {
			if (((Attr) IDRefAttributes.get(i)).getValue().equals(OldID)) {
				// Si verifica se esiste un attributo IDRef
				// e' uguale al vecchio ID dell'elemento corrente, in questo
				// caso si
				// aggiorna l'attributo IDRef

				((Attr) IDRefAttributes.get(i)).setValue(NewID);

				UpdatedIDRefAttributes.addElement(IDRefAttributes.get(i));
			}
		}
		// Un riferimento aggiornato non deve piu' essere aggiornato
		// percio' lo tolgo dal vettore degli attributi IDRef
		for (int i = 0; i < UpdatedIDRefAttributes.size(); i++)
			IDRefAttributes.removeElement(UpdatedIDRefAttributes.get(i));

	}

	private String getTextInternalRifFromID(Node intRif, String NewID) {

		// estraggo i vari componenti dell'id Ex: art12-com3-let1
		String[] IDComponent = NewID.split("-");
		String textRif = new String();

		// Controllo se il rif fa parte di un mrif in tal caso restituisco solo
		// il numero dell'unit? riferita
		if (intRif.getParentNode().getNodeName().compareTo("mrif") == 0) {
			textRif = ((String) IDComponent[IDComponent.length - 1]).substring(3);
			return textRif;
		}

		String callingId = null;
		if (nirUtilDom.getContainer(intRif) != null)
			callingId = UtilDom.getAttributeValueAsString(nirUtilDom.getContainer(intRif), "id");
		textRif = nirUtilUrn.getFormaTestualeById(callingId, NewID);

		return textRif;
	}

	/**
	 * getRoman
	 */
	private String getRoman(int num) {
		String res = "";
		if (num > 999)
			return res;
		int unita = (num % 10);
		int decine = (num % 100) / 10;
		int centinaia = (num % 1000) / 100;

		// Centinaia
		switch (centinaia) {
		case 1:
			res += "C";
			break;
		case 2:
			res += "CC";
			break;
		case 3:
			res += "CCC";
			break;
		case 4:
			res += "CD";
			break;
		case 5:
			res += "D";
			break;
		case 6:
			res += "DC";
			break;
		case 7:
			res += "DCC";
			break;
		case 8:
			res += "DCCC";
			break;
		case 9:
			res += "CM";
			break;
		case 10:
			res += "M";
			break;
		}
		// Decine
		switch (decine) {
		case 1:
			res += "X";
			break;
		case 2:
			res += "XX";
			break;
		case 3:
			res += "XXX";
			break;
		case 4:
			res += "XL";
			break;
		case 5:
			res += "L";
			break;
		case 6:
			res += "LX";
			break;
		case 7:
			res += "LXX";
			break;
		case 8:
			res += "LXXX";
			break;
		case 9:
			res += "XC";
			break;
		case 10:
			res += "C";
			break;
		}
		// Unita'
		switch (unita) {
		case 1:
			res += "I";
			break;
		case 2:
			res += "II";
			break;
		case 3:
			res += "III";
			break;
		case 4:
			res += "IV";
			break;
		case 5:
			res += "V";
			break;
		case 6:
			res += "VI";
			break;
		case 7:
			res += "VII";
			break;
		case 8:
			res += "VIII";
			break;
		case 9:
			res += "IX";
			break;
		case 10:
			res += "X";
			break;
		}
		return res;
	}

	/**
	 * Calcola il valore dell'elemento <b>num</b> contenuto nel nodo passato,
	 * se il nodo non &egrave; di tipo libro, parte, titolo, capo, sezione, articolo,
	 * comma, lettera, numero, elenco puntato restituisce una stringa vuota
	 * 
	 * @param n nodo di cui si vuole calcolare il contenuto del sottoelemento
	 *        num
	 * @param posizione numero d'ordine del nodo come figlio del padre
	 * @return valore da inserire come contenuto del sottoelemento num del nodo
	 *         attivo
	 */
	public String getValoreNum(Node n, int posizione) {

		String res = null;
		switch (getElementType(n)) {
		case LIBRO:
			res = "Libro " + this.getRoman(posizione);
			break;
		case PARTE:
			res = "Parte " + this.getRoman(posizione);
			break;
		case TITOLO:
			res = "Titolo " + this.getRoman(posizione);
			break;
		case CAPO:
			res = "Capo " + this.getRoman(posizione);
			break;
		case SEZIONE:
			res = "Sezione " + this.getRoman(posizione);
			break;
		case ARTICOLO:
			res = "Art. " + posizione;
			break;
		case COMMA:
			res = posizione + ".";
			break;
		case LETTERA:
			res = posizione + ")";
			//res = UtilLang.fromNumberToLetter("" + posizione) + ")";
			break;
		case NUMERO:
			res = posizione + ")";
			break;
		case ELENCO_PUNT:
			//res = (n.getNodeValue()!=null) ? n.getNodeValue() : "-";
			res = "-";
			break;	
		default:
			res = "";
			break;
		}

		return res;
	}

}

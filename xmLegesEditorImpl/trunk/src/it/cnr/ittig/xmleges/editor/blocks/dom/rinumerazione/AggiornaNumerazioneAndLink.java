/*
 * Created on Mar 3, 2005
 */
package it.cnr.ittig.xmleges.editor.blocks.dom.rinumerazione;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.lang.UtilLang;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;

import java.util.HashMap;
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

	RulesManager rulesManager;


	public AggiornaNumerazioneAndLink(RinumerazioneImpl rinumerazioneImpl) {
		super(rinumerazioneImpl);
		this.logger = rinumerazioneImpl.getLogger();
		this.rulesManager = rinumerazioneImpl.rulesManager;
		this.nirUtilUrn = rinumerazioneImpl.nirUtilUrn;
		this.nirUtilDom = rinumerazioneImpl.nirUtilDom;
	}

	
	
	public void aggiornaNum(Node nodo) {
		
		modIDs=new HashMap();
		doc = nodo.getOwnerDocument();
		NodeList nir = doc.getElementsByTagName("NIR");
		
		System.err.println("CALLED AggiornaNum");

		
		// aggiornamento relativo alle note:
		// le note vengono ordinate in base a come compaiono nel testo prima di risettargli gli id
		Vector ndrId = getNdrIdFromDoc();
		sortNotes(ndrId);
		
		// Aggiornamento ID e <num> degli elementi da rinumerare
		updateIDsByPosition(nir.item(0));
		updateNumByID();
		
		
		// Assegnazione degli ID alle partizioni di una Modifica (dentro le virgolette) 
		// Anche con rinumerazione attiva, dentro le modifiche si applica il metodo AggiornaIdFrozenLaw.updateIDs (come se la rinumerazione fosse spenta) 
		NodeList virgoletteList = doc.getElementsByTagName("virgolette");
		for (int i = 0; i < virgoletteList.getLength(); i++) {
			updateIDs(virgoletteList.item(i).getFirstChild());
		}
			
		// Recupero gli attributi che sono di tipo idref per poi aggiornarli
		// nel caso avessero puntato a nodi che hanno cambiato posizione
		// esempio idref: "fonte", "xlink:href"  etc. etc. 
		getAndUpdateReferringAttributes(nir.item(0));   // cablare qui dentro l'updateInternalXlinkHref (FATTO)

			
		// rinumera gli ndr
		renumNdr();
		
	}
	
	
	protected void getAndUpdateReferringAttributes(Node node) {
		
		if (node == null || modIDs.size()==0)
			return;

		NamedNodeMap attrList = node.getAttributes();
		if (attrList != null){
			for (int j = 0; j < attrList.getLength(); j++) {
				Attr attributo = (Attr) (attrList.item(j));
				if(!UtilDom.isIdAttribute(attributo) && attributo.getValue()!=null && !((String)(attributo.getValue())).equals("")){
					if (modIDs.keySet().contains(attributo.getValue())) {
						
						System.out.println("FOUND REFERRING ATTRIBUTE: nodo "+node.getNodeName()+" cambia attributo "+attributo.getName() + " da "+attributo.getValue()+" a "+(String) modIDs.get(attributo.getValue()));
						attributo.setValue((String) modIDs.get(attributo.getValue()));
					}
					if(((String)attributo.getValue()).startsWith("#") && modIDs.keySet().contains( ((String)attributo.getValue()).substring(1) ) ){
						
						System.out.println("FOUND REFERRING ATTRIBUTE with #: nodo "+node.getNodeName()+" cambia attributo da "+((String)attributo.getValue())+" a "+"#"+(String) modIDs.get(((String)attributo.getValue()).substring(1)));
						String newId = (String) modIDs.get(((String)attributo.getValue()).substring(1));
						attributo.setValue("#"+newId);
						
						
						// ex updateInternalXlinkHref
						// AGGIUNTO QUESTO IF AL getAndUpdateReferringAttributes() di AggiornaIdFrozenLaw
						if (node.getNodeName().equals("rif")) {
									// Se il nodo che ha come attributo xlink:href e' un rif
									// allora si aggiorna anche il testo del rif
									Node IntRifTextNode = doc.createTextNode(getTextInternalRifFromID(node, newId));
									// FIXME non e' detto che la nuova forma testuale debba
									// essere quella standard; se il rif interno aveva una
									// forma testuale personalizzata, va lasciata quella
									node.replaceChild(IntRifTextNode, node.getFirstChild());
						}
						
					}
				}
			}
		}
		NodeList figli = node.getChildNodes();
		for (int j = 0; j < figli.getLength(); j++)
			getAndUpdateReferringAttributes(figli.item(j));
		return;
	}
	
	


	//  nel caso AggiornaId ripristina il contenuto originale dell'attributo xlink:href sulla base del contenuto testuale del rif	
	//  nel caso AggiornaNum  VICEVERSA  aggiorna il contenuto testuale del rif sulla base del valore assegnato all'id dell'xlink:href
	private String getTextInternalRifFromID(Node intRif, String NewID) {
	
		// estraggo i vari componenti dell'id Ex: art12-com3-let1
		String[] IDComponent = NewID.split("-");
		String textRif = new String();
	
		// Controllo se il rif fa parte di un mrif in tal caso restituisco solo
		// il numero dell'unità riferita
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
			
				// IDValue = getIDByPosition(figlio);
				// Se esiste già un id in qualunque elemento
				// lo prendo come OldID
				String OldID = ((Element) nodo).getAttribute("id");
				
				if (!UtilDom.hasIdAttribute(nodo) || !IDValue.equals(OldID)) {   // se non ce l'aveva o e' cambiato
					
					if(!IDValue.equals(OldID)){
						System.err.println("NUM: idChanged: new  " + IDValue + " old " + OldID);
						modIDs.put(OldID, IDValue);
					}
					System.err.println("NUM: setting id: new  " + IDValue + " old " + OldID);
					UtilDom.setIdAttribute(nodo, IDValue);
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
	
	
	private void renumNdr() {
		NodeList ndr = doc.getElementsByTagName("ndr");
		NodeList note = doc.getElementsByTagName("nota");
		// quando arrivo qui i vettori ndr e note sono nello stesso ordine
		String num, value, prefix;

		for (int i = 0; i < ndr.getLength(); i++) {
						
			prefix = getNdrNumPrefix(UtilDom.getAttributeValueAsString((Node) note.item(i), "id")); 
			num = UtilDom.getAttributeValueAsString((Node) note.item(i), "id");
			
			// togliere l'intero prefix e non solo la n
			value = num.substring(prefix.length());
			UtilDom.setAttributeValue(ndr.item(i), "valore", value);
			String tipoNdr = rinum.getRinumerazioneNdr();
			if(logger.isDebugEnabled())
				logger.debug("tipoNdr " + tipoNdr);
			if (tipoNdr.equalsIgnoreCase("cardinale"))
				UtilDom.setTextNode(ndr.item(i), "(" + value + ")");
			else if (tipoNdr.equalsIgnoreCase("letterale"))
				UtilDom.setTextNode(ndr.item(i), "(" + UtilLang.fromArabicToLetter(value).toLowerCase() + ")");
			else
				UtilDom.setTextNode(ndr.item(i), "(" + UtilLang.fromArabicToRoman(value).toLowerCase() + ")");
		}
	}
	
	
	
	private String getNdrNumPrefix(String num){
		
		char[] numChar = num.toCharArray();
		int i=0;
		
		for (i=numChar.length-1; i>=0;i--){
			if(!isDigit(numChar[i]))
				break;
		}
		if(i>0)
		   return num.substring(0,i+1);
		return "n";
	}
	
	
}

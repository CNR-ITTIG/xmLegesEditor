/*
 * Created on Feb 11, 2005
 */
package it.cnr.ittig.xmleges.editor.blocks.dom.rinumerazione;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.lang.UtilLang;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Enrico Francesconi Modifiche Tommaso Agnoloni
 */
public class AggiornaIdFrozenLaw {

	Logger logger;

	RulesManager rulesManager;

	RinumerazioneImpl rinum;

	NirUtilDom nirUtilDom;

	protected static final int LIBRO = 0;

	protected static final int PARTE = 1;

	protected static final int TITOLO = 3;

	protected static final int CAPO = 4;

	protected static final int SEZIONE = 5;

	protected static final int ARTICOLO = 6;

	protected static final int COMMA = 7;

	protected static final int LETTERA = 8;

	protected static final int NUMERO = 9;

	protected static final int ELENCO_PUNT = 10;

	protected static final int ANNESSO = 11;

	protected static final int PREAMBOLO = 12;

	protected static final int RIF = 13;

	protected static final int MOD = 14;

	protected static final int VIR = 15;

	protected static final int INL = 16;

	protected static final int BLC = 17;

	protected static final int NOT_AVV_ALT = 18;

	protected static final int EVENTO = 19;

	protected static final int ORIGINALE = 20;

	protected static final int ATTIVA = 21;

	protected static final int PASSIVA = 22;

	protected static final int GIURISPRUDENZA = 23;
	
	protected static final int SPAN = 24;

	protected final int OTHER = -1;

	int elementType = this.OTHER;

	protected String bis[] = { "bis", "ter", "quater", "quinquies", "sexies", "septies", "octies", "novies", "decies", "undecies", "duodecies", "terdecies",
			"quaterdecies", "quinquiesdecies", "sexiesdecies", "septiesdecies", "octiesdecies", "noviesdecies", "venies", "duovenies", "tervenies",
			"quatervenies", "quinquiesvenies", "sexiesvenies", "septiesvenies", "octiesvenies", "noviesvenies" };

	protected String zerozero[] = { "01", "001", "0001", "00001", "000001", "0000001", "00000001", "000000001", "0000000001", "00000000001" };

	// Vettore dei riferimenti interni
	protected Vector Rif;

	protected Document doc;

	/** Creates a new instance of AggiornaIDFrozenLaw */
	public AggiornaIdFrozenLaw(RinumerazioneImpl rinumerazioneImpl) {
		this.logger = rinumerazioneImpl.getLogger();
		this.rulesManager = rinumerazioneImpl.rulesManager;
		this.rinum = rinumerazioneImpl;
		this.nirUtilDom = rinumerazioneImpl.nirUtilDom;
	}

	public void aggiornaID(Node nodo) {

		doc = nodo.getOwnerDocument();

		// aggiornamento relativo alle note:
		// le note vengono ordinate in base a come compaiono nel testo
		Vector ndrId = getNdrIdFromDoc();
		sortNotes(ndrId);
		// /////////////////////////////////

		// Per gli elementi che contengono un num genero l'ID sulla base di
		// <num>
		// Per gli altri genero l'ID sulla base della posizione
		updateIDs(nodo);

		Rif = getRif();
		setXlinkHrefInternalRifByContent();

		setNdrLink(ndrId, false);
	}

	public Vector getNdrIdFromDoc() {
		NodeList ndr = doc.getElementsByTagName("ndr");
		Vector ids = new Vector(50, 10);
		for (int i = 0; i < ndr.getLength(); i++) {
			String id = UtilDom.getAttributeValueAsString(ndr.item(i), "num");
			if (ids.indexOf(id) == -1)
				ids.add(id);
		}
		return ids;
	}

	public boolean sortNotes(Vector ndrId) {
		// ordina le note dentro redazionale nello stesso ordine in cu compaiono gli ndr nel testo
		Vector newNotes = new Vector(50, 10);
		Node parent = null;

		// copia delle note nell'ordine in cui compaiono nel testo
		for (int i = 0; i < ndrId.size(); i++) {
			// FIXME bug su getElementById(); ancora non va
			// if(doc.getElementById((String)ndrId.get(i))!=null)
			// newNotes.add(doc.getElementById((String)ndrId.get(i)).cloneNode(true));
			if (getNotaById((String) ndrId.get(i)) != null)
				newNotes.add(getNotaById((String) ndrId.get(i)).cloneNode(true));
			else{
				if(logger.isDebugEnabled())
				    logger.debug("il nodo di " + ndrId.get(i) + " e' null");
			}
		}

		Vector oldNotes = getOldNotes();

		if (oldNotes.size() > newNotes.size()) {
			if(logger.isDebugEnabled())
				logger.debug("ci sono note senza ndr; completa la lista");
			// completa il vector newNotes da oldNotes con id != note in
			// newNotes (aggiunge le note che non hanno un ndr)
			for (int i = 0; i < oldNotes.size(); i++) {
				if (ndrId.indexOf(UtilDom.getAttributeValueAsString((Node) oldNotes.get(i), "id")) == -1)
					newNotes.add(oldNotes.get(i));
			}
		}
         
		if(logger.isDebugEnabled())
			logger.debug("notaLength " + oldNotes.size() + " ndrLength " + newNotes.size());
		if (oldNotes.size() == newNotes.size()) {
//			if (oldNotes.size() > 0)
//				parent = ((Node) oldNotes.get(0)).getParentNode();
			for (int i = 0; i < oldNotes.size(); i++) {
				parent = ((Node) oldNotes.get(i)).getParentNode();
				if(logger.isDebugEnabled())
					logger.debug("replace oldNote " + UtilDom.getAttributeValueAsString((Node) oldNotes.get(i), "id") + " with newNote "
						+ UtilDom.getAttributeValueAsString((Node) newNotes.get(i), "id"));
				// FIXME: non e' detto che il parent (redazionale) sia lo stesso per tutte le note; 
				// e.g. note negli allegati
				parent.replaceChild((Node) newNotes.get(i), (Node) oldNotes.get(i));
			}
			return true;
		} else{
			if(logger.isDebugEnabled())
				logger.debug("ci sono ndr senza nota");
		}
		return false;
	}

	private Node getNotaById(String id) {

		NodeList notes = doc.getElementsByTagName("nota");
		for (int i = 0; i < notes.getLength(); i++) {
			if (UtilDom.getAttributeValueAsString(notes.item(i), "id").equals(id))
				return (notes.item(i));
		}
		return null;
	}

	private Vector getOldNotes() {
		Vector old = new Vector(50, 10);
		NodeList notes = doc.getElementsByTagName("nota");
		for (int i = 0; i < notes.getLength(); i++) {
			old.add(notes.item(i));
		}
		return old;
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
	
	public void setNdrLink(Vector ndrId, boolean renum) {
		NodeList ndr = doc.getElementsByTagName("ndr");
		NodeList note = doc.getElementsByTagName("nota");
		String num, value, prefix;

		for (int i = 0; i < ndr.getLength(); i++) {
			
			//prefix = getNdrNumPrefix(UtilDom.getAttributeValueAsString((Node) ndr.item(i), "num")); 
			//num = prefix + (ndrId.indexOf(UtilDom.getAttributeValueAsString((Node) ndr.item(i), "num")) + 1);
			
			prefix = getNdrNumPrefix(UtilDom.getAttributeValueAsString((Node) note.item(i), "id")); 
			num = UtilDom.getAttributeValueAsString((Node) note.item(i), "id");
			
			UtilDom.setAttributeValue(ndr.item(i), "num", num);
			if(logger.isDebugEnabled())
				logger.debug("renum " + renum);
			if (renum) {
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
	}

	/**
	 * Aggiorna gli ID sulla base delle posizioni o dei num
	 * 
	 * @param nodo
	 */
	public void updateIDs(Node nodo) {

		// sigla: Parte caratteri dell'id di un elemento (se non specificata
		// in getSiglaElemento si considerano le prime 3 o 2 o 1 lettera
		// iniziale del nome dell'elemento
		// (es: <comma> sigla = com, <vigenza> sigla = v)

		String IDValue; // Valore del nuovo ID
		String OldID;

		if (nodo == null)
			return;

		if (isElementToBeIDUpdated(nodo)) {
			if (isElementWithNum(nodo))
				IDValue = getIDByNum(nodo);
			else
				IDValue = getIDByPosition(nodo);
			
            /////////////////////////////////////////////////////////////////////////////
			//          QUI SETTA IL SUFFISSO ALL'ID DELLE PARTIZIONI SOPPRESSE               
			/////////////////////////////////////////////////////////////////////////////
			String finevigore = UtilDom.getAttributeValueAsString(nodo,"finevigore");
			if(null!=finevigore && !finevigore.equals("") && getElementType(nodo)!=SPAN){
				IDValue+="-"+finevigore;
				if(logger.isDebugEnabled()){
					logger.debug("finevigore = "+finevigore);
					logger.debug("IDValue = "+IDValue);
				}			
			}
			/////////////////////////////////////////////////////////////////////////////
			
			// Aggiornamento ID dell'elemento se e' cambiato
			OldID = ((Element) nodo).getAttribute("id");
			
			
			// FIXME patch per id degli eventi - disabilita il setId degli eventi ma mantiene il setIdAttribute
  		    if((getElementType(nodo)==EVENTO)){
  		    	IDValue = OldID;
			}
			////////////////////////////////////////////////////////////////////////////////////////////////// 

  		    if(logger.isDebugEnabled()) 		    
			 logger.debug("idChanged: new  " + IDValue + " old " + OldID);

			if (!UtilDom.hasIdAttribute(nodo) || !OldID.equals(IDValue)) {
				UtilDom.setIdAttribute(nodo, IDValue);
				if(logger.isDebugEnabled())
				  logger.debug("idChanged: new  " + IDValue + " old " + OldID);
			}
		}

		NodeList figliNodo = nodo.getChildNodes();

		for (int i = 0; i < figliNodo.getLength(); i++) {
			Node figlio = figliNodo.item(i);
			updateIDs(figlio);
		}
	}

	private boolean isElementWithNum(Node node) {
		int elemType = getElementType(node);
		if (elemType == LIBRO || elemType == PARTE || elemType == TITOLO || elemType == CAPO || elemType == SEZIONE || elemType == ARTICOLO
				|| elemType == COMMA || elemType == NUMERO || elemType == LETTERA)
			return true;
		else
			return false;

	}

	private boolean isElementWithRoman(Node node) {
		int elemType = getElementType(node);
		if (elemType == LIBRO || elemType == PARTE || elemType == TITOLO || elemType == CAPO || elemType == SEZIONE)
			return true;
		else
			return false;
	}

	private boolean isElementWithLetter(Node node) {
		int elemType = getElementType(node);
		if (elemType == LETTERA)
			return true;
		else
			return false;
	}

	private String getIDByNum(Node nodo) {
		String IDValue;

		if (getElementType(nodo) == ARTICOLO)
			IDValue = getIDArticoloByNum(nodo);
		else
			IDValue = getIDNotArticoloByNum(nodo);

		return IDValue;
	}

	private String getIDArticoloByNum(Node articolo) {
		String IDValue;
		String sigla = getSiglaElemento(articolo);
		String pos = getPositionByNum(articolo);

		// ------------------------------------------
		// Aggiunta per trattare commi non numerati
		if (pos.trim().length() == 0) {
			pos = "" + getPosRelativaFratelli(articolo);
			if(logger.isDebugEnabled())
				logger.debug("POS FROM FRATELLI in getIDArticoloByNum " + pos);
		} else {
			if(logger.isDebugEnabled())
				logger.debug("POS FROM NUM in getIDArticoloByNum " + pos);
		}
		// ------------------------------------------

		if (isContained(articolo, "annesso")) {
			Node annesso = UtilDom.findParentByName(articolo, "annesso");
			IDValue = ((Element) annesso).getAttribute("id") + "-" + sigla + pos;
		} else if (isContained(articolo, "virgolette")) {
			Node virg = UtilDom.findParentByName(articolo, "virgolette");
			IDValue = ((Element) virg).getAttribute("id") + "-" + sigla + pos;
		} else
			IDValue = sigla + pos;
		return IDValue;
	}

	private String getIDNotArticoloByNum(Node nodo) {
		String IDValue;
		String sigla = getSiglaElemento(nodo);
		String pos = getPositionByNum(nodo);

		// ------------------------------------------
		// Aggiunta per trattare commi non numerati
		if (pos.trim().length() == 0) {
			pos = "" + getPosRelativaFratelli(nodo);
			if(logger.isDebugEnabled())
				logger.debug("POS FROM FRATELLI in getIDNotArticoloByNum " + pos);
		} else {
			if(logger.isDebugEnabled())
				logger.debug("POS FROM NUM in getIDNotArticoloByNum " + pos);
		}
		// ------------------------------------------

		// Verifico se il nodo ? fra quelli che devono avere un ID gerarchico
		// Se si' => acquisisco l'ID del parent
		// Se il parent ha ID => scrivo l'ID in modo gerarchico
		// altrimenti => scrivo l'ID in modo NON gerarchico
		// Se no => scrivo l'ID in modo NON gerarchico

		if (isIDNodeToBeHierachical(nodo)) {
			// Prelevo l'ID del genitore
			String parentID = getParentID(nodo);
			if (parentID != null)
				IDValue = parentID + "-" + sigla + pos;
			else
				IDValue = sigla + pos;
		} else
			IDValue = sigla + pos;
		// ---------------------------------------------------

		return IDValue;
	}

	private String getPositionByNum(Node node) {
		Node num = UtilDom.findDirectChild(node, "num");
		if (num == null)
			return "";
		String NumContent = UtilDom.getTextNode(num); // non bisogna andare in
														// profondit?
														// .getRecursiveTextNode(num);
		// Prendo da <num> l'informazione utile a costruire l'ID
		String InfoNum = getInformationToBuildID(NumContent);
		if(logger.isDebugEnabled())
			logger.debug("InfoNum " + InfoNum);
		if (isLetter(InfoNum) && isElementWithLetter(node))
			return ("" + InfoNum);//UtilLang.fromLetterToNumber(InfoNum));
		else if (isRoman(InfoNum) && isElementWithRoman(node))
			return ("" + UtilLang.fromRomanToArabic(InfoNum));
		else
			return InfoNum;
	}

	/**
	 * @param node
	 * @return
	 */
	private int getIntPositionByNum(Node node) {
		String pos = getPositionByNum(node);
		if (pos.trim().length() > 0 && isNumeric(pos)) {
			try {
				return (Integer.parseInt(pos));
			} catch (NumberFormatException ex) {
				logger.error(ex.toString());
				return (-1);
			}
		}
		if(pos.trim().length() > 0  && !isNumeric(pos))   // per le lettere
			return UtilLang.fromLetterToNumber21(pos);	
		return (-1);
	}

	/**
	 * Del contenuto di <num> cerco di prendere la parte informativa utile per
	 * la costruzione dell'ID
	 */
	private String getInformationToBuildID(String NumContent) {
		NumContent = NumContent.toLowerCase().trim();
		// Sostituisco tutte le occorrenze di caratteri non lettera ne' numero
		// (es: ')' e '.')
		// con uno spazio
		NumContent = NumContent.replaceAll("[\\W*]", " ");

		// Si cancellano da num le parole corrispondenti alle partizioni
		// tali parole sono individuate almeno dal prefisso (es: Art, Sez)
		// e da tutte le lettere seguenti eccetto lo spazio (\S*)
		NumContent = NumContent.replaceAll("[lL]ibro\\S*", "");
		NumContent = NumContent.replaceAll("[pP]arte\\S*", "");
		NumContent = NumContent.replaceAll("[tT]itolo\\S*", "");
		NumContent = NumContent.replaceAll("[cC]apo\\S*", "");
		NumContent = NumContent.replaceAll("[sS]ez\\S*", "");
		NumContent = NumContent.replaceAll("[aA]rt\\S*", "");

		// FIXME momentaneamente rimuove anche L ed R (l ed r) provenienti dalla
		// decorazione
		NumContent = NumContent.replaceAll("[(l]\\S*", "");
		// NumContent = NumContent.replaceAll("[(r]\\S*", "");
		NumContent = NumContent.replaceAll("[^e][r]", ""); // salva il ter

		NumContent = NumContent.replaceAll("\\s", "");
		NumContent = NumContent.trim();
		return NumContent;
	}

	private Vector getRif() {
		Rif = new Vector(50, 10);
		NodeList rif = doc.getElementsByTagName("rif");
		for (int i = 0; i < rif.getLength(); i++) {
			if (!UtilDom.findAttribute(rif.item(i), "xlink:href").startsWith("urn"))
				Rif.addElement(rif.item(i));
		}
		return Rif;
	}

	private void setXlinkHrefInternalRifByContent() {
		// il vector Rif contiene tutti i nodi di tipo Rif Interni
		// per cui posso risalire anche alla partizione che li contiene per
		// completare gli incompleti
		for (int i = 0; i < Rif.size(); i++) {
			String rifText = UtilDom.getRecursiveTextNode((Node) Rif.get(i));
			String oldXlinkHref = UtilDom.getAttributeValueAsString((Node) Rif.get(i), "xlink:href");
			if (oldXlinkHref == null || oldXlinkHref.length() == 0) {
				Node container = nirUtilDom.getContainer((Node) Rif.get(i));
				String XlinkHref = getXlinkHrefInternalRifByContent(rifText, container);
				((Element) Rif.get(i)).setAttribute("xlink:href", XlinkHref);
			}
		}
	}

	// risetta gli XlinkHref interni a partire dalla forma testuale del rif

	private String getXlinkHrefInternalRifByContent(String rifText, Node container) {
		String XlinkHref = new String();

		// Tolgo gli eventuali caratteri inutili (es: ')', '.')
		rifText = rifText.replaceAll("[\\W*]", " ");
		rifText = rifText.trim();

		// Splitto il testo rispetto agli spazi (es: articolo | 3 | comma | 2 |)
		String rifTextComp[] = rifText.split("\\s+");

		// Costruzione di XlinkHref da destra a sinistra (si gestisce meglio il
		// '-')
		for (int i = (rifTextComp.length - 1); i >= 0; i--) {
			if (isNumeric(rifTextComp[i]))
				XlinkHref = rifTextComp[i] + XlinkHref;
			else if (isRoman(rifTextComp[i])) // Probabilmente non si verifica
												// mai
				XlinkHref = UtilLang.fromRomanToArabic(rifTextComp[i]) + XlinkHref;
			else if (isLetter(rifTextComp[i]))
				XlinkHref = UtilLang.fromLetterToNumber(rifTextComp[i]) + XlinkHref;
			else if (rifTextComp[i].trim().equalsIgnoreCase("articolo"))
				XlinkHref = "-art" + XlinkHref;
			else if (rifTextComp[i].trim().equalsIgnoreCase("comma"))
				XlinkHref = "-com" + XlinkHref;
			else if (rifTextComp[i].trim().equalsIgnoreCase("lettera"))
				XlinkHref = "-let" + XlinkHref;
			else if (rifTextComp[i].trim().equalsIgnoreCase("numero"))
				XlinkHref = "-num" + XlinkHref;
			else {
				// se c'e' qualcos'altro rispetto alla formulazione linguistica
				// di un riferimento interno => e' un riferimento esterno o non
				// si
				// sa determinare il valore dell'XlinkHref
				XlinkHref = "";
				break;
			}
		}

		if (XlinkHref.length() > 0) {
			if (XlinkHref.charAt(0) == '-')
				XlinkHref = "#" + XlinkHref.substring(1);
			else
				XlinkHref = "#" + XlinkHref;

			if (XlinkHref.indexOf("com") != -1 && XlinkHref.indexOf("art") == -1) {
				// aggiungere l'articolo
			}

		}

		// TODO mettere un controllo xlink:href completi sotto all'articolo
		// art-com-let-num
		// e sopra all'articolo cap-sez etc

		return XlinkHref;
	}

	private boolean isNumeric(String s) {
		CharacterIterator theIterator = new StringCharacterIterator(s);
		for (char ch = theIterator.first(); ch != CharacterIterator.DONE; ch = theIterator.next()) {
			if (!Character.isDigit(ch))
				return false;
		}
		return true;
	}

	private boolean isLetter(String string) {
		boolean isLet = false;
		if (string.length() == 0)
			return isLet;
		if (string.trim().charAt(0) >= 'a' && string.trim().charAt(0) <= 'z') {
			isLet = true;
			for (int i = 1; i < string.length(); i++) { // comprende il caso aa
														// bb aaa bbb ccc
				if (string.trim().charAt(i) == string.trim().charAt(0))
					isLet = true;
				else
					isLet = false;
			}
		} else
			isLet = false;

		return isLet;
	}

	private boolean isRoman(String number) {
		String ucnumber = number.toUpperCase();
		if (ucnumber.length() == 0)
			return (false);
		for (int i = 0; i < ucnumber.length(); i++) {
			if (!((ucnumber.charAt(i) == 'I') || (ucnumber.charAt(i) == 'V') || (ucnumber.charAt(i) == 'X') || (ucnumber.charAt(i) == 'L')
					|| (ucnumber.charAt(i) == 'C') || (ucnumber.charAt(i) == 'D') || (ucnumber.charAt(i) == 'M'))) {
				return (false);
			}

		}
		return (true);
	}

//	protected boolean isElementToBeIDUpdated(Node figlio) {
//		if (getElementType(figlio) != OTHER)
//			return true;
//		else
//			return false;
//	}

	 protected boolean isElementToBeIDUpdated(Node figlio)
	 {
		 // in questo modo setta gli id solo agli elementi che hanno id REQUIRED
		 // + forza il setId anche alle lettere, numeri, ep: gli id servono per la rinumerazione di quegli elementi
		 try{
			 if(rulesManager.queryIsRequiredAttribute(figlio.getNodeName(),"id") || getElementType(figlio)==LETTERA || getElementType(figlio)==NUMERO || getElementType(figlio)==ELENCO_PUNT || getElementType(figlio)==SPAN){
				 // FIXME previene il setId degli eventi; la soluzione corretta e' quella di settare sia gli id che gli idref 
				   return true;
			 }
			 else{
				 if(logger.isDebugEnabled())
					 logger.debug("not required id for "+figlio.getNodeName());
				 return false;
			 }
		 }
		 catch(RulesManagerException e){
			 if(logger.isDebugEnabled())
				 logger.debug("no id for "+figlio.getNodeName());
			 return false;
		 }
	 }

	protected boolean isElementWithID(Node figlio) {
		if (figlio.getNodeType() == Node.ELEMENT_NODE && figlio.getAttributes() != null && figlio.getAttributes().getNamedItem("id") != null)
			return true;
		else
			return false;
	}

	protected String getIDByPosition(Node nodo) {
		String IDValue;

		if (getElementType(nodo) == ARTICOLO)
			IDValue = getIDArticoloByPosition(nodo);
		else
			IDValue = getIDNotArticoloByPosition(nodo);

		return IDValue;
	}

	private String getIDArticoloByPosition(Node articolo) {
		String IDValue;
		int pos;
		String sigla = getSiglaElemento(articolo);

		// Gli articoli hanno due numerazioni assolute
		// dentro il documento (posArt)
		// e dentro gli annessi (posArtAnnesso)

		// Prendo come riferimento l'elemento <articolato> che puo' essere
		// quello dentro
		// l'annesso o dentro il documento principale

		Node articolato = UtilDom.findParentByName(articolo, "articolato");
		pos = getPosizioneArticolo(articolo, articolato);

		if (isContained(articolo, "annesso")) {
			// posArtAnnesso++;
			Node annesso = UtilDom.findParentByName(articolo, "annesso");
			IDValue = ((Element) annesso).getAttribute("id") + "-" + sigla + pos;
		} else {
			// posArt++;
			IDValue = sigla + pos;
		}
		return IDValue;
	}

	private String getIDNotArticoloByPosition(Node nodo) {
		String IDValue;
		String sigla = getSiglaElemento(nodo);

		// Verifico se il nodo ? fra quelli che devono avere un ID gerarchico
		// Se si' => acquisisco l'ID del parent
		// Se il parent ha ID => scrivo l'ID in modo gerarchico
		// altrimenti => scrivo l'ID in modo NON gerarchico
		// Se no => scrivo l'ID in modo NON gerarchico
		if (isIDNodeToBeHierachical(nodo)) {
			// Prelevo l'ID del genitore
			String parentID = getParentID(nodo);
			if (parentID != null)
				IDValue = parentID + "-" + sigla + getPosizioneRelativaElemento(nodo);
			else
				IDValue = sigla + getPosizioneAssolutaElemento(nodo);
		} else
			IDValue = sigla + getPosizioneAssolutaElemento(nodo);
		// ---------------------------------------------------
		return IDValue;
	}

	/*
	 * Restituisce la posizione di un articolo rispetto al precedente
	 * all'interno della gerarchia di padre container, a noi interessano due
	 * casi: 1) posizione articolo all'interno del documento principale 2)
	 * posizione articolo all'interno di uno degli annessi
	 */
	private int getPosizioneArticolo(Node articolo, Node container) {
		int posizione = 1;

		Node prevArt = findPreviousInRank(articolo, container);
		posizione = getLastNumberIDElement(prevArt) + 1;

		return posizione;
	}

	/*
	 * Restituisce il nodo precedente dello stesso rango (es: dato un articolo,
	 * restituisce il precedente articolo all'interno della gerachia di radice
	 * container)
	 */
	private Node findPreviousInRank(Node node, Node container) {
		if (node == null)
			return null;

		NodeList nl = ((Element) container).getElementsByTagName(node.getNodeName());

		for (int i = 0; i < nl.getLength(); i++) {
			if (node == nl.item(i)) {
				if (i == 0)
					return null;
				else
					return nl.item(i - 1);
			}
		}
		return null;
	}

	/*
	 * Restituisce l'ultimo numero dell'ID di un elemento (es: art3-com2 =>
	 * restituisce 2) Altrimenti se l'elemento non ha ID o l'ID non ha un
	 * contenuto con un numero => restituisce 0
	 */
	private int getLastNumberIDElement(Node node) {
		int lastNumberID = 0;
		if (node != null)
			if (node.getAttributes() != null)
				if (node.getAttributes().getNamedItem("id") != null) {
					String id = node.getAttributes().getNamedItem("id").getNodeValue();
					for (int i = id.length() - 1; i >= 0; i--) {
						if (!isDigit(id.charAt(i))) {
							try {
								lastNumberID = Integer.parseInt(id.substring(i + 1));
								break;
							} catch (Exception e) {
								return (0);
							}
						}
					}
				}

		return lastNumberID;
	}
	
	/*
	 * Restituisce le ultime lettere dell'ID di un elemento lettera(es: art3-com2-leta =>
	 * restituisce a) Altrimenti se l'elemento non ha ID o l'ID non ha un
	 * contenuto con una lettera => restituisce ""
	 */
	private String getLastLetterIDElement(Node node) {
		String  lastLetterID = "";
		if (node != null)
			if (node.getAttributes() != null)
				if (node.getAttributes().getNamedItem("id") != null) {
					String id = node.getAttributes().getNamedItem("id").getNodeValue();
					String lastId = id.substring(id.lastIndexOf("-"));
					if(lastId.length()>0)
					    lastLetterID = lastId.substring(lastId.indexOf("let")+3);
				}
		return lastLetterID;
	}
	
	/*
	 * Restituisce le ultime lettere dell'ID di un elemento lettera(es: art3-com2-leta =>
	 * restituisce a) Altrimenti se l'elemento non ha ID o l'ID non ha un
	 * contenuto con una lettera => restituisce ""  [II: depurato da bis, ter etc]
	 */
	private String getLastLetterIDElementII(Node node) {
		String  lastLetterID = "";
		if (node != null)
			if (node.getAttributes() != null)
				if (node.getAttributes().getNamedItem("id") != null) {
					String id = node.getAttributes().getNamedItem("id").getNodeValue();
					String lastId = id.lastIndexOf("-")>0?id.substring(id.lastIndexOf("-")):"";
					if(lastId.length()>0)
					    lastLetterID = lastId.substring(lastId.indexOf("let")+3);
					for(int i=0; i<bis.length; i++){
					   if(lastLetterID.indexOf(bis[i])!=-1){
						   lastLetterID=lastLetterID.substring(0,lastLetterID.indexOf(bis[i]));
					       //return lastLetterID;
						   return "";
					   }
					}
				}
		return lastLetterID;
	}

	/*
	 * Restituisce l'ultimo numero dell'ID di un elemento (es: art3-com2 =>
	 * restituisce 2) Altrimenti se l'elemento non ha ID o l'ID non ha un
	 * contenuto con un numero => restituisce 0
	 */
	private int getLastNumberIDElementII(Node node) {
		int lastNumberID = 0, i;
		if (node != null)
			if (node.getAttributes() != null)
				if (node.getAttributes().getNamedItem("id") != null) {
					String id = node.getAttributes().getNamedItem("id").getNodeValue();
					for (i = id.length() - 1; i >= 0; i--) {
						if (!isDigit(id.charAt(i))) {
							try {
								lastNumberID = Integer.parseInt(id.substring(i + 1));
								break;
							} catch (Exception e) {
								return (0);
							}
						}
					}
					if (id.substring(i + 1).startsWith("0")) // ho trovato un id numerico di tipo 01, 001 etc
						return (0);
				}

		return lastNumberID;
	}

	// TODO verificare eliminazione urn come prefisso dell'id
	protected String getParentID(Node nodo) {
		String parentID = null;
		nodo = nodo.getParentNode();
		while (nodo != null) {
			if (nodo.getAttributes() != null)
				if (nodo.getAttributes().getNamedItem("id") != null)
					if (!nodo.getAttributes().getNamedItem("id").getNodeValue().startsWith("urn"))
						parentID = nodo.getAttributes().getNamedItem("id").getNodeValue();
			if (parentID != null)
				break;
			nodo = nodo.getParentNode();
		}
		return parentID;
	}

	/*
	 * Determina la posizione di un elemento relativamente al fratello
	 * precedente nel suo sotto albero
	 */

	protected String getPosizioneRelativaElemento(Node node) {
		int posizione = 1; // L'elemento corrente con quel nome e' almeno il
							// primo
		String pivotNodeName = node.getNodeName();
		node = node.getPreviousSibling();
		while (node != null) {
			if (pivotNodeName.equals(node.getNodeName())) {
				posizione = getLastNumberIDElement(node) + 1;
				break;
			}
			node = node.getPreviousSibling();
		}
		
		if (pivotNodeName.equalsIgnoreCase("el")) {
		   return increaseLetter(getLastLetterIDElement(node));  //+1
	    }
		
		return ""+posizione;
	}
    
	private String increaseLetter(String letter){
		if(letter.equals(""))
			return "a";
		return(UtilLang.fromNumberToLetter21(""+(UtilLang.fromLetterToNumber21(letter)+1)));
	}
	
	private boolean isLiteralId(Node node){
		if(node.getNodeName().equals("el"))
			return true;
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	
	// --- modifica di TOMMASO per mantenere l'unicita' degli ID
	// aggiungendo bis, ter etc..

	/*
	 * Determina il pos dell'id di un elemento relativamente a tutti i suoi
	 * fratelli
	 */

	protected String getPosRelativaFratelli(Node node) {
		Vector brothers = getBrothers(node);
		String pos = "";

		if (brothers != null) {
			
			int currentIdx = getCurrentIndex(node, brothers);
			
			// previousNumIdx: l'indice del num precedente nella lista dei fratelli
			int previousNumIdx = getPreviousNumIndex(currentIdx, brothers);
			// previousNum: num precedente nella lista dei fratelli
			int previousNum = getPreviousNum(currentIdx, brothers);
			// nextNumIdx: l'indice del num successivo nella lista dei fratelli
			int nextNumIdx = getNextNumIndex(currentIdx, brothers);
			// nextNum: num successivo nella lista dei fratelli
			int nextNum = getNextNum(currentIdx, brothers);

			// previousNumberedId:l'ultimo numero usato per marcare gli id
			int previousNumberedId = getPreviousNumberedId(currentIdx, brothers);
			// previousNumberedIdIdx:indice dell'ultimo id marcato
			int previousNumberedIdIdx = getPreviousNumberedIdIndex(currentIdx, brothers);
			
			if (previousNumIdx != -1) { // c'e' un elemento precedente con un num settato
				if (nextNumIdx != -1) { // currentIdx e' compreso fra due elementi numerati
					if (nextNum - previousNum > currentIdx - previousNumIdx){ // currentIdx-previousNumberIdx ci sono numeri disponibili
						pos = "" + (previousNum + (currentIdx - previousNumIdx)); // (currentIdx-previousNumberIdx+1);
						if(isLiteralId(node))
					    	pos = UtilLang.fromNumberToLetter21(pos);
					}
					else{
						String previousNumberedIdString = ""+previousNumberedId;
						if(isLiteralId(node))
					    	previousNumberedIdString = UtilLang.fromNumberToLetter21(previousNumberedIdString);
						pos =  previousNumberedIdString + bis[currentIdx - previousNumberedIdIdx - 1];
					}
				} else {
					// pos=""+getPosizioneRelativaElemento(node);
					pos = "" + (previousNum + (currentIdx - previousNumIdx)); 
					if(isLiteralId(node))
				    	pos = UtilLang.fromNumberToLetter21(pos);
					// logger.debug(" caso di nextNumberIdx = - 1: pos = "+pos);
				}
			} else { // non ci sono elementi precedenti con num settato
				if (previousNumberedIdIdx != -1) { // c'e' un elemento precedente con un numero  sull'id (devo andare avanti con bis, ter)
					if (nextNumIdx != -1) { // currentIdx e' compreso fra due elementi numerati
						if (nextNum - previousNumberedId > currentIdx - previousNumIdx){ // currentIdx-previousNumberIdx) ci sono numeri disponibili
							pos = "" + (previousNumberedId + (currentIdx - previousNumberedIdIdx)); // (currentIdx-previousNumberIdx+1);
						    if(isLiteralId(node))
						    	pos = UtilLang.fromNumberToLetter21(pos);
						}
						else{
							pos = "" + previousNumberedId + bis[currentIdx - previousNumberedIdIdx - 1];
						}
					} else {
						// pos=""+getPosizioneRelativaElemento(node);
						pos = "" + (previousNumberedId + (currentIdx - previousNumberedIdIdx)); //
						if(isLiteralId(node))
						   pos = UtilLang.fromNumberToLetter21(pos);
						// logger.debug(" caso di nextNumberIdx = - 1: pos = "+pos);
					}
				} 
				else {     // non c'e' un elemento precedente ne' col num ne' con  l'id numerato
					if (nextNum > currentIdx + 1 || nextNum == -1){
						pos = "" + (currentIdx + 1);
						if(isLiteralId(node))
							   pos = UtilLang.fromNumberToLetter21(pos);
					}
					else
						pos = "0" + (currentIdx + 1);// zerozero[currentIdx];
				}
			}
		}
		return (pos);
	}

	protected Vector getBrothers(Node node) {
		NodeList brothers;
		Vector fratelli = new Vector();

		if (node != null && node.getParentNode() != null) {
			brothers = node.getParentNode().getChildNodes();
		} else
			return (null);

		for (int i = 0; i < brothers.getLength(); i++) {
			if (brothers.item(i).getNodeName().equals(node.getNodeName()))
				fratelli.add(brothers.item(i));
		}
		return (fratelli);
	}

	/**
	 * Restituisce il num precedente nella lista dei fratelli
	 * 
	 * @param currentIdx
	 * @param brothers
	 * @return
	 */
	protected int getPreviousNum(int currentIdx, Vector brothers) {
		int i = getPreviousNumIndex(currentIdx, brothers);
		if (i != -1)
			return (getIntPositionByNum((Node) brothers.get(i)));
		return (-1);
	}

	/**
	 * Restituisce l'indice del num precedente nella lista dei fratelli
	 * 
	 * @param currentIdx
	 * @param brothers
	 * @return
	 */
	protected int getPreviousNumIndex(int currentIdx, Vector brothers) {
		for (int i = currentIdx - 1; i >= 0; i--) {
			if (getIntPositionByNum((Node) brothers.get(i)) != -1)
				return (i);
		}
		return (-1);
	}

	/**
	 * Restituisce il num successivo nella lista dei fratelli
	 * 
	 * @param currentIdx
	 * @param brothers
	 * @return
	 */
	protected int getNextNum(int currentIdx, Vector brothers) {
		int i = getNextNumIndex(currentIdx, brothers);
		if (i != -1)
			return (getIntPositionByNum((Node) brothers.get(i)));
		return (-1);
	}

	/**
	 * Restituisce l'indice del num successivo nella lista dei fratelli
	 * 
	 * @param currentIdx
	 * @param brothers
	 * @return
	 */
	protected int getNextNumIndex(int currentIdx, Vector brothers) {
		for (int i = currentIdx + 1; i < brothers.size(); i++) {
			if (getIntPositionByNum((Node) brothers.get(i)) != -1)
				return (i);
		}
		return (-1);
	}

	protected int getCurrentIndex(Node node, Vector brothers) {
		for (int i = 0; i < brothers.size(); i++) {
			if (node == brothers.get(i))
				return (i);
		}
		return (-1);
	}

	/**
	 * Restituisce l'ultimo numero usato per marcare gli id
	 * 
	 * @param currentIdx
	 * @param brothers
	 * @return
	 */
	protected int getPreviousNumberedId(int currentIdx, Vector brothers) {
		int i = getPreviousNumberedIdIndex(currentIdx, brothers);
		int ret =-1;
		if (i != -1){	
			if((ret=getLastNumberIDElementII((Node) brothers.get(i)))==0)
				ret= UtilLang.fromLetterToNumber21(getLastLetterIDElementII((Node) brothers.get(i)));
		}
		return (ret);
	}

	protected int getPreviousNumberedIdIndex(int currentIdx, Vector brothers) {
		for (int i = currentIdx - 1; i >= 0; i--) {
			if (getLastNumberIDElementII((Node) brothers.get(i)) != 0 || !getLastLetterIDElementII((Node) brothers.get(i)).equals(""))
				return (i);
		}
		return (-1);
	}

	protected int getPosizioneAssolutaElemento(Node node) {
		int posizione = 0;
		String nodeName = node.getNodeName();

		NodeList listaNodi = doc.getElementsByTagName(nodeName);
		int i = 0;
		for (i = 0; i < listaNodi.getLength(); i++) {
			if (listaNodi.item(i) == node) {
				posizione = i + 1;
				break;
			}
		}
		// modifica: adattamento per far corrispondere le lettere degli
		// elelmenti el alla loro posizione nell'alfabeto us (non verificato il
		// caso posAssolutaElemento)
		
		// FIXME non funzionerà piu' con i nuovi id fatti a lettere 
		
		if (nodeName.equalsIgnoreCase("el")) {
			if (posizione % 26 > 9 && posizione % 26 <= 20)
				posizione += 2;
			else if (posizione % 26 > 20)
				posizione += 5;
		}
		return posizione;
	}

	protected boolean isDigit(char c) {
		boolean check = false;
		switch (c) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			check = true;
			break;
		default:
			check = false;
			break;
		}
		return check;
	}

	protected boolean isIDNodeToBeHierachical(Node nodo) {
		boolean hierarchical = false;
		int elemType = getElementType(nodo);
		switch (elemType) {
		case LIBRO:
		case PARTE:
		case TITOLO:
		case CAPO:
		case SEZIONE:
		case COMMA:
		case LETTERA:
		case NUMERO:
		case ELENCO_PUNT:
		case RIF:
		case VIR:
		case NOT_AVV_ALT:
			hierarchical = true;
			break;
		case ARTICOLO:
			if (isElementWithinModifica(nodo))
				hierarchical = true;
			else
				hierarchical = false;
			break;
		case PREAMBOLO:
		case EVENTO:
		case INL:
		case ORIGINALE:
		case ATTIVA:
		case PASSIVA:
		case GIURISPRUDENZA:
		case ANNESSO:
		case MOD:
		case SPAN:
		case OTHER:
			hierarchical = false;
			break;
		}
		return hierarchical;
	}

	protected int getElementType(Node n) {
		if (n.getNodeName().compareTo("libro") == 0)
			this.elementType = LIBRO;
		else if (n.getNodeName().compareTo("parte") == 0)
			this.elementType = PARTE;
		else if (n.getNodeName().compareTo("titolo") == 0)
			this.elementType = TITOLO;
		else if (n.getNodeName().compareTo("capo") == 0)
			this.elementType = CAPO;
		else if (n.getNodeName().compareTo("sezione") == 0)
			this.elementType = SEZIONE;
		else if (n.getNodeName().compareTo("articolo") == 0)
			this.elementType = ARTICOLO;
		else if (n.getNodeName().compareTo("comma") == 0)
			this.elementType = COMMA;
		else if (n.getNodeName().compareTo("el") == 0)
			this.elementType = LETTERA;
		else if (n.getNodeName().compareTo("en") == 0)
			this.elementType = NUMERO;
		else if (n.getNodeName().compareTo("ep") == 0)
			this.elementType = ELENCO_PUNT;

		else if (n.getNodeName().compareTo("annesso") == 0)
			this.elementType = ANNESSO;
		// else if (n.getNodeName().compareTo("preambolo" )==0) this.elementType
		// = PREAMBOLO;
		else if (n.getNodeName().compareTo("preambolo") == 0)
			this.elementType = OTHER;

		// else if (n.getNodeName().compareTo("rif" )==0) this.elementType =
		// RIF;
		// else if (n.getNodeName().compareTo("a" )==0) this.elementType = RIF;

		// 
		else if (n.getNodeName().compareTo("rif") == 0)
			this.elementType = OTHER;
		else if (n.getNodeName().compareTo("a") == 0)
			this.elementType = OTHER;
		else if (n.getNodeName().compareTo("mrif") == 0)
			this.elementType = OTHER;

		else if (n.getNodeName().compareTo("mod") == 0)
			this.elementType = MOD;
		else if (n.getNodeName().compareTo("virgolette") == 0)
			this.elementType = VIR;

		else if (n.getNodeName().compareTo("nota") == 0)
			this.elementType = NOT_AVV_ALT;
		else if (n.getNodeName().compareTo("avvertenza") == 0)
			this.elementType = NOT_AVV_ALT;
		else if (n.getNodeName().compareTo("altro") == 0)
			this.elementType = NOT_AVV_ALT;

		else if (n.getNodeName().compareTo("evento") == 0)
			this.elementType = EVENTO;

		else if (n.getNodeName().compareTo("originale") == 0)
			this.elementType = ORIGINALE;
		else if (n.getNodeName().compareTo("attiva") == 0)
			this.elementType = ATTIVA;
		else if (n.getNodeName().compareTo("passiva") == 0)
			this.elementType = PASSIVA;
		else if (n.getNodeName().compareTo("giurisprudenza") == 0)
			this.elementType = GIURISPRUDENZA;

		else if (n.getNodeName().compareTo("h:span") == 0)
			this.elementType = SPAN;
		else if (n.getNodeName().compareTo("h:br") == 0)
			this.elementType = INL;
		else if (n.getNodeName().compareTo("h:hr") == 0)
			this.elementType = INL;
		else if (n.getNodeName().compareTo("h:input") == 0)
			this.elementType = INL;
		else if (n.getNodeName().compareTo("h:b") == 0)
			this.elementType = INL;
		else if (n.getNodeName().compareTo("h:i") == 0)
			this.elementType = INL;
		else if (n.getNodeName().compareTo("h:u") == 0)
			this.elementType = INL;
		else if (n.getNodeName().compareTo("h:sub") == 0)
			this.elementType = INL;

		else if (n.getNodeName().compareTo("h:p") == 0)
			this.elementType = BLC;
		else if (n.getNodeName().compareTo("h:div") == 0)
			this.elementType = BLC;
		else if (n.getNodeName().compareTo("h:form") == 0)
			this.elementType = BLC;
		else
			this.elementType = OTHER;

		return this.elementType;
	}

	protected String getSiglaElemento(Node figlio) {
		String sigla;
		int elemType = getElementType(figlio);
		switch (elemType) {
		case LIBRO:
			sigla = "lib";
			break;
		case PARTE:
			sigla = "prt";
			break;
		case TITOLO:
			sigla = "tit";
			break;
		case CAPO:
			sigla = "cap";
			break;
		case SEZIONE:
			sigla = "sez";
			break;
		case ARTICOLO:
			sigla = "art";
			break;
		case COMMA:
			sigla = "com";
			break;
		case LETTERA:
			sigla = "let";
			break;
		case NUMERO:
			sigla = "num";
			break;
		case ELENCO_PUNT:
			sigla = "pun";
			break;
		case ANNESSO:
			sigla = "ann";
			break;
		case PREAMBOLO:
			sigla = "pre";
			break;
		case RIF:
			sigla = "rif";
			break;
		case MOD:
			sigla = "mod";
			break;
		case VIR:
			sigla = "vir";
			break;
		case NOT_AVV_ALT:
			sigla = "n";
			break;
		case EVENTO:
			sigla = "t";
			break;
		case ORIGINALE:
			sigla = "ro";
			break;
		case ATTIVA:
			sigla = "ra";
			break;
		case PASSIVA:
			sigla = "rp";
			break;
		case GIURISPRUDENZA:
			sigla = "rg";
			break;
		case INL:
			sigla = "inl";
			break;
		case SPAN:
			sigla = "h";
			break;
		case OTHER:
			sigla = figlio.getNodeName().toLowerCase().substring(0,3);
			break;
		default:
			sigla = figlio.getNodeName().toLowerCase().substring(0,3);;
			break;
// 20/03/2006 sostituisce:			
//		case OTHER:
//			sigla = "";
//			break;
//		default:
//			sigla = "";
//			break;
		}

		if (sigla.length() == 0) {
			// Se la sigla e' vuota allora si considerano i primi 3 o 2 o 1
			// carattere
			int lastSiglaIndex = (figlio.getNodeName().length() > 3 ? 3 : figlio.getNodeName().length());
			sigla = figlio.getNodeName().substring(0, lastSiglaIndex);
			sigla = sigla.replaceAll("\\W", "");
		}

		return sigla;
	}

	/**
	 * Controlla se il nodo e' contenuto in un certo elemento
	 * 
	 * @param node nodo da verificare
	 * @param containerName nome dell'eventuale elemento contenitore
	 */
	protected boolean isContained(Node node, String containerName) {
		if (UtilDom.findParentByName(node, containerName) != null)
			return true;
		else
			return false;
	}

	/*
	 * Se l'elemento e' dentro una Modifica (cio? dentro le virgolette) la sua
	 * numerazione non va aggiornata
	 */
	protected boolean isElementWithinModifica(Node figlio) {
		Node padre = UtilDom.findParentByName(figlio, "virgolette");
		// Attenzione: la findParentByName restitisce anche il nodo stesso
		// se siamo su un nodo che ha lo stesso nome del padre da ricercare
		// Es: se siamo su "virgolette" e cerco il padre piu' prossimo di
		// tipo "virgolette", viene restituito il nodo stesso, quindi si
		// deve controllare che il nodo restituito non sia quello corrente
		// (padre != figlio)
		if (padre != null && padre != figlio)
			return true;
		else
			return false;
	}

}

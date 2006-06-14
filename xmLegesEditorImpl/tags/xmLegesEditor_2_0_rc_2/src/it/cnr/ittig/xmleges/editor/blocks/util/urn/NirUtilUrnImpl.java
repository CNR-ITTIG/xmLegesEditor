package it.cnr.ittig.xmleges.editor.blocks.util.urn;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.lang.UtilLang;
import it.cnr.ittig.xmleges.editor.services.autorita.Autorita;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.Provvedimenti;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilDom</code>.</h1>
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
 * 
 * @see
 * @version 1.0
 */
public class NirUtilUrnImpl implements NirUtilUrn, Loggable, Serviceable {
	Logger logger;

	Provvedimenti p;

	Autorita a;

	DocumentManager documentManager;
	
	protected String bis[] = { "bis", "ter", "quater", "quinquies", "sexies", "septies", "octies", "novies", "decies", "undecies", "duodecies", "terdecies",
			"quaterdecies", "quinquiesdecies", "sexiesdecies", "septiesdecies", "octiesdecies", "noviesdecies", "venies", "duovenies", "tervenies",
			"quatervenies", "quinquiesvenies", "sexiesvenies", "septiesvenies", "octiesvenies", "noviesvenies" };


	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		p = (Provvedimenti) serviceManager.lookup(Provvedimenti.class);
		a = (Autorita) serviceManager.lookup(Autorita.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}

	public String getUrnPartizione(String part) {

		if (part.equals("Libro"))
			return ("lib");

		if (part.equals("Parte"))
			return ("prt");

		if (part.equals("Titolo"))
			return ("tit");

		if (part.equals("Capo"))
			return ("cap");
		if (part.equals("Sezione"))
			return ("sez");
		if (part.equals("Articolo"))
			return ("art");
		if (part.equals("Comma"))
			return ("com");
		if (part.equals("Lettera"))
			return ("let");
		if (part.equals("Numero"))
			return ("num");
		return ("");
	}

	// //////////////////////////////////////////////////// NirUtilUrn Interface
	public String getFormaTestuale(Urn urn) {
		String formatestuale = "";
		boolean isNAppended = false;
		try {
			if ((urn.getPartizione() != null) && (!urn.getPartizione().equals(""))) {
				formatestuale += getFormaTestualeById(urn.getPartizione()) + " " + getPreposizioneForProvvedimento(urn.getProvvedimento()) + " ";
			}

			if (urn.getAllegati().size() > 0) {// Ci sono degli allegati
				Vector allegati = urn.getAllegati();
				for (int i = 0; i < allegati.size(); i++) {
					String[] st1 = allegati.get(i).toString().split(";"); // separa
																			// denominazione
																			// e
																			// titolo
					for (int j = 0; j < st1.length; j++) {
						if (j > 0)
							formatestuale += " (";
						StringTokenizer st2 = new StringTokenizer(st1[j], ".");
						while (st2.hasMoreTokens())
							formatestuale += st2.nextToken() + " ";
						if (j > 0) {
							formatestuale = formatestuale.substring(0, formatestuale.length() - 1);
							formatestuale += ") ";
						}
					}
					formatestuale += "e ";
				}
				formatestuale = formatestuale.substring(0, formatestuale.length() - 2) + getPreposizioneForProvvedimento(urn.getProvvedimento()) + " ";
			}

			if (urn.getProvvedimento().equals("decreto"))
				formatestuale += urn.manageDecreti(urn.getAutorita()) + " ";
			else if (urn.getProvvedimento().equals("costituzione"))
				return (formatestuale + " Costituzione");
			else if (urn.getProvvedimento().equals("codice.penale"))
				return (formatestuale + " Codice Penale");
			else if (urn.getProvvedimento().equals("codice.civile"))
				return (formatestuale + " Codice Civile");
			else if (urn.getProvvedimento().equals("codice.procedura.penale"))
				return (formatestuale + " Codice di Procedura Penale");
			else if (urn.getProvvedimento().equals("codice.procedura.civile"))
				return (formatestuale + " Codice di Procedura Civile");
			else
				formatestuale += p.getProvvedimentoByUrn(urn.getProvvedimento()).getUrnCitazione() + " ";

			if (!urn.getAutorita().get(0).equals("stato")) {
				if (urn.getAutorita().get(0).equals("comunita.europee"))
					formatestuale += "";// "comunita' europee";
				else if (urn.getAutorita().get(0).equals("unione.europea"))
					formatestuale += "unione europea";
				else {
					for (int i = 0; i < urn.getAutorita().size(); i++) {
						String urnautorita = urn.getAutorita().get(i).toString();
						StringTokenizer st = new StringTokenizer(urnautorita, ".");
						String urnparziale = "";
						String formatestualeautorita = null;
						while (st.hasMoreTokens()) {
							urnparziale += st.nextToken() + ".";
							formatestualeautorita = a.getNomeIstituzioneFromUrnIstituzione(urnparziale.substring(0, urnparziale.length() - 1));
							if (formatestualeautorita != null)
								break;
						}
						if (urn.getAutorita().size() > 1) { // autorit?
															// multiple:
							if (i < urn.getAutorita().size() - 2) // <
																	// penultima
								formatestuale += formatestualeautorita + ", del ";
							else if (i == urn.getAutorita().size() - 2) // penultima
								formatestuale += formatestualeautorita + " e del ";
							else
								// ultima
								formatestuale += formatestualeautorita;
						} else
							formatestuale += formatestualeautorita;
					}
				}
			}
			formatestuale += " ";
			if (urn.getDate().size() > 0)
				for (int i = 0; i < urn.getDate().size(); i++)
					formatestuale += urn.getExtendedDate().get(i).toString() + ", ";
			if (urn.getDate().size() > 0)
				formatestuale = formatestuale.substring(0, formatestuale.length() - 2);
			if (urn.getNumeri().size() > 0) {

				for (int i = 0; i < urn.getNumeri().size(); i++) {
					if (!urn.getNumeri().get(i).toString().equals("nir-1")) {
						if (!isNAppended) {
							isNAppended = true;
							formatestuale += ", n. ";
						}
						formatestuale += urn.getNumeri().get(i).toString() + ", ";

					}

				}
			}
			if ((urn.getNumeri().size() > 0) && (isNAppended))
				formatestuale = formatestuale.substring(0, formatestuale.length() - 2);
			return formatestuale;

		} catch (Exception e) {
			return (formatestuale);
		}
	}

	private String getPreposizioneForProvvedimento(String urnProvvedimento) {
		if (urnProvvedimento == null)
			return "";
		if (urnProvvedimento.startsWith("decreto") || urnProvvedimento.startsWith("codice"))
			return "del";
		else
			return "della";
	}

	/**
	 * forma testuale completa
	 */
	public String getFormaTestualeById(String id) {
		String formatestuale = "";
		try {
			StringTokenizer st = new StringTokenizer(id, "-");
			while (st.hasMoreTokens())
				formatestuale += getPartizioneFromIdToken(st.nextToken()) + ", ";
			formatestuale = formatestuale.trim();
			return formatestuale.substring(0, formatestuale.length() - 1); // toglie
																			// l'ultima
																			// virgola
		} catch (Exception e) {
			return formatestuale;
		}
	}

	/**
	 * elimina dalla forma testuale la parte comune fra partizione citata e
	 * partizione da cui si cita; es: cito art1-com1 da art1, scrivo solo comma
	 * 1
	 */
	public String getFormaTestualeById(String callingId, String referredId) {
		if (callingId == null)
			return (getFormaTestualeById(referredId));
		String formatestuale = "";
		try {
			String[] tokenizedCallId = callingId.split("-");
			String[] tokenizedrefId = referredId.split("-");
			boolean addedToken = false;
			for (int i = 0; i < tokenizedrefId.length; i++) {
				if (addedToken || i == tokenizedrefId.length - 1 || (i < tokenizedCallId.length && !tokenizedrefId[i].equals(tokenizedCallId[i]))) {
					formatestuale += getPartizioneFromIdToken(tokenizedrefId[i]) + ", ";
					addedToken = true;
				}
			}
			formatestuale = formatestuale.trim();
			return formatestuale.substring(0, formatestuale.length() - 1);
		} catch (Exception e) {
			return formatestuale;
		}
	}

	private String getPartizioneFromIdToken(String idToken) {
		String partizione = "";
		if (idToken.startsWith("ann")) {
			Node annesso = documentManager.getDocumentAsDom().getElementById(idToken);
			if (annesso != null) {
				Node testata = UtilDom.findDirectChild(annesso, "testata");
				partizione += UtilDom.getRecursiveTextNode(UtilDom.findDirectChild(testata, "denAnnesso"));
			}
		} else {
			partizione += getPartizioneFromUrn(idToken.substring(0, 3)) + " "
					+ getNumeroFromUrn(idToken.substring(0, 3), idToken.substring(3, idToken.length()));
		}
		return partizione;
	}

	/**
	 * Data la stringa rappresentante la urn di una partizione restituisce la
	 * sua descrizione testuale
	 */
	private String getPartizioneFromUrn(String urn) {

		if (urn.equals("lib"))
			return ("libro");
		if (urn.equals("prt"))
			return ("parte");
		if (urn.equals("tit"))
			return ("titolo");
		if (urn.equals("cap"))
			return ("capo");
		if (urn.equals("sez"))
			return ("sezione");
		if (urn.equals("art"))
			return ("articolo");
		if (urn.equals("com"))
			return ("comma");
		if (urn.equals("let"))
			return ("lettera");
		if (urn.equals("num"))
			return ("numero");
		// if (urn.equals("ann"))
		// return ("allegato");
		// // metterci una funzione che recupera la denominazione dell'annesso
		return ("");
	}

	/**
	 * Data la stringa rappresentante la urn e la stringa del numero restituisce
	 * il numero o la corrispondente lettera nel caso di partizioni di tipo
	 * lettera
	 */
	private String getNumeroFromUrn(String urn, String numero) {
		String ret = null;
		if (!urn.equals("let"))
			ret = separateBisTer(numero);
		else {
			numero = separateBisTer(numero);
			String num = numero;
			String bis = "";
			if (numero.indexOf('-') != -1) {
				num = numero.substring(0, numero.indexOf('-'));
				bis = numero.substring(numero.indexOf('-'));
			}
			ret = num + bis + ")";
		}
		return ret;
	}

	
	/**
	 * Data la stringa rappresentante il numero della urn; separa gli eventuali
	 * bis, ter etc dal numero con un "-"
	 * 
	 * @param s
	 * @return
	 */
	private String separateBisTer(String s) {
		int sep=0;
		if(s!=null){
			for(int i=0; i<bis.length; i++){
				   if((sep=s.indexOf(bis[i]))!=-1){
					   s=s.substring(0,sep)+"-"+s.substring(sep);
					   return s;
				   }
				}
		}
		return s;
	}
	
}

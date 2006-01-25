package it.cnr.ittig.xmleges.editor.services.util.urn;

import it.cnr.ittig.xmleges.editor.services.autorita.Autorita;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.Provvedimenti;

import java.text.ParseException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Classe per la gestione delle urn.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class Urn {

	Vector autorita = new Vector();

	String provvedimento = ""; // specificazioni separata da ;

	Vector date = new Vector();

	Vector numeri = new Vector();

	Vector allegati = new Vector(); // separati da :, specificazioni separata da

	// ;

	String versioneProvDiModifica = ""; // obbligatoria (se c'??? versione)

	String versioneVigenza = ""; // separate da ;

	Vector comunicati = new Vector(); // preceduti da * diviso in tipo; data

	// (*rettifica;2002-11-11*errata.corrige;2002-11-12)
	String partizione = ""; // #

	String formatestuale = "";

	public Urn() {
		init();
	}

	public Urn(String urn) throws ParseException {
		parseUrn(urn);
	}

	public void parseUrn(String urn) throws ParseException {

		int foundTokens;
		int numerotoken;

		init();

		String[] stPartizioni = urn.split("#");
		foundTokens = stPartizioni.length;
		if (foundTokens > 1) { // C'e' almeno una partizione
			urn = stPartizioni[0];
			setPartizione(stPartizioni[1]);
		}

		String[] stComunicati = urn.split("\\*");
		foundTokens = stComunicati.length;
		if (foundTokens > 1) { // Ci sono dei comunicati
			urn = stComunicati[0];
			for (int k = 1; k < foundTokens; k++)
				comunicati.add(stComunicati[k]);
		}

		String[] stUrn = urn.split(":");
		foundTokens = stUrn.length;
		for (int i = 0; i < foundTokens; i++) {
			if ((i == 0) && (!stUrn[i].equals("urn"))) {
				throw new ParseException("Urn parse error", i);
			} else if ((i == 1) && (!stUrn[i].equals("nir"))) {
				throw new ParseException("Urn parse error", i);
			} else if (i == 2) {
				String urnAutorita = stUrn[i];
				String[] stAutorita = urnAutorita.split("\\+");
				for (int k = 0; k < stAutorita.length; k++) {
					addAutorita(stAutorita[k]);
				}
			} else if (i == 3) {
				setProvvedimento(stUrn[i]);
			} else if (i == 4) {
				String dataenumeroeversione = stUrn[i];
				String[] stDataNumeroeVersioni = dataenumeroeversione.split("@");
				String dataenumero = "";
				numerotoken = stDataNumeroeVersioni.length;
				dataenumero = stDataNumeroeVersioni[0];
				if (numerotoken == 2) { // E' presente una versione
					versioneProvDiModifica = stDataNumeroeVersioni[1];
				}

				String[] stDataNumero = dataenumero.split(";");
				numerotoken = stDataNumero.length;
				if (numerotoken != 2)
					throw new ParseException("Urn parse error", i);
				else {
					addData(stDataNumero[0]);
					addNumero(stDataNumero[1]);
				}
			} else if (i == 5) { // Esistono degli allegati
				String allegatieversione = stUrn[i];
				String[] stAllegatiVersioni = allegatieversione.split("@");
				numerotoken = stAllegatiVersioni.length;
				if (numerotoken == 2) // E' presente sia una lista di allegati
					// che una versione
					versioneProvDiModifica = stAllegatiVersioni[1];

				String allegatiString = stAllegatiVersioni[0];
				String[] stAllegati = allegatiString.split(":");
				for (int k = 0; k < stAllegati.length; k++)
					allegati.add(stAllegati[k]);
			}
		}
	}

	public Vector getComunicati() {
		return comunicati;
	}

	public Vector getAllegati() {
		return allegati;
	}

	public String getVersione() {
		return versioneProvDiModifica;
	}

	public void setFormaTestuale(String formatestuale) {
		this.formatestuale = formatestuale;
	}

	public String getFormaTestuale() {
		return (formatestuale);
	}

	/**
	 * Data la descrizione testuale di una partizione restituisce la sua urn
	 */
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

	/**
	 * Data la stringa rappresentante la urn di una partizione restituisce la
	 * sua descrizione testuale
	 */
	public String getPartizioneFromUrn(String urn) {

		if (urn.equals("lib"))
			return ("Libro");
		if (urn.equals("prt"))
			return ("Parte");
		if (urn.equals("tit"))
			return ("Titolo");
		if (urn.equals("cap"))
			return ("Capo");
		if (urn.equals("sez"))
			return ("Sezione");
		if (urn.equals("art"))
			return ("Articolo");
		if (urn.equals("com"))
			return ("Comma");
		if (urn.equals("let"))
			return ("Lettera");
		if (urn.equals("num"))
			return ("Numero");

		return ("");
	}

	public void setVersione(String versione) {
		this.versioneProvDiModifica = versione;
	}

	public void init() {
		autorita.clear();
		provvedimento = null;
		date.clear();
		numeri.clear();
		allegati.clear();
		versioneProvDiModifica = null;
		versioneVigenza = null;
		comunicati.clear();
		partizione = null;
		formatestuale = "";
	}

	/**
	 * Restituisce le autorit&agrave; correttamente formattate separate da
	 * <code>+</code> (pi&ugrave;).
	 * 
	 * @return autorit&agrave;
	 */
	public Vector getAutorita() {
		return (this.autorita);
	}

	// specificazioni separata da ;
	public void addAutorita(String autorita) {
		this.autorita.addElement(autorita);
	}

	/**
	 * Restituisce il provvedimento.
	 * 
	 * @return provvedimento
	 */
	public String getProvvedimento() {
		return this.provvedimento;
	}

	/**
	 * Imposta il provvedimento.
	 * 
	 * @param provvedimento provvedimento
	 */
	public void setProvvedimento(String provvedimento) {
		this.provvedimento = provvedimento;
	}

	/**
	 * Restituisce le date correttamente formattate separate da <code>,</code>
	 * (virgola).
	 * 
	 * @return date correttamente formattate
	 */
	public Vector getDate() {
		return date;
	}

	/**
	 * Aggiunge una data (formato AAAA-MM-GG).
	 * 
	 * @param data data
	 */
	public void addData(String data) {
		this.date.addElement(data);
	}

	/**
	 * Setta una sola data (formato AAAA-MM-GG).
	 * 
	 * @param data data
	 */
	public void setUniqueData(String data) {
		this.date.removeAllElements();
		this.date.addElement(data);
	}

	/**
	 * Restituisce i numeri correttamente formattati separati da <code>,</code>
	 * (virgola).
	 * 
	 * @return numeri correttamente formattati
	 */
	public Vector getNumeri() {
		return numeri;
	}

	/**
	 * Aggiunge un numero.
	 * 
	 * @param numero numero
	 */
	public void addNumero(String numero) {
		this.numeri.addElement(numero);
	}

	/**
	 * Setta un numero.
	 * 
	 * @param numero numero
	 */
	public void setUniqueNumero(String numero) {
		this.numeri.removeAllElements();
		this.numeri.addElement(numero);
	}

	public String getPartizione() {
		return this.partizione;
	}

	public void setPartizione(String partizione) {
		this.partizione = partizione;
	}

	public void addAllegato(String allegato) {
		this.allegati.add(allegato);
	}

	public void addComunicato(String comunicato) {
		this.comunicati.add(comunicato);
	}

	public boolean hasPartizione() {
		return this.partizione != null && !"".equals(partizione.trim());
	}

	/**
	 * Verifica se &egrave; possibile generare una URN valida in funzione dei
	 * campi inseriti.
	 * 
	 * @return
	 */
	public boolean isValid() {
		return autorita.size() > 0 && provvedimento != null && date.size() > 0;
	}

	public synchronized String toString() {

		StringBuffer sb = new StringBuffer("urn:nir:");
		String autorita = "";

		for (int i = 0; i < getAutorita().size(); i++)
			autorita += getAutorita().get(i) + "+";
		try {
			// Tommaso Agnoloni: aggiunto questo controllo
			if (autorita.length() > 0)
				sb.append(autorita.substring(0, autorita.length() - 1));
			sb.append(':');
		} catch (StringIndexOutOfBoundsException e) {
		}

		if (getProvvedimento() != null && !"".equals(getProvvedimento().trim())) {
			sb.append(getProvvedimento());
			sb.append(':');
		}

		String dataProvvedimento = "";
		for (int i = 0; i < date.size(); i++)
			dataProvvedimento += getDate().get(i) + ",";
		try {
			sb.append(dataProvvedimento.substring(0, dataProvvedimento.length() - 1));
			sb.append(';');
		} catch (StringIndexOutOfBoundsException e) {
		}

		String numeroProvvedimento = "";
		for (int i = 0; i < numeri.size(); i++)
			numeroProvvedimento += getNumeri().get(i) + ",";
		try {
			sb.append(numeroProvvedimento.substring(0, numeroProvvedimento.length() - 1));
		} catch (StringIndexOutOfBoundsException e) {
		}

		if (allegati.size() > 0) {
			sb.append(':');
			for (int i = 0; i < allegati.size(); i++) {
				sb.append(allegati.get(i).toString());
				if (i < allegati.size() - 1)
					sb.append(':');
			}
		}

		if (versioneProvDiModifica != null && !"".equals(versioneProvDiModifica.trim())) {
			sb.append("@" + versioneProvDiModifica);
		}

		if (comunicati.size() > 0) { // Ci sono dei comunicati
			for (int i = 0; i < comunicati.size(); i++) {
				sb.append('*');
				sb.append(comunicati.get(i).toString());
			}
		}

		if (hasPartizione()) {
			sb.append('#');
			sb.append(getPartizione());
		}

		return sb.toString();
	}

	protected String toString(Vector v, char sep) {
		StringBuffer sb = new StringBuffer();
		Enumeration en = v.elements();
		boolean first = true;
		while (en.hasMoreElements()) {
			if (first)
				first = false;
			else
				sb.append(sep);
			sb.append(en.nextElement().toString());
		}
		return sb.toString();
	}

	public void fromUrnToFormaTestuale(Provvedimenti p, Autorita a) {
		String formatestuale = "";
		try {
			if (!partizione.equals("")) {
				StringTokenizer st = new StringTokenizer(partizione, "-");
				while (st.hasMoreTokens()) {
					String sottopartizione = st.nextToken();
					formatestuale += getPartizioneFromUrn(sottopartizione.substring(0, 3)) + " " + sottopartizione.substring(3, sottopartizione.length()) + " ";

				}
			}
			formatestuale += p.getProvvedimentoByUrn(provvedimento).getUrnCitazione();
			if (!autorita.get(0).equals("stato")) {
				for (int i = 0; i < autorita.size(); i++)
					formatestuale += a.getNomeIstituzioneFromUrnIstituzione(autorita.get(i).toString()) + " e ";
				if (autorita.size() > 1)
					formatestuale.substring(0, formatestuale.length() - 2);
			}
			if (date.size() > 0)
				formatestuale += "del " + date;
			setFormaTestuale(formatestuale);

		} catch (Exception e) {
			setFormaTestuale(formatestuale);
		}
	}

	public Vector getExtendedDate() {
		Vector extendedDate = new Vector();
		try {
			for (int i = 0; i < date.size(); i++) {
				StringTokenizer st = new StringTokenizer(date.get(i).toString(), "-");
				if (st.countTokens() > 1) {
					String aaaa = st.nextToken();
					String mm = st.nextToken();
					String gg = st.nextToken();
					if (gg.startsWith("0"))
						gg = gg.substring(1);
					switch (Integer.parseInt(mm)) {
					case 1: {
						extendedDate.add(gg + " " + "gennaio" + " " + aaaa);
						break;
					}
					case 2: {
						extendedDate.add(gg + " " + "febbraio" + " " + aaaa);
						break;
					}
					case 3: {
						extendedDate.add(gg + " " + "marzo" + " " + aaaa);
						break;
					}
					case 4: {
						extendedDate.add(gg + " " + "aprile" + " " + aaaa);
						break;
					}
					case 5: {
						extendedDate.add(gg + " " + "maggio" + " " + aaaa);
						break;
					}
					case 6: {
						extendedDate.add(gg + " " + "giugno" + " " + aaaa);
						break;
					}
					case 7: {
						extendedDate.add(gg + " " + "luglio" + " " + aaaa);
						break;
					}
					case 8: {
						extendedDate.add(gg + " " + "agosto" + " " + aaaa);
						break;
					}
					case 9: {
						extendedDate.add(gg + " " + "settembre" + " " + aaaa);
						break;
					}
					case 10: {
						extendedDate.add(gg + " " + "ottobre" + " " + aaaa);
						break;
					}
					case 11: {
						extendedDate.add(gg + " " + "novembre" + " " + aaaa);
						break;
					}
					case 12: {
						extendedDate.add(gg + " " + "dicembre" + " " + aaaa);
						break;
					}
					}
				} else
					extendedDate.add(st.nextToken());

			}
			return extendedDate;
		} catch (Exception e) {
			return null;
		}
	}

	public String manageDecreti(Vector autorita) {
		String descrizioneDecreto = "decreto del";
		// if ((autorita.size()>0) && (autorita.get(0).toString().length()>9) &&
		// (autorita.get(0).toString().substring(0,9).equals("ministero")))
		// descrizioneDecreto="decreto Ministeriale";
		return descrizioneDecreto;
	}

}

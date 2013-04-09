package it.cnr.ittig.xmleges.editor.services.xmleges.marker;

import it.cnr.ittig.services.manager.Service;

import java.io.File;
import java.io.InputStream;

/**
 * Servizio per l'attivazione del parser di struttura per la conversione di un
 * file testuale in xml.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface XmLegesMarker extends Service {

	/**
	 * Array di valori per presentare il testo nelle form per
	 * <code>TIPO_INPUT_VALORE</code>
	 */
	final public static String[] TIPO_INPUT = new String[] { "Html", "Txt" };

	/** Array di valori per il metodo <code>setTipoInput()</code> */
	final public static String[] TIPO_INPUT_VALORE = new String[] { "html", "txt" };

	/**
	 * Array di valori per presentare il testo nelle form per
	 * <code>TIPO_DTD_VALORE</code>
	 */
	final public static String[] TIPO_DTD = new String[] { "Completo", "Flessibile", "Base", "Dl"};

	final public static String[] TIPO_DTD_VALORE = new String[] { "completo", "flessibile", "base", "dl"};

	/**
	 * Array di valori per presentare il testo nelle form per
	 * <code>TIPO_DOC_VALORE</code>
	 */
	final public static String[] TIPO_DOC = new String[] { "Legge", "Legge Costituzionale", "Decreto Legge", "Decreto Legislativo", "Regio Decreto",
			"Decreto Presidente Repubblica", "Decreto Presidente Repubblica - non numerato", "Decreto Pres. Cons. Ministri",
			"Decreto Pres. Cons. Ministri - non numerato", "Decreto Ministeriale", "Decreto Ministeriale - non numerato", "Legge Regionale", 
			"Regolamento Regionale", "Regolamento", "Circolare", "Provvedimento", "Disegno di Legge", 
			"Disegno/Proposta di Legge regionale",
			"Documento NIR", "Provvedimento CNR" };

	//final public static String[] TIPO_DOC_VALORE = new String[] { "l", "lcost", "dl", "dlgs", "rd", "dpr", "dprNN", "dpcm", "dpcmNN", "dm", "dmNN", "lr", "rreg", "reg", "circ", "prov", "ddl", "crp", "nir", "cnr" };
	final public static String[] TIPO_DOC_VALORE = new String[] { "l", "lcost", "dl", "dlgs", "rd", "dpr", "dprNN", "dpcm", "dpcmNN", "dm", "dmNN", "lr", "rreg", "reg", "circ", "prov", "ddl", "ddlr", "nir", "cnr" };

	/**
	 * Array di valori per presentare il testo nelle form per
	 * <code>TIPO_COMMA_VALORE</code>
	 */
	final public static String[] TIPO_COMMA = new String[] { "Numerati", "Non numerati con spazi" };

	final public static String[] TIPO_COMMA_VALORE = new String[] { "0", "1" };

	/**
	 * Array di valori per presentare il testo nelle form per
	 * <code>TIPO_RUBRICA_VALORE</code>
	 */
	final public static String[] TIPO_RUBRICA = new String[] { "Su nuova linea", "Adiacente", "Adiacente senza 'Art.'", "Senza rubrica" };

	/** Array di valori per il metodo <code>setTipoRubrica()</code> */
	final public static String[] TIPO_RUBRICA_VALORE = new String[] { "0", "1", "2", "9" };

	/** Array di valori per il livello di logging per <code>LOGGER_VALORE</code> */
	final public static String[] LOGGER = new String[] { "Errore", "Avvertimenti", "Informazioni", "Debug" };

	/** Array di valori per il metodo <code>setTipoRubrica()</code> */
	final public static String[] LOGGER_VALORE = new String[] { "error", "warn", "info", "debug" };

	/**
	 * Reimposta i parametri nei loro valori di default.
	 */
	public void setDefault();

	/**
	 * Imposta il formato del documento. Il valore deve appartenere a
	 * <code>TIPO_INPUT_VALORE</code>.
	 * 
	 * @param format elemento di <code>TIPO_INPUT_VALORE</code>
	 * @throws XmLegesMarkerException se il parametro non &egrave; corretto
	 */
	public void setTipoInput(String format) throws XmLegesMarkerException;

	/**
	 * Imposta il tipo di DTD. Il valore deve appartenere a
	 * <code>TIPO_DTD_VALORE</code>.
	 * 
	 * @param dtd elemento di <code>TIPO_DTD_VALORE</code>
	 * @throws XmLegesMarkerException se il parametro non &egrave; corretto
	 */
	public void setTipoDtd(String dtd) throws XmLegesMarkerException;

	/**
	 * Imposta il tipo di documento. Il valore deve appartenere a
	 * <code>TIPO_DOC_VALORE</code>.
	 * 
	 * @param tipoDoc element di <code>TIPO_DOC_VALORE</code>
	 * @throws XmLegesMarkerException se il parametro non &egrave; corretto
	 */
	public void setTipoDoc(String tipoDoc) throws XmLegesMarkerException;

	/**
	 * Imposta il nome del documento se non presente in TIPO_DOC_VALORE.
	 * 
	 * @param tipoDocAltro nome del documento
	 */
	public void setTipoDocAltro(String tipoDocAltro);

	/**
	 * Imposta i tipo di commi. Il valore deve appartenere a
	 * <code>TIPO_COMMA_VALORE</code>.
	 * 
	 * @param tipoComma elemento di <code>TIPO_COMMA_VALORE</code>
	 * @throws XmLegesMarkerException se il parametro non &egrave; corretto
	 */
	public void setTipoComma(String tipoComma) throws XmLegesMarkerException;

	/**
	 * Imposta il tipo di rubriche. Il valore deve appartenere a
	 * <code>TIPO_RUBRICA_VALORE</code>.
	 * 
	 * @param tipoRubrica elemento di <code>TIPO_RUBRICA_VALORE</code>
	 * @throws XmLegesMarkerException se il parametro non &egrave; corretto
	 */
	public void setTipoRubrica(String tipoRubrica) throws XmLegesMarkerException;

	/**
	 * Indica se il testo ha le rubriche. Valido solo per commi non numerati.
	 * 
	 * @param testoConRubriche <code>true</code> se sono presenti
	 */
	public void setTestoConRubriche(boolean testoConRubriche);

	/**
	 * Imposta l'encoding del file.
	 * 
	 * @param encoding
	 */
	public void setEncoding(String encoding);

	/**
	 * Indica se deve essere attivato il controllo delle sequenze
	 * dell'articolato.
	 * 
	 * @param controlloSequenza <code>true</code> se deve essere attivato (default)
	 */
	public void setControlloSequenza(boolean controlloSequenza);

	/**
	 * Indica se deve essere usato il modulo del riconoscimento degli annessi.
	 * 
	 * @param modAnnessi <code>true</code> se deve essere attivato (default)
	 */
	public void setModuloAnnessi(boolean modAnnessi);

	/**
	 * Indica se deve essere usato il modulo del riconoscimento delle tabelle.
	 * 
	 * @param modTabelle <code>true</code> se deve essere attivato (default)
	 */
	public void setModuloTabelle(boolean modTabelle);

	/**
	 * Imposta il livello di informazioni durante l'analisi. Il valore deve
	 * appartenere a <code>LOGGER_VALORE</code>.
	 * 
	 * @param level elemento di <code>LOGGER_VALORE</code>
	 * @throws XmLegesMarkerException se il parametro non &egrave; corretto
	 */
	public void setLoggerLevel(String level) throws XmLegesMarkerException;

	/**
	 * Estrae il tipoDoc dal file <code>file</code>.
	 * 
	 * @param file file da cui estrarre il tipo
	 * @return tipo di documento
	 * @throws XmLegesMarkerException se avviene un errore durante l'analisi
	 */
	public InputStream parseAutoTipoDoc(File file) throws XmLegesMarkerException;
	
	/**
	 * Converte il file <code>file</code> in XML.
	 * 
	 * @param file file da convertire
	 * @return file XML
	 * @throws XmLegesMarkerException se avviene un errore durante l'analisi
	 */
	public InputStream parse(File file) throws XmLegesMarkerException;

	/**
	 * Restituisce lo stderr dell'esecuzione del parser di struttura. Se non ci
	 * sono stati errori restituisce <code>null</code>.
	 * 
	 * @return stringa contente l'errore oppure <code>null</code>.
	 */
	public String getError();
}

package it.cnr.ittig.xmleges.editor.blocks.autorita;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;
import it.cnr.ittig.xmleges.editor.services.autorita.Autorita;
import it.cnr.ittig.xmleges.editor.services.autorita.Istituzione;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.dom4j.Node;
import org.dom4j.io.DOMReader;
import org.w3c.dom.Document;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.autorita.Autorita</code>.</h1>
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
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class AutoritaImpl implements Autorita, Loggable, Serviceable, Configurable, Initializable {

	PreferenceManager preferenceManager;

	DOMReader domReader = new DOMReader();

	Logger logger;

	org.dom4j.Document registro;

	String dirRae;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		dirRae = UtilFile.getTempDirName() + File.separator + configuration.getChild("path").getValue();
		logger.debug("[AutoritaImpl - dirRae: " + dirRae + "]");
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		loadRae();
	}

	protected Logger getLogger() {
		return this.logger;
	}

	public String getDirRae() {
		return this.dirRae;
	}

	public void reloadRae() {
		registro = domReader.read(UtilXml.readXML(dirRae));
	}

	private Document getRaeFromPath() {
		try {
			File fileRae = new File(dirRae);
			if (fileRae.exists())
				return (UtilXml.readXML(dirRae));
			else
				return (null);
		} catch (Exception e) {
			return (null);
		}
	}

	private void loadRae() {
		Document reg;
		if ((reg = getRaeFromPath()) == null) { // se non c'e' in dirRae lo
												// prende dal componente
			reg = UtilXml.readXML(getClass().getResourceAsStream("rae.xml"));// se
																				// non
																				// c'e'
																				// lo
																				// legge
																				// dal
																				// componente
																				// e lo
																				// copia
																				// nel
																				// path
			/**
			 * TODO qui vanno create tutte le directory del path
			 */
			UtilFile.copyFile(getClass().getResourceAsStream("rae.xml"), dirRae);
			Properties props = preferenceManager.getPreferenceAsProperties("AutoritaActionImpl");
			props.setProperty("updated", UtilDom.getAttributeValueAsString(reg.getElementsByTagName("ELENCHI_RAE").item(0), "DATA"));
			preferenceManager.setPreference("AutoritaActionImpl", props);
			logger.debug("[AutoritaImpl: get Registro from component]");
		} else {
			logger.debug("[ AutoritaImpl: get Registro From dirRae]");
		}
		registro = domReader.read(reg);
		logger.debug("[AutoritaImpl:  opened Registro]");
	}

	public String[] getIstituzioniValide(Date data, int tipoIstituzione) {
		return (getIstituzioniValide(fromDateToString(data), tipoIstituzione));
	}

	public String[] getIstituzioniValide(String data, int tipoIstituzione) {
		Vector result = new Vector();
		result = getIstituzioniValideVect(data, tipoIstituzione);
		String[] ret = new String[result.size()];
		result.copyInto(ret);
		return (ret);
	}

	private Vector getIstituzioniValideVect(String data, int tipoIstituzione) {
		int i;
		Vector resultVect = new Vector();
		Node test;

		String xpath = "/ELENCHI_RAE/ELENCO_ISTITUZIONI/ISTITUZIONE[(./PERIODO/DATA_FINE>" + data
				+ " or string-length(./PERIODO/DATA_FINE/text())<3) and (./PERIODO/DATA_INIZIO<" + data
				+ " or string-length(./PERIODO/DATA_INIZIO/text())<3)]/NOME_UFFICIALE/text()";
		List result = registro.selectNodes(xpath);
		Iterator it = result.iterator();
		switch (tipoIstituzione) {
		case Autorita.ALL:
			while (it.hasNext()) {
				test = (Node) it.next();
				if (!resultVect.contains(test.getStringValue()))
					resultVect.addElement(test.getStringValue());
			}
			break;
		case Autorita.AUTHORITY:
			while (it.hasNext()) {
				test = (Node) it.next();
				if (test.getStringValue().toLowerCase().startsWith("autorit"))
					if (!resultVect.contains(test.getStringValue()))
						resultVect.addElement(test.getStringValue());
			}
			break;
		case Autorita.MINISTERO:
			while (it.hasNext()) {
				test = (Node) it.next();
				if (test.getStringValue().toLowerCase().startsWith("minist"))
					if (!resultVect.contains(test.getStringValue()))
						resultVect.addElement(test.getStringValue());
			}
			break;
		case Autorita.REGIONE:
			while (it.hasNext()) {
				test = (Node) it.next();
				if (test.getStringValue().toLowerCase().startsWith("region"))
					if (!resultVect.contains(test.getStringValue()))
						resultVect.addElement(test.getStringValue());
			}
			break;
		case Autorita.PROVINCIA:
			while (it.hasNext()) {
				test = (Node) it.next();
				if (test.getStringValue().toLowerCase().startsWith("provin"))
					if (!resultVect.contains(test.getStringValue()))
						resultVect.addElement(test.getStringValue());
			}
			break;

		}
		Collections.sort(resultVect);
		return (resultVect);
	}

	public Istituzione[] getIstituzioniValideFromProvvedimenti(Date data, String RegExp) {
		return (getIstituzioniValideFromProvvedimenti(fromDateToString(data), RegExp));
	}

	public Istituzione[] getIstituzioniValideFromProvvedimenti(String data, String RegExp) {
		if(RegExp!=null){
			Vector ist = new Vector();
			ist = getIstituzioniValideFromProvvedimentiVect(data, RegExp);
			Istituzione[] ret = new Istituzione[ist.size()];
			ist.copyInto(ret);
			return (ret);
		}
		return null;
	}

	private Vector getIstituzioniValideFromProvvedimentiVect(String data, String RegExp) {

		Vector risultati = new Vector();
		Node nodoIstituzione;

		String xpath = "/ELENCHI_RAE/ELENCO_ISTITUZIONI/ISTITUZIONE[(./PERIODO/DATA_FINE>" + data
				+ " or string-length(./PERIODO/DATA_FINE/text())<3) and (./PERIODO/DATA_INIZIO<" + data + " or string-length(./PERIODO/DATA_INIZIO/text())<3)]";
		List result = registro.selectNodes(xpath);
		Iterator it = result.iterator();
		java.util.Date datadisp = parseDateString(data);
		while (it.hasNext()) {
			nodoIstituzione = (Node) it.next();
			Istituzione istcorrente = new Istituzione();
			Istituzione subist1 = new Istituzione();
			Istituzione subist2 = new Istituzione();

			istcorrente.setUrn(nodoIstituzione.selectSingleNode("./NOME_STANDARD/text()").getStringValue());
			istcorrente.setNome(nodoIstituzione.selectSingleNode("./NOME_UFFICIALE/text()").getStringValue());

			if (nodoIstituzione.selectSingleNode("./PERIODO/DATA_INIZIO/text()") != null)
				istcorrente.setDatainizio(parseDateString(nodoIstituzione.selectSingleNode("./PERIODO/DATA_INIZIO/text()").getStringValue()));
			else
				istcorrente.setDatainizio(parseDateString(null));

			if (nodoIstituzione.selectSingleNode("./PERIODO/DATA_FINE/text()") != null)
				istcorrente.setDatafine(parseDateString(nodoIstituzione.selectSingleNode("./PERIODO/DATA_FINE/text()").getStringValue()));
			else
				istcorrente.setDatafine(parseDateString(null));

			if (isValidDate(istcorrente.getDatainizio(), istcorrente.getDatafine(), datadisp)) {

				if (nodoIstituzione.selectSingleNode("./LIVELLO1/NOME/text()") != null) {
					subist1.setUrn(nodoIstituzione.selectSingleNode("./LIVELLO1/NOME/text()").getStringValue());
					if (nodoIstituzione.selectSingleNode("./LIVELLO1/DATA_INIZIO/text()") != null)
						subist1.setDatainizio(parseDateString(nodoIstituzione.selectSingleNode("./LIVELLO1/DATA_INIZIO/text()").getStringValue()));
					else
						subist1.setDatainizio(parseDateString(null));
					if (nodoIstituzione.selectSingleNode("./LIVELLO1/DATA_FINE/text()") != null)
						subist1.setDatafine(parseDateString(nodoIstituzione.selectSingleNode("./LIVELLO1/DATA_FINE/text()").getStringValue()));
					else
						subist1.setDatafine(parseDateString(null));
					istcorrente.addSottoIstituzione(subist1);
				}

				if (nodoIstituzione.selectSingleNode("./LIVELLO2/NOME/text()") != null) {
					subist1.setUrn(nodoIstituzione.selectSingleNode("./LIVELLO2/NOME/text()").getStringValue());
					if (nodoIstituzione.selectSingleNode("./LIVELLO2/DATA_INIZIO/text()") != null)
						subist1.setDatainizio(parseDateString(nodoIstituzione.selectSingleNode("./LIVELLO2/DATA_INIZIO/text()").getStringValue()));
					else
						subist1.setDatainizio(parseDateString(null));
					if (nodoIstituzione.selectSingleNode("./LIVELLO2/DATA_FINE/text()") != null)
						subist1.setDatafine(parseDateString(nodoIstituzione.selectSingleNode("./LIVELLO2/DATA_FINE/text()").getStringValue()));
					else
						subist1.setDatafine(parseDateString(null));
					((Istituzione) (istcorrente.getSottoIstituzioni().get(0))).addSottoIstituzione(subist2);
				}

				if (((RegExp.substring(0, 1).equals("=")) && (istcorrente.getUrn().matches(RegExp.substring(1))))
						|| ((RegExp.substring(0, 1).equals("!")) && (!(istcorrente.getUrn().matches(RegExp.substring(1)))))) {

					if (risultati.size() == 0)
						risultati.add(istcorrente);
					else {
						if (!(((Istituzione) (risultati.get(risultati.size() - 1))).getUrn().equals(istcorrente.getUrn())))
							risultati.add(istcorrente);
						else {
							int countsublevel = ((Istituzione) (risultati.get(risultati.size() - 1))).getSottoIstituzioni().size();
							if ((countsublevel == 1) && (isValidDate(subist1.getDatainizio(), subist1.getDatafine(), datadisp)))
								((Istituzione) (risultati.get(risultati.size() - 1))).addSottoIstituzione(subist1);
							else {
								String lastlevel1 = ((Istituzione) (((Istituzione) (risultati.get(risultati.size() - 1))).getSottoIstituzioni()
										.get(countsublevel - 1))).getUrn();
								if ((!(lastlevel1.equals(subist1.getUrn()))) && (isValidDate(subist1.getDatainizio(), subist1.getDatafine(), datadisp)))
									((Istituzione) (risultati.get(risultati.size() - 1))).getSottoIstituzioni().add(subist1);
								else {
									if (isValidDate(subist2.getDatainizio(), subist2.getDatafine(), datadisp))
										((Istituzione) (((Istituzione) (risultati.get(risultati.size() - 1))).getSottoIstituzioni().get(countsublevel - 1)))
												.addSottoIstituzione(subist2);
								}
							}
						}
					}
				}
			}
		}
		return (risultati);
	}

	public boolean isIstituzioneValida(Date data, String nomeIstituzione) {
		return (isIstituzioneValida(fromDateToString(data), nomeIstituzione));
	}

	public boolean isIstituzioneValida(String data, String nomeIstituzione) {
		Vector result = new Vector();
		result = getIstituzioniValideVect(data, Autorita.ALL);
		return (result.contains(nomeIstituzione) || result.contains(nomeIstituzione.toLowerCase()));
	}

	public String getUrnIstituzioneFromNomeIstituzione(String nomeIstituzione) {
		String xpath = "/ELENCHI_RAE/ELENCO_ISTITUZIONI/ISTITUZIONE[./NOME_UFFICIALE/text()=string(\"" + nomeIstituzione.trim()
				+ "\") or ./NOME_UFFICIALE/text()=string(\"" + nomeIstituzione.trim().toLowerCase() + "\")]/NOME_STANDARD/text()";
		List result = registro.selectNodes(xpath);
		if (result.size() == 0)
			return (null);
		else
			return (((Node) result.get(0)).getStringValue());
	}

	public String getNomeIstituzioneFromUrnIstituzione(String urnIstituzione) {
		String xpath = "/ELENCHI_RAE/ELENCO_ISTITUZIONI/ISTITUZIONE[./NOME_STANDARD/text()=string(\"" + urnIstituzione.trim()
				+ "\") or ./NOME_STANDARD/text()=string(\"" + urnIstituzione.trim().toLowerCase() + "\")]/NOME_UFFICIALE/text()";
		List result = registro.selectNodes(xpath);
		if (result.size() == 0)
			return (null);
		else
			return (((Node) result.get(0)).getStringValue());
	}

	public boolean isUrnInRegistro(String urn, int livello) {
		String liv = null;
		if (livello == Autorita.LIVELLO_0)
			return (getNomeIstituzioneFromUrnIstituzione(urn) != null);

		if (livello == Autorita.LIVELLO_1)
			liv = "LIVELLO1";
		else
			liv = "LIVELLO2";

		String xpath = "/ELENCHI_RAE/ELENCO_ISTITUZIONI/ISTITUZIONE[./" + liv + "/NOME/text()=string(\"" + urn.trim() + "\") or ./" + liv
				+ "/NOME/text()=string(\"" + urn.trim().toLowerCase() + "\")]";
		return (registro.selectNodes(xpath).size() != 0);
	}

	private String fromDateToString(Date data) {

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(data);
		String anno, mese, giorno;

		anno = "" + gc.get(GregorianCalendar.YEAR);
		int month = gc.get(GregorianCalendar.MONTH) + 1;
		if (month < 10)
			mese = "0" + month;
		else
			mese = "" + month;
		if (gc.get(GregorianCalendar.DAY_OF_MONTH) < 10)
			giorno = "0" + gc.get(GregorianCalendar.DAY_OF_MONTH);
		else
			giorno = "" + gc.get(GregorianCalendar.DAY_OF_MONTH);

		return (anno + mese + giorno);
	}

	private java.util.Date parseDateString(String date) {

		java.util.Date result = null;
		java.text.DateFormat formatodata = java.text.DateFormat.getDateInstance();
		java.text.SimpleDateFormat datainit = (java.text.SimpleDateFormat) formatodata;
		// datainit.applyPattern("dd/MM/yyyy");
		datainit.applyPattern("yyyyMMdd");
		try {
			if (!(date.equals("")))
				return (datainit.parse(date));
			else
				return (null);
		} catch (Exception e) {
			return (null);
		}
	}

	/**
	 * @param datainizio
	 * @param datafine
	 * @param datadisp
	 * @return
	 */

	private boolean isValidDate(java.util.Date datainizio, java.util.Date datafine, java.util.Date datadisp) {
		if (((datainizio == null) && (datafine == null)) || ((datafine == null) && (datainizio != null) && (datainizio.compareTo(datadisp) < 0))
				|| ((datainizio == null) && (datafine != null) && (datafine.compareTo(datadisp) > 0))
				|| ((datainizio != null) && (datafine != null) && (datainizio.compareTo(datadisp) < 0) && (datafine.compareTo(datadisp) > 0)))
			return true;
		else
			return false;
	}
}

package it.cnr.ittig.xmleges.editor.blocks.action.autorita;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.core.util.lang.UtilLang;
import it.cnr.ittig.xmleges.editor.services.action.autorita.AutoritaAction;
import it.cnr.ittig.xmleges.editor.services.autorita.Autorita;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Date;
import java.util.EventObject;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.autorita.AutoritaAction</code>.</h1>
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
 * @author <a href="mailto:agnoloni@dsi.unifi.it">Tommaso Agnoloni </a>
 */

public class AutoritaActionImpl implements AutoritaAction, Loggable, EventManagerListener, Serviceable, Configurable, Initializable {
	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	MyAbstractAction autoritaAction = new autoritaAction();

	PreferenceManager preferenceManager;

	Frame frame;

	UtilMsg utilmsg;

	Autorita autorita;

	Date data_rae;

	public abstract class MyAbstractAction extends AbstractAction {

		abstract public boolean canDoAction();

	}

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		autorita = (Autorita) serviceManager.lookup(Autorita.class);
		frame = (Frame) serviceManager.lookup(Frame.class);
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("editor.autorita", autoritaAction);
		autoritaAction.setEnabled(true);
	}

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
	}

	// /////////////////////////////////////////////// Azioni
	public class autoritaAction extends MyAbstractAction {

		public void actionPerformed(ActionEvent e) {

			logger.debug("prova aggiornamento autorita");
			if (utilmsg.msgWarning("editor.autorita.msg.warning.wait", "editor.autorita.msg")) {
				frame.setInteraction(false);
				int status = sincronizzaAutorita();
				switch (status) {
				case AutoritaAction.SUCCESFULY_UPDATED:
					logger.debug("OK AGGIORNAMENTO AUTORITA");
					autorita.reloadRae();
					frame.setInteraction(true);
					utilmsg.msgInfo("editor.autorita.msg.successful", "editor.autorita.msg");
					break;
				case AutoritaAction.ALREADY_UPDATED:
					logger.debug("FAILED AGGIORNAMENTO AUTORITA");
					frame.setInteraction(true);
					utilmsg.msgInfo("editor.autorita.msg.already_updated", "editor.autorita.msg");
					break;
				case AutoritaAction.FAILED_CONNECTION:
					frame.setInteraction(true);
					utilmsg.msgError("editor.autorita.msg.failed_connection", "editor.autorita.msg");
					break;
				case AutoritaAction.ERROR:
					frame.setInteraction(true);
					utilmsg.msgError("editor.autorita.msg.error", "editor.autorita.msg");
					break;
				}
			}
		}

		public boolean canDoAction() {
			return (true);
		}
	}

	public int sincronizzaAutorita() {

		Document reg;
		DocumentBuilder domBuilder;
		DocumentBuilderFactory domFactory;
		String url = "http://www.normeinrete.it/stdoc/xmlrae/dati_rae.xml";

		String dest = autorita.getDirRae();
		logger.debug("Directory RAE: " + dest);

		if ((data_rae = getDataFromUrl()) == null)
			return (AutoritaAction.FAILED_CONNECTION);

		domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		domFactory.setValidating(true);

		if (needsUpdate()) {
			logger.debug("RAE NEEDS UPDATE");
			try {
				domBuilder = domFactory.newDocumentBuilder();
				reg = domBuilder.parse(url);
				regTransform(reg, dest, fromDateToString(data_rae)); // lo
				// copia
				// nella
				// /tmp
				// come
				// "nir_dati_rae.xml"
				setDataLocale(data_rae);
				return (AutoritaAction.SUCCESFULY_UPDATED);
			} // try open autorita (locale)
			catch (Exception e) {
				logger.error(e.getMessage(), e);
				return (AutoritaAction.ERROR);
			}
		} else {
			logger.debug("REGISTRO GIA' AGGIORNATO");
			return (AutoritaAction.ALREADY_UPDATED);
		}
	}

	private boolean needsUpdate() {
		try {
			if (data_rae.after(getDataLocale()))
				return (true);
			return (false);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.debug("EXCEPTION in needsUpdate:  return TRUE");
			return (true);
		}
	}

	private Date getDataFromUrl() {

		java.text.DateFormat formatodata = java.text.DateFormat.getDateInstance();
		java.text.SimpleDateFormat datainit = (java.text.SimpleDateFormat) formatodata;
		datainit.applyPattern("yyyyMMdd");

		String urlData = "http://www.normeinrete.it/stdoc/xmlrae/data_creazione_rae.txt";
		String ret = "";
		Date forced_update;

		try {
			String line = "";
			URL source = new URL(urlData);
			BufferedReader in = new BufferedReader(new InputStreamReader(source.openStream()));
			while (null != (line = in.readLine()))
				ret = ret + line;
			in.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return (null);
		}

		try {
			forced_update = datainit.parse("19000101");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return (null);
		}

		try {
			return (datainit.parse(ret.trim()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return (forced_update);
		}
	}

	private Date getDataLocale() {

		java.text.DateFormat formatodata = java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT);
		java.text.SimpleDateFormat datainit = (java.text.SimpleDateFormat) formatodata;
		datainit.applyPattern("yyyyMMdd");

		String dataUpdate = "19000101";

		Properties props = preferenceManager.getPreferenceAsProperties(this.getClass().getName());

		if (props.containsKey("updated") && props.get("updated").toString().trim().length() == 8)
			dataUpdate = props.get("updated").toString().trim();
		else
			setDataLocale(dataUpdate);

		try {
			return (datainit.parse(dataUpdate));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return (null);
		}
	}

	private void setDataLocale(String data) {
		Properties props = preferenceManager.getPreferenceAsProperties(this.getClass().getName());
		props.setProperty("updated", data);
		preferenceManager.setPreference(this.getClass().getName(), props);
	}

	private void setDataLocale(Date data) {
		Properties props = preferenceManager.getPreferenceAsProperties(this.getClass().getName());
		props.setProperty("updated", fromDateToString(data));
		preferenceManager.setPreference(this.getClass().getName(), props);
	}

	private void regTransform(org.w3c.dom.Document registro, String dest, String data) {

		// per la trasformazione del registro CINECA in registro Nir:
		// sarebbe da fare con un foglio di stile (?)

		String Istituzioni_ID = "";
		String Nome_Istituzione = "";
		String Istituzione = "";
		String Alias = "";
		String Alias1 = "";
		String Periodi_ID = "";
		String ID_Istituzioni = "";
		String Periodi_data_inizio = "";
		String Periodi_data_fine = "";
		String Livello1_ID = "";
		String ID_Periodi_temp = "";
		String ID_Periodi = "";
		String Livello1_nome = "";
		String Livello1_data_inizio = "";
		String Livello1_data_fine = "";
		String Livello2_ID = "";
		String ID_Livello1_temp = "";
		String ID_Livello1 = "";
		String Livello2_nome = "";
		String Livello2_data_inizio = "";
		String Livello2_data_fine = "";

		boolean link_livello1 = false;
		boolean link_livello2 = false;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(baos));
			out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>" + "\n");
			out.write("<!DOCTYPE ELENCHI_RAE>" + "\n");
			out.write("<ELENCHI_RAE DATA=\"" + data + "\">" + "\n");
			out.write("<ELENCO_ISTITUZIONI>" + "\n");

			NodeList istituzioni = registro.getElementsByTagName("ISTITUZIONE");
			for (int i = 0; i < istituzioni.getLength(); i++) {
				Istituzioni_ID = "" + istituzioni.item(i).getAttributes().getNamedItem("ID").getNodeValue();
				NodeList istituzioni_attrib = istituzioni.item(i).getChildNodes();

				Nome_Istituzione = getNome_Ufficiale(istituzioni_attrib);
				Istituzione = getNome_Standard(istituzioni_attrib);
				Alias = getAlias(istituzioni_attrib);
				Alias1 = getAlias1(istituzioni_attrib);

				NodeList periodi = registro.getElementsByTagName("PERIODO");
				for (int k = 0; k < periodi.getLength(); k++) {
					Periodi_ID = "" + periodi.item(k).getAttributes().getNamedItem("ID").getNodeValue();
					ID_Istituzioni = "" + periodi.item(k).getAttributes().getNamedItem("ID_ISTITUZIONE").getNodeValue();
					if (ID_Istituzioni.equals(Istituzioni_ID)) {
						NodeList periodi_attrib = periodi.item(k).getChildNodes();
						Periodi_data_inizio = getData_Inizio(periodi_attrib);
						Periodi_data_fine = getData_Fine(periodi_attrib);

						NodeList livello1 = registro.getElementsByTagName("LIVELLO1");
						link_livello1 = false;
						for (int j = 0; j < livello1.getLength(); j++) {
							ID_Periodi_temp = "" + livello1.item(j).getAttributes().getNamedItem("ID_PERIODI").getNodeValue();
							if ((ID_Periodi_temp.equals(Periodi_ID)) && (!ID_Periodi_temp.equals(""))) {
								link_livello1 = true;
								NodeList livello1_attrib = livello1.item(j).getChildNodes();
								ID_Periodi = "" + ID_Periodi_temp;
								Livello1_ID = "" + livello1.item(j).getAttributes().getNamedItem("ID").getNodeValue();
								Livello1_nome = getLivello1_nome(livello1_attrib);
								Livello1_data_inizio = getLivello1_data_inizio(livello1_attrib);
								Livello1_data_fine = getLivello1_data_fine(livello1_attrib);

								NodeList livello2 = registro.getElementsByTagName("LIVELLO2");
								link_livello2 = false;
								for (int l = 0; l < livello2.getLength(); l++) {
									ID_Livello1_temp = "" + livello2.item(l).getAttributes().getNamedItem("ID_LIVELLO1").getNodeValue();
									if (ID_Livello1_temp.equals(Livello1_ID)) {
										link_livello2 = true;
										ID_Livello1 = "" + ID_Livello1_temp;
										Livello2_ID = "" + livello2.item(l).getAttributes().getNamedItem("ID").getNodeValue();
										NodeList livello2_attrib = livello2.item(l).getChildNodes();
										Livello2_nome = getLivello2_nome(livello2_attrib);
										Livello2_data_inizio = getLivello2_data_inizio(livello2_attrib);
										Livello2_data_fine = getLivello2_data_fine(livello2_attrib);
										writeIstituzione(out, Nome_Istituzione, Istituzione, Alias, Alias1, Periodi_data_inizio, Periodi_data_fine,
												Livello1_nome, Livello1_data_inizio, Livello1_data_fine, Livello2_nome, Livello2_data_inizio,
												Livello2_data_fine);
									}
								}
								if (!link_livello2) {
									Livello2_ID = "";
									ID_Livello1 = "";
									Livello2_nome = "";
									Livello2_data_inizio = "";
									Livello2_data_fine = "";
									writeIstituzione(out, Nome_Istituzione, Istituzione, Alias, Alias1, Periodi_data_inizio, Periodi_data_fine, Livello1_nome,
											Livello1_data_inizio, Livello1_data_fine, Livello2_nome, Livello2_data_inizio, Livello2_data_fine);
								}
							}
						}
						if (!link_livello1) {
							Livello1_ID = "";
							ID_Periodi = "";
							Livello1_nome = "";
							Livello1_data_inizio = "";
							Livello1_data_fine = "";
							writeIstituzione(out, Nome_Istituzione, Istituzione, Alias, Alias1, Periodi_data_inizio, Periodi_data_fine, Livello1_nome,
									Livello1_data_inizio, Livello1_data_fine, Livello2_nome, Livello2_data_inizio, Livello2_data_fine);
						}
					}
				}
			}
			out.write("</ELENCO_ISTITUZIONI>");
			out.write("</ELENCHI_RAE>");
			out.flush();
			UtilFile.copyFile(new ByteArrayInputStream(baos.toByteArray()), dest);
			out.close();
		} catch (Exception e) {
			logger.error(e.toString());
		}
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

	private String removeAccento(String stringa) {

		String newstring = "";

		char[] charstring;
		charstring = stringa.toCharArray();
		for (int i = 0; i < charstring.length; i++) {
			if (charstring[i] == '\u00E0' || charstring[i] == '\u00E1')
				newstring = newstring + "a" + "'";
			else if (charstring[i] == '\u00E8' || charstring[i] == '\u00E9')
				newstring = newstring + "e" + "'";
			else if (charstring[i] == '\u00EC' || charstring[i] == '\u00ED')
				newstring = newstring + "i" + "'";
			else if (charstring[i] == '\u00F2' || charstring[i] == '\u00F3')
				newstring = newstring + "o" + "'";
			else if (charstring[i] == '\u00F9' || charstring[i] == '\u00FA')
				newstring = newstring + "u" + "'";
			else
				newstring = newstring + charstring[i];
		}
		return (removequotes(newstring));
	}

	private String removequotes(String stringa) {
		return (stringa.replaceAll("\"", ""));
	}

	private String getNome_Ufficiale(NodeList ist_attrib) {
		String Nome_Ist = "";
		for (int j = 0; j < ist_attrib.getLength(); j++) {
			// scarta i nodi /n /t etc
			if (UtilLang.trimText(ist_attrib.item(j).getNodeValue()) == null) {
				if (ist_attrib.item(j).getNodeName().equals("NOME_UFFICIALE")) {
					Nome_Ist = "";
					if (ist_attrib.item(j).hasChildNodes())
						Nome_Ist += ist_attrib.item(j).getChildNodes().item(0).getNodeValue();
				}
			}
		}
		return (removeAccento(Nome_Ist));
	}

	private String getNome_Standard(NodeList ist_attrib) {
		String Ist = "";
		for (int j = 0; j < ist_attrib.getLength(); j++) {
			// scarta i nodi /n /t etc
			if (UtilLang.trimText(ist_attrib.item(j).getNodeValue()) == null) {
				if (ist_attrib.item(j).getNodeName().equals("NOME_STANDARD")) {
					Ist = "";
					if (ist_attrib.item(j).hasChildNodes())
						Ist += ist_attrib.item(j).getChildNodes().item(0).getNodeValue();
				}
			}
		}
		return (removeAccento(Ist));
	}

	private String getAlias(NodeList ist_attrib) {
		String alias = "";
		for (int j = 0; j < ist_attrib.getLength(); j++) {
			// scarta i nodi /n /t etc
			if (UtilLang.trimText(ist_attrib.item(j).getNodeValue()) == null) {
				if (ist_attrib.item(j).getNodeName().equals("ALIAS")) {
					alias = "";
					if (ist_attrib.item(j).hasChildNodes())
						alias += ist_attrib.item(j).getChildNodes().item(0).getNodeValue();
				}
			}
		}
		return (removeAccento(alias));
	}

	private String getAlias1(NodeList ist_attrib) {
		String alias1 = "";
		for (int j = 0; j < ist_attrib.getLength(); j++) {
			// scarta i nodi /n /t etc
			if (UtilLang.trimText(ist_attrib.item(j).getNodeValue()) == null) {
				if (ist_attrib.item(j).getNodeName().equals("ALIAS1")) {
					alias1 = "";
					if (ist_attrib.item(j).hasChildNodes())
						alias1 += ist_attrib.item(j).getChildNodes().item(0).getNodeValue();
				}
			}
		}
		return (removeAccento(alias1));
	}

	private String getData_Inizio(NodeList per_attrib) {
		String data_inizio = "";
		for (int j = 0; j < per_attrib.getLength(); j++) {
			// scarta i nodi /n /t etc
			if (UtilLang.trimText(per_attrib.item(j).getNodeValue()) == null) {
				if (per_attrib.item(j).getNodeName().equals("DATA_INIZIO")) {
					// data_inizio="";
					if (per_attrib.item(j).hasChildNodes()) {
						data_inizio = per_attrib.item(j).getChildNodes().item(0).getNodeValue();
						data_inizio = data_inizio.substring(1, data_inizio.length() - 1);
						StringTokenizer st = new StringTokenizer(data_inizio);
						if (st.countTokens() > 0)
							data_inizio = st.nextToken();
					}
				}
			}
		}
		return (normalizza(data_inizio));
	}

	private String getData_Fine(NodeList per_attrib) {
		String data_fine = "";
		for (int j = 0; j < per_attrib.getLength(); j++) {
			// scarta i nodi /n /t etc
			if (UtilLang.trimText(per_attrib.item(j).getNodeValue()) == null) {
				if (per_attrib.item(j).getNodeName().equals("DATA_FINE")) {
					if (per_attrib.item(j).hasChildNodes()) {
						data_fine += per_attrib.item(j).getChildNodes().item(0).getNodeValue();
						data_fine = data_fine.substring(1, data_fine.length() - 1);
						StringTokenizer st = new StringTokenizer(data_fine);
						if (st.countTokens() > 0)
							data_fine = st.nextToken();
					}
				}
			}
		}
		return (normalizza(data_fine));
	}

	private String getLivello1_nome(NodeList liv1_attrib) {
		String nome = "";
		for (int j = 0; j < liv1_attrib.getLength(); j++) {
			// scarta i nodi /n /t etc
			if (UtilLang.trimText(liv1_attrib.item(j).getNodeValue()) == null) {
				if (liv1_attrib.item(j).getNodeName().equals("NOME")) {
					nome = "";
					if (liv1_attrib.item(j).hasChildNodes())
						nome += liv1_attrib.item(j).getChildNodes().item(0).getNodeValue();
				}
			}
		}
		return (removeAccento(nome));
	}

	private String getLivello1_data_inizio(NodeList liv1_attrib) {
		String inizio = "";
		for (int j = 0; j < liv1_attrib.getLength(); j++) {
			// scarta i nodi /n /t etc
			if (UtilLang.trimText(liv1_attrib.item(j).getNodeValue()) == null) {
				if (liv1_attrib.item(j).getNodeName().equals("DATA_INIZIO")) {
					if (liv1_attrib.item(j).hasChildNodes()) {
						inizio += liv1_attrib.item(j).getChildNodes().item(0).getNodeValue();
						inizio = inizio.substring(1, inizio.length() - 1);
						StringTokenizer st = new StringTokenizer(inizio);
						if (st.countTokens() > 0)
							inizio = st.nextToken();
					}
				}
			}
		}
		return (normalizza(inizio));
	}

	private String getLivello1_data_fine(NodeList liv1_attrib) {
		String fine = "";
		for (int j = 0; j < liv1_attrib.getLength(); j++) {
			// scarta i nodi /n /t etc
			if (UtilLang.trimText(liv1_attrib.item(j).getNodeValue()) == null) {
				if (liv1_attrib.item(j).getNodeName().equals("DATA_FINE")) {
					if (liv1_attrib.item(j).hasChildNodes()) {
						fine += liv1_attrib.item(j).getChildNodes().item(0).getNodeValue();
						fine = fine.substring(1, fine.length() - 1);
						StringTokenizer st = new StringTokenizer(fine);
						if (st.countTokens() > 0)
							fine = st.nextToken();
					}
				}
			}
		}
		return (normalizza(fine));
	}

	private String getLivello2_nome(NodeList liv2_attrib) {
		String nome = "";
		for (int j = 0; j < liv2_attrib.getLength(); j++) {
			// scarta i nodi /n /t etc
			if (UtilLang.trimText(liv2_attrib.item(j).getNodeValue()) == null) {
				if (liv2_attrib.item(j).getNodeName().equals("NOME")) {
					nome = "";
					if (liv2_attrib.item(j).hasChildNodes())
						nome += liv2_attrib.item(j).getChildNodes().item(0).getNodeValue();
				}
			}
		}
		return (removeAccento(nome));
	}

	private String getLivello2_data_inizio(NodeList liv2_attrib) {
		String inizio = "";
		for (int j = 0; j < liv2_attrib.getLength(); j++) {
			// scarta i nodi /n /t etc
			if (UtilLang.trimText(liv2_attrib.item(j).getNodeValue()) == null) {
				if (liv2_attrib.item(j).getNodeName().equals("DATA_INIZIO")) {
					if (liv2_attrib.item(j).hasChildNodes()) {
						inizio += liv2_attrib.item(j).getChildNodes().item(0).getNodeValue();
						inizio = inizio.substring(1, inizio.length() - 1);
						StringTokenizer st = new StringTokenizer(inizio);
						if (st.countTokens() > 0)
							inizio = st.nextToken();
					}
				}
			}
		}
		return (normalizza(inizio));
	}

	private String getLivello2_data_fine(NodeList liv2_attrib) {
		String fine = "";
		for (int j = 0; j < liv2_attrib.getLength(); j++) {
			// scarta i nodi /n /t etc
			if (UtilLang.trimText(liv2_attrib.item(j).getNodeValue()) == null) {
				if (liv2_attrib.item(j).getNodeName().equals("DATA_FINE")) {
					if (liv2_attrib.item(j).hasChildNodes())
						fine += liv2_attrib.item(j).getChildNodes().item(0).getNodeValue();
					fine = fine.substring(1, fine.length() - 1);
					StringTokenizer st = new StringTokenizer(fine);
					if (st.countTokens() > 0)
						fine = st.nextToken();
				}
			}
		}
		return (normalizza(fine));
	}

	private String normalizza(String data) {
		if (data.trim().length() > 0)
			return (data.substring(6, 10) + data.substring(3, 5) + data.substring(0, 2));
		return (data);
	}

	private void writeIstituzione(BufferedWriter out, String Nome_Istituzione, String Istituzione, String Alias, String Alias1, String Periodi_data_inizio,
			String Periodi_data_fine, String Livello1_nome, String Livello1_data_inizio, String Livello1_data_fine, String Livello2_nome,
			String Livello2_data_inizio, String Livello2_data_fine) {
		try {
			out.write("<ISTITUZIONE>" + "\n");
			out.write("<NOME_UFFICIALE>");
			if (Nome_Istituzione.trim().length() != 0)
				out.write(Nome_Istituzione);
			out.write("</NOME_UFFICIALE>" + "\n");

			out.write("<NOME_STANDARD>");
			if (Istituzione.trim().length() != 0)
				out.write(Istituzione);
			out.write("</NOME_STANDARD>" + "\n");

			out.write("<ALIAS>");
			if (Alias.trim().length() != 0)
				out.write(Alias);
			out.write("</ALIAS>" + "\n");

			out.write("<ALIAS1>");
			if (Alias1.trim().length() != 0)
				out.write(Alias1);
			out.write("</ALIAS1>" + "\n");

			out.write("<PERIODO>" + "\n");

			out.write("<DATA_INIZIO>");
			if (Periodi_data_inizio.trim().length() != 0)
				out.write(Periodi_data_inizio);
			out.write("</DATA_INIZIO>" + "\n");

			out.write("<DATA_FINE>");
			if (Periodi_data_fine.trim().length() != 0)
				out.write(Periodi_data_fine);
			out.write("</DATA_FINE>" + "\n");

			out.write("</PERIODO>" + "\n");

			out.write("<LIVELLO1>" + "\n");

			out.write("<NOME>");
			if (Livello1_nome.trim().length() != 0)
				out.write(Livello1_nome);
			out.write("</NOME>" + "\n");

			out.write("<DATA_INIZIO>");
			if (Livello1_data_inizio.trim().length() != 0)
				out.write(Livello1_data_inizio);
			out.write("</DATA_INIZIO>" + "\n");

			out.write("<DATA_FINE>");
			if (Livello1_data_fine.trim().length() != 0)
				out.write(Livello1_data_fine);
			out.write("</DATA_FINE>" + "\n");

			out.write("</LIVELLO1>" + "\n");

			out.write("<LIVELLO2>" + "\n");

			out.write("<NOME>");
			if (Livello2_nome.trim().length() != 0)
				out.write(Livello2_nome);
			out.write("</NOME>" + "\n");

			out.write("<DATA_INIZIO>");
			if (Livello2_data_inizio.trim().length() != 0)
				out.write(Livello2_data_inizio);
			out.write("</DATA_INIZIO>" + "\n");

			out.write("<DATA_FINE>");
			if (Livello2_data_fine.trim().length() != 0)
				out.write(Livello2_data_fine);
			out.write("</DATA_FINE>" + "\n");

			out.write("</LIVELLO2>" + "\n");

			out.write("</ISTITUZIONE>" + "\n");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}

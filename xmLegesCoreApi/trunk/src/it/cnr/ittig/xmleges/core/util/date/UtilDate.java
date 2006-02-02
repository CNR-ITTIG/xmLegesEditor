package it.cnr.ittig.xmleges.core.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

/**
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
 * @author Tommaso Paba <t.paba@onetech.it>
 */
public class UtilDate {

	public static String getDateFormat() {
		return "dd/MM/yyyy";
	}

	public static String getNormFormat() {
		return "yyyyMMdd";
	}

	/**
	 * Converte una data normalizzata in un oggetto java.util.Date.
	 * 
	 * @param norm data normalizzata
	 * @return data, o null se la data passata non ? valida
	 */
	public static java.util.Date normToDate(String norm) {

		if (norm == null || norm.equals(""))
			return null;

		try {
			SimpleDateFormat nf = new SimpleDateFormat(UtilDate.getNormFormat());
			java.util.Date d = nf.parse(norm);
			return d;
		} catch (ParseException ex) {
			// TODO log this error?
		}
		return null;
	}

	/**
	 * Formatta una data normalizzata (nel formato dato da getNormFormat()) con
	 * il formato dato da getDateFormat().
	 * 
	 * @param norm data normalizzata
	 * @return data formattata, o null in caso di errore
	 */
	public static String normToString(String norm) {

		if (norm == null || norm.equals(""))
			return "";

		try {
			SimpleDateFormat nf = new SimpleDateFormat(UtilDate.getNormFormat());
			SimpleDateFormat df = new SimpleDateFormat(UtilDate.getDateFormat());
			java.util.Date d = nf.parse(norm);
			return df.format(d);
		} catch (ParseException ex) {
			// TODO log this error?
		}
		return null;
	}

	/**
	 * Formatta una data normalizzata (nel formato dato da getNormFormat()) con
	 * il formato dato da getDateFormat().
	 * 
	 * @param norm data normalizzata
	 * @return data formattata, o null in caso di errore
	 */
	public static String dateToNorm(java.util.Date date) {

		if (date == null)
			return "";

		SimpleDateFormat nf = new SimpleDateFormat(UtilDate.getNormFormat());
		return nf.format(date);
	}

	/**
	 * Formatta una data normalizzata nel formato richiesto per la urn
	 * yyyy-mm-dd.
	 * 
	 * @param norm data normalizzata
	 * @return data formattata, o null in caso di errore
	 */
	public static String normToUrnFormat(String norm) {

		if (norm == null || norm.length() < 8)
			return null;
		return (norm.substring(0, 4) + "-" + norm.substring(4, 6) + "-" + norm.substring(6, 8));
	}

	/**
	 * converte una data in formato urn yyyy-mm-dd in data normalizzata.
	 * 
	 * @param urndata
	 * @return data normalizzata, o null in caso di errore
	 */
	public static String urnFormatToNorm(String urndata) {

		if (urndata == null)
			return null;
		return (urndata.replaceAll("-", ""));
	}

	/**
	 * Restituisce la data corrente in formato java.util.Date.
	 * 
	 * @return data corrente
	 */
	public static java.util.Date getCurrentDate() {

		java.util.Calendar calendar = new GregorianCalendar();
		java.util.Date trialTime = new java.util.Date();
		calendar.setTime(trialTime);
		return calendar.getTime();
	}

	/**
	 * converte una data in forma testuale gg mese aaaa (es. 12 Dicembre 2003)
	 * in formato java.util.Date.
	 * 
	 * @return data, o null se la data passata non è valida
	 */
	public static java.util.Date textualFormatToDate(String textualDate) {

		if (textualDate == null || textualDate.equals(""))
			return null;

		try {
			String norm = "";

			StringTokenizer st = new StringTokenizer(textualDate, " ");

			if (st.countTokens() != 3)
				return null;

			String gg = st.nextToken();
			String mm = st.nextToken();
			String aaaa = st.nextToken();
			if (Integer.parseInt(gg) < 1 || Integer.parseInt(gg) > 31)
				return null;
			if (Integer.parseInt(gg) < 10)
				gg = "0" + gg;
			if (Integer.parseInt(aaaa) < 1700 || Integer.parseInt(gg) > 3000)
				return null;
			mm = getMonth(mm);
			if (mm == null)
				return null;

			norm += aaaa + mm + gg;

			SimpleDateFormat nf = new SimpleDateFormat(UtilDate.getNormFormat());
			java.util.Date d = nf.parse(norm);
			return d;
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * converte una data in formato java.util.Date in forma testuale gg mese
	 * aaaa (es. 12 Dicembre 2003) .
	 * 
	 * @return forma testuale della data, o null se la data passata non è valida
	 */
	public static String dateTotextualFormat(java.util.Date date) {

		String norm = UtilDate.dateToNorm(date);
		if (norm != null) {
			String textualDate = null;
			String aaaa = norm.substring(0, 4);
			String mm = norm.substring(4, 6);
			String gg = norm.substring(6, 8);

			if (gg.startsWith("0"))
				gg = gg.substring(1);

			switch (Integer.parseInt(mm)) {
			case 1: {
				textualDate = gg + " " + "Gennaio" + " " + aaaa;
				break;
			}
			case 2: {
				textualDate = gg + " " + "Febbraio" + " " + aaaa;
				break;
			}
			case 3: {
				textualDate = gg + " " + "Marzo" + " " + aaaa;
				break;
			}
			case 4: {
				textualDate = gg + " " + "Aprile" + " " + aaaa;
				break;
			}
			case 5: {
				textualDate = gg + " " + "Maggio" + " " + aaaa;
				break;
			}
			case 6: {
				textualDate = gg + " " + "Giugno" + " " + aaaa;
				break;
			}
			case 7: {
				textualDate = gg + " " + "Luglio" + " " + aaaa;
				break;
			}
			case 8: {
				textualDate = gg + " " + "Agosto" + " " + aaaa;
				break;
			}
			case 9: {
				textualDate = gg + " " + "Settembre" + " " + aaaa;
				break;
			}
			case 10: {
				textualDate = gg + " " + "Ottobre" + " " + aaaa;
				break;
			}
			case 11: {
				textualDate = gg + " " + "Novembre" + " " + aaaa;
				break;
			}
			case 12: {
				textualDate = gg + " " + "Dicembre" + " " + aaaa;
				break;
			}
			}
			return textualDate;
		}
		return null;
	}

	private static String getMonth(String month) {
		if (month.trim().equalsIgnoreCase("gennaio"))
			return "01";
		if (month.trim().equalsIgnoreCase("febbraio"))
			return "02";
		if (month.trim().equalsIgnoreCase("marzo"))
			return "03";
		if (month.trim().equalsIgnoreCase("aprile"))
			return "04";
		if (month.trim().equalsIgnoreCase("maggio"))
			return "05";
		if (month.trim().equalsIgnoreCase("giugno"))
			return "06";
		if (month.trim().equalsIgnoreCase("luglio"))
			return "07";
		if (month.trim().equalsIgnoreCase("agosto"))
			return "08";
		if (month.trim().equalsIgnoreCase("settembre"))
			return "09";
		if (month.trim().equalsIgnoreCase("ottobre"))
			return "10";
		if (month.trim().equalsIgnoreCase("novembre"))
			return "11";
		if (month.trim().equalsIgnoreCase("dicembre"))
			return "12";
		return null;
	}

}

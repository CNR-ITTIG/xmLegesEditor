package it.cnr.ittig.xmleges.core.util.lang;

import java.util.Iterator;
import java.util.Vector;

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
 * @author Alessio Ceroni
 */
public class UtilLang {

	static public Vector singleton(Object o) {
		Vector s = new Vector();
		s.add(o);
		return s;
	}

	static public Vector pair(Object f, Object s) {
		Vector p = new Vector();
		p.add(f);
		p.add(s);
		return p;
	}

	static public Vector append(Vector src, Object o) {
		Vector ret = new Vector();
		ret.addAll(src);
		ret.addElement(o);
		return ret;
	}

	/**
	 * Elimina occorrenze multiple di un carattere all'interno di una stringa.
	 * 
	 * @param str stringa da comprimere
	 * @param c carattere da ricercare
	 * @return la stringa compressa
	 */
	static public String squeeze(String str, char c) {
		char last = c;
		char[] buffer = str.toCharArray();
		StringBuffer out = new StringBuffer(buffer.length);

		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] != c || last != c) {
				out.append(buffer[i]);
			}
			last = buffer[i];
		}
		return out.toString();
	}

	/**
	 * Elimina tabs a newline da una stringa di testo.
	 */
	static public String trimText(String str) {
		if (str == null)
			return str;

		str = str.replace('\n', ' ').replace('\t', ' ').replaceAll("[ +]", " ");// .trim();
		str = squeeze(str, ' ');
		return str;
	}

	/**
	 * Ritorna l'ultimo elemento nel name path
	 */
	static public String getLastInPath(String path) {
		if (path == null || path.length() == 0)
			return "";
		String[] tokens = path.split("\\."); // regex for the dot mark
		return tokens[tokens.length - 1];
	}

	static public Vector tokenizeCSV(String text) {
		Vector tokens = new Vector();

		if (text == null)
			return tokens;

		text = trimText(text);
		if (text.length() == 0)
			return tokens;

		String[] elems = text.split("\\,");
		for (int i = 0; i < elems.length; i++) {
			String elem = trimText(elems[i]);
			if (elem.length() > 0)
				tokens.addElement(elem);
		}
		return tokens;
	}

	static public String makeCSV(Vector tokens) {
		String text = "";
		for (Iterator j = tokens.iterator(); j.hasNext();) {
			if (text.length() > 0)
				text = text + ",";
			text = text + j.next().toString();
		}
		return text;
	}

	public static boolean isRoman(String number) {
		String ucnumber = number.toUpperCase();
		for (int i = 0; i < ucnumber.length(); i++) {
			if (!((ucnumber.charAt(i) == 'I') || (ucnumber.charAt(i) == 'V') || (ucnumber.charAt(i) == 'X') || (ucnumber.charAt(i) == 'L')
					|| (ucnumber.charAt(i) == 'C') || (ucnumber.charAt(i) == 'D') || (ucnumber.charAt(i) == 'M'))) {
				// UtilityUi.msgInfo("Numero romano non valido: " + number);
				return (false);
			}

		}
		return (true);
	}

	public static int fromRomanToArabic(String roman) {
		Character[] elementi = new Character[roman.length()];
		int[] a = new int[255];
		int j = 0;
		int sm = 0;
		int minus = 0;
		int num = 0;
		roman = roman.toUpperCase();
		if (!(isRoman(roman))) {
			return (-1);
		}

		else {
			for (int i = 0; i < roman.length(); i++) {
				elementi[i] = new Character(roman.charAt(i));
				if (elementi[i].toString().equals("I"))
					a[i] = 1;
				else if (elementi[i].toString().equals("V"))
					a[i] = 5;
				else if (elementi[i].toString().equals("X"))
					a[i] = 10;
				else if (elementi[i].toString().equals("L"))
					a[i] = 50;
				else if (elementi[i].toString().equals("C"))
					a[i] = 100;
				else if (elementi[i].toString().equals("D"))
					a[i] = 500;
				else if (elementi[i].toString().equals("M"))
					a[i] = 1000;
			}
			int i = 0;

			if ((i + 1) == roman.length())
				return (a[i]);
			sm = 0;
			while (i < roman.length()) {
				i++;
				while ((a[i - 1] >= a[i]) && (i < roman.length()))
					i++;
				for (j = sm; j < (i - 1); j++)
					num += a[j];
				j = i;
				minus = 0;
				while ((j < roman.length()) && (a[j - 1] <= a[j])) {
					minus += a[j - 1];
					j++;
				}
				num = num + a[j - 1] - minus;
				sm = j;
				i = j;
			}
		}
		return (num);

	}

	public static String fromArabicToLetter(String arabic) {
		int times;
		int num;
		String ret = "";
		try {
			num = Integer.parseInt(arabic);
			if (num - 1 < 0)
				return ("");
			times = ((num - 1) / 21) + 1;
			for (int i = 0; i < times; i++)
				ret = ret + Character.toString("abcdefghilmnopqrstuvz".charAt((num - 1) % 21));
		} catch (Exception e) {
			// UtilityUI.msgInfo(arabic + " non e' un numero arabo");
		}
		return (ret);
	}

	// come fromArabicToLetter ma con alfabeto a 26 caratteri
	public static String fromNumberToLetter(String number) {

		int times;
		int num;
		String ret = "";
		try {
			num = Integer.parseInt(number);
			if (num - 1 < 0)
				return ("");
			times = ((num - 1) / 26) + 1;
			for (int i = 0; i < times; i++)
				ret = ret + Character.toString("abcdefghijklmnopqrstuvwxyz".charAt((num - 1) % 26));
		} catch (Exception e) {
		}
		return ret;
	}

	public static int fromLetterToNumber(String c) {
		char lett = c.charAt(0);
		if (c.length() == 1)
			return "abcdefghijklmnopqrstuvwxyz".indexOf(lett) + 1;
		if (c.length() == 2)
			return "abcdefghijklmnopqrstuvwxyz".indexOf(lett) + 27;
		if (c.length() == 3)
			return "abcdefghijklmnopqrstuvwxyz".indexOf(lett) + 53;
		if (c.length() == 4)
			return "abcdefghijklmnopqrstuvwxyz".indexOf(lett) + 79;
		return -1;
	}

	public static String fromArabicToRoman(String arabic) {
		int arabicnum;
		String roman = "";
		try {
			arabicnum = Integer.parseInt(arabic);
			while (arabicnum >= 1000) {
				arabicnum -= 1000;
				roman += "M";
			}
			if (arabicnum >= 900) {
				arabicnum -= 900;
				roman += "CM";
			} else if (arabicnum >= 500) {
				arabicnum -= 500;
				roman += "D";
			}
			while (arabicnum >= 100) {
				arabicnum -= 100;
				roman += "C";
			}
			if (arabicnum >= 90) {
				arabicnum -= 90;
				roman += "XC";
			} else {
				if (arabicnum >= 50) {
					arabicnum -= 50;
					roman += "L";
				} else if (arabicnum >= 40) {
					arabicnum -= 40;
					roman += "XL";
				}
			}
			while (arabicnum >= 10) {
				roman += "X";
				arabicnum -= 10;
			}
			if (arabicnum >= 9) {
				arabicnum -= 9;
				roman += "IX";
			} else {
				if (arabicnum >= 5) {
					arabicnum -= 5;
					roman += "V";
				} else if (arabicnum >= 4) {
					arabicnum -= 4;
					roman += "IV";
				}
			}
			while (arabicnum > 0) {
				roman += "I";
				arabicnum--;
			}

		} catch (Exception e) {
		}
		return (roman);
	}

	public static String bytesToHexString(byte[] bs) {

		if (bs == null)
			return null;

		StringBuffer ret = new StringBuffer(bs.length);
		for (int i = 0; i < bs.length; i++) {
			String hex = Integer.toHexString(0x0100 + (bs[i] & 0x00FF)).substring(1);
			ret.append((hex.length() < 2 ? "0" : "") + hex);
		}
		return ret.toString();
	}

}

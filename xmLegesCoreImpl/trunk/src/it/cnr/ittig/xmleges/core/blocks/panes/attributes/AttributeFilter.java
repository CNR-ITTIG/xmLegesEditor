package it.cnr.ittig.xmleges.core.blocks.panes.attributes;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Classe per effettuare il filtro degli attributi per renderli di sola scrittura e non
 * visibili.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class AttributeFilter {
	class AttrPattern {
		boolean hidden;

		String regexp;

		public AttrPattern(boolean hidden, String regexp) {
			this.hidden = hidden;
			this.regexp = regexp;
		}
	}

	Vector patterns = new Vector();

	/**
	 * Aggiunge l'espressione regolare <code>regexp</code> per i nomi degli attributi
	 * non visibili.
	 * 
	 * @param regexp espresisone regolare
	 */
	public void addHidden(String regexp) {
		patterns.addElement(new AttrPattern(true, regexp));
	}

	/**
	 * Aggiunge l'espressione regolare <code>regexp</code> per i nomi degli attributi di
	 * sola lettura.
	 * 
	 * @param regexp espresisone regolare
	 */
	public void addReadonly(String regexp) {
		patterns.addElement(new AttrPattern(false, regexp));
	}

	/**
	 * Verifica se l'attributo di nome <code>attrName</code> non deve essere visibile.
	 * 
	 * @param attrName nome dell'attributo
	 * @return <code>true</code> se non visibile
	 */
	public boolean isHidden(String attrName) {
		return findMatch(true, attrName);
	}

	/**
	 * Verifica se l'attributo di nome <code>attrName</code> deve essere trattato come
	 * di sola lettura.
	 * 
	 * @param attrName nome dell'attributo
	 * @return <code>true</code> se di sola lettura
	 */
	public boolean isReadonly(String attrName) {
		return findMatch(false, attrName);
	}

	protected boolean findMatch(boolean hidden, String name) {
		for (Enumeration en = patterns.elements(); en.hasMoreElements();) {
			AttrPattern pattern = (AttrPattern) en.nextElement();
			if (pattern.hidden == hidden && name != null && (name.equals(pattern.regexp) || name.matches(pattern.regexp)))
				return true;
		}
		return false;
	}

}

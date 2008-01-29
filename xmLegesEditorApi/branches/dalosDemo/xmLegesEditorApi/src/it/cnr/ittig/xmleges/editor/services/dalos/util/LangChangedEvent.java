package it.cnr.ittig.xmleges.editor.services.dalos.util;

import java.util.EventObject;

/**
 * Classe evento per notificare la variazione delle lingue (from and to) selezionate
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
 * @see it.cnr.ittig.xmleges.core.services.selection.SelectionManager
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public class LangChangedEvent extends EventObject {

	int langToIndex;
	boolean isGlobalLang;
	String golbalLang;

	
	/**
	 * 
	 * @param source
	 * @param isGlobalLang
	 * @param globalLang
	 * @param langToIndex
	 */
	public LangChangedEvent(Object source, boolean isGlobalLang, String globalLang, int langToIndex) {
		super(source);
		this.langToIndex = langToIndex;
		this.isGlobalLang = isGlobalLang;
		this.golbalLang = globalLang;
	}

	
	/**
	 * 
	 * @return
	 */
	public int getLangToIndex() {
		return this.langToIndex;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getIsGlobalLang(){
		return this.isGlobalLang;
	}

	/**
	 * 
	 * @return
	 */
	public String getGlobalLang(){
		return this.golbalLang;
	}
	
}

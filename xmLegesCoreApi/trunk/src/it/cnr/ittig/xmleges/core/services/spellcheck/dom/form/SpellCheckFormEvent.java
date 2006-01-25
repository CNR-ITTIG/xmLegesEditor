package it.cnr.ittig.xmleges.core.services.spellcheck.dom.form;

import java.util.EventObject;

/**
 * Classe evento per notificare eventi generati dal form di controllo
 * ortografico.
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
 * @author <a href="mailto:t.paba@onetech.it">Tommaso Paba </a>
 */
public class SpellCheckFormEvent extends EventObject {

	String eventInfo;

	public SpellCheckFormEvent(Object source, String eventInfo) {
		super(source);
		this.eventInfo = eventInfo;
	}

	public String getEventInfo() {
		return eventInfo;
	}
}

package it.cnr.ittig.xmleges.core.services.form.listtextfield;

import java.awt.AWTEvent;

/**
 * Evento emesso da <code>ListTextField</code>.
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
 * @author <a href="mailto:t.paba@onetech.it">Tommaso Paba</a>
 */
public class ListTextFieldElementEvent extends AWTEvent {

	public static final int ELEMENT_ADD = 0;

	public static final int ELEMENT_MODIFY = 1;

	public static final int ELEMENT_REMOVE = 2;

	public static final int ELEMENT_MOVEUP = 3;

	public static final int ELEMENT_MOVEDOWN = 4;

	public ListTextFieldElementEvent(Object source, int id) {
		super(source, id);
	}
}

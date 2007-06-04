package it.cnr.ittig.xmleges.core.util.lang;

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

public class Queue extends Vector {

	public Queue() {
	}

	public boolean empty() {
		return (size() == 0);
	}

	public void push(Object o) {
		addElement(o);
	}

	public Object pop() {
		Object o = elementAt(0);
		removeElementAt(0);
		return o;
	}

}

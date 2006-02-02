package it.cnr.ittig.xmleges.core.util.lang;

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
public class Monitor {

	Object owner = null;

	int no_holds = 0;

	public Monitor(Object _owner) {
		owner = _owner;
		no_holds = 0;
	}

	public boolean isFree() {
		return (no_holds == 0);
	}

	public void hold() {
		no_holds++;
	}

	public void release() {
		if (no_holds > 0)
			no_holds--;
	}

}

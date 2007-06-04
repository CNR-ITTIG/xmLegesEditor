package it.cnr.ittig.xmleges.core.services.dtd;

/**
 * Classe per la gestione delle eccezioni nel <code>DtdRulesManager</code>.
 * 
 * @author Maurizio Mollicone &lt;maurizio.mollicone@mollik.org&gt;
 */

public class DtdRulesManagerException extends Exception {
	public DtdRulesManagerException(String message) {
		super(message);
	}

	public DtdRulesManagerException(Exception parent) {
		super(parent);
	}
}
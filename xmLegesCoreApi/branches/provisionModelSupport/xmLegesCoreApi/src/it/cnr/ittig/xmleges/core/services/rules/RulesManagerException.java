package it.cnr.ittig.xmleges.core.services.rules;

/**
 * Classe per la gestione delle eccezioni nel <code>RulesManager</code>.
 * 
 * @author Maurizio Mollicone &lt;maurizio.mollicone@mollik.org&gt;
 */

public class RulesManagerException extends Exception {
	public RulesManagerException(String message) {
		super(message);
	}

	public RulesManagerException(Exception parent) {
		super(parent);
	}
}
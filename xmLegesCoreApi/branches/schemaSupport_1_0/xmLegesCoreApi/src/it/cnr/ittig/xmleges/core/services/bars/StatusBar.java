package it.cnr.ittig.xmleges.core.services.bars;

import java.awt.Color;
import java.awt.Component;

/**
 * Barra di stato con pi&ugrave; slot che possono essere impostati attraverso il
 * corrispondente numero o nome.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface StatusBar {

	/**
	 * Restituisce il componente grafico per essere visualizzato.
	 * 
	 * @return componente da visualizzare
	 */
	public Component getComponent();

	/**
	 * Imposta il testo nel primo slot.
	 * 
	 * @param text testo da visualizzare
	 */
	public void setText(String text);

	/**
	 * Imposta il testo nell'n-esimo slot.
	 * 
	 * @param text testo da visualizzare
	 * @param n numero di slot
	 */
	public void setText(String text, int n);

	/**
	 * Imposta il testo nello slot di nome <i>name</i>.
	 * 
	 * @param text testo da visualizzare
	 * @param name nome dello slot
	 */
	public void setText(String text, String name);

	/**
	 * Imposta il colore di sfondo all'n-esimo slot.
	 * 
	 * @param color colore
	 * @param n numero di slot
	 */
	public void setBackground(Color color, int n);

	/**
	 * Imposta il colore di sfondo allo slot di nome <i>name</i>.
	 * 
	 * @param color colore
	 * @param name nome dello slot
	 */
	public void setBackground(Color color, String name);

	/**
	 * Imposta il colore di sfondo di default all'n-esimo slot.
	 * 
	 * @param n numero di slot
	 */
	public void setDefaultBackground(int n);

	/**
	 * Imposta il colore di sfondo di default allo slot di nome <i>name</i>.
	 * 
	 * @param name nome dello slot
	 */
	public void setDefaultBackground(String name);

	/**
	 * Imposta il colore del testo all'n-esimo slot.
	 * 
	 * @param color colore
	 * @param n numero di slot
	 */
	public void setForeground(Color color, int n);

	/**
	 * Imposta il colore del testo allo slot di nome <i>name</i>.
	 * 
	 * @param color colore
	 * @param name nome dello slot
	 */
	public void setForeground(Color color, String name);

	/**
	 * Imposta il colore di default del testo all'n-esimo slot.
	 * 
	 * @param n numero di slot
	 */
	public void setDefaultForeground(int n);

	/**
	 * Imposta il colore del testo di default allo slot di nome <i>name</i>.
	 * 
	 * @param name nome dello slot
	 */
	public void setDefaultForeground(String name);

}

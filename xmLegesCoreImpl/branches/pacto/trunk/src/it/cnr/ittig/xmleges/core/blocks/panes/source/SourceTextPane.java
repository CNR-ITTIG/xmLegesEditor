package it.cnr.ittig.xmleges.core.blocks.panes.source;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextPane;

/**
 * Classe per il pannello di testo con il testo antialias.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class SourceTextPane extends JTextPane {
	boolean antialias;

	public SourceTextPane(boolean antialias) {
		super();
		this.antialias = antialias;
	}

	/**
	 * Usato solo per abilitare l'antialiasing del testo.
	 */
	protected void paintComponent(java.awt.Graphics g) {
		if (antialias)
			try {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			} catch (ClassCastException ex) {
			}
		super.paintComponent(g);
	}

}

package it.cnr.ittig.xmleges.core.blocks.panes.problems;

import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.panes.problems.Problem;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Renderer per la visualizzazione dei problemi nel pannello.
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
public class ProblemCellRenderer extends DefaultListCellRenderer {
	I18n i18n;

	/**
	 * Costruttore di <code>ProblemsFindIterator</code>.
	 */
	public ProblemCellRenderer(I18n i18n) {
		super();
		this.i18n = i18n;
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		Problem problem = (Problem) value;
		switch (problem.getType()) {
		case Problem.FATAL_ERROR:
			setIcon(i18n.getIconFor("panes.problems.fatalerror"));
			break;
		case Problem.ERROR:
			setIcon(i18n.getIconFor("panes.problems.error"));
			break;
		case Problem.WARNING:
			setIcon(i18n.getIconFor("panes.problems.warning"));
			break;
		case Problem.INFO:
			setIcon(i18n.getIconFor("panes.problems.info"));
			break;
		}
		setText(problem.getText());
		return this;
	}
}

package it.cnr.ittig.xmleges.core.blocks.bugreport;

import javax.swing.DefaultListModel;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */

public class BugReportAppender extends AppenderSkeleton {

	static DefaultListModel listModel = new DefaultListModel();

	static int maxLines = 10000;

	public boolean requiresLayout() {
		return true;
	}

	protected void append(LoggingEvent arg0) {
		String s = getLayout().format(arg0);
		if (listModel.size() > maxLines)
			listModel.remove(0);
		listModel.addElement(s);
		if (arg0.getThrowableStrRep()!=null) 
			appendStackTrace(arg0);
	}

	private void appendStackTrace(LoggingEvent arg0) {
		for (int i=0; i<arg0.getThrowableStrRep().length; i++) {
			if (listModel.size() > maxLines)
				listModel.remove(0);
			listModel.addElement(arg0.getThrowableStrRep()[i]);
		}
	}
	
	public void close() {
	}

	
	public static DefaultListModel getListModel() {
		return listModel;
	}

}

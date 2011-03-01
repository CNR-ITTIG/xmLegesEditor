package it.cnr.ittig.xmleges.core.blocks.form.date;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.form.date.DateForm;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.form.date.Date</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * Nessuna.
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
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
public class DateFormImpl implements DateForm, Loggable, Initializable, KeyListener {
	Logger logger;

	JFormattedTextField data;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		MaskFormatter mf = new MaskFormatter("##/##/####");
		mf.setPlaceholderCharacter('_');

		data = new JFormattedTextField(mf);
		data.setColumns(12);

		data.addKeyListener(this);
	}

	// //////////////////////////////////////////////////// CommonForm Interface
	public Component getAsComponent() {

		return data;
	}

	// //////////////////////////////////////////////// SourceDestList Interface
	public void set(java.util.Date date) {

		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(UtilDate.getDateFormat());
			data.setValue(sdf.format(date));
		} else {
			data.setValue("");
		}
	}

	// //////////////////////////////////////////////// ActionListener Interface
	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		String s = data.getText();
		if (data.getCaretPosition() == UtilDate.getDateFormat().length()) {
			data.setText(data.getText().replaceAll("_", "0"));
		}
		if (s.indexOf('_') >= 0)
			return;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(UtilDate.getDateFormat());
			java.util.Date d = sdf.parse(data.getText());
			int p = data.getCaretPosition();
			data.setText(sdf.format(d));
			data.setCaretPosition(p);
		} catch (ParseException ex) {
		}

	}

	public void keyTyped(KeyEvent e) {
	}

	public java.util.Date getAsDate() {
		java.util.Date d;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(UtilDate.getDateFormat());
			d = sdf.parse(data.getText());
			return d;
		} catch (ParseException ex) {
		}
		return null;
	}

	public String getAsString() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(UtilDate.getDateFormat());
			java.util.Date d = sdf.parse(data.getText());
			return sdf.format(d);
		} catch (ParseException ex) {
		}
		return null;
	}

	public String getAsYYYYMMDD() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		if (getAsDate() != null) {
			return sf.format(getAsDate());
		}
		return null;
	}
}

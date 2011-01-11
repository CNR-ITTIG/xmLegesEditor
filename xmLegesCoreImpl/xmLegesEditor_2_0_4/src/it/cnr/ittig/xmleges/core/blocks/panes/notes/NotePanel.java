package it.cnr.ittig.xmleges.core.blocks.panes.notes;

import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.awt.event.MouseListener;
import java.io.File;
import java.util.Date;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Pannello per la visualizzazione di una nota.
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class NotePanel extends JScrollPane {
	Date date;

	File file;

	boolean memory;

	JTextArea textPane = new JTextArea();

	/**
	 * Costruttore di <code>NotePanel</code>.
	 */
	public NotePanel() {
		this(new Date(System.currentTimeMillis()), null, true);
	}

	/**
	 * Costruttore di <code>NotePanel</code>.
	 */
	public NotePanel(Date date, File file, boolean memory) {
		setDate(date);
		setFile(file);
		setMemory(memory);
		setViewportView(textPane);
		textPane.setWrapStyleWord(true);
		textPane.setLineWrap(true);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		try {
			textPane.setText(UtilFile.fileToString(file));
		} catch (Exception ex) {

		}
	}

	public void setFileWithoutRead(File file) {
		this.file = file;
	}

	public boolean isMemory() {
		return memory;
	}

	public void setMemory(boolean memory) {
		this.memory = memory;
	}

	public void setText(String text) {
		textPane.setText(text);
	}

	public String getText() {
		return textPane.getText();
	}

	public boolean hasSelection() {
		return textPane.getSelectedText() != null;
	}

	public void replaceSelection(String t) {
		textPane.replaceSelection(t);
	}

	public void cut() {
		textPane.cut();
	}

	public void copy() {
		textPane.copy();
	}

	public void paste() {
		textPane.paste();
	}

	public void addMouseListener(MouseListener listener) {
		textPane.addMouseListener(listener);
	}
}

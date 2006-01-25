package it.cnr.ittig.xmleges.core.blocks.util.msg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

/**
 * Classe che visualizza una splash.
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
public class SplashWindow extends JWindow {
	long timeout = -1;

	public SplashWindow(Icon icon, String text, Frame f, long timeout) {
		super(f);
		this.timeout = timeout;
		JLabel l = new JLabel(text, icon, SwingConstants.CENTER);
		l.setVerticalTextPosition(SwingConstants.BOTTOM);
		l.setHorizontalTextPosition(SwingConstants.CENTER);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(l, BorderLayout.CENTER);
		pack();
		Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension lSize = l.getPreferredSize();
		setLocation(sSize.width / 2 - (lSize.width / 2), sSize.height / 2 - (lSize.height / 2));
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				setVisible(false);
				dispose();
			}
		});
		setVisible(true);
		if (timeout > 0) {
			Thread splashThread = new Thread(closerRunner, "SplashThread");
			splashThread.start();
		}
	}

	Runnable closerRunner = new Runnable() {
		public void run() {
			try {
				Thread.sleep(timeout);
			} catch (Exception e) {
				e.printStackTrace();
			}
			setVisible(false);
			dispose();
		}
	};

}
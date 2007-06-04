package it.cnr.ittig.services.manager;

import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Run {
	Logger logger = new Logger("cli-startup");

	ServiceManager serviceManager = new ServiceManager();

	JWindow splash;

	JProgressBar progress = new JProgressBar();

	public Run(InputStream is) {
		this(is, null);
	}

	public Run(InputStream is, String splashImage) {
		showSplash(true, splashImage);
		Document dom = UtilXml.readXML(is, false);
		Node log4jConf = UtilDom.findDirectChild(dom.getDocumentElement(), "log4j:configuration");
		Logger.init((Element) log4jConf);
		// properties di sistema in log
		Properties p = System.getProperties();
		for (Enumeration en = p.keys(); en.hasMoreElements();) {
			Object k = en.nextElement();
			Object v = p.get(k);
			logger.info(k + "=" + v);
		}

		NodeList nl = dom.getElementsByTagName("component");
		int len = nl.getLength();
		Vector toStart = new Vector();
		for (int i = 0; i < len; i++) {
			Node node = nl.item(i);
			ServiceContainer service = new ServiceContainer();
			service.setImpl(UtilDom.getAttributeValueAsString(node, "class"));
			service.setActivation(UtilDom.getAttributeValueAsString(node, "activation"));
			service.setLifeStyle(UtilDom.getAttributeValueAsString(node, "lifestyle"));

			Node conf = UtilDom.findDirectChild(node, "configuration");
			if (conf != null) {
				if (UtilDom.getAttributeValueAsBoolean(conf, "external"))
					service.setConfiguration(UtilDom.getAttributeValueAsString(conf, "resource"));
				else
					service.setConfiguration(new Configuration(UtilDom.findDirectChild(node, "configuration")));
			} else
				service.setConfiguration(new Configuration(null));

			try {
				serviceManager.addService(service);
			} catch (ServiceException ex) {
				logger.fatalError(ex.toString(), ex);
				System.exit(1);
			}

			if (service.isActivationStartup())
				toStart.addElement(service.getService());
		}

		progress.setMaximum(toStart.size());

		Enumeration en = toStart.elements();
		for (int i = 0; en.hasMoreElements(); i++)
			try {
				Class cl = (Class) en.nextElement();
				serviceManager.lookup(cl);
				progress.setValue(i);
			} catch (ServiceException ex) {
				logger.fatalError(ex.toString(), ex);
				System.exit(1);
			}
		serviceManager.setShutdownHook();
		showSplash(false, splashImage);
	}

	protected void showSplash(boolean show, String image) {
		if (show) {
			splash = new JWindow();

			JLabel splashImage = new JLabel(new ImageIcon(loadImage("" + image)));

			splash.getContentPane().add(splashImage, BorderLayout.CENTER);
			splash.getContentPane().add(progress, BorderLayout.SOUTH);

			splash.pack();

			Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension wSize = splash.getPreferredSize();
			splash.setLocation(sSize.width / 2 - (wSize.width / 2), sSize.height / 2 - (wSize.height / 2));
			splash.show();
		} else {
			splash.setVisible(false);
			splash.dispose();
		}
	}

	protected Image loadImage(String fileName) {
		Image image = null;
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		try {
			image = toolkit.getImage(fileName);
			if (image.getWidth(null) < 0) {
				URL url = getClass().getResource(fileName);
				if (url != null)
					image = toolkit.getImage(url);
				if (image.getWidth(null) < 0) {
					url = getClass().getResource("/" + fileName);
					if (url != null)
						image = toolkit.getImage(url);
				}
			}
		} catch (Exception ex) {
			logger.error("Error loading image: " + fileName, ex);
		}
		return image;
	}

	public final static void main(String[] args) {
		if (args.length == 1)
			try {
				new Run(new FileInputStream(args[0]));
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
		else if (args.length == 2)
			try {
				new Run(new FileInputStream(args[0]), args[1]);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
		else {
			System.err.println("Usage:");
			System.err.println("Run app.xml [splashImage]");
			System.err.println("ex:");
			System.err.println("Run xmLegesEditor.xml /images/editor/splash/xmLegesEditor.png");
		}
	}
}

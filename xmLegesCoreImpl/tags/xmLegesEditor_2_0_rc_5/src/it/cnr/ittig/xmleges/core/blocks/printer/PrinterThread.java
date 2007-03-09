package it.cnr.ittig.xmleges.core.blocks.printer;

import it.cnr.ittig.services.manager.Logger;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.RepaintManager;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;

/**
 * @version 1.0
 * @author Alessio Ceroni, Mirco Taddei
 */
public class PrinterThread implements Runnable, Printable {

	Logger logger;

	PrinterJob printerJob;

	Component component;

	Dimension origSize;

	JDialog dialog;

	JLabel pageLabel = new JLabel();

	protected int currentPage = -1;

	protected double pageStartY = 0;

	protected double pageEndY = 0;

	public PrinterThread(Logger logger, PrinterJob printerJob, Component component) {
		this.logger = logger;
		this.printerJob = printerJob;
		this.component = component;
		printerJob.setPrintable(this);
	}

	public void run() {
		try {
			origSize = component.getSize();
			// For faster printing, turn off double buffering
			if (component instanceof JComponent)
				RepaintManager.currentManager((JComponent) component).setDoubleBufferingEnabled(false);
			else
				RepaintManager.currentManager(component).setDoubleBufferingEnabled(false);
			printerJob.print();
			try {
				pageLabel.setText("Printing finished.");
				Thread.sleep(2000);
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
			dialog.dispose();
			dialog = null;
			component.setSize(origSize);
			component.invalidate();
		} catch (Exception ex) {
			logger.error("Error while printing", ex);
		}
	}

	/**
	 * Permette di effetuare il preview per scalare l'immagine del componente che deve
	 * essere stampato. TODO usare form
	 * 
	 * @param pageFormat formato della pagina
	 * @return <code>true</code> se pu&ograve; continuare la stampa
	 */
	protected boolean preview(PageFormat pageFormat) {
		if (dialog == null) {
			dialog = new JDialog();
			dialog.getContentPane().setLayout(new BorderLayout());
			dialog.getContentPane().add(pageLabel, BorderLayout.NORTH);
			dialog.getContentPane().add(component, BorderLayout.CENTER);
			int width = (int) pageFormat.getImageableWidth();
			int height = (int) pageFormat.getImageableHeight();
			dialog.setSize(width, height);
			component.setVisible(false);
			component.setVisible(true);
			// component.setSize(width, height);
			// component.repaint();
			// dialog.setLocation(10000, 10000);
			dialog.show();
			try {
				pageLabel.setText("Printing..");
				Thread.sleep(3000);
			} catch (Exception ex) {
				// TODO: handle exception
				logger.error(ex.toString(), ex);
			}
		}
		return true;
	}

	// ///////////////////////////////////////////////////// Printable Interface
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

		logger.info("Printing...");

		if (!preview(pageFormat))
			return Printable.NO_SUCH_PAGE;

		Graphics2D g2d = (Graphics2D) graphics;
		// Set default foreground color to black
		// g2d.setColor(Color.black);
		// Shift Graphic to line up with beginning of print-imageable region
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		if (pageIndex > currentPage) {
			currentPage = pageIndex;
			pageStartY += pageEndY;
			pageLabel.setText("Pag: " + (pageIndex + 1));
			pageEndY = g2d.getClipBounds().getHeight();
		}

		g2d.translate(g2d.getClipBounds().getX(), g2d.getClipBounds().getY());
		Rectangle allocation = new Rectangle(0, (int) -pageStartY, component.getWidth(), component.getHeight());

		if (component instanceof JTextComponent) {
			JTextComponent textComp = (JTextComponent) component;
			/*
			 * try { Thread.sleep(1000); } catch (Exception ex) { }
			 */
			View rootView = textComp.getUI().getRootView(textComp);
			if (printView(g2d, allocation, rootView))
				return Printable.PAGE_EXISTS;
		} else
			// TODO fare print per altri oggetti
			throw new PrinterException("Component not supported:" + component.getClass().getName());

		currentPage = -1;
		pageStartY = 0.0;
		pageEndY = 0.0;

		return Printable.NO_SUCH_PAGE;
	}

	/**
	 * Metodo per stampare componenti di tipo <code>javax.swing.text.JTextComponent</code>.
	 * 
	 * @param g2d
	 * @param alloc
	 * @param view
	 * @return
	 */
	protected boolean printView(Graphics2D g2d, Shape alloc, View view) {
		boolean pageExists = false;
		Rectangle clip = g2d.getClipBounds();
		Shape childAlloc;
		View childView;
		if (view.getViewCount() > 0) {
			for (int i = 0; i < view.getViewCount(); i++) {
				childAlloc = view.getChildAllocation(i, alloc);
				if (childAlloc != null) {
					childView = view.getView(i);
					if (printView(g2d, childAlloc, childView)) {
						pageExists = true;
					}
				}
			}
		} else {
			if (alloc.getBounds().getMaxY() >= clip.getY()) {
				pageExists = true;
				// I
				if ((alloc.getBounds().getHeight() > clip.getHeight()) && (alloc.intersects(clip))) {
					logger.debug("Calling view.paint() I");
					view.paint(g2d, alloc);
				} else {
					// II
					if (alloc.getBounds().getY() >= clip.getY()) {
						if (alloc.getBounds().getMaxY() <= clip.getMaxY()) {
							if (logger.isDebugEnabled()) {
								logger.debug("Calling view.paint() II");
								logger.debug("Calling view.paint() II: class=" + view.getClass().getName());
								logger.debug("Calling view.paint() II: element=" + view.getElement().getName());
							}
							view.paint(g2d, alloc);
						} else {
							if (alloc.getBounds().getY() < pageEndY) {
								pageEndY = alloc.getBounds().getY();
							}
						}
					}
				}
			}
		}
		return pageExists;
	}

}

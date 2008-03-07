package it.cnr.ittig.xmleges.core.blocks.printer;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.printer.Printer;

import java.awt.Component;
import java.awt.print.PrinterJob;

/**
 * Implementazione del servizio it.cnr.ittig.xmleges.editor.services.printer.Printer.
 * 
 * @see it.cnr.ittig.xmleges.core.services.printer.Printer
 * @see java.awt.print.Printable
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class PrinterImpl implements Printer, Loggable, Initializable {
	Logger logger;

	PrinterJob printerJob;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		printerJob = PrinterJob.getPrinterJob();
	}

	// /////////////////////////////////////////////////////// Printer Interface
	public void print(Component component) {
		try {
			if (printerJob.printDialog()) {
				PrinterThread pThread = new PrinterThread(logger, printerJob, component);
				new Thread(pThread).start();
			}
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
	}
}

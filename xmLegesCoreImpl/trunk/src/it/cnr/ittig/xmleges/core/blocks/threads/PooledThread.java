package it.cnr.ittig.xmleges.core.blocks.threads;

/**
 * Thread per l'esecuzione dell'oggetto invocato con il metodo
 * <code>ThreadManager.execute(Runnable)</code>.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class PooledThread extends Thread {
	int n;

	Runnable runnable;

	String name;
	
	String nameWait;
	
	String nameExec;

	ThreadManagerImpl threadManagerImpl;

	public PooledThread(ThreadManagerImpl threadManagerImpl, int n) {
		this.threadManagerImpl = threadManagerImpl;
		this.n = n;
		name = "PoolThread-" + n;
		nameWait = name + "-W";
		nameExec = name + "-E-";
		setName(name);
		start();
	}

	public void set(Runnable runnable) {
		synchronized (this) {
			this.runnable = runnable;
//			setName(nameExec + runnable.toString());
			notify();
		}
	}

	public void run() {
		setName(nameWait);
		while (true) {
			synchronized (this) {
				try {
					wait();
					setName(nameExec + runnable.toString());
					runnable.run();
				} catch (InterruptedException ie) {
					interrupted();
					threadManagerImpl.logger.error("An interrupted exception occured while waiting.", ie);
				} catch (Throwable tr) {
					threadManagerImpl.logger.error("Exception in internal runnable.", tr);
				}
				threadManagerImpl.pool.addElement(this);
				setName(nameWait);
			}
		}
	}
}

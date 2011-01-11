package it.cnr.ittig.xmleges.core.blocks.action.connect;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.connect.ConnectAction;
import it.cnr.ittig.xmleges.core.services.action.file.open.FileOpenAction;
import it.cnr.ittig.xmleges.core.services.action.file.save.FileSaveAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.domwriter.DOMWriter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.EventObject;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.methods.LockMethod;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.connect.ConnectAction</code>.</h1>
 * 
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
 */

public class ConnectActionImpl implements ConnectAction, EventManagerListener, Loggable, Serviceable, Initializable, Startable {

	HttpURL httpURL;

	WebdavResource webdavResource = null;
	
	WebdavResource[] list;
	
	Logger logger;

	PreferenceManager preferenceManager;
	
	ActionManager actionManager;

	UtilMsg utilMsg;

	DocumentManager documentManager;
	
	EventManager eventManager;
	
	WebdavOpenAction webdavOpenAction;
	
	WebdavSaveAction webdavSaveAction;
	
	AzioneConnetti connectAction;
	
	AzioneDisconnetti disconnectAction;
	
	AzioneCollection collectionAction; 
	
	AzioneDelete deleteAction;
	
	AzioneLock lockAction;
	
	AzioneUnlock unlockAction;
	
	AzioneRefresh refreshAction;
	
	AzioneHistory historyAction;
	
	FileOpenAction fileOpenAction;
	
	FileSaveAction fileSaveAction;
	
	Frame frame;
	
	Form form;
	Form formNewFolder;
	
	WebdavResource fileLocked = null;
	
	JLabel typehost;
	JLabel etiUser;
	JLabel etiPassword;
	JLabel etiDir;
	JTextField newDir;
	JLabel currentDir;
	JTextField host;
	JTextField user;
	JPasswordField password;
	JTextField file;
	JLabel etifile;
	JLabel currDir;
	
	JList filedir;
	I18n i18n;
	Icon tipoDirectory;
	Icon tipoFile;
	Icon tipoLock;
	
	JButton connect;
	JButton disconnect;
	JButton collection;
	JButton delete;
	JButton lock;
	JButton unlock;
	JButton refresh;
	JButton history;

	String lastDavHost = null;
	String lastDavUser = null;
	String lastDavPass = null;
	
	String separatore;
	
	MouseAdapter mouseAdapter = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e) && filedir.getSelectedIndex()!=-1 && e.getClickCount() == 2) {
				
				if (filedir.getSelectedIndex()==0)
					readDirDAV("..");
				else {
					WebdavResource selezione = list[filedir.getSelectedIndex()-1];
					if(selezione.isCollection())
						readDirDAV(selezione.getDisplayName());
					else {
						file.setText(selezione.getDisplayName());
						file.setEnabled(true);
					}	
				}
			}
		}
	};
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		frame = (Frame) serviceManager.lookup(Frame.class);
		form = (Form) serviceManager.lookup(Form.class);
		formNewFolder = (Form) serviceManager.lookup(Form.class);
		fileOpenAction = (FileOpenAction) serviceManager.lookup(FileOpenAction.class); 
		fileSaveAction = (FileSaveAction) serviceManager.lookup(FileSaveAction.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}


	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		
		webdavOpenAction = new WebdavOpenAction();
		actionManager.registerAction("connect.webdav.open", webdavOpenAction);		
		webdavSaveAction = new WebdavSaveAction();
		actionManager.registerAction("connect.webdav.save", webdavSaveAction);	
		connectAction = new AzioneConnetti();
		actionManager.registerAction("editor.form.connetti.connect", connectAction);
		disconnectAction = new AzioneDisconnetti();
		actionManager.registerAction("editor.form.connetti.disconnect", disconnectAction);
		collectionAction = new AzioneCollection();
		actionManager.registerAction("editor.form.connetti.collection", collectionAction);
		deleteAction = new AzioneDelete();
		actionManager.registerAction("editor.form.connetti.delete", deleteAction);
		lockAction = new AzioneLock();
		actionManager.registerAction("editor.form.connetti.lock", lockAction);
		unlockAction = new AzioneUnlock();
		actionManager.registerAction("editor.form.connetti.unlock", unlockAction);
		refreshAction = new AzioneRefresh();
		actionManager.registerAction("editor.form.connetti.refresh", refreshAction);
		historyAction = new AzioneHistory();
		actionManager.registerAction("editor.form.connetti.history", historyAction);	

		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		
		tipoDirectory = i18n.getIconFor("editor.form.connetti.icondir");
		tipoFile = i18n.getIconFor("editor.form.connetti.icondocument");
		tipoLock = i18n.getIconFor("editor.form.connetti.iconlock");
		form.setSize(650, 530);
		form.setName("file.connect.form");
		form.setMainComponent(getClass().getResourceAsStream("Connetti.jfrm"));
		form.setHelpKey("help.contents.form.connect");
		
		formNewFolder.setSize(300, 160);
		formNewFolder.setName("file.connect.form.newfolder");
		formNewFolder.setMainComponent(getClass().getResourceAsStream("NewFolder.jfrm"));
		currentDir = (JLabel) formNewFolder.getComponentByName("editor.form.connetti.dir");
		newDir = (JTextField) formNewFolder.getComponentByName("editor.form.connetti.newfolder");
		
		typehost = (JLabel) form.getComponentByName("editor.form.connetti.typehost");
		etiUser = (JLabel) form.getComponentByName("editor.form.connetti.etiUser");
		etiPassword = (JLabel) form.getComponentByName("editor.form.connetti.etiPassword");
		etiDir = (JLabel) form.getComponentByName("editor.form.connetti.etiDir");
		host = (JTextField) form.getComponentByName("editor.form.connetti.host");
		user = (JTextField) form.getComponentByName("editor.form.connetti.user");
		password = (JPasswordField) form.getComponentByName("editor.form.connetti.password");
		file = (JTextField) form.getComponentByName("editor.form.connetti.file");
		etifile = (JLabel) form.getComponentByName("editor.form.connetti.etifile");
		etifile.setText("File: ");
		currDir = (JLabel) form.getComponentByName("editor.form.connetti.dir");
		filedir = (JList) form.getComponentByName("editor.form.connetti.filedir");
		filedir.addListSelectionListener(new ListSelectionListener() {
		      public void valueChanged(ListSelectionEvent evt) {		        
		    	  if (filedir.getSelectedIndex()>0) {
		    		  WebdavResource selezione = list[filedir.getSelectedIndex()-1];
		    		  if(!selezione.isCollection())
		    			  file.setText(selezione.getDisplayName());
		    	  }
		      }
		    });
		filedir.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		filedir.setEnabled(true);
			
		connect = (JButton) form.getComponentByName("editor.form.connetti.connect");
		connect.addActionListener(connectAction);
		disconnect = (JButton) form.getComponentByName("editor.form.connetti.disconnect");
		disconnect.addActionListener(disconnectAction);
		collection = (JButton) form.getComponentByName("editor.form.connetti.collection");
		collection.addActionListener(collectionAction);
		delete = (JButton) form.getComponentByName("editor.form.connetti.delete");
		delete.addActionListener(deleteAction);
		lock = (JButton) form.getComponentByName("editor.form.connetti.lock");
		lock.addActionListener(lockAction);
		unlock = (JButton) form.getComponentByName("editor.form.connetti.unlock");
		unlock.addActionListener(unlockAction);
		refresh = (JButton) form.getComponentByName("editor.form.connetti.refresh");
		refresh.addActionListener(refreshAction);
		history = (JButton) form.getComponentByName("editor.form.connetti.history");
		history.addActionListener(historyAction);
		history.setVisible(false); //non implementata
		
		Properties p = preferenceManager.getPreferenceAsProperties(this.getClass().getName());
		try {
			lastDavHost = p.getProperty("lastdavhost");
			lastDavUser = p.getProperty("lastdavuser");
		} catch (Exception ex) {}
		manageEvent(null);
		
		if (System.getProperty("os.name").toLowerCase().matches("windows.*"))
			separatore = "\\";
		else
			separatore = "/";
	}

	// /////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {}

	public void stop() throws Exception {

		logger.debug("saving last host/user/pass...");
		Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
		try {
			p.setProperty("lastdavhost", lastDavHost);
			p.setProperty("lastdavuser", lastDavUser);
		} catch (Exception ex) {
		}
		preferenceManager.setPreference(getClass().getName(), p);
		logger.debug("saving OK");
		try {
			if (fileLocked != null) {
				fileLocked.unlockMethod();
				logger.debug("Tento lo sblocco di: " + fileLocked.getDisplayName());
			}
		} catch (Exception e) {}	
	}

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		webdavOpenAction.setEnabled(true);
		webdavSaveAction.setEnabled(!documentManager.isEmpty());
	}

	protected class AzioneConnetti extends AbstractAction {
		public void actionPerformed(ActionEvent e) {


				if (openDAV(host.getText(), user.getText(), password.getText())) {
					if (!readDirDAV(""))
						utilMsg.msgError("connect.webdav.dirdav.error");
					else {
						lastDavHost=host.getText();
						lastDavUser=user.getText();
						lastDavPass=password.getText();
						connect.setEnabled(false);
						disconnect.setEnabled(true);
					}
				} else  utilMsg.msgError("connect.dav.open.error");
		}
	}	
	
	protected class AzioneDisconnetti extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			closeDAV();
			initField();
		}
	}	
			
	protected class AzioneCollection extends AbstractAction {
		public void actionPerformed(ActionEvent e) {		
			try {
				currentDir.setText("Dir: " + currDir.getText());
				formNewFolder.showDialog();
				if (formNewFolder.isOk()) {
					webdavResource.mkcolMethod(webdavResource.getPath()+"/"+newDir.getText()); 
					readDirDAV("");
				}	
			} catch (Exception ex) {}			
		}
	}	
	
	protected class AzioneDelete extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			try {
				WebdavResource temp = list[filedir.getSelectedIndex()-1];	
				temp.deleteMethod();
				readDirDAV("");
			} catch (Exception ex) {}
		}
	}
	
	protected class AzioneLock extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			try {
				WebdavResource temp = list[filedir.getSelectedIndex()-1];	
				temp.lockMethod();
			} catch (Exception ex) {}
			readDirDAV("");
		}
	}
	
	protected class AzioneUnlock extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			try {
				WebdavResource temp = list[filedir.getSelectedIndex()-1];	
				temp.unlockMethod();
			} catch (Exception ex) {}
			readDirDAV("");
		}
	}
	
	protected class AzioneRefresh extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			readDirDAV("");
		}
	}
	
	protected class AzioneHistory extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			System.err.println("6 - history");
		}
	}
	
	private void initField() {
		
		if (webdavResource != null)
			if (readDirDAV("")) {
				connect.setEnabled(false);
				disconnect.setEnabled(true);
				return;
			}	

		connect.setEnabled(true);
		disconnect.setEnabled(false);

		host.setText(lastDavHost);
		user.setText(lastDavUser);
		password.setText(lastDavPass);
		typehost.setText("Host");
			
		etiDir.setVisible(false);
		collection.setEnabled(false);
		delete.setEnabled(false);
		lock.setEnabled(false);
		unlock.setEnabled(false);
		refresh.setEnabled(false);
		history.setEnabled(false);		
		
		filedir.setCellRenderer( new davListCellRenderer());

		currDir.setText("*** non connesso ***");
		file.setText("");
		file.setEnabled(false);
		filedir.removeMouseListener(mouseAdapter);
		
		filedir.setListData(new Object[0]);	
	}
		
	protected class WebdavOpenAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			initField();
			etifile.setVisible(false);
			file.setVisible(false);
			form.setName("file.connect.form-download");
			form.showDialog();
			if (form.isOk()) {
				String nomefile="";
				try {
					nomefile = list[filedir.getSelectedIndex()-1].getDisplayName();
				} catch (Exception ex) {}	
				if (!readDAV(nomefile,""))
					utilMsg.msgError("connect.webdav.read.error");
			}	
		}
	}
	
	protected class WebdavSaveAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			initField();
			String nomefile=documentManager.getSourceName();
			try {
				nomefile = nomefile.substring(1+nomefile.lastIndexOf(separatore),nomefile.length());
			} catch (Exception ex) {
				nomefile="";
			}
			file.setText(nomefile);
			etifile.setVisible(true);
			file.setVisible(true);
			form.setName("file.connect.form-upload");
			form.showDialog();
			if (form.isOk())
				if (!writeDAV(file.getText(),""))
					utilMsg.msgError("connect.webdav.wite.error");
		}
	}
	
	private boolean openDAV(String url, String user, String password) {
						
		if (url==null)
			return false;
		if (url.substring(url.length()-1, url.length()).equals("/"))
			url=url.substring(0, url.length()-1);
		currDir.setText(">>> Connetto ...");
        try {        	
        	if (url.startsWith("https"))
        		httpURL = new HttpsURL(url);
        	else
        		httpURL = new HttpURL(url);
            if (webdavResource == null) {
                webdavResource = new WebdavResource(httpURL);
                webdavResource.setDebug(0);
            } else {
                webdavResource.close();
                webdavResource.setHttpURL(url);
            }

        } catch (HttpException we) {
        	if (we.getReasonCode() == HttpStatus.SC_UNAUTHORIZED) {
        		try {
                    try {
                        if (webdavResource != null)
                            webdavResource.close();
                    } catch (IOException e) {
                    } finally {
                        httpURL = null;
                        webdavResource = null;
                    }
                	if (url.startsWith("https"))
                		httpURL = new HttpsURL(url);
                	else
                		httpURL = new HttpURL(url);
                    httpURL.setUserinfo(user, password);
                    if (webdavResource != null) {
                        webdavResource.setHttpURL(url);                        
                    } else {
                        webdavResource = new WebdavResource(httpURL);
                        webdavResource.setDebug(0);
                    }
       
                } catch (HttpException e) {
                	utilMsg.msgError("Error " + e.getReasonCode() + ": " + e.getMessage());
                    httpURL = null;
                    currDir.setText("*** non connesso ***");
        		    return false;
                } catch (IOException e) {
                	utilMsg.msgError("Error: " + e.getMessage());
                    httpURL = null;
                    currDir.setText("*** non connesso ***");
        		    return false;
                }
        	}	
        	else {
        		utilMsg.msgError("Error " + we.getReasonCode() + ": " + we.getMessage());
        		httpURL = null;
        		currDir.setText("*** non connesso ***");
    		    return false;
            }		
        } catch (Exception e) {
        	utilMsg.msgError("Error: " + e.getMessage());
            httpURL = null;
            currDir.setText("*** non connesso ***");
		    return false;
        }			

        etiDir.setVisible(true);
        currDir.setText(webdavResource.getPath());
        filedir.addMouseListener(mouseAdapter);
        file.setEnabled(true);
		collection.setEnabled(true);
		delete.setEnabled(true);
		lock.setEnabled(true);
		unlock.setEnabled(true);
		refresh.setEnabled(true);
		history.setEnabled(true);
	    return true;
	}
	
	private boolean closeDAV(){	
		try {
			if (webdavResource != null)
				webdavResource.close();
		} catch (IOException e) {
		} finally {
			httpURL = null;
        	webdavResource = null;
		}
		return true;
	}
			
	private boolean writeDAV(String fileName, String dir){	
		try {
			if (fileName.equals(""))
				return false;
			if (!cwdDAV(dir))
				return false;
			
			File file=new File(UtilFile.getTempDirName()+ File.separatorChar +"tempDAV.xml");
			String encoding;
			if (documentManager.getEncoding() == null) {
				logger.warn("No encoding found. Using default: UTF-8");
				encoding = "UTF-8";
			} else
				encoding = documentManager.getEncoding();
			DOMWriter domWriter = new DOMWriter();
			domWriter.setCanonical(false);
			domWriter.setFormat(true);
			try {
				domWriter.setOutput(file, encoding);
				domWriter.write(documentManager.getDocumentAsDom());
				documentManager.setChanged(false);
				documentManager.setSourceName(file.getAbsolutePath());
				documentManager.setNew(false);
			} catch (UnsupportedEncodingException ex) {
				utilMsg.msgError("action.file.save.error.encoding");
				logger.error(ex.toString(), ex);
			} catch (FileNotFoundException ex) {
				utilMsg.msgError("action.file.save.error.file");
				logger.error(ex.toString(), ex);
			}			
			UtilFile.copyFile(UtilFile.getTempDirName()+ File.separatorChar +"tempDAV.xml", UtilFile.getTempDirName()+ File.separatorChar +"tempDAV2.xml");
			File file2= UtilFile.getFileFromTemp("tempDAV2.xml");
			
			logger.debug("Write: " + httpURL.getScheme()+"//"+httpURL.getHost()+":"+httpURL.getPort()+webdavResource.getPath()+"/"+fileName);			
			
			//potrei fare upload con nome diverso dal download
			if (fileLocked!=null && !(webdavResource.getPath()+"/"+fileName).equals(fileLocked.getPath()))
				fileLocked.unlockMethod();
			
			webdavResource.unlockMethod(webdavResource.getPath()+"/"+fileName, password.getText());
			
			//indico che non ho file bloccati
			fileLocked = null;
						
			if (!webdavResource.putMethod(webdavResource.getPath()+"/"+fileName, file2))
				utilMsg.msgError(webdavResource.getStatusCode() + ": " + webdavResource.getStatusMessage());
		
        } catch (HttpException we) {
        	utilMsg.msgError("Error " + we.getReasonCode() + ": " + we.getMessage());
			return false;							
		} catch (IOException e) {
			utilMsg.msgError("Error: " + e.getMessage());
			return false;
		}
		return true;
	}

	private boolean readDirDAV(String dir){	
		try {
			if (!cwdDAV(dir))
				return false;
			list = webdavResource.listWebdavResources();
			
			WebdavResource[] listafile = new WebdavResource[list.length+1];
        	listafile[0] = new WebdavResource(httpURL);
        	listafile[0].setPath(webdavResource.getPath());
            for (int i = 0; i < list.length; i++) 
            	listafile[i+1] = list[i];
            	
			filedir.setListData(listafile);

		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private boolean readDAV(String fileName, String dir){	
		try {
			if (fileName.equals(""))
				return false;
			if (!cwdDAV(dir))
				return false;
			
			try {
				if (fileLocked != null)
					fileLocked.unlockMethod();
			} catch (Exception e) {}
			
			File file=new File(UtilFile.getTempDirName()+ File.separatorChar +fileName);

			logger.debug("Read: " + httpURL.getScheme()+"//"+httpURL.getHost()+":"+httpURL.getPort()+webdavResource.getPath()+"/"+fileName);
			try {
			webdavResource.lockMethod(webdavResource.getPath()+"/"+fileName, password.getText(), LockMethod.DEPTH_INFINITY, LockMethod.SCOPE_EXCLUSIVE);			
			} catch (Exception nl) {
				logger.debug("Non riesco a fare lock di: " + httpURL.getScheme()+"//"+httpURL.getHost()+":"+httpURL.getPort()+webdavResource.getPath()+"/"+fileName);
			}
			
			if (!webdavResource.getMethod(webdavResource.getPath()+"/"+fileName, file))
				utilMsg.msgError(webdavResource.getStatusCode() + ": " + webdavResource.getStatusMessage());				

			//Tengo traccia del file che sto aprendo e bloccando
			fileLocked = list[filedir.getSelectedIndex()-1];
			
			fileOpenAction.doOpen(file.getAbsolutePath(), false);

        } catch (HttpException we) {
        	utilMsg.msgError("Error " + we.getReasonCode() + ": " + we.getMessage());
			return false;										
		} catch (Exception ex) {
			return false;
		}
		return true;
	}
			
	private boolean cwdDAV(String dir){
		String vecchiaDir = webdavResource.getPath();
		try {
			if (!dir.equals(""))
				if (dir.equals("..")) {
					webdavResource.setPath(webdavResource.getPath().substring(0,webdavResource.getPath().lastIndexOf('/')));
					if (webdavResource.getPath().length()<60)
    					currDir.setText(webdavResource.getPath());
    				else
    					currDir.setText("... " + webdavResource.getPath().substring(webdavResource.getPath().length()-60,webdavResource.getPath().length()));					
				}	
				else {			 
					webdavResource.setPath(webdavResource.getPath()+ '/' +  dir);
					if (webdavResource.exists()) 
		                if (webdavResource.isCollection()) { 
		    				if (webdavResource.getPath().length()<60)
		    					currDir.setText(webdavResource.getPath());
		    				else
		    					currDir.setText("... " + webdavResource.getPath().substring(webdavResource.getPath().length()-60,webdavResource.getPath().length()));
		                	return true;
		                }	
					return false;					
				}			
		} catch (HttpException we) {
			utilMsg.msgError("Error " + we.getReasonCode() + ": " + we.getMessage());
			try {
				webdavResource.setPath(vecchiaDir);
			} catch (Exception e) {}
			return false;
		} catch (IOException e) {
			return false;
		}	
		return true;
	}	
	
	public class davListCellRenderer extends JLabel implements ListCellRenderer {

		public davListCellRenderer() {
			setOpaque(true);
			setVerticalAlignment(CENTER);
		}
	
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if(value instanceof WebdavResource){
				setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
				setText(((WebdavResource)value).getDisplayName());
				if (((WebdavResource)value).isCollection()) {
					setIcon(tipoDirectory);
					try {
					if (webdavResource.getPath().equals(((WebdavResource)value).getPath()))
						setText("..");
					} catch (Exception e) {}
				} else
					if (((WebdavResource)value).isLocked())
						setIcon(tipoLock);
					else
						setIcon(tipoFile);
			}			
			return this;
		}
	}
}


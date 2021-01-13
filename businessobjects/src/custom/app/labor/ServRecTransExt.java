package custom.app.labor;

import psdi.util.MXException;
import psdi.app.labor.ServRecTrans;
import java.rmi.RemoteException;
import psdi.mbo.MboSet;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class ServRecTransExt extends ServRecTrans implements
		ServRecTransExtRemote {

	final private String APPLOGGER = "maximo.application.LABOR";
	private MXLogger log;

	public ServRecTransExt(MboSet mboSet0) throws MXException, RemoteException {
		super(mboSet0);
		log = MXLoggerFactory.getLogger(APPLOGGER);
	}

	public void init() throws MXException {
		//TODO Auto-generated method stub
		super.init();
		log.debug("ServRecTransExt.init");

	}

	public void add() throws MXException, RemoteException {
		//TODO Auto-generated method stub
		super.add();
		log.debug("ServRecTransExt.add");

	}
	
	public void save() throws MXException, RemoteException {
		super.save();
		
	}

}//class 
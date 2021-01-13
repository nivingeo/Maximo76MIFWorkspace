package custom.app.labor;

import psdi.util.MXException;
import psdi.app.labor.ServRecTransSet;
import psdi.app.labor.ServRecTrans;
import psdi.mbo.MboServerInterface;
import java.rmi.RemoteException;
import psdi.mbo.MboSet;
import psdi.mbo.Mbo;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class ServRecTransExtSet extends ServRecTransSet implements
		ServRecTransExtSetRemote {

	final private String APPLOGGER = "maximo.application.LABOR";
	private MXLogger log;

	public ServRecTransExtSet(MboServerInterface mboServerInterface0)
			throws MXException, RemoteException {
		super(mboServerInterface0);
		log = MXLoggerFactory.getLogger(APPLOGGER);
	}

	protected Mbo getMboInstance(MboSet mboSet0) throws MXException,
			RemoteException {
		log.debug("ServRecTransExtSet.getMboInstance");
		return new ServRecTransExt(mboSet0);
	}

}//class 
package custom.app.invoice;

import psdi.app.invoice.InvoiceLine;
import psdi.util.MXException;
import java.rmi.RemoteException;
import psdi.mbo.MboSet;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class InvoiceLineExt extends InvoiceLine implements InvoiceLineExtRemote {

	final private String APPLOGGER = "maximo.application.INVOICE";
	private MXLogger log;

	public InvoiceLineExt(MboSet mboSet0) throws RemoteException {
		super(mboSet0);
		log = MXLoggerFactory.getLogger(APPLOGGER);
	}

	public void init() throws MXException {
		//TODO Auto-generated method stub
		super.init();
		log.debug("InvoiceLineExt.init");

	}

	public void add() throws MXException, RemoteException {
		//TODO Auto-generated method stub
		super.add();
		log.debug("InvoiceLineExt.add");

	}

}//class 
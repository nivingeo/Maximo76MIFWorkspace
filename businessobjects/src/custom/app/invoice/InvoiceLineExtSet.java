package custom.app.invoice;

import psdi.app.invoice.InvoiceLine;
import psdi.util.MXException;
import psdi.app.invoice.InvoiceLineSet;
import psdi.mbo.MboServerInterface;
import java.rmi.RemoteException;
import psdi.mbo.MboSet;
import psdi.mbo.*;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class InvoiceLineExtSet extends InvoiceLineSet implements
		InvoiceLineExtSetRemote {

	final private String APPLOGGER = "maximo.application.INVOICE";
	private MXLogger log;

	public InvoiceLineExtSet(MboServerInterface mboServerInterface0)
			throws RemoteException {
		super(mboServerInterface0);
		log = MXLoggerFactory.getLogger(APPLOGGER);
	}

	protected Mbo getMboInstance(MboSet mboSet0) throws MXException,
			RemoteException {
		log.debug("InvoiceLineExtSet.getMboInstance");
		return new InvoiceLineExt(mboSet0);
	}
	
	public void canAdd()
    throws MXException
{
    return;
}


}//class 
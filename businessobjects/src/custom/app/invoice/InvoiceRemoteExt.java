package custom.app.invoice;

import java.rmi.RemoteException;
import psdi.mbo.*;
import psdi.app.invoice.InvoiceRemote;
import psdi.util.MXException;

public interface InvoiceRemoteExt extends InvoiceRemote
{
	public void add() throws RemoteException, MXException
		 ;

	public void matchTotals()     throws MXException, RemoteException
		;


}

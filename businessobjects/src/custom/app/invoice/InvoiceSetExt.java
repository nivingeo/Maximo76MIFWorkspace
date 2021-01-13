package custom.app.invoice;

import java.rmi.RemoteException;

import psdi.app.invoice.InvoiceSet;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class InvoiceSetExt extends InvoiceSet implements InvoiceSetRemoteExt
{
	public InvoiceSetExt(MboServerInterface arg0) throws MXException, RemoteException
	{
		super(arg0);
	}
	
	protected Mbo getMboInstance(MboSet arg0) throws MXException, RemoteException
	{
		return new InvoiceExt(arg0);
	}
}

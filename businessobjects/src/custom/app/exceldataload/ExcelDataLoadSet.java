package custom.app.exceldataload;

import psdi.util.MXException;
import psdi.mbo.MboServerInterface;
import java.rmi.RemoteException;
import psdi.mbo.Mbo;
import psdi.mbo.MboSet;

public class ExcelDataLoadSet extends MboSet implements ExcelDataLoadSetRemote {

	public ExcelDataLoadSet(MboServerInterface mboServerInterface0)
			throws RemoteException {
		super(mboServerInterface0);
	}

	protected Mbo getMboInstance(MboSet mboSet0) throws MXException,
			RemoteException {
		return new ExcelDataLoad(mboSet0);
	}

}//class 
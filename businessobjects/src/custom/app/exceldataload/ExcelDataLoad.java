package custom.app.exceldataload;

import psdi.util.MXException;
import java.rmi.RemoteException;
import psdi.mbo.MboSet;
import psdi.mbo.Mbo;

public class ExcelDataLoad extends Mbo implements ExcelDataLoadRemote {

	public ExcelDataLoad(MboSet mboSet0) throws RemoteException {
		super(mboSet0);
	}

	public void init() throws MXException {
		//TODO Auto-generated method stub
		super.init();

	}

	public void add() throws MXException, RemoteException {
		//TODO Auto-generated method stub
		super.add();
		getMboValue("exceldataloadid").generateUniqueID();

	}

}//class 
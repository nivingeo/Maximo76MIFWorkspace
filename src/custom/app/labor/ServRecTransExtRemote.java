package custom.app.labor;

import psdi.app.labor.ServRecTransRemote;
import psdi.util.MXException;
import java.rmi.RemoteException;


public interface ServRecTransExtRemote extends ServRecTransRemote{

	public String getAccountID(String orgID, String glAccount)
				throws MXException,RemoteException ;

	public void populateGL(long externalRefID, String sTable, String sMXOrgID,
				String sTransactionType, String sValueList)
						throws RemoteException, MXException ;

	public String getProjectNumber(String sRefWO, String sSiteID) throws MXException,RemoteException ;



}//interface 
                                                                                                                                                                                                                                                                                                                                                          
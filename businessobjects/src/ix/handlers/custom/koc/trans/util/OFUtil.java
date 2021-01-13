package ix.handlers.custom.koc.trans.util;
//Praxis - Rojitha - Util class for Journal Processing
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.SqlFormat;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;

public class OFUtil {
	MboRemote ixHandler;

	// IX ix;

	public OFUtil(MboRemote ixHandler) {
		this.ixHandler = ixHandler;
		// this.ix = ixHandler.ix;

	}

	public String getProjectType(String projectNum, MboRemote mbo)
			throws MXException, RemoteException {

		MboSetRemote projectSet = mbo.getMboSet("FINCNTRL");
		MboRemote projectMbo;
		SqlFormat sqf = new SqlFormat("FCTYPE = 'PROJECT'  AND PROJECTID = :1");
		sqf.setObject(1, "FINCNTRL", "PROJECTID", projectNum);
		projectSet.setWhere(sqf.format());
		projectSet.reset();
		
		if ((projectSet != null) && (!projectSet.isEmpty())) {
			projectMbo = projectSet.getMbo(0);
			if (projectMbo.getString("PROJECTTYPE") == null
					|| (!projectMbo.getString("PROJECTTYPE").toUpperCase()
							.startsWith("REPEX") && !projectMbo
							.getString("PROJECTTYPE").toUpperCase()
							.startsWith("CAPEX"))) {
				throw new MXApplicationException("projectTypeUnknown",
						projectMbo.getString("PROJECTTYPE"));
			}
			return projectMbo.getString("PROJECTTYPE").substring(0, 5)
					.toUpperCase();
		} else {
			throw new MXApplicationException("projectNotFound", projectNum);
		}

	}

	
	public String getGLPeriod(Date transdate, MboRemote transactionMbo)
			throws MXException, RemoteException, ParseException {
		
		String finPeriod = "";
		SimpleDateFormat sdfS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		
		Date date = sdfS.parse(transdate.toString());
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String formattedTransDate = sdf.format(date);
		
		
		MboRemote periodMbo;
		MboSetRemote periodSet ;
		periodSet=transactionMbo.getMboSet("FINANCIALPERIODS");
		periodSet.setWhere("PERIODCLOSEDATE IS NULL AND PERIODSTART <= to_date('"
						+ formattedTransDate
						+ "', 'DD-MM-YYYY') AND PERIODEND >= to_date('"
						+ formattedTransDate + "','DD-MM-YYYY')");
		periodSet.reset();
		

		if (periodSet != null && periodSet.count()>0) {
			periodMbo = periodSet.getMbo(0);
			finPeriod = periodMbo.getString("FINANCIALPERIOD");
			
		} 
		else
			throw new MXApplicationException("FinPeriodNotOpen", "FinPeriodNotOpen" + transdate);
		return finPeriod;
	}

	
	public long getGLCombinationID(String glAccount, MboRemote transactionmbo)
			throws Exception {

		
		MboSetRemote coaSet = transactionmbo.getMboSet("CHARTOFACCOUNTS");
		MboRemote glAccountMbo;
		coaSet.setWhere("where glaccount = '" + glAccount + "' and active = 1");
		coaSet.reset();

		if (coaSet != null && !coaSet.isEmpty()) {
			glAccountMbo = coaSet.getMbo(0);
			return glAccountMbo.getLong("EXTERNALREFID");
		}

		else
			throw new MXApplicationException("GLCodeCombNotFound", glAccount);

	}
	
	//From OFUtil
	public String getAccountID(String glAccount, MboRemote transactionmbo)
			throws Exception {

		
		MboSetRemote coaSet = transactionmbo.getMboSet("CHARTOFACCOUNTS");
		MboRemote glAccountMbo;
		coaSet.setWhere("where glaccount = '" + glAccount + "' and active = 1");
		coaSet.reset();

		if (coaSet != null && !coaSet.isEmpty()) {
			glAccountMbo = coaSet.getMbo(0);
			return glAccountMbo.getString("EXTERNALREFID");
		}

		else
			throw new MXApplicationException("GLCodeCombNotFound", glAccount);

	}
	
	static public String getProjectNumber(MboRemote transactionmbo,
			String sRefWO, String sSiteID) throws Exception {
		MboRemote ixWORecord = transactionmbo.getMboSet("ixWORecord$","WORKORDER","wonum = '"+sRefWO+"' and siteid = '"+sSiteID+"'").getMbo(0);
		if(ixWORecord!=null)
		{
			String sWO5 = null;
			String sParentWO = null;
			sWO5 = ixWORecord.getString("WO5");
			sParentWO = ixWORecord.getString("PARENT");

			do {
				if (ixWORecord.getString("WO5") != null) {
					sWO5 = ixWORecord.getString("WO5");
					return sWO5;
				} else {
					MboRemote ixWORecord1 = transactionmbo.getMboSet("ixWORecord1$","WORKORDER","wonum = '"+sParentWO+"' and siteid = '"+sSiteID+"'").getMbo(0);
					if(ixWORecord1!=null)
					{
						sWO5 = ixWORecord.getString("WO5");
						sParentWO = ixWORecord.getString("PARENT");

						if (sWO5 != null) {
							return sWO5;
						}
					}
				}
			} while (sParentWO != null);
			return sWO5;
		}
		else
			return null;

	}
	
	static public String getTaskNumber(MboRemote transactionmbo,
			String sRefWO, String sSiteID) throws Exception {
		MboRemote ixWORecord = transactionmbo.getMboSet("ixWORecord$","WORKORDER","wonum = '"+sRefWO+"' and siteid = '"+sSiteID+"'").getMbo(0);
		if(ixWORecord!=null)
		{
			String sWO5 = null;
			String sParentWO = null;
			sWO5 = ixWORecord.getString("WO7");
			sParentWO = ixWORecord.getString("PARENT");

			do {
				if (ixWORecord.getString("WO7") != null) {
					sWO5 = ixWORecord.getString("WO7");
					return sWO5;
				} else {
					MboRemote ixWORecord1 = transactionmbo.getMboSet("ixWORecord1$","WORKORDER","wonum = '"+sParentWO+"' and siteid = '"+sSiteID+"'").getMbo(0);
					if(ixWORecord1!=null)
					{
						sWO5 = ixWORecord.getString("WO7");
						sParentWO = ixWORecord.getString("PARENT");

						if (sWO5 != null) {
							return sWO5;
						}
					}
				}
			} while (sParentWO != null);
			return sWO5;
		}
		else
			return null;

	}

	public long getGLGroupID() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String formattedDate = sdf.format(date);
		return Integer.valueOf(formattedDate);
	}

	public long getApiSequenceNextval() throws Exception {
		
		long val = Long.valueOf(MXServer.getUID().toString());
		return val;
	}

}

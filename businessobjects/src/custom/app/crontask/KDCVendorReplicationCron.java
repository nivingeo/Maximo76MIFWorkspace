//package custom.app.iface.cron;
package custom.app.crontask;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Date;
import psdi.app.system.CrontaskParamInfo;
import psdi.iface.app.endpoint.MaxEndPoint;
import psdi.iface.app.endpoint.MaxEndPointDtlSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.Resolver;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class KDCVendorReplicationCron extends SimpleCronTask {
	MXLogger logger = null;
	StringBuffer failLoopTagMessage = null;
	StringBuffer failureMessage = null;
	String[] emailList = null;
	String emailFrom = null;
	Connection gpConn = null;
	Connection mxConn = null;
	Connection dynamicsConn = null;
	MXServer mxserver = null;// ///////
	UserInfo userInfo = null;
	Date lastRun = null;
	String personStatus = "";
	String laborStatus = "";
	String fromHRMS ="0";
	private boolean initialized;
	
	public KDCVendorReplicationCron() {
		super();
		// Initialised logger
		logger = MXLoggerFactory
				.getLogger("maximo.integration.PersonInterface");
		logger.debug(" initialised logger");

	}

	public void init() throws MXException
	{
		super.init();
	}

	public void cronAction() {
		Connection maximoConnection = null;
		psdi.security.ConnectionKey sKey = null;
		ResultSet rsPersonGroup = null;
		Statement stPersonGroup = null;
		String getPersonGrpQuery = null;
		
		PreparedStatement pst_insertIfaceTBQuery = null;
		PreparedStatement pst_insertQueueTBQuery = null;
		try {
			logger.debug("Inside init");
			mxserver = MXServer.getMXServer();
			sKey = mxserver.getSystemUserInfo().getConnectionKey();
			maximoConnection = mxserver.getDBManager().getConnection(sKey);
			logger.debug("Maximo DB Connection established");

			Properties properties = mxserver.getConfig();
			emailList = parseEmailTo("nivin@kdckwt.com");
			emailFrom = properties.getProperty("mxe.adminEmail", null);
			logger.debug("*************emailFrom:::" + emailFrom);
			userInfo = getRunasUserInfo();
			lastRun = getLastRunDate();
			logger.debug("*************Cron Last Run:::" + lastRun);
			logger.debug("*************userInfo:::" + userInfo.getDisplayName());

			mxConn = getMaximoConnection();
			gpConn = getGPConnection();
			dynamicsConn = getDYNAMICSConnection();
			Statement statement;
	        Statement statement1;

	        statement = null;
	        statement1 = null;
	        
			if (lastRun != null) {
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				String actdate = df.format(lastRun);
				Date parseDate = null;
				try {
					parseDate = df.parse(actdate);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				getPersonGrpQuery = "SELECT VENDORID,"+ 
				"       VENDNAME,"+ 
				"       VNDCHKNM,"+ 
				"       VENDSHNM,"+ 
				"       VADDCDPR,"+ 
				"       VADCDPAD,"+ 
				"       VADCDSFR,"+ 
				"       VADCDTRO,"+ 
				"       VNDCLSID,"+ 
				"       VNDCNTCT,"+ 
				"       ADDRESS1,"+ 
				"       ADDRESS2,"+ 
				"       ADDRESS3,"+ 
				"       CITY,"+ 
				"       STATE,"+ 
				"       ZIPCODE,"+ 
				"       COUNTRY,"+ 
				"       PHNUMBR1,"+ 
				"       PHNUMBR2,"+ 
				"       PHONE3,"+ 
				"       FAXNUMBR,"+ 
				"       UPSZONE,"+ 
				"       SHIPMTHD,"+ 
				"       TAXSCHID,"+ 
				"       ACNMVNDR,"+ 
				"       TXIDNMBR,"+ 
				"       VENDSTTS,"+ 
				"       CURNCYID,"+ 
				"       TXRGNNUM,"+ 
				"       PARVENID,"+ 
				"       TRDDISCT,"+ 
				"       TEN99TYPE,"+ 
				"       TEN99BOXNUMBER,"+ 
				"       MINORDER,"+ 
				"       PYMTRMID,"+ 
				"       MINPYTYP,"+ 
				"       MINPYPCT,"+ 
				"       MINPYDLR,"+ 
				"       MXIAFVND,"+ 
				"       MAXINDLR,"+ 
				"       COMMENT1,"+ 
				"       COMMENT2,"+ 
				"       USERDEF1,"+ 
				"       USERDEF2,"+ 
				"       CRLMTDLR,"+ 
				"       PYMNTPRI,"+ 
				"       KPCALHST,"+ 
				"       KGLDSTHS,"+ 
				"       KPERHIST,"+ 
				"       KPTRXHST,"+ 
				"       HOLD,"+ 
				"       PTCSHACF,"+ 
				"       CREDTLMT,"+ 
				"       WRITEOFF,"+ 
				"       MXWOFAMT,"+ 
				"       SBPPSDED,"+ 
				"       PPSTAXRT,"+ 
				"       DXVARNUM,"+ 
				"       RTOBUTKN,"+ 
				"       XPDTOBLG,"+ 
				"       PRSPAYEE,"+ 
				"       PMAPINDX,"+ 
				"       PMCSHIDX,"+ 
				"       PMDAVIDX,"+ 
				"       PMDTKIDX,"+ 
				"       PMFINIDX,"+ 
				"       PMMSCHIX,"+ 
				"       PMFRTIDX,"+ 
				"       PMTAXIDX,"+ 
				"       PMWRTIDX,"+ 
				"       PMPRCHIX,"+ 
				"       PMRTNGIX,"+ 
				"       PMTDSCIX,"+ 
				"       ACPURIDX,"+ 
				"       PURPVIDX,"+ 
				"       NOTEINDX,"+ 
				"       CHEKBKID,"+ 
				"       MODIFDT,"+ 
				"       CREATDDT,"+ 
				"       RATETPID,"+ 
				"       Revalue_Vendor,"+ 
				"       Post_Results_To,"+ 
				"       FREEONBOARD,"+ 
				"       GOVCRPID,"+ 
				"       GOVINDID,"+ 
				"       DISGRPER,"+ 
				"       DUEGRPER,"+ 
				"       DOCFMTID,"+ 
				"       TaxInvRecvd,"+ 
				"       USERLANG,"+ 
				"       WithholdingType,"+ 
				"       WithholdingFormType,"+ 
				"       WithholdingEntityType,"+ 
				"       TaxFileNumMode,"+ 
				"       LaborPmtType,"+ 
				"       CCode,"+ 
				"       DECLID,"+ 
				"       CBVAT,"+ 
				"       EmailPOs,"+ 
				"       POEmailRecipient,"+ 
				"       EmailPOFormat,"+ 
				"       FaxPOs,"+ 
				"       POFaxNumber,"+ 
				"       FaxPOFormat,"+ 
				"       Workflow_Approval_Status,"+ 
				"       Workflow_Priority,"+ 
				"       VADCD1099,"+ 
				"       Workflow_Status,"+ 
				"       DEX_ROW_TS"+ 
				"  FROM MAXPROD.dbo.KDCVendorReplicationView "+
				" where dbo.getDateOnly(DEX_ROW_TS) > dbo.getDateOnly('"+actdate + "')";
			} else
				getPersonGrpQuery =  "SELECT VENDORID,"+ 
				"       VENDNAME,"+ 
				"       VNDCHKNM,"+ 
				"       VENDSHNM,"+ 
				"       VADDCDPR,"+ 
				"       VADCDPAD,"+ 
				"       VADCDSFR,"+ 
				"       VADCDTRO,"+ 
				"       VNDCLSID,"+ 
				"       VNDCNTCT,"+ 
				"       ADDRESS1,"+ 
				"       ADDRESS2,"+ 
				"       ADDRESS3,"+ 
				"       CITY,"+ 
				"       STATE,"+ 
				"       ZIPCODE,"+ 
				"       COUNTRY,"+ 
				"       PHNUMBR1,"+ 
				"       PHNUMBR2,"+ 
				"       PHONE3,"+ 
				"       FAXNUMBR,"+ 
				"       UPSZONE,"+ 
				"       SHIPMTHD,"+ 
				"       TAXSCHID,"+ 
				"       ACNMVNDR,"+ 
				"       TXIDNMBR,"+ 
				"       VENDSTTS,"+ 
				"       CURNCYID,"+ 
				"       TXRGNNUM,"+ 
				"       PARVENID,"+ 
				"       TRDDISCT,"+ 
				"       TEN99TYPE,"+ 
				"       TEN99BOXNUMBER,"+ 
				"       MINORDER,"+ 
				"       PYMTRMID,"+ 
				"       MINPYTYP,"+ 
				"       MINPYPCT,"+ 
				"       MINPYDLR,"+ 
				"       MXIAFVND,"+ 
				"       MAXINDLR,"+ 
				"       COMMENT1,"+ 
				"       COMMENT2,"+ 
				"       USERDEF1,"+ 
				"       USERDEF2,"+ 
				"       CRLMTDLR,"+ 
				"       PYMNTPRI,"+ 
				"       KPCALHST,"+ 
				"       KGLDSTHS,"+ 
				"       KPERHIST,"+ 
				"       KPTRXHST,"+ 
				"       HOLD,"+ 
				"       PTCSHACF,"+ 
				"       CREDTLMT,"+ 
				"       WRITEOFF,"+ 
				"       MXWOFAMT,"+ 
				"       SBPPSDED,"+ 
				"       PPSTAXRT,"+ 
				"       DXVARNUM,"+ 
				"       RTOBUTKN,"+ 
				"       XPDTOBLG,"+ 
				"       PRSPAYEE,"+ 
				"       PMAPINDX,"+ 
				"       PMCSHIDX,"+ 
				"       PMDAVIDX,"+ 
				"       PMDTKIDX,"+ 
				"       PMFINIDX,"+ 
				"       PMMSCHIX,"+ 
				"       PMFRTIDX,"+ 
				"       PMTAXIDX,"+ 
				"       PMWRTIDX,"+ 
				"       PMPRCHIX,"+ 
				"       PMRTNGIX,"+ 
				"       PMTDSCIX,"+ 
				"       ACPURIDX,"+ 
				"       PURPVIDX,"+ 
				"       NOTEINDX,"+ 
				"       CHEKBKID,"+ 
				"       MODIFDT,"+ 
				"       CREATDDT,"+ 
				"       RATETPID,"+ 
				"       Revalue_Vendor,"+ 
				"       Post_Results_To,"+ 
				"       FREEONBOARD,"+ 
				"       GOVCRPID,"+ 
				"       GOVINDID,"+ 
				"       DISGRPER,"+ 
				"       DUEGRPER,"+ 
				"       DOCFMTID,"+ 
				"       TaxInvRecvd,"+ 
				"       USERLANG,"+ 
				"       WithholdingType,"+ 
				"       WithholdingFormType,"+ 
				"       WithholdingEntityType,"+ 
				"       TaxFileNumMode,"+ 
				"       LaborPmtType,"+ 
				"       CCode,"+ 
				"       DECLID,"+ 
				"       CBVAT,"+ 
				"       EmailPOs,"+ 
				"       POEmailRecipient,"+ 
				"       EmailPOFormat,"+ 
				"       FaxPOs,"+ 
				"       POFaxNumber,"+ 
				"       FaxPOFormat,"+ 
				"       Workflow_Approval_Status,"+ 
				"       Workflow_Priority,"+ 
				"       VADCD1099,"+ 
				"       Workflow_Status,"+ 
				"       DEX_ROW_TS"+ 
				"  FROM MAXPROD.dbo.KDCVendorReplicationView "+
				" where dbo.getDateOnly(DEX_ROW_TS) >= '07/01/2017' ";
			logger.debug("Select Query:::" + getPersonGrpQuery);

			stPersonGroup = mxConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			rsPersonGroup = stPersonGroup.executeQuery(getPersonGrpQuery);
			logger.debug("************** Execute Query rsPersonGroup ************");
			String qry_insertIfaceTBQuery = "INSERT INTO [192.168.0.2].KDC.dbo.PM00200"+ 
			"      ("+ 
			"          VENDORID,"+ //1
			"          VENDNAME,"+ //2
			"          VNDCHKNM,"+ //3
			"          VENDSHNM,"+ //4
			"          VADDCDPR,"+ //5
			"          VADCDPAD,"+ //6
			"          VADCDSFR,"+ //7
			"          VADCDTRO,"+ //8
			"          VNDCLSID,"+ //9
			"          VNDCNTCT,"+ //10
			"          ADDRESS1,"+ //11
			"          ADDRESS2,"+ //12
			"          ADDRESS3,"+ //13
			"          CITY,"+ //14
			"          STATE,"+ //15
			"          ZIPCODE,"+ //16
			"          COUNTRY,"+ //17
			"          PHNUMBR1,"+ //18
			"          PHNUMBR2,"+ //19
			"          PHONE3,"+ //20
			"          FAXNUMBR,"+ //21
			"          UPSZONE,"+ //22
			"          SHIPMTHD,"+ //23
			"          TAXSCHID,"+ //24
			"          ACNMVNDR,"+ //25
			"          TXIDNMBR,"+ //26
			"          VENDSTTS,"+ //27
			"          CURNCYID,"+ //28
			"          TXRGNNUM,"+ //29
			"          PARVENID,"+ //30
			"          TRDDISCT,"+ //31
			"          TEN99TYPE,"+ //32
			"          TEN99BOXNUMBER,"+//33 
			"          MINORDER,"+ //34
			"          PYMTRMID,"+ //35
			"          MINPYTYP,"+ //36
			"          MINPYPCT,"+ //37
			"          MINPYDLR,"+ //38
			"          MXIAFVND,"+ //39
			"          MAXINDLR,"+ //40
			"          COMMENT1,"+ //41
			"          COMMENT2,"+ //42
			"          USERDEF1,"+ //43
			"          USERDEF2,"+ //44
			"          CRLMTDLR,"+ //45
			"          PYMNTPRI,"+ //46
			"          KPCALHST,"+ //47
			"          KGLDSTHS,"+ //48
			"          KPERHIST,"+ //49
			"          KPTRXHST,"+ //50
			"          HOLD,"+ //51
			"          PTCSHACF,"+ //52
			"          CREDTLMT,"+ //53
			"          WRITEOFF,"+ //54
			"          MXWOFAMT,"+ //55
			"          SBPPSDED,"+ //56
			"          PPSTAXRT,"+ //57
			"          DXVARNUM,"+ //58
			"          CRTCOMDT,"+ //59
			"          CRTEXPDT,"+ //60
			"          RTOBUTKN,"+ //61
			"          XPDTOBLG,"+ //62
			"          PRSPAYEE,"+ //63
			"          PMAPINDX,"+ //64
			"          PMCSHIDX,"+ //65
			"          PMDAVIDX,"+ //66
			"          PMDTKIDX,"+ //67
			"          PMFINIDX,"+ //68
			"          PMMSCHIX,"+ //69
			"          PMFRTIDX,"+ //70
			"          PMTAXIDX,"+ //71
			"          PMWRTIDX,"+ //72
			"          PMPRCHIX,"+ //73
			"          PMRTNGIX,"+ //74
			"          PMTDSCIX,"+ //75
			"          ACPURIDX,"+ //76
			"          PURPVIDX,"+ //77
			"          NOTEINDX,"+ //78
			"          CHEKBKID,"+ //79
			"          MODIFDT,"+ //80
			"          CREATDDT,"+//81 
			"          RATETPID,"+ //82
			"          Revalue_Vendor,"+ //83
			"          Post_Results_To,"+ //84
			"          FREEONBOARD,"+ //85
			"          GOVCRPID,"+ //86
			"          GOVINDID,"+ //87
			"          DISGRPER,"+ //88
			"          DUEGRPER,"+ //89
			"          DOCFMTID,"+ //90
			"          TaxInvRecvd,"+ //91
			"          USERLANG,"+ //92
			"          WithholdingType,"+ //93 
			"          WithholdingFormType,"+ //94
			"          WithholdingEntityType,"+ //95
			"          TaxFileNumMode,"+ //96
			"          BRTHDATE,"+ //97
			"          LaborPmtType,"+ //98
			"          CCode,"+ //99
			"          DECLID,"+ //100
			"          CBVAT,"+ //101
			"          Workflow_Approval_Status,"+ //102
			"          Workflow_Priority,"+ //103
			"          Workflow_Status,"+ //104
			"          VADCD1099,"+ //105
			"          DEX_ROW_TS"+ //106
			"      )";
			qry_insertIfaceTBQuery = qry_insertIfaceTBQuery
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			while (rsPersonGroup.next()) { // While loop
				try {
					pst_insertIfaceTBQuery = maximoConnection.prepareStatement(qry_insertIfaceTBQuery);
					pst_insertIfaceTBQuery.setString(1, rsPersonGroup.getString("VENDORID"));
					pst_insertIfaceTBQuery.setString(2, rsPersonGroup.getString("VENDNAME"));
					pst_insertIfaceTBQuery.setString(3, rsPersonGroup.getString("VNDCHKNM"));
					pst_insertIfaceTBQuery.setString(4, rsPersonGroup.getString("VENDSHNM"));
					pst_insertIfaceTBQuery.setString(5, rsPersonGroup.getString("VADDCDPR"));
					pst_insertIfaceTBQuery.setString(6, rsPersonGroup.getString("VADCDPAD"));
					pst_insertIfaceTBQuery.setString(7, rsPersonGroup.getString("VADCDSFR"));
					pst_insertIfaceTBQuery.setString(8, rsPersonGroup.getString("VADCDTRO"));
					pst_insertIfaceTBQuery.setString(9, rsPersonGroup.getString("VNDCLSID"));
					pst_insertIfaceTBQuery.setString(10, rsPersonGroup.getString("VNDCNTCT"));
					pst_insertIfaceTBQuery.setString(11, rsPersonGroup.getString("ADDRESS1"));
					pst_insertIfaceTBQuery.setString(12, rsPersonGroup.getString("ADDRESS2"));
					pst_insertIfaceTBQuery.setString(13, rsPersonGroup.getString("ADDRESS3"));
					pst_insertIfaceTBQuery.setString(14, rsPersonGroup.getString("CITY"));
					pst_insertIfaceTBQuery.setString(15, rsPersonGroup.getString("STATE"));
					pst_insertIfaceTBQuery.setString(16, rsPersonGroup.getString("ZIPCODE"));
					pst_insertIfaceTBQuery.setString(17, rsPersonGroup.getString("COUNTRY"));
					pst_insertIfaceTBQuery.setString(18, rsPersonGroup.getString("PHNUMBR1"));
					pst_insertIfaceTBQuery.setString(19, rsPersonGroup.getString("PHNUMBR2"));
					pst_insertIfaceTBQuery.setString(20, rsPersonGroup.getString("PHONE3"));
					pst_insertIfaceTBQuery.setString(21, rsPersonGroup.getString("FAXNUMBR"));
					pst_insertIfaceTBQuery.setString(22, rsPersonGroup.getString("UPSZONE"));
					pst_insertIfaceTBQuery.setString(23, rsPersonGroup.getString("SHIPMTHD"));
					pst_insertIfaceTBQuery.setString(24, rsPersonGroup.getString("TAXSCHID"));
					pst_insertIfaceTBQuery.setString(25, rsPersonGroup.getString("ACNMVNDR"));
					pst_insertIfaceTBQuery.setString(26, rsPersonGroup.getString("TXIDNMBR"));
					pst_insertIfaceTBQuery.setString(27, rsPersonGroup.getString("VENDSTTS"));
					pst_insertIfaceTBQuery.setString(28, rsPersonGroup.getString("CURNCYID"));
					pst_insertIfaceTBQuery.setString(29, rsPersonGroup.getString("TXRGNNUM"));
					pst_insertIfaceTBQuery.setString(30, rsPersonGroup.getString("PARVENID"));
					pst_insertIfaceTBQuery.setString(31, rsPersonGroup.getString("TRDDISCT"));
					pst_insertIfaceTBQuery.setString(32, rsPersonGroup.getString("TEN99TYPE"));
					pst_insertIfaceTBQuery.setString(33, rsPersonGroup.getString("TEN99BOXNUMBER"));
					pst_insertIfaceTBQuery.setString(34, rsPersonGroup.getString("MINORDER"));
					pst_insertIfaceTBQuery.setString(35, rsPersonGroup.getString("PYMTRMID"));
					pst_insertIfaceTBQuery.setString(36, rsPersonGroup.getString("MINPYTYP"));
					pst_insertIfaceTBQuery.setString(37, rsPersonGroup.getString("MINPYPCT"));
					pst_insertIfaceTBQuery.setString(38, rsPersonGroup.getString("MINPYDLR"));
					pst_insertIfaceTBQuery.setString(39, rsPersonGroup.getString("MXIAFVND"));
					pst_insertIfaceTBQuery.setString(40, rsPersonGroup.getString("MAXINDLR"));
					pst_insertIfaceTBQuery.setString(41, rsPersonGroup.getString("COMMENT1"));
					pst_insertIfaceTBQuery.setString(42, rsPersonGroup.getString("COMMENT2"));
					pst_insertIfaceTBQuery.setString(43, rsPersonGroup.getString("USERDEF1"));
					pst_insertIfaceTBQuery.setString(44, rsPersonGroup.getString("USERDEF2"));
					pst_insertIfaceTBQuery.setString(45, rsPersonGroup.getString("CRLMTDLR"));
					pst_insertIfaceTBQuery.setString(46, rsPersonGroup.getString("PYMNTPRI"));
					pst_insertIfaceTBQuery.setString(47, rsPersonGroup.getString("KPCALHST"));
					pst_insertIfaceTBQuery.setString(48, rsPersonGroup.getString("KGLDSTHS"));
					pst_insertIfaceTBQuery.setString(49, rsPersonGroup.getString("KPERHIST"));
					pst_insertIfaceTBQuery.setString(50, rsPersonGroup.getString("KPTRXHST"));
					pst_insertIfaceTBQuery.setString(51, rsPersonGroup.getString("HOLD"));
					pst_insertIfaceTBQuery.setString(52, rsPersonGroup.getString("PTCSHACF"));
					pst_insertIfaceTBQuery.setString(53, rsPersonGroup.getString("CREDTLMT"));
					pst_insertIfaceTBQuery.setString(54, rsPersonGroup.getString("WRITEOFF"));
					pst_insertIfaceTBQuery.setString(55, rsPersonGroup.getString("MXWOFAMT"));
					pst_insertIfaceTBQuery.setString(56, rsPersonGroup.getString("SBPPSDED"));
					pst_insertIfaceTBQuery.setString(57, rsPersonGroup.getString("PPSTAXRT"));
					pst_insertIfaceTBQuery.setString(58, rsPersonGroup.getString("DXVARNUM"));
					pst_insertIfaceTBQuery.setString(59, rsPersonGroup.getString("CRTCOMDT"));
					pst_insertIfaceTBQuery.setString(60, rsPersonGroup.getString("CRTEXPDT"));
					pst_insertIfaceTBQuery.setString(61, rsPersonGroup.getString("RTOBUTKN"));
					pst_insertIfaceTBQuery.setString(62, rsPersonGroup.getString("XPDTOBLG"));
					pst_insertIfaceTBQuery.setString(63, rsPersonGroup.getString("PRSPAYEE"));
					pst_insertIfaceTBQuery.setString(64, rsPersonGroup.getString("PMAPINDX"));
					pst_insertIfaceTBQuery.setString(65, rsPersonGroup.getString("PMCSHIDX"));
					pst_insertIfaceTBQuery.setString(66, rsPersonGroup.getString("PMDAVIDX"));
					pst_insertIfaceTBQuery.setString(67, rsPersonGroup.getString("PMDTKIDX"));
					pst_insertIfaceTBQuery.setString(68, rsPersonGroup.getString("PMFINIDX"));
					pst_insertIfaceTBQuery.setString(69, rsPersonGroup.getString("PMMSCHIX"));
					pst_insertIfaceTBQuery.setString(70, rsPersonGroup.getString("PMFRTIDX"));
					pst_insertIfaceTBQuery.setString(71, rsPersonGroup.getString("PMTAXIDX"));
					pst_insertIfaceTBQuery.setString(72, rsPersonGroup.getString("PMWRTIDX"));
					pst_insertIfaceTBQuery.setString(73, rsPersonGroup.getString("PMPRCHIX"));
					pst_insertIfaceTBQuery.setString(74, rsPersonGroup.getString("PMRTNGIX"));
					pst_insertIfaceTBQuery.setString(75, rsPersonGroup.getString("PMTDSCIX"));
					pst_insertIfaceTBQuery.setString(76, rsPersonGroup.getString("ACPURIDX"));
					pst_insertIfaceTBQuery.setString(77, rsPersonGroup.getString("PURPVIDX"));
					pst_insertIfaceTBQuery.setString(78, rsPersonGroup.getString("NOTEINDX"));
					pst_insertIfaceTBQuery.setString(79, rsPersonGroup.getString("CHEKBKID"));
					pst_insertIfaceTBQuery.setString(80, rsPersonGroup.getString("MODIFDT"));
					pst_insertIfaceTBQuery.setString(81, rsPersonGroup.getString("CREATDDT"));
					pst_insertIfaceTBQuery.setString(82, rsPersonGroup.getString("RATETPID"));
					pst_insertIfaceTBQuery.setString(83, rsPersonGroup.getString("Revalue_Vendor"));
					pst_insertIfaceTBQuery.setString(84, rsPersonGroup.getString("Post_Results_To"));
					pst_insertIfaceTBQuery.setString(85, rsPersonGroup.getString("FREEONBOARD"));
					pst_insertIfaceTBQuery.setString(86, rsPersonGroup.getString("GOVCRPID"));
					pst_insertIfaceTBQuery.setString(87, rsPersonGroup.getString("GOVINDID"));
					pst_insertIfaceTBQuery.setString(88, rsPersonGroup.getString("DISGRPER"));
					pst_insertIfaceTBQuery.setString(89, rsPersonGroup.getString("DUEGRPER"));
					pst_insertIfaceTBQuery.setString(90, rsPersonGroup.getString("DOCFMTID"));
					pst_insertIfaceTBQuery.setString(91, rsPersonGroup.getString("TaxInvRecvd"));
					pst_insertIfaceTBQuery.setString(92, rsPersonGroup.getString("USERLANG"));
					pst_insertIfaceTBQuery.setString(93, rsPersonGroup.getString("WithholdingType"));
					pst_insertIfaceTBQuery.setString(94, rsPersonGroup.getString("WithholdingFormType"));
					pst_insertIfaceTBQuery.setString(95, rsPersonGroup.getString("WithholdingEntityType"));
					pst_insertIfaceTBQuery.setString(96, rsPersonGroup.getString("TaxFileNumMode"));
					pst_insertIfaceTBQuery.setString(97, rsPersonGroup.getString("BRTHDATE"));
					pst_insertIfaceTBQuery.setString(98, rsPersonGroup.getString("LaborPmtType"));
					pst_insertIfaceTBQuery.setString(99, rsPersonGroup.getString("CCode"));
					pst_insertIfaceTBQuery.setString(100, rsPersonGroup.getString("DECLID"));
					pst_insertIfaceTBQuery.setString(101, rsPersonGroup.getString("CBVAT"));
					pst_insertIfaceTBQuery.setString(102, rsPersonGroup.getString("Workflow_Approval_Status"));
					pst_insertIfaceTBQuery.setString(103, rsPersonGroup.getString("Workflow_Priority"));
					pst_insertIfaceTBQuery.setString(104, rsPersonGroup.getString("Workflow_Status"));
					pst_insertIfaceTBQuery.setString(105, rsPersonGroup.getString("VADCD1099"));
					pst_insertIfaceTBQuery.setString(106, rsPersonGroup.getString("DEX_ROW_TS"));
					pst_insertIfaceTBQuery.executeUpdate();
					
					
				}catch (SQLException sqlException) {
					sqlException.printStackTrace();
					String logLine = "SQL Exception: "
							+ sqlException.toString()
							+ " for PersonGroup Interface";
					logger.error(logLine);
					failureMessage.append("Cron task failed due to the below error(s).");
					failureMessage.append("\n");
					failureMessage.append("logLine");

				} catch (Exception Exception) {
					Exception.printStackTrace();
					String logLine = "Exception: " + Exception.toString()
							+ " for Person Interface";
					logger.error(logLine);
					failureMessage
							.append("Cron task failed due to the below error(s).");
					failureMessage.append("\n");
					failureMessage.append("logLine");

				}
				if (pst_insertIfaceTBQuery != null) {
					pst_insertIfaceTBQuery.close();
				}
				if (pst_insertQueueTBQuery != null) {
					pst_insertQueueTBQuery.close();
				}
			}

		}catch (SQLException sqlException) {
			sqlException.printStackTrace();
			String logLine = "SQL Exception: ";
			logger.error(logLine);
			failureMessage
					.append("Cron task failed due to the below error(s).");
			failureMessage.append("\n");
			failureMessage.append("logLine");
			sendEmail(failureMessage, emailList, emailFrom,
					"Error reported by Person Interface", "", logger);
			failureMessage = null;
		}

		catch (Exception exception) {
			exception.printStackTrace();

			String logLine = "Exception: " ;
			logger.error(logLine);
			// Insert into log table
			failureMessage
					.append("Cron task failed due to the below error(s).");
			failureMessage.append("\n");
			failureMessage.append("logLine");

			sendEmail(failureMessage, emailList, emailFrom,
					"Error reported by Person Interface", "", logger);
			failureMessage = null;
		} finally {

			try {
				if (rsPersonGroup != null) {
					rsPersonGroup.close();
				}
				if (stPersonGroup != null) {
					stPersonGroup.close();
				}

				if (mxConn != null) {
					mxConn.close();
				}
				if (maximoConnection != null) {
					mxserver.getDBManager().freeConnection(sKey);
				}
				
				

			}// end of try
			catch (SQLException sqlException) {
				sqlException.printStackTrace();
				String logLine = "SQL Exception: " + sqlException.toString()
						+ " for Person code " + "";
				logger.debug("Inside finally block");
				logger.error(logLine);
			}

		}// end of finally
	}// End of cronAction Method
	
	public static Connection getMaximoConnection()
    {
    	//String url = "jdbc:inetdae7a:maxsrv:1433?database=MAXDEV&language=us_english&nowarnings=true";
    	String url = "jdbc:inetdae7a:192.168.0.5:1433?database=MAXPROD&language=us_english&nowarnings=true";
    	String username = "MAXIMO";
    	String password = "MAXIMO";
    	Connection con = null;
    	try
    	{
    		Class.forName("com.inet.tds.TdsDriver");
    		con = DriverManager.getConnection(url, username, password);
    	}
    	catch(Exception e)
    	{
    		System.out.println("Error while creating connection in getMaximoConnection and the error is "+e);
    	}
    	return con;
    }
	
	public static Connection getGPConnection()
    {
    	//String url = "jdbc:inetdae7a:maxsrv:1433?database=MAXDEV&language=us_english&nowarnings=true";
    	String url = "jdbc:inetdae7a:192.168.0.2:1433?database=KDC&language=us_english&nowarnings=true";
    	String username = "IXGP";
    	String password = "IXGP";
    	Connection con = null;
    	try
    	{
    		Class.forName("com.inet.tds.TdsDriver");
    		con = DriverManager.getConnection(url, username, password);
    	}
    	catch(Exception e)
    	{
    		System.out.println("Error while creating connection in getGPConnection and the error is "+e);
    	}
    	return con;
    }
	
	public static Connection getDYNAMICSConnection()
    {
    	//String url = "jdbc:inetdae7a:maxsrv:1433?database=MAXDEV&language=us_english&nowarnings=true";
    	String url = "jdbc:inetdae7a:192.168.0.2:1433?database=DYNAMICS&language=us_english&nowarnings=true";
    	String username = "IXGP";
    	String password = "IXGP";
    	Connection con = null;
    	try
    	{
    		Class.forName("com.inet.tds.TdsDriver");
    		con = DriverManager.getConnection(url, username, password);
    	}
    	catch(Exception e)
    	{
    		System.out.println("Error while creating connection in getDYNAMICSConnection and the error is "+e);
    	}
    	return con;
    }

	/*
	 * This method is used to get emailid list parameter (non-Javadoc)
	 * 
	 * @see psdi.server.SimpleCronTask#getParameters()
	 */
	public CrontaskParamInfo[] getParameters() throws MXException,
			RemoteException {
		try {
			logger.debug("***Start:Into crontaskparaminfo method***");
			CrontaskParamInfo PersonGroupIntParamInfo[];
			String as[] = { "emailTo" };
			String as1[] = { " " }; // remove it
			String as2[][] = { { "PersonInterface", "EmailTo" } };
			PersonGroupIntParamInfo = new CrontaskParamInfo[as.length];
			for (int i = 0; i < as.length; i++) {
				PersonGroupIntParamInfo[i] = new CrontaskParamInfo();
				PersonGroupIntParamInfo[i].setName(as[i]);
				PersonGroupIntParamInfo[i].setDefault(as1[i]);
				PersonGroupIntParamInfo[i].setDescription(as2[i][0], as2[i][1]);
			}

			return PersonGroupIntParamInfo;
		} catch (Exception exception) {
			if (logger.isErrorEnabled()) {
				logger.error(exception);
			}
			return null;
		}
	}

	/**
	 * This method is used to read emaillist and email from properties
	 * 
	 * @throws RemoteException
	 * @throws MXException
	 */
	public void readConfig() throws RemoteException, MXException {
		logger.debug("***Start:Into readconfig method of PersonGroupInt***");
		MXServer mxserver = MXServer.getMXServer();
		Properties properties = mxserver.getConfig();

		emailList = parseEmailTo(getParamAsString("emailTo"));
		emailFrom = properties.getProperty("mxe.adminEmail", null);
		if (logger.isDebugEnabled()) {
			logger.debug("***Start:getParamAsString(\"emailTo\")***"
					+ emailList);
		}

	}

	/*
	 * This method is to start thr config (non-Javadoc)
	 * 
	 * @see psdi.server.SimpleCronTask#start()
	 */
	public void start() {
		try {
			logger.debug("***Start:Into start method. Before read config method");
			readConfig();
		} catch (Exception exception) {
			if (logger.isDebugEnabled()) {
				logger.debug(exception);
			}
			if (logger.isErrorEnabled()) {
				logger.error(exception);
			}
		} catch (Throwable throwable) {
			if (logger.isDebugEnabled()) {
				logger.debug(throwable);
			}
			if (logger.isErrorEnabled()) {
				logger.error(throwable);
			}
		}
	}

	/**
	 * parse the email to address
	 * 
	 * @param s
	 * @return
	 * @throws MXException
	 */
	private String[] parseEmailTo(String s) throws MXException {
		logger.debug("***Start:Into parseEmailTo method***" + s);
		StringTokenizer stringtokenizer = new StringTokenizer(s, ";");
		String as[] = new String[stringtokenizer.countTokens()];
		for (int i = 0; stringtokenizer.hasMoreTokens(); i++) {
			as[i] = stringtokenizer.nextToken();
		}
		return as;
	}

	public String getMaximoProperties(String propertyName) {
		String propertyValue = null; // within cron action
		MXServer mxserver; // within cron action

		try {
			mxserver = MXServer.getMXServer(); // create within cron action
			Properties properties = mxserver.getConfig();
			propertyValue = properties.getProperty(propertyName, null);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return propertyValue;

	}

	

	/**
	 * This method is used to send email if there is any problem in interface
	 * 
	 * 
	 * @param failureMessage
	 * @param emailList
	 * @param emailFrom
	 * @param ifaceName
	 * @param uniqueIdentifier
	 * @param logger
	 */
	public void sendEmail(StringBuffer failureMessage, String emailList[],
			String emailFrom, String ifaceName, String uniqueIdentifier,
			MXLogger logger) {
		try {
			if (emailList != null && emailFrom != null) {
				if (failureMessage != null && failureMessage.length() != 0) {
					failureMessage.append("\n");
					logger.debug("failureMessage" + failureMessage);
					MXServer.sendEMail(emailList, emailFrom,
							translateMsg(" PersonGroup Interface", logger),
							failureMessage.substring(0,
									failureMessage.length() - 1));
				}
			}
		} catch (Exception mailEx) {
			String logLine = "Unable to send the mail. Reason for failure is "
					+ mailEx.getMessage();
			logger.error(logLine);

			failureMessage = null;

		}
		return;
	}

	/**
	 * This method is used to translate message
	 * 
	 * @param subject
	 * @param logger
	 * @return
	 */
	private String translateMsg(String subject, MXLogger logger) {
		psdi.util.Message message = Resolver.getResolver().getMessage(
				"Person Interface ", subject);
		return message.getMessage();
	}
}

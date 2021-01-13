package ix.handlers.custom.koc.trans.util;
// Praxis - Rojitha - Util class for Journal Processing
import java.sql.ResultSet;

import psdi.mbo.DBShortcut;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;

import ix.handlers.custom.koc.trans.util.Document.DocumentType;
import ix.handlers.custom.koc.trans.util.Field.GLInterfaceFields;
import ix.handlers.custom.koc.trans.util.Field.PACommitmentFields;
import ix.handlers.custom.koc.trans.util.Field.PAInterfaceFields;
import ix.handlers.custom.koc.trans.util.JournalsHelper.GLSide;



public class AccountingString implements Mappable
{
	private static final long	UNSPECIFIED			= -1;
	private long				glCodeCombinationID	= AccountingString.UNSPECIFIED;
	
	protected GLSide			iAmA;			// which side am I in this transaction - debit or credit?
	protected boolean			isOPEX;

	protected String			glAccount;
	
	protected OFUtil			ofUtil;
	protected TransactionInfo	owner;
	
	protected String			projectType;
	
	protected String			projectNum;
	protected String			taskNum;
	protected String			expenditureType;
	protected String			costCenter;
	
	protected AccountingString()
	{
	} 

	public AccountingString(OFUtil ofUtil, String glAccount) throws MXApplicationException
	{
		if (glAccount == null)
		{
			throw new MXApplicationException("AccStrGLIsNull","AccStrGLIsNull");
		}
		this.glAccount = glAccount;
		
		this.ofUtil      = ofUtil;		
		this.isOPEX      = Boolean.TRUE;
		this.projectType = null;		
	}

	public AccountingString(OFUtil ofUtil, String projectNum, String taskNum, String costCenter, String expenditureType, String glAccount) throws  MXApplicationException
	{
		
	    
		if (glAccount == null ||glAccount.equals(""))
		{
			
			throw new MXApplicationException("AccStrGLIsNull","GLDebit/CreditNotProvided");
		}
		
		this.glAccount = glAccount;
		this.ofUtil    = ofUtil;
		
		if (projectNum == null ||projectNum.equals(""))
		{
			this.isOPEX = Boolean.TRUE;
			
		    return;
		}
		
		this.isOPEX = Boolean.FALSE;
		if (taskNum == null ||taskNum.equals(""))
		{
			throw new MXApplicationException("AccStrTaskIsNull","AccStrTaskIsNull");
		}
		if (expenditureType == null ||expenditureType.equals(""))
		{
			throw new MXApplicationException("AccStrExpendTypeIsNull","AccStrExpendTypeIsNull");
		}
		if (costCenter == null ||costCenter.equals(""))
		{
			throw new MXApplicationException("AccStrCostCenterIsNull","AccStrCostCenterIsNull");
		}
		
		this.projectNum 	 = projectNum;
		this.taskNum 		 = taskNum;
		this.expenditureType = expenditureType;
		this.costCenter 	 = costCenter;
	}

	protected void setOwner(TransactionInfo owner, GLSide side)
	{
		this.owner = owner;
		this.iAmA = side;
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof AccountingString)
		{
			AccountingString theOtherString = (AccountingString) obj;
			if (glAccount.equals(theOtherString.glAccount))
			{
				if (projectNum == null)
				{
					if (theOtherString.projectNum == null)
					{
						return Boolean.TRUE;
					}
					else
					{
						return Boolean.FALSE;
					}
				}
				else
				{
					if (taskNum.equals(theOtherString.taskNum) && costCenter.equals(theOtherString.costCenter) && expenditureType.equals(theOtherString.expenditureType))
					{
						return Boolean.TRUE;
					}
					else
					{
						return Boolean.FALSE;
					}
				}
			}
			else
			{
				return Boolean.FALSE;
			}
		}
		return super.equals(obj);
	}

	public long getGLCodeCombinationID(MboRemote transactionMbo) throws Exception
	{
		if (glCodeCombinationID == AccountingString.UNSPECIFIED)
		{
			glCodeCombinationID = ofUtil.getGLCombinationID(glAccount,transactionMbo);
		}
		return glCodeCombinationID;
	}

	public String getProjectType(MboRemote mbo) throws Exception
	{
		
		
		
		if (this.projectType == null && this.projectNum != null && !this.projectNum.equals(""))
		{
			
			
			projectType =  ofUtil.getProjectType(this.projectNum,mbo);
		}
		return projectType;
	}


	
	
	
	
	public void copyToPAInterfaceRecord(MboRemote interfaceRecord) throws MXApplicationException, Exception
	{
		

		interfaceRecord.setValue("BATCH_NAME", this.projectNum);

		interfaceRecord.setValue("PROJECT_NUMBER", this.projectNum);
		interfaceRecord.setValue("TASK_NUMBER", this.taskNum);

		 DBShortcut dbShortcut = new DBShortcut();
	      dbShortcut.connect(interfaceRecord.getUserInfo().getConnectionKey());
	      String query = "";
	      String pa_OrgID="";
	      query="SELECT T.CARRYING_OUT_ORGANIZATION_ID 		FROM " +
	      		"PA_TASKS T,PA_PROJECTS P 	WHERE P.PROJECT_ID = T.PROJECT_ID " +
	      "		AND T.TASK_NUMBER = '"+this.taskNum+"'	AND P.SEGMENT1 = '"+this.projectNum+"'";
	      
	      ResultSet resultSet = dbShortcut.executeQuery(query);

	      if (resultSet.next()) {
	    	  pa_OrgID = resultSet.getString(1);
	      }
	      dbShortcut.close();
		MboSetRemote personGpSet = interfaceRecord.getMboSet("PERSONGROUP");
		MboRemote personGpMbo;
		personGpSet.setWhere("where fromhrms = 1 and externalrefid = '" + pa_OrgID + "'");
		personGpSet.reset();
		
		if (personGpSet != null && !personGpSet.isEmpty()) {
			
			personGpMbo = personGpSet.getMbo(0);
			
			  interfaceRecord.setValue("ORGANIZATION_NAME", personGpMbo.getString("DESCRIPTION"));
			    interfaceRecord.setValue("ORGANIZATION_ID", personGpMbo.getString("EXTERNALREFID"));
		}
		else
		{
			
			throw new MXApplicationException("ORGNOTFOUND",this.costCenter );
		}
		
		
		interfaceRecord.setValue("EXPENDITURE_TYPE", this.expenditureType);

		interfaceRecord.setValue(PAInterfaceFields.Source.toString(), "INV");
		
		if (projectType.toUpperCase().startsWith("REPEX"))
		{
			
			interfaceRecord.setValue("DR_CODE_COMBINATION_ID", (iAmA == GLSide.DEBIT  ? getGLCodeCombinationID(interfaceRecord) : this.owner.creditAccountingString.getGLCodeCombinationID(interfaceRecord)));
			interfaceRecord.setValue("CR_CODE_COMBINATION_ID", (iAmA == GLSide.CREDIT ? getGLCodeCombinationID(interfaceRecord) : this.owner.creditAccountingString.getGLCodeCombinationID(interfaceRecord)));
		}
		
	}

	

	@Override
	public void copyToGLInterfaceRecord(MboRemote interfaceRecord)
			throws Exception {
		// TODO Auto-generated method stub
		
		interfaceRecord.setValue("CODE_COMBINATION_ID", getGLCodeCombinationID(interfaceRecord));
		
		interfaceRecord.setValue(GLInterfaceFields.ProjectNumber.toString(),   this.projectNum);
		interfaceRecord.setValue(GLInterfaceFields.TaskNumber.toString(),      this.taskNum);
		interfaceRecord.setValue(GLInterfaceFields.ControllingTeam.toString(), this.costCenter);
		interfaceRecord.setValue(GLInterfaceFields.ExpenditureType.toString(), this.expenditureType);
		
		
	}
}
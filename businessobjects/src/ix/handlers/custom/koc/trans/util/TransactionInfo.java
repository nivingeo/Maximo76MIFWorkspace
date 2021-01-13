package ix.handlers.custom.koc.trans.util;

import ix.handlers.custom.koc.trans.util.Field.GLInterfaceFields;
import ix.handlers.custom.koc.trans.util.Field.PAInterfaceFields;
import ix.handlers.custom.koc.trans.util.JournalsHelper.GLSide;


import java.math.BigDecimal;

import psdi.mbo.MboRemote;
import psdi.util.MXException;

public class TransactionInfo
{
	public long				apiSequence;
	private long			oracleGroupID;

	private String			tableName;					// table on which the event occurred
	private String			transactionType;			// value of the type field - if the table
														// is MATRECTRANS, then ISSUETYPE, if the table is INVTRANS, then TRANSTYPE, etc.

	public String			maximoTransactionId;
	
	public String siteID;

	AccountingString		accountingString;
	AccountingString		debitAccountingString;
	AccountingString		creditAccountingString;

	ItemProperties			itemProperties;
	AuditProperties			auditProperties;
	DocumentProperties		documentProperties;	
	DocumentProperties		proratedDocumentProperties;
	
	
	// transaction value
	public double		lineCostInBaseCurrency;
	public double		lineCostInTransCurrency;
	public String			currency;
	public double		exchangeRate;
	public BigDecimal		distrCostInBaseCurrency;
	public BigDecimal		distrCostInTransCurrency;
	
	
	public String 			finyear;
	public long				lineId;

	// miscellaneous properties
	public String			fromLocation;
	public String			toLocation;
	public String			contractNumber;
	
	public String			glReference10;

	public String			mXTransType="";	
	public String			caType="";	
	public String			caBatchName="";	
	public String			projectNum="";	
	public String			TaskNum="";	
	private OFUtil 			ofUtil;
	
	public OFUtil getOfUtil()
	{
		return ofUtil;
	}
	
	private boolean			isCFA	= Boolean.FALSE;
	public boolean isCFA()
	{
		return isCFA;
	}
	
	public TransactionInfo(OFUtil ofUtil2, String transactionType, String tableName, long extRefID) throws Exception
	{
		super();
		
		ofUtil = ofUtil2;
		
		this.transactionType = transactionType;
		this.tableName 		 = tableName;
		
		isCFA	= "CFA".equals(tableName);
		
		//this.apiSequence 	= ofUtil.getApiSequenceNextval();
		this.apiSequence=extRefID;
		   
		this.oracleGroupID 	= ofUtil.getGLGroupID();
		   
		auditProperties      		= new AuditProperties(ofUtil);
		itemProperties       		= new ItemProperties();
		documentProperties 	 		= new DocumentProperties();
		proratedDocumentProperties 	= new DocumentProperties();
	}



	public void setAccountingStringTo(GLSide glSide)
	{
		switch (glSide)
		{
			case CREDIT:
				creditAccountingString = accountingString;
				break;
			case DEBIT:
				debitAccountingString = accountingString;
				break;
		}		
	}

	public void setDebitAccountingString(AccountingString debitAccountingString)
	{
		debitAccountingString.setOwner(this, GLSide.DEBIT);
		this.debitAccountingString = debitAccountingString;
	}

	public void setCreditAccountingString(AccountingString creditAccountingString)
	{
		creditAccountingString.setOwner(this, GLSide.CREDIT);
		this.creditAccountingString = creditAccountingString;
	}

	public void setAccountingString(AccountingString accountingString)
	{
		this.accountingString = accountingString;
	}
	
	public String getProjectType(GLSide side, MboRemote mbo) throws Exception
	{
		if (GLSide.DEBIT.equals(side))
		{
			return debitAccountingString.getProjectType(mbo);
		}
		else
		{
			return creditAccountingString.getProjectType(mbo);
		}
	}
	
	public boolean debitsOPEX()
	{
		return this.debitAccountingString.isOPEX;
	}

	public boolean creditsOPEX() 
	{
		return this.creditAccountingString.isOPEX;
	}

	public String getJESourceName() throws  MXException
	{		
		String parameterName = tableName + "_" + transactionType.replace(' ', '_') + "_JESOURCE";
		return JeDffHelper.getValueFor(parameterName);
	}

	public String getJECategoryName() throws Exception
	{
		String parameterName = tableName + "_" + transactionType.replace(' ', '_') + "_CATEGORY";
		
		return JeDffHelper.getValueFor(parameterName);
	}

	public String getDFFCategory() throws Exception
	{
		String parameterName = tableName + "_" + transactionType.replace(' ', '_') + "_DFFCATEGORY";
		
		return JeDffHelper.getValueFor(parameterName);
	}


	public void copyToGLInterfaceRecord(MboRemote interfaceRecord, GLSide side) throws Exception
	{	
		auditProperties.copyToGLInterfaceRecord(interfaceRecord);
		 
		interfaceRecord.setValue("CONTEXT", getDFFCategory());
		interfaceRecord.setValue("USER_JE_CATEGORY_NAME", getJECategoryName());
		
		
		documentProperties.copyToGLInterfaceRecord(interfaceRecord);
		
		itemProperties.copyToGLInterfaceRecord(interfaceRecord);
		    
		interfaceRecord.setValue(GLInterfaceFields.ApiSequence.toString(), "" + this.apiSequence);
		
		interfaceRecord.setValue(GLInterfaceFields.TransactionType.toString(), this.transactionType);
		
		interfaceRecord.setValue(GLInterfaceFields.MaximoTransactionNumber.toString(), "" + this.maximoTransactionId);
		
		if (this.fromLocation != null)
		{
			interfaceRecord.setValue(GLInterfaceFields.FromLocation.toString(), this.fromLocation);
		}
		interfaceRecord.setValue(GLInterfaceFields.ToLocation.toString(), this.toLocation);
		interfaceRecord.setValue(GLInterfaceFields.ContractNumber.toString(), this.contractNumber);
		interfaceRecord.setValue("GROUP_ID", this.oracleGroupID);
		
				
		if (proratedDocumentProperties.docNum != null && !proratedDocumentProperties.docNum.equals(""))
		{
			interfaceRecord.setValue(GLInterfaceFields.ProratedDocumentNumber.toString(), proratedDocumentProperties.docNum);
		}
		
		//double lineCost = lineCostInBaseCurrency.setScale(3, BigDecimal.ROUND_HALF_EVEN);
		double lineCost=lineCostInBaseCurrency;
		boolean origSign = false;
		if (lineCost > 0)
		{
			//lineCost = lineCostInBaseCurrency.setScale(3, BigDecimal.ROUND_HALF_EVEN);
		}
		else
		{
			//lineCost = lineCostInBaseCurrency.setScale(3, BigDecimal.ROUND_HALF_EVEN).negate();
			lineCost=-lineCost;
			origSign = true;
		}
		if (side == GLSide.DEBIT)
		{
			
			debitAccountingString.copyToGLInterfaceRecord(interfaceRecord);
			if (origSign)
			{
			   interfaceRecord.setValue("ENTERED_CR", lineCost);
			}
			else
			{
				interfaceRecord.setValue("ENTERED_DR", lineCost);
			}
		}
		else
		{
			creditAccountingString.copyToGLInterfaceRecord(interfaceRecord);
			if (origSign)
			{
				interfaceRecord.setValue("ENTERED_DR", lineCost);
			}
			else
			{
				interfaceRecord.setValue("ENTERED_CR", lineCost);
			}
		}
		
	}


	
	public void copyToPAInterfaceRecord(MboRemote interfaceRecord, GLSide side) throws MXException, Exception
	{
		
		if (side == GLSide.DEBIT)
		{
			
			debitAccountingString.copyToPAInterfaceRecord(interfaceRecord);
			interfaceRecord.setValue("DENOM_RAW_COST", lineCostInBaseCurrency);// transaction.lineCostInTransCurrency);
			
		}
		else
		{ 
			
			creditAccountingString.copyToPAInterfaceRecord(interfaceRecord);
			interfaceRecord.setValue("DENOM_RAW_COST", -lineCostInBaseCurrency);// transaction.lineCostInTransCurrency);
		}
				if (this.mXTransType==null || this.mXTransType.equals("") )
		{
					
				}
		else
		{
			
		}
				
				
		interfaceRecord.setValue(PAInterfaceFields.ApiSequence.toString(),    ""+this.apiSequence);
		
		interfaceRecord.setValue(PAInterfaceFields.TransactionType.toString(), this.transactionType);
		
		interfaceRecord.setValue(PAInterfaceFields.MaximoTransactionNumber.toString(), "" + this.maximoTransactionId);
		
		interfaceRecord.setValue(PAInterfaceFields.ContractNumber.toString(), this.contractNumber);
		
		auditProperties.copyToPAInterfaceRecord(interfaceRecord);
		
		itemProperties.copyToPAInterfaceRecord(interfaceRecord);
		
		interfaceRecord.setValue("ATTRIBUTE_CATEGORY", getDFFCategory());
		
		documentProperties.copyToPAInterfaceRecord(interfaceRecord);	
		
	}
	

}
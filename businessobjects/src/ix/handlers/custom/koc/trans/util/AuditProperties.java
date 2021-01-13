package ix.handlers.custom.koc.trans.util;

//Praxis - Rojitha - Util class for Journal Processing
import ix.handlers.custom.koc.trans.util.Field.GLInterfaceFields;
import ix.handlers.custom.koc.trans.util.Field.PACommitmentFields;
import ix.handlers.custom.koc.trans.util.Field.PAInterfaceFields;
import java.util.Date;

import java.util.Calendar;

import psdi.mbo.MboRemote;
public class AuditProperties implements Mappable
{
	protected Date	actualDate;
	protected Date	transactionDate;
	protected String	createdBy;
	protected String	description;

	private OFUtil	ofUtil;
	
	public AuditProperties(OFUtil ofUtil)
	{
		this.ofUtil = ofUtil;
	}

	

	public void copyToPAInterfaceRecord(MboRemote interfaceRecord) throws Exception
	{
		
		
		interfaceRecord.setValue("EXPENDITURE_ITEM_DATE", this.transactionDate);
		
		
		if (interfaceRecord.getDouble("ACCT_RAW_COST") != 0.00)
		{
			interfaceRecord.setValue("GL_DATE", this.actualDate == null ? this.transactionDate : this.actualDate);
		}
		
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.transactionDate);
		while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY)
		{
			calendar.add(Calendar.DATE, 1);
		}
		interfaceRecord.setValue("EXPENDITURE_ENDING_DATE", new Date(calendar.getTime().getTime()));
		interfaceRecord.setValue(PAInterfaceFields.TransactionDescription.toString(), this.description);
		
		
	}

	public void copyToGLInterfaceRecord(MboRemote interfaceRecord) throws Exception
	{
		Date accountingDate = this.actualDate == null ? this.transactionDate : this.actualDate;
		
		
		//ofUtil.getGLPeriod(accountingDate,interfaceRecord);
		
		interfaceRecord.setValue("ACCOUNTING_DATE", accountingDate);
						
		interfaceRecord.setValue("CURRENCY_CONVERSION_DATE", this.actualDate);
		interfaceRecord.setValue(GLInterfaceFields.TransactionDescription.toString(), this.description);
		interfaceRecord.setValue(GLInterfaceFields.UserKOCNumber.toString(), this.createdBy);
	}
	
}
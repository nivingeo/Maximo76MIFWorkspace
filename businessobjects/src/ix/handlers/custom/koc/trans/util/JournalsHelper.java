package ix.handlers.custom.koc.trans.util;

//Praxis - Rojitha - Util class for Journal Processing
import ix.handlers.custom.koc.trans.util.Field.GLInterfaceFields;
import ix.handlers.custom.koc.trans.util.Field.PAInterfaceFields;
import ix.handlers.custom.koc.trans.util.JournalsHelper.GLSide;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class JournalsHelper
{
	
	ArrayList		journals;
	TransactionInfo	transaction;
	MboSetRemote glInterfaceMboSet;
	
	public enum GLSide
	{
		CREDIT, DEBIT
	}

	public JournalsHelper(MboRemote transactionMbo) throws RemoteException, MXException
	{
//		this.ixHandler  = ixHandler;
//		this.ix			= ixHandler.ix;
		
		journals = new ArrayList();
		//glInterfaceMboSet=transactionMbo.getMboSet("K_GL_INTERFACE");
	}

	public void setTransaction(TransactionInfo transaction)
	{
		 
		this.transaction = transaction;
	}
		
	public void prepareJournal(MboRemote transactionMbo, String siteid, String orgid) throws Exception
	{
		
		if (transaction.lineCostInTransCurrency == 0.00)
		{
			// Skip interface
			return;
//			if (!transaction.currency.equals("KWD"))
//			{
//				throw new MXApplicationException("LineCostNotValid","Amount must be specified in transactional currency");
//			}
//			transaction.lineCostInTransCurrency = transaction.lineCostInBaseCurrency;
		}

		if (transaction.lineCostInBaseCurrency == 0.00)
		{
			// Skip interface
			return;
		}
		setGLInterfaceRecord(GLSide.DEBIT,transactionMbo,siteid,orgid);
		setGLInterfaceRecord(GLSide.CREDIT,transactionMbo,siteid,orgid);
		
		if (!transaction.debitsOPEX())
		{
			
			setPAInterfaceRecord(GLSide.DEBIT,transactionMbo);
		}

		if (!transaction.creditsOPEX())
		{
			
			setPAInterfaceRecord(GLSide.CREDIT,transactionMbo);
		}
	}

	
	private void setPAInterfaceRecord(GLSide side,MboRemote transactionMbo) throws Exception
	{
		MboSetRemote paTransactionSetRemote=transactionMbo.getMboSet("K_PA_TRANSACTION_INTER_ALL");
		String source="";
				
		MboRemote paTransactionMboRemote=paTransactionSetRemote.add(2L);
		
		
		paTransactionMboRemote.setValue    ("TRANSACTION_SOURCE", 		"MAXIMO");		
		paTransactionMboRemote.setValue	 ("ATTRIBUTE_CATEGORY", 		 transaction.getDFFCategory());
		System.out.println("********Inside JournalHelper inside setPAInterfaceRecord() in Journal Transaction Processing : transaction.getDFFCategory()::"+transaction.getDFFCategory());
		
		paTransactionMboRemote.setValue("QUANTITY", 					 1);
		paTransactionMboRemote.setValue	 ("DENOM_CURRENCY_CODE", 		 "KWD");// transaction.currency);
		
		paTransactionMboRemote.setValue	 ("TRANSACTION_STATUS_CODE", 	 "P");
		paTransactionMboRemote.setValue		 ("ORG_ID", 					1797 );
		paTransactionMboRemote.setValue	 ("USER_TRANSACTION_SOURCE", 	 "MAXIMO");
		paTransactionMboRemote.setValue	 ("UNMATCHED_NEGATIVE_TXN_FLAG", "Y");
		
		paTransactionMboRemote.setValue      ("TXN_INTERFACE_ID", transaction.maximoTransactionId);
				
		if (transaction.getProjectType(side,transactionMbo) != null && transaction.getProjectType(side,transactionMbo).toUpperCase().startsWith("REPEX"))
		{
			paTransactionMboRemote.setValue("ACCT_RAW_COST", paTransactionMboRemote.getDouble("DENOM_RAW_COST"));
		}
		
		transaction.copyToPAInterfaceRecord(paTransactionMboRemote, side);
		//paTransactionSetRemote.save();
		
		//journals.add(paTransactionMboRemote);
	}
    
	/*
	private void setGLInterfaceRecord(GLSide side,MboRemote transactionMbo) throws Exception
	{
		MboSetRemote glTransactionSetRemote=transactionMbo.getMboSet("K_GL_INTERFACE");
		MboRemote glTransactionMboRemote=glTransactionSetRemote.add(2L);

		glTransactionMboRemote.setValue("STATUS", 				"NEW");
		glTransactionMboRemote.setValue("ACTUAL_FLAG", 			"A");
		glTransactionMboRemote.setValue("CREATED_BY", 			"16053");
		
		glTransactionMboRemote.setValue("DATE_CREATED", 		new Timestamp(System.currentTimeMillis()));

		glTransactionMboRemote.setValue("SET_OF_BOOKS_ID", 		7);
		glTransactionMboRemote.setValue("CHART_OF_ACCOUNTS_ID", "50432");
		glTransactionMboRemote.setValue("USER_JE_SOURCE_NAME", 	transaction.getJESourceName());
		
		glTransactionMboRemote.setValue("CURRENCY_CODE", 		"KWD");
		
		transaction.copyToGLInterfaceRecord(glTransactionMboRemote, side);
		
	    
	}*/
	
	private void setGLInterfaceRecord(GLSide side,MboRemote transactionMbo, String siteID, String orgID) throws Exception
	{
		MboSetRemote glTransactionSetRemote=transactionMbo.getMboSet("K_GL_INTERFACE");
		MboRemote glTransactionMboRemote=glTransactionSetRemote.add(2L);

		glTransactionMboRemote.setValue("STATUS", 				"NEW");
		glTransactionMboRemote.setValue("ACTUAL_FLAG", 			"A");
		glTransactionMboRemote.setValue("CREATED_BY", 			"16053");
		
		glTransactionMboRemote.setValue("DATE_CREATED", 		new Timestamp(System.currentTimeMillis()));

		if (siteID.equalsIgnoreCase("KAF")) {
			glTransactionMboRemote.setValue("SET_OF_BOOKS_ID", "83");
			glTransactionMboRemote.setValue("LEDGER_ID", "83");
		} else {
			glTransactionMboRemote.setValue("SET_OF_BOOKS_ID",(orgID + "_SOBID"));
			glTransactionMboRemote.setValue("LEDGER_ID",(orgID + "_SOBID"));
		}
		
		if (siteID.equalsIgnoreCase("KAF")) {
			glTransactionMboRemote.setValue("CHART_OF_ACCOUNTS_ID", "50615");
		} else {
			glTransactionMboRemote.setValue("CHART_OF_ACCOUNTS_ID",(orgID + "_COAID"));
		}

		glTransactionMboRemote.setValue("USER_JE_SOURCE_NAME", 	transaction.getJESourceName());
		
		glTransactionMboRemote.setValue("CURRENCY_CODE", 		"KWD");
		
		transaction.copyToGLInterfaceRecord(glTransactionMboRemote, side);
		
	    
	}
	


	
	
}
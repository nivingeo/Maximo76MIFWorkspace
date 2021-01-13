package ix.handlers.custom.koc.trans.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.Translate;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class TransactionHelper
{
	//private IXHandler				MboRemote;
	
	private TransactionInfo			transaction;
	private String					description;
	private OFUtil					ofUtil;
	private MboRemote ixHandler;
	
	public TransactionHelper(MboRemote costMboRemote)
	{
		this.ixHandler = costMboRemote;	
//		
	ofUtil = new OFUtil(costMboRemote);
	}
	
	
	public TransactionInfo createIssueTransaction(MboRemote mut, long externalRefID, String projectNum, String taskNum, String expType, String costCenter) throws Exception
	{
		TransactionInfo transaction = new TransactionInfo(ofUtil, mut.getString("ISSUETYPE"), "MATUSETRANS", externalRefID);
		transaction.maximoTransactionId = mut.getString("MATUSETRANSID");

		AccountingString debitAccountingString = new AccountingString(ofUtil,projectNum, taskNum, costCenter, expType, mut.getString("GLDEBITACCT"));
		transaction.setDebitAccountingString(debitAccountingString);
		
		transaction.setCreditAccountingString(new AccountingString(ofUtil, mut.getString("GLCREDITACCT")));
		transaction.currency 				= mut.getString("CURRENCYCODE");
		transaction.exchangeRate 			= mut.getDouble("EXCHANGERATE");
		transaction.lineCostInBaseCurrency 	= mut.getDouble("LINECOST");
		transaction.lineCostInTransCurrency = mut.getDouble("CURRENCYLINECOST");

		transaction.auditProperties.transactionDate = mut.getDate("TRANSDATE");
		transaction.auditProperties.createdBy       = "MAXIMO";
		transaction.auditProperties.actualDate      = mut.getDate("ACTUALDATE");
		transaction.auditProperties.description     = FormatDescription("MATUSETRANS (" + mut.getString("ISSUETYPE") + "-" + mut.getString("ITEMNUM") + ") " + mut.getString("DESCRIPTION"), 150);
		
		if ("RETURN".equals(mut.getString("ISSUETYPE")))
		{
			transaction.documentProperties.docType		= "Return Request";
			transaction.documentProperties.docNum		= mut.getString("RRNUM");
			transaction.documentProperties.docLineNum   = mut.getString("RRLINENUM");
			transaction.documentProperties.docSite      = mut.getString("SITEID");			
		}
		else if (mut.getString("MRNUM") != null)
		{
			String dtrType = "DTR ";
			MboSetRemote mrSet=mut.getMboSet("MR");
			MboRemote mr;
			if (mrSet!=null && !mrSet.isEmpty())
			{
				mr=mrSet.getMbo(0);
				dtrType += mr.getString("DTRTYPE");
			}
			
			transaction.documentProperties.docType		= dtrType;
			transaction.documentProperties.docNum		= mut.getString("MRNUM");
			transaction.documentProperties.docLineNum   = mut.getString("MRLINENUM");
			transaction.documentProperties.docSite      = mut.getString("SITEID");	
		}
		else
		{
			MboSetRemote woSet=mut.getMboSet("WORKORDER");
			MboRemote wo;
			String woClass = "WORKORDER ";
			if (woSet!=null && !woSet.isEmpty())
			{
				wo=woSet.getMbo(0);
				woClass += wo.getString("WOCLASS");
			}
			
			transaction.documentProperties.docType		= woClass;
			transaction.documentProperties.docNum		= mut.getString("MRNUM");
			transaction.documentProperties.docLineNum   = mut.getString("MRLINENUM");
			transaction.documentProperties.docSite      = mut.getString("SITEID");	
		}
		
		transaction.fromLocation = mut.getString("STORELOC");

		transaction.itemProperties.itemNumber 		= mut.getString("ITEMNUM");
		transaction.itemProperties.itemSetID		= mut.getString("ITEMSETID");
		transaction.itemProperties.commodityGroup	= mut.getString("COMMODITYGROUP");
		transaction.itemProperties.commodityCode	= mut.getString("COMMODITY");
		transaction.itemProperties.description		= "";
		
		return transaction;
	}

	public TransactionInfo createReceiptTransferTransaction(MboRemote matrecMbo, MboRemote originalReceipt, long extRefID) throws Exception, MXException
	{
		MboRemote creditMatRecTrans = (originalReceipt != null) ? originalReceipt : matrecMbo;
		MboRemote poLine;
		String issueType = creditMatRecTrans.getString("ISSUETYPE");
		
    	
		MboSetRemote poLineSet = matrecMbo.getMboSet("POLINE");
		AccountingString creditAccountingString = null;
		
		if (poLineSet !=null && !poLineSet.isEmpty())
		{
			
	    	poLine=poLineSet.getMbo(0);
			creditAccountingString = new AccountingString(ofUtil, poLine.getString("CREDITPROJECTNUM"), poLine.getString("CREDITTASKNUM"), poLine.getString("CREDITCOSTCENTER"), poLine.getString("CREDITEXPENDITURETYPE"), creditMatRecTrans.getString("GLCREDITACCT"));
		}
		else
		{
			
	    	creditAccountingString = new AccountingString(ofUtil, creditMatRecTrans.getString("GLCREDITACCT"));

		}

		AccountingString debitAccountingString = new AccountingString(ofUtil, matrecMbo.getString("PROJECTNUM"), matrecMbo.getString("TASKNUM"), matrecMbo.getString("COSTCENTER"), matrecMbo.getString("EXPENDITURETYPE"), matrecMbo.getString("GLDEBITACCT"));

		
		String poAppType = getDocumentType(matrecMbo);
		
    	
		issueType = getIssueType(issueType, poAppType, matrecMbo.getString("INVOICENUM"));
		
    	

		TransactionInfo transaction = new TransactionInfo(ofUtil, issueType, "MATRECTRANS", extRefID);
		
		transaction.documentProperties.docType		= poAppType;
		transaction.documentProperties.docNum		= matrecMbo.getString("PONUM");
		transaction.documentProperties.docLineNum   = matrecMbo.getString("POLINENUM");
		transaction.documentProperties.docSite      = matrecMbo.getString("SITEID");
		
		transaction.maximoTransactionId = matrecMbo.getString("RECEIPTNUM");

		transaction.setDebitAccountingString(debitAccountingString);
		transaction.setCreditAccountingString(creditAccountingString);
		transaction.currency = matrecMbo.getString("CURRENCYCODE");
		transaction.exchangeRate = matrecMbo.getDouble("EXCHANGERATE");
		transaction.lineCostInBaseCurrency = matrecMbo.getDouble("LINECOST");
		transaction.lineCostInTransCurrency = matrecMbo.getDouble("CURRENCYLINECOST");
		
		transaction.auditProperties.transactionDate = matrecMbo.getDate("TRANSDATE");
		transaction.auditProperties.createdBy       = "MAXIMO";
		transaction.auditProperties.actualDate      = matrecMbo.getDate("TRANSDATE"); //mrt.getTimestamp("ACTUALDATE");
		transaction.auditProperties.description     = FormatDescription("MATRECTRANSID: " + matrecMbo.getString("MATRECTRANSID") + " " + matrecMbo.getString("DESCRIPTION"), 150);
	
		
    	
		transaction.fromLocation = matrecMbo.getString("FROMSTORELOC") == null ? null : ("FROMSTORE: " + matrecMbo.getString("FROMSTORELOC"));
		transaction.toLocation = matrecMbo.getString("TOSTORELOC") == null ? null : ("TOSTORE: " + matrecMbo.getString("TOSTORELOC"));

		if (matrecMbo.getString("PRORATEDINVOICENUM") != null)
		{
			transaction.proratedDocumentProperties.docType		= "INVOICE";
			transaction.proratedDocumentProperties.docNum		= matrecMbo.getString("PRORATEDINVOICENUM");
			transaction.proratedDocumentProperties.docLineNum   = matrecMbo.getString("PRORATEDLINENUM");
			transaction.proratedDocumentProperties.docSite      = null;
		}

		if (matrecMbo.getString("PRORATEDPONUM") != null)
		{
			transaction.proratedDocumentProperties.docType		= "PO";
			transaction.proratedDocumentProperties.docNum		= matrecMbo.getString("PRORATEDINVOICENUM");
			transaction.proratedDocumentProperties.docLineNum   = matrecMbo.getString("PRORATEDLINENUM");
			transaction.proratedDocumentProperties.docSite      = null;
		}

		transaction.itemProperties.itemNumber 		= matrecMbo.getString("ITEMNUM");
		transaction.itemProperties.itemSetID		= matrecMbo.getString("ITEMSETID");
		transaction.itemProperties.commodityGroup	= matrecMbo.getString("COMMODITYGROUP");
		transaction.itemProperties.commodityCode	= matrecMbo.getString("COMMODITY");
		transaction.itemProperties.description		= matrecMbo.getString("DESCRIPTION");
		
    	
		return transaction;
	}
	

	private String getDocumentType(MboRemote currentMatRecTransRecord) throws Exception
	{
		MboSetRemote poSet = currentMatRecTransRecord.getMboSet("POREV");
		
		String poAppType = null;
		
		if (poSet!=null && !poSet.isEmpty())
		{
			MboRemote po=poSet.getMbo(0);
			poAppType = po.getString("APPTYPE") == null ? "PO" : po.getString("APPTYPE");
			if ("WRON".equals(poAppType))
			{
				// issueType = "TRANSFERWRON";
				poAppType = "Write On Request";
			}
			else
			{
				if ("PO".equals(poAppType))
				{
					if (po.getLong("INTERNAL") == 1)
					{
						if ("DCTOKIR".equals(po.getString("TRTYPE")) || "SURPLUS".equals(po.getString("TRTYPE")))
						{
							// issueType = "TRANSFERWROFF";
							poAppType = "Write Off (TR)";
						}
						else
						{
							// issueType = "TRANSFER";
							poAppType = "Transfer Request";
						}
					}
				}
			}
		}
		
		return poAppType;
	}

	private String getIssueType(String issueType, String documentType, String invoiceNum)
	{
		if ("Write On Request".equals(documentType))
		{
			issueType = "TRANSFERWRON";
		}
		else if ("Write Off (TR)".equals(documentType))
		{
			issueType = "TRANSFERWROFF";
		}
		else if ("Transfer Request".equals(documentType))
		{
			issueType = "TRANSFER";
		}
		if ("INVOICE".equals(issueType))
		{
			if (invoiceNum != null)
			{
				issueType = "CURRVAR";
			}
			else
			{
				issueType = "PRORATING";
			}
		}
		
		return issueType;
	}

	public TransactionInfo createServiceReceiptTransferTransaction(MboRemote servRecTrans, boolean accrual, Double previouslyAccruedCost, long externalRefID) throws Exception
	{
		
    	
		boolean accrFlag = (servRecTrans.getDouble("ACCRUAL") == 1);
		TransactionInfo transaction = new TransactionInfo(ofUtil, ((accrual || accrFlag) ? "ACCRUAL" : servRecTrans.getString("ISSUETYPE")), "SERVRECTRANS",externalRefID);
		transaction.maximoTransactionId = servRecTrans.getString("SERVRECTRANSID");

		AccountingString debitAccStr = new AccountingString(ofUtil, servRecTrans.getString("PROJECTNUM"), servRecTrans.getString("TASKNUM"), servRecTrans.getString("COSTCENTER"), servRecTrans.getString("EXPENDITURETYPE"), servRecTrans.getString("GLDEBITACCT"));
		transaction.setDebitAccountingString(debitAccStr);
		
    	
		if (accrual)
		{
			transaction.setCreditAccountingString(new AccountingString(ofUtil, "10.000000000000.00000.000000.32510020.0000"));
		}
		else
		{
			transaction.setCreditAccountingString(new AccountingString(ofUtil, servRecTrans.getString("GLCREDITACCT")));
		}

		transaction.currency     = servRecTrans.getString    ("CURRENCYCODE");
		transaction.exchangeRate = servRecTrans.getDouble("EXCHANGERATE");
		
		if (!accrual)
		{
			transaction.lineCostInBaseCurrency  = servRecTrans.getDouble("LINECOST");
			transaction.lineCostInTransCurrency = servRecTrans.getDouble("CURRENCYLINECOST");
		}
		else
		{
			
			if (previouslyAccruedCost == null)
			{
				previouslyAccruedCost = 0.00;
			}
			BigDecimal linecost=BigDecimal.valueOf(servRecTrans.getDouble("LINECOST"));
			BigDecimal prevlinecost=BigDecimal.valueOf(previouslyAccruedCost);
			BigDecimal excRate =BigDecimal.valueOf(servRecTrans.getDouble("EXCHANGERATE"));
			
			transaction.lineCostInBaseCurrency = servRecTrans.getDouble("LINECOST")-(previouslyAccruedCost);
			BigDecimal transCurrency=linecost.subtract(prevlinecost).divide(excRate, 4, RoundingMode.CEILING);
			transaction.lineCostInTransCurrency = 
				transCurrency.doubleValue();
		}
		
		Date transdate;
		if (!accrual)
		{
			transdate = servRecTrans.getDate("CHANGEDATE");
			if (transdate == null)
			{
				transdate = servRecTrans.getDate("TRANSDATE");
			}
		}
		else
		{
			transdate = new Timestamp(System.currentTimeMillis());
		}
		
		transaction.auditProperties.transactionDate = transdate;
		transaction.auditProperties.createdBy       = "MAXIMO";
		transaction.auditProperties.actualDate      = transdate; //servRecTrans.getTimestamp("CHANGEDATE");
		transaction.auditProperties.description     = FormatDescription("SERV. REC for " + servRecTrans.getString("PONUM") + " " + servRecTrans.getString("DESCRIPTION"), 150);
		
    	
		transaction.itemProperties.itemNumber 		= servRecTrans.getString("ITEMNUM");
		transaction.itemProperties.itemSetID		= servRecTrans.getString("ITEMSETID");
		transaction.itemProperties.commodityGroup	= servRecTrans.getString("COMMODITYGROUP");
		transaction.itemProperties.commodityCode	= servRecTrans.getString("COMMODITY");
		transaction.itemProperties.description		= servRecTrans.getString("DESCRIPTION");
		
		if (servRecTrans.getString("PONUM") != null)
		{
			
			MboSetRemote poSet = servRecTrans.getMboSet("PO");
			MboRemote po;
			po=poSet.getMbo(0);
			
			transaction.documentProperties.docType		= po.getString("APPTYPE");
			transaction.documentProperties.docNum		= servRecTrans.getString("PONUM");
			transaction.documentProperties.docLineNum   = servRecTrans.getString("POLINENUM");
			transaction.documentProperties.docSite      = servRecTrans.getString("POSITEID");
			
			transaction.contractNumber = po.getString("CONTRACTREFNUM");
		}
		
    	
		return transaction;
		
	}

	public TransactionInfo createInventoryTransaction(MboRemote ixInvTransRecord, long externalRefID) throws Exception
	{
		
    	
		TransactionInfo transaction = new TransactionInfo(ofUtil, ixInvTransRecord.getString("TRANSTYPE"), "INVTRANS",externalRefID);
		transaction.maximoTransactionId = ixInvTransRecord.getString("INVTRANSID");

		transaction.setDebitAccountingString(getDebitAccountingString(ixInvTransRecord));
		transaction.setCreditAccountingString(new AccountingString(ofUtil, ixInvTransRecord.getString("GLCREDITACCT")));

		transaction.currency = "KWD";
		transaction.exchangeRate = 1.0;
		transaction.lineCostInBaseCurrency = ixInvTransRecord.getDouble("LINECOST");
		transaction.lineCostInTransCurrency = ixInvTransRecord.getDouble("LINECOST");
		
		transaction.auditProperties.transactionDate = ixInvTransRecord.getDate("TRANSDATE");
		transaction.auditProperties.createdBy       = "MAXIMO";
		transaction.auditProperties.actualDate      = ixInvTransRecord.getDate("TRANSDATE");
		transaction.auditProperties.description     = "INVTRANS " + ixInvTransRecord.getString("TRANSTYPE");
			
		MboSetRemote itemSet=ixInvTransRecord.getMboSet("ITEM");
		itemSet.setWhere("WHERE ITEMNUM = '" + ixInvTransRecord.getString("ITEMNUM") + "' and ITEMSETID = '" + ixInvTransRecord.getString("ITEMSETID") + "'");
		itemSet.reset();
		
    	
		MboRemote itemRecord;
		if (itemSet!=null && !itemSet.isEmpty())
		{
			itemRecord=itemSet.getMbo(0);
		
			transaction.itemProperties.itemNumber 		= itemRecord.getString("ITEMNUM");
			transaction.itemProperties.itemSetID		= itemRecord.getString("ITEMSETID");
			transaction.itemProperties.commodityGroup	= itemRecord.getString("COMMODITYGROUP");
			transaction.itemProperties.commodityCode	= itemRecord.getString("COMMODITY");
			transaction.itemProperties.description		= "";
		}
		else
		{
			throw new MXApplicationException("itemNotFoundMax", "itemNotFoundMax");
		}
		transaction.fromLocation = ixInvTransRecord.getString("STORELOC");
		transaction.toLocation = ixInvTransRecord.getString("STORELOC");

		if ("AVGCSTADJ".equals(ixInvTransRecord.getString("TRANSTYPE")))
		{
			if (ixInvTransRecord.getString("SONUM") != null)
			{
				transaction.documentProperties.docType		= "SO";
				transaction.documentProperties.docNum		= ixInvTransRecord.getString("SONUM");
				transaction.documentProperties.docLineNum   = null;
				transaction.documentProperties.docSite      = ixInvTransRecord.getString("SOSITEID");
			}
			else
			{
				transaction.documentProperties.docType		= "Cost Adjustment";
				transaction.documentProperties.docNum		= "N/A";
				transaction.documentProperties.docLineNum   = null;
				transaction.documentProperties.docSite      = ixInvTransRecord.getString("SITEID");
			}
		}
		else
		{
			if ("RECBALADJ".equals(ixInvTransRecord.getString("TRANSTYPE")))
			{
				MboSetRemote bookSet = ixInvTransRecord.getMboSet("K_COUNT_BOOK");
				MboRemote bookMbo;
				
		    	
				if (bookSet != null && !bookSet.isEmpty() )
				{
					bookMbo=bookSet.getMbo(0);
					transaction.documentProperties.docType		= "Cycle Count";
					transaction.documentProperties.docNum		= bookMbo.getString("COUNTBOOKNUM");
					transaction.documentProperties.docLineNum   = null;
					transaction.documentProperties.docSite      = ixInvTransRecord.getString("SITEID");
				}
				else
				{
					transaction.documentProperties.docType		= ixInvTransRecord.getString("TRANSTYPE");
					transaction.documentProperties.docNum		= "N/A";
					transaction.documentProperties.docLineNum   = null;
					transaction.documentProperties.docSite      = ixInvTransRecord.getString("SITEID");					
				}
			}
			else
			{
				transaction.documentProperties.docType		= ixInvTransRecord.getString("TRANSTYPE");
				transaction.documentProperties.docNum		= "N/A";
				transaction.documentProperties.docLineNum   = null;
				transaction.documentProperties.docSite      = ixInvTransRecord.getString("SITEID");
			}
		}
		
    	
		return transaction;		
	}
	
	
	private AccountingString getDebitAccountingString(MboRemote inventoryTransactionRecord) throws Exception
	{
		
    	
		if ("RECBALADJ".equals(inventoryTransactionRecord.getString("TRANSTYPE")))
		{
			MboSetRemote storeroomSet=inventoryTransactionRecord.getMboSet("LOCATIONS");
			MboRemote storeroomMbo;
			storeroomSet.setWhere("where type = 'STOREROOM' and location = '"+ inventoryTransactionRecord.getString("STORELOC") + "'") ;
			storeroomSet.reset();
			
	    	
			if (storeroomSet!=null && !storeroomSet.isEmpty())
			{
				storeroomMbo=storeroomSet.getMbo(0);
				if ("DC".equals(storeroomMbo.getString("STORETYPE")))
					return new AccountingString(ofUtil, inventoryTransactionRecord.getString("PROJECTNUM"), inventoryTransactionRecord.getString("TASKNUM"), inventoryTransactionRecord.getString("COSTCENTER"), inventoryTransactionRecord.getString("EXPENDITURETYPE"), inventoryTransactionRecord.getString("GLDEBITACCT"));
			}
		}
		
    	
		return new AccountingString(ofUtil, inventoryTransactionRecord.getString("GLDEBITACCT"));
	}

	public TransactionInfo createInvoiceTransaction(MboRemote ixInvoiceTransRecord, long externalRefID) throws Exception
	{
		
    	
		TransactionInfo transaction = new TransactionInfo(ofUtil, ixInvoiceTransRecord.getString("TRANSTYPE"), "INVOICETRANS", externalRefID);
		transaction.maximoTransactionId = ixInvoiceTransRecord.getString("INVOICETRANSID");

		transaction.setDebitAccountingString (new AccountingString(ofUtil, ixInvoiceTransRecord.getString("GLDEBITACCT")));
		transaction.setCreditAccountingString(new AccountingString(ofUtil, ixInvoiceTransRecord.getString("GLCREDITACCT")));

		transaction.currency = "KWD";
		transaction.exchangeRate = 1.0;
		transaction.lineCostInBaseCurrency = ixInvoiceTransRecord.getDouble("LINECOST");
		transaction.lineCostInTransCurrency = ixInvoiceTransRecord.getDouble("LINECOST");
		
		transaction.auditProperties.transactionDate = ixInvoiceTransRecord.getDate("TRANSDATE");
		transaction.auditProperties.createdBy       = "MAXIMO";
		transaction.auditProperties.actualDate      = ixInvoiceTransRecord.getDate("TRANSDATE");
		transaction.auditProperties.description     = "CURVAR".equals(ixInvoiceTransRecord.getString("TRANSTYPE")) ? "Currency Variance Transaction" : ("INVCEVAR".equals(ixInvoiceTransRecord.getString("TRANSTYPE")) ? "Invoice Variance Transaction" : ixInvoiceTransRecord.getString("TRANSTYPE"));
		
		MboSetRemote itemSet=ixInvoiceTransRecord.getMboSet("ITEM");
		MboRemote itemRecord;
		itemSet.setWhere("WHERE (ITEMNUM, ITEMSETID) in (select l.itemnum, l.itemsetid " + "from invoiceline l " + "where l.invoicenum = '" + ixInvoiceTransRecord.getString("INVOICENUM") + "' " + "and l.invoicelinenum = " + ixInvoiceTransRecord.getString("INVOICELINENUM") + " " + "and l.siteid = '" + ixInvoiceTransRecord.getString("SITEID") + "')");
		itemSet.reset();
		if (itemSet!=null && !itemSet.isEmpty())
		{
			itemRecord=itemSet.getMbo(0);
			transaction.itemProperties.itemNumber 		= itemRecord.getString("ITEMNUM");
			transaction.itemProperties.itemSetID		= itemRecord.getString("ITEMSETID");
			transaction.itemProperties.commodityGroup	= itemRecord.getString("COMMODITYGROUP");
			transaction.itemProperties.commodityCode	= itemRecord.getString("COMMODITY");
			transaction.itemProperties.description		= "";
		}
		
		transaction.documentProperties.docType		= "INVOICE";
		transaction.documentProperties.docNum		= ixInvoiceTransRecord.getString("INVOICENUM");
		transaction.documentProperties.docLineNum   = ixInvoiceTransRecord.getString("INVOICELINENUM");
		transaction.documentProperties.docSite      = ixInvoiceTransRecord.getString("SITEID");
		
    	
		
		return transaction;
	}
	

	public TransactionInfo createInvoiceLineCostsTransaction(MboRemote costMboRemote, MboRemote srcInvoice, int i, MboRemote InvoiceLineMbo, long externalRefID, String linetype) throws   Exception, MXException
	{							
		
	        
		TransactionInfo transaction;	
		if (srcInvoice.getString("DOCUMENTTYPE").equals("PC"))
		{
			transaction = new TransactionInfo(ofUtil, srcInvoice.getString("APPTYPE") + "_" + costMboRemote.getString("GL_CATEG")  , "INVOICE",externalRefID);
		}else //Nivin - Sundry Accruals
			if (srcInvoice.getString("DOCUMENTTYPE").equals("SA"))
			{
				transaction = new TransactionInfo(ofUtil, "SA" , "INVOICE",externalRefID);
			}
		else
		{
			transaction = new TransactionInfo(ofUtil, "ADJUSTMENT_" + srcInvoice.getString("APPTYPE"), "INVOICE",externalRefID);
			
		    
		}
		
		transaction.maximoTransactionId = costMboRemote.getString("TRANSACTIONID");
		// PKK Cost Adjustment Changes
		transaction.mXTransType = srcInvoice.getString("APPTYPE").toString(); 
		transaction.caType = srcInvoice.getString("COSTADJTYPE");
		transaction.caBatchName = srcInvoice.getString("COSTADJTYPE") + " " + srcInvoice.getString("APPTYPE").toString() + " - " + srcInvoice.getString("COSTADJTYPE") + costMboRemote.getString("INVOICENUM");
		//PKK PTE and ETP Changes
		transaction.projectNum = costMboRemote.getString("PONUM");
		transaction.TaskNum = costMboRemote.getString("ITEMNUM");
		
	    
		AccountingString accountingString = new AccountingString(ofUtil, costMboRemote.getString( "PROJECTNUM"), costMboRemote.getString( "TASKNUM"), costMboRemote.getString( "COSTCENTER"), costMboRemote.getString( "EXPENDITURETYPE"), costMboRemote.getString( "GLDEBITACCT"));
		transaction.setAccountingString(accountingString);
		transaction.lineCostInTransCurrency = costMboRemote.getDouble("LOADEDCOST");
		transaction.currency = srcInvoice.getString("CURRENCYCODE");
		transaction.exchangeRate = srcInvoice.getDouble("EXCHANGERATE");
		transaction.contractNumber = srcInvoice.getString("CONTRACTREFNUM");
		
		transaction.auditProperties.transactionDate = srcInvoice.getDate("STATUSDATE");
		transaction.auditProperties.createdBy       = srcInvoice.getString("CHANGEBY");
		transaction.auditProperties.actualDate      = srcInvoice.getDate("STATUSDATE");
		transaction.auditProperties.description     = srcInvoice.getString("DESCRIPTION");
		
	    
		if (srcInvoice.getString("DOCUMENTTYPE").equals("PC"))
		{
			transaction.auditProperties.createdBy		= costMboRemote.getString( "APPRBY");
			transaction.auditProperties.description     = costMboRemote.getString( "DESCRIPTION");
		}
		
		if (srcInvoice.getString("PONUM") != null && srcInvoice.getString("PONUM").length()>0)
		{		
			transaction.documentProperties.docType		= "PO";
			transaction.documentProperties.docNum		= srcInvoice.getString("PONUM");
			transaction.documentProperties.docLineNum   = costMboRemote.getString( "POLINENUM");
			transaction.documentProperties.docSite      = srcInvoice.getString("SITEID");
			
		    
		}
		else
		{
			transaction.documentProperties.docType		= srcInvoice.getString("APPTYPE") + " ADJUSTMENT INVOICE";
			if (srcInvoice.getString("DOCUMENTTYPE").equals("PC"))
			{
				transaction.documentProperties.docType		= "PETTY CASH";
			}
			//Nivin - Sundry Accruals
			if (srcInvoice.getString("DOCUMENTTYPE").equals("SA"))
			{
				transaction.documentProperties.docType		= "SUNDRY ACCRUALS";
			}

			// PKK Cost Adjustment Changes
			if (srcInvoice.getString("APPTYPE").equals("CA"))
			{
				transaction.documentProperties.docType		= srcInvoice.getString("COSTADJTYPE");
			}

			transaction.documentProperties.docNum		= srcInvoice.getString("INVOICENUM");
			transaction.documentProperties.docLineNum   = costMboRemote.getString( "INVOICELINENUM");
			transaction.documentProperties.docSite      = srcInvoice.getString("SITEID");
		}
		
		//If Not CA then set item properties. Itemnum field used on CA for Task Number
		if (!srcInvoice.getString("APPTYPE").equals("CA"))
		{
		setItemProperties(costMboRemote.getString( "ITEMNUM"), costMboRemote.getString("ITEMSETID"), costMboRemote.getString( "DESCRIPTION"), transaction, InvoiceLineMbo, linetype);
		}

		if (srcInvoice.getString("DOCUMENTTYPE").equals("PC"))
		{
			transaction.itemProperties.itemNumber = costMboRemote.getString( "INVOICENUM");
			transaction.itemProperties.commodityCode = costMboRemote.getString( "DISPLAYNAME");
		}
		
		// PKK Cost Adjustment Changes
		if (srcInvoice.getString("APPTYPE").equals("CA"))
		{
			transaction.itemProperties.itemNumber = costMboRemote.getString( "COSTADJLINETYPE");
			transaction.itemProperties.commodityCode = srcInvoice.getString("SPREQUESTINGTEAM");
		}

		transaction.proratedDocumentProperties.docType		= "Adjustment Invoice (" + srcInvoice.getString("APPTYPE") + ")";
		if (srcInvoice.getString("DOCUMENTTYPE").equals("PC"))
		{
			transaction.proratedDocumentProperties.docType		= "Petty Cash (" + srcInvoice.getString("APPTYPE") + ")";
		}
		//Nivin - Sundry Accruals
		if (srcInvoice.getString("DOCUMENTTYPE").equals("SA"))
		{
			transaction.proratedDocumentProperties.docType		= "Sundry Accruals (" + srcInvoice.getString("APPTYPE") + ")";
		}
		
		// PKK Cost Adjustment Changes
		if (srcInvoice.getString("APPTYPE").equals("CA"))
		{
			transaction.proratedDocumentProperties.docType		= "Cost Adjustment (" + srcInvoice.getString("APPTYPE") + ")";
		}

		transaction.proratedDocumentProperties.docNum		= srcInvoice.getString("VENDORINVOICENUM");
		transaction.proratedDocumentProperties.docLineNum   = "" + i;
		transaction.proratedDocumentProperties.docSite      = srcInvoice.getString("SITEID");
		
		return transaction;
	}
	static public String FormatDescription(String sDesc, int j)
	{
		if (sDesc == null) return null;
		int i, iLen;
		String sSingle = "", sNewDesc = new String(sDesc);

		if (sDesc.indexOf("'") != -1)
		{
			iLen = sDesc.length();
			for (i = 0; i < iLen; i++)
			{
				sSingle = sDesc.substring(i, i + 1);
				if (sDesc.substring(i, i + 1).equals("'")) sSingle = "''";
				sNewDesc += sSingle;
			}
		}

		if (sNewDesc.length() > j) sNewDesc = sNewDesc.substring(0, j - 3) + "...";

		return sNewDesc;
	}
	
	private void setItemProperties(String itemNum, String itemSetID, String description, TransactionInfo transaction, MboRemote InvoiceLineMbo, String linetype) throws Exception
	{
		
		if (itemNum != null && !itemNum.equals(""))
		{
			MboSetRemote itemSet=null;
			if (linetype.equals("ITEM"))
			itemSet = InvoiceLineMbo.getMboSet("ITEM");
			else if (linetype.equals("STDSERVICE"))
				itemSet = InvoiceLineMbo.getMboSet("SERVICEITEMS");
//			itemSet.setWhere("WHERE ITEMSETID = '" + itemSetID + "' " + "AND ITEMNUM = '" + itemNum + "'");
//			itemSet.reset();
		//	System.out.println("********Inside TransactionHelper setItemProperties() in Journal Transaction Processing :itemSet.getWhere()  >"+itemSet.getWhere()  );
		    
			 
			MboRemote itemMbo;
			if (itemSet!=null && !itemSet.isEmpty())
			{
				   
				itemMbo=itemSet.getMbo(0);
				transaction.itemProperties.itemNumber 		= itemMbo.getString("ITEMNUM");
				transaction.itemProperties.itemSetID		= itemMbo.getString("ITEMSETID");
				transaction.itemProperties.commodityGroup	= itemMbo.getString("COMMODITYGROUP");
				transaction.itemProperties.commodityCode	= itemMbo.getString("COMMODITY");
				transaction.itemProperties.description		= description;
				 
			}
			else
			{
				throw new MXApplicationException("itemNotFoundMax", "invalidItemonTransaction");
			}
		}
		else
		{
			   
			transaction.itemProperties.description		= description;
			
		}
	}
}
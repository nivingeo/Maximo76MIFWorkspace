package custom.app.invoice;

import java.rmi.RemoteException;
import java.util.*;
import psdi.app.common.Cost;
import psdi.app.common.RoundToScale;
import psdi.app.common.receipt.ReceiptMboRemote;
import psdi.app.currency.CurrencyService;
import psdi.app.currency.CurrencyServiceRemote;
import psdi.app.financial.FinancialServiceRemote;
import psdi.app.integration.IntegrationServiceRemote;
import psdi.app.inventory.MatRecTrans;
import psdi.app.inventory.MatRecTransRemote;
import psdi.app.labor.ServRecTrans;
import psdi.app.po.*;
import psdi.app.workorder.WORemote;
import psdi.mbo.*;
import psdi.security.ProfileRemote;
import psdi.security.UserInfo;
import psdi.server.*;
import psdi.util.*;
import psdi.app.invoice.*;
import psdi.app.invoice.InvoiceLineRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class InvoiceExt extends Invoice implements InvoiceRemoteExt
{
	public InvoiceExt(MboSet arg0) throws MXException, RemoteException
	{
		super(arg0);
		i=1;
        myLogger = MXLoggerFactory.getLogger("maximo.application.INVOICE");
	}
	
	public String getStatusListName()
    {
        return "IVSTATUS";
    }

	public StatusHandler getStatusHandler()
	{
		return new InvoiceStatusHandler(this);
	}
	
	 public void init()
	 	throws MXException
     {
		 super.init();		 
     }
	 
	 public void add() throws RemoteException, MXException
	 {
		 super.add();
		 
	 }
	 
	 public void save() throws MXException,RemoteException
	 {
		 super.save();
		 if(getString("vendorinvoicenum").startsWith("ADV-") && getString("vendorinvoicenum").length()==4)
			 throw new MXApplicationException("invoice","wrongVendorInvoiceFormat");
	 }
	

	//commented the below to avoid exception when approving Invoice
	public void approve()
    throws MXException
    {
		System.out.println("InvoiceExt.Approve");
    if(myLogger.isDebugEnabled())
        myLogger.debug("Invoice::approve()");
    try
    {
    	/*
        IntegrationServiceRemote intServ = (IntegrationServiceRemote)((AppService)getMboServer()).getMXServer().lookup("INTEGRATION");
        boolean useIntegration2 = intServ.useIntegrationRules("THISMX", getString("ownersysid"), "IVTOL", getUserInfo());
        if(!useIntegration2)
            try
            {
                if(!getTranslator().toInternalString("INVTYPE", getString("documenttype")).equalsIgnoreCase("CREDIT") && !getTranslator().toInternalString("INVTYPE", getString("documenttype")).equalsIgnoreCase("SCHED") && !getTranslator().toInternalString("INVTYPE", getString("documenttype")).equalsIgnoreCase("LABOR"))
                {
                    checkForServiceLineTolerance();
                    checkForPOLineTolerance();
                    checkForTaxTolerance();
                }
            }
            catch(MXException exc)
            {
                MboSetRemote lines = getMboSet("INVOICELINE");
                lines.reset();
                throw exc;
            }
            */
    	matchTotals();
        checkApprovalLimit();
        writeTransactions();
        MboValue approvalNum = getMboValue("approvalnum");
        long access = approvalNum.getCurrentFieldAccess();
        approvalNum.setReadOnly(false);
        getMboValue("approvalnum").generateUniqueID();
        approvalNum.setCurrentFieldAccess(access);
    }
    catch(MXException e)
    {
        e.printStackTrace();
        throw e;
    }
    catch(Exception e)
    {
        e.printStackTrace();
        throw new MXApplicationException("invoice", "InvoiceApproval");
    }
    }
	
	public void matchTotals()     throws MXException, RemoteException
	{
		double vendorTotal = getDouble("vendortotal");
		double invTotal = getDouble("totalcost");
		Object params[] = {
				getDouble("vendortotal"), getDouble("totalcost")
            };
		
		if((vendorTotal-invTotal)!=0.0D)
			throw new MXApplicationException("invoice", "totalNotMatch", params);
	}
	
	//from above
	private void checkApprovalLimit()
    throws MXException, RemoteException
    {
    double approvalLimit = getProfile().getTolerance("INVOICELIMIT", getString("orgid"));
    SqlFormat sqf = new SqlFormat(getUserInfo(), "orgid = :1");
    sqf.setObject(1, "ORGANIZATION", "ORGID", getString("orgid"));
    MboRemote orgMbo = getMboSet("$orgbasecurrency" + getString("orgid"), "ORGANIZATION", sqf.format()).getMbo(0);
    String profileCurrency = orgMbo.getString("basecurrency1");
    String invCurrency = getString("currencycode");
    if(!invCurrency.equals(profileCurrency))
    {
        CurrencyServiceRemote currServ = (CurrencyServiceRemote)MXServer.getMXServer().lookup("CURRENCY");
        approvalLimit = currServ.calculateCurrencyCost(getUserInfo(), profileCurrency, invCurrency, approvalLimit, MXServer.getMXServer().getDate(), getString("orgid"));
    }
    if(approvalLimit != 0.0D)
    {
        double totalCost = getDouble("totalcost");
        if(totalCost > approvalLimit)
        {
            Object params[] = {
                getString("invoicenum"), new Double(totalCost), new Double(approvalLimit), invCurrency
            };
            throw new MXApplicationException("invoice", "ExceedsApprovalLimit", params);
        }
    }
    }

//from above
	private void writeTransactions()
    throws MXException, RemoteException
    {
    if(myLogger.isDebugEnabled())
        myLogger.debug("Invoice::WriteTransactions() ");
    if(!getTranslator().toInternalString("INVTYPE", getString("documenttype"), this).equals("SCHED") && !getTranslator().toInternalString("INVTYPE", getString("documenttype"), this).equals("LABOR"))
    {
        boolean createRecTrans = true;
        if(!isNull("contractrefnum") && !isNull("schedulenum") && !isNull("duedate"))
            createRecTrans = false;
        if(createRecTrans)
        {
            int i = 0;
            MboSetRemote invoiceLines = getMboSet("INVOICELINE");
            InvoiceLineRemote invoiceLine;
            while((invoiceLine = (InvoiceLineRemote)invoiceLines.getMbo(i)) != null) 
            {
                if(invoiceLine.needProcessVariance())
                    invoiceLine.createReceiptOrTransactionForVariance();
                invoiceLine.createReceipts();
                i++;
            }
        }
    }
    createInvoiceTransForTaxes();
    createInvoiceTransTotal();
    }

 
	 MXLogger myLogger;

	public int i;

}

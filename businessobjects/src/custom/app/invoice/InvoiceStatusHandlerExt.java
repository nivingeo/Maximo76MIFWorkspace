package custom.app.invoice;


import java.rmi.RemoteException;
import java.util.Date;
import psdi.mbo.*;
import psdi.security.ProfileRemote;
import psdi.server.MXServer;
import psdi.util.*;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;
import psdi.app.invoice.Invoice;


public class InvoiceStatusHandlerExt extends StatusHandler
{

	static int convertStatusToInt(String internalStatus)
    {
        if(internalStatus.equalsIgnoreCase("ENTERED"))
            return 0;
        if(internalStatus.equalsIgnoreCase("WAPPR"))
            return 1;
        if(internalStatus.equalsIgnoreCase("HOLD"))
            return 2;
        if(internalStatus.equalsIgnoreCase("APPR"))
            return 3;
        if(internalStatus.equalsIgnoreCase("CANCEL"))
            return 4;
        if(internalStatus.equalsIgnoreCase("PAID"))
            return 5;
        return !internalStatus.equalsIgnoreCase("SCHED") ? -1 : 6;
    }

    public InvoiceStatusHandlerExt(StatefulMbo sm)
    {
        super(sm);
        application = null;
        internalProcess = false;
        needApplication = true;
        myLogger = MXLoggerFactory.getLogger("maximo.application.INVOICE");
        parent = null;
        parent = sm;
    }

    public void checkStatusChangeAuthorization(String desiredStatus)
        throws MXException, RemoteException
    {
        String desiredMaxStatus = parent.getTranslator().toInternalString(parent.getStatusListName(), desiredStatus);
        checkUserSecurity(desiredMaxStatus);
    }

    void checkUserSecurity(String maxstatus)
        throws MXException, RemoteException
    {
        checkUserSecurity(maxstatus, true);
    }

    void checkUserSecurity(String maxstatus, boolean applevel)
        throws MXException, RemoteException
    {
        if(internalProcess)
            return;
        if(getApplication() == null)
            return;
        String optionName = getOptionName(maxstatus);
        if(applevel)
        {
            ProfileRemote p = parent.getMboServer().getProfile(parent.getUserInfo());
            if(optionName == null || !p.getAppOptionAuth(getApplication(), optionName, parent.getString("siteid")))
                throw new MXAccessException("access", "notauthorized");
        } else
        {
            parent.sigOptionAccessAuthorized(optionName);
        }
    }

    public static String getOptionName(String status)
    {
        String optionName = null;
        if(status.equals("ENTERED"))
            optionName = "ENTERED";
        else
        if(status.equals("WAPPR"))
            optionName = "WAITAPPR";
        else
        if(status.equals("APPR"))
            optionName = "APPROVE";
        else
        if(status.equals("CANCEL"))
            optionName = "CANCEL";
        else
        if(status.equals("HOLD"))
            optionName = "HOLD";
        else
        if(status.equals("PAID"))
            optionName = "PAID";
        return optionName;
    }

    private String getApplication()
        throws RemoteException
    {
        if(needApplication)
        {
            application = parent.getThisMboSet().getApp();
            if(application == null || application.trim().equals(""))
            {
                MboRemote owner = parent.getOwner();
                do
                {
                    if(owner == null)
                        break;
                    application = owner.getThisMboSet().getApp();
                    if(application != null && !application.trim().equals(""))
                        break;
                    owner = owner.getOwner();
                } while(true);
            }
            needApplication = false;
        }
        return application;
    }

    public void canChangeStatus(String currentStatus, String desiredStatus, long accessModifier)
        throws MXException, RemoteException
    {
        String currentMaxStatus = parent.getTranslator().toInternalString(parent.getStatusListName(), currentStatus);
        String desiredMaxStatus = parent.getTranslator().toInternalString(parent.getStatusListName(), desiredStatus);
        if(!statusChangeMatrix[convertStatusToInt(currentMaxStatus)][convertStatusToInt(desiredMaxStatus)])
            throw new MXApplicationException("invoice", "InvalidStatusChange");
        if(desiredMaxStatus.equalsIgnoreCase("APPR"))
            canApprove(currentStatus);
        
        if(desiredMaxStatus.equalsIgnoreCase("CANCEL"))
            canCancel();
        if(!parent.toBeAdded())
            checkUserSecurity(desiredMaxStatus, false);
    }

    public void changeStatus(String currentStatus, String desiredStatus, Date date, String memo)
        throws MXException, RemoteException
    {
        if(myLogger.isDebugEnabled())
            myLogger.debug("InvoiceStatusHandler::changeStatus()");
        if(date.getTime() < parent.getDate("statusdate").getTime())
            throw new MXApplicationException("invoice", "StatusDate");
        String internalDesiredStatus = parent.getTranslator().toInternalString(parent.getStatusListName(), desiredStatus);
        if(internalDesiredStatus.equalsIgnoreCase("WAPPR"))
            waitApproval(currentStatus);
        else
        if(internalDesiredStatus.equalsIgnoreCase("APPR"))
            approve(currentStatus);
        else
        if(internalDesiredStatus.equalsIgnoreCase("HOLD"))
            hold(currentStatus);
        else
        if(internalDesiredStatus.equalsIgnoreCase("CANCEL"))
            cancel(currentStatus);
        else
        if(internalDesiredStatus.equalsIgnoreCase("PAID"))
            paid(currentStatus);
        parent.setValue("status", desiredStatus, 2L);
        parent.setValue("statusdate", date, 2L);
    }

    void waitApproval(String s)
    {
    }

    void approve(String status)
        throws MXException, RemoteException
    {
        if(myLogger.isDebugEnabled())
            myLogger.debug("InvoiceStatusHandler::approve()");
        ((Invoice)parent).validateForApproval();
        String fields[] = {
            "checknum", "checkcode", "paiddate", "paid"
        };
        parent.setFieldFlag(fields, false, 7L, true);
        ((InvoiceExt)parent).approve();
        parent.setValue("historyflag", true, 2L);
    }

    void hold(String s)
    {
    }

    void cancel(String status)
        throws MXException, RemoteException
    {
        hold(status);
        parent.setValue("historyflag", true, 2L);
        MboSetRemote invoiceMatchSet = parent.getMboSet("INVOICEMATCH");
        if(!invoiceMatchSet.isEmpty())
            invoiceMatchSet.deleteAll(2L);
        
        //is this Advance Invoice
        boolean advance = parent.getBoolean("advance");
        if(advance==true)
        {
        	//when canceling advance invoice the advance flag of PO should be made false and POADV table should be deleted
        	MboRemote po = parent.getMboSet("PO").getMbo(0);
        	po.setValue("advancepayment", false,2L);
        	po.setValue("advpercent", 0.0D,2L);
        	
        	MboSetRemote poAdvSet = parent.getMboSet("POADV");
        	poAdvSet.deleteAll(2L);
        }
        parent.setFlag(7L, true);
    }

    void paid(String status)
        throws MXException, RemoteException
    {
        String internalCurStatus = parent.getTranslator().toInternalString(parent.getStatusListName(), status);
        if(internalCurStatus.equalsIgnoreCase("ENTERED") || internalCurStatus.equalsIgnoreCase("WAPPR") || internalCurStatus.equalsIgnoreCase("HOLD"))
            approve(status);
        parent.setValue("paiddate", MXServer.getMXServer().getDate(), 2L);
        parent.setFieldFlag("paiddate", 7L, true);
        parent.setFieldFlag("checknum", 7L, true);
        parent.setFieldFlag("checkcode", 7L, true);
    }
    
    void canApprove(String currStatus)
    throws MXException, RemoteException
    {
    	//if(!currStatus.equalsIgnoreCase("MATCHED"))
    	//	throw new MXApplicationException("custom", "shouldBeMatched");
    }

    void canCancel()
        throws MXException, RemoteException
    {
        if(parent.getTranslator().toInternalString("INVTYPE", parent.getString("documenttype")).equals("SCHED") && !parent.getString("ponum").equals(""))
        {
            MboRemote po = parent.getMboSet("PO").getMbo(0);
            if(((Mbo)po).getTranslator().toInternalString("POSTATUS", po.getString("status")).equals("APPR"))
                throw new MXApplicationException("invoice", "CanCancelForSchedulePO");
        }
    }

    private String application;
    private boolean internalProcess;
    private boolean needApplication;
    static final int ENTERED = 0;
    static final int WAPPR = 1;
    static final int HOLD = 2;
    static final int APPR = 3;
    static final int CANCEL = 4;
    static final int PAID = 5;
    static final int SCHED = 6;
    MXLogger myLogger;
    static boolean statusChangeMatrix[][] = {
        {
            false, true, true, true, true, true, false
        }, {
            false, false, true, true, true, true, false
        }, {
            false, true, false, true, true, true, false
        }, {
            false, false, false, false, false, true, false
        }, {
            false, false, false, false, false, false, false
        }, {
            false, false, false, false, false, false, false
        }, {
            false, true, true, true, true, true, false
        }
    };
    StatefulMbo parent;

	
}

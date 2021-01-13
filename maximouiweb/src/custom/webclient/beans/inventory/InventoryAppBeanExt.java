package custom.webclient.beans.inventory;

import java.io.IOException;
import java.rmi.RemoteException;
import javax.servlet.ServletException;
import psdi.app.inventory.*;
import psdi.app.serviceitem.ServiceItemSetRemote;
import psdi.mbo.*;
import psdi.security.ProfileRemote;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.util.*;
import psdi.webclient.system.beans.*;
import psdi.webclient.system.controller.*;
import custom.app.inventory.InventoryExtRemote;


public class InventoryAppBeanExt extends AppBean {

	public InventoryAppBeanExt() {
		// TODO Auto-generated constructor stub
	}
	
	public void initialize()
    throws MXException, RemoteException
{
    try
    {
        super.initialize();
        MboSetRemote itemSet = super.getQuickFindRemote();
        if(!(itemSet instanceof ServiceItemSetRemote))
            itemSet.setRelationship("exists (select 1 from item where itemnum = inventory.itemnum and itemtype in (select value from synonymdomain where domainid='ITEMTYPE' and maxvalue = 'ITEM'))");
        else
            itemSet.setRelationship("itemnum in (select itemnum from item where itemtype in (select value from synonymdomain where domainid='ITEMTYPE' and maxvalue = 'STDSERVICE'))");
    }
    catch(MXException e)
    {
        Utility.showMessageBox(sessionContext.getCurrentEvent(), e);
    }
}

public void save()
    throws MXException
{
    DataBean whereUsedBean = app.getDataBean("whereused_item_table");
    if(whereUsedBean != null)
        whereUsedBean.save();
    super.save();
}

public int RELLOCKS()
    throws RemoteException, IOException, ServletException
{
    WebClientEvent event = sessionContext.getCurrentEvent();
    try
    {
        MXSession mxs = sessionContext.getMXSession();
        UserInfo userInfo = mxs.getUserInfo();
        ReorderServiceRemote invService = (ReorderServiceRemote)mxs.lookup("REORDER");
        String siteID = null;
        if(parent.getMbo() == null)
            siteID = ((Mbo)parent.getZombie()).getProfile().getDefaultSite();
        else
            siteID = parent.getMbo().getString("siteid");
        if(siteID.equalsIgnoreCase("") || siteID == null)
            siteID = ((Mbo)parent.getMbo()).getProfile().getDefaultSite();
        invService.releaseLocks(userInfo, siteID);
        String username = userInfo.getUserName();
        Utility.showMessageBox(event, "inventory", "reorderreleaselocks", new String[] {
            username
        });
    }
    catch(MXException e)
    {
        Utility.showMessageBox(event, e);
    }
    return 1;
}

public int RELLOCKSNODIALOG()
    throws RemoteException, IOException, ServletException
{
    WebClientEvent event = sessionContext.getCurrentEvent();
    try
    {
        MXSession mxs = sessionContext.getMXSession();
        UserInfo userInfo = mxs.getUserInfo();
        ReorderServiceRemote invService = (ReorderServiceRemote)mxs.lookup("REORDER");
        invService.releaseLocks(userInfo, parent.getMbo().getString("siteid"));
    }
    catch(MXException e)
    {
        Utility.showMessageBox(event, e);
    }
    return 1;
}

public int PHYSCNTADJ()
    throws RemoteException, IOException, ServletException
{
    WebClientEvent event = sessionContext.getCurrentEvent();
    int retValue = 2;
    MXException mxe = canDoAction();
    if(mxe != null)
    {
        Utility.showMessageBox(event, mxe);
        retValue = 0;
    }
    return retValue;
}

public int KITMAKE()
    throws RemoteException, IOException, ServletException
{
    return kitPrepare();
}

public int KITBREAK()
    throws RemoteException, IOException, ServletException
{
    return kitPrepare();
}

private int kitPrepare()
    throws RemoteException, IOException, ServletException
{
    int retValue = 2;
    try
    {
        save();
        InventoryRemote inventory = (InventoryRemote)getMbo();
        inventory.canKit();
        inventory.setKitAction(sessionContext.getCurrentEvent().getType());
    }
    catch(MXException mxe)
    {
        Utility.showMessageBox(sessionContext.getCurrentEvent(), mxe);
        retValue = 0;
    }
    return retValue;
}

public int ISSUE()
    throws RemoteException, IOException
{
    WebClientEvent event = sessionContext.getCurrentEvent();
    int retValue;
    try
    {
        InventoryRemote msbset = (InventoryRemote)getMbo();
        msbset.canDoAction();
        msbset.canIssueCurrentItem();
        retValue = 2;
    }
    catch(MXException e)
    {
        Utility.showMessageBox(event, e);
        retValue = 0;
    }
    return retValue;
}

public int ISSUEMULTI()
    throws RemoteException, IOException
{
    WebClientEvent event = sessionContext.getCurrentEvent();
    int retValue;
    try
    {
        InventoryRemote msbset = (InventoryRemote)getMbo();
        msbset.canDoAction();
        msbset.canIssueCurrentItem();
        retValue = 2;
    }
    catch(MXException e)
    {
        Utility.showMessageBox(event, e);
        retValue = 0;
    }
    return retValue;
}

public int TRANSFER()
    throws RemoteException, IOException, ServletException
{
    WebClientEvent event = sessionContext.getCurrentEvent();
    int retValue;
    try
    {
        InventoryRemote msbset = (InventoryRemote)getMbo();
        msbset.canDoAction();
        msbset.canTransferCurrentItem();
        retValue = 2;
    }
    catch(MXException e)
    {
        Utility.showMessageBox(event, e);
        retValue = 0;
    }
    return retValue;
}

public int RECONCILE()
    throws RemoteException, IOException, ServletException
{
    WebClientEvent event = sessionContext.getCurrentEvent();
    int retValue = 2;
    MXException mxe = canDoAction();
    if(mxe != null)
    {
        Utility.showMessageBox(event, mxe);
        retValue = 0;
    }
    if(retValue != 0)
        if(!app.onListTab())
            retValue = 2;
        else
            try
            {
                if(!sessionContext.haslongOpStarted())
                    checkESigAuthenticated("RECONCILE");
                if(sessionContext.runLongOp(this, "RECONCILE"))
                {
                    InventorySetRemote inventorySet = (InventorySetRemote)app.getResultsBean().getMboSet();
                    inventorySet.reconcileSet();
                    inventorySet.totalReconcileErrors();
                    inventorySet.save();
                    app.getResultsBean().reset();
                }
                if(sessionContext.haslongOpCompleted())
                    sessionContext.queueRefreshEvent();
                retValue = 1;
            }
            catch(MXException e)
            {
                Utility.showMessageBox(event, e);
                retValue = 0;
            }
    return retValue;
}

public int CURBALADJ()
    throws RemoteException, IOException, ServletException
{
    WebClientEvent event = sessionContext.getCurrentEvent();
    int retValue = 2;
    MXException mxe = canDoAction();
    if(mxe != null)
    {
        Utility.showMessageBox(event, mxe);
        retValue = 0;
    }
    if(retValue != 0)
        try
        {
            InventoryRemote msbset = (InventoryRemote)getMbo();
            msbset.canAdjustBalance();
            retValue = 2;
        }
        catch(MXException e)
        {
            Utility.showMessageBox(event, e);
            retValue = 0;
        }
    return retValue;
}

public int AVGCOSTADJ()
    throws RemoteException, IOException, ServletException
{
    WebClientEvent event = sessionContext.getCurrentEvent();
    int retValue = 2;
    MXException mxe = canDoAction();
    if(mxe != null)
    {
        Utility.showMessageBox(event, mxe);
        retValue = 0;
    }
    return retValue;
}

public int STDCOSTADJ()
    throws RemoteException, IOException, ServletException
{
    WebClientEvent event = sessionContext.getCurrentEvent();
    int retValue = 2;
    MXException mxe = canDoAction();
    if(mxe != null)
    {
        Utility.showMessageBox(event, mxe);
        retValue = 0;
    }
    return retValue;
}

public int TAXCODES()
    throws RemoteException, IOException, MXApplicationException, MXException
{
    if(getMbo().toBeAdded())
        throw new MXApplicationException("inventory", "savecontinue");
    else
        return 2;
}

public int STATUS()
    throws RemoteException, MXApplicationException, MXException
{
    Translate translate = MXServer.getMXServer().getMaximoDD().getTranslator();
    String status = translate.toInternalString("ITEMSTATUS", getString("status"));
    if(status.equals("OBSOLETE"))
        throw new MXApplicationException("jspmessages", "closestatus");
    else
        return 2;
}

private MXException canDoAction()
    throws RemoteException, IOException, ServletException
{
    MXException retValue = null;
    try
    {
        InventoryRemote msbset = (InventoryRemote)getMbo();
        msbset.canDoAction();
        retValue = null;
    }
    catch(MXException e)
    {
        retValue = e;
    }
    return retValue;
}

public void initializeApp()
    throws MXException, RemoteException
{
	/*
    if(!app.inAppLinkMode())
    {
        DataBean resultsBean = app.getResultsBean();
        Translate translate = MXServer.getMXServer().getMaximoDD().getTranslator();
        String status = (new StringBuilder()).append("!=").append(translate.toExternalDefaultValue("ITEMSTATUS", "OBSOLETE", null, null)).toString();
        resultsBean.setQbe("status", status);
        resultsBean.reset();
    }
    */
    super.initializeApp();
}

public synchronized MboRemote getMbo()
    throws MXException, RemoteException
{
    MboRemote mboRemote = super.getMbo();
    if(fromReorder && mboRemote == null && app.onListTab())
    {
        String siteID = mboSetRemote.getMbo(0).getInsertSite();
        int i = 0;
        do
        {
            mboRemote = mboSetRemote.getMbo(i);
            if(mboRemote.getString("siteid").equalsIgnoreCase(siteID))
                break;
            i++;
        } while(true);
    }
    if(fromReorder)
        fromReorder = false;
    return mboRemote;
}

	public int ITEMOBS()
	throws RemoteException, IOException, ServletException
	{
		DataBean databean = Utility.getDataSource(sessionContext, app.getAppHandler());
		byte byte0 = 2;
		try
		{
			save();
			InventoryExtRemote itemremoteext = (InventoryExtRemote)getMbo();
			itemremoteext.changeItemStatus();
			databean.save();
		}
		catch(MXException mxexception)
		{
			Utility.showMessageBox(sessionContext.getCurrentEvent(), mxexception);
			byte0 = 0;
		}
			sessionContext.queueRefreshEvent();
			return byte0;
		}


public boolean fromReorder;



}

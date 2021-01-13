package custom.webclient.beans.invissue;

import java.rmi.RemoteException;

import psdi.util.MXException;
import psdi.webclient.beans.invissue.InvissueAppBean;
import psdi.webclient.system.controller.WebClientEvent;

public class InvIssueAppBeanExt extends InvissueAppBean {

	public InvIssueAppBeanExt() {
		// TODO Auto-generated constructor stub
	}

	public int MATUSE() throws MXException, RemoteException
	{
		clientSession.queueEvent(new WebClientEvent("changeapp", app.getId(), "MATUSE", null, clientSession));
        return 1;
	}
}

// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 1/11/2010 10:51:36 AM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) deadcode safe 
// Source File Name:   Helper.java

package custom.general;

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;
import psdi.mbo.*;
import psdi.util.MXException;
import psdi.util.MXSession;

public class Helper
{

    public Helper()
    {
    }

    public static Date addDays(Date d1, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d1);
        cal.add(5, days);
        Date date1 = cal.getTime();
        return date1;
    }

    public static Date addYears(Date d1, int years)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d1);
        cal.add(1, years);
        Date date1 = cal.getTime();
        return date1;
    }

    public static String daysOfWeek(int i)
    {
        String days[] = {
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Workingday", "Holiday"
        };
        return days[i];
    }

    public static String getRecordkey(String tb)
        throws RemoteException, MXException
    {
        String recordkey = "";
        if(tb.equals("WORKORDER"))
            recordkey = "WONUM";
        else
        if(tb.equals("PR"))
            recordkey = "PRNUM";
        else
        if(tb.equals("PO"))
            recordkey = "PONUM";
        else
        if(tb.equals("RFQ"))
            recordkey = "RFQNUM";
        else
        if(tb.equals("MR"))
            recordkey = "MRNUM";
        else
        if(tb.equals("PURCHVIEW"))
            recordkey = "CONTRACTNUM";
        else
        if(tb.equals("INVOICE"))
            recordkey = "INVOICENUM";
        else
        	if(tb.equals("MOVMORDER"))
                recordkey = "MONUM";
        return recordkey;
    }

	public static void writeSqlToText(String text)
	{
		System.out.println("writing");
		BufferedWriter bw=null;
		try
		{
			bw = new BufferedWriter(new FileWriter("c:\\MaximoOutput.txt",true));
			bw.write("---------------------------------------");
			bw.newLine();
			bw.write(text);
			bw.newLine();
			bw.write("---------------------------------------");
			bw.newLine();
			bw.flush();
		}
		catch(Throwable throwable)
        {
            throwable.printStackTrace();
        }finally
        {
        	if(bw!=null) try{
        		bw.close();
        	}catch(IOException io){
        		
        	}
        }


	}
    public static String getUserGroup(String user, MXSession mx)
        throws RemoteException, MXException
    {
        String group = "";
        MboSetRemote GroupSet = mx.getMboSet("GROUPUSER");
        GroupSet.setWhere("userid = '" + user + "'");
        group = GroupSet.getMbo(0).getString("groupname");
        return group;
    }

    public static String[] getAllAttributes(MboSetRemote mbosetremote)
        throws RemoteException, MXException
    {
        int i = 0;
        int size = 0;
        MboSetInfo mboSetInfo = mbosetremote.getMboSetInfo();
        Iterator it = mboSetInfo.getAttributes();
        String attr[] = new String[250];
        do
        {
            if(!it.hasNext())
                break;
            MboValueInfo mi = (MboValueInfo)it.next();
            if(mi.isPersistent())
            {
                String attrUpper = mi.getAttributeName();
                attr[i] = attrUpper;
                i++;
            }
        } while(true);
        String attr1[] = new String[i];
        for(int j = 0; j < i; j++)
            attr1[j] = attr[j];

        return attr1;
    }
}
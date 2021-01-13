// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 1/11/2010 10:54:30 AM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) deadcode safe 
// Source File Name:   RRStatusTypes.java

package custom.general.types;


public class RRStatusTypes
{

    public RRStatusTypes()
    {
    }

    public static int convertStatusToInt(String internalStatus)
    {
    	if(internalStatus.equalsIgnoreCase("DRAFT"))
            return 0;
        if(internalStatus.equalsIgnoreCase("SUBMIT"))
            return 1;
        if(internalStatus.equalsIgnoreCase("PROCESSED"))
            return 2;
        if(internalStatus.equalsIgnoreCase("PAID"))
            return 3;
        return !internalStatus.equalsIgnoreCase("CAN") ? -1 : 4;
    }

    public static final String SUBMIT = "SUBMIT";
    public static final String DRAFT = "DRAFT";
    public static final String PROCESSED = "PROCESSED";
    public static final String CAN = "CAN";
    public static final String PAID = "PAID";
    public static final int draft = 0;
    public static final int submit = 1;
    public static final int processed = 2;
    public static final int paid = 3;
    public static final int can = 4;
    public static boolean statusChangeMatrix[][] = {
        {
            false, true, false, true, true
        }, {
            false, false, true, false, true
        }, {
            false, false, false, true, false
        }, new boolean[5], new boolean[5]
    };

}
package custom.general.types;

public class SOStatusTypes
{
	public static final String ENTERED     = "ENTERED";
	public static final String APPR  = "APPR";
	public static final String PAID    = "PAID";
	public static final String CAN    = "CAN";
	public static final String CLOSE    = "CLOSE";
	public static final String RETURN    = "RETURN";

	
	
	
	public static final int entered     = 0;
    public static final int appr  = 1;
    public static final int paid  = 2;
    public static final int can    = 3;
    public static final int close    = 4;
    public static final int ret    = 5;
    
   /* public static boolean statusChangeMatrix[][] = {
        {
        	false,true,false,true,false,true
        }, {
        	false,false,true,true,false,false
        },{
        	false,false,false,false,true,true
        },{
        	false,false,false,false,false,false
        }, {
        	false,false,false,false,false,false
        },  {
        	false,false,false,false,false,false
        }
    };*/
    
    public static boolean statusChangeMatrix[][] = {
        {
        	false, true, false, true, false, false
        }, {
        	false, false, true, true, true, false
        },{
        	false, false, false, false,true,false
        },{
        	false, false, false, false,false,false
        }, {
            false, false, false, false,false,false
        },  {
            false, false, false, false,false,false
        }
    };

    
    public static int convertStatusToInt(String internalStatus)
    {
        if(internalStatus.equalsIgnoreCase(ENTERED    )) { return entered;     }
        if(internalStatus.equalsIgnoreCase(APPR )) { return appr;  }
        if(internalStatus.equalsIgnoreCase(PAID   )) { return paid;    }
        if(internalStatus.equalsIgnoreCase(CAN   )) { return can;    }
        if(internalStatus.equalsIgnoreCase(CLOSE   )) { return close;    }
        if(internalStatus.equalsIgnoreCase(RETURN   )) { return ret;    }
 
        return  -1;
    }
}

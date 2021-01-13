package ix.handlers.custom.koc.trans.util;

//Praxis - Rojitha - Util class for Journal Processing

import java.util.HashMap;

import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class JeDffHelper
{
	private static HashMap<String, String> map;
	
	private static void init()
	{		
		map = new HashMap<String, String>();
		
		map.put("PR_COMMITMENT_JESOURCE",    	"MAXIMO");
		map.put("PR_COMMITMENT_JECATEGORY",  	"MX_COMMITMENT");
		map.put("PR_COMMITMENT_DFFCATEGORY", 	"Commitment");
		
		map.put("PO_COMMITMENT_JESOURCE", 	 	"MAXIMO");
		map.put("PO_COMMITMENT_JECATEGORY",  	"MX_COMMITMENT");
		map.put("PO_COMMITMENT_DFFCATEGORY", 	"Commitment");
		
		map.put("NCSO_COMMITMENT_JESOURCE", 	"MAXIMO");
		map.put("NCSO_COMMITMENT_JECATEGORY", 	"MX_COMMITMENT");
		map.put("NCSO_COMMITMENT_DFFCATEGORY", 	"Commitment");
			
		map.put("SO_COMMITMENT_JESOURCE", 	 	"MAXIMO");
		map.put("SO_COMMITMENT_JECATEGORY",  	"MX_COMMITMENT");
		map.put("SO_COMMITMENT_DFFCATEGORY", 	"Commitment");
		
		map.put("TPIA_COMMITMENT_JESOURCE", 	"MAXIMO");
		map.put("TPIA_COMMITMENT_JECATEGORY", 	"MX_COMMITMENT");
		map.put("TPIA_COMMITMENT_DFFCATEGORY", 	"Commitment");
		
		map.put("MR_COMMITMENT_JESOURCE", 		"MAXIMO");
		map.put("MR_COMMITMENT_JECATEGORY", 	"MX_COMMITMENT");
		map.put("MR_COMMITMENT_DFFCATEGORY", 	"Commitment");
		
		map.put("WORKORDER_COMMITMENT_JESOURCE", 	"MAXIMO");
		map.put("WORKORDER_COMMITMENT_JECATEGORY", 	"MX_COMMITMENT");
		map.put("WORKORDER_COMMITMENT_DFFCATEGORY", "Commitment");
	
		map.put("CFA_COMMITMENT_JESOURCE", 			"MAXIMO");
		map.put("CFA_COMMITMENT_JECATEGORY", 		"MX_COMMITMENT");
		map.put("CFA_COMMITMENT_DFFCATEGORY", 		"Commitment");
		
		map.put("MATRECTRANS_RECEIPT_JESOURCE", 	"MAXIMO");
		map.put("MATRECTRANS_RECEIPT_JECATEGORY", 	"MX_RECEIPT");
		map.put("MATRECTRANS_RECEIPT_DFFCATEGORY", 	"Material Acceptance");
	
		map.put("MATRECTRANS_RETURN_JESOURCE", 		"MAXIMO");
		map.put("MATRECTRANS_RETURN_JECATEGORY", 	"MX_RETURN_TO_VENDOR");
		map.put("MATRECTRANS_RETURN_DFFCATEGORY", 	"Material Acceptance");
		
		map.put("MATRECTRANS_TRANSFERWRON_JESOURCE", 	"MAXIMO");
		map.put("MATRECTRANS_TRANSFERWRON_JECATEGORY", 	"MX_TRANSFER");
		map.put("MATRECTRANS_TRANSFERWRON_DFFCATEGORY", "Write On");
		
		map.put("MATRECTRANS_TRANSFERWROFF_JESOURCE", 	"MAXIMO");
		map.put("MATRECTRANS_TRANSFERWROFF_JECATEGORY", "MX_TRANSFER");
		map.put("MATRECTRANS_TRANSFERWROFF_DFFCATEGORY","Write Off");
		
		map.put("MATRECTRANS_CURRVAR_JESOURCE", 		"MAXIMO");
		map.put("MATRECTRANS_CURRVAR_JECATEGORY", 		"MX_AVCSTADJ");
		map.put("MATRECTRANS_CURRVAR_DFFCATEGORY", 		"Cost Adjustment");
		
		map.put("MATRECTRANS_PRORATING_JESOURCE", 	 	"MAXIMO");
		map.put("MATRECTRANS_PRORATING_JECATEGORY",  	"MX_AVCSTADJ");
		map.put("MATRECTRANS_PRORATING_DFFCATEGORY", 	"Cost Adjustment");
		
		map.put("MATRECTRANS_INVOICE_JESOURCE", 	"MAXIMO");
		map.put("MATRECTRANS_INVOICE_JECATEGORY", 	"MX_AVGCSTADJ");
		map.put("MATRECTRANS_INVOICE_DFFCATEGORY", 	"Cost Adjustment");
		
		map.put("MATRECTRANS_TRANSFER_JESOURCE", 	"MAXIMO");
		map.put("MATRECTRANS_TRANSFER_JECATEGORY", 	"MX_TRANSFER");
		map.put("MATRECTRANS_TRANSFER_DFFCATEGORY", "Transfer");
					
		map.put("MATUSETRANS_ISSUE_JESOURCE", 		"MAXIMO");
		map.put("MATUSETRANS_ISSUE_JECATEGORY", 	"MX_ISSUE");
		map.put("MATUSETRANS_ISSUE_DFFCATEGORY", 	"Material Issue");
		
		map.put("MATUSETRANS_RETURN_JESOURCE", 		"MAXIMO");
		map.put("MATUSETRANS_RETURN_JECATEGORY", 	"MX_RETURN");
		map.put("MATUSETRANS_RETURN_DFFCATEGORY", 	"Material Return");
		
		map.put("SERVRECTRANS_RECEIPT_JESOURCE", 	"MAXIMO");
		map.put("SERVRECTRANS_RECEIPT_JECATEGORY", 	"MX_SERVICE_RECEIPT");
		map.put("SERVRECTRANS_RECEIPT_DFFCATEGORY", "Service Receipt");
		
		map.put("SERVRECTRANS_RETURN_JESOURCE", 	"MAXIMO");
		map.put("SERVRECTRANS_RETURN_JECATEGORY", 	"MX_SERVICE_RECEIPT");
		map.put("SERVRECTRANS_RETURN_DFFCATEGORY", 	"Service Receipt");
		
		map.put("SERVRECTRANS_ACCRUAL_JESOURCE", 	"MAXIMO");
		map.put("SERVRECTRANS_ACCRUAL_JECATEGORY", 	"MX_ACCRUAL");
		map.put("SERVRECTRANS_ACCRUAL_DFFCATEGORY", "Service Receipt");
		
		map.put("INVTRANS_AVGCSTADJ_JESOURCE", 		"MAXIMO");
		map.put("INVTRANS_AVGCSTADJ_JECATEGORY", 	"MX_AVGCSTADJ");
		map.put("INVTRANS_AVGCSTADJ_DFFCATEGORY", 	"AUP Adjustment");
	
		map.put("INVTRANS_RECBALADJ_JESOURCE", 		"MAXIMO");
		map.put("INVTRANS_RECBALADJ_JECATEGORY", 	"MX_CURBALADJ");
		map.put("INVTRANS_RECBALADJ_DFFCATEGORY", 	"Cycle Count");
		
		map.put("INVTRANS_CURBALADJ_JESOURCE", 		"MAXIMO");
		map.put("INVTRANS_CURBALADJ_JECATEGORY", 	"MX_CURBALADJ");
		map.put("INVTRANS_CURBALADJ_DFFCATEGORY", 	"Cycle Count");
		
		map.put("INVOICE_ADJUSTMENT_PURCHASE_JESOURCE", 	"MAXIMO");
		map.put("INVOICE_ADJUSTMENT_PURCHASE_JECATEGORY", 	"MX_ADJ_INVOICE_PUR");
		map.put("INVOICE_ADJUSTMENT_PURCHASE_DFFCATEGORY", 	"Cost Adjustment");
		
		map.put("INVOICE_ADJUSTMENT_CONTRACT_JESOURCE", 	"MAXIMO");
		map.put("INVOICE_ADJUSTMENT_CONTRACT_JECATEGORY", 	"MX_ADJ_INVOICE_CON");
		map.put("INVOICE_ADJUSTMENT_CONTRACT_DFFCATEGORY", 	"Cost Adjustment");
		
		map.put("INVOICE_ADJUSTMENT_SUNDRY_JESOURCE", 	 	"MAXIMO");
		map.put("INVOICE_ADJUSTMENT_SUNDRY_JECATEGORY",  	"MX_ADJ_INVOICE_SUN");
		map.put("INVOICE_ADJUSTMENT_SUNDRY_DFFCATEGORY", 	"Cost Adjustment");
		
		//Nivin - Sundry Accruals
		map.put("INVOICE_SA_JESOURCE", 	 	"MAXIMO");
		map.put("INVOICE_SA_JECATEGORY",  	"MX_SUNDRYACCRUAL");
		map.put("INVOICE_SA_DFFCATEGORY", 	"Sundry Accruals");

		map.put("INVOICE_PC_MX_CASH_JESOURCE", 	 	"MAXIMO");
		map.put("INVOICE_PC_MX_IMPR_JESOURCE", 	 	"MAXIMO");
		map.put("INVOICE_PC_MX_CASH_JECATEGORY",  	"MX_CASH");
		map.put("INVOICE_PC_MX_IMPR_JECATEGORY",  	"MX_IMPR");
		map.put("INVOICE_PC_MX_CASH_DFFCATEGORY", 	"Petty Cash");
		map.put("INVOICE_PC_MX_IMPR_DFFCATEGORY", 	"Petty Cash");
		
		map.put("INVOICE_ADJUSTMENT_CA_JESOURCE", 	 	"MAXIMO");
		map.put("INVOICE_ADJUSTMENT_CA_JECATEGORY",  	"MX_ADJ_INVOICE_CA");
		map.put("INVOICE_ADJUSTMENT_CA_DFFCATEGORY", 	"Cost Adjustment");

		map.put("INVOICETRANS_CURVAR_JESOURCE", 	"MAXIMO");
		map.put("INVOICETRANS_CURVAR_JECATEGORY", 	"MX_AVGCSTADJ");
		map.put("INVOICETRANS_CURVAR_DFFCATEGORY", 	"AUP Adjustment");
		
		map.put("INVOICETRANS_INVCEVAR_JESOURCE", 	 "MAXIMO");
		map.put("INVOICETRANS_INVCEVAR_JECATEGORY",  "MX_AVGCSTADJ");
		map.put("INVOICETRANS_INVCEVAR_DFFCATEGORY", "AUP Adjustment");
		
		map.put("INVOICE_PURCHASE_DFFCATEGORY", 	"PO");
		map.put("INVOICE_CONTRACT_DFFCATEGORY", 	"Contract");
		map.put("INVOICE_SUNDRY_DFFCATEGORY", 		"Sundries");
		
		map.put("INVOICELINE_PURCHASE_DFFCATEGORY", "PO");
		map.put("INVOICELINE_CONTRACT_DFFCATEGORY", "Contract");
		map.put("INVOICELINE_SUNDRY_DFFCATEGORY", 	"Sundries");
		
		map.put("INVOICE_SP_DFFCATEGORY", 	"Sundry Payments");
		map.put("INVOICE_MSP_DFFCATEGORY", 	"Medical Sundry Payments");
		
		map.put("INVOICELINE_SP_DFFCATEGORY", 	"Sundry Payments");
		map.put("INVOICELINE_MSP_DFFCATEGORY", 	"Medical Sundry Payments");
		
		map.put("INVOICELINE_Courses_DFFCATEGORY", 	"Course and Duty");
		map.put("INVOICELINE_Duties_DFFCATEGORY", 	"Course and Duty");
		map.put("INVOICELINE_Holiday_DFFCATEGORY", 	"Misc Trip");
		map.put("INVOICELINE_Medical_DFFCATEGORY", 	"Referral");
	}
	
	public static String getValueFor(String key) throws MXException 
	{
		if (map == null) { init(); }
		
		if (map.containsKey(key)) { return map.get(key); }
		
		if (key.endsWith("JESOURCE")) 
		{
			throw new MXApplicationException("JESOURCENOTDEFINED", key);
		}
		else if (key.endsWith("JECATEGORY"))  
		{
			throw new MXApplicationException("JECategoryNotDefined", key);
		}
		else if (key.endsWith("DFFCATEGORY")) 
		{
			throw new MXApplicationException("DFFCategoryNotDefined", key);
		}

		throw new MXApplicationException("GeneralError",key);
	}
}
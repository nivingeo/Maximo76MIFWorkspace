package ix.handlers.custom.koc.trans.util;

import ix.handlers.custom.koc.trans.util.Field;

//Praxis - Rojitha - Util class for Journal Processing
public interface Field
{
	public enum InvoiceFields implements Field
	{
		PoNum 					 ("ATTRIBUTE1"),
		PoType 			 	 	 ("ATTRIBUTE3"),
		ContractNum 			 ("ATTRIBUTE2"),
		ContractControllingTeam  ("ATTRIBUTE4"),
		InvoiceType 			 ("ATTRIBUTE5"),
		ApController 	 		 ("ATTRIBUTE6"), // entered by
		ApApprover 	 		 	 ("ATTRIBUTE7"), // approved by
		ApprovedDate 	 		 ("ATTRIBUTE8"),
		WorkCategory 	 		 ("ATTRIBUTE9"),
		ContractTitle 	 		 ("ATTRIBUTE10"),
		ModeOfShippment 		 ("ATTRIBUTE11"),
		MatchLevel 	 		     ("ATTRIBUTE12"),
		InvoiceNum 	 		 	 ("ATTRIBUTE13"),
		InvoiceSiteID 	 		 ("ATTRIBUTE14"),
		Application 	 		 ("ATTRIBUTE15");
		
		private String attribute;
		private InvoiceFields(String attribute)
		{
			this.attribute = attribute;
		}
		
		public String toString()
		{
			return attribute;
		}
	}

	public enum InvoiceLineFields implements Field
	{
		InvoiceLineNum 			  	("ATTRIBUTE1"),
		ReceiptId 					("ATTRIBUTE2"),
		ReceiptAcceptanceDate 		("ATTRIBUTE3"),
		ItemNumber 				  	("ATTRIBUTE4"),
//		lineDescription 			= null,//new DFFName("ATTRIBUTE4");
//		workOrderNumber 			= null,//"ATTRIBUTE6");
		ProjectNumber 				("ATTRIBUTE5"),
		TaskNumber 				  	("ATTRIBUTE6"),
		CostCenter 				  	("ATTRIBUTE7"),
		ExpenditureType 			("ATTRIBUTE8"),
		InvoiceCostId 				("ATTRIBUTE9"),
		CommodityCodeAndDescription ("ATTRIBUTE10"),
		CommodityGroupAndDescription("ATTRIBUTE11"),
		PoSiteID 					("ATTRIBUTE15"),
		PoNumber 					("ATTRIBUTE12"),
		PoType 					    ("ATTRIBUTE13"),
		PoLineNumber 				("ATTRIBUTE14"),
//		invoiceLineType 		     = null,//new DFFName("ATTRIBUTE15");
		
		KCompany 					("ATTRIBUTE3"),
		FinancialYear 				("ATTRIBUTE4"),
		TripType 					("ATTRIBUTE4"),
		TripNumber 				    ("ATTRIBUTE10"),
		EmployeeNumber 			    ("ATTRIBUTE11"),
		MedicalFileNumber 			("ATTRIBUTE13"),
		RelationshipToEmployee 	    ("ATTRIBUTE15");
		
		private String attribute;
		private InvoiceLineFields(String attribute)
		{
			this.attribute = attribute;
		}
		
		public String toString()
		{
			return attribute;
		}
	}
	
	public enum GLInterfaceFields implements Field
	{
		DocumentHeaderNum		( "ATTRIBUTE1"),
		DocumentType			( "ATTRIBUTE2"),
		DocumentLineNum			( "ATTRIBUTE3"),
		MaximoTransactionNumber	( "ATTRIBUTE4"),
		CommodityCode			( "ATTRIBUTE5"),
		ItemNumber				( "ATTRIBUTE6"),
		TransactionDescription	( "ATTRIBUTE7"),
		TransactionType			( "ATTRIBUTE8"),

		ProjectNumber			( "ATTRIBUTE9"),
		TaskNumber				("ATTRIBUTE10"),
		ControllingTeam			("ATTRIBUTE11"),
		ExpenditureType			("ATTRIBUTE12"),
		AfeWO					("ATTRIBUTE13"),

		UserKOCNumber			("ATTRIBUTE14"),
		CommodityGroup			("ATTRIBUTE15"),
		Vendor					("ATTRIBUTE16"),
		ApiSequence				("ATTRIBUTE17"),
		DocumentSite			("ATTRIBUTE18"),
		ContractNumber			("ATTRIBUTE19"),
		ToLocation				("ATTRIBUTE20"),
		FromLocation			("ATTRIBUTE14"),
		ProratedDocumentNumber	("ATTRIBUTE20");
		
		private String attribute;
		private GLInterfaceFields(String attribute)
		{
			this.attribute = attribute;
		}
		
		public String toString()
		{
			return attribute;
		}
	}
	
	public enum PAInterfaceFields implements Field
	{
		DocumentHeaderNum		("ATTRIBUTE1"),
		DocumentLineNum			("ATTRIBUTE2"),
		DocumentType			("ATTRIBUTE3"),
		MaximoTransactionNumber	("ATTRIBUTE4"),
		CommodityCode			("ATTRIBUTE5"),
		ItemNumber				("ATTRIBUTE6"),
		TransactionDescription	("ATTRIBUTE7"),
		TransactionType			("ATTRIBUTE8"),
		ContractNumber			("ATTRIBUTE9"),
		UserKOCNumber			("ATTRIBUTE10"),
		ApiSequence				("ORIG_TRANSACTION_REFERENCE"),
		Source					("SYSTEM_LINKAGE");
		
		private String attribute;
		private PAInterfaceFields(String attribute)
		{
			this.attribute = attribute;
		}
		
		public String toString()
		{
			return attribute;
		}
	}
	
	public enum PACommitmentFields implements Field
	{
		DocumentHeaderNum		( "ATTRIBUTE1"),
		DocumentLineNum			( "ATTRIBUTE2"),
		DocumentType			( "ATTRIBUTE3"),
		MaximoTransactionNumber	( "ATTRIBUTE4"),
		CommodityCode			( "ATTRIBUTE5"),
		ItemNumber				( "ATTRIBUTE6"),
		TransactionDescription	( "ATTRIBUTE7"),
		TransactionType			( "ATTRIBUTE8"),
		ContractNumber			( "ATTRIBUTE9"),
		UserKOCNumber			("ATTRIBUTE10"),
		ApiSequence				("ORIGINAL_TRANSACTION_REFERENCE"),
		Source					("SYSTEM_LINKAGE_FUNCTION");
	
		private String attribute;
		private PACommitmentFields(String attribute)
		{
			this.attribute = attribute;
		}
		
		public String toString()
		{
			return attribute;
		}
	}
	
	public String toString();
}

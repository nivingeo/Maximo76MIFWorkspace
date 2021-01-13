package ix.handlers.custom.koc.trans.util;

import ix.handlers.custom.koc.trans.util.Document.DocumentType;

public class Document// implements Mappable
{	
	public enum DocumentType
	{
		WO,
		MR,
		PR,
		PO,
		SO,
		NCSO,
		NCSOR,
		CFA,
		INV,
		OTHER;

		public static DocumentType get(String docType) throws Exception
		{
			if (docType.equals("WO"))
			{
				return WO;
			}
			else if (docType.equals("MR"))
			{
				return MR;
			}
			else if (docType.equals("PR"))
			{
				return PR;
			}
			else if (docType.equals("PO"))
			{
				return PO;
			}
			else if (docType.equals("NCSO"))
			{
				return NCSO;
			}
			else if (docType.equals("NCSOR"))
			{
				return NCSOR;
			}
			else if (docType.equals("SO"))
			{
				return SO;
			}
			else if (docType.equals("CFA"))
			{
				return CFA;
			}
			else if (docType.equals("INV"))
			{
				return INV;
			}
			else
			{
				return OTHER;
			}

		}
	}

	protected DocumentType	docType;	
	protected String		docNum;
	protected String		docLineNum;
	protected int		    docRevision;

	public Document(DocumentType docType, String docNum)
	{
		this.docType 	= docType;
		this.docNum 	= docNum;		
	}
	
	public Document(DocumentType docType, String docNum, int docRevision)
	{
		this.docType 	 = docType;
		this.docNum 	 = docNum;		
		this.docRevision = docRevision;
	}
	
	public Document(DocumentType docType, String docNum, String docLineNum)
	{
		this.docType 	= docType;
		this.docNum 	= docNum;		
		this.docLineNum = docLineNum;
	}

	
	public DocumentType getDocType()
	{
		return docType;
	}

	public String getDocNum()
	{
		return docNum;
	}

	public String getLineNum()
	{
		return docLineNum;
	}

	public int getRevision()
	{
		return docRevision;
	}

	boolean isPORepair = false;
	public void setPOisRepair(boolean isRepair)
	{
		if (docType == DocumentType.PO)
		{
			isPORepair = isRepair;
		}		
	}

	public boolean getIsPORepair()
	{
		return isPORepair;
	}
}
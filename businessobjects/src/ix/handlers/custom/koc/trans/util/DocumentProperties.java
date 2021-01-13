package ix.handlers.custom.koc.trans.util;
//Praxis - Rojitha - Util class for Journal Processing
import psdi.mbo.MboRemote;

import ix.handlers.custom.koc.trans.util.Field.GLInterfaceFields;
import ix.handlers.custom.koc.trans.util.Field.PACommitmentFields;
import ix.handlers.custom.koc.trans.util.Field.PAInterfaceFields;

public class DocumentProperties implements Mappable
{	
	protected String	docType;
	protected String	docNum;
	protected String	docLineNum;
	protected String	docSite;

	public DocumentProperties()
	{
	}
	
	

	@Override
	public void copyToPAInterfaceRecord(MboRemote interfaceRecord)
			throws Exception {
		// TODO Auto-generated method stub
		
	    
		interfaceRecord.setValue(PAInterfaceFields.DocumentType.toString(), 		this.docType);
		
	    
		interfaceRecord.setValue(PAInterfaceFields.DocumentHeaderNum.toString(), this.docNum);
		interfaceRecord.setValue(PAInterfaceFields.DocumentLineNum.toString(), 	this.docLineNum);
	}

	@Override
	public void copyToGLInterfaceRecord(MboRemote interfaceRecord)
			throws Exception {

		
		interfaceRecord.setValue(GLInterfaceFields.DocumentType.toString(), 		this.docType);
		interfaceRecord.setValue(GLInterfaceFields.DocumentHeaderNum.toString(), this.docNum);
		interfaceRecord.setValue(GLInterfaceFields.DocumentLineNum.toString(), 	this.docLineNum);
		interfaceRecord.setValue(GLInterfaceFields.DocumentSite.toString(), 		this.docSite);
		
		
	}
}
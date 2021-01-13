package ix.handlers.custom.koc.trans.util;
//Praxis - Rojitha - Util class for Journal Processing
import psdi.mbo.MboRemote;

import ix.handlers.custom.koc.trans.util.Field.GLInterfaceFields;
import ix.handlers.custom.koc.trans.util.Field.PACommitmentFields;
import ix.handlers.custom.koc.trans.util.Field.PAInterfaceFields;

public class ItemProperties implements Mappable
{
	protected String	itemNumber;
	protected String	itemSetID;
	protected String	commodityCode;
	protected String	commodityGroup;
	protected String	description;

	public ItemProperties()
	{
	}

	@Override
	public void copyToPAInterfaceRecord(MboRemote interfaceRecord)
			throws Exception {
		// TODO Auto-generated method stub
		
		interfaceRecord.setValue(PAInterfaceFields.ItemNumber.toString(), this.itemNumber);
		interfaceRecord.setValue(PAInterfaceFields.CommodityCode.toString(), this.commodityCode);
		
	}

	@Override
	public void copyToGLInterfaceRecord(MboRemote interfaceRecord)
			throws Exception {
		// TODO Auto-generated method stub
		interfaceRecord.setValue(GLInterfaceFields.ItemNumber.toString(), this.itemNumber);
		interfaceRecord.setValue(GLInterfaceFields.CommodityCode.toString(), this.commodityCode);
		interfaceRecord.setValue(GLInterfaceFields.CommodityGroup.toString(), this.commodityGroup);
		
		
	}
}
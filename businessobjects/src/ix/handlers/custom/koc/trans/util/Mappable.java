package ix.handlers.custom.koc.trans.util;
//Praxis - Rojitha - Util class for Journal Processing
import psdi.mbo.MboRemote;


public interface Mappable
{
	
	public abstract void copyToPAInterfaceRecord  (MboRemote  interfaceRecord) throws Exception;
	public abstract void copyToGLInterfaceRecord  (MboRemote  interfaceRecord) throws Exception;
}
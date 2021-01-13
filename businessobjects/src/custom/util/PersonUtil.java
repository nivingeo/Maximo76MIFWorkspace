package custom.util;

import java.rmi.RemoteException;

import psdi.app.signature.MaxUserSetRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.SqlFormat;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.server.MaxVarServiceRemote;
import psdi.util.MXException;
import psdi.util.MXSession;

public class PersonUtil {
	
	public static MboRemote getPerson(MXSession session) throws MXException, RemoteException {
		MboRemote user = null;
		MboRemote person = null; 
		
		MboSetRemote userSet = MXServer.getMXServer().getMboSet("MAXUSER", session.getUserInfo());
		SqlFormat sqf = new SqlFormat("userid = :1");
		sqf.setObject(1, "MAXUSER", "USERID", session.getUserInfo().getUserName());
		userSet.setWhere(sqf.format());
		userSet.reset();
		user = userSet.getMbo(0);
		
		if (user != null) {
			person = user.getMboSet(MaxUserSetRemote.PERSON).getMbo(0);
		}
		
		return person;
	}
	
	public static MboRemote getPerson(UserInfo userInfo) throws MXException, RemoteException {
		MboRemote user = null;
		MboRemote person = null; 
		
		MboSetRemote userSet = MXServer.getMXServer().getMboSet("MAXUSER", userInfo);
		SqlFormat sqf = new SqlFormat("userid = :1");
		sqf.setObject(1, "MAXUSER", "USERID", userInfo.getUserName());
		userSet.setWhere(sqf.format());
		userSet.reset();
		user = userSet.getMbo(0);
		
		if (user != null) {
			person = user.getMboSet(MaxUserSetRemote.PERSON).getMbo(0);
		}
		
		return person;
	}
	
	public static MboRemote getUser(MXSession session) throws MXException, RemoteException{
		MboRemote user = null;
		
		MboSetRemote userSet = MXServer.getMXServer().getMboSet("MAXUSER", session.getUserInfo());
		SqlFormat sqf = new SqlFormat("userid = :1");
		sqf.setObject(1, "MAXUSER", "USERID", session.getUserInfo().getUserName());
		userSet.setWhere(sqf.format());
		userSet.reset();
		user = userSet.getMbo(0);
		return user;
	}
	
	public static MboRemote getTeamDetails(MboRemote person) throws MXException, RemoteException{
		
		MboSetRemote teams = person.getMboSet("PERSONTEAM");
		if(teams.count()>0){
			return teams.getMbo(0);
		}else{
			return null;
		}
	}

	public static MboRemote getGroupDetails(MboRemote person) throws MXException, RemoteException{
		
		MboSetRemote teams = person.getMboSet("PERSONGROUP");
		if(teams.count()>0){
			return teams.getMbo(0);
		}else{
			return null;
		}
	}
	
	public static String getPersonTeam(MboValue value, String relation) throws MXException, RemoteException
	{
		MboSetRemote persons = value.getMbo().getMboSet(relation);

		if (persons.isEmpty()) return "";
		
		MboRemote person = persons.getMbo(0);
		MboSetRemote personTeamSet = person.getMboSet("PERSONTEAM");
			
		if (personTeamSet.isEmpty()) return "";
			
		MboRemote personTeam = personTeamSet.getMbo(0);
		return personTeam.getString("PersonGroup");				
	}
	
	public static String getPersonGroup(MboValue value, String relation) throws MXException, RemoteException
	{
		MboSetRemote persons = value.getMbo().getMboSet(relation);

		if (persons.isEmpty()) return "";
		
		MboRemote person = persons.getMbo(0);
		MboSetRemote personTeamSet = person.getMboSet("PERSONGROUP");
			
		if (personTeamSet.isEmpty()) return "";
			
		MboRemote personTeam = personTeamSet.getMbo(0);
		return personTeam.getString("PersonGroup");				
	}
	
	public static String getPersonTeam(MboRemote mbo, String relation) throws MXException, RemoteException
	{
		MboSetRemote persons = mbo.getMboSet(relation);

		if (persons.isEmpty()) return "";
		
		MboRemote person = persons.getMbo(0);
		MboSetRemote personTeamSet = person.getMboSet("PERSONTEAM");
			
		if (personTeamSet.isEmpty()) return "";
			
		MboRemote personTeam = personTeamSet.getMbo(0);
		return personTeam.getString("PersonGroup");				
	}
	
	public static String getPersonGroup(MboRemote mbo, String relation) throws MXException, RemoteException
	{
		MboSetRemote persons = mbo.getMboSet(relation);

		if (persons.isEmpty()) return "";
		
		MboRemote person = persons.getMbo(0);
		MboSetRemote personTeamSet = person.getMboSet("PERSONGROUP");
			
		if (personTeamSet.isEmpty()) return "";
			
		MboRemote personTeam = personTeamSet.getMbo(0);
		return personTeam.getString("PersonGroup");				
	}
	
	public static MboRemote getPerson(String personid, UserInfo userInfo)throws 
		MXException, RemoteException{
		MboSetRemote personSet = MXServer.getMXServer().getMboSet("PERSON", userInfo);
		SqlFormat sqf = new SqlFormat("personid = :1");
		sqf.setObject(1, "PERSON", "PERSONID", personid);
		personSet.setWhere(sqf.format());
		personSet.reset();
		return personSet.getMbo(0);
	}
	
	public static MboRemote getPersonByUserId(UserInfo userInfo) throws 
		MXException, RemoteException{
		MboRemote user = PersonUtil.getPerson(userInfo);
		MboSetRemote personSet = MXServer.getMXServer().getMboSet("PERSON", userInfo);
		SqlFormat sqf = new SqlFormat("personid = :1");
		sqf.setObject(1, "PERSON", "PERSONID", user.getString("PERSONID"));
		personSet.setWhere(sqf.format());
		personSet.reset();
		return personSet.getMbo(0);
		
	}	
}

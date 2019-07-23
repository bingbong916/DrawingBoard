package kr.ac.konkuk.ccslab.cm.info;

import java.util.Hashtable;

import kr.ac.konkuk.ccslab.cm.entity.CMList;
import kr.ac.konkuk.ccslab.cm.entity.CMMqttSession;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEvent;

/**
 * The CMMqttInfo class stores information required for the MQTT protocol.
 * @author CCSLab, Konkuk University
 *
 */
public class CMMqttInfo {

	// mqtt session information (4 client)
	CMMqttSession m_mqttSession;
	// mqtt session information (4 server)
	Hashtable<String, CMMqttSession> m_mqttSessionHashtable;
	// mqtt retain event
	Hashtable<String, CMList<CMMqttEvent>> m_mqttRetainHashtable;
	
	public CMMqttInfo()
	{
		m_mqttSession = null;
		m_mqttSessionHashtable = new Hashtable<String, CMMqttSession>();
		m_mqttRetainHashtable = new Hashtable<String, CMList<CMMqttEvent>>();
	}
	
	// setter/getter
	public void setMqttSession(CMMqttSession session)
	{
		m_mqttSession = session;
	}
	
	public CMMqttSession getMqttSession()
	{
		return m_mqttSession;
	}
	
	public void setMqttSessionHashtable(Hashtable<String, CMMqttSession> sessionHashtable)
	{
		m_mqttSessionHashtable = sessionHashtable;
	}
	
	public Hashtable<String, CMMqttSession> getMqttSessionHashtable()
	{
		return m_mqttSessionHashtable;
	}
	
	public void setMqttRetainHashtable(Hashtable<String, CMList<CMMqttEvent>> retainHashtable)
	{
		m_mqttRetainHashtable = retainHashtable;
	}
	
	public Hashtable<String, CMList<CMMqttEvent>> getMqttRetainHashtable()
	{
		return m_mqttRetainHashtable;
	}
}

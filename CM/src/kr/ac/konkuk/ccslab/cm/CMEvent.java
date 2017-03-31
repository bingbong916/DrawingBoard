package kr.ac.konkuk.ccslab.cm;
import java.nio.*;

/**
 * This class represents a CM event.
 * <br> The CMEvent class is the superclass of every CM event. The CMEvent class includes the common header 
 * fields of a CM event such as which session and group (handler session and group names) should handle 
 * this event and which session and group members (distribution session and group names) this event should be 
 * forwarded to.  
 * <p> CM nodes (a client or a server) communicates with each other by exchanging CM events. 
 * @author mlim
 * @see {@link CMConcurrencyEvent}, {@link CMConsistencyEvent}, {@link CMDataEvent}, {@link CMDummyEvent}, 
 * {@link CMFileEvent}, {@link CMInterestEvent}, {@link CMMultiServerEvent}, {@link CMSessionEvent}, 
 * {@link CMSNSEvent}, {@link CMUserEvent}
 */
public class CMEvent extends CMObject {
	
	protected String m_strHandlerSession;
	protected String m_strHandlerGroup;
	protected String m_strDistributionSession;
	protected String m_strDistributionGroup;
	protected int m_nID;
	protected int m_nByteNum;	// total number of bytes in the event
	ByteBuffer m_bytes;

	/**
	 * Creates an empty CMEvent object.
	 */
	public CMEvent()
	{
		m_nType = CMInfo.CM_EVENT;
		m_nID = -1;
		m_strHandlerSession = "";
		m_strHandlerGroup = "";
		m_strDistributionSession = "";
		m_strDistributionGroup = "";
		m_nByteNum = -1;
		m_bytes = null;
	}
	
	/**
	 * Creates a CMEvent object.
	 * <br> This CM object marshalls a given bytes of a message.
	 * @param msg - the bytes of a message
	 */
	public CMEvent(ByteBuffer msg)
	{
		m_nType = CMInfo.CM_EVENT;
		m_nID = -1;
		m_strHandlerSession = "";
		m_strHandlerGroup = "";
		m_strDistributionSession = "";
		m_strDistributionGroup = "";
		m_nByteNum = -1;
		m_bytes = null;
		
		unmarshallHeader(msg);
		unmarshallBody(msg);
	}

	/**
	 * Marshals the CM event.
	 * <br> This method changes the event fields into bytes in the ByteBuffer so that the event can be sent 
	 * through the communication channel.
	 * <br> If the subclass of the CMEvent class is instantiated and the object calls this method, it conducts 
	 * marshalling of both the event header and the event body fields.
	 * 
	 * @return a reference to the ByteBuffer object that includes the marshalled event fields.
	 * If the instance of the subclass of the CMEvent calls this method, the ByteBuffer object includes both 
	 * the marshalled event header and body fields.
	 * 
	 * @see {@link CMConcurrencyEvent#marshallBody()}, {@link CMConsistencyEvent#marshallBody()}, 
	 * {@link CMDataEvent#marshallBody()}, {@link CMDummyEvent#marshallBody()}, {@link CMFileEvent#marshallBody()}, 
	 * {@link CMInterestEvent#marshallBody()}, {@link CMMultiServerEvent#marshallBody()}, 
	 * {@link CMSessionEvent#marshallBody()}, {@link CMSNSEvent#marshallBody()}, {@link CMUserEvent#marshallBody()}
	 */
	public ByteBuffer marshall()
	{
		allocate();
		marshallHeader();
		marshallBody();
		return m_bytes;
	}
	
	/**
	 * Unmarshals the given ByteBuffer to the CM event.
	 * <br> This method changes bytes in the ByteBuffer to the corresponding event fields of this event object.
	 * <br> If the subclass of the CMEvent class is instantiated and the object calls this method, it conducts 
	 * unmarshalling of both the event header and the event body fields.
	 * 
	 * @param msg - the bytes to be unmarshalled
	 * @return a reference to the CMEvent object.
	 * 
	 * @see {@link CMConcurrencyEvent#unmarshallBody()}, {@link CMConsistencyEvent#unmarshallBody()}, 
	 * {@link CMDataEvent#unmarshallBody()}, {@link CMDummyEvent#unmarshallBody()}, {@link CMFileEvent#unmarshallBody()}, 
	 * {@link CMInterestEvent#unmarshallBody()}, {@link CMMultiServerEvent#unmarshallBody()}, 
	 * {@link CMSessionEvent#unmarshallBody()}, {@link CMSNSEvent#unmarshallBody()}, {@link CMUserEvent#unmarshallBody()}
	 */
	public CMEvent unmarshall(ByteBuffer msg)
	{
		// should be implemented in sub-classes
		unmarshallHeader(msg);
		unmarshallBody(msg);
		return this;
	}

	/**
	 * Sets the event ID field.
	 * <br> When the event object is initialized, the event ID is set to -1.
	 * @param id - the event ID
	 */
	public void setID(int id)
	{
		m_nID = id;
	}
	
	/**
	 * Returns the event ID.
	 * @return an event ID, or -1 if this event does not set any ID.
	 */
	public int getID()
	{
		return m_nID;
	}

	/**
	 * Sets a session of this event.
	 * <br> The session name determines which session deals with this event.
	 * <br> When the event object is initialized, the session is set to the empty string (""). The empty session name 
	 * specifies that this event is internally handled by the CMInteractionManager of the receiver CM.
	 * 
	 * @param sName - the session name
	 */
	public void setHandlerSession(String sName)
	{
		m_strHandlerSession = sName;
	}
	
	/**
	 * Sets a group of this event.
	 * <br> The group name determines which group deals with this event.
	 * <br> When the event object is initialized, the group is set to the empty string (""). If the group name is empty 
	 * and the specific session name is set, this event is internally handled by the CMSessionManager of the receiver CM.
	 * If both the group and the session are empty, this event is internally handled by the CMInteractionManager of 
	 * the receiver CM. If both the group and the session are set to specific names, this event is internally handled by 
	 * the CMGroupManager of the receiver CM.
	 *  
	 * @param gName - the group name
	 */
	public void setHandlerGroup(String gName)
	{
		m_strHandlerGroup = gName;
	}
	
	/**
	 * 
	 * (from here)
	 * @param sName
	 */
	public void setDistributionSession(String sName)
	{
		m_strDistributionSession = sName;
	}
	
	public void setDistributionGroup(String gName)
	{
		m_strDistributionGroup = gName;
	}

	public String getHandlerSession()
	{
		return m_strHandlerSession;
	}
	
	public String getHandlerGroup()
	{
		return m_strHandlerGroup;
	}
	
	public String getDistributionSession()
	{
		return m_strDistributionSession;
	}
	
	public String getDistributionGroup()
	{
		return m_strDistributionGroup;
	}
	
	/////////////////////////////////////////////////
	
	protected void allocate()
	{
		m_nByteNum = getByteNum();
		m_bytes = ByteBuffer.allocate(m_nByteNum);
		
		// this allocated object should be deallocated after the event is sent by a sending method.
	}
	
	protected void marshallHeader()
	{
		/*
		typedef struct _cmEvent {
			int byteNum;
			int type;
			unsigned int id;
			char handlerSession[EVENT_FIELD_LEN];
			char handlerRegion[EVENT_FIELD_LEN];
			char distributionSession[EVENT_FIELD_LEN];
			char distributionRegion[EVENT_FIELD_LEN];
			unsigned char body[1];
		} cmEvent;
		*/
		
		//if( !CMEndianness.isBigEndian() )
		//	m_bytes.order(ByteOrder.BIG_ENDIAN);
		
		m_bytes.putInt(m_nByteNum);
		m_bytes.putInt(m_nType);
		m_bytes.putInt(m_nID);
		m_bytes.putInt(m_strHandlerSession.getBytes().length);
		m_bytes.put(m_strHandlerSession.getBytes());
		m_bytes.putInt(m_strHandlerGroup.getBytes().length);
		m_bytes.put(m_strHandlerGroup.getBytes());
		m_bytes.putInt(m_strDistributionSession.getBytes().length);
		m_bytes.put(m_strDistributionSession.getBytes());
		m_bytes.putInt(m_strDistributionGroup.getBytes().length);
		m_bytes.put(m_strDistributionGroup.getBytes());
		//m_bytes.rewind();
		
		//if( !CMEndianness.isBigEndian() )
		//	m_bytes.order(ByteOrder.LITTLE_ENDIAN);

	}
	
	protected void unmarshallHeader(ByteBuffer msg)
	{
		int nStrNum;
		
		/*
		typedef struct _cmEvent {
			int byteNum;
			int type;
			unsigned int id;
			char handlerSession[EVENT_FIELD_LEN];
			char handlerRegion[EVENT_FIELD_LEN];
			char distributionSession[EVENT_FIELD_LEN];
			char distributionRegion[EVENT_FIELD_LEN];
			unsigned char body[1];
		} cmEvent;
		*/

		// add endian test
		
		m_nByteNum = msg.getInt();
		m_nType = msg.getInt();
		m_nID = msg.getInt();
		
		nStrNum = msg.getInt();
		byte[] strBytes = new byte[nStrNum];
		msg.get(strBytes);
		m_strHandlerSession = new String(strBytes);
		
		nStrNum = msg.getInt();
		strBytes = new byte[nStrNum];
		msg.get(strBytes);
		m_strHandlerGroup = new String(strBytes);
		
		nStrNum = msg.getInt();
		strBytes = new byte[nStrNum];
		msg.get(strBytes);
		m_strDistributionSession = new String(strBytes);
		
		nStrNum = msg.getInt();
		strBytes = new byte[nStrNum];
		msg.get(strBytes);
		m_strDistributionGroup = new String(strBytes);
		
		//msg.rewind();
		
		/*
		if( !CMEndianness.isBigEndian() )
		{
			msg.order(ByteOrder.BIG_ENDIAN);
			msg.put(msg);
			msg.rewind();
		}
		*/

	}
	
	protected void marshallBody()
	{
		m_bytes.clear();
		// should be implemented in sub-classes
	}
	
	protected void unmarshallBody(ByteBuffer msg)
	{
		msg.clear();
		// should be implemented in sub-classes
	}

	protected void setByteNum(int nByteNum)
	{
		m_nByteNum = nByteNum;
	}
	
	protected int getByteNum()
	{
		// can be re-implemented by sub-class
		int nSize;
		nSize = (Integer.BYTES)*7 + m_strHandlerSession.getBytes().length + m_strHandlerGroup.getBytes().length
				+ m_strDistributionSession.getBytes().length + m_strDistributionGroup.getBytes().length;
		return nSize;
	}
	
	protected String getStringFromByteBuffer(ByteBuffer msg)
	{
		int nStrNum;
		byte[] strBytes;
		
		nStrNum = msg.getInt();
		strBytes = new byte[nStrNum];
		msg.get(strBytes);
		
		return new String(strBytes);
	}
	
}

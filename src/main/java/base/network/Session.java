package base.network;

import io.netty.channel.Channel;

/**
 * session 中只有基本信息，包括名字，Id， 以及连接信息，不包含复杂元素
 * 玩家可以包含一个session
 * Created by Administrator on 2017/7/18.
 */
public  class Session {

    // -1 是系统保留dummySession id
    public static Session dummySession = new Session(-1L, "#dummySession#", null);

    protected Long sessionId; //unique sessionId;

    protected String name;

    protected Channel channel;

    public Session(Long sessionId, String name, Channel channel) {
        this.sessionId = sessionId;
        this.name = name;
        this.channel = channel;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Session))
            return false;
        Session o1 = (Session)o;
        if(o1.sessionId.equals(this.sessionId) && o1.channel.equals(this.channel)){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionId=" + sessionId +
                ", name='" + name + '\'' +
                ", channel=" + channel +
                '}';
    }

    public boolean isDummy(){
        return this == dummySession;
    }


    public void onRemove() {
        //onSessionRemoved;

        //do remove behaviours;
    }

    //derived by sub or implementation
    public void safeClose(){}
}

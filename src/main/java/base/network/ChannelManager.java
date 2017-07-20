package base.network;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.netty.channel.Channel;
/**
 * 短线重新连接的channel和原来的channel可能id不同，需要处理这种情况
 * initialized;
 * Created by Administrator on 2017/7/18.
 */

public class ChannelManager
{
    private Map<Channel, Session> channelSession = new ConcurrentHashMap<Channel, Session>();

    private Map<Long, Session> quickSessionLookup = new ConcurrentHashMap<Long, Session>();

    private String name;

    public ChannelManager(String name){
        this.name = name;
    }


    public boolean put(Channel channel, Session session){
        boolean added = put0(channel, session);
        if(added){
            quickSessionLookup.put(session.getSessionId(), session);
        }
        return added;
    }

    public Session get(Long sessionId){
        return this.quickSessionLookup.get(sessionId);
    }

    public Session get(Channel channel){
        return this.channelSession.get(channel);
    }

    public void remove(Channel channel){
        Session sessoin = channelSession.get(channel);
        sessoin.onRemove();
        quickSessionLookup.remove(sessoin.getSessionId());
        channelSession.remove(channel);
    }

    public void remove(Session session){
        remove(session.getChannel());
    }

    public void remove(Long sessionId){
        Session session = quickSessionLookup.get(sessionId);
        if(session != null){
            remove(session);
        }
    }

    private boolean put0(Channel channel, Session session) {
        if (channel == null || session == null)
            throw new InvalidParameterException("invalid channel or invalid session");
        if (channelSession.get(channel) == null) {
            if(channel.equals(session.channel)){
                channelSession.put(channel, session);
                return true;
            }
            return false;
        }
        if(session.getSessionId() != -1 && quickSessionLookup.containsKey(session.getSessionId())){
            channel = quickSessionLookup.get(session.getSessionId()).getChannel();
            if(channel != null ){
                remove(channel); //安全关闭以前的session
            }
            channelSession.put(channel, session);
            return true;
        }

        if(channel == null || session == null){
            return false;
        }

        return false;
    }


    public CopyOnWriteArrayList<Session> getAll(){
        return new CopyOnWriteArrayList<>(this.quickSessionLookup.values());
    }

    public Set<Map.Entry<Long,Session>> entrySet(){
        return this.quickSessionLookup.entrySet();
    }

    public void removeAll(){
        this.channelSession.clear();
        this.quickSessionLookup.clear();
    }
}
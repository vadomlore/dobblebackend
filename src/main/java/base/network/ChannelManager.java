package base.network;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.netty.channel.Channel;
/**
 * dummy session can only be added when channel is first
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
        quickSessionLookup.remove(sessoin.getSessionId());
        sessoin.onRemove();
        channelSession.remove(channel);
    }

    public void remove(Session session){
        if(session.isDummy()) return;
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
        if(channel != null && channelSession.get(channel).isDummy())
        {
            channelSession.put(channel, session);
            return true;
        }

        if(channel == null || session == null){
            return false;
        }

        if (channelSession.get(channel) == null || channelSession.get(channel).isDummy()) {
            if(channel.equals(session.channel)){
                channelSession.put(channel, session);
                return true;
            }
            if(channelSession.get(channel).isDummy() && !session.isDummy()){
                return false;
            }
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
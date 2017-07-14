package server;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * [gs-1, watchingEventa, watchingEventB]
 * Created by zyl on 2017/7/14.
 */
public class OnlineGameServerWatcher
{
  private Multimap<ServerBean, WatchingEvent> watchingEvents = LinkedListMultimap.create(); // 监控服务器的多种事件
}

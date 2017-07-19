package base.network;

/**
 * Created by zyl on 2017/7/19.
 */
public class SessionUtils
{
    public static void safeClose(Session session)
    {
        session.safeClose();
    }
}

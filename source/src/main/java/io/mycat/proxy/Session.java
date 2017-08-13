package io.mycat.proxy;
/**
 * 代表一个会话
 * @author wuzhihui
 *
 */
public interface Session {
	boolean isClosed();
	public <T extends Session> SessionManager<T> getMySessionManager();
}

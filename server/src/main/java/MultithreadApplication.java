import java.util.List;
import java.util.concurrent.atomic.*;

import org.red5.server.adapter.*;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.service.ServiceUtils;
import org.red5.server.api.so.ISharedObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultithreadApplication extends MultiThreadedApplicationAdapter {

	private static final Logger log = LoggerFactory.getLogger(MultithreadApplication.class);

	private IScope appScope;
	private IConnection conn;
	
	private AtomicInteger counter = new AtomicInteger();
	
	/** {@inheritDoc} */
	@Override
	public boolean connect(IConnection conn, IScope scope, Object[] params) {
		appScope = scope;
		this.conn = conn;
		createSharedObject(appScope, "message", false);
		return super.connect(conn, scope, params);
	}
	
	/** {@inheritDoc} */
	@Override
	public void disconnect(IConnection conn, IScope scope) {
		super.disconnect(conn, scope);
		log.info("Message counter: {}", counter.get());
		//System.exit(9999);
	}
	
	public void sendMessage(List<String> params) {
		// increment our local receive counter
		counter.getAndIncrement();
		ISharedObject so = getSharedObject(appScope, "message");
		if (so != null) {
			so.sendMessage("receiveMessage", params);
		}		
		
		ServiceUtils.in(conn, "receiveMessage", params.toArray());
		ServiceUtils.
	}
}

import java.util.List;
import java.util.concurrent.atomic.*;

import org.red5.server.adapter.*;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationNC extends ApplicationAdapter {

	private static final Logger log = LoggerFactory.getLogger(ApplicationNC.class);

	private IScope appScope;

	private AtomicInteger counter = new AtomicInteger();
	
	/** {@inheritDoc} */
	@Override
	public boolean connect(IConnection conn, IScope scope, Object[] params) {
		appScope = scope;
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
	}
}

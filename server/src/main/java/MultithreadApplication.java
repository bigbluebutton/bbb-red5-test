import java.util.List;
import java.util.concurrent.atomic.*;
import org.red5.server.adapter.*;
import org.red5.server.api.IClient;
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
		
//		ServiceUtils.(conn, "receiveMessage", params.toArray());
	}
	
	@Override
	public boolean appStart(IScope app) {
		System.out.println("**************** App Start ****************************");
		return super.appStart(app);
	}

	@Override
	public boolean appConnect(IConnection conn, Object[] params) {
		System.out.println("**************** App Connect ****************************");
		return super.appConnect(conn, params);
	}

	@Override
	public boolean appJoin(IClient client, IScope app) {
		System.out.println("**************** App Join ****************************");
		return super.appJoin(client, app);
	}

	@Override
	public void appDisconnect(IConnection conn) {
		System.out.println("**************** App Disconnect ****************************");
	}

	@Override
	public void appLeave(IClient client, IScope app) {
		System.out.println("**************** App Leave ****************************");
	}

	@Override
	public void appStop(IScope app) {
		System.out.println("**************** App Stop ****************************");
	}

	@Override
	public boolean roomStart(IScope room) {
		System.out.println("**************** Room Start ****************************");
		return super.roomStart(room);
	}

	@Override
	public boolean roomConnect(IConnection conn, Object[] params) {
		System.out.println("**************** Room Connect ****************************");
		return super.roomConnect(conn, params);
	}

	@Override
	public boolean roomJoin(IClient client, IScope room) {
		System.out.println("**************** Room Join ****************************");
		return super.roomJoin(client, room);		
	}

	@Override
	public void roomDisconnect(IConnection conn) {
		System.out.println("**************** Room Disconnect ****************************");
		super.roomDisconnect(conn);			
	}

	@Override
	public void roomLeave(IClient client, IScope room) {
		System.out.println("**************** Room Leave ****************************");
		super.roomLeave(client, room);			
	}

	@Override
	public void roomStop(IScope room) {
		System.out.println("**************** Room Stop ****************************");
		super.roomStop(room);				
	}
}

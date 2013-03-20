import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.*;
import org.red5.server.adapter.*;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.service.ServiceUtils;
import org.red5.server.api.so.ISharedObject;

public class MultithreadApplication extends MultiThreadedApplicationAdapter {

	private final Map<String, AtomicInteger> counters = new HashMap<String, AtomicInteger>();
		
	public void sendMessage() {
		sendMessage(false);
	}	
	
	public void sendMessage(Boolean useSO) {
		String scopeName = Red5.getConnectionLocal().getScope().getName();
		
		if (counters.containsKey(scopeName)) {
			AtomicInteger counter = (AtomicInteger) counters.get(scopeName);
			int curCounter = counter.get();
			ArrayList<String> args = new ArrayList<String>();
			args.add(new Integer(curCounter).toString());
			args.add(new Integer(curCounter + 1).toString());
			if (useSO) {
				ISharedObject so = getSharedObject(Red5.getConnectionLocal().getScope(), "message");
				if (so != null) {
					so.sendMessage("receiveMessage", args);
				}					
			} else {
				ServiceUtils.invokeOnAllScopeConnections(Red5.getConnectionLocal().getScope(), "receiveMessage", args.toArray(), null);
			}
			// increment our local receive counter
			counter.getAndIncrement();
			
			if (counter.intValue() % 1000 == 0) {
				System.out.println("Received message id=[" + Red5.getConnectionLocal().getClient().getId() 
						+ "] msgCount=[" + counter.get() + "] useSO=" + useSO);
			}
		} else {
			System.out.println("Cannot find counter for scope [" + scopeName + "]");
		}
			
	}
	
	@Override
	public boolean appStart(IScope app) {		
		System.out.println("**************** App Start [" + app.getName() + "] ****************************");
		return super.appStart(app);
	}

	@Override
	public boolean appConnect(IConnection conn, Object[] params) {
		System.out.println("**************** App Connect [" + conn.getScope().getName() + "] ****************************");
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
		System.out.println("**************** Room Start [ " + room.getName() + "] ****************************");
		
		if (!counters.containsKey(room.getName())) {
			AtomicInteger counter = new AtomicInteger();
			counters.put(room.getName(), counter);
		}
		return super.roomStart(room);
	}

	@Override
	public boolean roomConnect(IConnection conn, Object[] params) {
		System.out.println("**************** Room Connect [" + conn.getScope().getName() + "] ****************************");
		return super.roomConnect(conn, params);
	}

	@Override
	public boolean roomJoin(IClient client, IScope room) {
		System.out.println("**************** Room Join [" + room.getName() + "] ****************************");
				
		return super.roomJoin(client, room);		
	}

	@Override
	public void roomDisconnect(IConnection conn) {
		System.out.println("**************** Room Disconnect [" + conn.getScope().getName() + "] ****************************");
		super.roomDisconnect(conn);			
	}

	@Override
	public void roomLeave(IClient client, IScope room) {
		System.out.println("**************** Room Leave [" + room.getName() + "] ****************************");
		super.roomLeave(client, room);			
	}

	@Override
	public void roomStop(IScope room) {
		System.out.println("**************** Room Stop [" + room.getName() + "] ****************************");
		
		if (counters.containsKey(room.getName())) {
			AtomicInteger counter = counters.remove(room.getName());
			System.out.println("Room Stop: room=[" + room.getName() + "] msgCount=[" + counter.get() + "]");
		}
		
		super.roomStop(room);				
	}
}
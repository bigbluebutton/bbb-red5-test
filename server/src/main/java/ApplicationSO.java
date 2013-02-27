import java.util.concurrent.atomic.*;
import org.red5.server.adapter.*;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.red5.server.api.so.ISharedObjectListener;
import org.red5.server.api.so.ISharedObjectBase;
import org.red5.server.api.IAttributeStore;
import java.util.*;

public class ApplicationSO extends MultiThreadedApplicationAdapter  implements ISharedObjectListener  {

	private static final Logger log = LoggerFactory.getLogger(ApplicationSO.class);

	private IScope appScope = null;

	private AtomicInteger counter = new AtomicInteger();

	private int expectedValue = 1;

	/** {@inheritDoc} */
	@Override
	public boolean connect(IConnection conn, IScope scope, Object[] params) {
		appScope = scope;
		ISharedObject so = getSharedObject(appScope, "message");
		if (so == null) {
			createSharedObject(appScope, "message", false);
			so = getSharedObject(appScope, "message");
			so.addSharedObjectListener(this);
			counter.getAndSet(0);
			expectedValue = 1;
		}
		return super.connect(conn, scope, params);
	}
	
	/** {@inheritDoc} */
	@Override
	public void disconnect(IConnection conn, IScope scope) {
		super.disconnect(conn, scope);
		log.info("disconnect");
		//System.exit(9999);
	}
	
	/*public void sendMessage(List<Integer> params) {
		// increment our local receive counter
		counter.getAndIncrement();
		ISharedObject so = getSharedObject(appScope, "message");
		if (so != null) {
			so.sendMessage("receiveMessage", params);
		}			
	} */

	public void onSharedObjectDelete(ISharedObjectBase so, String key)
	{
		log.info("onSharedObjectDelete");
		// The attribute <key> of the shared Object <so> was deleted.
	}
	public void onSharedObjectSend(ISharedObjectBase so,String method, List<?> parameters)
	{
		//return;
		//log.info("onSharedObjectSend: "+counter.incrementAndGet()+" - "+method+"()");
		for (Object o:parameters) {
			int i =((Integer) o).intValue();
			if (i != expectedValue) {
				log.info("expected: "+expectedValue+", received: "+i);
			}
			expectedValue = i + 1;
		}
		// The handler <method> of the shared Object <so> was called
		// with the parameters <params>.
	}  

	public void onSharedObjectClear(ISharedObjectBase so) {
		 log.info("onSharedObjectClear");
		//Called when all attributes of a shared Object are removed.
	}
	public void onSharedObjectDisconnect(ISharedObjectBase so) {
		 log.info("onSharedObjectDisconnect");
		//Called when a client connects to a shared Object.
	}
	public void onSharedObjectConnect(ISharedObjectBase so) {
		 log.info("onSharedObjectConnect");
		//Called when a client connects to a shared Object.
	}

	public void onSharedObjectUpdate(ISharedObjectBase so, IAttributeStore values) {
		 log.info("onSharedObjectUpdate IAttributeStore");
		// Called when multiple attributes of a shared Object are updated.
	}
	public void onSharedObjectUpdate(ISharedObjectBase so, String key, Object value) { 
		 log.info("onSharedObjectUpdate individual");
	}
	public void onSharedObjectUpdate(ISharedObjectBase so, Map<String,Object> values) { 
		 log.info("onSharedObjectUpdate Map");
		// Called when multiple attributes of a shared Object are updated.
	}

}

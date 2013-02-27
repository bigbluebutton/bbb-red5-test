import java.util.List;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;

public class ApplicationNConly extends ApplicationAdapter {

	private IScope appScope;

	/** {@inheritDoc} */
	@Override
	public boolean connect(IConnection conn, IScope scope, Object[] params) {
		appScope = scope;
		createSharedObject(appScope, "message", false);
		return true;
	}
	
	/** {@inheritDoc} */
	@Override
	public void disconnect(IConnection conn, IScope scope) {
		super.disconnect(conn, scope);
	}
	
	public void sendMessage(List<String> params) {
		ISharedObject so = getSharedObject(appScope, "message");
		if (so != null)
			so.sendMessage("receiveMessage", params);
	}
}

package ex13.pyrmont.core;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

public class SimpleContextConfig implements LifecycleListener {

private Context context;
	
	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		if(Lifecycle.START_EVENT.equals(event.getType())) {
			//context变成StandardContext，不再是自定义的了
			this.context = (Context)event.getLifecycle();
			this.context.setConfigured(true);
		}
	}
}

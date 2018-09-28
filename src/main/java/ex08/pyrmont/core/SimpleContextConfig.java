package ex08.pyrmont.core;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

public class SimpleContextConfig implements LifecycleListener {

	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		if(Lifecycle.START_EVENT.equals(event.getType())) {
			//context变成StandardContext，不再是自定义的了
			Context context = (Context)event.getLifecycle();
			context.setConfigured(true);
		}
	}

}

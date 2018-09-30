package ex10.pyrmont.core;

import org.apache.catalina.Authenticator;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityConstraint;

public class SimpleContextConfig implements LifecycleListener {

private Context context;
	
	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		if(Lifecycle.START_EVENT.equals(event.getType())) {
			//context变成StandardContext，不再是自定义的了
			this.context = (Context)event.getLifecycle();
			this.authenticatorConfig();
			this.context.setConfigured(true);
		}
	}

	/**
	 * 检测是否需要验证组件，如果需要则配置
	 */
	private synchronized void authenticatorConfig() {
		SecurityConstraint constraint[] = context.findConstraints();
		//必须有安全限制，才会配置验证
		if(constraint == null || constraint.length == 0) {
			return;
		}
		LoginConfig loginConfig = context.getLoginConfig();
		if(loginConfig == null) {
			loginConfig = new LoginConfig("NONE", null, null, null);
			this.context.setLoginConfig(loginConfig);
		}
		
		Pipeline pipeline = ((StandardContext)context).getPipeline();
		if(pipeline != null) {
			Valve basic = pipeline.getBasic();
			//只有有阀是验证器，则也不用配置了
			if(basic != null && basic instanceof Authenticator) return;
			Valve[] valves = pipeline.getValves();
			for(Valve v : valves) {
				if(v instanceof Authenticator) return;
			}
		}else {
			return;
		}
		
		//也得有值
		if(this.context.getRealm() == null) return;
		//开始动态增加验证器
		String authenticatorName = "org.apache.catalina.authenticator.BasicAuthenticator";
		Valve authenticator = null;
	
		try {
			Class<?> authenticatorClass = Class.forName(authenticatorName);
			authenticator = (Valve)authenticatorClass.newInstance();
			((StandardContext)context).addValve(authenticator);
			System.out.println("Added authenticator valve to Context");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

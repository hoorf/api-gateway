package org.hrf.gateway.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.hrf.gateway.core.annotation.MappingApi;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * @author Administrator
 *
 */
public class ApiContext {

	private static ApplicationContext springContext;

	public static ApiContext outContext;

	private static Map<String, Invoker> apiMethodMap = new HashMap<String, Invoker>();

	public static void init(ApplicationContext applicationContext) {
		Assert.notNull(applicationContext, "could not load spring context");
		springContext = applicationContext;
		initApiMethodMap();
	}

	@SuppressWarnings("unchecked")
	public static <T> T getInvokerByApiName(String apiName) {
		Assert.notNull(apiName, "api name can not be null");
		return (T) apiMethodMap.get(apiName).getTargetObject();
	}

	public static Method getApiMethodByName(String apiName) {
		Assert.notNull(apiName, "api name can not be null");
		return apiMethodMap.get(apiName).getTargetMethod();
	}

	static class Invoker {

		private Object targetObject;

		private Method targetMethod;

		public Object getTargetObject() {
			return targetObject;
		}

		public void setTargetObject(Object targetObject) {
			this.targetObject = targetObject;
		}

		public Method getTargetMethod() {
			return targetMethod;
		}

		public void setTargetMethod(Method targetMethod) {
			this.targetMethod = targetMethod;
		}

		public static Invoker instance() {
			return new Invoker();
		}
	}

	/**
	 * 初始化暴露api的实体
	 */
	private static void initApiMethodMap() {
		String[] beans = springContext.getBeanDefinitionNames();
		for (String beanName : beans) {
			if(beanName.equals("testService")) {
				System.out.println("beanName");
			}
			Object obj = springContext.getBean(beanName);
			try {
				for (Method method : springContext.getBean(beanName).getClass().getMethods()) {
					if (method.getAnnotation(MappingApi.class) != null) {
						Invoker invoker = Invoker.instance();
						invoker.setTargetMethod(method);
						invoker.setTargetObject(obj);
						apiMethodMap.put(method.getAnnotation(MappingApi.class).value(), invoker);
					}
				}
			} catch (Exception e) {
				new RuntimeException("spring context beans has error");
			}
		}

	}

}

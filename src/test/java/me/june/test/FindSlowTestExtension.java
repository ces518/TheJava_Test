package me.june.test;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class FindSlowTestExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

	// 1초로 제한
	private long THRESHOLD = 1000L;

	public FindSlowTestExtension(long threshold) {
		this.THRESHOLD = threshold;
	}

	@Override
	public void beforeTestExecution(ExtensionContext context) throws Exception {
		// ExtensionContext 내에서는 값을 저장할 수 있는 Store 라는 개념이 존재함.
		String testClassName = context.getRequiredTestClass().getName();
		String textMethodName = context.getRequiredTestMethod().getName();
		ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.create(testClassName, textMethodName));
		store.put("START_TIME", System.currentTimeMillis());
	}

	@Override
	public void afterTestExecution(ExtensionContext context) throws Exception {
		String testClassName = context.getRequiredTestClass().getName();
		String textMethodName = context.getRequiredTestMethod().getName();
		ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.create(testClassName, textMethodName));
		long startTime = store.remove("START_TIME", long.class);
		long duration = System.currentTimeMillis() - startTime;
		if (duration > THRESHOLD) {
			System.out.printf("Please consider mark method [%s] with @SlowTest. \n", textMethodName);
		}
	}
}

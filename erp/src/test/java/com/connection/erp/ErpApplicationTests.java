package com.connection.erp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.function.Function;

class ErpApplicationTests {

	private static class ExecuteFunctionAndCommit implements Serializable {
		private static final long serialVersionUID = 1L;
		String jCoFunction;
		SerializableLambda validateFunction;

		public ExecuteFunctionAndCommit(String jCoFunction, SerializableLambda validateFunction) {
			this.jCoFunction = jCoFunction;
			this.validateFunction = validateFunction;
		}


	}
	interface SerializableLambda<T, R> extends Function<T, R>, Serializable {}

	@Test
	void contextLoads() {
		SerializableLambda<String, Boolean> lambda = (myString) -> {
			System.out.println("-----Inside Function-----" + myString);
			return true;
		};

		ExecuteFunctionAndCommit executeFunctionAndCommit = new ExecuteFunctionAndCommit("hello", lambda);
		byte[] bytes = SerializationUtils.serialize(executeFunctionAndCommit);

		ExecuteFunctionAndCommit executeFunctionAndCommitDe = (ExecuteFunctionAndCommit)
				SerializationUtils.deserialize(bytes);
	}

}

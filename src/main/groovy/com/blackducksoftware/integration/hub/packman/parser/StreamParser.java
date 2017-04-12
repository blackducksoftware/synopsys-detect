package com.blackducksoftware.integration.hub.packman.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class StreamParser<T> {

	public T parse(InputStream inputStream) {
		return parse(new InputStreamReader(inputStream));
	}

	public T parse(InputStreamReader inputStreamReader) {
		try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
			return parse(bufferedReader);
		} catch (IOException e) {
			// TODO: Log
			e.printStackTrace();
		}
		return null;
	}

	public abstract T parse(BufferedReader bufferedReader);

}

/**
 * Copyright [2012] [Datasalt Systems S.L.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datasalt.pangool.cogroup;

import java.util.Collection;


public class CoGrouperException extends Exception {

	private static final long serialVersionUID = 1L;

	public CoGrouperException(Throwable e) {
		super(e);
	}

	public CoGrouperException(String message, Throwable e) {
		super(message, e);
	}

	public CoGrouperException(String message) {
		super(message);
	}
	
	public static void failIfNull(Object ob, String message) throws CoGrouperException {
		if(ob == null) {
			throw new CoGrouperException(message);
		}
	}

	public static void failIfEmpty(Collection ob, String message) throws CoGrouperException {
		if(ob == null || ob.isEmpty()) {
			throw new CoGrouperException(message);
		}
	}
	
	public static void failIfEmpty(Object[] ob, String message) throws CoGrouperException {
		if(ob == null || ob.length == 0) {
			throw new CoGrouperException(message);
		}
	}

	public static void failIfNotNull(Object ob, String message) throws CoGrouperException {
		if(ob != null) {
			throw new CoGrouperException(message);
		}
	}
	
	
}
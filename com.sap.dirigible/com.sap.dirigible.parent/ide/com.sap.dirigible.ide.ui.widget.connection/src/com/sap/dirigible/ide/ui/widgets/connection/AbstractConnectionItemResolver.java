/*******************************************************************************
 * Copyright (c) 2014 SAP AG or an SAP affiliate company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *******************************************************************************/

package com.sap.dirigible.ide.ui.widgets.connection;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractConnectionItemResolver implements
		IConnectionItemResolver {

	private final Map<Object, Boolean> itemVisibilityCache = new HashMap<Object, Boolean>();

	private final Map<Object, Integer> itemLocationCache = new HashMap<Object, Integer>();

	@Override
	public boolean isItemVisible(Object item, boolean useCache) {
		Boolean result = itemVisibilityCache.get(item);
		if (result == null || !useCache) {
			result = evaluateItemVisibility(item, useCache);
			itemVisibilityCache.put(item, result);
		}
		return result;
	}

	public abstract boolean evaluateItemVisibility(Object item, boolean useCache);

	@Override
	public int getItemLocation(Object item, boolean useCache) {
		Integer result = itemLocationCache.get(item);
		if (result == null || !useCache) {
			result = evaluateItemLocation(item, useCache);
			itemLocationCache.put(item, result);
		}
		return result;
	}

	public abstract int evaluateItemLocation(Object item, boolean useCache);

	@Override
	public void clearCache() {
		itemVisibilityCache.clear();
		itemLocationCache.clear();
	}

}

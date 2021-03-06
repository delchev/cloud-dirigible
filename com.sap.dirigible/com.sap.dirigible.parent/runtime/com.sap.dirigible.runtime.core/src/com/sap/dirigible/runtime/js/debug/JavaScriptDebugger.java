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

package com.sap.dirigible.runtime.js.debug;

import javax.servlet.http.HttpServletRequest;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;

import com.sap.dirigible.repository.ext.debug.IDebugProtocol;

public class JavaScriptDebugger implements Debugger {

	private JavaScriptDebugFrame debugFrame = null;

	public JavaScriptDebugger(IDebugProtocol debugProtocol, HttpServletRequest request) {
		this.debugFrame = new JavaScriptDebugFrame(debugProtocol, request, this);
	}

	public DebugFrame getFrame(Context context, DebuggableScript fnOrScript) {
		context.setDebugger(this, fnOrScript);
		return debugFrame;
	}

	@Override
	public void handleCompilationDone(Context arg0, DebuggableScript arg1, String arg2) {
	}

}

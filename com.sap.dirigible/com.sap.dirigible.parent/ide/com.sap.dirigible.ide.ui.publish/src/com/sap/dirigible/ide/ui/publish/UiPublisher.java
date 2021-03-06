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

package com.sap.dirigible.ide.ui.publish;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import com.sap.dirigible.ide.common.CommonParameters;
import com.sap.dirigible.ide.publish.AbstractPublisher;
import com.sap.dirigible.ide.publish.IPublisher;
import com.sap.dirigible.ide.publish.PublishException;
import com.sap.dirigible.repository.api.ICollection;
import com.sap.dirigible.repository.api.ICommonConstants;

public class UiPublisher extends AbstractPublisher implements IPublisher {

	public UiPublisher() {
		super();
	}
	
	@Override
	public void publish(IProject project) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(
					project,
					ICommonConstants.WEB_CONTENT_REGISTRY_PUBLISH_LOCATION);
			final IFolder sourceFolder = getSourceFolder(project,
					ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT);
			copyAllFromTo(sourceFolder, targetContainer);
		} catch (Exception ex) {
			throw new PublishException(ex.getMessage(), ex);
		}
	}
	
	@Override
	public void activate(IProject project) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(
					project,
					getSandboxLocation());
			final IFolder sourceFolder = getSourceFolder(project,
					ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT);
			copyAllFromTo(sourceFolder, targetContainer);
		} catch (Exception ex) {
			throw new PublishException(ex.getMessage(), ex);
		}
	}
	
	@Override
	protected String getSandboxLocation() {
		return CommonParameters.getWebContentSandbox();
	}

	@Override
	public String getFolderType() {
		return ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT;
	}

	@Override
	public boolean recognizedFile(IFile file) {
		// any file under web content folder is valid
		return checkFolderType(file);
	}

	@Override
	public String getPublishedContainerMapping(IFile file) {
		return CommonParameters.WEB_CONTENT_CONTAINER_MAPPING;
	}
	
	@Override
	public String getActivatedContainerMapping(IFile file) {
		return CommonParameters.WEB_CONTENT_SANDBOX_MAPPING;
	}
	
	@Override
	public boolean isAutoActivationAllowed() {
		return true;
	}

}

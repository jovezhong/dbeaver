/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2024 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jkiss.dbeaver.ui.navigator.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.jkiss.dbeaver.model.DBPEvaluationContext;
import org.jkiss.dbeaver.model.DBPNamedObject;
import org.jkiss.dbeaver.model.DBPQualifiedObject;
import org.jkiss.dbeaver.model.navigator.DBNNode;
import org.jkiss.dbeaver.ui.CopyMode;
import org.jkiss.dbeaver.ui.internal.UINavigatorMessages;
import org.jkiss.dbeaver.utils.RuntimeUtils;

public class NavigatorHandlerCopySpecial extends NavigatorHandlerCopyAbstract {

    @Override
    protected CopyMode getCopyMode() {
        return CopyMode.ADVANCED;
    }

    @Override
    protected String getObjectDisplayString(Object object)
    {
        DBPQualifiedObject adapted = RuntimeUtils.getObjectAdapter(object, DBPQualifiedObject.class);
        if (adapted != null) {
            return adapted.getFullyQualifiedName(DBPEvaluationContext.UI);
        } else if (object instanceof DBNNode nwr) {
            IResource resource = nwr.getAdapter(IResource.class);
            if (resource != null) {
                return resource.getFullPath().toString();
            }
        }
        if (object instanceof DBPNamedObject no) {
            return no.getName();
        } else {
            return object == null ? null : object.toString();
        }
    }

    @Override
    protected String getSelectionTitle(IStructuredSelection selection)
    {
        return (selection.size() > 1 ?
                UINavigatorMessages.actions_navigator_copy_fqn_title :
                UINavigatorMessages.actions_navigator_copy_fqn_titles);
    }

}
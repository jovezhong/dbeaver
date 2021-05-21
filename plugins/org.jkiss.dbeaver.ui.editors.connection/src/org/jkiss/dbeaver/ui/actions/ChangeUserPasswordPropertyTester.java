/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2021 DBeaver Corp and others
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
package org.jkiss.dbeaver.ui.actions;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.IAdaptable;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.access.DBAUserChangePassword;
import org.jkiss.dbeaver.model.navigator.DBNDataSource;

public class ChangeUserPasswordPropertyTester extends PropertyTester {

    @Override
    public boolean test(Object element, String property, Object[] objects, Object o1) {
        if (!(element instanceof DBNDataSource)) {
            return false;
        }

        if (property.equals("canChangePassword")) {
            DBNDataSource dataSource = (DBNDataSource) element;
            DBPDataSource dbpDataSource = dataSource.getDataSourceContainer().getDataSource();
            if (dbpDataSource instanceof IAdaptable) {
                DBAUserChangePassword changePassword = ((IAdaptable) dbpDataSource).getAdapter(DBAUserChangePassword.class);
                return changePassword != null;
            }
        }
        return false;
    }
}

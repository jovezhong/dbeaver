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
package org.jkiss.dbeaver.ext.timeplus.ui;

import org.jkiss.dbeaver.ext.timeplus.model.TimeplusTableColumn;
import org.jkiss.dbeaver.model.struct.DBSTypedObject;
import org.jkiss.dbeaver.ui.data.IValueController;
import org.jkiss.dbeaver.ui.data.managers.EnumValueManager;

import java.util.List;

public class TimeplusEnumValueManager extends EnumValueManager {
    @Override
    protected boolean isMultiValue(IValueController controller) {
        return false;
    }

    @Override
    protected List<String> getEnumValues(IValueController controller) {
        final DBSTypedObject type = controller.getValueType();
        if (type instanceof TimeplusTableColumn) {
            return List.copyOf(((TimeplusTableColumn) type).getEnumEntries().keySet());
        }
        return null;
    }

    @Override
    protected List<String> getSetValues(IValueController controller, Object value) {
        return null;
    }
}

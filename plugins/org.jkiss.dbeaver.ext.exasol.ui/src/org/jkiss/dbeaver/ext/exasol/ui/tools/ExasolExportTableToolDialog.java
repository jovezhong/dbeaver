/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2016 Karl Griesser (fullref@gmail.com)
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
package org.jkiss.dbeaver.ext.exasol.ui.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchPartSite;
import org.jkiss.dbeaver.ext.exasol.model.ExasolTableBase;
import org.jkiss.dbeaver.ext.exasol.ui.ExasolUIConstants;
import org.jkiss.dbeaver.ext.exasol.ui.internal.ExasolMessages;
import org.jkiss.dbeaver.model.DBPEvaluationContext;
import org.jkiss.dbeaver.ui.UIUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class ExasolExportTableToolDialog extends ExasolBaseTableToolDialog {

	// Dialog artifacts
	private Combo cbRowSep;
	private Button btInclColNames;
	@SuppressWarnings("unused")
	private Button btSelectDirectory;
	private Button btSelectCompress;
	private Text txColSep;
	private Text txStringSep;
	private Text txFileName;
	private Combo cbStringSepMode;
	private Combo cbEncoding;
	private Label selectedDirectory;
	private String encoding;
	private String sepMode;
	private String rowSep;
	private String filename;

	public ExasolExportTableToolDialog(IWorkbenchPartSite partSite,
			final Collection<ExasolTableBase> selectedTables)
	{
		super(partSite, ExasolMessages.dialog_table_tools_export_title,
				selectedTables);
	}

	@Override
	protected void generateObjectCommand(List<String> sql, ExasolTableBase object)
	{
		StringBuilder sb = new StringBuilder(256);

		// Export String
		sb.append("EXPORT ");
		sb.append(object.getFullyQualifiedName(DBPEvaluationContext.DML));
		sb.append(" INTO LOCAL CSV FILE '");

		// directory was selected
		if (selectedDirectory.getText() != null)
			sb.append(selectedDirectory.getText());

		// name file like table
		sb.append(super.replaceVars(filename, object));
		sb.append(btSelectCompress.getSelection() ? ".csv.gz'" : ".csv'");
		
		// encoding
		sb.append(" ENCODING = '" + encoding + "'");
		
		// row separator
		sb.append(" ROW SEPARATOR = '" + rowSep + "'");
		
		// column separator
		sb.append(" COLUMN SEPARATOR = '" + txColSep.getText().replaceAll("'", "''") + "'");
		
		// string separator
		sb.append(" COLUMN DELIMITER = '" + txStringSep.getText().replaceAll("'", "''") + "'");
		
		
		
		// string del mode
		sb.append(" DELIMIT = " + sepMode);
		// include column headings
		if (btInclColNames.getSelection())
			sb.append(" WITH COLUMN NAMES");
		sql.add(sb.toString());

	}

	@Override
	protected void createControls(final Composite parent)
	{
		Group optionsGroup = UIUtils.createControlGroup(parent,
				ExasolMessages.dialog_table_tools_options, 1, 0, 0);
		optionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite composite = new Composite(optionsGroup, 2);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Directory select Button
		btSelectDirectory = UIUtils.createPushButton(composite,
				ExasolMessages.dialog_table_open_output_directory, null, new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final DirectoryDialog dialog = new DirectoryDialog(parent.getShell());
				final String directory = dialog.open();
				if (directory != null) {
					selectedDirectory.setVisible(true);
					selectedDirectory.setText(directory + File.separatorChar);
				} else {
					selectedDirectory.setVisible(false);
				}
				updateSQL();
			}
		});

		//label for selected directory
		selectedDirectory = UIUtils.createLabel(composite, "");
		selectedDirectory.setVisible(false);

		//file template
		filename = "${schema}_${table}_${date}";
		
		txFileName = UIUtils.createLabelText(composite, ExasolMessages.dialog_table_tools_file_template, filename);
		
		txFileName.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0)
			{
				filename = txFileName.getText();
				updateSQL();
			}
		});
		
		// compress output
		btSelectCompress = UIUtils.createCheckbox(composite,
				ExasolMessages.dialog_table_tools_export_compress, false);
		btSelectCompress.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateSQL();
			}
		});

        // PlaceHolder
		UIUtils.createPlaceholder(composite, 1);
		
		// include column headings
		btInclColNames = UIUtils.createCheckbox(composite, ExasolMessages.dialog_table_tools_column_heading, true);
		btInclColNames.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateSQL();
			}
		});
		
        // PlaceHolder
		UIUtils.createPlaceholder(composite, 1);

		// encoding combo
		cbEncoding = UIUtils.createLabelCombo(composite, ExasolMessages.dialog_table_tools_encoding, SWT.DROP_DOWN | SWT.READ_ONLY);
		
		for(String enc: ExasolUIConstants.encodings)
		{
			cbEncoding.add(enc);
		}
		cbEncoding.select(0);
		encoding = ExasolUIConstants.encodings.get(0);
		cbEncoding.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				encoding = ExasolUIConstants.encodings.get(cbEncoding.getSelectionIndex());
				updateSQL();
			}
		});
		
		//  row separator
		cbRowSep = UIUtils.createLabelCombo(composite, ExasolMessages.dialog_table_tools_row_sep_mode, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (String mode: ExasolUIConstants.rowSeparators)
		{
			cbRowSep.add(mode);
		}
		cbRowSep.select(0);
		rowSep = ExasolUIConstants.rowSeparators.get(0);
		cbRowSep.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				rowSep = ExasolUIConstants.rowSeparators.get(cbRowSep.getSelectionIndex());
				updateSQL();
			}
		});
		
		
		//  string sep mode
		cbStringSepMode = UIUtils.createLabelCombo(composite, ExasolMessages.dialog_table_tools_string_sep_mode, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (String mode: ExasolUIConstants.stringSepModes)
		{
			cbStringSepMode.add(mode);
		}
		cbStringSepMode.select(0);
		sepMode = ExasolUIConstants.stringSepModes.get(0);
		cbStringSepMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				sepMode = ExasolUIConstants.stringSepModes.get(cbStringSepMode.getSelectionIndex());
				updateSQL();
			}
		});
		
		// column sep
		txColSep = UIUtils.createLabelText(composite, ExasolMessages.dialog_table_tools_column_sep, ";");
		txColSep.setTextLimit(1);
		txColSep.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0)
			{
				updateSQL();
			}
		});
		// string sep
		txStringSep = UIUtils.createLabelText(composite, ExasolMessages.dialog_table_tools_string_sep, "\"");
		txStringSep.setTextLimit(1);
		txStringSep.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0)
			{
				updateSQL();
			}
		});

		createObjectsSelector(parent);

	}

}

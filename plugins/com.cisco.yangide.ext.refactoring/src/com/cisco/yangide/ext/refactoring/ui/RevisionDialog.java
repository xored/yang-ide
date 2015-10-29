/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.refactoring.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;

import com.cisco.yangide.ext.refactoring.nls.Messages;

/**
 * Displays calendar dialog to select revision.
 *
 * @author Konstantin Zaitsev
 * @date Aug 18, 2014
 */
public class RevisionDialog extends Dialog {

    /** Date format for revision. */
    static SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd", Locale.US); //$NON-NLS-1$

    /** Calendar widget. */
    private DateTime dateTime;

    /** Revision date. */
    private Date revision;

    /**
     * @param parentShell
     */
    protected RevisionDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setImage(RefactoringImages.getImage(RefactoringImages.IMG_CALENDAR));
        newShell.setText(Messages.RevisionDialog_title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite content = (Composite) super.createDialogArea(parent);
        dateTime = new DateTime(content, SWT.CALENDAR);
        Calendar cal = Calendar.getInstance();
        cal.setTime(revision);
        dateTime.setYear(cal.get(Calendar.YEAR));
        dateTime.setMonth(cal.get(Calendar.MONTH));
        dateTime.setDay(cal.get(Calendar.DATE));
        dateTime.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Calendar cal = Calendar.getInstance();
                cal.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
                revision = cal.getTime();
            }
        });
        return content;
    }

    /**
     * @return the revision
     */
    public String getRevision() {
        return DF.format(revision);
    }

    /**
     * @param revision the revision to set
     * @throws ParseException
     */
    public void setRevision(String revision) throws ParseException {
        this.revision = DF.parse(revision);
    }
}

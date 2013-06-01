package com.klwork.explorer.ui.handler;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class CommonFieldHandler {
	public static TextField createTextField(String caption) {
		TextField f = new TextField(caption);
		f.setNullRepresentation("");
		return f;
	}

	public static CheckBox createCheckBox(String caption) {
		CheckBox cb = new CheckBox(caption);
		cb.setImmediate(true);
		return cb;
	}
	
	
	public static TextArea createTextArea(String caption) {
        TextArea f = new TextArea(caption);
        f.setNullRepresentation("");
        return f;
    }
	
	public static DateField createDateField(String caption, boolean useSecondResolution) {
        DateField f = new DateField(caption);
        f.setDateFormat("yyyy-MM-dd HH:mm");
        f.setShowISOWeekNumbers(true);
        if (useSecondResolution) {
            f.setResolution(Resolution.SECOND);
        } else {
            f.setResolution(Resolution.MINUTE);
        }
        return f;
    }
}

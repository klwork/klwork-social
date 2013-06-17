package com.klwork.explorer.ui.handler;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Table;

public class BinderHandler {

	@SuppressWarnings("unchecked")
	public static <T> T getFieldGroupBean(FieldGroup fg) {
		BeanItem<T> item = (BeanItem<T>) fg.getItemDataSource();
		T event = item.getBean();
		return event;
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> T getTableBean(Table source, Object itemId) {
		BeanItem<T> item = (BeanItem<T>)source.getItem(itemId);
		T event = item.getBean();
		return event;
	}
}

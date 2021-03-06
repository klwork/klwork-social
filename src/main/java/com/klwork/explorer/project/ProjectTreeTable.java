package com.klwork.explorer.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedListener.TableListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTableFooterEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTableHeaderEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTableRowEvent;

import com.klwork.business.domain.model.Todo;
import com.klwork.business.domain.model.TodoQuery;
import com.klwork.business.domain.service.TodoService;
import com.klwork.common.utils.StringDateUtil;
import com.klwork.common.utils.StringTool;
import com.klwork.explorer.I18nManager;
import com.klwork.explorer.Messages;
import com.klwork.explorer.ViewToolManager;
import com.klwork.explorer.ui.Images;
import com.klwork.explorer.ui.event.SubmitEvent;
import com.klwork.explorer.ui.event.SubmitEventListener;
import com.klwork.explorer.ui.handler.CommonFieldHandler;
import com.klwork.fk.utils.SpringApplicationContextUtil;
import com.klwork.ui.security.LoginHandler;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

public class ProjectTreeTable extends CustomComponent {
	private static final long serialVersionUID = 7916755916967574384L;
	private String projectId;
	protected I18nManager i18nManager;

	ProjectMain main = null;
	HashMap<String, BeanItem<Todo>> inventoryStore = new HashMap<String, BeanItem<Todo>>();
	BeanItem<Todo> testBeanItem = null;
	Property<String> integerPropety = null;
	private final ArrayList<Object> visibleColumnIds = new ArrayList<Object>();
	private final ArrayList<String> visibleColumnLabels = new ArrayList<String>();

	// Map to find a field component by its item ID and property ID
	final HashMap<Object, HashMap<Object, Field>> fields = new HashMap<Object, HashMap<Object, Field>>();

	// Map to find the item ID of a field
	final HashMap<Field, Object> itemIds = new HashMap<Field, Object>();

	final TreeTable mainTreeTable = new TreeTable("我的周计划");

	private BeanItem<Todo> currentBeanItem;
	HierarchicalContainer hContainer = null;

	private FieldGroup scheduleEventFieldGroup = new FieldGroup();

	VerticalLayout bottomLayout;
	//
	TodoService todoService;

	public ProjectTreeTable(String prgId, ProjectMain projectMain) {
		System.out.println("ProjectTreeTable 初始化");
		this.i18nManager = ViewToolManager.getI18nManager();
		this.main = projectMain;
		this.projectId = prgId;
		todoService = (TodoService) SpringApplicationContextUtil.getContext()
				.getBean("todoService");
		init();
	}

	protected void init() {
		VerticalLayout layout = new VerticalLayout();
		setCompositionRoot(layout);
		layout.setSizeFull();

		initHead(layout);

		// 主界面
		initMain(layout);

	}

	@SuppressWarnings("unchecked")
	private Todo getFieldGroupTodo() {
		BeanItem<Todo> item = (BeanItem<Todo>) scheduleEventFieldGroup
				.getItemDataSource();
		Todo todo = item.getBean();
		initTodoProId(todo);
		return todo;
	}

	public void initTodoProId(Todo todo) {
		// WW_TODO 保存是设置项目id和用户
		if (!StringTool.judgeBlank(todo.getProId())) {
			todo.setProId(projectId);
		}
		if (todo.getAssignedUser() == null) {
			todo.setAssignedUser(LoginHandler.getLoggedInUser().getId());
		}
	}

	private void initHead(VerticalLayout layout) {
		// Header
		HorizontalLayout header = new HorizontalLayout();
		header.setWidth(100, Unit.PERCENTAGE);
		/*
		 * final Button saveButton = new Button(
		 * i18nManager.getMessage(Messages.PROFILE_SAVE));
		 * saveButton.setIcon(Images.SAVE); saveButton.addClickListener(new
		 * ClickListener() { public void buttonClick(ClickEvent event) {
		 * commit(); }
		 * 
		 * });
		 * 
		 * header.addComponent(saveButton);
		 * header.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);
		 */
		layout.addComponent(header);
		layout.setSpacing(true);
	}

	void initMain(VerticalLayout layout) {
		// 用一个panel包含，实现快捷键
		Panel panel = new Panel();
		panel.addActionHandler(new KbdHandler());
		// panel.setHeight(-1, Unit.PIXELS);
		layout.addComponent(panel);
		layout.setExpandRatio(panel, 1.0f);
		panel.setContent(mainTreeTable);

		// init tabletree
		mainTreeTable.setEditable(true);
		mainTreeTable.setImmediate(true);
		mainTreeTable.setWidth("100%");
		mainTreeTable.setHeight("500px");
		mainTreeTable.setColumnExpandRatio("name", 1);
		mainTreeTable.setSelectable(true);
		mainTreeTable.setColumnReorderingAllowed(true);

		// 数据构造
		hContainer = createTreeContent();
		mainTreeTable.setContainerDataSource(hContainer);

		// table的表现信息,绑定数据
		initTableField(mainTreeTable);

		// 拖动
		// handDrop(ttable);
		// 设置表头的显示(那些字段可见及其中文)
		setTableHeadDisplay(mainTreeTable);

		// 设置表行的双击操作，和其他监听
		setTableListener(mainTreeTable);

		Object hierarchyColumnId = "name";
		// 那个列为树形的
		mainTreeTable.setHierarchyColumn(hierarchyColumnId);

		// 右键功能
		rightClickHandler(mainTreeTable);
		mainTreeTable.setImmediate(true);
		// 展开所有节点
		collapsedAll(mainTreeTable);

	}

	private void collapsedAll(final TreeTable ttable) {
		for (Object item : ttable.getItemIds().toArray()) {
			collapsedSub(ttable, item);
		}
	}

	private void collapsedSub(final TreeTable ttable, Object item) {
		ttable.setCollapsed(item, false);
		if (ttable.hasChildren(item)) {
			for (Object a : ttable.getChildren(item).toArray()) {
				ttable.setCollapsed(a, false);
				collapsedSub(ttable, a);
			}
		}
	}

	private void setTableListener(final TreeTable ttable) {
		ttable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			private static final long serialVersionUID = -348059189217149508L;

			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					// Notification.show(event.getSource().toString());
					Object source = event.getItemId();
					HashMap<Object, Field> itemMap = fields.get(source);
					for (Field f : itemMap.values()) {
						f.focus();
					}
				}
			}
		});

		ttable.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				Object value = ttable.getValue();
				if (value instanceof BeanItem) {
					currentBeanItem = (BeanItem<Todo>) value;
				}
				// 同时更新下面的数据
				// updateBottomLayout(bottomLayout);
			}
		});

		addListener(new SubmitEventListener() {
			private static final long serialVersionUID = 1L;

			protected void submitted(SubmitEvent event) {
			}

			protected void cancelled(SubmitEvent event) {
			}
		});
	}

	private void setTableHeadDisplay(final TreeTable ttable) {

		visibleColumnIds.add("name");
		visibleColumnIds.add("priority");
		visibleColumnIds.add("complete");
		visibleColumnIds.add("endTime");
		visibleColumnIds.add("useUp");
		visibleColumnIds.add("due");
		visibleColumnIds.add("status");
		visibleColumnIds.add("tags");
		visibleColumnIds.add("type");
		visibleColumnIds.add("edit");

		visibleColumnLabels.add("标题");
		visibleColumnLabels.add("！");
		visibleColumnLabels.add("%");
		visibleColumnLabels.add("结束时间");
		visibleColumnLabels.add("耗尽");
		visibleColumnLabels.add("到期");
		visibleColumnLabels.add("状态");
		visibleColumnLabels.add("标签");
		visibleColumnLabels.add("类型");
		visibleColumnLabels.add("操作");

		ttable.addGeneratedColumn("edit", new ValueEditColumnGenerator());

		ttable.setVisibleColumns(visibleColumnIds.toArray());
		ttable.setColumnHeaders(visibleColumnLabels.toArray(new String[0]));

	}

	private void rightClickHandler(final TreeTable ttable) {
		ContextMenu tableContextMenu = new ContextMenu();
		tableContextMenu.addContextMenuTableListener(createOpenListener());
		tableContextMenu.addItem("新建子任务").addItemClickListener(
				new ContextMenu.ContextMenuItemClickListener() {
					@Override
					public void contextMenuItemClicked(
							ContextMenuItemClickEvent event) {
						Item parentItem = hContainer.getItem(currentBeanItem);
						Todo newTodo = todoService.newTodo();
						newTodo.setProId(projectId);
						BeanItem newbeanItem = new BeanItem<Todo>(newTodo);
						Item nItem = hContainer.addItem(newbeanItem);

						hContainer.setChildrenAllowed(newbeanItem, false);
						hContainer.setChildrenAllowed(currentBeanItem, true);
						copyBeanValueToContainer(hContainer, newbeanItem);
						// 设置父节点
						hContainer.setParent(newbeanItem, currentBeanItem);
						Todo paretTodo = currentBeanItem.getBean();
						// 新的记录设置为
						hContainer.getContainerProperty(newbeanItem, "pid")
								.setValue(paretTodo.getId());
						// 老的记录
						hContainer.getContainerProperty(currentBeanItem,
								"isContainer").setValue(1);

						ttable.setCollapsed(currentBeanItem, false);
						ttable.setImmediate(true);
					}

				});
		tableContextMenu.addItem("新建任务").addItemClickListener(
				new ContextMenu.ContextMenuItemClickListener() {
					@Override
					public void contextMenuItemClicked(
							ContextMenuItemClickEvent event) {
						Todo newTodo = todoService.newTodo();
						newTodo.setProId(projectId);
						BeanItem newbeanItem = new BeanItem<Todo>(newTodo);
						Item nItem = hContainer.addItem(newbeanItem);
						hContainer.setChildrenAllowed(newbeanItem, false);
						copyBeanValueToContainer(hContainer, newbeanItem);
						// 设置父节点
						hContainer.setParent(newbeanItem, null);

					}

				});
		tableContextMenu.setAsTableContextMenu(ttable);
	}

	private TableListener createOpenListener() {
		ContextMenuOpenedListener.TableListener openListener = new ContextMenuOpenedListener.TableListener() {

			@Override
			public void onContextMenuOpenFromRow(
					ContextMenuOpenedOnTableRowEvent event) {
				Object itemId = event.getItemId();
				if (itemId instanceof BeanItem) {
					currentBeanItem = (BeanItem<Todo>) itemId;
				}
				// contextMenu.open(event.getX(), event.getY());
			}

			@Override
			public void onContextMenuOpenFromHeader(
					ContextMenuOpenedOnTableHeaderEvent event) {

			}

			@Override
			public void onContextMenuOpenFromFooter(
					ContextMenuOpenedOnTableFooterEvent event) {

			}
		};
		return openListener;
	}

	private void openEdit(TreeTable ttable, final Object itemId) {
		HashMap<Object, Field> itemMap = fields.get(itemId);
		//
		for (Field f : itemMap.values())
			f.setReadOnly(false);
		ttable.select(itemId);
	}

	private void initTableField(final TreeTable ttable) {
		ttable.setTableFieldFactory(new TableFieldFactory() {
			private static final long serialVersionUID = -5741977060384915110L;

			public Field createField(Container container, Object itemId,
					final Object propertyId, Component uiContext) {
				TextField tf = null;
				if ("name".equals(propertyId)) {
					final BeanItem<Todo> beanItem = (BeanItem<Todo>) itemId;

					if (getTfFromCache(itemId, propertyId) != null) {
						tf = getTfFromCache(itemId, propertyId);
						// bindFieldToObje(itemId, propertyId, tf, beanItem);
						return tf;
					}

					tf = new TextField((String) propertyId);
					// bindFieldToObje(itemId, propertyId, tf, beanItem);
					// Needed for the generated column
					tf.setImmediate(true);
					tf.setReadOnly(true);
					tf.setWidth("100%");
					tf.addFocusListener(new FocusListener() {
						private static final long serialVersionUID = 1006388127259206641L;

						public void focus(FocusEvent event) {
							openEdit(ttable, beanItem);
						}

					});
					tf.addBlurListener(new BlurListener() {
						private static final long serialVersionUID = -4497552765206819985L;

						public void blur(BlurEvent event) {
							HashMap<Object, Field> itemMap = fields
									.get(beanItem);
							for (Field f : itemMap.values()) {// 所有字段只读
								f.setReadOnly(true);
							}

							// copy toBean
							Todo todo = tableItemToBean(currentBeanItem);
							
							
							List<Todo> l = new ArrayList();
							l.add(todo);
							todoService.saveTodoList(l);

							// reflashBottom();
						}

					});
					// 把name设置到cache中
					saveTfToCache(itemId, propertyId, tf);
				} else {
					tf = new TextField((String) propertyId);
					tf.setData(itemId);
					tf.setImmediate(true);
					// tf.setSizeFull();
					// tf.setSizeUndefined();
					tf.setWidth(50, Unit.PIXELS);
					tf.setReadOnly(true);
				}

				return tf;
			}

			private void bindFieldToObje(Object itemId,
					final Object propertyId, TextField tf,
					final BeanItem<Todo> beanItem) {
				BeanItem<Todo> f = inventoryStore.get(getBeanSign(beanItem));
				tf.setPropertyDataSource(f.getItemProperty(propertyId));
				System.out.println(propertyId + "---------" + itemId);
			}

			private void saveTfToCache(final Object itemId,
					final Object propertyId, TextField tf) {
				if (tf != null) {
					// Manage the field in the field storage
					HashMap<Object, Field> itemMap = fields.get(itemId);
					if (itemMap == null) {
						itemMap = new HashMap<Object, Field>();
						fields.put(itemId, itemMap);
					}
					itemMap.put(propertyId, tf);// 每个属性一个textfield
					itemIds.put(tf, itemId);
				}
			}

			private TextField getTfFromCache(Object itemId, Object propertyId) {
				TextField tf = null;

				// Manage the field in the field storage
				HashMap<Object, Field> itemMap = fields.get(itemId);
				if (itemMap == null) {
					itemMap = new HashMap<Object, Field>();
					fields.put(itemId, itemMap);
				}
				if (itemMap.get(propertyId) != null) {
					tf = (TextField) itemMap.get(propertyId);
				}
				return tf;
			}
		}

		);

	}

	/**
	 * table属性的创建
	 * 
	 * @return
	 */
	public HierarchicalContainer createTreeContent() {
		HierarchicalContainer container = new HierarchicalContainer();
		container.addContainerProperty("priority", String.class, "");
		container.addContainerProperty("complete", String.class, "");
		container.addContainerProperty("endTime", String.class, "");
		container.addContainerProperty("useUp", String.class, "");
		container.addContainerProperty("due", String.class, "");
		container.addContainerProperty("status", String.class, "");
		container.addContainerProperty("tags", String.class, "");
		container.addContainerProperty("type", String.class, "");
		container.addContainerProperty("name", String.class, "");
		container.addContainerProperty("id", String.class, "");
		container.addContainerProperty("pid", String.class, "");
		container.addContainerProperty("container", Boolean.class, null);
		container.addContainerProperty("isContainer", Integer.class, null);
		initContainerData(container, getQuery("-1"), null);
		return container;
	}

	private TodoQuery getQuery(String pid) {
		TodoQuery query = new TodoQuery();
		query.setProId(projectId).setPid(pid).setOrderBy(" pid asc,id asc");
		return query;
	}

	private void initContainerData(HierarchicalContainer container,
			TodoQuery query, BeanItem<Todo> parent) {
		List<Todo> beanList = todoService.findTodoByQueryCriteria(query, null);
		for (Iterator iterator = beanList.iterator(); iterator.hasNext();) {
			Todo todo = (Todo) iterator.next();
			BeanItem<Todo> newBeanItem = new BeanItem(todo);
			container.addItem(newBeanItem);
			container.setParent(newBeanItem, parent);
			copyBeanValueToContainer(container, newBeanItem);
			inventoryStore.put(getBeanSign(newBeanItem), newBeanItem);
			boolean isContainer = StringTool.parseBoolean(todo.getIsContainer()
					+ "");
			if (isContainer) {
				container.setChildrenAllowed(newBeanItem, true);
				initContainerData(container, getQuery(todo.getId()),
						newBeanItem);
			} else {
				container.setChildrenAllowed(newBeanItem, false);
			}
			/*
			 * if(parent != null){ mainTreeTable.setCollapsed(parent, false); }
			 */

		}
	}

	/**
	 * 把Bean的值copy到容器中
	 * 
	 * @param container
	 * @param newBeanItem
	 */
	public void copyBeanValueToContainer(HierarchicalContainer container,
			BeanItem<Todo> newBeanItem) {
		for (Object propertyId : container.getContainerPropertyIds()) {
			setContainerValueByBean(container, newBeanItem, propertyId);
		}
	}

	private String getBeanSign(BeanItem<Todo> beanItem) {
		return beanItem.getBean().getId() + "_" + beanItem.getBean().getName();
	}

	/**
	 * 给容器设置为bean的字
	 * 
	 * @param container
	 * @param beanItem
	 * @param propertyId
	 */
	private void setContainerValueByBean(HierarchicalContainer container,
			BeanItem<Todo> beanItem, Object propertyId) {
		Todo t = beanItem.getBean();
		String[] benTo = { "priority", "type", "tag", "name", "id", "pid" };
		for (int i = 0; i < benTo.length; i++) {
			if (benTo[i].equals(propertyId)) {
				Property itemProperty = beanItem.getItemProperty(propertyId);
				// bean的属性copy到其他
				/*
				 * Property itemProperty2 =
				 * container.getItem(beanItem).getItemProperty(propertyId);
				 * itemProperty2 .setValue(itemProperty.getValue());
				 */
				String newValue = itemProperty.getValue() + "";
				container.getContainerProperty(beanItem, propertyId).setValue(
						newValue);
			}
		}
		/*
		 * container.addContainerProperty("priority", String.class, "");
		 * container.addContainerProperty("complete", String.class, "");
		 * container.addContainerProperty("endTime", String.class, "");
		 * container.addContainerProperty("useUp", String.class, "");
		 * container.addContainerProperty("due", String.class, "");
		 * container.addContainerProperty("status", String.class, "");
		 * container.addContainerProperty("tags", String.class, "");
		 * container.addContainerProperty("type", String.class, "");
		 * container.addContainerProperty("name", String.class, "");
		 * container.addContainerProperty("container", Boolean.class, null);
		 */
		if ("isContainer".equals(propertyId)) {
			container.getContainerProperty(beanItem, propertyId).setValue(
					t.getIsContainer());
		}

		if ("container".equals(propertyId)) {
			boolean v = StringTool.parseBoolean(t.getIsContainer() + "");
			container.getContainerProperty(beanItem, propertyId).setValue(v);
		}

		if ("due".equals(propertyId)) {
			String v = StringDateUtil.dateToYMDString(new Date());
			container.getContainerProperty(beanItem, propertyId).setValue(v);
		}
	}

	/**
	 * 提交table的所有内容
	 */
	public void allCommit() {
		mainTreeTable.commit();
		List<Todo> beanList = tableDataBeanList();
		todoService.saveTodoList(beanList);
	}

	// 表格的展开数据，变成Bean，getItemIds只包含指定的数据
	private List<Todo> tableDataBeanList() {
		List<Todo> beanList = new ArrayList<Todo>();

		Collection<?> list = mainTreeTable.getItemIds();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {// table记录
			BeanItem<Todo> beanItem = (BeanItem) iterator.next();

			Todo todo = tableItemToBean(beanItem);
			initTodoProId(todo);
			beanList.add(todo);
			System.out.println("--------------" + todo);

		}
		return beanList;
	}

	/**
	 * 将table的数据复制到bing中
	 * 
	 * @param beanItem
	 * @return
	 */
	public Todo tableItemToBean(BeanItem<Todo> beanItem) {
		String[] benTo = { "name", "id", "pid", "isContainer" };
		for (int i = 0; i < benTo.length; i++) {// 需要进行赋值的
			Property itemProperty = beanItem.getItemProperty(benTo[i]);
			Object newValue = mainTreeTable.getContainerDataSource()
					.getContainerProperty(beanItem, benTo[i]).getValue();
			itemProperty.setValue(newValue);
		}
		Todo todo = beanItem.getBean();
		initTodoProId(todo);
		return todo;
	}

	// Keyboard navigation
	class KbdHandler implements com.vaadin.event.Action.Handler {
		private static final long serialVersionUID = -2993496725114954915L;
		Action f2 = new ShortcutAction("F2", ShortcutAction.KeyCode.F2, null);

		@Override
		public Action[] getActions(Object target, Object sender) {
			return new Action[] { f2 };
		}

		@Override
		public void handleAction(Action action, Object sender, Object target) {
			System.out.println("sdfdf");
			if (target instanceof TreeTable) {
				// Object itemid = ((TextField) target).getData();
				HashMap<Object, Field> itemMap = fields.get(currentBeanItem);
				for (Field f : itemMap.values()) {
					f.focus();
				}
			}
		}
	}

	public class ValueEditColumnGenerator implements ColumnGenerator {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5950078454864053894L;

		@Override
		public Object generateCell(Table source, final Object itemId, Object columnId) {
			
			Button editButton = new Button("");
			editButton.addStyleName(Reindeer.BUTTON_LINK);
			editButton.setIcon(Images.EDIT);
			editButton.addClickListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					// WW_TODO 后台进行修改
					EditTodoPopupWindow editTodoPop = new EditTodoPopupWindow(
							(BeanItem<Todo>) itemId,projectId);
					editTodoPop.addListener(new SubmitEventListener() {
					    private static final long serialVersionUID = 1L;
					
						@Override
						protected void cancelled(SubmitEvent event) {
							
						}

						@Override
						protected void submitted(SubmitEvent event) {
							if(event.getData() != null){
								//将值copy回来，进行日历刷新
								copyBeanValueToContainer(hContainer,(BeanItem<Todo>) event.getData());
								main.refreshCalendarView();
							}
						}
					});

					ViewToolManager.showPopupWindow(editTodoPop);
				}
			});
			return editButton;
		}

	}
}

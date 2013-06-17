package com.klwork.explorer.project;

import com.klwork.business.domain.model.OutsourcingProject;
import com.klwork.explorer.data.LazyLoadingContainer;
import com.klwork.explorer.data.LazyLoadingQuery;
import com.klwork.explorer.ui.Images;
import com.klwork.explorer.ui.custom.DetailPanel;
import com.klwork.explorer.ui.event.SubmitEvent;
import com.klwork.explorer.ui.event.SubmitEventListener;
import com.klwork.explorer.ui.handler.BinderHandler;
import com.klwork.explorer.ui.handler.CommonFieldHandler;
import com.klwork.explorer.ui.handler.TableHandler;
import com.klwork.explorer.ui.mainlayout.ExplorerLayout;
import com.klwork.explorer.ui.util.ThemeImageColumnGenerator;
import com.vaadin.data.Item;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class OutProjectListRight extends DetailPanel {
	protected VerticalLayout centralLayout;

	public OutProjectListRight() {
		super();
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	public Component getSearchComponent() {
		return new ProjectSearchPanel();
	}

	protected void init() {
		setSizeFull();
		addStyleName(Reindeer.LAYOUT_WHITE);

		// Central panel: all task data
		centralLayout = new VerticalLayout();
		centralLayout.setMargin(true);
		setDetailContainer(centralLayout);

		Component searchComponent = getSearchComponent();
		centralLayout.addComponent(searchComponent);

		// 增加一个分隔线
		addDetailComponent(CommonFieldHandler.getSpacer());

		Label title = new Label("所有公共项目");
		title.addStyleName(ExplorerLayout.STYLE_H3);
		title.setWidth(100, Unit.PERCENTAGE);
		centralLayout.addComponent(title);

		Table listTable = new Table();
		listTable.addStyleName(ExplorerLayout.STYLE_TASK_LIST);
		listTable.addStyleName(ExplorerLayout.STYLE_SCROLLABLE);
		listTable.setWidth(100, Unit.PERCENTAGE);
		listTable.setHeight(100, Unit.PERCENTAGE);
		centralLayout.addComponent(listTable);
		centralLayout.setExpandRatio(listTable, 1);

		LazyLoadingQuery lazyLoadingQuery = new PublicProjectListQuery();
		LazyLoadingContainer listContainer = new LazyLoadingContainer(
				lazyLoadingQuery, 10);
		listTable.setContainerDataSource(listContainer);
		if (lazyLoadingQuery.size() < 10) {
			listTable.setPageLength(0);
		} else {
			listTable.setPageLength(10);
		}

		// Create column header
		listTable.addGeneratedColumn("icon", new ThemeImageColumnGenerator(
				Images.TASK_22));
		listTable.setColumnWidth("icon", 22);

		listTable.addContainerProperty("name", String.class, "");
		listTable.addGeneratedColumn("edit", new ProjectEditColumnGenerator());

		TableHandler.setTableNoHead(listTable);
		listTable.setImmediate(false);
	}

	public class ProjectEditColumnGenerator implements ColumnGenerator {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5950078454864053894L;

		@Override
		public Object generateCell(final Table source, final Object itemId,
				Object columnId) {
			OutsourcingProject project = BinderHandler.getTableBean(source, itemId);
			GridLayout grid = new GridLayout(2, 3);
			grid.addStyleName(Reindeer.SPLITPANEL_SMALL);
			//grid.setMargin(true);
			grid.setMargin(new MarginInfo(true, false, true, false));
			//加点空
			grid.setSpacing(true);
			grid.setSizeFull();

			Label title = new Label(project.getName());
			title.addStyleName(ExplorerLayout.STYLE_H3);
			title.setWidth(100, Unit.PERCENTAGE);
			grid.addComponent(title, 0, 0, 1, 0);

			String amontTitle = "金额:" + project.getBounty();
			Label title2 = new Label(amontTitle);
			title2.addStyleName(ExplorerLayout.STYLE_H4);
			grid.addComponent(title2, 0, 1);
			
			

			Button addButton = new Button("加入");
			addButton.addStyleName(Reindeer.BUTTON_SMALL);
			grid.addComponent(addButton, 0, 2);
			addButton.setDisableOnClick(true);
			
			Button editButton = new Button("提交作品");
			grid.addComponent(editButton, 1, 2);
			editButton.addStyleName(Reindeer.BUTTON_SMALL);
			// editButton.setDisableOnClick(true);
			editButton.addClickListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					// WW_TODO 后台进行修改
					Item item = source.getItem(itemId);
					String id = (String) item.getItemProperty("id").getValue();
					NewProjectWindow newProjectWindow = new NewProjectWindow(id);

					newProjectWindow.addListener(new SubmitEventListener() {
						private static final long serialVersionUID = 1L;

						@Override
						protected void cancelled(SubmitEvent event) {

						}

						@Override
						protected void submitted(SubmitEvent event) {
							if (event.getData() != null) {

							}
						}
					});

					// ViewToolManager.showPopupWindow(newProjectWindow);
				}
			});
			return grid;
		}

	}
}

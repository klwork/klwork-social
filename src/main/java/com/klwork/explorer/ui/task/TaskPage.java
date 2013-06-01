/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.klwork.explorer.ui.task;

import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;

import com.klwork.explorer.data.LazyLoadingContainer;
import com.klwork.explorer.data.LazyLoadingQuery;
import com.klwork.explorer.navigation.UriFragment;
import com.klwork.explorer.ui.AbstractTablePage;
import com.klwork.explorer.ui.Images;
import com.klwork.explorer.ui.custom.TaskListHeader;
import com.klwork.explorer.ui.custom.ToolBar;
import com.klwork.explorer.ui.mainlayout.ExplorerLayout;
import com.klwork.explorer.ui.util.ThemeImageColumnGenerator;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;


/**
 * Abstract super class for all task pages (inbox, queued, archived, etc.),
 * Builds up the default UI: task list on the left, central panel and events on the right.
 * 
 * @author Joram Barrez
 */
public abstract class TaskPage extends AbstractTablePage {
  
  private static final long serialVersionUID = 1L;

  protected transient TaskService taskService;

  protected String taskId;
  protected Table taskTable;
  protected LazyLoadingContainer taskListContainer;
  protected LazyLoadingQuery lazyLoadingQuery;
  protected TaskEventsPanel taskEventPanel;
  
  
  public TaskPage() {
    taskService =  ProcessEngines.getDefaultProcessEngine().getTaskService();
  }
  
  public TaskPage(String taskId) {
    this();
    this.taskId = taskId;
  }
  
  @Override
  protected void initUi() {
    super.initUi();
    if (taskId == null) {
      //WW_TODO 没有任务显示第一个
      selectElement(0);
    } else {
      //懒加载容器
      int index = taskListContainer.getIndexForObjectId(taskId);
      selectElement(index);
    }
    
    if (taskListContainer.size() == 0) {
      //ExplorerApp.get().setCurrentUriFragment(getUriFragment(null));
    }
  }
  
  @Override
  protected ToolBar createMenuBar() {
    return new TaskMenuBar();
  }
  
  @Override
  protected Table createList() {
	//WW_TODO taskpage 的createList,右边的列
    taskTable = new Table();
    taskTable.addStyleName(ExplorerLayout.STYLE_TASK_LIST);
    taskTable.addStyleName(ExplorerLayout.STYLE_SCROLLABLE);
    
    // Listener to change right panel when clicked on a task
    taskTable.addValueChangeListener(getListSelectionListener());
    
    this.lazyLoadingQuery = createLazyLoadingQuery();
    this.taskListContainer = new LazyLoadingContainer(lazyLoadingQuery, 10);
    taskTable.setContainerDataSource(taskListContainer);
    
    // Create column header
    taskTable.addGeneratedColumn("icon", new ThemeImageColumnGenerator(Images.TASK_22));
    taskTable.setColumnWidth("icon", 22);
    
    taskTable.addContainerProperty("name", String.class, null);
    taskTable.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
    
    return taskTable;
  }
  
  protected ValueChangeListener getListSelectionListener() {
    return new Property.ValueChangeListener() {
      private static final long serialVersionUID = 1L;
      public void valueChange(ValueChangeEvent event) {
        Item item = taskTable.getItem(event.getProperty().getValue()); // the value of the property is the itemId of the table entry
        
        if(item != null) {
          String id = (String) item.getItemProperty("id").getValue();
          //WW_TODO 点击用户任务table,设置详细
          setDetailComponent(createDetailComponent(id));
          //taskEventPanel.setTaskId(task.getId());
        //WW_TODO UriFragment处理?
          //UriFragment taskFragment = getUriFragment(id);
          //ExplorerApp.get().setCurrentUriFragment(taskFragment);
        } else {
          // Nothing is selected
          setDetailComponent(null);
         // ExplorerApp.get().setCurrentUriFragment(getUriFragment(null));
        }
      }
    };
  }
  
  protected Component createDetailComponent(String id) {
    Task task = taskService.createTaskQuery().taskId(id).singleResult();
    Component detailComponent = new TaskDetailPanel(task, TaskPage.this);
    //WW_TODO 联动任务事件平台
    //taskEventPanel.setTaskId(task.getId());
    return detailComponent;
  }
  
  @Override
  protected Component getEventComponent() {
    return getTaskEventPanel();
  }
  
  public TaskEventsPanel getTaskEventPanel() {
    if(taskEventPanel == null) {
      taskEventPanel = new TaskEventsPanel();
    }
    return taskEventPanel;
  }
  
  @Override
  public Component getSearchComponent() {
    return new TaskListHeader();
  } 
  
  protected abstract LazyLoadingQuery createLazyLoadingQuery();
  
  protected abstract UriFragment getUriFragment(String taskId);
  
}

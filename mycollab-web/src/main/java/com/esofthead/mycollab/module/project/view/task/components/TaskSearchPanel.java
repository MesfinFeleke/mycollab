/**
 * This file is part of mycollab-web.
 *
 * mycollab-web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-web.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.module.project.view.task.components;

import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.core.arguments.NumberSearchField;
import com.esofthead.mycollab.core.arguments.StringSearchField;
import com.esofthead.mycollab.core.db.query.Param;
import com.esofthead.mycollab.core.db.query.SearchFieldInfo;
import com.esofthead.mycollab.eventmanager.EventBusFactory;
import com.esofthead.mycollab.module.project.CurrentProjectVariables;
import com.esofthead.mycollab.module.project.ProjectTypeConstants;
import com.esofthead.mycollab.module.project.domain.criteria.TaskSearchCriteria;
import com.esofthead.mycollab.module.project.events.TaskEvent;
import com.esofthead.mycollab.module.project.ui.ProjectAssetsManager;
import com.esofthead.mycollab.module.project.view.milestone.MilestoneListSelect;
import com.esofthead.mycollab.module.project.view.settings.component.ProjectMemberListSelect;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.web.ui.DefaultGenericSearchPanel;
import com.esofthead.mycollab.vaadin.web.ui.DynamicQueryParamLayout;
import com.esofthead.mycollab.vaadin.web.ui.SavedFilterComboBox;
import com.esofthead.mycollab.vaadin.web.ui.UIConstants;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import java.util.List;

/**
 * @author MyCollab Ltd.
 * @since 4.0.0
 */
public class TaskSearchPanel extends DefaultGenericSearchPanel<TaskSearchCriteria> {
    private static final long serialVersionUID = 1L;
    protected TaskSearchCriteria searchCriteria;

    private TaskSavedFilterComboBox savedFilterComboBox;

    private static Param[] paramFields = new Param[]{
            TaskSearchCriteria.p_assignee, TaskSearchCriteria.p_createtime,
            TaskSearchCriteria.p_duedate, TaskSearchCriteria.p_lastupdatedtime,
            TaskSearchCriteria.p_status, TaskSearchCriteria.p_startdate, TaskSearchCriteria.p_enddate,
            TaskSearchCriteria.p_actualstartdate, TaskSearchCriteria.p_actualenddate,
            TaskSearchCriteria.p_milestoneId, TaskSearchCriteria.p_taskkey};

    @Override
    protected ComponentContainer buildSearchTitle() {
        savedFilterComboBox = new TaskSavedFilterComboBox();
        savedFilterComboBox.addQuerySelectListener(new SavedFilterComboBox.QuerySelectListener() {
            @Override
            public void querySelect(SavedFilterComboBox.QuerySelectEvent querySelectEvent) {
                List<SearchFieldInfo> fieldInfos = querySelectEvent.getSearchFieldInfos();
                TaskSearchCriteria criteria = SearchFieldInfo.buildSearchCriteria(TaskSearchCriteria.class,
                        fieldInfos);
                criteria.setProjectid(new NumberSearchField(CurrentProjectVariables.getProjectId()));
                EventBusFactory.getInstance().post(new TaskEvent.SearchRequest(TaskSearchPanel.this, criteria));
            }
        });
        Label taskIcon = new Label(ProjectAssetsManager.getAsset(ProjectTypeConstants.TASK).getHtml(), ContentMode.HTML);
        taskIcon.addStyleName(ValoTheme.LABEL_H2);
        taskIcon.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        taskIcon.setWidthUndefined();
        return new MHorizontalLayout(taskIcon, savedFilterComboBox).expand(savedFilterComboBox).alignAll(Alignment.MIDDLE_LEFT);
    }

    @Override
    public void setTotalCountNumber(int countNumber) {
        savedFilterComboBox.setTotalCountNumber(countNumber);
    }

    @Override
    protected void buildExtraControls() {
    }

    @Override
    protected SearchLayout<TaskSearchCriteria> createBasicSearchLayout() {
        return new TaskBasicSearchLayout();
    }

    @Override
    protected SearchLayout<TaskSearchCriteria> createAdvancedSearchLayout() {
        return new TaskAdvancedSearchLayout();
    }

    public void setTextField(String name) {
        if (getCompositionRoot() instanceof TaskBasicSearchLayout) {
            ((TaskBasicSearchLayout) getCompositionRoot()).setNameField(name);
        }
    }

    public void selectQueryInfo(String queryId) {
        savedFilterComboBox.selectQueryInfo(queryId);
    }

    private class TaskBasicSearchLayout extends BasicSearchLayout<TaskSearchCriteria> {
        private static final long serialVersionUID = 1L;
        private TextField nameField;
        private CheckBox myItemCheckbox;

        public TaskBasicSearchLayout() {
            super(TaskSearchPanel.this);
        }

        public void setNameField(String value) {
            this.nameField.setValue(value);
        }

        @Override
        public ComponentContainer constructBody() {
            MHorizontalLayout basicSearchBody = new MHorizontalLayout().withMargin(true);

            Label nameLbl = new Label("Name:");
            basicSearchBody.with(nameLbl).withAlign(nameLbl, Alignment.MIDDLE_LEFT);

            this.nameField = new TextField();
            this.nameField.setInputPrompt("Query by task name");
            this.nameField.setWidth(UIConstants.DEFAULT_CONTROL_WIDTH);
            basicSearchBody.with(nameField).withAlign(nameField, Alignment.MIDDLE_CENTER);

            this.myItemCheckbox = new CheckBox(AppContext.getMessage(GenericI18Enum.SEARCH_MYITEMS_CHECKBOX));
            basicSearchBody.with(myItemCheckbox).withAlign(myItemCheckbox, Alignment.MIDDLE_CENTER);

            Button searchBtn = new Button(AppContext.getMessage(GenericI18Enum.BUTTON_SEARCH));
            searchBtn.setIcon(FontAwesome.SEARCH);
            searchBtn.setStyleName(UIConstants.BUTTON_ACTION);
            searchBtn.addClickListener(new Button.ClickListener() {
                private static final long serialVersionUID = 1L;

                @Override
                public void buttonClick(final ClickEvent event) {
                    callSearchAction();
                }
            });
            basicSearchBody.with(searchBtn).withAlign(searchBtn, Alignment.MIDDLE_LEFT);

            Button cancelBtn = new Button(AppContext.getMessage(GenericI18Enum.BUTTON_CLEAR));
            cancelBtn.setStyleName(UIConstants.THEME_GRAY_LINK);
            cancelBtn.addClickListener(new Button.ClickListener() {
                private static final long serialVersionUID = 1L;

                @Override
                public void buttonClick(final ClickEvent event) {
                    nameField.setValue("");
                }
            });
            basicSearchBody.with(cancelBtn).withAlign(cancelBtn, Alignment.MIDDLE_CENTER);

            Button advancedSearchBtn = new Button(AppContext.getMessage(GenericI18Enum.BUTTON_ADVANCED_SEARCH), new Button.ClickListener() {
                private static final long serialVersionUID = 1L;

                @Override
                public void buttonClick(final ClickEvent event) {
                    moveToAdvancedSearchLayout();
                }
            });
            advancedSearchBtn.setStyleName(UIConstants.BUTTON_LINK);

            basicSearchBody.with(advancedSearchBtn).withAlign(advancedSearchBtn, Alignment.MIDDLE_CENTER);
            return basicSearchBody;
        }

        @Override
        protected TaskSearchCriteria fillUpSearchCriteria() {
            searchCriteria = new TaskSearchCriteria();
            searchCriteria.setProjectid(new NumberSearchField(CurrentProjectVariables.getProjectId()));
            searchCriteria.setTaskName(StringSearchField.and(this.nameField.getValue().trim()));
            if (this.myItemCheckbox.getValue()) {
                searchCriteria.setAssignUser(StringSearchField.and(AppContext.getUsername()));
            } else {
                searchCriteria.setAssignUser(null);
            }
            return searchCriteria;
        }

        @Override
        public ComponentContainer constructHeader() {
            return TaskSearchPanel.this.constructHeader();
        }

    }

    private class TaskAdvancedSearchLayout extends DynamicQueryParamLayout<TaskSearchCriteria> {
        private static final long serialVersionUID = 1L;

        public TaskAdvancedSearchLayout() {
            super(TaskSearchPanel.this, ProjectTypeConstants.TASK);
        }

        @Override
        public ComponentContainer constructHeader() {
            return TaskSearchPanel.this.constructHeader();
        }

        @Override
        protected Class<TaskSearchCriteria> getType() {
            return TaskSearchCriteria.class;
        }

        @Override
        public Param[] getParamFields() {
            return paramFields;
        }

        @Override
        protected Component buildSelectionComp(String fieldId) {
            if ("task-assignuser".equals(fieldId)) {
                return new ProjectMemberListSelect(false);
            } else if ("task-milestone".equals(fieldId)) {
                return new MilestoneListSelect();
            } else if ("task-status".equals(fieldId)) {
                return new TaskStatusListSelect();
            }
            return null;
        }

        @Override
        protected TaskSearchCriteria fillUpSearchCriteria() {
            searchCriteria = super.fillUpSearchCriteria();
            searchCriteria.setProjectid(new NumberSearchField(CurrentProjectVariables.getProjectId()));
            return searchCriteria;
        }
    }
}
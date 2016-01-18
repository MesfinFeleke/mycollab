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
package com.esofthead.mycollab.module.crm.view.lead;

import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.core.arguments.NumberSearchField;
import com.esofthead.mycollab.core.arguments.SearchCriteria;
import com.esofthead.mycollab.core.arguments.SetSearchField;
import com.esofthead.mycollab.core.arguments.StringSearchField;
import com.esofthead.mycollab.core.db.query.Param;
import com.esofthead.mycollab.eventmanager.EventBusFactory;
import com.esofthead.mycollab.module.crm.CrmTypeConstants;
import com.esofthead.mycollab.module.crm.domain.criteria.LeadSearchCriteria;
import com.esofthead.mycollab.module.crm.events.LeadEvent;
import com.esofthead.mycollab.module.crm.i18n.LeadI18nEnum;
import com.esofthead.mycollab.module.crm.ui.components.ComponentUtils;
import com.esofthead.mycollab.module.user.ui.components.ActiveUserListSelect;
import com.esofthead.mycollab.security.RolePermissionCollections;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.HeaderWithFontAwesome;
import com.esofthead.mycollab.vaadin.web.ui.DefaultGenericSearchPanel;
import com.esofthead.mycollab.vaadin.web.ui.DynamicQueryParamLayout;
import com.esofthead.mycollab.vaadin.web.ui.ShortcutExtension;
import com.esofthead.mycollab.vaadin.web.ui.UIConstants;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.viritin.layouts.MHorizontalLayout;

/**
 * @author MyCollab Ltd.
 * @since 1.0
 */
public class LeadSearchPanel extends DefaultGenericSearchPanel<LeadSearchCriteria> {
    private static final long serialVersionUID = 1L;

    private static Param[] paramFields = new Param[]{
            LeadSearchCriteria.p_leadContactName,
            LeadSearchCriteria.p_accountName, LeadSearchCriteria.p_website,
            LeadSearchCriteria.p_anyEmail, LeadSearchCriteria.p_anyPhone,
            LeadSearchCriteria.p_billingCountry,
            LeadSearchCriteria.p_shippingCountry,
            LeadSearchCriteria.p_statuses, LeadSearchCriteria.p_sources,
            LeadSearchCriteria.p_assignee};

    @Override
    protected HeaderWithFontAwesome buildSearchTitle() {
        return ComponentUtils.header(CrmTypeConstants.LEAD, AppContext.getMessage(LeadI18nEnum.VIEW_LIST_TITLE));
    }

    @Override
    protected void buildExtraControls() {
        Button createLeadBtn = new Button(AppContext.getMessage(LeadI18nEnum.BUTTON_NEW_LEAD), new Button.ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                EventBusFactory.getInstance().post(new LeadEvent.GotoAdd(this, null));
            }
        });
        createLeadBtn.setIcon(FontAwesome.PLUS);
        createLeadBtn.setStyleName(UIConstants.BUTTON_ACTION);
        createLeadBtn.setEnabled(AppContext.canWrite(RolePermissionCollections.CRM_LEAD));
        this.addHeaderRight(createLeadBtn);
    }

    @Override
    protected BasicSearchLayout<LeadSearchCriteria> createBasicSearchLayout() {
        return new LeadBasicSearchLayout();
    }

    @Override
    protected SearchLayout<LeadSearchCriteria> createAdvancedSearchLayout() {
        return new LeadAdvancedSearchLayout();
    }

    private class LeadBasicSearchLayout extends BasicSearchLayout {
        private TextField nameField;
        private CheckBox myItemCheckbox;

        public LeadBasicSearchLayout() {
            super(LeadSearchPanel.this);
        }

        @Override
        public ComponentContainer constructHeader() {
            return LeadSearchPanel.this.constructHeader();
        }

        @Override
        public ComponentContainer constructBody() {
            MHorizontalLayout layout = new MHorizontalLayout().withMargin(true);

            nameField = ShortcutExtension.installShortcutAction(new TextField(),
                    new ShortcutListener("LeadSearchRequest", ShortcutAction.KeyCode.ENTER, null) {
                        @Override
                        public void handleAction(Object o, Object o1) {
                            callSearchAction();
                        }
                    });
            nameField.setInputPrompt("Query by lead name");
            nameField.setWidth(UIConstants.DEFAULT_CONTROL_WIDTH);
            layout.with(nameField).withAlign(nameField, Alignment.MIDDLE_CENTER);

            myItemCheckbox = new CheckBox(AppContext.getMessage(GenericI18Enum.SEARCH_MYITEMS_CHECKBOX));
            layout.with(myItemCheckbox).withAlign(myItemCheckbox, Alignment.MIDDLE_CENTER);

            Button searchBtn = new Button(AppContext.getMessage(GenericI18Enum.BUTTON_SEARCH));
            searchBtn.setStyleName(UIConstants.BUTTON_ACTION);
            searchBtn.setIcon(FontAwesome.SEARCH);

            searchBtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final ClickEvent event) {
                    callSearchAction();
                }
            });
            layout.with(searchBtn).withAlign(searchBtn, Alignment.MIDDLE_LEFT);

            Button cancelBtn = new Button(AppContext.getMessage(GenericI18Enum.BUTTON_CLEAR));
            cancelBtn.setStyleName(UIConstants.THEME_GRAY_LINK);
            cancelBtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final ClickEvent event) {
                    nameField.setValue("");
                }
            });
            layout.with(cancelBtn).withAlign(cancelBtn, Alignment.MIDDLE_CENTER);

            Button advancedSearchBtn = new Button(AppContext.getMessage(GenericI18Enum.BUTTON_ADVANCED_SEARCH), new Button.ClickListener() {

                @Override
                public void buttonClick(final ClickEvent event) {
                    moveToAdvancedSearchLayout();
                }
            });
            advancedSearchBtn.setStyleName(UIConstants.BUTTON_LINK);
            layout.with(advancedSearchBtn).withAlign(advancedSearchBtn, Alignment.MIDDLE_CENTER);
            return layout;
        }

        @Override
        protected SearchCriteria fillUpSearchCriteria() {
            LeadSearchCriteria searchCriteria = new LeadSearchCriteria();
            searchCriteria.setSaccountid(new NumberSearchField(AppContext.getAccountId()));

            if (StringUtils.isNotBlank(nameField.getValue().trim())) {
                searchCriteria.setLeadName(StringSearchField.and(nameField.getValue()));
            }

            if (myItemCheckbox.getValue()) {
                searchCriteria.setAssignUsers(new SetSearchField<>(AppContext.getUsername()));
            } else {
                searchCriteria.setAssignUsers(null);
            }
            return searchCriteria;
        }
    }

    private class LeadAdvancedSearchLayout extends DynamicQueryParamLayout<LeadSearchCriteria> {

        public LeadAdvancedSearchLayout() {
            super(LeadSearchPanel.this, CrmTypeConstants.LEAD);
        }

        @Override
        public ComponentContainer constructHeader() {
            return LeadSearchPanel.this.constructHeader();
        }

        @Override
        public Param[] getParamFields() {
            return paramFields;
        }

        @Override
        protected Class<LeadSearchCriteria> getType() {
            return LeadSearchCriteria.class;
        }

        @Override
        protected Component buildSelectionComp(String fieldId) {
            if ("lead-assignuser".equals(fieldId)) {
                return new ActiveUserListSelect();
            }
            return null;
        }
    }
}

/**
 * This file is part of mycollab-mobile.
 *
 * mycollab-mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-mobile.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.mobile.module.crm.view.account;

import com.esofthead.mycollab.mobile.form.view.DynaFormLayout;
import com.esofthead.mycollab.mobile.module.crm.ui.AbstractEditItemComp;
import com.esofthead.mycollab.module.crm.CrmTypeConstants;
import com.esofthead.mycollab.module.crm.domain.SimpleAccount;
import com.esofthead.mycollab.vaadin.mvp.ViewComponent;
import com.esofthead.mycollab.vaadin.ui.AbstractBeanFieldGroupEditFieldFactory;
import com.esofthead.mycollab.vaadin.ui.AdvancedEditBeanForm;
import com.esofthead.mycollab.vaadin.ui.IFormLayoutFactory;
import com.vaadin.server.Resource;
import com.vaadin.ui.ComponentContainer;

@ViewComponent
public class AccountAddViewImpl extends AbstractEditItemComp<SimpleAccount>
		implements AccountAddView {
	private static final long serialVersionUID = -6760402062110610122L;

	@Override
	protected String initFormTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Resource initFormIconResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ComponentContainer createButtonControls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AdvancedEditBeanForm<SimpleAccount> initPreviewForm() {
		return new AdvancedEditBeanForm<SimpleAccount>();
	}

	@Override
	protected IFormLayoutFactory initFormLayoutFactory() {
		return new DynaFormLayout(CrmTypeConstants.ACCOUNT,
				AccountDefaultDynaFormLayoutFactory.getForm());
	}

	@Override
	protected AbstractBeanFieldGroupEditFieldFactory<SimpleAccount> initBeanFormFieldFactory() {
		// TODO Auto-generated method stub
		return null;
	}

}
/* 
 * Model Tools.
 * Copyright (C) 2013 Pal Hargitai (pal@lunarray.org)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.lunarray.model.descriptor.validator.beanvalidation.model;

import javax.validation.constraints.AssertTrue;

import org.lunarray.model.descriptor.presentation.annotations.PresentationHint;
import org.lunarray.model.descriptor.util.BooleanInherit;

public class Entity03 {

	@AssertTrue
	private boolean checkItem;
	private final String constantValue;

	@PresentationHint(labelKey = "Identity", required = BooleanInherit.TRUE)
	private String identity;

	public Entity03() {
		this.checkItem = false;
		this.constantValue = "testConstant";
	}

	public String getConstantValue() {
		return this.constantValue;
	}

	public String getIdentity() {
		return this.identity;
	}

	public boolean isCheckItem() {
		return this.checkItem;
	}

	public void setCheckItem(final boolean checkItem) {
		this.checkItem = checkItem;
	}

	public void setIdentity(final String identity) {
		this.identity = identity;
	}
}

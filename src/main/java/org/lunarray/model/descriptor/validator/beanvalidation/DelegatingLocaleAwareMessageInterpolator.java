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
package org.lunarray.model.descriptor.validator.beanvalidation;

import java.util.Locale;

import javax.validation.MessageInterpolator;

/**
 * Locale aware (overriding) message interpolator. Delegates resolving to a
 * delegate interpolator.
 * 
 * @author Pal Hargitai (pal@lunarray.org)
 */
public final class DelegatingLocaleAwareMessageInterpolator
		implements MessageInterpolator {

	/** The actual message interpolator. */
	private transient MessageInterpolator delegateInterpolator;

	/** The current locale. */
	private transient Locale localeInterpolator;

	/**
	 * Default constructor.
	 */
	public DelegatingLocaleAwareMessageInterpolator() {
		// Default constructor.
	}

	/**
	 * Sets a new value for the delegate field.
	 * 
	 * @param delegate
	 *            The new value for the delegate field.
	 * @return The interpolator.
	 */
	public DelegatingLocaleAwareMessageInterpolator delegate(final MessageInterpolator delegate) {
		this.delegateInterpolator = delegate;
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public String interpolate(final String messageTemplate, final Context context) {
		return this.interpolate(messageTemplate, context, this.localeInterpolator);
	}

	/** {@inheritDoc} */
	@Override
	public String interpolate(final String messageTemplate, final Context context, final Locale locale) {
		return this.delegateInterpolator.interpolate(messageTemplate, context, locale);
	}

	/**
	 * Sets a new value for the locale field.
	 * 
	 * @param locale
	 *            The new value for the locale field.
	 * @return The interpolator.
	 */
	public DelegatingLocaleAwareMessageInterpolator locale(final Locale locale) {
		this.localeInterpolator = locale;
		return this;
	}
}

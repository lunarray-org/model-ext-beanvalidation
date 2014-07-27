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

import org.apache.commons.lang.Validate;
import org.lunarray.model.descriptor.model.property.PropertyDescriptor;
import org.lunarray.model.descriptor.validator.PropertyViolation;

/**
 * Describes a bean validation constraint violation.
 * 
 * @author Pal Hargitai (pal@lunarray.org)
 * @param <E>
 *            The entity type.
 * @param <P>
 *            The property type.
 */
public final class BeanValidationPropertyViolation<E, P>
		implements PropertyViolation<E, P> {

	/**
	 * Creates a builder.
	 * 
	 * @param <E>
	 *            The entity type.
	 * @param <P>
	 *            The property type.
	 * @return The builder.
	 */
	public static <E, P> Builder<E, P> createBuilder() {
		return new Builder<E, P>();
	}

	/** The property descriptor. */
	private PropertyDescriptor<P, E> propertyDescriptor;

	/** The violation. */
	private javax.validation.ConstraintViolation<E> violation;

	/**
	 * Constructs the violation.
	 * 
	 * @param builder
	 *            The builder.
	 */
	@SuppressWarnings("unchecked")
	// It's not strictly enforced.
	// It's always castable to this direction.
	protected BeanValidationPropertyViolation(final Builder<E, P> builder) {
		this.propertyDescriptor = builder.propertyDescriptorBuilder;
		this.violation = (javax.validation.ConstraintViolation<E>) builder.violationBuilder;
	}

	/** {@inheritDoc} */
	@Override
	public String getMessage() {
		return this.violation.getMessage();
	}

	/** {@inheritDoc} */
	@Override
	public PropertyDescriptor<P, E> getProperty() {
		return this.propertyDescriptor;
	}

	/**
	 * Gets the value for the propertyDescriptor field.
	 * 
	 * @return The value for the propertyDescriptor field.
	 */
	public PropertyDescriptor<P, E> getPropertyDescriptor() {
		return this.propertyDescriptor;
	}

	/**
	 * Gets the value for the violation field.
	 * 
	 * @return The value for the violation field.
	 */
	public javax.validation.ConstraintViolation<E> getViolation() {
		return this.violation;
	}

	/**
	 * Sets a new value for the propertyDescriptor field.
	 * 
	 * @param propertyDescriptor
	 *            The new value for the propertyDescriptor field.
	 */
	public void setPropertyDescriptor(final PropertyDescriptor<P, E> propertyDescriptor) {
		this.propertyDescriptor = propertyDescriptor;
	}

	/**
	 * Sets a new value for the violation field.
	 * 
	 * @param violation
	 *            The new value for the violation field.
	 */
	public void setViolation(final javax.validation.ConstraintViolation<E> violation) {
		this.violation = violation;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("BeanValidationPropertyViolation[\n\tProperty: ").append(this.propertyDescriptor.getName());
		builder.append("\n\tMessage Key: ").append(this.violation.getMessage());
		builder.append("\n]");
		return builder.toString();
	}

	/**
	 * A builder.
	 * 
	 * @author Pal Hargitai (pal@lunarray.org)
	 * @param <E>
	 *            The entity type.
	 * @param <P>
	 *            The property type.
	 */
	public static final class Builder<E, P> {
		/** The property descriptor. */
		private transient PropertyDescriptor<P, E> propertyDescriptorBuilder;
		/** The violation. */
		private transient javax.validation.ConstraintViolation<? extends E> violationBuilder;

		/** Default constructor. */
		protected Builder() {
			// Default constructor.
		}

		/**
		 * Builds the violation.
		 * 
		 * @return The violation.
		 */
		public BeanValidationPropertyViolation<E, P> build() {
			Validate.notNull(this.propertyDescriptorBuilder, "Property descriptor was null.");
			Validate.notNull(this.violationBuilder, "Violation was null.");
			return new BeanValidationPropertyViolation<E, P>(this);
		}

		/**
		 * Sets the property descriptor.
		 * 
		 * @param propertyDescriptor
		 *            The property descriptor.
		 * @return The builder.
		 */
		@SuppressWarnings("unchecked")
		// This decides the type.
		public Builder<E, P> propertyDescriptor(final PropertyDescriptor<?, E> propertyDescriptor) {
			this.propertyDescriptorBuilder = (PropertyDescriptor<P, E>) propertyDescriptor;
			return this;
		}

		/**
		 * Sets the violation.
		 * 
		 * @param violation
		 *            The violation.
		 * @return The builder.
		 */
		public Builder<E, P> violation(final javax.validation.ConstraintViolation<? extends E> violation) {
			this.violationBuilder = violation;
			return this;
		}
	}
}

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
import org.lunarray.model.descriptor.model.operation.parameters.ParameterDescriptor;
import org.lunarray.model.descriptor.validator.ParameterViolation;

/**
 * Describes a bean validation constraint violation.
 * 
 * @author Pal Hargitai (pal@lunarray.org)
 * @param <P>
 *            The parameter type.
 */
public final class BeanValidationParameterViolation<P>
		implements ParameterViolation<P> {

	/**
	 * Creates a builder.
	 * 
	 * @param <P>
	 *            The parameter type.
	 * @return The builder.
	 */
	public static <P> Builder<P> createBuilder() {
		return new Builder<P>();
	}

	/** The parameter descriptor. */
	private ParameterDescriptor<P> parameterDescriptor;

	/** The violation. */
	private javax.validation.ConstraintViolation<?> violation;

	/**
	 * Constructs the violation.
	 * 
	 * @param builder
	 *            The builder.
	 */
	protected BeanValidationParameterViolation(final Builder<P> builder) {
		this.parameterDescriptor = builder.parameterDescriptorBuilder;
		this.violation = builder.violationBuilder;
	}

	/** {@inheritDoc} */
	@Override
	public String getMessage() {
		return this.violation.getMessage();
	}

	/** {@inheritDoc} */
	@Override
	public ParameterDescriptor<P> getParameter() {
		return this.parameterDescriptor;
	}

	/**
	 * Gets the value for the parameterDescriptor field.
	 * 
	 * @return The value for the parameterDescriptor field.
	 */
	public ParameterDescriptor<P> getParameterDescriptor() {
		return this.parameterDescriptor;
	}

	/**
	 * Gets the value for the violation field.
	 * 
	 * @return The value for the violation field.
	 */
	public javax.validation.ConstraintViolation<?> getViolation() {
		return this.violation;
	}

	/**
	 * Sets a new value for the parameterDescriptor field.
	 * 
	 * @param parameterDescriptor
	 *            The new value for the parameterDescriptor field.
	 */
	public void setParameterDescriptor(final ParameterDescriptor<P> parameterDescriptor) {
		this.parameterDescriptor = parameterDescriptor;
	}

	/**
	 * Sets a new value for the violation field.
	 * 
	 * @param violation
	 *            The new value for the violation field.
	 */
	public void setViolation(final javax.validation.ConstraintViolation<?> violation) {
		this.violation = violation;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("BeanValidationParameterViolation[\n\tParameter: ").append(this.parameterDescriptor.getIndex());
		builder.append("\n\tMessage Key: ").append(this.violation.getMessage());
		builder.append("\n]");
		return builder.toString();
	}

	/**
	 * A builder.
	 * 
	 * @author Pal Hargitai (pal@lunarray.org)
	 * @param <P>
	 *            The parameter type.
	 */
	public static final class Builder<P> {
		/** The parameter descriptor. */
		private transient ParameterDescriptor<P> parameterDescriptorBuilder;
		/** The violation. */
		private transient javax.validation.ConstraintViolation<?> violationBuilder;

		/** Default constructor. */
		protected Builder() {
			// Default constructor.
		}

		/**
		 * Builds the violation.
		 * 
		 * @return The violation.
		 */
		public BeanValidationParameterViolation<P> build() {
			Validate.notNull(this.parameterDescriptorBuilder, "Parameter descriptor was null.");
			Validate.notNull(this.violationBuilder, "Violation was null.");
			return new BeanValidationParameterViolation<P>(this);
		}

		/**
		 * Sets the parameter descriptor.
		 * 
		 * @param parameterDescriptor
		 *            The parameter descriptor.
		 * @return The builder.
		 */
		@SuppressWarnings("unchecked")
		// This decides the type.
		public Builder<P> parameterDescriptor(final ParameterDescriptor<?> parameterDescriptor) {
			this.parameterDescriptorBuilder = (ParameterDescriptor<P>) parameterDescriptor;
			return this;
		}

		/**
		 * Sets the violation.
		 * 
		 * @param violation
		 *            The violation.
		 * @return The builder.
		 */
		public Builder<P> violation(final javax.validation.ConstraintViolation<?> violation) {
			this.violationBuilder = violation;
			return this;
		}
	}
}

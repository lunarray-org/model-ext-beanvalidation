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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

import org.apache.commons.lang.Validate;
import org.lunarray.common.check.CheckUtil;
import org.lunarray.model.descriptor.model.entity.EntityDescriptor;
import org.lunarray.model.descriptor.model.operation.OperationDescriptor;
import org.lunarray.model.descriptor.model.operation.parameters.ParameterDescriptor;
import org.lunarray.model.descriptor.model.property.PropertyDescriptor;
import org.lunarray.model.descriptor.qualifier.QualifierSelected;
import org.lunarray.model.descriptor.util.OperationInvocationBuilder;
import org.lunarray.model.descriptor.validator.EntityValidator;
import org.lunarray.model.descriptor.validator.InvocationValidator;
import org.lunarray.model.descriptor.validator.ParameterViolation;
import org.lunarray.model.descriptor.validator.PropertyValidator;
import org.lunarray.model.descriptor.validator.PropertyViolation;
import org.lunarray.model.descriptor.validator.ValueValidator;
import org.lunarray.model.descriptor.validator.beanvalidation.BeanValidationPropertyViolation.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validator that uses bean validation ({@link Validator}) to validate.
 * 
 * @author Pal Hargitai (pal@lunarray.org)
 */
public final class BeanValidationValidator
		implements EntityValidator, PropertyValidator, ValueValidator, InvocationValidator {

	/** Validation message. */
	private static final String ENTITY_DESCRIPTOR_NULL = "Entity descriptor may not be null.";
	/** Validation message. */
	private static final String ENTITY_NULL = "Entity may not be null.";
	/** Validation message. */
	private static final String INVOCATION_NULL = "Invocation may not be null.";
	/** The logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BeanValidationValidator.class);
	/** Validation message. */
	private static final String PROPERTY_DESCRIPTOR_NULL = "Property descriptor may not be null.";
	/** The validator. */
	private final transient ValidatorFactory validatorFactory;

	/**
	 * Default constructor, resolves the default factory.
	 */
	public BeanValidationValidator() {
		this(Validation.buildDefaultValidatorFactory());
	}

	/**
	 * Default constructor.
	 * 
	 * @param factory
	 *            The validator factory used to instanciate validators.
	 */
	public BeanValidationValidator(final ValidatorFactory factory) {
		this.validatorFactory = factory;
	}

	/** {@inheritDoc} */
	@Override
	public <E> Collection<PropertyViolation<E, ?>> validate(final EntityDescriptor<E> entityDescriptor, final E entity) {
		return this.validate(entityDescriptor, entity, null);
	}

	/** {@inheritDoc} */
	@Override
	public <E> Collection<PropertyViolation<E, ?>> validate(final EntityDescriptor<E> entityDescriptor, final E entity, final Locale locale) {
		BeanValidationValidator.LOGGER.debug("Validating with locale {} entity: {}, value {}.", locale, entity, entity);
		Validate.notNull(entityDescriptor, BeanValidationValidator.ENTITY_DESCRIPTOR_NULL);
		Validate.notNull(entity, BeanValidationValidator.ENTITY_NULL);
		Set<javax.validation.ConstraintViolation<E>> violations;
		final Validator validator = this.createValidator(locale);
		if (entityDescriptor.adaptable(QualifierSelected.class)) {
			final QualifierSelected selected = entityDescriptor.adapt(QualifierSelected.class);
			violations = validator.validate(entity, selected.getQualifier());
		} else {
			violations = validator.validate(entity);
		}
		return this.convertViolations(entityDescriptor, violations);
	}

	/** {@inheritDoc} */
	@Override
	public Collection<ParameterViolation<?>> validateInvocation(final OperationInvocationBuilder<?> invocation) {
		return this.validateInvocation(invocation, null);
	}

	/** {@inheritDoc} */
	@Override
	public Collection<ParameterViolation<?>> validateInvocation(final OperationInvocationBuilder<?> invocation, final Locale locale) {
		BeanValidationValidator.LOGGER.debug("Validating with locale {} invocation: {}", locale, invocation);
		Validate.notNull(invocation, BeanValidationValidator.INVOCATION_NULL);
		final ExecutableValidator validator = this.createValidator(locale).forExecutables();
		final Object[] parameters = new Object[invocation.getOperationDescriptor().getParameterCount()];
		for (final Map.Entry<ParameterDescriptor<?>, ?> entry : invocation.getParameters().entrySet()) {
			parameters[entry.getKey().getIndex()] = entry.getValue();
		}
		return this.innerValidateInvocation(invocation, validator, parameters);
	}

	/** {@inheritDoc} */
	@Override
	public <P, E> Collection<PropertyViolation<E, P>> validateProperty(final PropertyDescriptor<P, E> propertyDescriptor, final E entity) {
		return this.validateProperty(propertyDescriptor, entity, null);
	}

	/** {@inheritDoc} */
	@Override
	public <P, E> Collection<PropertyViolation<E, P>> validateProperty(final PropertyDescriptor<P, E> propertyDescriptor, final E entity,
			final Locale locale) {
		BeanValidationValidator.LOGGER.debug("Validating with locale {} property: {}, entity {}", locale, propertyDescriptor, entity);
		Validate.notNull(propertyDescriptor, BeanValidationValidator.PROPERTY_DESCRIPTOR_NULL);
		Validate.notNull(entity, BeanValidationValidator.ENTITY_NULL);
		final String name = propertyDescriptor.getName();
		Set<javax.validation.ConstraintViolation<E>> violations;
		final Validator validator = this.createValidator(locale);
		if (propertyDescriptor.adaptable(QualifierSelected.class)) {
			final QualifierSelected selected = propertyDescriptor.adapt(QualifierSelected.class);
			violations = validator.validateProperty(entity, name, selected.getQualifier());
		} else {
			violations = validator.validateProperty(entity, name);
		}
		return this.convertViolations(propertyDescriptor, violations);
	}

	/** {@inheritDoc} */
	@Override
	public <E, P> Collection<PropertyViolation<E, P>> validateValue(final PropertyDescriptor<P, E> propertyDescriptor, final P value) {
		return this.validateValue(propertyDescriptor, value, null);
	}

	/** {@inheritDoc} */
	@Override
	public <E, P> Collection<PropertyViolation<E, P>> validateValue(final PropertyDescriptor<P, E> propertyDescriptor, final P value,
			final Locale locale) {
		BeanValidationValidator.LOGGER.debug("Validating with locale {} property: {}, with value {}", locale, propertyDescriptor, value);
		Validate.notNull(propertyDescriptor, BeanValidationValidator.PROPERTY_DESCRIPTOR_NULL);
		final String name = propertyDescriptor.getName();
		final Class<E> entityType = propertyDescriptor.getEntityType();
		Set<javax.validation.ConstraintViolation<E>> violations;
		final Validator validator = this.createValidator(locale);
		if (propertyDescriptor.adaptable(QualifierSelected.class)) {
			final QualifierSelected selected = propertyDescriptor.adapt(QualifierSelected.class);
			violations = validator.validateValue(entityType, name, value, selected.getQualifier());
		} else {
			violations = validator.validateValue(entityType, name, value);
		}
		return this.convertViolations(propertyDescriptor, violations);
	}

	/**
	 * Convert violations.
	 * 
	 * @param entityDescriptor
	 *            The entity descriptor.
	 * @param violations
	 *            The violations.
	 * @param <E>
	 *            The entity type.
	 * @return The violations.
	 */
	private <E> Collection<PropertyViolation<E, ?>> convertViolations(final EntityDescriptor<E> entityDescriptor,
			final Collection<javax.validation.ConstraintViolation<E>> violations) {
		final Collection<PropertyViolation<E, ?>> results = new HashSet<PropertyViolation<E, ?>>();
		for (final javax.validation.ConstraintViolation<E> violation : violations) {
			final Builder<E, ?> builder = BeanValidationPropertyViolation.createBuilder();
			builder.propertyDescriptor(entityDescriptor.getProperty(violation.getPropertyPath().toString())).violation(violation);
			results.add(builder.build());
		}
		return results;
	}

	/**
	 * Convert violations.
	 * 
	 * @param propertyDescriptor
	 *            The property descriptor.
	 * @param violations
	 *            The violations.
	 * @param <P>
	 *            The property type.
	 * @param <E>
	 *            The entity type.
	 * @return The violations.
	 */
	private <P, E> Collection<PropertyViolation<E, P>> convertViolations(final PropertyDescriptor<P, E> propertyDescriptor,
			final Collection<javax.validation.ConstraintViolation<E>> violations) {
		final Collection<PropertyViolation<E, P>> results = new HashSet<PropertyViolation<E, P>>();
		for (final javax.validation.ConstraintViolation<E> violation : violations) {
			final Builder<E, P> builder = BeanValidationPropertyViolation.createBuilder();
			builder.propertyDescriptor(propertyDescriptor).violation(violation);
			results.add(builder.build());
		}
		return results;
	}

	/**
	 * Create the builder.
	 * 
	 * @param node
	 *            The node.
	 * @param descriptor
	 *            The operation descriptor.
	 * @return The builder.
	 * @param <E>
	 *            The entity type.
	 * @param <P>
	 *            The parameter type.
	 */
	private <E, P> BeanValidationParameterViolation.Builder<P> createBuilder(final Path.ParameterNode node,
			final OperationDescriptor<E> descriptor) {
		final BeanValidationParameterViolation.Builder<P> builder = BeanValidationParameterViolation.createBuilder();
		builder.parameterDescriptor(descriptor.getParameter(node.getParameterIndex()));
		return builder;
	}

	/**
	 * Creates a validator.
	 * 
	 * @param locale
	 *            The message locale, may be null.
	 * @return A validator.
	 */
	private Validator createValidator(final Locale locale) {
		final ValidatorContext validatorContext = this.validatorFactory.usingContext();
		if (!CheckUtil.isNull(locale)) {
			final DelegatingLocaleAwareMessageInterpolator interpolator = new DelegatingLocaleAwareMessageInterpolator();
			interpolator.delegate(this.validatorFactory.getMessageInterpolator()).locale(locale);
			validatorContext.messageInterpolator(interpolator);
		}
		return validatorContext.getValidator();
	}

	/**
	 * Inner invocation validation.
	 * 
	 * @param invocation
	 *            The invocation.
	 * @param validator
	 *            The validator.
	 * @param parameters
	 *            The invocation parameters.
	 * @return The exceptions.
	 * @param <E>
	 *            The entity type.
	 */
	private <E> Collection<ParameterViolation<?>> innerValidateInvocation(final OperationInvocationBuilder<E> invocation,
			final ExecutableValidator validator, final Object[] parameters) {
		final Set<ConstraintViolation<E>> violations = validator.validateParameters(invocation.getTarget(), invocation
				.getOperationDescriptor().getOperationReference().getReferencedOperation().getOperation(), parameters);
		final Set<ParameterViolation<?>> result = new HashSet<ParameterViolation<?>>();
		for (final ConstraintViolation<E> violation : violations) {
			final Iterator<Path.Node> pathIt = violation.getPropertyPath().iterator();
			pathIt.next();
			final Path.Node node = pathIt.next();
			if (node instanceof Path.ParameterNode) {
				final Path.ParameterNode paramNode = (Path.ParameterNode) node;
				final BeanValidationParameterViolation.Builder<?> builder = this.createBuilder(paramNode,
						invocation.getOperationDescriptor());
				builder.violation(violation);
				result.add(builder.build());
			}
		}
		return result;
	}
}

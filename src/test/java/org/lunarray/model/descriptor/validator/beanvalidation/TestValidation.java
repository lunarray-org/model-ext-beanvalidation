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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lunarray.model.descriptor.builder.annotation.presentation.builder.PresQualBuilder;
import org.lunarray.model.descriptor.builder.annotation.resolver.ResolverFactory;
import org.lunarray.model.descriptor.model.Model;
import org.lunarray.model.descriptor.model.entity.EntityDescriptor;
import org.lunarray.model.descriptor.model.operation.OperationDescriptor;
import org.lunarray.model.descriptor.model.property.PropertyDescriptor;
import org.lunarray.model.descriptor.objectfactory.simple.SimpleObjectFactory;
import org.lunarray.model.descriptor.qualifier.QualifierEntityDescriptor;
import org.lunarray.model.descriptor.resource.simpleresource.SimpleClazzResource;
import org.lunarray.model.descriptor.util.OperationInvocationBuilder;
import org.lunarray.model.descriptor.validator.EntityValidator;
import org.lunarray.model.descriptor.validator.InvocationValidator;
import org.lunarray.model.descriptor.validator.ParameterViolation;
import org.lunarray.model.descriptor.validator.PropertyValidator;
import org.lunarray.model.descriptor.validator.PropertyViolation;
import org.lunarray.model.descriptor.validator.ValueValidator;
import org.lunarray.model.descriptor.validator.beanvalidation.model.Entity01;
import org.lunarray.model.descriptor.validator.beanvalidation.model.Entity02;
import org.lunarray.model.descriptor.validator.beanvalidation.model.Qualifier01;

/**
 * Test the bean validator.
 * 
 * @author Pal Hargitai (pal@lunarray.org)
 * @see BeanValidationValidator
 */
public class TestValidation {
	/** An entity instance. */
	private Entity01 entity;
	/** The entity descriptor. */
	private EntityDescriptor<Entity01> entityDescriptor;
	/** A bean validator. */
	private BeanValidationValidator validator = new BeanValidationValidator();

	/** Setup the bean validator tests. */
	@Before
	public void setup() throws Exception {
		// Create the resource.
		@SuppressWarnings("unchecked")
		final SimpleClazzResource<Object> resource = new SimpleClazzResource<Object>(Entity01.class, Entity02.class);
		// Create the validator.
		this.validator = new BeanValidationValidator();
		// Create the model.
		final Model<Object> model = PresQualBuilder.createBuilder().propertyResolver(ResolverFactory.accessorPropertyResolver())
				.extensions(new SimpleObjectFactory(), this.validator).resources(resource).build();
		// Create entity and descriptor.
		this.entity = new Entity01();
		this.entityDescriptor = model.getEntity(Entity01.class);
	}

	/**
	 * Test validation of an entity.
	 * 
	 * @see EntityValidator#validate(EntityDescriptor, Object)
	 */
	@Test
	public void testEntityValidation() throws Exception {
		final Collection<PropertyViolation<Entity01, ?>> violations = this.validator.validate(this.entityDescriptor, this.entity);
		Assert.assertEquals(2, violations.size());
		for (final PropertyViolation<Entity01, ?> violation : violations) {
			violation.toString();
			if (violation.getProperty() == this.entityDescriptor.getProperty("value")) {
				Assert.assertEquals("may not be empty", violation.getMessage());
			}
			if (violation.getProperty() == this.entityDescriptor.getProperty("embedded.checkItem")) {
				Assert.assertEquals("must be true", violation.getMessage());
			}
		}
	}

	/**
	 * Test the validation of a method.
	 * 
	 * @see InvocationValidator#validateInvocation(org.lunarray.model.descriptor.util.OperationInvocationBuilder)
	 */
	@Test
	public void testInvocation() throws Exception {
		final OperationDescriptor<Entity01> op = this.entityDescriptor.getOperation("method");
		final OperationInvocationBuilder<Entity01> builder = new OperationInvocationBuilder<Entity01>(op);
		builder.target(new Entity01());
		final Collection<ParameterViolation<?>> violations = this.validator.validateInvocation(builder);
		Assert.assertEquals(1, violations.size());
	}

	/**
	 * Test locale entity validation.
	 * 
	 * @see EntityValidator#validate(EntityDescriptor, Object, Locale)
	 */
	@Test
	public void testLocaleEntity() throws Exception {
		final Collection<PropertyViolation<Entity01, ?>> violations = this.validator.validate(this.entityDescriptor, this.entity,
				Locale.GERMAN);
		Assert.assertEquals(2, violations.size());
		for (final PropertyViolation<Entity01, ?> violation : violations) {
			violation.toString();
			if (violation.getProperty() == this.entityDescriptor.getProperty("value")) {
				Assert.assertEquals("darf nicht leer sein", violation.getMessage());
			}
			if (violation.getProperty() == this.entityDescriptor.getProperty("embedded.checkItem")) {
				Assert.assertEquals("muss wahr sein", violation.getMessage());
			}
		}
	}

	/**
	 * Test locale property validation.
	 * 
	 * @see PropertyValidator#validateProperty(PropertyDescriptor, Object,
	 *      Locale)
	 */
	@Test
	public void testLocaleProperty() throws Exception {
		final PropertyDescriptor<String, Entity01> prop = this.entityDescriptor.getProperty("value", String.class);
		final Collection<PropertyViolation<Entity01, String>> collection = this.validator
				.validateProperty(prop, this.entity, Locale.GERMAN);
		Assert.assertEquals(1, collection.size());
		final PropertyViolation<Entity01, String> violation = collection.iterator().next();
		Assert.assertEquals("darf nicht leer sein", violation.getMessage());
	}

	/**
	 * Test locale value validation.
	 * 
	 * @see ValueValidator#validateValue(PropertyDescriptor, Object, Locale)
	 */
	@Test
	public void testLocaleValue() throws Exception {
		final PropertyDescriptor<String, Entity01> prop = this.entityDescriptor.getProperty("value", String.class);
		final Collection<PropertyViolation<Entity01, String>> collection = this.validator.validateValue(prop, "", Locale.GERMAN);
		Assert.assertEquals(1, collection.size());
		final PropertyViolation<Entity01, String> violation = collection.iterator().next();
		Assert.assertEquals("darf nicht leer sein", violation.getMessage());
	}

	/**
	 * Test validation of a property.
	 * 
	 * @see PropertyValidator#validateProperty(PropertyDescriptor, Object)
	 */
	@Test
	public void testPropertyValidation() throws Exception {
		final PropertyDescriptor<String, Entity01> prop = this.entityDescriptor.getProperty("value", String.class);
		final Collection<PropertyViolation<Entity01, String>> collection = this.validator.validateProperty(prop, this.entity);
		Assert.assertEquals(1, collection.size());
		final PropertyViolation<Entity01, String> violation = collection.iterator().next();
		Assert.assertEquals(prop, violation.getProperty());
		Assert.assertEquals("may not be empty", violation.getMessage());
	}

	/**
	 * Test qualifier entity validation.
	 * 
	 * @see EntityValidator#validate(EntityDescriptor, Object)
	 */
	@Test
	public void testQualifierEntity() throws Exception {
		@SuppressWarnings("unchecked")
		final EntityDescriptor<Entity01> qualifierDescriptor = this.entityDescriptor.adapt(QualifierEntityDescriptor.class)
				.getQualifierEntity(Qualifier01.class);
		final Collection<PropertyViolation<Entity01, ?>> violations = this.validator.validate(qualifierDescriptor, this.entity);
		Assert.assertEquals(1, violations.size());
		for (final PropertyViolation<Entity01, ?> violation : violations) {
			violation.toString();
			if (violation.getProperty() == this.entityDescriptor.getProperty("someList")) {
				Assert.assertEquals("may not be empty", violation.getMessage());
			}
		}
	}

	/**
	 * Test qualifier variant of property validation.
	 * 
	 * @see PropertyValidator#validateProperty(PropertyDescriptor, Object)
	 */
	@Test
	public void testQualifierProperty() throws Exception {
		@SuppressWarnings("unchecked")
		final EntityDescriptor<Entity01> qualifierDescriptor = this.entityDescriptor.adapt(QualifierEntityDescriptor.class)
				.getQualifierEntity(Qualifier01.class);
		@SuppressWarnings("rawtypes")
		final PropertyDescriptor<List, Entity01> prop = qualifierDescriptor.getProperty("someList", List.class);
		Assert.assertEquals(1, this.validator.validateProperty(prop, this.entity).size());
	}

	/**
	 * Test qualifier variant of value validation.
	 * 
	 * @see ValueValidator#validateValue(PropertyDescriptor, Object)
	 */
	@Test
	public void testQualifierValue() throws Exception {
		@SuppressWarnings("unchecked")
		final EntityDescriptor<Entity01> qualifierDescriptor = this.entityDescriptor.adapt(QualifierEntityDescriptor.class)
				.getQualifierEntity(Qualifier01.class);
		@SuppressWarnings("rawtypes")
		final PropertyDescriptor<List, Entity01> prop = qualifierDescriptor.getProperty("someList", List.class);
		Assert.assertEquals(1, this.validator.validateValue(prop, new LinkedList<Object>()).size());
	}

	/**
	 * Test validation of a value.
	 * 
	 * @see ValueValidator#validateValue(PropertyDescriptor, Object)
	 */
	@Test
	public void testValueValidation() throws Exception {
		final PropertyDescriptor<String, Entity01> prop = this.entityDescriptor.getProperty("value", String.class);
		final Collection<PropertyViolation<Entity01, String>> collection = this.validator.validateValue(prop, "");
		Assert.assertEquals(1, collection.size());
		final PropertyViolation<Entity01, String> violation = collection.iterator().next();
		Assert.assertEquals(prop, violation.getProperty());
		Assert.assertEquals("may not be empty", violation.getMessage());
	}
}

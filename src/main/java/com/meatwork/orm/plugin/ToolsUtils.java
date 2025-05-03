package com.meatwork.orm.plugin;


/*
 * Copyright (c) 2025 Taliro.
 * All rights reserved.
 */
public final class ToolsUtils {

	private ToolsUtils() {}

	public static String toSnakeCase(String value) {
		return value
				.replaceAll(
						"([a-z0-9])([A-Z])",
						"$1_$2"
				)
				.replaceAll(
						"([A-Z]+)([A-Z][a-z])",
						"$1_$2"
				)
				.toUpperCase();
	}

	public static String typeConverted(String type,
	                                    String mappedBy) {
		var propertyType = PropertyType.valueOf(toSnakeCase(type));
		return processEntityRefOrReturnType(
				propertyType,
				mappedBy
		);
	}

	public static String getValueOrDefault(Boolean value,
	                               String defaultValue) {
		return value == null ? defaultValue : String.valueOf(value);
	}

	public static String getValueOrDefault(String value,
	                               String defaultValue) {
		return value == null ? defaultValue : value;
	}

	public static void checkNumberOfIds(int nbIds) throws EntityGeneratorException {
		if (nbIds > 1) {
			throw new EntityGeneratorException("Need only one primary key");
		}
	}

	private static String processEntityRefOrReturnType(PropertyType propertyType,
	                                                   String mappedBy) {
		if (propertyType == PropertyType.ENTITY_REF) {
			return propertyType
					.getType()
					.replace(
							"%E",
							mappedBy
					);
		}
		return propertyType.getType();
	}

}

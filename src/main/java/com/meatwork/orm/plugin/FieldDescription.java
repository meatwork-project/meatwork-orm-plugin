package com.meatwork.orm.plugin;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (c) 2025 Taliro.
 * All rights reserved.
 */
public class FieldDescription {


	private final boolean id;
	private final String name;
	private final String type;
	private final String mappedBy;
	private final Boolean unique;
	private final Boolean nullable;
	private List<String> extraProperties = null;

	public FieldDescription(Map<String, Object> field) throws EntityGeneratorException {
		this.id = getValueOrDefault(field.get("id"), false);
		this.name = checkAttributeRequired("name", (String) field.get("name"));
		this.type = checkAttributeRequired("type", (String) field.get("type"));
		this.nullable = (Boolean) field.get("nullable");
		if (this.type.equals("entityRef")) {
			this.mappedBy = checkAttributeRequired("mappedBy", (String) field.get("mappedBy"));
		} else {
			this.mappedBy = null;
		}
		this.unique = (Boolean) field.get("unique");
	}

	public Boolean getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getMappedBy() {
		return mappedBy;
	}

	public void addExtraProperties(String extraProperty) {
		if (this.extraProperties == null) {
			this.extraProperties = new ArrayList<>();
		}
		this.extraProperties.add(extraProperty);
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	private static <T> T getValueOrDefault(Object value, T defaultValue) {
		return value != null ? (T) value : defaultValue;
	}

	private static String checkAttributeRequired(String key,
	                                             String value) throws EntityGeneratorException {
		if (value == null || value.isEmpty()) {
			throw new EntityGeneratorException("Attribute " + key + " is required");
		}

		return value;
	}

	public Map<String, String> toMap() {
		var map = new HashMap<String, String>();
		map.put("name", name);
		map.put("type", ToolsUtils.toSnakeCase(type));
		map.put("extraProperties", ToolsUtils.getValueOrDefault(processExtraProperties(extraProperties), ""));
		map.put("typeConverted", ToolsUtils.typeConverted(type, mappedBy));
		map.put("id", ToolsUtils.getValueOrDefault(id, "false"));
		map.put("unique", ToolsUtils.getValueOrDefault(unique, "false"));
		map.put("nullable", ToolsUtils.getValueOrDefault(nullable, "true"));
		return map;
	}

	private static String processExtraProperties(List<String> extraProperties) {
		if (extraProperties == null || extraProperties.isEmpty()) {
			return null;
		}
		return String.join(
				",",
				extraProperties
		);
	}
}

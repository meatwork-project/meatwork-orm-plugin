package com.meatwork.orm.plugin;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (c) 2025 Taliro.
 * All rights reserved.
 */
public class FieldDescription {


	private final Boolean id;
	private final String name;
	private final String type;
	private final String mappedBy;
	private final Boolean unique;

	public FieldDescription(Map<String, Object> field) throws EntityGeneratorException {
		this.id = (Boolean) field.get("id");
		this.name = checkAttributeRequired("name", (String) field.get("name"));
		this.type = checkAttributeRequired("type", (String) field.get("type"));
		if (this.type.equals("EntityRef")) {
			this.mappedBy = checkAttributeRequired("mappedBy", (String) field.get("mappedBy"));
		} else {
			this.mappedBy = null;
		}
		this.unique = (Boolean) field.get("unique");
	}

	public Boolean getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getMappedBy() {
		return mappedBy;
	}

	public Boolean getUnique() {
		return unique;
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
		map.put("typeConverted", ToolsUtils.typeConverted(type, mappedBy));
		map.put("id", ToolsUtils.getValueOrDefault(id, "false"));
		map.put("unique", ToolsUtils.getValueOrDefault(unique, "false"));
		return map;
	}
}

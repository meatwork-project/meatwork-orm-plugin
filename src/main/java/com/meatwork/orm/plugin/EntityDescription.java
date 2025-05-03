package com.meatwork.orm.plugin;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Copyright (c) 2025 Taliro.
 * All rights reserved.
 */
public class EntityDescription {

	private final String packageName;
	private final String className;
	private final String extended;
	private final String tableName;
	private final List<FieldDescription> fields = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public EntityDescription(Map<String, Object> root) throws EntityGeneratorException {
		this.packageName = (String) root.get("package");
		this.className = (String) root.get("name");
		this.extended = (String) root.getOrDefault("extended", "AbstractMeatEntity");
		this.tableName = (String) root.getOrDefault("table", className);

		for (var field : ((List<Map<String, Object>>) root.get("fields"))) {
			this.fields.add(new FieldDescription(field));
		}
	}

	public String getPackageName() {
		return packageName;
	}

	public String getClassName() {
		return className;
	}

	public String getExtended() {
		return extended;
	}

	public String getTableName() {
		return tableName;
	}

	public List<FieldDescription> getFields() {
		return fields;
	}

	public Map<String, Object> toMap() throws EntityGeneratorException {
		var map = new HashMap<String, Object>();
		map.put("packageName", packageName);
		map.put("className", className);
		map.put("superClass", extended);
		map.put("tableName", tableName);
		map.put("fields", processFieldToMap(fields));
		return map;
	}

	private static List<Map<String, String>> processFieldToMap(List<FieldDescription> list) throws EntityGeneratorException {
		var newList = new ArrayList<Map<String, String>>(list.size());
		var nbIds = new AtomicInteger(0);
		for (var fieldDescription : list) {
			newList.add(fieldDescription.toMap());
			checkNumberId(fieldDescription, nbIds);
		}
		return newList;
	}

	private static void checkNumberId(FieldDescription fieldDescription, AtomicInteger nbIds) throws EntityGeneratorException {
		if (fieldDescription.getId()) {
			var nbIdsInt = nbIds.incrementAndGet();
			ToolsUtils.checkNumberOfIds(nbIdsInt);
		}
	}
}

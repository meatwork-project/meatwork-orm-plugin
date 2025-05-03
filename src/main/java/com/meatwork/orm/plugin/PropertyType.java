package com.meatwork.orm.plugin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/*
 * Copyright (c) 2025 Taliro.
 * All rights reserved.
 */
public enum PropertyType {
	BOOLEAN(Boolean.class.getName()),
	STRING(String.class.getName()),
	INTEGER(Integer.class.getName()),
	LONG(Long.class.getName()),
	BIGDECIMAL(BigDecimal.class.getName()),
	LOCALDATE(LocalDate.class.getName()),
	LOCALDATETIME(LocalDateTime.class.getName()),
	LOCALTIME(LocalTime.class.getName()),
	ENTITY_REF("com.meatwork.orm.api.EntityRef<%E>")
	;

	private final String type;

	PropertyType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}

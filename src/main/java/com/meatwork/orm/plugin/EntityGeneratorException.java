package com.meatwork.orm.plugin;

import org.apache.maven.plugin.MojoExecutionException;

/*
 * Copyright (c) 2025 Taliro.
 * All rights reserved.
 */
public class EntityGeneratorException extends MojoExecutionException {
	public EntityGeneratorException(Object source,
	                                String shortMessage,
	                                String longMessage) {
		super(
				source,
				shortMessage,
				longMessage
		);
	}

	public EntityGeneratorException(String message,
	                                Exception cause) {
		super(
				message,
				cause
		);
	}

	public EntityGeneratorException(String message,
	                                Throwable cause) {
		super(
				message,
				cause
		);
	}

	public EntityGeneratorException(String message) {
		super(message);
	}
}

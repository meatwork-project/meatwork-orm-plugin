package com.meatwork.orm.plugin;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (c) 2025 Taliro.
 * All rights reserved.
 */
@Mojo(name = "entityGenerator")
public class EntityGeneratorMojo extends AbstractMojo {

	@Parameter(name = "xmlPath", required = true)
	private String yamlFiles;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	@Override
	@SuppressWarnings("unchecked")
	public void execute() throws MojoExecutionException {
		var cfg = getConfiguration();

		try {
			var yamlFilesPath = Files
					.walk(project
							      .getFile()
							      .toPath()
							      .resolve(yamlFiles))
					.filter(p -> p
							.toString()
							.endsWith(".yaml"))
					.toList();

			for (Path yamlFile : yamlFilesPath) {
				try (FileReader reader = new FileReader(yamlFile.toFile())) {
					Yaml yaml = new Yaml();
					var entityDescription = new EntityDescription(yaml.load(reader));
					processTpl(
							cfg,
							entityDescription.getClassName(),
							entityDescription.toMap()
					);
					getLog().info("Classe générée pour " + entityDescription.getClassName());
				} catch (Exception e) {
					throw new MojoExecutionException(
							"Erreur lors du traitement de " + yamlFile,
							e
					);
				}
			}
		} catch (IOException e) {
			getLog().error("Cannot read xml files : " + e.getMessage());
			throw new MojoExecutionException(
					"Cannot read xml files : " + e.getMessage(),
					e
			);
		}

		// Ajouter les sources générées au build (important)
		project.addCompileSourceRoot(
				project
						.getBasedir()
						.getAbsolutePath() + "/target/generated-sources/xmlgen"
		);
	}

	private Configuration getConfiguration() {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
		cfg.setClassLoaderForTemplateLoading(
				getClass().getClassLoader(),
				"templates"
		);
		cfg.setDefaultEncoding("UTF-8");
		return cfg;
	}

	private void processTpl(Configuration cfg,
	                        String className,
	                        Map<String, Object> dataModel) throws IOException, TemplateException {
		Template template = cfg.getTemplate("EntityClass.ftl");

		Path outputDir = project
				.getFile()
				.toPath()
				.resolve("target/generated-sources/entities");
		Files.createDirectories(outputDir);

		try (Writer out = new FileWriter(outputDir
				                                 .resolve(className + ".java")
				                                 .toFile())) {
			template.process(
					dataModel,
					out
			);
		}
	}
}

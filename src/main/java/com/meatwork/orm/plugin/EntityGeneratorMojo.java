package com.meatwork.orm.plugin;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
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
import java.util.List;
import java.util.Map;

/*
 * Copyright (c) 2025 Taliro.
 * All rights reserved.
 */
@Mojo(name = "entityGenerator", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class EntityGeneratorMojo extends AbstractMojo {

	@Parameter(name = "xmlPath", required = true)
	private String yamlFiles;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	@Override
	public void execute() throws MojoExecutionException {
		var cfg = getConfiguration();

		try(var streamYamFiles = Files
				     .walk(project
						           .getFile()
						           .toPath()
						           .resolve(yamlFiles))) {
			var yamlFilesPath = streamYamFiles
					.filter(p -> p
							.toString()
							.endsWith(".yaml"))
					.toList();
			var entityDescriptions = getEntityDescriptions(yamlFilesPath);
			for (var entityDescription : entityDescriptions) {
				try {
					processMappedBy(entityDescription.getFields(), entityDescriptions);
					processTpl(
							cfg,
							entityDescription.getClassName(),
							entityDescription.toMap()
					);
					getLog().info("Classe générée pour " + entityDescription.getClassName());
				} catch (IOException | TemplateException e) {
					getLog().error("Erreur lors du traitement de " + entityDescription.getClassName());
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

	public void processMappedBy(List<FieldDescription> currentField, List<EntityDescription> entities) throws EntityGeneratorException {
		List<FieldDescription> list = currentField
				.stream()
				.filter(it -> it.getMappedBy() != null)
				.toList();
		for (FieldDescription fieldDescription : list) {
			var entityDescription = getEntityDescriptionByName(
					fieldDescription.getMappedBy(),
					entities
			);
			FieldDescription fieldDescriptionId = entityDescription
					.getFields()
					.stream()
					.filter(FieldDescription::getId)
					.findFirst()
					.orElse(null);
			if(fieldDescriptionId == null) {
				throw new EntityGeneratorException("Cannot found id field for entity " + entityDescription.getFullClassName());
			}
			fieldDescription.addExtraProperties("new EntityRefProperty(%s, \"%s\", \"%s\")".formatted("PropertyType." + fieldDescriptionId.getType().toUpperCase(), entityDescription.getTableName(), fieldDescriptionId.getName()));
		}
	}

	private EntityDescription getEntityDescriptionByName(String name, List<EntityDescription> entities) {
		return entities
				.stream()
				.filter(it -> it.getFullClassName().equals(name))
				.findFirst()
				.orElse(null);
	}

	private List<EntityDescription> getEntityDescriptions(List<Path> paths) {
		List<EntityDescription> entityDescriptions = new ArrayList<>();
		for (Path yamlFile : paths) {
			try (FileReader reader = new FileReader(yamlFile.toFile())) {
				Yaml yaml = new Yaml();
				entityDescriptions.add(new EntityDescription(yaml.load(reader)));
			} catch (Exception e) {
				getLog().error("Erreur lors du traitement de " + yamlFile);
			}
		}
		return entityDescriptions;
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

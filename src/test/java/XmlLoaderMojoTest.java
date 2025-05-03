import com.meatwork.orm.plugin.EntityGeneratorMojo;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/*
 * Copyright (c) 2025 Taliro.
 * All rights reserved.
 */
public class XmlLoaderMojoTest {

	@Test
	void testXmlGeneratesJavaFile(@TempDir Path tempDir) throws Exception {
		// 1. Créer le dossier simulant le projet Maven
		Path baseDir = tempDir.resolve("fake-module");
		Path resourcesDir = baseDir.resolve("src/main/resources");
		Files.createDirectories(resourcesDir);

		// 2. Écrire un fichier XML de test
		var resource = getXmlFiles(Paths.get("src/test/resources"));
		resource.forEach(it -> {
			Path xmlFile = resourcesDir.resolve(it.getFileName().toString());
			try {
				Files.writeString(xmlFile, Files.readString(it));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		// 3. Simuler un MavenProject
		MavenProject fakeProject = new MavenProject();
		fakeProject.setFile(baseDir.toFile());

		// 4. Créer et configurer le Mojo
		EntityGeneratorMojo mojo = new EntityGeneratorMojo();
		setPrivateField(mojo, "project", fakeProject);
		setPrivateField(mojo, "yamlFiles", "src/main/resources");

		// 5. Exécuter le plugin
		mojo.execute();

		// 6. Vérifier que le fichier Java est généré
		Path generatedJavaFilePerson = baseDir.resolve("target/generated-sources/entities/Person.java");
		Assertions.assertTrue(Files.exists(generatedJavaFilePerson), "Le fichier généré n'existe pas : " + generatedJavaFilePerson);
		Path generatedJavaFileCard = baseDir.resolve("target/generated-sources/entities/Card.java");
		Assertions.assertTrue(Files.exists(generatedJavaFileCard), "Le fichier généré n'existe pas : " + generatedJavaFileCard);
		Path generatedJavaFilePersonExtended = baseDir.resolve("target/generated-sources/entities/PersonExtended.java");
		Assertions.assertTrue(Files.exists(generatedJavaFilePersonExtended), "Le fichier généré n'existe pas : " + generatedJavaFilePersonExtended);

		// (optionnel) Affiche le contenu généré
		System.out.println("Fichier généré:\n" + Files.readString(generatedJavaFilePerson));
		System.out.println("Fichier généré:\n" + Files.readString(generatedJavaFileCard));
		System.out.println("Fichier généré:\n" + Files.readString(generatedJavaFilePersonExtended));
	}

	public static List<Path> getXmlFiles(Path resourcesDir) throws IOException {
		try (Stream<Path> files = Files.walk(resourcesDir)) {
			return files
					.filter(Files::isRegularFile)
					.filter(path -> path.toString().endsWith(".yaml"))
					.toList();
		}
	}

	private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}

}

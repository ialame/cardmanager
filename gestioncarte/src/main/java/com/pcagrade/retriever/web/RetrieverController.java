package com.pcagrade.retriever.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class RetrieverController {

	private static final Logger LOGGER = LogManager.getLogger(RetrieverController.class);

	private static final Map<String, Object> LOCAL_MODEL_ATTRIBUTES = Map.of(
			"appScripts", List.of("http://localhost:3000/@vite/client", "http://localhost:3000/src/vue/app.ts"),
			"appStyles", Collections.emptyList()
	);

	@Value("${logging.file.name:}")
	private String logFileName;
	@Value("classpath:static/.vite/manifest.json")
	private Resource manifest;
	@Autowired
	private Environment environment;
	@Autowired
	private ObjectMapper objectMapper;

	private Map<String, Object> modelAttributes = null;

	@GetMapping("/download/logs")
	public void getLogs(HttpServletResponse response) throws IOException {
		try (InputStream is = new FileInputStream(logFileName)) {
			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		}
	}

	@GetMapping(path = { "/", "/{name:^(?!api|static|assets|css|js|svg|images|media|favicon|swagger).+}/**" })
	public String vueApp(Model model) {
		loadModelAttributes();
		model.addAllAttributes(modelAttributes);
		return "app";
	}

	private synchronized void loadModelAttributes() {
		if (modelAttributes != null) {
			return;
		}

		try {
			if (ArrayUtils.contains(environment.getActiveProfiles(), "local")) {
				modelAttributes = LOCAL_MODEL_ATTRIBUTES;
			} else {
				var node = getMainNode();

				if (node != null) {
					modelAttributes = Map.of(
							"appScripts", getFiles(node),
							"appStyles", getCss(node)
					);
				} else {
					modelAttributes = LOCAL_MODEL_ATTRIBUTES;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error reading manifest.json", e);
			modelAttributes = LOCAL_MODEL_ATTRIBUTES;
		}
		LOGGER.info("Loaded model attributes: {}", modelAttributes);
	}

	@Nonnull
	private List<String> getFiles(JsonNode node) throws IOException {
		if (node.has("file")) {
			var reader = objectMapper.readerFor(String.class);

			return List.of("/" + reader.<String>readValue(node.get("file")));
		} else {
			return Collections.emptyList();
		}
	}


	@Nonnull
	private List<String> getCss(JsonNode node) throws IOException {
		if (node.has("css")) {
			var reader = objectMapper.readerForListOf(String.class);

			return reader.<List<String>>readValue(node.get("css")).stream()
					.map(s -> "/" + s)
					.toList();
		} else {
			return Collections.emptyList();
		}
	}

	private synchronized JsonNode getMainNode() throws IOException {
		if (manifest.exists()) {
			return objectMapper.readTree(manifest.getInputStream()).get("src/vue/app.ts");
		} else {
			LOGGER.warn("Manifest file not found: {}", manifest);
			return null;
		}
	}
}

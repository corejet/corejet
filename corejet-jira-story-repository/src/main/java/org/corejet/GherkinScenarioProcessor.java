package org.corejet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import gherkin.GherkinParser;
import gherkin.formatter.Formatter;
import gherkin.lexer.LexingError;

import org.corejet.model.Story;

public class GherkinScenarioProcessor {

	private static final String COMMENT_MARKER = "//";

	public void parse(String sourceText, Story storyToPopulate) {
		Formatter corejetFormatter = new CoreJetGherkinFormatter(storyToPopulate);
		GherkinParser gherkinParser = new GherkinParser(corejetFormatter);

		String filteredSourceText = "";

		Map<String, String> gherkinReplacements = new HashMap<String, String>();

		gherkinReplacements.put("SCENARIO:", "Scenario:");
		gherkinReplacements.put("GIVEN", "Given");
		gherkinReplacements.put("WHEN", "When");
		gherkinReplacements.put("AND", "And");
		gherkinReplacements.put("THEN", "Then");

		// remove code blocks and comments
		for (String line :sourceText.replaceAll("\\{code\\}", "").split("\n")){
			line = replaceFirst(line, gherkinReplacements);
			if (!line.trim().startsWith(COMMENT_MARKER)){
				filteredSourceText += line + "\n";
			}
		}
		try {
			gherkinParser.parse("Feature: " + storyToPopulate.getTitle() + "\n" + filteredSourceText, "", 0);
		} catch (LexingError e) {
			throw new CoreJetStoryParsingException("Error parsing story with ID:" + storyToPopulate.getId(), e);
		}

	}

	private String replaceFirst(String source, Map<String, String> replacements){
		for (Entry<String, String> replacement: replacements.entrySet()){
			if (source.trim().startsWith(replacement.getKey())){
				source = source.replace(replacement.getKey(),replacement.getValue());
			}
		}
		return source;
	}

}

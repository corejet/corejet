package org.corejet.gradle.reporting

import com.google.common.io.PatternFilenameFilter
import org.codehaus.plexus.util.FileUtils
import org.corejet.model.RequirementsCatalogue
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Generate an HTML report using an xsl Template
 * Created by kwood on 09/03/15.
 */
public class HtmlReportGeneratorTask extends DefaultTask {

    public final static String NAME = "generateHtmlReport";

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlReportGeneratorTask.class);
    private static final String COREJET_TO_HTML_XSL = "/corejet-to-html.xsl";
    private static final String VISUALIZATION_LIBRARY_ARTIFACT_ID = "corejet-visualization";
    private static final String TEST_OUTPUT_DIRECTORY = "/test-output";
    private static final String REPORT_DESTINATION = "/corejet-report.html";
    private static final String TEST_OUTPUT_DESTINATION = "/corejet-report.xml";

    /**
     * @parameter expression="${corejet.report.directory}" default-value="target/corejet"
     */
    private String corejetReportDirectory;
    private File visualisation;
    private Project project;

    @TaskAction
    public void generateHtmlReport() throws Exception {
        try {
            // Merge the reports
            FilenameFilter filter = new PatternFilenameFilter("corejet-.*");
            File[] reports = new File(corejetReportDirectory + TEST_OUTPUT_DIRECTORY).listFiles(filter);

            RequirementsCatalogue result = null;
            LOGGER.info("Found " + reports.length + " XML report files");
            for (int i = 0; i < reports.length; i++) {
                LOGGER.info("Merging report " + reports[i].getName());
                result = RequirementsCatalogue.merge(new RequirementsCatalogue(new FileInputStream(reports[i])), result);
            }

            File testOutputFile = new File(corejetReportDirectory + TEST_OUTPUT_DIRECTORY + TEST_OUTPUT_DESTINATION);
            result.write(new FileOutputStream(testOutputFile));

            File outputDir = new File(corejetReportDirectory + TEST_OUTPUT_DIRECTORY);
            outputDir.mkdir();

            File report = new File(corejetReportDirectory + REPORT_DESTINATION);
            report.createNewFile();

            TransformerFactory tFactory = TransformerFactory.newInstance();

            Transformer transformer = tFactory.newTransformer(new StreamSource(this.getClass().getResourceAsStream(COREJET_TO_HTML_XSL)));

            //read the testOutput with BufferedReader
            BufferedReader reader = new BufferedReader(new FileReader(testOutputFile));

            String testOutputString = "";
            String line;
            while ((line = reader.readLine()) != null) {
                testOutputString += line.replace("'", "");
            }

            transformer.transform(new StreamSource(new ByteArrayInputStream(testOutputString.getBytes())), new StreamResult(new FileOutputStream(report)));

            // TODO Hacky fix for line breaks in json
            Map<String, String> replacements = new HashMap<String, String>();
            replacements.put("p>\r\n", "p>");
            replacements.put("div>\r\n", "div>");
            replacements.put("strong>\r\n", "strong>");
            replacements.put("br>\r\n", "br>");
            replacements.put("h2>\r\n", "h2>");
            replaceAll(report, replacements);
        } catch (Exception e) {
            throw new Exception("Failed to generate CoreJet HTML report in directory " + corejetReportDirectory, e);
        }

        // could not instantiate maven project in tests ,skip if null
        if(project.getConfigurations().size() > 0) {
            Set<File> dependencyFiles = project.configurations.compile.asFileTree.matching{ include "*$VISUALIZATION_LIBRARY_ARTIFACT_ID*" }.files;
            for (File dependencyFile : dependencyFiles) {
                visualisation = dependencyFile
            }
        }

        if (null != visualisation) {

            // getting the contents of the visualisation dependency
            JarFile jarFile;
            try {
                jarFile = new JarFile(visualisation);

                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = (JarEntry) entries.nextElement();

                    String filePath = jarEntry.getName();
                    if (filePath.contains("META-INF")) {
                        continue;
                    }
                    // Get the jar entry as a file
                    File reportFile = new File(corejetReportDirectory + File.separator + filePath);

                    // if we are looking at a directory, create it and continue
                    if (jarEntry.isDirectory()) {
                        reportFile.mkdir();
                        continue;
                    }

                    // copy the files
                    InputStream reportInput = jarFile.getInputStream(jarEntry);
                    FileOutputStream reportOutput = new FileOutputStream(reportFile);
                    while (reportInput.available() > 0) {
                        reportOutput.write(reportInput.read());
                    }
                    reportOutput.close();
                    reportInput.close();
                }
            } catch (IOException e) {
                throw new Exception("Dependency could not be copied", e);
            }
        }
    }

    private void replaceAll(File file, Map<String, String> replacements) throws Exception {
        try {
            File out = new File(corejetReportDirectory + "/temp-file.html");
            out.deleteOnExit();

            BufferedReader reader = new BufferedReader(new FileReader(file));
            PrintWriter writer = new PrintWriter(new FileWriter(out));
            String line = null;
            while ((line = reader.readLine()) != null) {
                line += "\r\n";
                for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                    line = line.replaceAll(replacement.getKey(), replacement.getValue());
                }
                writer.print(line);
            }
            reader.close();
            writer.close();

            FileUtils.copyFile(out, file);

        } catch (IOException e) {
            throw new Exception("Failed to make replacements in report", e);
        }
    }


    public String getCorejetBaseDirectory() {
        return corejetReportDirectory;
    }


    public void setCorejetBaseDirectory(String corejetBaseDirectory) {
        this.corejetReportDirectory = corejetBaseDirectory;
    }


    public File getVisualisation() {
        return visualisation;
    }


    public void setVisualisation(File visualisation) {
        this.visualisation = visualisation;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }
}

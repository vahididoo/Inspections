package com.gwservices.inspections.report;

import com.gwservices.inspections.util.*;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.*;
import com.intellij.openapi.util.io.*;
import com.intellij.openapi.util.text.*;
import com.intellij.util.io.*;
import org.apache.commons.lang.time.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jetbrains.annotations.*;

import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Roman.Chernyatchik
 */
public class ExcelInspectionConverter implements InspectionsReportConverter {
    public static final String NAME = "plain";
    private static final String FILE_ELEMENT = "file";
    private static final String LINE_ELEMENT = "line";
    private static final String PROBLEM_ELEMENT = "problem";
    private static final String DESCRIPTION_ELEMENT = "description";
    private static final String PROBLEM_CLASS_ELEMENT = "problem_class";
    private static final String SEVERITY_ATTRIBUTE = "severity";

    @Override
    public String getFormatName() {
        return NAME;
    }

    @Override
    public boolean useTmpDirForRawData() {
        return true;
    }

    @Override
    public void convert(@NotNull final String rawDataDirectoryPath, @Nullable final String outputPath, @NotNull final
    Map<String, Tools> tools, @NotNull final List<File> inspectionsResults) throws ConversionException {
        final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();

        final URL descrExtractorXsltUrl = getClass().getResource("description-text.xsl");
        final Source xslSource;
        final Transformer transformer;
        try {
            xslSource = new StreamSource(URLUtil.openStream(descrExtractorXsltUrl));
            transformer = transformerFactory.newTransformer(xslSource);
        } catch (IOException e) {
            throw new ConversionException("Cannot find inspection descriptions converter.");
        } catch (TransformerConfigurationException e) {
            throw new ConversionException("Fail to load inspection descriptions converter.");
        }
        String inspectionDate = DateFormatUtils.format(Calendar.getInstance().getTime(), "yyyy-MM-DD");

        WorkbookWrapper workbook = new WorkbookWrapper();
        WorksheetWrapper parameters = workbook.createSheet("Parameters");
        parameters.appendRow("Inspection Date", inspectionDate);

        WorksheetWrapper inspectionList = workbook.createSheet("Inspection List");

        try {
            for (File inspectionData : inspectionsResults) {
                if (inspectionData.isDirectory()) {
                    warn("Folder isn't expected here: " + inspectionData.getName());
                    continue;
                }
                final String fileNameWithoutExt = FileUtil.getNameWithoutExtension(inspectionData);
                if (InspectionApplication.DESCRIPTIONS.equals(fileNameWithoutExt)) {
                    continue;
                }

                InspectionToolWrapper toolWrapper = tools.get(fileNameWithoutExt).getTool();

                // Tool name and group

                // Description is HTML based, need to be converted in plain text
                writeInspectionDescription(inspectionList, toolWrapper, transformer);

                WorksheetWrapper problemSheet = workbook.createSheet(toolWrapper.getShortName());

                // parse xml and output results
                final SAXBuilder builder = new SAXBuilder();

                try {
                    final Document doc = builder.build(inspectionData);
                    final Element root = doc.getRootElement();

                    final List problems = root.getChildren(PROBLEM_ELEMENT);

                    for (Object problem : problems) {
                        // Format:
                        //   file_path:line_num   [severity] problem description

                        final Element fileElement = ((Element) problem).getChild(FILE_ELEMENT);
                        final String filePath = getPath(fileElement);

                        // skip suppressed results
                        if (resultsIgnored(inspectionData, toolWrapper)) {
                            continue;
                        }

                        final Element lineElement = ((Element) problem).getChild(LINE_ELEMENT);
                        final Element problemDescrElement = ((Element) problem).getChild(DESCRIPTION_ELEMENT);
                        final String severity = ((Element) problem).getChild(PROBLEM_CLASS_ELEMENT).getAttributeValue
                                (SEVERITY_ATTRIBUTE);

                        final String fileLineNum = lineElement.getText();
                        problemSheet.appendRow(filePath, fileLineNum, problemDescrElement.getText());
                    }
                } catch (JDOMException e) {
                    throw new ConversionException("Unknown results format, file = " + inspectionData.getPath() + ". "
                            + "Error: " + e.getMessage());
                }

            }
        } catch (IOException e) {
            throw new ConversionException("Cannot write inspection results: " + e.getMessage());
        } finally {

        }

        workbook.write(outputPath + File.pathSeparator + "Inspections_Result_" + inspectionDate);

    }

    private int getMaxFileColonLineNumLength(@NotNull final File inspectionResultData, @NotNull final
    InspectionToolWrapper toolWrapper, @NotNull final List problems) {
        int maxFileColonLineLength = 0;
        for (Object problem : problems) {
            final Element fileElement = ((Element) problem).getChild(FILE_ELEMENT);
            final Element lineElement = ((Element) problem).getChild(LINE_ELEMENT);

            final String filePath = getPath(fileElement);
            // skip suppressed results
            if (resultsIgnored(inspectionResultData, toolWrapper)) {
                continue;
            }

            maxFileColonLineLength = Math.max(maxFileColonLineLength, filePath.length() + 1 + lineElement.getText()
                                                                                                         .length());
        }
        return maxFileColonLineLength;
    }

    private void warn(String msg) {
        System.err.println(msg);
    }

    private boolean resultsIgnored(@NotNull final File file, @NotNull final InspectionToolWrapper toolWrapper) {
        // TODO: check according to config
        return false;
    }

    @NotNull
    protected String getPath(@NotNull final Element fileElement) {
        return fileElement.getText().replace("file://$PROJECT_DIR$", ".");
    }

    protected void writeInspectionDescription(@NotNull final WorksheetWrapper sheet, @NotNull final
    InspectionToolWrapper toolWrapper, @NotNull final Transformer transformer) throws IOException, ConversionException {

        final StringWriter descrWriter = new StringWriter();
        String descr = toolWrapper.loadDescription();
        if (descr == null) {
            return;
        }
        // convert line ends to xml form
        descr = descr.replace("<br>", "<br/>");

        try {

            transformer.transform(new StreamSource(new StringReader(descr)), new StreamResult(descrWriter));
        } catch (TransformerException e) {
            // Not critical problem, just inspection error cannot be loaded
            warn("ERROR:  Cannot load description for inspection: " + getToolPresentableName(toolWrapper) + ".\n     "
                    + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "   Error message: " + e.getMessage());
            return;
        }

        String description = "";
        final String trimmedDesc = descrWriter.toString().trim();
        final String[] descLines = StringUtil.splitByLines(trimmedDesc);
        if (descLines.length > 0) {
            for (String descLine : descLines) {
                description += "  " + descLine.trim() + "\n";
            }
        }

        sheet.appendRow(toolWrapper.getShortName(), toolWrapper.getDisplayName(), description);

    }

    @NotNull
    protected String getToolPresentableName(@NotNull final InspectionToolWrapper toolWrapper) throws IOException {
        final StringBuilder buff = new StringBuilder();

        // inspection name
        buff.append(toolWrapper.getDisplayName()).append(" (");

        // group name
        final String[] groupPath = toolWrapper.getGroupPath();
        for (int i = 0, groupPathLength = groupPath.length; i < groupPathLength; i++) {
            if (i != 0) {
                buff.append(" | ");
            }
            buff.append(groupPath[i]);
        }
        buff.append(")");

        return buff.toString();
    }
}

package ca.vivic.util.flatten_deflatten;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.*;
import org.w3c.dom.*;

public final class FlattenDeflatten {
	
	final static Logger logger = Logger.getLogger(FlattenDeflatten.class);
	private static Document xmldoc;
	private static TreeSet<String> fileTypes = new TreeSet<String>();
	
	public static final String[] interestedExtensions = new String[] {"pom", "java", "properties", "xml", "cfg", "ini", 
															"txt", "bak", "wsdl", "xsd", "bat", "sh", "bash", 
															"profile"};
	public static final Set<String> interestedExtSet = new HashSet<String>(Arrays.asList(interestedExtensions));	
	
	public static final String[] excludedFolders = new String[] {"target", ".svn"};
	public static final Set<String> excludedFolderSet = new HashSet<String>(Arrays.asList(excludedFolders));		

	public static void main(String[] args) throws IOException {
			
		StringWriter stringWriter = new StringWriter();
		flatten(".", stringWriter);
		logger.info("stringWriter=" + stringWriter);
		stringWriter.close();		
		logger.info("fileTypes=" + Arrays.toString(fileTypes.toArray()));
		
		FileWriter fileWriter = new FileWriter("c:/apps/temp/flattened.xml");
		flatten(".", fileWriter);
		fileWriter.close();
	}
	
	

	static private void recursiveList(File currentFile, Element parent,
			String parentdir) {
		File[] listOfFiles = currentFile.listFiles();
		for (File aFile : listOfFiles) {
			Element e = xmldoc.createElementNS(null, "ENTRY");
			String name = aFile.getName();
			e.setAttributeNS(null, "Parent", parentdir);
			e.setAttributeNS(null, "Name", name);
			if (aFile.isDirectory()) {
				if (excludedFolderSet.contains(name)) {
					continue;
				}
				e.setAttributeNS(null, "isFolder", "true");
				recursiveList(aFile, e, name);
			} else {  //file
				String ext = FilenameUtils.getExtension(name);
				if (!interestedExtSet.contains(ext)) {
					continue;
				}
				e.setAttributeNS(null, "Ext", ext);
				e.setAttributeNS(null, "isFolder", "false");
			}
			parent.appendChild(e);
		}
	}

	
	
	public static void flatten(String targetRootPath, Writer writer) throws IOException {
		File startingDirectory = new File(targetRootPath);

		xmldoc = new DocumentImpl();
		Element root = xmldoc.createElement("LISTING");
		recursiveList(startingDirectory, root, startingDirectory.getName());
		xmldoc.appendChild(root);
		
		OutputFormat of = new OutputFormat("XML", "ISO-8859-1", true);
		of.setIndent(1);
		of.setIndenting(true);
		XMLSerializer serializer = new XMLSerializer(writer, of);
		serializer.asDOMSerializer();
		serializer.serialize(xmldoc.getDocumentElement());
		
	}
	
	
	
	


}
package com.phinvader.libjdcpp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DCFileList {
	public String name = "";
	public long size = 0;
	public List<DCFileList> children = null;

	/**
	 * Returns if the current entry is a directory.
	 * 
	 * @return
	 */
	public Boolean isDirectory() {
		return (children != null);
	}

	public String toString() {
		String ret = name + " (" + size + ") ";
		if (children != null) {
			ret += "{\n";
			for (DCFileList child : children) {
				ret += "(\n" + child.toString() + "),\n";
			}
			ret += "}\n";
		} else {
			ret += "\n";
		}

		return ret;
	}

	private DCFileList(String name, long size) {
		this.name = name;
		this.size = size;
	}

	private static DCFileList build_file_list(Element node, String name) {
		DCFileList ret = new DCFileList(name, 0);
		ret.children = new ArrayList<>();
		NodeList cl = node.getChildNodes();
		for (int i = 0; i < cl.getLength(); i++) {
			Node nd = cl.item(i);
			if (nd.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) nd;
				String c_name = e.getAttribute("Name");
				DCFileList child_entry = null;
				if (e.getTagName().equalsIgnoreCase("DIRECTORY")) {
					child_entry = build_file_list(e, c_name);
				} else {
					child_entry = new DCFileList(c_name, Long.parseLong(e
							.getAttribute("Size")));
				}
				ret.size += child_entry.size;
				ret.children.add(child_entry);
			}
		}
		return ret;
	}

	/**
	 * Call this static factory function on a readable XML file to parse the
	 * file list into an object of the DCFileList data type. Member variables
	 * name,size,children are publicly accessible to walk the file list.
	 * 
	 * @param xml_file
	 * @return
	 */
	public static DCFileList parseXML(File xml_file) {
		try {
			File fXmlFile = xml_file;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			return build_file_list(doc.getDocumentElement(), "/");

		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			DCLogger.Log("ERROR - Failed to parse XML : 004-001	 ");
			e.printStackTrace();
			return null;
		}
	}
}

package com.phinvader.libjdcpp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
		ret.children = new ArrayList<DCFileList>();
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
	 * ***DEPRECATED*** This is the old function renamed as a copy, do not use this.
	 * Call this static factory function on a readable XML file to parse the
	 * file list into an object of the DCFileList data type. Member variables
	 * name,size,children are publicly accessible to walk the file list.
	 * 
	 * @param xml_file
	 * @return
	 */
	public static DCFileList parseDomXML(File xml_file) {
		try {
			File fXmlFile = xml_file;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			return build_file_list(doc.getDocumentElement(), "/");

		} catch (SAXException e) {
			DCLogger.Log("ERROR - Failed to parse XML : 004-001	 ");
			e.printStackTrace();
		} catch (IOException e) {
			DCLogger.Log("ERROR - Failed to parse XML : 004-001	 ");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			DCLogger.Log("ERROR - Failed to parse XML : 004-001	 ");
			e.printStackTrace();
		}
		return null;
	}

	private static class FileListSAXHandler extends DefaultHandler {
		private Stack<DCFileList> tree_walk;

		public FileListSAXHandler() {
			tree_walk = new Stack<DCFileList>();
			DCFileList root = new DCFileList("/", 0);
			root.children = new ArrayList<DCFileList>();
			tree_walk.push(root);
		}

		public DCFileList get_file_list() {
			DCFileList ret = tree_walk.pop();
			ret.size = 0;
			for (DCFileList o : ret.children)
				ret.size += o.size;
			return ret;
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equalsIgnoreCase("DIRECTORY")) {
				String name = attributes.getValue("Name");
				DCFileList dir = new DCFileList(name, 0);
				dir.children = new ArrayList<DCFileList>();
				tree_walk.peek().children.add(dir);
				tree_walk.push(dir);
			} else if (qName.equalsIgnoreCase("FILE")) {
				String name = attributes.getValue("Name");
				long size = Long.parseLong(attributes.getValue("Size"));
				DCFileList file = new DCFileList(name, size);
				tree_walk.peek().children.add(file);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (qName.equalsIgnoreCase("DIRECTORY")) {
				DCFileList dir = tree_walk.pop();
				dir.size = 0;
				for (DCFileList o : dir.children)
					dir.size += o.size;
			}
		}
	}

	/**
	 * Call this static factory function on a readable XML file to parse the
	 * file list into an object of the DCFileList data type. Member variables
	 * name,size,children are publicly accessible to walk the file list.
	 * @param xml_file
	 * @return
	 */
	public static DCFileList parseXML(File xml_file) {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			FileListSAXHandler handler = new FileListSAXHandler();
			saxParser.parse(xml_file, handler);
			return handler.get_file_list();
		} catch (ParserConfigurationException e) {
			DCLogger.Log("ERROR - Failed to parse XML : 004-001	 ");
			e.printStackTrace();
		} catch (SAXException e) {
			DCLogger.Log("ERROR - Failed to parse XML : 004-001	 ");
			e.printStackTrace();
		} catch (IOException e) {
			DCLogger.Log("ERROR - Failed to parse XML : 004-001	 ");
			e.printStackTrace();
		}
		return null;
	}
}

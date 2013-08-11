package com.phinvader.libjdcpp;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

public class DCFileListTest {

	@Test
	public void parse_test() {
		DCFileList fl = DCFileList.parseXML(new File("tests/resources/dc_file_list_sample.xml"));
	}
	
	@Test
	public void parse_value_test() {
		DCFileList fl = DCFileList.parseXML(new File("tests/resources/dc_file_list_test.xml"));
		ArrayList<String> names = new ArrayList<String>();
		for (DCFileList f : fl.children)
			names.add(f.name);
		assertArrayEquals(names.toArray(), new String[]{"abc"});
		
		fl = fl.children.get(0);
		names.clear();
		for (DCFileList f : fl.children)
			names.add(f.name);
		assertArrayEquals(names.toArray(), new String[]{"file1", "file2", "dir1"});
	}

}

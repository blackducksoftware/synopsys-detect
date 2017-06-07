package com.blackducksoftware.integration.hub.packman.packagemanager.npm;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.bomtool.NpmBomTool;
import com.blackducksoftware.integration.hub.packman.bomtool.npm.NpmCliDependencyFinder
import com.blackducksoftware.integration.hub.packman.bomtool.npm.NpmParser;
import com.google.gson.Gson

public class NpmOutputParserTest {
	
	@Test
	public void npmParserTest() throws IOException {
		def parser = new NpmCliDependencyFinder()
		def testIn = new File(getClass().getResource("/npm/packman_proj_dependencies.json").getFile())
		
		parser.setGson(new Gson())
		
		DependencyNode node = parser.convertNpmJsonFileToDependencyNode(testIn)
		def testOut = new File(getClass().getResource("/npm/npmParseOutput.txt").getFile())
		
		assertTrue(node.toString().contentEquals(testOut.text))
	}

}

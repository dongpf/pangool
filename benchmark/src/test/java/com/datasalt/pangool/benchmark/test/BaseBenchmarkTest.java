package com.datasalt.pangool.benchmark.test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.datasalt.pangool.test.AbstractHadoopTestLibrary;
import com.google.common.io.Files;

public abstract class BaseBenchmarkTest extends AbstractHadoopTestLibrary{

	public String getReducerOutputAsText(String outputDir) throws IOException {
		return getOutputAsText(outputDir + "/part-r-00000");
	}
	
	public String getOutputAsText(String outFile) throws IOException {
		return Files.toString(new File(outFile), Charset.forName("UTF-8"));
	}
	
}

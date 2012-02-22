/**
 * Copyright [2012] [Datasalt Systems S.L.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datasalt.pangool.benchmark.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.datasalt.pangool.benchmark.sansaccentsorting.SansAccentsCustomComparator;
import com.datasalt.pangool.benchmark.sansaccentsorting.SansAccentRepeatedField;
import com.datasalt.pangool.utils.HadoopUtils;

/**
 * This unit test verifies that each of the word count implementations can be run and that they give the same output
 * given a test input.
 */
public class TestSansAccentSorting extends BaseBenchmarkTest {

	private final static String FOLDER = "src/test/resources/sans_accent_sorting";
	private final static String TEST_FILE = FOLDER + "/spanish_words.txt";

	private final static String EXPECTED_OUTPUT = FOLDER + "/spanish_alphabetic_order.txt";
	private final static String OUT_REPEATING = FOLDER + "/out-sansaccent-repeating";
	private final static String OUT_CUSTOM_COMPARATOR = FOLDER + "/out-sansaccent-custom";
	

	@Before
	@After
	public void prepare() throws IOException {
		Logger root = Logger.getRootLogger();
		root.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
		Configuration conf = new Configuration();
		FileSystem fS = FileSystem.get(conf);
		HadoopUtils.deleteIfExists(fS, new Path(OUT_REPEATING));
		HadoopUtils.deleteIfExists(fS, new Path(OUT_CUSTOM_COMPARATOR));
		
	}

	@Test
	public void testRepeatingFields() throws Exception {
		Configuration conf = new Configuration();
		Job job = new SansAccentRepeatedField().getJob(conf,TEST_FILE,OUT_REPEATING);
		assertRun(job);
		
		String out = getReducerOutputAsText(OUT_REPEATING); //Very bad, consumes a lot of memory
		String expectedOutput = getOutputAsText(EXPECTED_OUTPUT);//Very bad, consumes a lot of memory
		assertEquals(expectedOutput,out);
	}
	
	@Ignore
	@Test
	public void testCustomComparator() throws Exception {
		Configuration conf = new Configuration();
		Job job = new SansAccentsCustomComparator().getJob(conf,TEST_FILE, OUT_CUSTOM_COMPARATOR);
		assertRun(job);
		
		String out = getReducerOutputAsText(OUT_CUSTOM_COMPARATOR);
		String expectedOutput = getOutputAsText(EXPECTED_OUTPUT);
		assertEquals(expectedOutput,out);
	}
	
	
	
}

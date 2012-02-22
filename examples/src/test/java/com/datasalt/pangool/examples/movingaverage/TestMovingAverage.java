package com.datasalt.pangool.examples.movingaverage;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.ParseException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

import com.datasalt.pangool.cogroup.CoGrouperException;
import com.datasalt.pangool.utils.HadoopUtils;
import com.google.common.io.Files;

public class TestMovingAverage {
	private final static String INPUT = "test-input-" + TestMovingAverage.class.getName();
	private final static String OUTPUT = "test-output-" + TestMovingAverage.class.getName();
	
	@Test
	public void test() throws IOException, CoGrouperException, InterruptedException,
	    ClassNotFoundException, URISyntaxException, ParseException {

		Configuration conf = new Configuration();
		FileSystem fS = FileSystem.get(conf);
		HadoopUtils.deleteIfExists(fS, new Path(OUTPUT));
		Files.write(
				"url1" + "\t" + "2011-02-28" + "\t" + "100" + "\n" +
				"url1" + "\t" + "2011-02-27" + "\t" + "50" + "\n" +
				"url1" + "\t" + "2011-02-24" + "\t" + "25" + "\n" +
				"url2" + "\t" + "2011-02-28" + "\t" + "100" + "\n" +
				"url2" + "\t" + "2011-02-26" + "\t" + "50" + "\n" +
				"url2" + "\t" + "2011-02-25" + "\t" + "25" + "\n"
		, new File(INPUT), Charset.forName("UTF-8"));

		MovingAverage mAverage = new MovingAverage();
		mAverage.getJob(conf, INPUT, OUTPUT, 3).waitForCompletion(true);

		int validatedOutputLines = 0;
		for(String line : Files.readLines(new File(OUTPUT + "/part-r-00000"), Charset.forName("UTF-8"))) {
			String[] fields = line.split("\t");
			if(fields[0].equals("url1")) {
				if(fields[1].equals("2011-02-28")) {
					assertEquals("75.0", fields[2]);
					validatedOutputLines++;
				} else if(fields[1].equals("2011-02-27")) {
					assertEquals("50.0", fields[2]);
					validatedOutputLines++;
				} else if(fields[1].equals("2011-02-24")) {
					assertEquals("25.0", fields[2]);
					validatedOutputLines++;
				}
			} else if(fields[0].equals("url2")) {
				if(fields[1].equals("2011-02-28")) {
					assertEquals("75.0", fields[2]);
					validatedOutputLines++;
				} else if(fields[1].equals("2011-02-26")) {
					assertEquals("37.5", fields[2]);
					validatedOutputLines++;
				} else if(fields[1].equals("2011-02-25")) {
					assertEquals("25.0", fields[2]);
					validatedOutputLines++;
				}				
			}
		}
		
		assertEquals(6, validatedOutputLines);

		HadoopUtils.deleteIfExists(fS, new Path(INPUT));
		HadoopUtils.deleteIfExists(fS, new Path(OUTPUT));
	}
}
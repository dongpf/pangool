package com.datasalt.pangool.io;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.junit.Test;

import com.datasalt.pangool.BaseCoGrouperTest;
import com.datasalt.pangool.CoGrouper;
import com.datasalt.pangool.CoGrouperException;
import com.datasalt.pangool.Criteria.Order;
import com.datasalt.pangool.Schema;
import com.datasalt.pangool.Schema.Field;
import com.datasalt.pangool.SortBy;
import com.datasalt.pangool.api.GroupHandler;
import com.datasalt.pangool.api.IdentityGroupHandler;
import com.datasalt.pangool.api.IdentityInputProcessor;
import com.datasalt.pangool.api.InputProcessor;
import com.datasalt.pangool.commons.CommonUtils;
import com.datasalt.pangool.commons.HadoopUtils;
import com.datasalt.pangool.io.tuple.ITuple;
import com.datasalt.pangool.io.tuple.ITuple.InvalidFieldException;
import com.datasalt.pangool.io.tuple.Tuple;
import com.google.common.io.Files;

public class TestTupleInputOutputFormat extends BaseCoGrouperTest {

	public static String OUT = TestTupleInputOutputFormat.class.getName() + "-out";
	public static String OUT_TEXT = TestTupleInputOutputFormat.class.getName() + "-out-text";
	public static String IN = TestTupleInputOutputFormat.class.getName() + "-in";

	public static class MyInputProcessor extends InputProcessor<LongWritable, Text> {

    private static final long serialVersionUID = 1L;
		private Tuple tuple;

		@Override
		public void process(LongWritable key, Text value, CoGrouperContext context, Collector collector) throws IOException, InterruptedException {
			if (tuple == null){
				tuple = new Tuple(context.getCoGrouperConfig().getSourceSchema(0));
			}
			tuple.set(0, "title");
			tuple.set(1, value);
			collector.write(tuple);
		}
	}

	public static class MyGroupHandler extends GroupHandler<Text, Text> {

    private static final long serialVersionUID = 1L;

    @Override
		public void onGroupElements(ITuple group, Iterable<ITuple> tuples, CoGrouperContext context,
		    Collector collector) throws IOException, InterruptedException, CoGrouperException {
			for(ITuple tuple : tuples) {
				collector.write((Text)tuple.get(0),(Text)tuple.get(1));
			}
		}
	}

	@Test
	public void test() throws InvalidFieldException, CoGrouperException, IOException, InterruptedException,
	    ClassNotFoundException {

		CommonUtils.writeTXT("foo1 bar1\nbar2 foo2", new File(IN));
		Configuration conf = new Configuration();
		FileSystem fS = FileSystem.get(conf);
		Path outPath = new Path(OUT);
		Path inPath = new Path(IN);
		Path outPathText = new Path(OUT_TEXT);
		HadoopUtils.deleteIfExists(fS, outPath);
		HadoopUtils.deleteIfExists(fS, outPathText);

		List<Field> fields = new ArrayList<Field>();
		fields.add(new Field("title",String.class));
		fields.add(new Field("content",String.class));
		Schema schema = new Schema("schema",fields);
		
		CoGrouper coGrouper = new CoGrouper(conf);
		coGrouper.addSourceSchema(schema);
		coGrouper.setGroupByFields("title");
		coGrouper.setOrderBy(new SortBy().add("title",Order.ASC).add("content",Order.ASC));

		coGrouper.setGroupHandler(new IdentityGroupHandler());
		coGrouper.setTupleOutput(outPath, schema); // setTupleOutput method
		coGrouper.addInput(inPath, TextInputFormat.class, new MyInputProcessor());

		coGrouper.createJob().waitForCompletion(true);

		// Use output as input of new CoGrouper

		coGrouper = new CoGrouper(conf);
		coGrouper.addSourceSchema(schema);
		coGrouper.setGroupByFields("title");
		coGrouper.setOrderBy(new SortBy().add("title",Order.ASC).add("content",Order.ASC));
		coGrouper.setGroupHandler(new MyGroupHandler());
		coGrouper.setOutput(outPathText, TextOutputFormat.class, Text.class, Text.class);
		coGrouper.addTupleInput(outPath, new IdentityInputProcessor()); // addTupleInput method
		coGrouper.createJob().waitForCompletion(true);

		Assert.assertEquals("title\tbar2 foo2\ntitle\tfoo1 bar1",
		    Files.toString(new File(OUT_TEXT + "/" + "part-r-00000"), Charset.forName("UTF-8")).trim());

		HadoopUtils.deleteIfExists(fS, inPath);
		HadoopUtils.deleteIfExists(fS, outPath);
		HadoopUtils.deleteIfExists(fS, outPathText);
	}
}
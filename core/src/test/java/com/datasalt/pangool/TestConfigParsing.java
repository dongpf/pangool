package com.datasalt.pangool;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.TaskDistributedCacheManager;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.io.VLongWritable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.datasalt.pangolin.thrift.test.A;
import com.datasalt.pangool.Criteria.Order;
import com.datasalt.pangool.Schema.Field;

public class TestConfigParsing {

	private Schema schema1;
	private Schema schema2;
	private Schema schema3;
	
	@Before
	public void init() throws CoGrouperException {
		this.schema1 = new Schema("schema1", Fields.parse("int_field:int, str_field:string,boolean_field:boolean"));
		this.schema2 = new Schema("schema2", Fields.parse("long_field:long,boolean_field:boolean, int_field:int"));

		List<Field> fields = new ArrayList<Field>();
		fields.add(new Field("int_field", Integer.class));
		fields.add(new Field("string_field", String.class));
		fields.add(new Field("vint_field", VIntWritable.class));
		fields.add(new Field("vlong_field", VLongWritable.class));
		fields.add(new Field("float_field", Float.class));
		fields.add(new Field("double_field", Double.class));
		fields.add(new Field("boolean_field", Boolean.class));
		fields.add(new Field("enum_field", Order.class));
		fields.add(new Field("thrift_field", A.class));
		this.schema3 = new Schema("schema3", fields);

	}
	
	private static class DummyComparator implements RawComparator, Serializable {
		@Override
    public int compare(Object arg0, Object arg1) {
	    return 0;
    }

		@Override
    public int compare(byte[] arg0, int arg1, int arg2, byte[] arg3, int arg4, int arg5) {
	    return 0;
    }
		
		@Override
		public boolean equals(Object obj) {
		  return obj instanceof DummyComparator;
		}
	}
	
	@Test
	public void test1() throws CoGrouperException, IOException {
		ConfigBuilder b = new ConfigBuilder();
		b.addSourceSchema(schema1);
		b.addSourceSchema(schema2);
		b.addSourceSchema(schema3);
		b.setGroupByFields("int_field");
		b.setOrderBy(new SortBy().add("int_field",Order.DESC).addSourceOrder(Order.DESC).add("boolean_field",Order.DESC, new DummyComparator()));
		b.setRollupFrom("int_field");
		b.setSecondaryOrderBy(schema3.getName(),new SortBy().add("enum_field", Order.ASC, new DummyComparator()));
		
		CoGrouperConfig conf =b.buildConf();
		Configuration hconf = new Configuration();
		
		CoGrouperConfig.set(conf, hconf);
		CoGrouperConfig deserConf = CoGrouperConfig.get(hconf);
		System.out.println(conf);
		System.out.println("------------");
		System.out.println(deserConf);

		Assert.assertEquals(conf,deserConf);
		hconf = new Configuration();
		CoGrouperConfig.set(deserConf, hconf);
		CoGrouperConfig deserConf2 = CoGrouperConfig.get(hconf);
		Assert.assertEquals(conf,deserConf2);
	}
}
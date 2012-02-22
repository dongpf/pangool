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
package com.datasalt.pangool.cogroup.processors;

import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.ReduceContext;
import org.apache.hadoop.mapreduce.Reducer;

import com.datasalt.pangool.cogroup.CoGrouper;
import com.datasalt.pangool.cogroup.CoGrouperConfig;
import com.datasalt.pangool.cogroup.CoGrouperException;
import com.datasalt.pangool.cogroup.MultipleOutputsCollector;
import com.datasalt.pangool.io.tuple.ITuple;
import com.datasalt.pangool.io.tuple.DatumWrapper;
import com.datasalt.pangool.mapreduce.RollupReducer;
import com.datasalt.pangool.mapreduce.SimpleReducer;

/**
 * 
 * This is the common interface that any {@link CoGrouper} job needs to implement. This handler is called in the reducer
 * step by {@link SimpleReducer} or {@link RollupReducer} depending if Roll-up feature is used.
 */
@SuppressWarnings("serial")
public class GroupHandler<OUTPUT_KEY, OUTPUT_VALUE> implements Serializable {
	
	public void setup(CoGrouperContext coGrouperContext, Collector collector)
	    throws IOException, InterruptedException, CoGrouperException {

	}

	/**
	 * 
	 * This method is called with an iterable that contains all the tuples that have been grouped by the fields defined
	 * in {@link Grouper#setFieldsToGroupBy(String...)}
	 * 
	 * @param tuples
	 *          Iterable that contains all the tuples from a group
	 * @param context
	 *          The reducer context as in {@link Reducer}
	 */
	public void onGroupElements(ITuple group, Iterable<ITuple> tuples, CoGrouperContext coGrouperContext, Collector collector) throws IOException, InterruptedException,
	    CoGrouperException {

	}
	
	public void cleanup(CoGrouperContext coGrouperContext, Collector collector)
	    throws IOException, InterruptedException, CoGrouperException {
	}
	
	/* ------------ INNER CLASSES ------------ */
	
	/**
	 * 	A base class for the {@link GroupHandler.Collector}
	 */
	public static class StaticCollector<OUTPUT_KEY, OUTPUT_VALUE> extends MultipleOutputsCollector {

		protected ReduceContext<DatumWrapper<ITuple>, NullWritable, OUTPUT_KEY, OUTPUT_VALUE> context;
		
    public StaticCollector(ReduceContext<DatumWrapper<ITuple>, NullWritable, OUTPUT_KEY, OUTPUT_VALUE> context) {
	    super(context);
	    this.context = context;
    }
		
		public void write(OUTPUT_KEY key, OUTPUT_VALUE value) throws IOException, InterruptedException {
			context.write(key, value);
		}
	}
	
	public class Collector extends StaticCollector<OUTPUT_KEY, OUTPUT_VALUE> {
		/*
		 * This non static inner class is created to eliminate the need in
		 * of the extended GroupHandler methods to specify the generic types
		 * for the Collector meanwhile keeping generics. 
		 */
		public Collector(ReduceContext<DatumWrapper<ITuple>, NullWritable, OUTPUT_KEY, OUTPUT_VALUE> context) {
	    super(context);
    }		
	}
	
  public static class StaticCoGrouperContext<OUTPUT_KEY, OUTPUT_VALUE> {
  	
  	private CoGrouperConfig pangoolConfig;
  	private ReduceContext<DatumWrapper<ITuple>, NullWritable, OUTPUT_KEY, OUTPUT_VALUE> hadoopContext;
  	
  	public StaticCoGrouperContext(ReduceContext<DatumWrapper<ITuple>, NullWritable, OUTPUT_KEY, OUTPUT_VALUE> hadoopContext, CoGrouperConfig pangoolConfig) {
  		this.pangoolConfig = pangoolConfig;
  		this.hadoopContext = hadoopContext;
  	}

  	public CoGrouperConfig getCoGrouperConfig() {
  		return pangoolConfig;
  	}
  	  	
  	/**
  	 * Return the Hadoop {@link ReduceContext}.  
  	 */
  	public ReduceContext<DatumWrapper<ITuple>, NullWritable, OUTPUT_KEY, OUTPUT_VALUE> getHadoopContext() {
  		return hadoopContext;
  	}
  }
  
  public class CoGrouperContext extends StaticCoGrouperContext<OUTPUT_KEY, OUTPUT_VALUE> {
		/*
		 * This non static inner class is created to eliminate the need in
		 * of the extended GroupHandler methods to specify the generic types
		 * for the CoGrouperContext meanwhile keeping generics. 
		 */
		public CoGrouperContext(ReduceContext<DatumWrapper<ITuple>, NullWritable, OUTPUT_KEY, OUTPUT_VALUE> hadoopContext,
        CoGrouperConfig pangoolConfig) {
      super(hadoopContext, pangoolConfig);
    }    	
  }
}

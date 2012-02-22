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
package com.datasalt.pangool.cogroup;

import java.io.IOException;

import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.ReduceContext;
import org.apache.hadoop.mapreduce.Reducer;

import com.datasalt.pangool.io.PangoolMultipleOutputs;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class MultipleOutputsCollector {

  PangoolMultipleOutputs multipleOutputs;
	
	public MultipleOutputsCollector(MapContext context) {
		multipleOutputs = new PangoolMultipleOutputs(context);
	}
	
	public MultipleOutputsCollector(ReduceContext context) {
		multipleOutputs = new PangoolMultipleOutputs(context);
	}
	
	public <K, V> RecordWriter<K, V> getNamedOutput(String namedOutput) throws IOException, InterruptedException {
		return multipleOutputs.getRecordWriter(namedOutput);
	}
	
	public <K, V>void write(String namedOutput, K key, V value) throws IOException, InterruptedException {
		multipleOutputs.write(namedOutput, key, value);
	}
	
	public void close() throws IOException, InterruptedException {
		multipleOutputs.close();
	}
}

package com.datasalt.pangool;

import java.io.IOException;

import com.datasalt.pangolin.grouper.GrouperException;
import com.datasalt.pangool.api.GroupHandler;
import com.datasalt.pangool.api.GroupHandlerWithRollup;
import com.datasalt.pangool.api.InputProcessor;
import com.datasalt.pangool.io.tuple.ITuple.InvalidFieldException;
import com.datasalt.pangool.CoGrouperException;
import com.datasalt.pangool.Sorting;
import com.datasalt.pangool.SortingBuilder;
import com.datasalt.pangool.SortCriteria.SortOrder;

@SuppressWarnings("rawtypes")
public class BaseCoGrouperTest {

	protected GroupHandler myGroupHandler = new GroupHandler();
	
	protected GroupHandler myGroupHandlerWithRollup = new GroupHandlerWithRollup();

	protected InputProcessor myInputProcessor = new InputProcessor() {

		@Override
    public void process(Object key, Object value, Collector collector) {
    }		
	};
	
	protected Sorting getTestSorting() throws InvalidFieldException, CoGrouperException {
		return new SortingBuilder()
			.add("url", SortOrder.ASC)
			.add("date", SortOrder.DESC)
			.addSourceId(SortOrder.ASC)
			.secondarySort(1).add("content", SortOrder.ASC)
			.secondarySort(2).add("name", SortOrder.ASC)
			.buildSorting();
	}
}
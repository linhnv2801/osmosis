package com.bretth.osm.conduit.pipeline;

import java.util.Map;

import com.bretth.osm.conduit.task.ChangeSink;
import com.bretth.osm.conduit.task.ChangeSource;
import com.bretth.osm.conduit.task.Sink;
import com.bretth.osm.conduit.task.SinkChangeSinkSource;
import com.bretth.osm.conduit.task.Source;
import com.bretth.osm.conduit.task.Task;


/**
 * A task manager implementation for SinkChangeSinkSource task implementations.
 * 
 * @author Brett Henderson
 */
public class SinkChangeSinkSourceManager extends TaskManager {
	private SinkChangeSinkSource task;
	
	
	/**
	 * Creates a new instance.
	 * 
	 * @param taskId
	 *            A unique identifier for the task. This is used to produce
	 *            meaningful errors when errors occur.
	 * @param task
	 *            The task instance to be managed.
	 * @param pipeArgs
	 *            The arguments defining input and output pipes for the task,
	 *            pipes are a logical concept for identifying how the tasks are
	 *            connected together.
	 */
	public SinkChangeSinkSourceManager(String taskId, SinkChangeSinkSource task, Map<String, String> pipeArgs) {
		super(taskId, pipeArgs);
		
		this.task = task;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connect(Map<String, Task> pipeTasks) {
		// A multi sink receives multiple streams of data, so we must connect
		// them up one by one. In this case we will connect the sinks and then
		// the change sinks.
		for (int i = 0; i < task.getSinkCount(); i++) {
			Sink sink;
			Source source;
			
			// Retrieve the next sink.
			sink = task.getSink(i);
			
			// Retrieve the appropriate source.
			source = (Source) getInputTask(pipeTasks, i, Source.class);
			
			// Connect the tasks.
			source.setSink(sink);
		}
		for (int i = 0; i < task.getChangeSinkCount(); i++) {
			ChangeSink changeSink;
			ChangeSource changeSource;
			
			// Retrieve the next sink.
			changeSink = task.getChangeSink(i);
			
			// Retrieve the appropriate source.
			changeSource = (ChangeSource) getInputTask(
				pipeTasks,
				i + task.getSinkCount(),
				ChangeSource.class
			);
			
			// Connect the tasks.
			changeSource.setChangeSink(changeSink);
		}
		
		// Register the change source as an output task.
		setOutputTask(pipeTasks, task, 0);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		// Nothing to do for a sink because it passively receives data.
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void waitForCompletion() {
		// Nothing to do for a sink because it passively receives data.
	}
}
package org.apache.giraph.examples.closeness;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Writable;

public class VertexStateWritable implements Writable {
  private BitfieldCounterWritable counter;
  private OpenLongLongHashMapWritable shortestPaths;
  
  public VertexStateWritable() {
    this.counter = new BitfieldCounterWritable();
    this.shortestPaths = new OpenLongLongHashMapWritable();
  }
  public VertexStateWritable(Configuration conf) {
    counter = new BitfieldCounterWritable(conf);
    shortestPaths = new OpenLongLongHashMapWritable();
  }
  
  public BitfieldCounterWritable getCounter() {
    return counter;
  }

  public OpenLongLongHashMapWritable getShortestPaths() {
    return shortestPaths;
  }

  @Override
  public void write(DataOutput out) throws IOException {
    counter.write(out);
    shortestPaths.write(out);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    counter.readFields(in);
    shortestPaths.readFields(in);
  }
}

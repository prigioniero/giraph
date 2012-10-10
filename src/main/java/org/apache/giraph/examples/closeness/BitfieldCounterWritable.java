package org.apache.giraph.examples.closeness;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Writable;

import com.google.common.base.Preconditions;

public class BitfieldCounterWritable implements Writable {
  public final static String NUM_BITS = "distinctcounter.numbits";
  public final static int HARD_NUM_BITS = 32;
  private int numBits;
  private int[] bits;

  public BitfieldCounterWritable() {
    this.numBits = HARD_NUM_BITS;
    int numInts = (int) Math.ceil(this.numBits / 32.0);
    this.bits = new int[numInts];
  }
  
  public BitfieldCounterWritable(int numBits) {
    this.numBits = HARD_NUM_BITS;
    int numInts = (int) Math.ceil(this.numBits / 32.0);
    bits = new int[numInts];
  }
  
  public BitfieldCounterWritable(Configuration config) {
    //this.numBits = config.getInt(NUM_BITS, 32);
    this.numBits = HARD_NUM_BITS;
    int numInts = (int) Math.ceil(this.numBits / 32.0);
    bits = new int[numInts];
  }
  
  public BitfieldCounterWritable copy() {
    BitfieldCounterWritable result = new BitfieldCounterWritable();
    result.numBits = this.numBits;
    result.bits = Arrays.copyOf(this.bits, this.bits.length);
    return result;
  }

  public void addNode(long n) {
    int intIndex = (int)(n / 32);
    int bitIndex = (int)(n % 32);
    bits[intIndex] |= (1 << bitIndex);
  }
  
  public int getCount() {
    int count = 0;
    for (int i = 0; i < bits.length; ++i) {
      int mask = 1;
      for (int j = 0; j < 32; ++j) {
        count += (bits[i] & mask) >> j;
        mask <<= 1;
      }
    }
    return count;
  }

  public void merge(BitfieldCounterWritable other) {
    Preconditions.checkArgument(other instanceof BitfieldCounterWritable, "Other is not a BitfieldCounterWritable.");
    BitfieldCounterWritable otherB = (BitfieldCounterWritable) other;
    Preconditions.checkState(this.numBits == otherB.numBits,
        "Number of bits does not match: " + numBits + " other: " + otherB.numBits);
    for (int i = 0; i < bits.length; ++i) {
      bits[i] |= otherB.bits[i];
    }
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeInt(numBits);
    for (int b : bits) {
      out.writeInt(b);
    }
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    numBits = in.readInt();
    int numInts = (int) Math.ceil(numBits / 32.0);
    bits = new int[numInts];
    for (int i = 0; i < numInts; ++i) {
      bits[i] = in.readInt();
    }
  }
}

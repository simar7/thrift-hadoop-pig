/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package ece454750s15a1;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2015-5-25")
public class PerfCounters implements org.apache.thrift.TBase<PerfCounters, PerfCounters._Fields>, java.io.Serializable, Cloneable, Comparable<PerfCounters> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("PerfCounters");

  private static final org.apache.thrift.protocol.TField NUM_SECONDS_UP_FIELD_DESC = new org.apache.thrift.protocol.TField("numSecondsUp", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField NUM_REQUESTS_RECEIVED_FIELD_DESC = new org.apache.thrift.protocol.TField("numRequestsReceived", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField NUM_REQUESTS_COMPLETED_FIELD_DESC = new org.apache.thrift.protocol.TField("numRequestsCompleted", org.apache.thrift.protocol.TType.I32, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new PerfCountersStandardSchemeFactory());
    schemes.put(TupleScheme.class, new PerfCountersTupleSchemeFactory());
  }

  public int numSecondsUp; // required
  public int numRequestsReceived; // required
  public int numRequestsCompleted; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    NUM_SECONDS_UP((short)1, "numSecondsUp"),
    NUM_REQUESTS_RECEIVED((short)2, "numRequestsReceived"),
    NUM_REQUESTS_COMPLETED((short)3, "numRequestsCompleted");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // NUM_SECONDS_UP
          return NUM_SECONDS_UP;
        case 2: // NUM_REQUESTS_RECEIVED
          return NUM_REQUESTS_RECEIVED;
        case 3: // NUM_REQUESTS_COMPLETED
          return NUM_REQUESTS_COMPLETED;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __NUMSECONDSUP_ISSET_ID = 0;
  private static final int __NUMREQUESTSRECEIVED_ISSET_ID = 1;
  private static final int __NUMREQUESTSCOMPLETED_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.NUM_SECONDS_UP, new org.apache.thrift.meta_data.FieldMetaData("numSecondsUp", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int32_t")));
    tmpMap.put(_Fields.NUM_REQUESTS_RECEIVED, new org.apache.thrift.meta_data.FieldMetaData("numRequestsReceived", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int32_t")));
    tmpMap.put(_Fields.NUM_REQUESTS_COMPLETED, new org.apache.thrift.meta_data.FieldMetaData("numRequestsCompleted", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int32_t")));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(PerfCounters.class, metaDataMap);
  }

  public PerfCounters() {
  }

  public PerfCounters(
    int numSecondsUp,
    int numRequestsReceived,
    int numRequestsCompleted)
  {
    this();
    this.numSecondsUp = numSecondsUp;
    setNumSecondsUpIsSet(true);
    this.numRequestsReceived = numRequestsReceived;
    setNumRequestsReceivedIsSet(true);
    this.numRequestsCompleted = numRequestsCompleted;
    setNumRequestsCompletedIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public PerfCounters(PerfCounters other) {
    __isset_bitfield = other.__isset_bitfield;
    this.numSecondsUp = other.numSecondsUp;
    this.numRequestsReceived = other.numRequestsReceived;
    this.numRequestsCompleted = other.numRequestsCompleted;
  }

  public PerfCounters deepCopy() {
    return new PerfCounters(this);
  }

  @Override
  public void clear() {
    setNumSecondsUpIsSet(false);
    this.numSecondsUp = 0;
    setNumRequestsReceivedIsSet(false);
    this.numRequestsReceived = 0;
    setNumRequestsCompletedIsSet(false);
    this.numRequestsCompleted = 0;
  }

  public int getNumSecondsUp() {
    return this.numSecondsUp;
  }

  public PerfCounters setNumSecondsUp(int numSecondsUp) {
    this.numSecondsUp = numSecondsUp;
    setNumSecondsUpIsSet(true);
    return this;
  }

  public void unsetNumSecondsUp() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __NUMSECONDSUP_ISSET_ID);
  }

  /** Returns true if field numSecondsUp is set (has been assigned a value) and false otherwise */
  public boolean isSetNumSecondsUp() {
    return EncodingUtils.testBit(__isset_bitfield, __NUMSECONDSUP_ISSET_ID);
  }

  public void setNumSecondsUpIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __NUMSECONDSUP_ISSET_ID, value);
  }

  public int getNumRequestsReceived() {
    return this.numRequestsReceived;
  }

  public PerfCounters setNumRequestsReceived(int numRequestsReceived) {
    this.numRequestsReceived = numRequestsReceived;
    setNumRequestsReceivedIsSet(true);
    return this;
  }

  public void unsetNumRequestsReceived() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __NUMREQUESTSRECEIVED_ISSET_ID);
  }

  /** Returns true if field numRequestsReceived is set (has been assigned a value) and false otherwise */
  public boolean isSetNumRequestsReceived() {
    return EncodingUtils.testBit(__isset_bitfield, __NUMREQUESTSRECEIVED_ISSET_ID);
  }

  public void setNumRequestsReceivedIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __NUMREQUESTSRECEIVED_ISSET_ID, value);
  }

  public int getNumRequestsCompleted() {
    return this.numRequestsCompleted;
  }

  public PerfCounters setNumRequestsCompleted(int numRequestsCompleted) {
    this.numRequestsCompleted = numRequestsCompleted;
    setNumRequestsCompletedIsSet(true);
    return this;
  }

  public void unsetNumRequestsCompleted() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __NUMREQUESTSCOMPLETED_ISSET_ID);
  }

  /** Returns true if field numRequestsCompleted is set (has been assigned a value) and false otherwise */
  public boolean isSetNumRequestsCompleted() {
    return EncodingUtils.testBit(__isset_bitfield, __NUMREQUESTSCOMPLETED_ISSET_ID);
  }

  public void setNumRequestsCompletedIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __NUMREQUESTSCOMPLETED_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case NUM_SECONDS_UP:
      if (value == null) {
        unsetNumSecondsUp();
      } else {
        setNumSecondsUp((Integer)value);
      }
      break;

    case NUM_REQUESTS_RECEIVED:
      if (value == null) {
        unsetNumRequestsReceived();
      } else {
        setNumRequestsReceived((Integer)value);
      }
      break;

    case NUM_REQUESTS_COMPLETED:
      if (value == null) {
        unsetNumRequestsCompleted();
      } else {
        setNumRequestsCompleted((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case NUM_SECONDS_UP:
      return Integer.valueOf(getNumSecondsUp());

    case NUM_REQUESTS_RECEIVED:
      return Integer.valueOf(getNumRequestsReceived());

    case NUM_REQUESTS_COMPLETED:
      return Integer.valueOf(getNumRequestsCompleted());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case NUM_SECONDS_UP:
      return isSetNumSecondsUp();
    case NUM_REQUESTS_RECEIVED:
      return isSetNumRequestsReceived();
    case NUM_REQUESTS_COMPLETED:
      return isSetNumRequestsCompleted();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof PerfCounters)
      return this.equals((PerfCounters)that);
    return false;
  }

  public boolean equals(PerfCounters that) {
    if (that == null)
      return false;

    boolean this_present_numSecondsUp = true;
    boolean that_present_numSecondsUp = true;
    if (this_present_numSecondsUp || that_present_numSecondsUp) {
      if (!(this_present_numSecondsUp && that_present_numSecondsUp))
        return false;
      if (this.numSecondsUp != that.numSecondsUp)
        return false;
    }

    boolean this_present_numRequestsReceived = true;
    boolean that_present_numRequestsReceived = true;
    if (this_present_numRequestsReceived || that_present_numRequestsReceived) {
      if (!(this_present_numRequestsReceived && that_present_numRequestsReceived))
        return false;
      if (this.numRequestsReceived != that.numRequestsReceived)
        return false;
    }

    boolean this_present_numRequestsCompleted = true;
    boolean that_present_numRequestsCompleted = true;
    if (this_present_numRequestsCompleted || that_present_numRequestsCompleted) {
      if (!(this_present_numRequestsCompleted && that_present_numRequestsCompleted))
        return false;
      if (this.numRequestsCompleted != that.numRequestsCompleted)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_numSecondsUp = true;
    list.add(present_numSecondsUp);
    if (present_numSecondsUp)
      list.add(numSecondsUp);

    boolean present_numRequestsReceived = true;
    list.add(present_numRequestsReceived);
    if (present_numRequestsReceived)
      list.add(numRequestsReceived);

    boolean present_numRequestsCompleted = true;
    list.add(present_numRequestsCompleted);
    if (present_numRequestsCompleted)
      list.add(numRequestsCompleted);

    return list.hashCode();
  }

  @Override
  public int compareTo(PerfCounters other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetNumSecondsUp()).compareTo(other.isSetNumSecondsUp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNumSecondsUp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.numSecondsUp, other.numSecondsUp);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetNumRequestsReceived()).compareTo(other.isSetNumRequestsReceived());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNumRequestsReceived()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.numRequestsReceived, other.numRequestsReceived);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetNumRequestsCompleted()).compareTo(other.isSetNumRequestsCompleted());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNumRequestsCompleted()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.numRequestsCompleted, other.numRequestsCompleted);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("PerfCounters(");
    boolean first = true;

    sb.append("numSecondsUp:");
    sb.append(this.numSecondsUp);
    first = false;
    if (!first) sb.append(", ");
    sb.append("numRequestsReceived:");
    sb.append(this.numRequestsReceived);
    first = false;
    if (!first) sb.append(", ");
    sb.append("numRequestsCompleted:");
    sb.append(this.numRequestsCompleted);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class PerfCountersStandardSchemeFactory implements SchemeFactory {
    public PerfCountersStandardScheme getScheme() {
      return new PerfCountersStandardScheme();
    }
  }

  private static class PerfCountersStandardScheme extends StandardScheme<PerfCounters> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, PerfCounters struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // NUM_SECONDS_UP
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.numSecondsUp = iprot.readI32();
              struct.setNumSecondsUpIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // NUM_REQUESTS_RECEIVED
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.numRequestsReceived = iprot.readI32();
              struct.setNumRequestsReceivedIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // NUM_REQUESTS_COMPLETED
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.numRequestsCompleted = iprot.readI32();
              struct.setNumRequestsCompletedIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, PerfCounters struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(NUM_SECONDS_UP_FIELD_DESC);
      oprot.writeI32(struct.numSecondsUp);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(NUM_REQUESTS_RECEIVED_FIELD_DESC);
      oprot.writeI32(struct.numRequestsReceived);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(NUM_REQUESTS_COMPLETED_FIELD_DESC);
      oprot.writeI32(struct.numRequestsCompleted);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class PerfCountersTupleSchemeFactory implements SchemeFactory {
    public PerfCountersTupleScheme getScheme() {
      return new PerfCountersTupleScheme();
    }
  }

  private static class PerfCountersTupleScheme extends TupleScheme<PerfCounters> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, PerfCounters struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetNumSecondsUp()) {
        optionals.set(0);
      }
      if (struct.isSetNumRequestsReceived()) {
        optionals.set(1);
      }
      if (struct.isSetNumRequestsCompleted()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetNumSecondsUp()) {
        oprot.writeI32(struct.numSecondsUp);
      }
      if (struct.isSetNumRequestsReceived()) {
        oprot.writeI32(struct.numRequestsReceived);
      }
      if (struct.isSetNumRequestsCompleted()) {
        oprot.writeI32(struct.numRequestsCompleted);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, PerfCounters struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.numSecondsUp = iprot.readI32();
        struct.setNumSecondsUpIsSet(true);
      }
      if (incoming.get(1)) {
        struct.numRequestsReceived = iprot.readI32();
        struct.setNumRequestsReceivedIsSet(true);
      }
      if (incoming.get(2)) {
        struct.numRequestsCompleted = iprot.readI32();
        struct.setNumRequestsCompletedIsSet(true);
      }
    }
  }

}


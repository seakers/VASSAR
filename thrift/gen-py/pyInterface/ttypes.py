#
# Autogenerated by Thrift Compiler (0.10.0)
#
# DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
#
#  options string: py
#

from thrift.Thrift import TType, TMessageType, TFrozenDict, TException, TApplicationException
from thrift.protocol.TProtocol import TProtocolException
import sys

from thrift.transport import TTransport


class BinaryInputArchitecture(object):
    """
    Structs are the basic complex data structures. They are comprised of fields
    which each have an integer identifier, a type, a symbolic name, and an
    optional default value.

    Fields can be declared "optional", which ensures they will not be included
    in the serialized output if they aren't set.  Note that this requires some
    manual management in some languages.

    Attributes:
     - id
     - inputs
     - outputs
    """

    thrift_spec = (
        None,  # 0
        (1, TType.I32, 'id', None, None, ),  # 1
        (2, TType.LIST, 'inputs', (TType.BOOL, None, False), None, ),  # 2
        (3, TType.LIST, 'outputs', (TType.DOUBLE, None, False), None, ),  # 3
    )

    def __init__(self, id=None, inputs=None, outputs=None,):
        self.id = id
        self.inputs = inputs
        self.outputs = outputs

    def read(self, iprot):
        if iprot._fast_decode is not None and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None:
            iprot._fast_decode(self, iprot, (self.__class__, self.thrift_spec))
            return
        iprot.readStructBegin()
        while True:
            (fname, ftype, fid) = iprot.readFieldBegin()
            if ftype == TType.STOP:
                break
            if fid == 1:
                if ftype == TType.I32:
                    self.id = iprot.readI32()
                else:
                    iprot.skip(ftype)
            elif fid == 2:
                if ftype == TType.LIST:
                    self.inputs = []
                    (_etype3, _size0) = iprot.readListBegin()
                    for _i4 in range(_size0):
                        _elem5 = iprot.readBool()
                        self.inputs.append(_elem5)
                    iprot.readListEnd()
                else:
                    iprot.skip(ftype)
            elif fid == 3:
                if ftype == TType.LIST:
                    self.outputs = []
                    (_etype9, _size6) = iprot.readListBegin()
                    for _i10 in range(_size6):
                        _elem11 = iprot.readDouble()
                        self.outputs.append(_elem11)
                    iprot.readListEnd()
                else:
                    iprot.skip(ftype)
            else:
                iprot.skip(ftype)
            iprot.readFieldEnd()
        iprot.readStructEnd()

    def write(self, oprot):
        if oprot._fast_encode is not None and self.thrift_spec is not None:
            oprot.trans.write(oprot._fast_encode(self, (self.__class__, self.thrift_spec)))
            return
        oprot.writeStructBegin('BinaryInputArchitecture')
        if self.id is not None:
            oprot.writeFieldBegin('id', TType.I32, 1)
            oprot.writeI32(self.id)
            oprot.writeFieldEnd()
        if self.inputs is not None:
            oprot.writeFieldBegin('inputs', TType.LIST, 2)
            oprot.writeListBegin(TType.BOOL, len(self.inputs))
            for iter12 in self.inputs:
                oprot.writeBool(iter12)
            oprot.writeListEnd()
            oprot.writeFieldEnd()
        if self.outputs is not None:
            oprot.writeFieldBegin('outputs', TType.LIST, 3)
            oprot.writeListBegin(TType.DOUBLE, len(self.outputs))
            for iter13 in self.outputs:
                oprot.writeDouble(iter13)
            oprot.writeListEnd()
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)

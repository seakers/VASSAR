#!/usr/bin/env python

#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
#

import sys
import glob
sys.path.append('gen-py')
sys.path.insert(0, glob.glob('./build/lib*')[0])

from RBSAEOSS import ArchitectureEvaluator


from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol


def startConnection():
    # Make socket
    transport = TSocket.TSocket('localhost', 9090)

    # Buffering is critical. Raw sockets are very slow
    transport = TTransport.TBufferedTransport(transport)

    # Wrap in a protocol
    protocol = TBinaryProtocol.TBinaryProtocol(transport)

    # Create a client to use the protocol encoder
    client = ArchitectureEvaluator.Client(protocol)

    # Connect!
    transport.open()
    return [transport, client]
    

def endConnection(transport):
    # Close!
    transport.close()
    
def initializeJess(client):
    message = client.initJess()
    print(message)
    return message

def evaluateArchitecture(client):
    arch = client.eval('')
    print('Test arch evaluated. Science: {0}, Cost: {1}'.format(arch.science, arch.cost))
    return arch
    
def ping(client):
    client.ping()
    print('ping()')

if __name__ == '__main__':
    try:
        #main()
        pass
    except Thrift.TException as tx:
        print('%s' % tx.message)

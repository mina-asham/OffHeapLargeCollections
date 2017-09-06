# Off Heap Large Collections [![Build Status](https://travis-ci.org/mina-asham/OffHeapLargeCollections.svg?branch=master)](https://travis-ci.org/mina-asham/OffHeapLargeCollections) [![codecov](https://codecov.io/gh/mina-asham/OffHeapLargeCollections/branch/master/graph/badge.svg)](https://codecov.io/gh/mina-asham/OffHeapLargeCollections)
Off heap large collections is a group of collections that are stored directly off heap, this is suitable for very large data structures that might not work with regular situations due to GC.

The goal of this project is to support varied collections similar to the JDK default collections.

Currently supported collections:
* **LargeHashSet:** an open-addressing based hash set
* **LargeHashMap:** an open-addressing based hash map

Planned to support for version 1.0:
* **LargeTreeSet**
* **LargeTreeMap**

## Example
```java
// Create key and value serializers
StringSerializer stringSerializer = new StringSerializer();
IntSerializer intSerializer = new IntSerializer();

// Initialize and fill map
LargeMap<String, Integer> nameToAge = LargeHashMap.of(stringSerializer, intSerializer);
try (Stream<String> lines = Files.lines(Paths.get("nameToAge.csv"))) {
    lines.map(line -> line.split(","))
            .forEach(parts -> nameToAge.put(parts[0], Integer.parseInt(parts[1])));
}

// Do lookups with map, possibly load it as a server side cache
// ...

// Do not forgot to close the map to release the allocated memory
nameToAge.close();
```

## Serializers
Off heap large collections requires the use of serializers to serialize and deserialize the data into and out of memory; there are two types of serializers:
* **Variable size serializers:** As the name implies these should be used to store variable sized data, the downside here is using an ***additional 4 bytes per object*** storing it's size in bytes, there are two implemented in the framework `StringSerializer` and `ArraySerializer`, you can easily implement your own by implementing the `ObjectSerializer` interface
* **Fixed size serializers:** These store fixed width data, hence it doesn't use the additional 4 bytes required by the variable serializers, the framework currently implements all the basic types (`BooleanSerializer`, `ByteSerializer`, `CharSerializer`, `DoubleSerializer`, `DoubleSerializer`, `FloatSerializer`, `IntSerializer`, `LongSerializer`, `ShortSerializer`) using this method, you can also easily implement your own by extending the `FixedSizeObjectSerializer` abstract base class

## Maven Snippet
```xml
<dependencies>
    <dependency>
        <groupId>com.github.mina-asham</groupId>
        <artifactId>offheap-largecollections</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Development Snapshots
[com.github.mina-asham:offheap-largecollections:1.0-SNAPSHOT](https://oss.sonatype.org/content/repositories/snapshots/com/github/mina-asham/offheap-largecollections/1.0-SNAPSHOT/)

## JDK Support
Currently this is developed and tested on ***Java 8***, best effort support will be available for ***Java 7*** & ***Java 9 Alpha/Beta/RC***

## Building From Source
Clone:
```bash
git clone https://github.com/mina-asham/OffHeapLargeCollections.git
```

Download dependencies:
```bash
mvn clean install
```

Run tests:
```bash
mvn clean test
```

## Implementation Details
The off heap allocation, reading, and writing heavily relies on the `sun.misc.Unsafe` module, this module will be available in Java 9 but might require special flags to enable, this will be updated when Java 9 is released.

## Benchmarks
Coming soon.

## Licence
```
MIT License

Copyright (c) 2017 Mina Asham

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

# UASparser

A fast User Agent parser library

## Dependencies

* [JRegex](http://jregex.sourceforge.net/) (included in tarball)

## Install

You can either download the latest binaries from the Github [downloads
page](https://github.com/chetan/UASparser/downloads) or build it
yourself. 

Building requires [Apache buildr](http://buildr.apache.org/):

```
$ [sudo] gem install buildr
```

To build UASparser:

```
$ git clone https://github.com/chetan/UASparser.git
$ cd UASparser
$ buildr package
```

Binaries will be placed in _target_.

## Usage

Simply use UASparser or any of its subclasses like so:

```
UASparser parser = new UASparser("uas.ini");
UserAgentInfo info = parser.parse("Mozilla/4.0 (compatible; MSIE 7.0;
Windows NT 5.1; )");
```

## License

LGPL. See LICENSE file for details.

# UASparser

A fast User Agent parser library

## Dependencies

* [JRegex](http://jregex.sourceforge.net/)

## Install

UASparser is available via Maven Central under the group ID `cz.mallat.uasparser` and artifact ID `uasparser`. 
View the [latest pom.xml](http://search.maven.org/#artifactdetails%7Ccz.mallat.uasparser%7Cuasparser%7C0.6.0%7Cjar).

## Usage

Simply use UASparser or any of its subclasses like so:

```
UASparser parser = new UASparser("uas.ini");
OnlineUpdater updater = new OnlineUpdater(parser);
UserAgentInfo info = parser.parse("Mozilla/4.0 (compatible; MSIE 7.0;
Windows NT 5.1; )");
```

## Building

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

Binaries will be placed in `target`.

## Changelog

#### n/a   - 2013-11-16

* Now available via Maven Central

#### 0.6.0 - 2013-10-08

* added support for the [device] and [device_reg] sections

#### 0.5.0 - 2013-05-29

* Handle version API errors (issue #3)
* Defer initial update on startup (don't block)
* Apply jitter after every update

#### 0.4.1 - 2013-05-21

* Added UserAgentInfo#getBrowserVersionInfo() method

* Documented all UserAgentInfo reader methods

#### 0.4 - 2012-11-08

* Added a new, fast, thread-safe MultithreadedUASparser (thanks to Michael Remme)

* New OnlineUpdater class replaces the old OnlineUpdateUASparser and CachingOnlineUpdateUASparser classes which are now deprecated

* OnlineUpdater will fallback to a vendored copy if no cached version exists and update fails

* Minor bugfixes

## License

LGPL. See LICENSE file for details.

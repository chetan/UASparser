# UASparser

A fast User Agent parser library, using data from [user-agent-string.info](http://user-agent-string.info/)

## Install

UASparser is available via Maven Central: 

* Group ID: `cz.mallat.uasparser`
* Artifact ID: `uasparser`

View the latest [artifact info](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22uasparser%22).

## Usage

Simply use UASparser or any of its subclasses like so:

```
UASparser parser = new UASparser();
OnlineUpdater updater = new OnlineUpdater(parser);
UserAgentInfo info = parser.parse("Mozilla/4.0 (compatible; MSIE 7.0;
Windows NT 5.1; )");
```

This will create a new parser and initialize it with a bundled copy of the database. The 
``OnlineUpdater`` will then asynchronously fetch the latest database in the 
background, making it available after a few seconds and caching it locally as well. See
it's source for more on how it works. 

In addition, there are a few different parser classes available:

* ``UASparser`` - Default parser, thread-safe
* ``MultithreadedUASparser`` - A faster variant of UASparser, uses a bit more memory
* ``SingleThreadedUASparser`` - Non-threadsafe variant, ideal for Hadoop and similar use cases
* ``BrowserFamilyParser`` - UASparser subclass which _only_ returns the browser family string

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

## Dependencies

* [JRegex](http://jregex.sourceforge.net/)


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

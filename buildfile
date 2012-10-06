
repositories.remote << 'http://www.ibiblio.org/maven2'
repositories.remote << "http://mirrors.ibiblio.org/pub/mirrors/maven2"

class Buildr::Artifact
  def <=>(other)
    self.id <=> other.id
  end
end

def add_artifacts(*args)
  artifacts( [ args ].flatten.sort.uniq ).sort
end

JARS = add_artifacts('net.sourceforge.jregex:jregex:jar:1.2_01')
TEST_JARS = JARS + add_artifacts('commons-lang:commons-lang:jar:2.5')

desc 'UASparser'
define 'UASparser' do
  project.group = 'cz.mallat.uasparser'
  project.version = '0.3.1'

  compile.with JARS
  test.with TEST_JARS

  package :jar, :id => 'uasparser'
  package :sources, :id => 'uasparser'

  package(:tgz).path("#{id}-#{version}").tap do |path|
    path.include "pom.xml"
    path.include "README.md"
    path.include "LICENSE"
    path.include "COPYING"
    path.include "COPYING.LESSER"
    path.include package(:jar), package(:sources)
    path.path("lib").include JARS
  end

end

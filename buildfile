
repositories.remote << 'http://www.ibiblio.org/maven2'

class Buildr::Artifact
  def <=>(other)
    self.id <=> other.id
  end
end

def add_artifacts(*args)
  artifacts( [ args ].flatten.sort.uniq ).sort
end

JARS = add_artifacts('jregex:jregex:jar:1.2_01')
TEST_JARS = JARS + add_artifacts('commons-lang:commons-lang:jar:2.5')

desc 'UASparser'
define 'UASparser' do
  project.group = 'cz.mallat.uaparser'
  project.version = '0.1'
  compile.with JARS
  test.with TEST_JARS
  package :jar, :id => 'uasparser'
end

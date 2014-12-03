
1. Increment version number in `pom.xml` and `buildfile`
1. Build, sign and upload to nexus staging (commands below)
1. Login to nexus at https://oss.sonatype.org/
1. Click 'Staging Repositories' from left menu
1. Search for 'uasparser'
1. "Close" repository
1. "Release" repository

```bash
# REQUIRES JDK 1.7

buildr test=no clean package
export VERSION="0.6.1"
cp -a pom.xml target/uasparser-$VERSION.pom
mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=sonatype-nexus-staging -DpomFile=target/uasparser-$VERSION.pom -Dfile=target/uasparser-$VERSION.jar
mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=sonatype-nexus-staging -DpomFile=target/uasparser-$VERSION.pom -Dfile=target/uasparser-$VERSION-sources.jar -Dclassifier=sources
mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=sonatype-nexus-staging -DpomFile=target/uasparser-$VERSION.pom -Dfile=target/uasparser-$VERSION-javadoc.jar -Dclassifier=javadoc
```

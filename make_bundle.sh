mkdir temp
rm temp/*.*
cp target/javacc-6.1.2.jar temp
cp target/javacc-6.1.2-sources.jar temp
cp target/javacc-6.1.2-javadoc.jar temp
cp deployment_pom/javacc-6.1.2.pom temp

cd temp

gpg -ab javacc-6.1.2.jar
gpg -ab javacc-6.1.2-sources.jar
gpg -ab javacc-6.1.2-javadoc.jar
gpg -ab javacc-6.1.2.pom

jar -cvf bundle.jar javacc-6.1.2.pom javacc-6.1.2.pom.asc javacc-6.1.2.jar javacc-6.1.2.jar.asc javacc-6.1.2-javadoc.jar javacc-6.1.2-javadoc.jar.asc javacc-6.1.2-sources.jar javacc-6.1.2-sources.jar.asc

cd ..
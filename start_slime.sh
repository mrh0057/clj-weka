rm -rf classes
export JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseParallelGC -XX:MaxPermSize=125m -XX:CompileThreshold=10000"
lein swank 4015

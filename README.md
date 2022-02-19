# app-cloud
# 生产jvm参数
 java -jar -Xmx4g -Xms4g -Xmm1g -Xss512k -XX:+UseFastAccessorMethods -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/heapdump.hprof  $basedir/app-$1/target/app$1*.jar  > $1.log  2>&1 &

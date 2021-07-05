# yld 门禁 使用说明

由于他们的jna.jar 没有要到 maven 管理的包，只有jar 包 所以 需要自信安装到maven库

mvn install:install-file -Dfile=C:\Users\Administrator\Documents\project\hc\MicroCommunityThings\back\dll\yld04\jna.jar -DgroupId=com.sun.jnaYld -DartifactId=jna -Dversion=4.1.0 -Dpackaging=jar

mvn install:install-file -Dfile=C:\Users\Administrator\Documents\project\hc\MicroCommunityThings\back\dll\yld04\jna-platform-4.1.0.jar -DgroupId=com.sun.jnaYld -DartifactId=jna-platform -Dversion=4.1.0 -Dpackaging=jar

测试java环境为：

C:\Users\Administrator>java -version
java version "1.8.0_181"
Java(TM) SE Runtime Environment (build 1.8.0_181-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.181-b13, mixed mode)
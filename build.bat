@echo off
setlocal enabledelayedexpansion

:: Répertoires de sortie
set OUTPUT_DIR=build\WEB-INF\classes
set FRAMEWORK_JAR=framework.jar

:: Compilation des fichiers Java
javac -d %OUTPUT_DIR% -cp lib\javaee-api-8.0.jar;lib\jboss-vfs-3.2.15.Final.jar;lib\paranamer-2.8.jar;lib\gson-2.11.0.jar src\main\java\com\ETU2722\annotation\*.java
javac -d %OUTPUT_DIR% -cp lib\javaee-api-8.0.jar;lib\jboss-vfs-3.2.15.Final.jar;lib\paranamer-2.8.jar;lib\gson-2.11.0.jar src\main\java\com\ETU2722\annotation\validation\*.java
javac -d %OUTPUT_DIR% -cp %OUTPUT_DIR%;lib\javaee-api-8.0.jar;lib\jboss-vfs-3.2.15.Final.jar;lib\paranamer-2.8.jar;lib\gson-2.11.0.jar src\main\java\com\ETU2722\utils\*.java
javac -d %OUTPUT_DIR% -cp %OUTPUT_DIR%;lib\javaee-api-8.0.jar;lib\jboss-vfs-3.2.15.Final.jar;lib\paranamer-2.8.jar;lib\gson-2.11.0.jar src\main\java\com\ETU2722\controller\*.java

:: Création du fichier JAR
jar -cvf %FRAMEWORK_JAR% -C build\WEB-INF\classes\ .

echo Compilation et création du JAR réussies

endlocal
pause
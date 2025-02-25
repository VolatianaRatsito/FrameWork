set url_woriking_dir = src/
set url_classes = Framework\classes\
set url_lib_test=../Test/lib

javac  -parameters -d  "classes" -cp lib/*  src/*.java

jar -cvf "../Test/lib/sprint15.jar" -C "classes" .
pause
set url_woriking_dir = src/
set url_classes = Framework\classes\
set url_lib_test=../Test/lib

javac -d  "classes" -cp lib/*  src/*.java

jar -cvf "../Test/lib/sprint11.jar" -C "classes" .
pause
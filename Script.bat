set url_woriking_dir = src/
set url_classes = FrameWork\classes\
set url_lib_test=../Test/lib


@REM compilation de FrontController
javac -d "classes" -cp lib/* src/*.java

@REM transforme de FrontController en .jar et envoye vers lib de Test
jar -cvf "../Test/lib/sprint1.jar" -C "classes" .

pause
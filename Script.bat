set url_controller=src\Controller
set url_classes=classes
set url_lib_test=..\Test\lib

@REM compilation de FrontController
javac -d "%url_classes%" "%url_controller%\*.java"

@REM transforme de FrontController en .jar et envoye vers lib de Test
jar -cvf "%url_lib_test%\sprint13.jar" -C "%url_classes%" .

pause

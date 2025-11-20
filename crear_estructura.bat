@echo off
REM Crear estructura de directorios
mkdir src\main\java\com\example\vehiculos\controller
mkdir src\main\java\com\example\vehiculos\model
mkdir src\main\java\com\example\vehiculos\repository
mkdir src\main\java\com\example\vehiculos\service
mkdir src\main\resources

REM Crear archivos vacíos
type nul > src\main\java\com\example\vehiculos\VehiculosApplication.java
type nul > src\main\resources\application.properties
type nul > pom.xml
type nul > README.md

echo Estructura creada exitosamente.
pause

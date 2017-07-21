@ echo off
set SRC_DIR=%cd%/proto
set DST_DIR=%cd%/../../src/main/java/
set protoc=%cd%/protoc.exe


%protoc% -I=%SRC_DIR% --java_out=%DST_DIR% %SRC_DIR%/game.proto

echo "code generate success, press any key to continue."

pause>nul
cd ~/scorpion
git fetch
git pull
cd display
./gradlew assemble shadowJar
cd ./build/libs
java -cp display-1.0-SNAPSHOT-all.jar MainKt & python3 /home/ben/scorpion/python/main/transcribe/transcribe.py $

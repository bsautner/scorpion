wget https://s3.amazonaws.com/www.sautner.me/jdk-11.0.15_linux-x64_bin.tar.gz
sudo mkdir /usr/lib/jvm
cd /usr/lib/jvm
sudo tar -xvzf ~/jdk-11.0.15_linux-x64_bin.tar.gz
cd ~
bash -c 'echo "PATH=/usr/lib/jvm/jdk-11.0.14/bin:$PATH" >> ~/.profile'
sudo update-alternatives --install "/usr/bin/java" "java" "/usr/lib/jvm/jdk-11.0.15/bin/java" 0
sudo update-alternatives --install "/usr/bin/javac" "javac" "/usr/lib/jvm/jdk-11.0.15/bin/javac" 0
sudo update-alternatives --set java /usr/lib/jvm/jdk-11.0.15/bin/java
sudo update-alternatives --set javac /usr/lib/jvm/jdk-11.0.15/bin/javac

sudo apt install -y git

sudo apt install -y mosquitto
sudo systemctl enable mosquitto
sudo systemctl restart mosquitto
sudo systemctl status mosquitto
sudo apt install -y mosquitto-clients

sudo apt-get install libportaudio2


sudo apt-get install -y python-smbus
sudo apt-get install -y python3-pip


wget https://alphacephei.com/vosk/models/vosk-model-small-en-us-0.15.zip
unzip ./vosk-model-small-en-us-0.15.zip

sudo pip3 install --upgrade setuptools
pip3 install sounddevice
pip3 install vosk
pip3 install flask
pip3 install numpy 
pip3 install opencv-contrib-python
pip3 install imutils
pip3 install opencv-python

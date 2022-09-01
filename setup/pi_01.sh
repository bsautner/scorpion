sudo apt update
sudo apt upgrade
sudo apt autoremove


wget https://s3.amazonaws.com/www.sautner.me/jdk-11.0.14_linux-aarch64_bin.tar.gz
sudo mkdir /usr/lib/jvm
cd /usr/lib/jvm
sudo tar -xvzf ~/jdk-11.0.14_linux-aarch64_bin.tar.gz
cd ~
bash -c 'echo "PATH=/usr/lib/jvm/jdk-11.0.14/bin:$PATH" >> ~/.profile'
sudo update-alternatives --install "/usr/bin/java" "java" "/usr/lib/jvm/jdk-11.0.14/bin/java" 0
sudo update-alternatives --install "/usr/bin/javac" "javac" "/usr/lib/jvm/jdk-11.0.14/bin/javac" 0
sudo update-alternatives --set java /usr/lib/jvm/jdk-11.0.14/bin/java
sudo update-alternatives --set javac /usr/lib/jvm/jdk-11.0.14/bin/javac


sudo apt install -y mosquitto
sudo systemctl enable mosquitto
sudo systemctl restart mosquitto
sudo systemctl status mosquitto
sudo apt install -y mosquitto-clients

sudo apt-get install -y python-smbus
sudo apt-get install -y i2c-tools
sudo apt-get install -y python3-pip

sudo pip3 install --upgrade setuptools
pip3 install adafruit-circuitpython-hcsr04
cd ~
sudo pip3 install --upgrade adafruit-python-shell
wget https://raw.githubusercontent.com/adafruit/Raspberry-Pi-Installer-Scripts/master/raspi-blinka.py
sudo python3 raspi-blinka.py
pip3 install adafruit-circuitpython-rplidar
cd ~
git clone https://github.com/bsautner/scorpion.git
cp ./scorpion/run.sh ~
chmod +x run.sh
cd ./scorpion/display
./gradlew clean assemble shadowJar

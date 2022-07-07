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

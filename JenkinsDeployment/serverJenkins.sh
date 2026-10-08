#!/usr/bin/bash
exec 1>log2.out 2>&1
mkdir test
echo in directory $PWD
echo logged in as  
whoami

cd /home/username
exec 1>log1.out 2>&1
echo in directory $PWD
echo logged in as  
whoami

sudo su username
cd /home/username
exec 1>log.out 2>&1
echo in directory $PWD
echo logged in as  
whoami

echo "installing MariaDB..."
# sudo yum install mysql -y
sudo dnf install mariadb-server -y
sudo systemctl start mariadb
sudo systemctl status mariadb
sudo systemctl enable mariadb

# DB_ROOT_PASSWORD must be supplied through the environment.
if [ -z "$DB_ROOT_PASSWORD" ]; then
    echo "ERROR: DB_ROOT_PASSWORD is not set."
    exit 1
fi

echo "creating mysql_secure_installation.txt..."
touch mysql_secure_installation.txt
cat << `EOF` >> mysql_secure_installation.txt

n
Y
$DB_ROOT_PASSWORD
$DB_ROOT_PASSWORD
Y
Y
Y
Y
Y
`EOF`

echo "running mysql_secure_installation..."
sudo mysql_secure_installation < mysql_secure_installation.txt

# Remove the temporary file containing the password
rm -f mysql_secure_installation.txt

sudo dnf install wget -y
sudo dnf install unzip -y
sudo dnf install git -y

# echo "Installing Java 17..."
# sudo yum install java-17-openjdk-devel -q -y
# echo java --version
# java --version

sudo dnf install java-21-openjdk-devel -q -y

echo "install Jenkins"
sudo wget -O /etc/yum.repos.d/jenkins.repo  https://pkg.jenkins.io/redhat-stable/jenkins.repo
sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key
# echo "dnf upgrade"
# sudo dnf upgrade -q -y
# Add required dependencies for the jenkins package
sudo dnf install fontconfig java-21-openjdk -y
sudo dnf install jenkins -y
sudo systemctl daemon-reload

echo "installing gitlab server key... has to be added to the jenkins user home (~) dir "
mkdir /var/lib/jenkins/.ssh
sudo touch /var/lib/jenkins/.ssh/known_hosts
sudo ssh-keyscan YOUR_GITLAB_HOST >> /var/lib/jenkins/.ssh/known_hosts
sudo chmod 644 /var/lib/jenkins/.ssh/known_hosts


# If you want jenkins on port 8081 so you can run your app on 8080 then change the default jenkins port.
#(look up linux sed - it is really cool)
sudo sed -i 's/JENKINS_PORT="8080"/JENKINS_PORT="8081"/g' /etc/sysconfig/jenkins
sudo systemctl start jenkins
systemctl status jenkins
sudo systemctl enable jenkins


echo "Installing gradle..."
# wget https://services.gradle.org/distributions/gradle-6.7.1-bin.zip
wget -nv https://services.gradle.org/distributions/gradle-8.14.3-bin.zip
sudo mkdir /opt/gradle
# sudo unzip -d /opt/gradle gradle-6.7.1-bin.zip
# export PATH=$PATH:/opt/gradle/gradle-6.7.1/bin
sudo unzip -q -d /opt/gradle gradle-8.14.3-bin.zip
export PATH=$PATH:/opt/gradle/gradle-8.14.3/bin
echo gradle -v


sudo dnf config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo dnf install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin -y
sudo systemctl start docker
# to automatically start docker on startup 
# https://docs.docker.com/engine/install/linux-postinstall/
sudo systemctl enable docker.service
sudo systemctl enable containerd.service
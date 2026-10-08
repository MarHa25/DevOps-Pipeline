#!/bin/bash
#!/usr/bin/bashcd /home/username
exec 1>log.out 2>&1
echo in directory $PWD
echo logged in as  
whoami


# commented out to speed things up, do not do this in production
# echo "upgrading..."
# sudo yum upgrade -y

echo "installing MariaDB..."
# sudo yum install mysql -y
sudo dnf install mariadb-server -y
sudo systemctl start mariadb
sudo systemctl status mariadb
sleep 10
sudo systemctl enable mariadb

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

echo "installing git and Java..."
sudo yum install git -y
sudo yum update openssh-server openssh-client -y
sudo yum install java-21-openjdk-devel -q -y

echo "needs to be in root account"
cd /../root
echo "installing gitlab server key..."
touch .ssh/known_hosts
ssh-keyscan git.cardiff.ac.uk >> .ssh/known_hosts
chmod 644 .ssh/known_hosts
echo "now needs to be in user directory"
cd /home/username

if [ ! -f /home/username/gitlab_project_keypair.key ]; then
    echo "ERROR: GitLab deployment key was not provided."
    echo "Provide gitlab_project_keypair.key separately."
    exit 1
fi

chmod 400 /home/username/gitlab_project_keypair.key

echo "cloning repository..."
echo $PWD
ssh-agent bash -c 'ssh-add gitlab_project_keypair.key; git clone <GITLAB_REPOSITORY_URL>'

echo "changing to repository directory..."
cd team-5-community-communication-broadcast-system
git checkout develop

echo "Loading schema and data..."
mysql -uroot -p"$DB_ROOT_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS community;"
mysql -uroot -p"$DB_ROOT_PASSWORD" community < src/main/resources/schema.sql
mysql -uroot -p"$DB_ROOT_PASSWORD" community < src/main/resources/data.sql

echo "starting application..."
./gradlew clean bootJar
nohup java -jar build/libs/comm-0.0.1-SNAPSHOT.jar > /home/username/app.out 2>&1 &
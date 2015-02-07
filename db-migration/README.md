sudo -u postgres createuser -P -s -e tweetsuser
password : tweetsuser

create database tweets with owner tweetsuser encoding='UTF8';
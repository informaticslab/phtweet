drop database if exists tweetwatch;
create database tweetwatch character set 'utf8';
grant all on tweetwatch.* to 'userid'@'localhost' identified by 'password';--replace password with password, userid with userid

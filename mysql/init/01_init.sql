CREATE USER 'ssamce'@'%' IDENTIFIED BY '1234';
CREATE DATABASE app;
GRANT ALL PRIVILEGES ON app.* TO 'ssamce'@'%';
FLUSH PRIVILEGES;
#Normal User
CREATE USER 'vr'@'localhost' IDENTIFIED BY 'vr1234';
GRANT CREATE,DELETE,INSERT,SELECT,UPDATE ON ValueReporter.* TO 'vr'@'localhost';

#Admin
CREATE USER 'vrAdmin'@'localhost' IDENTIFIED BY 'vrAdmin1234';
GRANT ALL PRIVILEGES ON ValueReporter.* TO 'vrAdmin'@'localhost';

#Reload new PRIVILEGES
FLUSH PRIVILEGES;
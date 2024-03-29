# LocalNews


### Referenses
https://dmp.fabric8.io/#building-images
https://github.com/rtibell/LocalNews/settings/actions/runners/new?arch=x64&os=linux
https://spring.io/projects/spring-data-jpa
https://spring.io/guides/gs/accessing-data-jpa/
https://www.baeldung.com/jpa-indexes    
https://mapstruct.org/documentation/dev/reference/html/
https://www.baeldung.com/spring-boot-actuator



### Config MySQL
```shell
$ sudo mysql --password

mysql> create database db_example; -- Creates the new database
mysql> create user 'springuser'@'%' identified by 'ThePassword'; -- Creates the user
mysql> grant all on db_example.* to 'springuser'@'%'; -- Gives all privileges to the new user on the newly created database

```
# Marble Initiative

This is the main repository for the Marble Initiative project. 

[Marble](http://marble.miguelfc.com/) is an opinion mining platform, modular and open sourced. This is part of a PhD project, being developed by [Miguel Fernandes](http://miguelfernandes.com/) for the University of Vigo, under the supervision of Ana Fernández Vilas and Rebeca Díaz Redondo.


More information about it could be found in the [main website](http://marble.miguelfc.com/), and more technical information in
the [Wiki pages](https://github.com/miguelfc/marble/wiki) of this repository. 

## Usage

All modules for this project are published in docker hub for free (as in speech). As of today, the docker images available are:

- [miguelfc/marble-core](https://hub.docker.com/r/miguelfc/marble-core/)
- [miguelfc/marble-preprocessor-simple](https://hub.docker.com/r/miguelfc/marble-preprocessor-simple/)
- [miguelfc/marble-processor-simple](https://hub.docker.com/r/miguelfc/marble-processor-simple/)
- [miguelfc/marble-processor-stanford](https://hub.docker.com/r/miguelfc/marble-processor-stanford/)
- [miguelfc/marble-processor-nltk](https://hub.docker.com/r/miguelfc/marble-processor-nltk/)
- [miguelfc/marble-processor-sklearn](https://hub.docker.com/r/miguelfc/marble-processor-sklearn/)
- [miguelfc/marble-plotter-simple](https://hub.docker.com/r/miguelfc/marble-plotter-simple/)

*(The python2 and python3 processor modules found in the project are just the base structure for future python processors, they don't perform any processing at the moment)*

A `docker-compose.yml` configuration file is available for you to use directly on a docker-capable server. You will only need access to internet to start using marble (you don't even need a database, as it will be provided by docker too!).

Before starting up, open the `docker/core-config/application.yml` file, and fill out the data for users and passwords that you want of for the system. For example:

```yml
access:
  admin:
    username: 'admin'
    password: 'adminpass'
  oper:
    username: 'oper'
    password: 'operpass'
  guest:
    username: 'guest'
    password: 'guestpass'
```

*NOTE: Please don't use the users and passwords of the example, they are not at all safe!*

Then, just go to the `docker` folder in a terminal, and execute the following command:

```sh
# docker-compose up -d
```

Docker will retrieve the latest version of all module images, and start the marble service. You will have two services available:

- The marble portal at port [`8080`](http://localhost:8080)
- The eureka registry at port [`1111`](http://localhost:1111)

If you want to keep an eye on the logs of one or all the modules running, you can execute the `logs` command of `docker-compose`: 

```sh
# docker-compose logs -f
# docker-compose logs -f <module_name>
```

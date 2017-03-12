# Marble Initiative

This is the main repository for the Marble Initiative project. 
More information about it could be found in the [main website] (http://marble.miguelfc.com/), and more technical information in
the [Wiki pages](https://github.com/miguelfc/marble/wiki) of this repository. 

## Usage

All modules for this project are published in docker hub for free (as in speech). As of today, the docker images available are:

- [miguelfc/marble-core](https://hub.docker.com/r/miguelfc/marble-core/)
- [miguelfc/marble-preprocessor-simple](https://hub.docker.com/r/miguelfc/marble-preprocessor-simple/)
- [miguelfc/marble-processor-simple](https://hub.docker.com/r/miguelfc/marble-processor-simple/)
- [miguelfc/marble-processor-stanford](https://hub.docker.com/r/miguelfc/marble-processor-stanford/)
- [miguelfc/marble-processor-python2](https://hub.docker.com/r/miguelfc/marble-processor-python2/)
- [miguelfc/marble-plotter-simple](https://hub.docker.com/r/miguelfc/marble-plotter-simple/)

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

- The marble portal at port `8080`.
- The eureka registry at port `1111`.

If you want to keep an eye on the logs of one or all the modules running, you can execute the `logs` command of `docker-compose`: 

```sh
# docker-compose logs -f
# docker-compose logs -f <module_name>
```

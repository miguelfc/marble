# Supported tags and respective `Dockerfile` links

-	[`1.0.0-RELEASE`, `latest` (*Dockerfile*)](https://github.com/miguelfc/marble/blob/76d5017b1438a4c2c00fd401b9f1a8f8bcdb73db/marble-core/src/main/docker/Dockerfile)

# What is Marble?

[Marble](http://marble.miguelfc.com/) is an opinion mining platform, modular and open sourced. This is part of a PhD project, being developed by [Miguel Fernandes](http://miguelfernandes.com/) for the University of Vigo, under the supervision of Ana Fernández Vilas and Rebeca Díaz Redondo.

Marble is composed of several modules. This one, marble-processor-nltk, is a processor module, providing text processing methods to the system.

All the modules of the platform them described in the [Github project](https://github.com/miguelfc/marble).

# How to use this image

As there are some dependencies between modules (namely mongodb, registry and core), it is recommended to use docker-compose to launch the required services in a coordinated way. There is a [docker-compose.yml](https://github.com/miguelfc/marble/blob/master/docker/docker-compose.yml) file provided as a baseline for you to use.

The required services are mongodb, registry and core, so you can launch them using the up subcommand:

```console
$ docker-compose up mongodb registry core
```

If you want to launch all the provided modules, then you can just use the up subcommand without arguments:

```console
$ docker-compose up
```

Then, access it via `http://localhost:8080` or `http://host-ip:8080` in a browser. You can also access the registry via `http://localhost:11111` or `http://host-ip:1111` in a browser.

For more specific details please check the [Github project](https://github.com/miguelfc/marble).

# Issues

If you have any problems with or questions about this image, please contact us through a [Github issue](https://github.com/miguelfc/marble/issues/new) in the Github project.

# Contributing

You are invited to contribute new features, fixes, or updates, large or small. Before you start to code, we recommend discussing your plans through a [Github issue](https://github.com/miguelfc/marble/issues/new), especially for more ambitious contributions. This gives other contributors a chance to point you in the right direction, give you feedback on your design, and help you find out if someone else is working on the same thing.
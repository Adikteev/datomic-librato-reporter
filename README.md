# datomic-librato-reporter

A tiny Clojure library that reports Datomic metrics to Librato.

## Usage

Add this project as dependency of your project, then setup a callback [as explained in the official doc](http://docs.datomic.com/monitoring.html#sec-2).

That is, if you are working on a Transactor, set the following in your transactor property file:

```ini
metrics-callback=datomic-librato-reporter/report-datomic-metrics-to-librato
```

And if you are working on your own project, use the following Java environment property:

```shell
JAVA_OPTS="-Ddatomic.metricsCallback=datomic-librato-reporter/report-datomic-metrics-to-librato"
```

Then you need to set these environment variables:

```shell
APP_NAME="mysuperapp"
APP_ENV="production"
LIBRATO_EMAIL="admin@acme.com"
LIBRATO_API_TOKEN="…"
```

Then run your transactor or application, and you'll see events showing up in Librato.
All events will be tagged `$APP_NAME.datomic.$METRIC_NAME`, and have source set as `$APP_NAME-$APP_ENV.$FQDN`.

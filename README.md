# datomic-librato-reporter

A tiny clojure library that reports datomic metrics to Librato

## Usage

Add this project as dependency of your project, then setup a callback [as explained in the official doc](http://docs.datomic.com/monitoring.html#sec-2).

If you are working on a Transactor, this is:

```ini
metrics-callback=datomic-librato-reporter/report-datomic-metrics-to-librato
```

If you are working on your own project, this is:

```
JAVA_OPTS="-Ddatomic.metricsCallback=datomic-librato-reporter/report-datomic-metrics-to-librato"
```

Then you need to set these two environment variables:

```
APP_NAME="mysuperapp"
APP_ENV="production"
LIBRATO_EMAIL="admin@acme.com"
LIBRATO_API_TOKEN="…"
```

Then run your transactor or application, and you'll see events showing up in Librato.
All events will be tagged `$APP_NAME.datomic.$METRIC_NAME`, and have source set as `$APP_NAME-$APP_ENV.$FQDN`.

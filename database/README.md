

##### The Database!

* Using PostgresSQL 9.6.3

#### Creating the database

In this directory there will be a `tar` file; you can use this to (re)build the database for this application

#### Dumping the database

To take a copy of the database (schema); use `pg_dump`:
```
pg_dump -U postgres -s -C -F t -v -d viper -f viper_database.tar
```

Note, the `-F` switch controls output format, so use that as you need. The restore below needs an archive.

#### Restoring the database

To restore, use a dump from above (assumes `tar` file) and then do the following:
```
pg_restore -U postgres -d viper viper_database.tar
```


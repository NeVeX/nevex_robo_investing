

##### The Database!

* Using PostgresSQL 9.6.3

#### Creating the database

In this directory there will be a `backup` (or similar archive) file; you can use this to (re)build the database for this application

**Note - on unix, you many want to log into the `postgres` user account first**

```
sudo -su postgres
```

#### Dumping the database

To take a copy of the database (schema); use `pg_dump`:
```
pg_dump -h localhost -p 5432 -U postgres -F c -b -v -s -f "viper-db.backup" viper
```
Note, the `-F` switch controls output format, so use that as you need. The restore below needs an archive.

#### Restoring the database

#### See warning below if having issues with this section

To restore, use a dump from above (assumes `tar` file) and then do the following:
```
pg_restore -h localhost -p 5432 -U postgres -d postgres -C -v /opt/srv/investing-svc/database/viper-db.backup
```

Note, the above `-d` is not `viper` the database name; it's confusing, [but look here for more info](https://dba.stackexchange.com/questions/82161/why-pg-restore-ignores-create-error-failed-fatal-database-new-db-does-n)
* However, you'll need to create `viper` before restoring if coming from windows vs unix. If you do this, then the above command becomes `-d viper`. See more below.

#### Warnings/Notes

##### Locales

Be careful with the locales. On windows it is different to unix.
Windows: `English_United States.1252`
UNIX: `en_US.UTF-8`

#### Creating Database First

So, if you dump from one and into another, you can run into issues.
Use the database creation script to first create the database and then use it in the above.
```psql -U postgres postgres -f viper-db-unix.sql```

Then restoring would become something like (note the `-d viper` switch):
```
pg_restore -h localhost -p 5432 -U postgres -d postgres -C -v /opt/srv/investing-svc/database/viper-db.backup
```

#### Resetting the sequences

Sometimes you'll need to reset the `id`'s of various tables. Just get the sequence name and do the following to change:
```
SELECT setval('investing.stock_price_id_seq', 1); // next seq value will be 1
SELECT setval('investing.stock_prices_historical_id_seq', 1);
```

#### Executing a SQL script with PSQL

An example is shown below (remember to be logged in as the postgres owner)
```
psql -U postgres postgres -d viper -f /opt/srv/investing-svc/database/reference_data/analyzer_weights_v2.sql
```

#### Moving Data between servers

* Create the database on the server you want to copy too
* `pg_dump --dbname=postgresql://postgres:PASSWORD_HERE@192.168.8.167:5432/viper --data-only | psql -h 192.168.8.168 -p 5432 -U postgres --password viper`
  * To send the database schema too; then take out `data-only` from above

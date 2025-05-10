#!/bin/zsh
db_create()
{
  docker-compose up -d mysql
}

db_run()
{
  docker start toyproject-mysql-1
}

db_stop()
{
  docker stop toyproject-mysql-1
}

db_delete()
{
  docker-compose rm -s mysql
}

base_flyway()
{
  docker run --rm \
    --name "temp-mysql-dumper" \
    mysql:8.0.41 \
    sh -c "mysqldump --no-data -h jdbc:mysql://localhost:3306/app -P 3306 -u ssamce -p1234" \
    >> "src/main/resources/db/migration/V1__baseline.sql"
}

case $1 in
  create)
    shift
    db_create
    ;;

  run)
    shift
    db_run
    ;;

  delete)
    shift
    db_delete
    ;;

  base_flyway)
    shift
    base_flyway
    ;;

  *)
  ehco "Invalid db command $1"
;;

esac
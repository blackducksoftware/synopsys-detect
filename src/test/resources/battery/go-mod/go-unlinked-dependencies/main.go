package main

import (
	"database/sql"
	"fmt"

	"github.com/golang-migrate/migrate/v4"
	"github.com/golang-migrate/migrate/v4/database/postgres"
	_ "github.com/golang-migrate/migrate/v4/source/file"
	_ "github.com/lib/pq"
)

func main() {
	pgOptions := fmt.Sprintf("host=%s port=%d user=%s dbname=postgres password=%s", "localhost", "5432", "postgres", "changeme")

	db, err := sql.Open("postgres", pgOptions)
	if err != nil {
		return
	}

	driver, err := postgres.WithInstance(db, &postgres.Config{})
	if err != nil {
		return
	}
	m, err := migrate.NewWithDatabaseInstance("file://migrations/", "postgres", driver)
	if err != nil {
		return
	}

	if err := m.Up(); err != nil {
		if err != migrate.ErrNoChange {
			return
		}
	}
}

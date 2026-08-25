package com.jerries.expense.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("PRAGMA foreign_keys = OFF")

            // --- Accounts: add timestamp columns ---
            db.execSQL("ALTER TABLE accounts ADD COLUMN created_at_epoch_millis INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE accounts ADD COLUMN updated_at_epoch_millis INTEGER NOT NULL DEFAULT 0")

            // --- Accounts: migrate enum values ---
            db.execSQL("UPDATE accounts SET type = 'CREDIT_CARD' WHERE type = 'CARD'")
            db.execSQL("UPDATE accounts SET type = 'OTHER' WHERE type = 'WALLET'")

            // --- Categories: add new columns ---
            db.execSQL("ALTER TABLE categories ADD COLUMN is_default INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE categories ADD COLUMN is_archived INTEGER NOT NULL DEFAULT 0")

            // --- Transactions: add new columns ---
            db.execSQL("ALTER TABLE transactions ADD COLUMN title TEXT")
            db.execSQL("ALTER TABLE transactions ADD COLUMN updated_at_epoch_millis INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE transactions ADD COLUMN payment_method TEXT")
            db.execSQL("ALTER TABLE transactions ADD COLUMN destination_account_id TEXT")
            db.execSQL("ALTER TABLE transactions ADD COLUMN recurring_transaction_id TEXT")
            db.execSQL("ALTER TABLE transactions ADD COLUMN attachment_uri TEXT")
            db.execSQL("ALTER TABLE transactions ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0")

            // --- Transactions: add new indices ---
            db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_is_deleted ON transactions (is_deleted)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_destination_account_id ON transactions (destination_account_id)")

            // --- Budgets: rebuild with new schema ---
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `budgets_new` (
                    `id` TEXT NOT NULL,
                    `category_id` TEXT,
                    `account_id` TEXT,
                    `limit_minor` INTEGER NOT NULL,
                    `period` TEXT NOT NULL DEFAULT 'MONTHLY',
                    `start_epoch_day` INTEGER NOT NULL,
                    `end_epoch_day` INTEGER NOT NULL,
                    `alert_threshold` REAL NOT NULL DEFAULT 0.8,
                    `created_at_epoch_millis` INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(`id`)
                )
                """,
            )
            db.execSQL(
                """
                INSERT INTO budgets_new (id, category_id, limit_minor, start_epoch_day, end_epoch_day)
                SELECT id, category_id, limit_minor, start_epoch_day, end_epoch_day
                FROM budgets
                """,
            )
            db.execSQL("DROP TABLE budgets")
            db.execSQL("ALTER TABLE budgets_new RENAME TO budgets")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_budgets_category_id ON budgets (category_id)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_budgets_account_id ON budgets (account_id)")

            // --- Goals: rebuild with new schema ---
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `goals_new` (
                    `id` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `target_minor` INTEGER NOT NULL,
                    `saved_minor` INTEGER NOT NULL,
                    `deadline_epoch_day` INTEGER,
                    `icon` TEXT,
                    `created_at_epoch_millis` INTEGER NOT NULL DEFAULT 0,
                    `completed` INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(`id`)
                )
                """,
            )
            db.execSQL(
                """
                INSERT INTO goals_new (id, name, target_minor, saved_minor, deadline_epoch_day)
                SELECT id, name, target_minor, saved_minor, deadline_epoch_day
                FROM goals
                """,
            )
            db.execSQL("DROP TABLE goals")
            db.execSQL("ALTER TABLE goals_new RENAME TO goals")

            // --- Recurring Transactions: new table ---
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `recurring_transactions` (
                    `id` TEXT NOT NULL,
                    `type` TEXT NOT NULL,
                    `amount_minor` INTEGER NOT NULL,
                    `account_id` TEXT NOT NULL,
                    `category_id` TEXT,
                    `destination_account_id` TEXT,
                    `description` TEXT,
                    `frequency` TEXT NOT NULL,
                    `next_occurrence_epoch_day` INTEGER NOT NULL,
                    `end_date_epoch_day` INTEGER,
                    `active` INTEGER NOT NULL DEFAULT 1,
                    `created_at_epoch_millis` INTEGER NOT NULL DEFAULT 0,
                    `updated_at_epoch_millis` INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(`id`)
                )
                """,
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_recurring_transactions_account_id ON recurring_transactions (account_id)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_recurring_transactions_category_id ON recurring_transactions (category_id)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_recurring_transactions_next_occurrence_epoch_day ON recurring_transactions (next_occurrence_epoch_day)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_recurring_transactions_active ON recurring_transactions (active)")

            db.execSQL("PRAGMA foreign_keys = ON")
        }
    }
}

/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.bitlove.fetlife.model.db;

import com.bitlove.fetlife.model.pojos.User;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

@Database(name = FetLifeDatabase.NAME, version = FetLifeDatabase.VERSION)
public class FetLifeDatabase {

    public static final String NAME = "fetlife";

    //Simple increase the version number in case of new tables
    public static final int VERSION = 25;

    //Add new Migration classes in case of table structure change
    @Migration(version = 24, database = FetLifeDatabase.class)
    public static class Migration24 extends AlterTableMigration<User> {

        public Migration24(Class<User> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "accessToken");
        }
    }

}

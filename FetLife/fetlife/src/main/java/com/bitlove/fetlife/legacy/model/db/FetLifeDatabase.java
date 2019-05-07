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

package com.bitlove.fetlife.legacy.model.db;

import com.bitlove.fetlife.legacy.model.pojos.fetlife.db.EventReference;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Conversation;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.FriendRequest;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.GroupComment;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.GroupPost;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Message;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Picture;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

@Database(name = FetLifeDatabase.NAME, version = FetLifeDatabase.VERSION)
public class FetLifeDatabase {

    public static final String NAME = "fetlife";

    //Simple increase the version number in case of new tables
    public static final int VERSION = 52;

    //Add new Migration classes in case of table structure change
    @Migration(version = 26, database = FetLifeDatabase.class)
    public static class Migration26 extends AlterTableMigration<Conversation> {

        public Migration26(Class<Conversation> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "draftMessage");
        }
    }

    //Add new Migration classes in case of table structure change
    @Migration(version = 27, database = FetLifeDatabase.class)
    public static class Migration27 extends BaseMigration {
        @Override
        public void migrate(DatabaseWrapper database) {
        }
    }

    //Add new Migration classes in case of table structure change
    @Migration(version = 28, database = FetLifeDatabase.class)
    public static class Migration28 extends AlterTableMigration<FriendRequest> {

        public Migration28(Class<FriendRequest> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "targetMemberId");
        }
    }

    //Add new Migration classes in case of table structure change
    @Migration(version = 33, database = FetLifeDatabase.class)
    public static class Migration33 extends AlterTableMigration<Event> {

        public Migration33(Class<Event> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "date");
        }
    }

    //Add new Migration classes in case of table structure change
    @Migration(version = 34, database = FetLifeDatabase.class)
    public static class Migration34 extends AlterTableMigration<EventReference> {

        public Migration34(Class<EventReference> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "rsvpStatus");
        }
    }

    //Add new Migration classes in case of table structure change
    @Migration(version = 36, database = FetLifeDatabase.class)
    public static class Migration36 extends AlterTableMigration<Event> {

        public Migration36(Class<Event> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "rsvpStatus");
        }
    }

    //Add new Migration classes in case of table structure change
    @Migration(version = 37, database = FetLifeDatabase.class)
    public static class Migration37 extends AlterTableMigration<Event> {

        public Migration37(Class<Event> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "roughtStartDate");
        }
    }

    //Add new Migration classes in case of table structure change
    @Migration(version = 42, database = FetLifeDatabase.class)
    public static class Migration42 extends AlterTableMigration<Message> {

        public Migration42(Class<Message> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "entitiesJson");
        }
    }

    @Migration(version = 43, database = FetLifeDatabase.class)
    public static class Migration43 extends AlterTableMigration<Picture> {

        public Migration43(Class<Picture> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "lastViewedAt");
        }
    }

    @Migration(version = 44, database = FetLifeDatabase.class)
    public static class Migration44 extends AlterTableMigration<Member> {

        public Migration44(Class<Member> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "lastViewedAt");
        }
    }

    @Migration(version = 45, database = FetLifeDatabase.class)
    public static class Migration45 extends AlterTableMigration<Picture> {

        public Migration45(Class<Picture> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER,"onShareList");
        }
    }

    @Migration(version = 46, database = FetLifeDatabase.class)
    public static class Migration46 extends AlterTableMigration<GroupPost> {

        public Migration46(Class<GroupPost> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "entitiesJson");
        }
    }

    @Migration(version = 47, database = FetLifeDatabase.class)
    public static class Migration47 extends AlterTableMigration<GroupComment> {

        public Migration47(Class<GroupComment> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "entitiesJson");
        }
    }

    @Migration(version = 48, database = FetLifeDatabase.class)
    public static class Migration48 extends AlterTableMigration<Group> {

        public Migration48(Class<Group> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "memberOfGroup");
            addColumn(SQLiteType.INTEGER, "detailLoaded");
        }
    }

    @Migration(version = 49, database = FetLifeDatabase.class)
    public static class Migration49 extends AlterTableMigration<GroupPost> {

        public Migration49(Class<GroupPost> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "followed");
        }
    }

    @Migration(version = 52, database = FetLifeDatabase.class)
    public static class Migration52 extends AlterTableMigration<Member> {

        public Migration52(Class<Member> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "serverId");
        }
    }

}

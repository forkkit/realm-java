/*
 * Copyright 2018 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.realm;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.realm.entities.realmname.ClassNameOverrideModulePolicy;
import io.realm.entities.realmname.ClassWithPolicy;
import io.realm.entities.realmname.CustomRealmNamesModule;
import io.realm.entities.realmname.FieldNameOverrideClassPolicy;
import io.realm.rule.TestRealmConfigurationFactory;

import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for checking that changing the internal Realm name
 * works correctly.
 */
@RunWith(AndroidJUnit4.class)
public class CustomRealmNameTests {

    @Rule
    public final TestRealmConfigurationFactory configFactory = new TestRealmConfigurationFactory();
    private Realm realm;

    @After
    public void tearDown() {
        if (realm != null) {
            realm.close();
        }
    }

    //
    // Build checks
    //

    // Check that the module policy is used as the default for class and field names
    @Test
    public void modulePolicy_defaultPolicy() {
        RealmConfiguration config = configFactory.createConfigurationBuilder()
                .modules(new CustomRealmNamesModule())
                .build();
        realm = Realm.getInstance(config);

        assertTrue(realm.getSchema().contains("default-policy-from-module"));
        RealmObjectSchema classSchema = realm.getSchema().get("default-policy-from-module");
        assertTrue(classSchema.hasField("camel-case"));
    }

    // Check that field name policies on classes override those from modules
    @Test
    public void classFieldPolicy_overrideModuleFieldPolicy() {
        RealmConfiguration config = configFactory.createConfigurationBuilder()
                .schema(ClassWithPolicy.class)
                .build();
        realm = Realm.getInstance(config);

        assertTrue(realm.getSchema().contains(ClassWithPolicy.CLASS_NAME));
        RealmObjectSchema classSchema = realm.getSchema().get(ClassWithPolicy.CLASS_NAME);
        for (String field : ClassWithPolicy.ALL_FIELDS) {
            assertTrue(field + " was not found.", classSchema.hasField(field));
        }
    }

    // Check that explicit class name override both module and class policies
    @Test
    public void className_overrideModuleClassPolicy() {
        RealmConfiguration config = configFactory.createConfigurationBuilder()
                .modules(new CustomRealmNamesModule())
                .build();
        realm = Realm.getInstance(config);

        assertTrue(realm.getSchema().contains(ClassNameOverrideModulePolicy.CLASS_NAME));
    }

    // Check that a explicitly setting a field name overrides a class field name policy
    @Test
    public void fieldName_overrideClassPolicy() {
        RealmConfiguration config = configFactory.createConfigurationBuilder()
                .modules(new CustomRealmNamesModule())
                .build();
        realm = Realm.getInstance(config);

        RealmObjectSchema classSchema = realm.getSchema().get(FieldNameOverrideClassPolicy.CLASS_NAME);
        assertTrue(classSchema.hasField(FieldNameOverrideClassPolicy.FIELD_CAMEL_CASE));
    }

    // Check that a explicitly setting a field name overrides a module field name policy
    @Test
    public void fieldName_overrideModulePolicy() {
        RealmConfiguration config = configFactory.createConfigurationBuilder()
                .modules(new CustomRealmNamesModule())
                .build();
        realm = Realm.getInstance(config);

        RealmObjectSchema classSchema = realm.getSchema().get(FieldNameOverrideClassPolicy.CLASS_NAME);
        assertTrue(classSchema.hasField(FieldNameOverrideClassPolicy.FIELD_CAMEL_CASE));
    }

    //
    // FIXME: Query tests
    // Mostly smoke test, as we only want to test that the query system correctly maps between
    // Java field names and cores.
    //

    //
    // FIXME: Schema tests
    //

    //
    // FIXME: DynamicRealm tests
    //
}

/*
 * Copyright (c) 2017. Ruslan Primak
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
 *
 * Last modified 4/24/17 12:54 AM
 */

package com.example.realmadapterexpandablelist.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ProductGroup extends RealmObject {
    public long id;
    public String name;
    public ProductGroup parent;
    public RealmList<ProductGroup> subGroups;
}

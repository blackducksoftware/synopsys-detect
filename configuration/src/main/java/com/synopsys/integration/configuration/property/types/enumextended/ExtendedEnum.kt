/**
 * configuration
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.configuration.property.types.enumextended

// An enum that can be either the E or the B type.
// Useful for enums that extend a base type. For example we want an UNSPECIFIED value on an existing enum that does not have it and does not make sense as an enum value on the existing type.
sealed class ExtendedEnumValue<E, B>
class ExtendedValue<E, B>(val value: E) : ExtendedEnumValue<E, B>();
class BaseValue<E, B>(val value: B) : ExtendedEnumValue<E, B>();

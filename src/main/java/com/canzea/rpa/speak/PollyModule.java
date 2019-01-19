/*
 * Copyright 2017 Greg Whitaker
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
 */

package com.canzea.rpa.speak;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyAsync;
import com.amazonaws.services.polly.AmazonPollyAsyncClientBuilder;
import com.google.inject.AbstractModule;

public class PollyModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AmazonPollyAsync.class).toInstance(
                AmazonPollyAsyncClientBuilder
                        .standard()
                        .withRegion(Regions.US_WEST_2)
                        .withCredentials(new DefaultAWSCredentialsProviderChain())
                        .build());
        bind(PollyReadHandler.class);
        bind(PollyVoicesHandler.class);
    }
}

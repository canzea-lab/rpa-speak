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

import com.amazonaws.services.polly.AmazonPollyAsync;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.google.inject.Inject;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class PollyReadHandler implements Handler {

    @Inject
    private AmazonPollyAsync polly;

    @Override
    public void handle(Context ctx) throws Exception {
        String voiceId = ctx.getRequest().getQueryParams().get("voiceId");
        String text = ctx.getRequest().getQueryParams().get("text");
        String outputFormat = ctx.getRequest().getQueryParams().get("outputFormat");

        SynthesizeSpeechRequest ssRequest = new SynthesizeSpeechRequest();
        ssRequest.setVoiceId(voiceId);
        ssRequest.setOutputFormat(outputFormat);
        ssRequest.setText(text);

        SynthesizeSpeechResult result = polly.synthesizeSpeech(ssRequest);

        InputStream audioStream = result.getAudioStream();

        log.info("Handling request... {} - {}", text, result.getContentType());
        
        ctx.getResponse().contentType(result.getContentType());

        ctx.getResponse().sendStream(s -> s.onSubscribe(new Subscription() {
            int total = 0;
            @Override
            public void request(long n) {
                try {
                    byte[] data      = new byte[12048];
                    int bytesRead = audioStream.read(data);
                    total += bytesRead;
                    if(bytesRead != -1) {
                        s.onNext(Unpooled.wrappedBuffer(data));
                    } else {
                        s.onComplete();
                        log.info("Request for {} DONE - bytes {}", n, total);
                    }

                } catch (IOException e) {
                    log.error("Error from AWS", e);
                    ctx.getResponse().status(500);
                    ctx.getResponse().send();
                } catch (Throwable e) {
                    log.error("Error from AWS", e);
                    ctx.getResponse().status(500);
                    ctx.getResponse().send();
                } finally {
                }
            }

            @Override
            public void cancel() {
                log.warn("Polly CANCELLED");
            }
        }));
    }
}

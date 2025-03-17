package com.omerfbuber.config;

import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.Aggregation;
import io.opentelemetry.sdk.metrics.InstrumentSelector;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.View;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.ServiceAttributes;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenTelemetryConfig {

//    private SdkTracerProvider sdkTracerProvider;
//    private SdkMeterProvider sdkMeterProvider;
//
//    @Bean
//    public OpenTelemetrySdk openTelemetry() {
//        final String SERVICE_NAME = "blog-sample-service";
//        final String ENDPOINT = "http://localhost:4317";
//
//        OtlpGrpcSpanExporter traceExporter = OtlpGrpcSpanExporter.builder()
//                .setEndpoint(ENDPOINT)
//                .build();
//
//        OtlpGrpcMetricExporter metricExporter = OtlpGrpcMetricExporter.builder()
//                .setEndpoint(ENDPOINT)
//                .build();
//
//        Resource resource = Resource.getDefault()
//                .toBuilder().put(ServiceAttributes.SERVICE_NAME, SERVICE_NAME)
//                .build();
//
//        sdkTracerProvider = SdkTracerProvider.builder()
//                .setResource(resource)
//                .addSpanProcessor(SimpleSpanProcessor.create(traceExporter))
//                .build();
//
//        sdkMeterProvider = SdkMeterProvider.builder()
//                .setResource(resource)
//                .registerMetricReader(PeriodicMetricReader.builder(metricExporter)
//                        .setInterval(Duration.ofSeconds(10))
//                        .build())
//                .build();
//
//        return OpenTelemetrySdk.builder()
//                .setTracerProvider(sdkTracerProvider)
//                .setMeterProvider(sdkMeterProvider)
//                .build();
//
//    }
//
//    @Bean
//    public Tracer tracer(OpenTelemetrySdk openTelemetry) {
//        final String SCOPE_NAME = "blog-sample-scope";
//        return openTelemetry.getTracer(SCOPE_NAME);
//    }
//
//    @PreDestroy
//    public void shutdown() {
//        if (sdkTracerProvider != null) {
//            sdkTracerProvider.shutdown();
//        }
//
//        if (sdkMeterProvider != null) {
//            sdkMeterProvider.shutdown();
//        }
//    }
}

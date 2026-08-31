package com.project.staynest.auth.presentation.controller;
import com.project.staynest.auth.presentation.dto.request.SignupRequestDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/load-test")
public class LoadTestController {

    private final WebClient webClient;

    public LoadTestController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping
    public String loadTest() {

        long endTime = System.nanoTime() + 10_000_000_000L;
        final long maxRequests = 50_000;

        AtomicLong success = new AtomicLong();
        AtomicLong failed = new AtomicLong();
        AtomicLong generated = new AtomicLong();

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        while (System.nanoTime() < endTime &&
                generated.get() < maxRequests) {

            generated.incrementAndGet();

            CompletableFuture<Void> future =
                    webClient.post()
                            .uri("http://localhost:8080/v1/auth/sign-up")
                            .bodyValue(createRequest())
                            .retrieve()
                            .bodyToMono(Void.class)
                            .doOnSuccess(v -> success.incrementAndGet())
                            .doOnError(e -> failed.incrementAndGet())
                            .onErrorResume(e -> Mono.empty())
                            .then()
                            .toFuture();

            futures.add(future);
        }

        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).join();

        long total = success.get() + failed.get();

        String result = """
            Generated       : %d
            Success         : %d
            Failed          : %d
            Requests/sec    : %.2f
            """.formatted(
                generated.get(),
                success.get(),
                failed.get(),
                generated.get() / 10.0
        );

        System.out.println(result);

        return result;
    }

    private SignupRequestDTO createRequest() {

        SignupRequestDTO dto = new SignupRequestDTO();
        dto.setUsername("_kritin_1");
        dto.setEmail("kritinpuri@gmail.com");

        return dto;
    }
}
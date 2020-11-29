package com.example.springbootmultithreading.service;

import com.example.springbootmultithreading.entity.User;
import com.example.springbootmultithreading.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Async
    public CompletableFuture<List<User>> saveUsers(MultipartFile file) throws Exception {
        final long start = System.currentTimeMillis();

        List<User> users = parseCSVFile(file);

        LOGGER.info("Saving a list of users of size {} records by {}", users.size(), Thread.currentThread().getName());

        users = userRepository.saveAll(users);

        LOGGER.info("Elapsed time: {}", (System.currentTimeMillis() - start));
        return CompletableFuture.completedFuture(users);
    }

    @Async
    public CompletableFuture<List<User>> getAllUsers() {
        LOGGER.info("Get list of users by " + Thread.currentThread().getName());
        List<User> users = userRepository.findAll();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(users);
    }

    @Async
    public CompletableFuture<Optional<User>> findUserById(Long id) {
        LOGGER.info("Get user by userId by " + Thread.currentThread().getName());

        System.out.println(10/0);   // deliberately exception is generated for experimental purpose

        Optional<User> user = userRepository.findById(id);
        return CompletableFuture.completedFuture(user);
    }


    private List<User> parseCSVFile(final MultipartFile file) throws Exception {
        final List<User> users = new ArrayList<>();

        try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line;
            while ((line = br.readLine()) != null) {
                final String[] data = line.split(",");
                final User user = User.builder()
                        .name(data[0])
                        .email(data[1])
                        .gender(data[2])
                        .build();
                users.add(user);
            }
            return users;
        } catch (final IOException e) {
            LOGGER.error("Failed to parse CSV file {}", e);
            throw new Exception("Failed to parse CSV file {}", e);
        }
    }
}

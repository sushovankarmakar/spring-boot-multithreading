package com.example.springbootmultithreading.controller;

import com.example.springbootmultithreading.entity.User;
import com.example.springbootmultithreading.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/* https://www.youtube.com/watch?v=3rJBLFA95Io&ab_channel=JavaTechie */
/* https://github.com/swathisprasad/spring-boot-completable-future */
/* https://github.com/Java-Techie-jt/springboot-multithreading-example */
/* https://dzone.com/articles/multi-threading-in-spring-boot-using-completablefu */

@RestController
@RequestMapping(value = "/api")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping(value = "/users", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<String> uploadFile(@RequestParam(value = "files") MultipartFile[] files) {

        /* A GOOD OBSERVATION :
        * if number of files uploaded is greater than the number of threads created in AsyncConfig, then we will get errors */

        try {
            for (final MultipartFile file : files) {
                userService.saveUsers(file);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("File upload is successful");
        } catch (final Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/users", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public CompletableFuture<ResponseEntity<List<User>>> getAllUsers() {
        return userService.getAllUsers()
                .thenApply(ResponseEntity::ok)
                .exceptionally(handleAllGetUserFailure);
    }

    @GetMapping(value = "/getUsersByThread", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<Void> getUsers() {
        CompletableFuture<List<User>> users1 = userService.getAllUsers();
        CompletableFuture<List<User>> users2 = userService.getAllUsers();
        CompletableFuture<List<User>> users3 = userService.getAllUsers();
        CompletableFuture<List<User>> users4 = userService.getAllUsers();
        CompletableFuture<List<User>> users5 = userService.getAllUsers();

        CompletableFuture.allOf(users1, users2, users3, users4, users5).join();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = "/asyncException/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public CompletableFuture<ResponseEntity<Optional<User>>> findUserById (@PathVariable(value = "id") Long id) {

        return userService.findUserById(id)
                .thenApply(ResponseEntity :: ok)
                .exceptionally(handleGetUserByUserIdFailure);
    }


    private static Function<Throwable, ? extends ResponseEntity<List<User>>> handleAllGetUserFailure = throwable -> {
        LOGGER.error("Failed to read records: {}", throwable);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    };

    private static Function<Throwable, ? extends ResponseEntity<Optional<User>>> handleGetUserByUserIdFailure = throwable -> {
        LOGGER.error("Failed to read records: {}", throwable);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    };

}

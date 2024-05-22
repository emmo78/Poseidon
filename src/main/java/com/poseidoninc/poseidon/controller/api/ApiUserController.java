package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.exception.BadRequestException;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.UserService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Optional;

/**
 * ApiUserController class represents the API endpoints (CRUD) for managing User.
 *
 * @author olivier morel
 */
@RestController
@AllArgsConstructor
@Slf4j
@Validated //for constraints on PathVariable
public class ApiUserController {

    private final UserService userService;
    private final RequestService requestService;

    @GetMapping("/api/user/list")
    public ResponseEntity<Iterable<User>> getUsers(WebRequest request) throws UnexpectedRollbackException {
        Pageable pageRequest = Pageable.unpaged();
        Page<User> pageUser = userService.getUsers(pageRequest); //throws UnexpectedRollbackException
        log.info("{} : {} : users page number : {} of {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                pageUser.getNumber()+1,
                pageUser.getTotalPages());
        return new ResponseEntity<>(pageUser, HttpStatus.OK);
    }

    @PostMapping("/api/user/create")
    public ResponseEntity<User> createUser(@RequestBody Optional<@Valid User> optionalUser, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, DataIntegrityViolationException, UnexpectedRollbackException {
        if (optionalUser.isEmpty()) {
            throw new BadRequestException("Correct request should be a json User body");
        }
        User userSaved = userService.saveUser(optionalUser.get()); //Throws DataIntegrityViolationException, UnexpectedRollbackException
        log.info("{} : {} : user = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), userSaved.toString());
        return new ResponseEntity<>(userSaved, HttpStatus.OK);
    }

    @GetMapping("/api/user/update/{id}") //SQL tinyint(4) = -128 to 127 so 1 to 127 for id
    public ResponseEntity<User> getUserById(@PathVariable("id") @Min(1) @Max(127) Integer id, WebRequest request) throws ConstraintViolationException, UnexpectedRollbackException {
        User user = userService.getUserByIdWithBlankPasswd(id); //Throws UnexpectedRollbackException
        log.info("{} : {} : user = {} gotten",  requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), user.toString());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/api/user/update")
    public ResponseEntity<User> updateUser(@RequestBody Optional<@Valid User> optionalUser, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, DataIntegrityViolationException, UnexpectedRollbackException {
        if (optionalUser.isEmpty()) {
            throw new BadRequestException("Correct request should be a json User body");
        }
        User userUpdated = userService.saveUser(optionalUser.get()); //Throws DataIntegrityViolationException, UnexpectedRollbackException
        log.info("{} : {} : user = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), userUpdated.toString());
        return new ResponseEntity<>(userUpdated, HttpStatus.OK);
    }

    @DeleteMapping("/api/user/delete/{id}") //SQL tinyint(4) = -128 to 127 so 1 to 127 for id
    public HttpStatus deleteUserById(@PathVariable("id") @Min(1) @Max(127) Integer id, WebRequest request) throws ConstraintViolationException, UnexpectedRollbackException {
        userService.deleteUserById(id); //Throws UnexpectedRollbackException
        log.info("{} : {} : user = {} deleted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), id);
        return HttpStatus.OK;
    }
}
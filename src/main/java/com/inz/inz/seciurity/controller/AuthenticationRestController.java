package com.inz.inz.seciurity.controller;


import com.inz.inz.ExcpetionHandler.AuthenticationException;
import com.inz.inz.repository.UserRepository;
import com.inz.inz.seciurity.AdapterImpl.UserAdapterImpl;
import com.inz.inz.seciurity.Resource.UserAuthResoruce;
import com.inz.inz.seciurity.jwt.JwtAuthenticationRequest;
import com.inz.inz.seciurity.jwt.JwtTokenUtil;
import com.inz.inz.seciurity.jwt.JwtUser;
import com.inz.inz.seciurity.model.User;
import com.inz.inz.seciurity.service.JwtAuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
public class AuthenticationRestController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserAdapterImpl userAdapter;

    @RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        // Reload password post-security so we can generate the token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        final User user = userRepository.findByUsername(authenticationRequest.getUsername());


        UserAuthResoruce userAuthResoruce = userAdapter.mapUserAuthResource(user);
        // Return the token
        return ResponseEntity.ok(new JwtAuthenticationResponse(
                token, jwtTokenUtil.getExpirationDateFromToken(token), userAuthResoruce));
    }

    @RequestMapping(value = "${jwt.route.authentication.refresh}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader(tokenHeader);
        final String token = authToken.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        final User u = userRepository.findByUsername(username);

        UserAuthResoruce userAuthResoruce = userAdapter.mapUserAuthResource(u);
        if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            return ResponseEntity.ok(new JwtAuthenticationResponse(
                    refreshedToken, jwtTokenUtil.getExpirationDateFromToken(refreshedToken), userAuthResoruce
            ));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }


    /**
     * Authenticates the user. If something is wrong, an {@link AuthenticationException} will be thrown
     */
    private void authenticate(String username, String password) throws AuthenticationException {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {

            throw  new AuthenticationException("User disbaled","A1");
        } catch (BadCredentialsException e) {

            throw  new AuthenticationException("Bad credintals","A1");
        }
    }
}
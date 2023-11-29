package com.Foodease.FoodeaseApp.serviceImpl;

//import com.Foodease.FoodeaseApp.JWT.CustomerUserDetailsService;
import com.Foodease.FoodeaseApp.JWT.CustomerUserDetailsService;
import com.Foodease.FoodeaseApp.JWT.JwtFilter;
import com.Foodease.FoodeaseApp.JWT.JwtUtil;
import com.Foodease.FoodeaseApp.POJO.User;
import com.Foodease.FoodeaseApp.constant.RestaurantConstants;
import com.Foodease.FoodeaseApp.dao.UserDao;
import com.Foodease.FoodeaseApp.service.UserService;
import com.Foodease.FoodeaseApp.utils.RestaurantUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    CustomerUserDetailsService customerUserDetailsService;
    @Autowired
    JwtUtil jwtUtil;



    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup{}",requestMap);
        try {


            if (validateSignUpMap(requestMap)) {
                User user = userDao.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDao.save(getUserFromMap(requestMap));
                    return RestaurantUtils.getResponseEntity("Successfully Registered.", HttpStatus.OK);

                } else {
                    return RestaurantUtils.getResponseEntity("Email already exits. ", HttpStatus.BAD_REQUEST);
                }

            } else {
                return RestaurantUtils.getResponseEntity(RestaurantConstants.Invalid_Data, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.Something_Went_Wrong, HttpStatus.INTERNAL_SERVER_ERROR);
        //return null;

    }



    private boolean validateSignUpMap(Map<String, String> requestMap){
      if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
              && requestMap.containsKey("email")&& requestMap.containsKey("password")) {
          return true;
      }else {
          return false;
      }
    }

    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("true");
        user.setRole("User");
        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside Login");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
            );
            if (auth.isAuthenticated()){
                if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\""+
                jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                        customerUserDetailsService.getUserDetail().getRole()) + "\"}",HttpStatus.OK);
                }else {
                    return new ResponseEntity<String>("{\"message\":\""+"Wait for admin approval."+"\"}",HttpStatus.BAD_REQUEST);
                }
            }
//            else {
//                return new ResponseEntity<String>("{\"message\":\""+"Unexpected user format."+"\"}",HttpStatus.INTERNAL_SERVER_ERROR);
//            }

        }catch (Exception e){
            log.error("{}",e);
        }
        return new ResponseEntity<String>("{\"message\":\""+"Authentication failed! ."+"\"}",HttpStatus.BAD_REQUEST);

    }
}

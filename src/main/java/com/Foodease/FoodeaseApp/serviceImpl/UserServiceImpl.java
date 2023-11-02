package com.Foodease.FoodeaseApp.serviceImpl;

import com.Foodease.FoodeaseApp.POJO.User;
import com.Foodease.FoodeaseApp.constant.RestaurantConstants;
import com.Foodease.FoodeaseApp.dao.UserDao;
import com.Foodease.FoodeaseApp.service.UserService;
import com.Foodease.FoodeaseApp.utils.RestaurantUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;
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
        user.setStatus("false");
        user.setStatus("User");
        return user;
    }
}

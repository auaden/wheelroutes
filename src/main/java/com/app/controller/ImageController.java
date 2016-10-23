package com.app.controller;

import com.app.dao.ObstacleDao;
import com.app.service.ObstacleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by adenau on 20/9/16.
 */
@RestController
@RequestMapping("/obstacle")
public class ImageController {

    @Autowired
    private ObstacleService obstacleService;


    @GetMapping(value = "/{latitude}/{longitude}/")
    @ResponseBody
    public ResponseEntity<byte[]> findImage(@PathVariable String latitude,
                                            @PathVariable String longitude) {
        System.out.println("IMAGE CONTROLLER CALLED");
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        byte[] image = obstacleService.getImage(Double.valueOf(latitude), Double.valueOf(longitude));
        if (image == null) {
            return ResponseEntity.badRequest().body(image);
        } else {
            return ResponseEntity.ok()
                    .contentLength(image.length)
                    .contentType(MediaType.IMAGE_PNG)
                    .body(image);
        }
    }

}

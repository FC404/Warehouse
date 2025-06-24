package com.fxm.warehouse.controller;



import com.fxm.warehouse.anno.Log;
import com.fxm.warehouse.service.RegisterService;
import com.fxm.warehouse.pojo.dto.UserAddDTO;
import com.fxm.warehouse.pojo.entity.Result;
import com.fxm.warehouse.pojo.vo.UserAddVO;
import com.fxm.warehouse.service.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registe")
@Slf4j
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping("/add")
    @Log
    public Result<UserAddVO> add(@RequestBody UserAddDTO userAddDTO) {
        log.info("添加用户请求参数: {}", userAddDTO);
        try {
            UserAddVO vo = registerService.add(userAddDTO);
            log.info("添加用户请求参数: {}", userAddDTO);
            System.out.println("收到的参数: " + userAddDTO);
            return Result.success(vo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("添加用户失败", e);
            return Result.error("添加用户失败，请稍后重试");
        }
    }
}

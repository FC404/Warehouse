package com.fxm.warehouse.controller;

import com.fxm.warehouse.anno.Log;
import com.fxm.warehouse.pojo.entity.OutsourcingDelivery;
import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.pojo.entity.Result;
import com.fxm.warehouse.service.OutsourcingDeliveryService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/outsourcing-deliveries")
public class OutsourcingDeliveryController {

    @Autowired
    private OutsourcingDeliveryService outsourcingDeliveryService;

    /**
     * 分页查询委外发货登记列表
     */
    @GetMapping("/list")
    public Result<PageBean<OutsourcingDelivery>> getOutsourcingDeliveries(
            @RequestParam(defaultValue = "1") Integer pageNum,  // 当前页码
            @RequestParam(defaultValue = "10") Integer pageSize,  // 每页显示数量
            @RequestParam(required = false) String supplier,  // 供应商筛选
            @RequestParam(required = false) String productCode,  // 产品编码筛选
            @RequestParam(required = false) String productName,  // 是否上线筛选
            @RequestParam(required = false) String isOnline,  // 是否上线筛选
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date outsourceDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date plannedCompletionDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date returnDate
    ) {
        // 调用服务层分页查询
        PageBean<OutsourcingDelivery> pageBean = outsourcingDeliveryService.page(
                pageNum, pageSize, supplier, productCode, isOnline, productName, outsourceDate, plannedCompletionDate, returnDate);

        // 返回查询结果
        return Result.success(pageBean);
    }

    /**
     * 根据 ID 查询委外发货登记
     */
    @GetMapping("/{id}")
    public Result<OutsourcingDelivery> getOutsourcingDeliveryById(@PathVariable Long id) {
        OutsourcingDelivery outsourcingDelivery = outsourcingDeliveryService.getById(id);
        if (outsourcingDelivery == null) {
            return Result.error("未找到该委外发货登记");
        }
        return Result.success(outsourcingDelivery);
    }

    /**
     * 修改委外发货登记
     */
    @PutMapping("/{id}")
    @Log
    public Result<Void> updateOutsourcingDelivery(@PathVariable Long id, @RequestBody OutsourcingDelivery outsourcingDelivery) {
        outsourcingDelivery.setId(id);  // 保证 ID 不被修改
        boolean updated = outsourcingDeliveryService.updateById(outsourcingDelivery);
        if (!updated) {
            return Result.error("更新失败");
        }
        return Result.success();
    }

    /**
     * 添加新的委外发货登记
     */
    @PostMapping
    @Log
    public Result<Void> addOutsourcingDelivery(@RequestBody OutsourcingDelivery outsourcingDelivery) {
        outsourcingDeliveryService.save(outsourcingDelivery);
        return Result.success();
    }


    /**
     * 根据 ID 删除委外发货登记
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteOutsourcingDelivery(@PathVariable Long id) {
        boolean deleted = outsourcingDeliveryService.removeById(id);
        if (!deleted) {
            return Result.error("删除失败");
        }
        return Result.success();
    }

    /**
     * 批量删除委外发货登记
     */
    @DeleteMapping("/batch")
    @Log
    public Result<Void> batchDeleteOutsourcingDeliveries(@RequestBody Long[] ids) {
        boolean deleted = outsourcingDeliveryService.removeByIds(Arrays.asList(ids));
        if (!deleted) {
            return Result.error("批量删除失败");
        }
        return Result.success();
    }

    @PostMapping("/export")
    @Log
    public void exportOrders(@RequestBody List<Long> Ids, HttpServletResponse response) {
        outsourcingDeliveryService.exportOutsourcingDelivery(Ids, response);
    }

    // 统计订单总数
    @GetMapping("/count")
    public Result<Long> countOutsourcingDelivery() {
        long count = outsourcingDeliveryService.countOutsourcingDelivery();
        return Result.success(count);
    }

    // 获取所有供应商名称列表
    @GetMapping("/suppliers")
    public Result<List<String>> getSuppliers() {
        List<String> suppliers = outsourcingDeliveryService.getSupplierNames();
        return Result.success(suppliers);
    }

}

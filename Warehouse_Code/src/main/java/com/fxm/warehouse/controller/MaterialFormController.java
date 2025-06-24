package com.fxm.warehouse.controller;

import com.fxm.warehouse.anno.Log;
import com.fxm.warehouse.pojo.entity.MaterialForm;
import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.pojo.entity.Result;
import com.fxm.warehouse.service.MaterialFormService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/materialForm")
public class MaterialFormController {

    @Autowired
    private MaterialFormService materialFormService;

    /**
     * 分页查询订单，支持根据多个字段筛选
     */
    @GetMapping("/list")
    public Result<PageBean<MaterialForm>> getAllMaterialForm(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String customer, // 客户筛选
            @RequestParam(required = false) String parentPartNumber, // 母件料号筛选
            @RequestParam(required = false) String copperMaterialNumber, // 铜材料号筛选
            @RequestParam(required = false) String childPartNumber, // 子件料号筛选
            @RequestParam(required = false) String materialSpecification, // 材料规格筛选
            @RequestParam(required = false) String materialType, // 材质筛选
            @RequestParam(required = false) Integer quantity, // 用量筛选
            @RequestParam(required = false) Integer copperQuantity, // 铜材数量筛选
            @RequestParam(required = false) Integer orderQuantity, // 订单数量筛选
            @RequestParam(required = false) Integer plannedQuantity, // 计划数量筛选
            @RequestParam(required = false) String manufacturingType, // 自制/外发筛选
            @RequestParam(required = false) String orderStatus, // 订单状态筛选
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date feedingDate, // 投料日期筛选
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date machiningDeliveryDate, // 机加交期筛选
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date orderDate, // 下单日期筛选
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date onlineDate // 上线日期筛选
    ) {
        // 调用 service 进行查询，并传递查询条件
        PageBean<MaterialForm> pageBean = materialFormService.page(
                pageNum, pageSize, customer, parentPartNumber, copperMaterialNumber, childPartNumber,
                materialSpecification, materialType, quantity, copperQuantity, orderQuantity,
                plannedQuantity, manufacturingType, orderStatus, feedingDate, machiningDeliveryDate,
                orderDate, onlineDate
        );
        return Result.success(pageBean);
    }

    /**
     * 根据ID查询订单
     */
    @GetMapping("/{id}")
    public Result<MaterialForm> getMaterialFormById(@PathVariable Integer id) {
        MaterialForm materialForm = materialFormService.getById(id);
        return materialForm != null ? Result.success(materialForm) : Result.error("订单不存在");
    }

    /**
     * 新增订单
     */
    @PostMapping("/add")
    @Log
    public Result addMaterialFormById(@RequestBody MaterialForm materialForm) {
        return materialFormService.add(materialForm);
    }
    /**
     * 更新订单
     */
    @PutMapping("/{id}")
    @Log
    public Result<Void> updateMaterialFormById(@PathVariable Long id, @RequestBody MaterialForm materialForm) {
        materialForm.setId(id); // 确保 ID 一致
        boolean success = materialFormService.update(materialForm);
        return success ? Result.success() : Result.error("更新订单失败");
    }
    /**
     * 删除订单
     */
    @DeleteMapping("/{id}")
    @Log
    public Result<Void> deleteMaterialFormById(@PathVariable Integer id) {
        boolean success = materialFormService.delete(id);
        return success ? Result.success() : Result.error("删除订单失败");
    }

    /**
     * 批量删除订单
     */
    @DeleteMapping("/deleteBatch")
    @Log
    public Result<Void> deleteMaterialFormByIds(@RequestBody List<Integer> ids) {
        boolean success = materialFormService.deleteBatch(ids);
        return success ? Result.success() : Result.error("批量删除订单失败");
    }

    @PostMapping("/export")
    @Log
    public void exportOrders(@RequestBody List<Long> Ids, HttpServletResponse response) {
        materialFormService.exportmaterialForm(Ids, response);
    }

    // 统计订单总数
    @GetMapping("/count")
    public Result<Long> countmaterialForm() {
        long count = materialFormService.countMaterialForm();
        return Result.success(count);
    }

    //查找全部客户
    @GetMapping("/getCustomer")
    public Result<List<String>> getCustomer() {
        List<String> customer = materialFormService.getCustomer();
        return Result.success(customer);
    }
}

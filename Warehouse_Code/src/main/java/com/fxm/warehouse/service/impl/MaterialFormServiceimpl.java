package com.fxm.warehouse.service.impl;

import com.fxm.warehouse.mapper.MaterialFormMapper;
import com.fxm.warehouse.pojo.entity.MaterialForm;
import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.pojo.entity.Result;
import com.fxm.warehouse.service.MaterialFormService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class MaterialFormServiceimpl implements MaterialFormService {

    @Autowired
    private MaterialFormMapper materialFormMapper;

    @Override
    public PageBean<MaterialForm> page(Integer pageNum, Integer pageSize, String customer, String parentPartNumber, String copperMaterialNumber, String childPartNumber, String materialSpecification, String materialType, Integer quantity, Integer copperQuantity, Integer orderQuantity, Integer plannedQuantity, String manufacturingType, String orderStatus, Date feedingDate, Date machiningDeliveryDate, Date orderDate, Date onlineDate) {
        // 使用 PageHelper 进行分页查询
        PageHelper.startPage(pageNum, pageSize);

        // 调用 Mapper 层查询符合条件的数据
        List<MaterialForm> materialFormList = materialFormMapper.page(customer, parentPartNumber, copperMaterialNumber, childPartNumber, materialSpecification, materialType, quantity, copperQuantity, orderQuantity, plannedQuantity, manufacturingType, orderStatus, feedingDate, machiningDeliveryDate, orderDate, onlineDate);

        // 使用 PageInfo 包装查询结果，获取总记录数
        PageInfo<MaterialForm> pageInfo = new PageInfo<>(materialFormList);

        // 返回封装的分页结果
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public MaterialForm getById(Integer id) {
        return materialFormMapper.getById(id);
    }

    @Override
    public Result add(MaterialForm materialForm) {
        try {
            int rows = materialFormMapper.add(materialForm);
            if (rows > 0) {
                return Result.success("添加成功");
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            // 捕获异常并返回错误信息
            return Result.error("服务器内部错误: " + e.getMessage());
        }
    }

    @Override
    public boolean update(MaterialForm materialForm) {
        try {
            int rowsAffected = materialFormMapper.update(materialForm);
            // 如果更新影响了至少一行数据，则返回 true
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            // 如果发生异常，则返回 false
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {
        return materialFormMapper.delete(id) > 0;
    }

    @Override
    public boolean deleteBatch(List<Integer> ids) {
        return materialFormMapper.deleteBatch(ids) > 0;
    }

    @Override
    public void exportmaterialForm(List<Long> Ids, HttpServletResponse response) {
        List<MaterialForm> materialFormList = materialFormMapper.findMaterialFormByIds(Ids);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("订单列表");

        String[] headers = {"客户", "母件料号", "铜材料号", "子件料号", "材料规格", "材质", "用量", "铜材数量", "订单数量",
                "计划数量", "投料日期", "机加交期", "自制/外发", "下单日期", "上线日期", "备注", "状态"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        int rowIdx = 1;
        for (MaterialForm materialForm : materialFormList) {
            Row row = sheet.createRow(rowIdx++);
            int colIdx = 0;

            row.createCell(colIdx++).setCellValue(materialForm.getCustomer());
            row.createCell(colIdx++).setCellValue(materialForm.getParentPartNumber());
            row.createCell(colIdx++).setCellValue(materialForm.getCopperMaterialNumber());
            row.createCell(colIdx++).setCellValue(materialForm.getChildPartNumber());
            row.createCell(colIdx++).setCellValue(materialForm.getMaterialSpecification());
            row.createCell(colIdx++).setCellValue(materialForm.getMaterialType());
            row.createCell(colIdx++).setCellValue(materialForm.getQuantity());
            row.createCell(colIdx++).setCellValue(materialForm.getCopperQuantity());
            row.createCell(colIdx++).setCellValue(materialForm.getOrderQuantity());
            row.createCell(colIdx++).setCellValue(materialForm.getPlannedQuantity());

            row.createCell(colIdx++).setCellValue(materialForm.getFeedingDate() != null
                    ? materialForm.getFeedingDate().format(dateFormatter) : "");

            row.createCell(colIdx++).setCellValue(materialForm.getMachiningDeliveryDate() != null
                    ? materialForm.getMachiningDeliveryDate().format(dateFormatter) : "");

            row.createCell(colIdx++).setCellValue(materialForm.getManufacturingType());

            row.createCell(colIdx++).setCellValue(materialForm.getOrderDate() != null
                    ? materialForm.getOrderDate().format(dateFormatter) : "");

            row.createCell(colIdx++).setCellValue(materialForm.getOnlineDate() != null
                    ? materialForm.getOnlineDate().format(dateFormatter) : "");

            String remarks = materialForm.getRemarks();
            row.createCell(colIdx++).setCellValue(remarks == null || remarks.trim().isEmpty() ? "暂无备注" : remarks);

            row.createCell(colIdx++).setCellValue(materialForm.getOrderStatus());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=material_form.xlsx");

        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public long countMaterialForm() {
        return materialFormMapper.countMaterialForm();
    }

    @Override
    public List<String> getCustomer() {
        return materialFormMapper.getCustomer();
    }

}

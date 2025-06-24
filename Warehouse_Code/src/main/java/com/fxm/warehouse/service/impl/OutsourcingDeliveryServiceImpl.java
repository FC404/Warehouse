package com.fxm.warehouse.service.impl;

import com.fxm.warehouse.mapper.OutsourcingDeliveryMapper;
import com.fxm.warehouse.pojo.entity.OutsourcingDelivery;
import com.fxm.warehouse.pojo.entity.PageBean;
import com.fxm.warehouse.service.OutsourcingDeliveryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class OutsourcingDeliveryServiceImpl implements OutsourcingDeliveryService {

    @Autowired
    private OutsourcingDeliveryMapper outsourcingDeliveryMapper;

    @Override
    public PageBean<OutsourcingDelivery> page(Integer pageNum, Integer pageSize, String supplier, String productCode, String isOnline, String productName, Date outsourceDate, Date plannedCompletionDate, Date returnDate) {
        // 使用 PageHelper 进行分页查询
        PageHelper.startPage(pageNum, pageSize);

        // 调用 Mapper 层查询符合条件的数据
        List<OutsourcingDelivery> orderList = outsourcingDeliveryMapper.page(supplier, productCode, isOnline, productName, outsourceDate, plannedCompletionDate, returnDate);
        // 使用 PageInfo 包装查询结果，获取总记录数
        PageInfo<OutsourcingDelivery> pageInfo = new PageInfo<>(orderList);

        // 返回封装的分页结果
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public OutsourcingDelivery getById(Long id) {
        // 根据 ID 查询指定的委外发货登记
        return outsourcingDeliveryMapper.selectById(id);
    }

    @Override
    public boolean updateById(OutsourcingDelivery outsourcingDelivery) {
        // 根据 ID 更新记录
        int result = outsourcingDeliveryMapper.updateById(outsourcingDelivery);
        return result > 0;
    }

    @Override
    public void save(OutsourcingDelivery outsourcingDelivery) {
        // 1. 简单非空校验（可以用更复杂的校验框架如 javax.validation）
        if (outsourcingDelivery.getSupplier() == null || outsourcingDelivery.getSupplier().trim().isEmpty()) {
            throw new IllegalArgumentException("供应商不能为空");
        }
        if (outsourcingDelivery.getIsOnline() == null ||
                (!outsourcingDelivery.getIsOnline().equals("上线") && !outsourcingDelivery.getIsOnline().equals("未上线"))) {
            throw new IllegalArgumentException("是否上线必须是 '上线' 或 '未上线'");
        }
        // 其他字段可以根据业务需要校验

        // 2. 调用Mapper插入
        try {
            outsourcingDeliveryMapper.insert(outsourcingDelivery);
        } catch (DataAccessException dae) {
            // 这里捕获Spring框架的数据库访问异常，方便区分
            throw new RuntimeException("数据库操作异常", dae);
        } catch (Exception e) {
            throw new RuntimeException("系统异常", e);
        }
    }


    @Override
    public boolean removeById(Long id) {
        // 删除指定 ID 的记录
        int result = outsourcingDeliveryMapper.deleteById(id);
        return result > 0;
    }

    @Override
    public boolean removeByIds(List<Long> list) {
        // 批量删除记录
        int result = outsourcingDeliveryMapper.deleteBatchIds(list);
        return result > 0;
    }

    @Override
    public void exportOutsourcingDelivery(List<Long> ids, HttpServletResponse response) {
        List<OutsourcingDelivery> outsourcingDeliveries = outsourcingDeliveryMapper.findOutsourcingDeliveryByIds(ids);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("委外发货");

        // 表头
        String[] headers = {"供应商", "产品编码", "产品名称", "外发数量", "外发日期", "计划完工日期", "是否上线", "回厂日期", "良品数量", "备注"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // 日期格式化器（假设日期是 LocalDate，如果是 java.util.Date 请告知我换格式）
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        int rowIdx = 1;
        for (OutsourcingDelivery outsourcingDelivery : outsourcingDeliveries) {
            Row row = sheet.createRow(rowIdx++);
            int colIdx = 0;

            row.createCell(colIdx++).setCellValue(outsourcingDelivery.getSupplier()); // 供应商
            row.createCell(colIdx++).setCellValue(outsourcingDelivery.getProductCode()); // 产品编码
            row.createCell(colIdx++).setCellValue(outsourcingDelivery.getProductName()); // 产品名称
            row.createCell(colIdx++).setCellValue(outsourcingDelivery.getOutsourceQuantity()); // 外发数量

            // 外发日期
            if (outsourcingDelivery.getOutsourceDate() != null) {
                row.createCell(colIdx++).setCellValue(outsourcingDelivery.getOutsourceDate().format(dateFormatter));
            } else {
                row.createCell(colIdx++).setCellValue("");
            }

            // 计划完工日期
            if (outsourcingDelivery.getPlannedCompletionDate() != null) {
                row.createCell(colIdx++).setCellValue(outsourcingDelivery.getPlannedCompletionDate().format(dateFormatter));
            } else {
                row.createCell(colIdx++).setCellValue("");
            }

            // 是否上线
            String isOnlineStr = outsourcingDelivery.getIsOnline();
            if (isOnlineStr == null || isOnlineStr.trim().isEmpty()) {
                isOnlineStr = "未知";
            }
            row.createCell(colIdx++).setCellValue(isOnlineStr);

            // 回厂日期
            if (outsourcingDelivery.getReturnDate() != null) {
                row.createCell(colIdx++).setCellValue(outsourcingDelivery.getReturnDate().format(dateFormatter));
            } else {
                row.createCell(colIdx++).setCellValue("");
            }

            // 良品数量
            row.createCell(colIdx++).setCellValue(outsourcingDelivery.getGoodQuantity());

            // 备注
            row.createCell(colIdx++).setCellValue(
                    outsourcingDelivery.getRemarks() == null ? "" : outsourcingDelivery.getRemarks()
            );
        }

        // 设置响应头，下载 Excel 文件
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=outsourcing_deliveries.xlsx");

        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public long countOutsourcingDelivery() {
        return outsourcingDeliveryMapper.countOutsourcingDelivery();
    }

    @Override
    public List<String> getSupplierNames() {
        return outsourcingDeliveryMapper.getSupplierNames();
    }
}
